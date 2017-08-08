package eu.transkribus.core.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.dea.util.pdf.PageImageWriter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;

import eu.transkribus.core.io.formats.Page2010Converter;
import eu.transkribus.core.io.formats.XmlFormat;
import eu.transkribus.core.io.util.ImgFileFilter;
import eu.transkribus.core.io.util.ImgPriority;
import eu.transkribus.core.io.util.MdFileFilter;
import eu.transkribus.core.model.beans.EdFeature;
import eu.transkribus.core.model.beans.EdOption;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocDir;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.enums.EditStatus;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.builder.mets.TrpMetsBuilder;
import eu.transkribus.core.model.builder.mets.util.MetsUtil;
import eu.transkribus.core.util.JaxbList;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.NaturalOrderComparator;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.XmlUtils;

/**
 * Reader class for loading a TRP Document from the local filesystem.<br>
 * The given path should contain:<br>
 * <ul>
 * <li>Image files of type JPG or TIFF (allowed types and priorities defined in
 * {@link ImgPriority})</li>
 * <li>Optional: PAGE XML files (corresponding images and XMLs must have the
 * same name)</li>
 * <li>Optional: XML file containing metadata. Filename has to match
 * "metadata.xml".</li>
 * </ul>
 * Order of pages is implied by order of filenames. The metadata XML is
 * marshalled to a TrpDocMetadata Object and thus has to match the bean's
 * fieldnames.<br>
 * <br>
 * <pre>
 * &lt;trpDocMetadata&gt;
 * 	&lt;docId&gt;-1&lt;/docId&gt;
 * 	&lt;title&gt;Bentham Box 2&lt;/title&gt;
 *	&lt;author&gt;Jeremy Bentham&lt;/author&gt;
 * 	&lt;uploadTimestamp&gt;0&lt;/uploadTimestamp&gt;
 * 	&lt;genre&gt;Notes&lt;/genre&gt;
 * 	&lt;writer&gt;Secretary&lt;/writer&gt;
 * 	&lt;nrOfPages&gt;5&lt;/nrOfPages&gt;
 * &lt;/trpDocMetadata&gt;
 * </pre>
 * <br>
 * 
 * @author philip
 * 
 */
public class LocalDocReader {

	private final static Logger logger = LoggerFactory.getLogger(LocalDocReader.class);

	public static TrpDoc load(final String path) throws IOException {
		return load(path, true, true, false, true, false);
	}

	/**
	 * Extracts images from a pdf into the given directory. 
	 * Further loads the document from the specified image directory.
	 * @param file absolute path of the pdf document
	 * @param path absolute path of the directory to which the pdf images should be extracted to
	 * @return new TrpDoc
	 * @throws IOException
	 * @throws SecurityException
	 * @throws DocumentException
	 */
	public static TrpDoc loadPdf(final String file, final String path) 
			throws IOException, SecurityException, DocumentException {
		logger.info("Extracting pdf " + file + " to folder " + path);
		PageImageWriter imgWriter = new PageImageWriter();
		imgWriter.extractImages(file, path);
		
		return load(imgWriter.getExtractDirectory());
	}
	
	/**
	 * @param path the path where the document is stored
	 * @param forceCreatePageXml if true, then a Page XML skeleton is created for pages where none exists
	 * @return the constructed document
	 * @throws IOException if the path can't be read or is malformed or an invalid XML format is found
	 */
	public static TrpDoc load(final String path, boolean forceCreatePageXml) throws IOException {
		return load(path, true, true, false, forceCreatePageXml, false);
	}
	
	/**
	 * Load document given extra parameter for syncing
	 * @param path the path where the document is stored
	 * @param forceCreatePageXml if true, then a Page XML skeleton is created for pages where none exists
	 * @param enableSyncWithoutImages if true, a document is created even if there are no images (only relevant for syncing) 
	 * @return the constructed document
	 * @throws IOException if the path can't be read or is malformed or an invalid XML format is found
	 */
	public static TrpDoc load(final String path, boolean forceCreatePageXml, boolean enableSyncWithoutImages) throws IOException {
		return load(path, true, true, false, forceCreatePageXml, enableSyncWithoutImages);
	}
	
	/**
	 * Loads a document from path.<br>
	 * 
	 * Document metadata has to be in an XML called "metadata.xml".<br>
	 * 
	 * Image files and corresponding XML files have to have the same name. <br>
	 * Lexicographic order of image names will imply order of pages.<br>
	 * Types of XML files are searched in this order:
	 * <ol>
	 * <li>./page: PAGE XMLs according to schema 2010/2013</li>
	 * <li>./ocr: Abbyy Finereader XMLs schema version 10</li>
	 * <li>./alto: ALTO v2 XMls
	 * </ol>
	 * Testdoc is in $dea_scratch/TRP/TrpTestDoc <br>
	 * No versioning of files for local use!<br>
	 * 
	 * @param path the path where the document is stored
	 * @param preserveOcrTxtStyles when creating the pageXML from alto/finereader XMLs, preserve the text style information
	 * @param preserveOcrFontFamily when creating the pageXML from alto/finereader XMLs, preserve the font information
	 * @param replaceBadChars TODO when creating the pageXML from alto/finereader XMLs, specific characters are replaced. see FinereaderUtils
	 * @param forceCreatePageXml if true, then a Page XML skeleton is created for pages where none exists
	 * @param enableSyncWithoutImages if true, document will be created from XMLs only even if no images exist
	 * @return the constructed document
	 * @throws IOException if the path can't be read or is malformed
	 */
	public static TrpDoc load(final String path, boolean preserveOcrTxtStyles, 
			boolean preserveOcrFontFamily, boolean replaceBadChars, boolean forceCreatePageXml,
			boolean enableSyncWithoutImages) throws IOException {
		//check OS and adjust URL protocol
		final String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("win")) {
			LocalDocConst.URL_PROT_CONST = "file:///";
		} //else: keep default

		final File inputDir = new File(path);

		logger.info("Reading document at " + inputDir.getAbsolutePath());

		//validate input path ======================================================

		if (!inputDir.canRead()) {
			logger.info("IS NOT READABLE");
			throw new IOException(inputDir.getAbsolutePath() + " is not readable.");
		}

		if (!inputDir.isDirectory()) {
			throw new IOException(inputDir.getAbsolutePath() + " is not a directory.");
		}

		//find metadata file ========================================================
		TrpDocMetadata docMd = findOrCreateDocMd(inputDir);

		//create the document
		TrpDoc doc = new TrpDoc();
		//and set the docMd
		doc.setMd(docMd);

		// search for IMG files
		TreeMap<String, File> pageMap = findImgFiles(inputDir);
		logger.info("Found " + pageMap.entrySet().size() + " page images.");

		//Construct the input dir with pageXml Files. 
		File pageInputDir = new File(inputDir.getAbsolutePath() + File.separatorChar
				+ LocalDocConst.PAGE_FILE_SUB_FOLDER);
		if (!pageInputDir.isDirectory()) {
			pageInputDir.mkdir();
		}
		
		//Construct thumb dir name. Its creation is handled by the GUI App
		File thumbDir = new File(inputDir.getAbsolutePath() + File.separatorChar + LocalDocConst.THUMBS_FILE_SUB_FOLDER);
		
		//abbyy XML search path
		File ocrInputDir = new File(inputDir.getAbsolutePath() + File.separatorChar
				+ LocalDocConst.OCR_FILE_SUB_FOLDER);
		
		//alto XML search path
		File altoInputDir = new File(inputDir.getAbsolutePath() + File.separatorChar
				+ LocalDocConst.ALTO_FILE_SUB_FOLDER);
		
		//backupfolder for outdated page format files, if any
		final String backupFolderName = XmlFormat.PAGE_2010.toString().toLowerCase()
				+ "_backup";
		final String backupPath = pageInputDir.getAbsolutePath() + File.separator
				+ backupFolderName;
		
		// iterate imgList, search for corresponding XML files and build TrpPages
		int pageNr = 1;
		List<TrpPage> pages = new ArrayList<TrpPage>(pageMap.entrySet().size());
		
		// TODO:FIXME Test, test, test!!!
		// need a special variable to test whether we are in sync mode (only then do the following!!!!)
		if (pages.size() == 0 && enableSyncWithoutImages ) {
			pageMap = createDummyImgFilesForXmls(inputDir, pageInputDir);
		}
		
		for (Entry<String, File> e : pageMap.entrySet()) {
			
			File imgFile = e.getValue();
			//the img file name without extension
			final String imgFileName = e.getKey();

			//check for a page XML of this name
			File pageXml = findXml(imgFileName, pageInputDir);
			
			//TODO thumbURL dir + imgFile.getName())+".jpg"
			File thumbFile = getThumbFile(thumbDir, imgFileName);
					
			if(pageXml != null) {
				XmlFormat xmlFormat = XmlUtils.getXmlFormat(pageXml);
				switch(xmlFormat){
				case PAGE_2010:
					Page2010Converter.updatePageFormatSingleFile(pageXml, backupPath);
					break;
				case PAGE_2013:
					break;
				default:
					throw new IOException("Incompatible XML file in PAGE XML path! " + pageXml.getAbsolutePath());
				}
			} 
			
			//if no page XML, then create one at this path
			
			
			File pageOutFile = new File(pageInputDir.getAbsolutePath() + File.separatorChar + imgFileName
					+ ".xml");
			
			File abbyyXml = findXml(imgFileName, ocrInputDir);
			File altoXml = findXml(imgFileName, altoInputDir);
			
			pageXml = createPageXmlIfNull(pageXml, forceCreatePageXml, pageOutFile, abbyyXml, altoXml, preserveOcrFontFamily, preserveOcrTxtStyles, replaceBadChars, imgFile);
			

			
			TrpPage page = buildPage(inputDir, pageNr++, imgFile, pageXml, thumbFile);
			pages.add(page);
		}

		doc.setPages(pages);
		doc.getMd().setNrOfPages(doc.getPages().size());
		
		// set editorial declaration:
		List<EdFeature> features = readEditDeclFeatures(doc.getMd().getLocalFolder());
		doc.setEdDeclList(features);

		logger.debug(doc.toString());
		return doc;
	}

	public static File createPageXmlIfNull(File pageXml, boolean forceCreatePageXml, File pageOutFile, File abbyyXml, File altoXml, boolean preserveOcrFontFamily, boolean preserveOcrTxtStyles, boolean replaceBadChars, File imgFile) throws IOException {
		if(pageXml == null && forceCreatePageXml){
			//try find Abbyy XML
			
			if(abbyyXml != null){
				try{
					XmlFormat xmlFormat = XmlUtils.getXmlFormat(abbyyXml);
					if(xmlFormat.equals(XmlFormat.ABBYY_10)){
						logger.info(abbyyXml.getAbsolutePath() + ": Transforming Finereader10/11 XML to PAGE XML.");
						PcGtsType pc = PageXmlUtils.createPcGtsTypeFromAbbyy(
								abbyyXml, imgFile.getName(), 
								preserveOcrTxtStyles, preserveOcrFontFamily, 
								replaceBadChars
								);
						pageXml = JaxbUtils.marshalToFile(pc, pageOutFile);
					}
				} catch(IOException | TransformerException ioe){
					logger.error(ioe.getMessage(), ioe);
					throw new IOException("Could not migrate file: " + abbyyXml.getAbsolutePath(), ioe);
				} catch (ParserConfigurationException | SAXException xmle) {
					logger.error(xmle.getMessage(), xmle);
					throw new IOException("Could not transform XML file!", xmle);
				} catch (JAXBException je) {
					/* TODO This exception is only thrown when the pageXML is unmarshalled 
					 * for inserting the image filename which is not included in the abbyy xml! */
					logger.error(je.getMessage(), je);
					throw new IOException("Transformation output is not a valid page XML!", je);
				}
			}
		}
		
		if(pageXml == null && forceCreatePageXml){
			//try find ALTO XML
			
			if(altoXml != null){
				try {
					pageXml = createPageFromAlto2(imgFile, altoXml, pageOutFile, preserveOcrTxtStyles, preserveOcrFontFamily, replaceBadChars);
					
//					XmlFormat xmlFormat = XmlUtils.getXmlFormat(altoXml);
//					if(xmlFormat.equals(XmlFormat.ALTO_2)) {
//						logger.info(altoXml.getAbsolutePath() + ": Transforming ALTO v2 XMLs to PAGE XML.");
//						PcGtsType pc = PageXmlUtils.createPcGtsTypeFromAlto(
//								altoXml, e.getValue().getName(), 
//								preserveOcrTxtStyles, preserveOcrFontFamily, 
//								replaceBadChars
//								);
//						pageXml = JaxbUtils.marshalToFile(pc, pageOutFile);
//					}
				} catch(IOException | TransformerException ioe){
					logger.error(ioe.getMessage(), ioe);
					throw new IOException("Could not migrate file: " + altoXml.getAbsolutePath(), ioe);
				} catch (ParserConfigurationException | SAXException xmle) {
					logger.error(xmle.getMessage(), xmle);
					throw new IOException("Could not transform XML file!", xmle);
				} catch (JAXBException je) {
					/* TODO This exception is only thrown when the pageXML is unmarshalled 
					 * for inserting the image filename which is not included in the abbyy xml! */
					logger.error(je.getMessage(), je);
					throw new IOException("Transformation output is not a valid page XML!", je);
				}
			}
		}
		
		//if still null, there is no suitable file for this page
		if (pageXml == null && forceCreatePageXml) {
			logger.warn("No Transcript XML found for img: " + FilenameUtils.getBaseName(imgFile.getName()));
			try{
				logger.info("Creating empty PageXml.");
				PcGtsType pc = PageXmlUtils.createEmptyPcGtsType(imgFile);
				pageXml = JaxbUtils.marshalToFile(pc, pageOutFile);
			} catch (JAXBException je) {
				logger.error(je.getMessage(), je);
				throw new IOException("Could not create empty PageXml!", je);
			}
		}
		return pageXml;
	}

	public static List<EdFeature> readEditDeclFeatures(File folder) {
		List<EdFeature> features = new ArrayList<>();

		File editDecl = new File(folder + "/" + LocalDocConst.EDITORIAL_DECLARATION_FN);
		if (editDecl.isFile()) {
			try {
				JaxbList<EdFeature> list = JaxbUtils.unmarshal(editDecl, JaxbList.class, EdFeature.class, EdOption.class);
				features = list.getList();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		return features;
	}
	
	/**
	 * Read a local doc based on its mets.xml file that has to contain file
	 * paths relative to parentDir
	 * 
	 * @param mets the mets object
	 * @param parentDir the directory of the local document
	 * @return the constructed Document
	 * @throws IOException if the path can't be read
	 */
	public static TrpDoc load(Mets mets, File parentDir) throws IOException {
		final TrpDoc doc = new TrpDoc();
		TrpDocMetadata md;
		List<TrpPage> pages;
//		FIXME set TRP_METS_VERSION to PROFILE, not TYPE
		if (mets.getPROFILE().equals(TrpMetsBuilder.TRP_METS_PROFILE)) {
			//unmarshal TrpDocMetadata
			md = MetsUtil.getTrpDocMd(mets);
			//collect files
			pages = MetsUtil.getTrpPages(mets, parentDir);
//		} else if (mets.getPROFILE().equals(EnmapMetsBuilder.ENMAP_METS_PROFILE)){
//			md = EnmapMetsBuilder.getTrpDocMd(mets);			
//			pages = EnmapMetsBuilder.getTrpPages(mets, parentDir);
		} else {
			throw new IOException("Unsupported METS PROFILE: " + mets.getPROFILE());			
		}
		md.setLocalFolder(parentDir);
		doc.setMd(md);
		doc.setPages(pages);
		return doc;
	}
	
	public static List<TrpDocDir> listDocDirs(final String path) throws IOException{
		return listDocDirs(path, null);
	}
	
	public static List<TrpDocDir> listDocDirs(final String path, IProgressMonitor monitor) throws IOException{
		if(path == null || path.isEmpty()){
			throw new IllegalArgumentException("Path is null or empty!");
		}
		File dir = new File(path);
		if(!dir.isDirectory()){
			throw new FileNotFoundException("Path is not a directory: " + path);
		}
		File[] docDirs = dir.listFiles(new FileFilter() {	
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
//		File pageInputDir = new File(dir.getAbsolutePath() + File.separatorChar
//				+ LocalDocConst.PAGE_FILE_SUB_FOLDER);
		if(monitor != null){
			monitor.beginTask("Scanning...", docDirs.length);
		}
		int i = 0;
		List<TrpDocDir> result = new LinkedList<>();
		
		for(File d : docDirs){
//			Map<String, File> imgs;
//			try {
//				imgs = findImgFiles(d);
//			} catch(IOException e){
//				logger.debug("Dir does not contain image files: " + d.getAbsolutePath());
//				continue;
//			}
			final String name = d.getName();
//			final long size = FileUtils.sizeOf(d);
//			final long size = -1; // too slow...
			final Date date = new Date(d.lastModified());
			TrpDocDir docDir = new TrpDocDir();
			docDir.setName(name);
			docDir.setNrOfFiles(d.list().length);
			docDir.setCreateDate(date);
//			TrpDocMetadata md = findOrCreateDocMd(d);
//			md.setLocalFolder(null); // delete local folder s.t. server dir is not visible for clients!
//			docDir.setMetadata(md);
			
			result.add(docDir);
			if(monitor != null){
				monitor.worked(++i);
			}
		}
		if(monitor != null){
			monitor.done();
		}
		return result;
	}
	
	public static File createPageFromAlto2(File imgFile, File altoXml, File pageOutFile, boolean preserveOcrTxtStyles, boolean preserveOcrFontFamily,
			boolean replaceBadChars) throws IOException, TransformerException, SAXException, ParserConfigurationException, JAXBException {
		XmlFormat xmlFormat = XmlUtils.getXmlFormat(altoXml);
		if (xmlFormat.equals(XmlFormat.ALTO_2)) {
			logger.info(altoXml.getAbsolutePath() + ": Transforming ALTO v2 XMLs to PAGE XML.");
			PcGtsType pc = PageXmlUtils.createPcGtsTypeFromAlto(altoXml, imgFile.getName(), preserveOcrTxtStyles, preserveOcrFontFamily, replaceBadChars);
			return JaxbUtils.marshalToFile(pc, pageOutFile);
		}
		throw new IOException("Could not determine xml file as valid alto2: " + altoXml.getAbsolutePath());
	}

	public static File getThumbFile(File thumbDir, String imgFileName) {
		final String path = thumbDir.getAbsolutePath() + File.separatorChar 
				+ imgFileName + LocalDocConst.THUMB_FILE_EXT;
		return new File(path);
	}


//	private static void startThumbCreationThread(final TrpDoc doc) {
//		Runnable thumbCreator = new Runnable(){
//			@Override
//			public void run() {
//				try{
//					LocalDocWriter.createThumbsForDoc(doc, false);
//				}catch (Exception e){
//					logger.error(e);
//					return;
//				}
//			}
//		};
//		new Thread(thumbCreator).start();
//	}

	/**
	 * Builds a TrpPage object with file URLs set
	 * @param inputDir the path where the local document is stored
	 * 
	 * @param pageNr
	 *            of the page to be built
	 * @param img
	 *            the img file to include
	 * @param pageXml
	 *            the corresponding PAGE XML
	 * @param thumb
	 * 			  the thumbnail file for this image
	 * @return a TrpPage object with Transcript. The Transcript is null, if
	 *         pageXml is null.
	 * @throws MalformedURLException if an URL can't be constructed from parentDir
	 */
	public static TrpPage buildPage(File inputDir, int pageNr, File img, File pageXml, File thumb) throws IOException {
		logger.debug(pageNr + ": XML = " + (pageXml == null ? "null" : pageXml.getName())
				+ " - IMG = " + img.getName());

		TrpPage page = new TrpPage();
		page.setPageNr(pageNr);
		page.setKey(null);
		page.setImgFileName(img.getName());
		page.setDocId(-1);
		final URL imgUrl = img.toURI().toURL();
		page.setUrl(imgUrl);
//		page.setThumbUrl(new File(LocalDocWriter.getThumbFileName(page)).toURI().toURL());
		final URL thumbUrl = thumb.toURI().toURL();
		page.setThumbUrl(thumbUrl);
		
		if(pageXml != null){
			final URL xmlUrl = pageXml.toURI().toURL();	
			TrpTranscriptMetadata tmd = new TrpTranscriptMetadata();
			tmd.setPageReferenceForLocalDocs(page);
			tmd.setPageNr(pageNr);
			tmd.setKey(null);
			tmd.setUrl(xmlUrl);
			tmd.setStatus(EditStatus.NEW);
			tmd.setLocalFolder(inputDir);
			tmd.setTimestamp(new Date().getTime());
			tmd.setUserName("LocalDocReader");
			//TODO real status, time and user parsed from PageXML?
			page.getTranscripts().add(tmd);
		}
		return page;
	}
	
	public static String getThumbFileName(TrpPage page) throws IOException {
		File imgFile = org.apache.commons.io.FileUtils.toFile(page.getUrl());
		if (imgFile == null)
			throw new IOException("Cannot retrieve image url from: "+page.getUrl());
				
		File thmbsDir = new File(FilenameUtils.getFullPath(imgFile.getAbsolutePath())+"/"+LocalDocConst.THUMBS_FILE_SUB_FOLDER);
		File outFile = new File(thmbsDir.getAbsolutePath()+"/"+FilenameUtils.getBaseName(imgFile.getName())+".jpg");
		
		return outFile.getAbsolutePath();
	}

	/**
	 * Searches for a corresponding XML for an image file, i.e. an XML with the
	 * same name.
	 * 
	 * @param imgName
	 *            an existing image file
	 * @param xmlInputDir
	 *            where to search
	 * @return an existing and readable xml file or null if none is found
	 */
	public static File findXml(String imgName, File xmlInputDir) {
		//assume XML file
		File xmlFile = new File(xmlInputDir.getAbsolutePath() + File.separatorChar + imgName
				+ ".xml");

		if (xmlFile.exists() && xmlFile.canRead()) {
			//			logger.debug("Found XML for page " + imgName + " in " + xmlFile.getAbsolutePath());
			return xmlFile;
		} else {
			//			logger.debug("Found NO XML for page " + imgName + "!");
			return null;
		}
	}
	
	/**
	 * Check existence of PAGE XML files and return tree map of (fake) image filenames and files
	 * @param baseDir folder in which images should be found
	 * @param xmlDir folder holding all existing xml files - by default named "page"
	 * @return
	 * @throws IOException
	 */
	public static TreeMap<String, File> createDummyImgFilesForXmls(File baseDir, File xmlDir) throws IOException {
		File[] xmlArr = xmlDir.listFiles();
		
		//Use number sensitive ordering so that:		
		//img1 -> img2 -> ... -> img9 -> img10 -> etc.
		//try Java 8: http://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html#naturalOrder--
		Comparator<String> naturalOrderComp = new NaturalOrderComparator();
		TreeMap<String, File> pageMap = new TreeMap<>(naturalOrderComp);

		if (xmlArr == null || xmlArr.length == 0){
			logger.debug("Folder " + xmlDir.getAbsolutePath() + " does not contain any XML files!");
			logger.debug("No PAGE XML files found - returning empty TreeMap");
			return pageMap;
		}
		
		for (File xml : xmlArr) {
			final String pageName = FilenameUtils.getBaseName(xml.getName());
			if (!pageMap.containsKey(pageName)) {
				//new page. add this xml
				File img = new File(baseDir, pageName+".png");
				pageMap.put(pageName, img);
				logger.debug(pageName + ": created fake image for: " + img.getName());
			} 
		}
		return pageMap;
	}
	
	/**
	 * Finds image files and builds a list with distinct pages (based on
	 * filenames).<br>
	 * If several image files in different formats are found for one page, the
	 * one with the highest priority (according to {@link eu.transkribus.core.io.util.ImgPriority}) is
	 * returned in the list.
	 * 
	 * @param inputDir
	 *            that is searched for image files
	 * @return a sorted Map with page image files
	 * @throws IOException if the inputDir can't be read or no image file is found there
	 */
	public static TreeMap<String, File> findImgFiles(File inputDir) throws IOException {
		File[] imgArr = inputDir.listFiles(new ImgFileFilter());
		
		if(imgArr == null || imgArr.length == 0){
			//try fallback to OCRmaster folder
			logger.debug("No images found in doc root! Trying fall back to subdir: " + LocalDocConst.OCR_MASTER_DIR);
			File ocrMasterDir = new File(inputDir.getAbsolutePath() + File.separator + LocalDocConst.OCR_MASTER_DIR);
			imgArr = ocrMasterDir.listFiles(new ImgFileFilter());
		}
		
		if(imgArr == null || imgArr.length == 0){
			//try fallback to FEP-style img folder
			logger.debug("No images found in OCRmaster folder! Trying fall back to subdir: " + LocalDocConst.FEP_IMG_DIR);
			File fepImgDir = new File(inputDir.getAbsolutePath() + File.separator + LocalDocConst.FEP_IMG_DIR);
			imgArr = fepImgDir.listFiles(new ImgFileFilter());
		}
		
		
		//Use number sensitive ordering so that:		
		//img1 -> img2 -> ... -> img9 -> img10 -> etc.
		//try Java 8: http://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html#naturalOrder--
		Comparator<String> naturalOrderComp = new NaturalOrderComparator();
		TreeMap<String, File> pageMap = new TreeMap<>(naturalOrderComp);

		if (imgArr == null || imgArr.length == 0){
			logger.debug("Folder " + inputDir.getAbsolutePath() + " does not contain any image files!");
			logger.debug("No images found - returning empty TreeMap");
			return pageMap;
		}
		
		for (File img : imgArr) {
//			logger.debug("img = " + img.getName());
			final String pageName = FilenameUtils.getBaseName(img.getName());
			if (!pageMap.containsKey(pageName)) {
				//new page. add this img in any case
				pageMap.put(pageName, img);
				logger.debug(pageName + ": found image: " + img.getName());
			} else if (ImgPriority.getPriority(img) > ImgPriority
					.getPriority(pageMap.get(pageName))) {
				logger.debug(pageName + ": found better image: " + img.getName() + " | Removing "
						+ pageMap.get(pageName).getName());
				//found a better img with same name
				pageMap.put(pageName, img);
			}
		}
		return pageMap;
	}

	/**
	 * searches the inputDir for files ending in XmlFileFilter.mdFileEnding,
	 * which is e.g. "metadata.xml". If a file is found, it is parsed into a
	 * TrpDocMetadata Object.
	 * 
	 * @param inputDir
	 *            where the document is stored
	 * @return TrpDocMetadata Object or null if no mdFile is found.
	 * @throws IOException
	 *             If more than one mdFile is on the path
	 */
	public static TrpDocMetadata findDocMd(File inputDir) throws IOException {
		final File[] mdFiles = inputDir.listFiles(new MdFileFilter());

		if (mdFiles == null || mdFiles.length == 0) {
			//no file => no metadata
			throw new FileNotFoundException("No metadata XML was found on path: "
					+ inputDir.getAbsolutePath());
		} else {
			final File mdFile = mdFiles[0];
			logger.info("Found md File " + mdFile.getAbsolutePath());
			try {
				TrpDocMetadata docMd = JaxbUtils.unmarshal(mdFile, TrpDocMetadata.class);
				//set ID to -1 in order to create confusion
				docMd.setDocId(-1);
				
				return docMd;
			} catch (JAXBException je) {
				//this file will be ignored
				throw new IOException("The md File " + mdFile.getName()
						+ " did not obey the correct format. "
						+ "A doc without metadata will be provided.");
			}
		}
	}
	
	public static TrpDocMetadata findDocMd2(File docDir) {
		try {
			return findDocMd(docDir);
		} catch (IOException ioe) {
			return null;
		}
	}
	
	public static TrpDocMetadata findOrCreateDocMd(File inputDir) {
		TrpDocMetadata docMd;
		try {
			docMd = findDocMd(inputDir);
		} catch (IOException e) {
			docMd = new TrpDocMetadata();
		}
		
		docMd.setLocalFolder(inputDir);
		docMd.setDocId(-1);
		if (StringUtils.isEmpty(docMd.getTitle())) {
			docMd.setTitle(inputDir.getName());
		}
		
		return docMd;
	}
}
