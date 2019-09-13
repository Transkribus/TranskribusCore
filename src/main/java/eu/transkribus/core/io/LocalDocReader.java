package eu.transkribus.core.io;

import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observer;
import java.util.TreeMap;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dea.util.pdf.PageImageWriter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;

import eu.transkribus.core.exceptions.CorruptImageException;
import eu.transkribus.core.io.formats.Page2010Converter;
import eu.transkribus.core.io.formats.XmlFormat;
import eu.transkribus.core.io.util.ImgFileFilter;
import eu.transkribus.core.io.util.ImgPriority;
import eu.transkribus.core.io.util.MdFileFilter;
import eu.transkribus.core.model.beans.DocumentUploadDescriptor.PageUploadDescriptor;
import eu.transkribus.core.model.beans.EdFeature;
import eu.transkribus.core.model.beans.EdOption;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocDir;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.TrpUpload;
import eu.transkribus.core.model.beans.enums.EditStatus;
import eu.transkribus.core.model.beans.enums.TranscriptionLevel;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.rest.JaxbList;
import eu.transkribus.core.model.builder.mets.TrpMetsBuilder;
import eu.transkribus.core.model.builder.mets.util.MetsUtil;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.ImgUtils;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.NaturalOrderComparator;
import eu.transkribus.core.util.NaturalOrderFileComparator;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.XmlUtils;

/**
 * Reader class for loading a TRP Document from the local filesystem.<br>
 * The given path should contain:<br>
 * <ul>
 * <li>Image files of type JPG, PNG or TIFF (allowed types and priorities defined in
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
 * When the document is loaded for the first time, the structure will be stored on disk in the file
 * "doc.xml". Each subsequent loading will read that file, check if directory content has changed, 
 * and return the given structure if possible.
 * 
 * @author philip
 * 
 */
public class LocalDocReader {

	private final static Logger logger = LoggerFactory.getLogger(LocalDocReader.class);

	public static TrpDoc load(final String path) throws IOException {
		DocLoadConfig config = new DocLoadConfig();
		return load(path, config, null);
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
		return loadPdf(file, path, null);
	}
	
	/**
	 * Extracts images from a pdf into the given directory. 
	 * Further loads the document from the specified image directory.
	 * @param file absolute path of the pdf document
	 * @param path absolute path of the directory to which the pdf images should be extracted to
	 * @param observer optional observer object to transport state of the extraction process
	 * @return new TrpDoc
	 * @throws IOException
	 * @throws SecurityException
	 * @throws DocumentException
	 */
	public static TrpDoc loadPdf(final String file, final String path, Observer observer) 
			throws IOException, SecurityException, DocumentException {
		logger.info("Extracting pdf " + file + " to folder " + path);
		PageImageWriter imgWriter = new PageImageWriter();
		if(observer != null) {
			imgWriter.addObserver(observer);
		}
		imgWriter.extractImages(file, path);
		// Upload PDF to server temp folder and extract images with pdfimages -j
		
		
		return load(imgWriter.getExtractDirectory());
	}
	
	/**
	 * @param path the path where the document is stored
	 * @param forceCreatePageXml if true, then a Page XML skeleton is created for pages where none exists
	 * @return the constructed document
	 * @throws IOException if the path can't be read or is malformed or an invalid XML format is found
	 */
	@Deprecated
	public static TrpDoc load(final String path, boolean forceCreatePageXml) throws IOException {
		DocLoadConfig config = new DocLoadConfig();
		config.setForceCreatePageXml(forceCreatePageXml);
		return load(path, config, null);
	}
	
	/**
	 * Load document given extra parameter for syncing
	 * @param path the path where the document is stored
	 * @param forceCreatePageXml if true, then a Page XML skeleton is created for pages where none exists
	 * @param enableSyncWithoutImages if true, a document is created even if there are no images (only relevant for syncing) 
	 * @return the constructed document
	 * @throws IOException if the path can't be read or is malformed or an invalid XML format is found
	 */
	@Deprecated
	public static TrpDoc load(final String path, boolean forceCreatePageXml, boolean enableSyncWithoutImages) throws IOException {
		DocLoadConfig config = new DocLoadConfig();
		config.setForceCreatePageXml(forceCreatePageXml);
		config.setEnableSyncWithoutImages(enableSyncWithoutImages);
		return load(path, config, null);
	}
	
	@Deprecated
	public static TrpDoc load(final String path, boolean preserveOcrTxtStyles, 
			boolean preserveOcrFontFamily, boolean replaceBadChars, boolean forceCreatePageXml,
			boolean enableSyncWithoutImages) throws IOException {
		DocLoadConfig config = new DocLoadConfig();
		config.setPreserveOcrTxtStyles(preserveOcrTxtStyles);
		config.setPreserveOcrFontFamily(preserveOcrFontFamily);
		config.setReplaceBadChars(replaceBadChars);
		config.setForceCreatePageXml(forceCreatePageXml);
		config.setEnableSyncWithoutImages(enableSyncWithoutImages);
		return load(path, config, null);
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
	 * 
	 */
	@Deprecated
	public static TrpDoc load(final String path, boolean preserveOcrTxtStyles, 
			boolean preserveOcrFontFamily, boolean replaceBadChars, boolean forceCreatePageXml,
			boolean enableSyncWithoutImages, IProgressMonitor monitor) throws IOException {
		DocLoadConfig config = new DocLoadConfig();
		config.setPreserveOcrTxtStyles(preserveOcrTxtStyles);
		config.setPreserveOcrFontFamily(preserveOcrFontFamily);
		config.setReplaceBadChars(replaceBadChars);
		config.setForceCreatePageXml(forceCreatePageXml);
		config.setEnableSyncWithoutImages(enableSyncWithoutImages);
		return load(path, config, monitor);
	}
	
	public static TrpDoc load(final String path, DocLoadConfig config) throws IOException {
		return load(path, config, null);
	}
	
	/**
	 * Loads a document from path.<br>
	 * 
	 * Document metadata has to be in an XML called "metadata.xml".<br>
	 * 
	 * Image files and corresponding XML/txt files have to have the same name. <br>
	 * Lexicographic order of image names will imply order of pages.<br>
	 * Types of transcript source files are searched in this order:
	 * <ol>
	 * <li>./page: PAGE XMLs according to schema 2010/2013</li>
	 * <li>./ocr: Abbyy Finereader XMLs schema version 10</li>
	 * <li>./alto: ALTO v2 XMls
	 * <li>./txt: txt files with transcription fulltext only
	 * </ol>
	 * Testdoc is in $dea_scratch/TRP/TrpTestDoc <br>
	 * No versioning of files for local use!<br>
	 * 
	 * @param path the path where the document is stored
	 * @param config {@link DocLoadConfig}
	 * @return the constructed document
	 * @throws IOException if the path can't be read or is malformed
	 * 
	 * @todo implement monitor feedback! 
	 * @todo Respect Storage.uploadDocument where the monitor will be used by the upload itself later.
	 */
	public static TrpDoc load(final String path, DocLoadConfig config, IProgressMonitor monitor) throws IOException {
		//create the document
		TrpDoc doc = new TrpDoc();
		//check OS and adjust URL protocol
		final String os = System.getProperty("os.name");
				
		/*
		 * FIXME use SysUtils.isWin() here?
		 */
		if (os.toLowerCase().contains("win")) {
			LocalDocConst.URL_PROT_CONST = "file:///";
		} //else: keep default

		final File inputDir = new File(path);
		final File docXml = new File(inputDir.getAbsolutePath() + File.separator + LocalDocConst.DOC_XML_FILENAME);

		//validate input path ======================================================

		checkInputDir(inputDir);
		
		// search for IMG files
		TreeMap<String, File> pageMap = findImgFiles(inputDir);
		logger.info("Found " + pageMap.entrySet().size() + " page images.");
		
		// need a special variable to test whether we are in sync mode (only then do the following!!!!)
		if (pageMap.isEmpty() && config.isEnableSyncWithoutImages()) {
			pageMap = createDummyImgFiles(inputDir);
		}
		
		if(pageMap.isEmpty()) {
			throw new FileNotFoundException("The directory does not contain any images: " + inputDir.getAbsolutePath());
		}
		
		TrpDocMetadata docMd = null;
		boolean doRefresh = true;
		//try to read doc structure from disk
		if(docXml.isFile()) {
			doc = loadDocXml(docXml);
			if(isValid(doc, pageMap.size(), config.isForceCreatePageXml())) {
				logger.info("Loaded document structure from disk.");
				docMd = doc.getMd();
				//no refresh is necessary as doc structure matches the input dir content
				doRefresh = false;
			} else {
				if(doc != null && doc.getMd() != null) {
					//keep any existing metadata if invalid doc structure was found
					docMd = doc.getMd();
				}
				logger.info("Removing faulty doc XML from disk and doing reload.");
				docXml.delete();
				doc = new TrpDoc();
			}
		}

		logger.info("Reading document at " + inputDir.getAbsolutePath());

		//find metadata file if not extracted from doc.xml =============================================
		if(docMd == null) {
			try {
				docMd = loadDocMd(inputDir);
			} catch(IOException ioe) {
				docMd = new TrpDocMetadata();
			}
		}
		
		initDocMd(docMd, inputDir, config.isStripServerRelatedMetadata());
		
		//Set the docMd
		doc.setMd(docMd);
		
		if(!doRefresh) {
			//Stop now and reuse doc structure from file
			return doc;
		}

		//Construct the input dir with pageXml Files. For sync mode the input folder can be the page directory directly
		File pageInputDir = (inputDir.getName().equals(LocalDocConst.PAGE_FILE_SUB_FOLDER)&&config.isEnableSyncWithoutImages() ? inputDir : getPageXmlInputDir(inputDir));
		if (config.isForceCreatePageXml() && !pageInputDir.isDirectory()) {
			pageInputDir.mkdir();
		}
		
		//abbyy XML search path
		File ocrInputDir = getOcrXmlInputDir(inputDir);
				
		//alto XML search path
		File altoInputDir = getAltoXmlInputDir(inputDir);
		
		//txt file search path
		File txtInputDir = getTxtInputDir(inputDir);
		
		//backupfolder for outdated page format files, if any
		final String backupFolderName = XmlFormat.PAGE_2010.toString().toLowerCase()
				+ "_backup";
		final String backupPath = pageInputDir.getAbsolutePath() + File.separator
				+ backupFolderName;
		
		// iterate imgList, search for corresponding XML files and build TrpPages
		int pageNr = 1;
		List<TrpPage> pages = new ArrayList<TrpPage>(pageMap.entrySet().size());
		
		int i = 1;
		for (Entry<String, File> e : pageMap.entrySet()) {
			monitorSubTask(monitor, "Inspecting image " + i++ + "/" + pageMap.entrySet().size());
			File imgFile = e.getValue();
			//the img file name without extension
			final String imgFileName = e.getKey();

			//check for a page XML of this name
			File pageXml = findXml(imgFileName, pageInputDir, config.isFindFilesIgnoringPrefix());
			
			//TODO thumbURL dir + imgFile.getName())+".jpg"
			File thumbFile = getThumbFile(inputDir, imgFileName);
					
			if(pageXml != null) {
				logger.debug("page Xml found for file: " + pageXml.getAbsolutePath());
				XmlFormat xmlFormat = XmlUtils.getXmlFormat(pageXml);
				switch(xmlFormat){
				case PAGE_2010:
					//initial solution for Windows concurrent access bug: do not use backup mechanism of Page2010Converter
					File tmp = new File(backupPath);
					if(!tmp.isDirectory()) {
						tmp.mkdir();
					}
					File dest = new File(tmp.getAbsolutePath(), pageXml.getName());
					FileUtils.moveFile(pageXml, dest);
					pageXml = Page2010Converter.convert(dest, pageXml);
					
					//updated method including backup creation using FileUtils#moveFile
//					pageXml = Page2010Converter.updatePageFormatSingleFile(pageXml, backupPath);
					break;
				case PAGE_2013:
					break;
				default:
					throw new IOException("Incompatible XML file in PAGE XML path! " + pageXml.getAbsolutePath());
				}
			} 
			
			//try to read image dimension in any case to detect corrupt files immediately!
			//FIXME this is taking too long and is only necessary on initial loading
			Dimension dim = null;
			String imageRemark = null;
			try {
				if (!config.isEnableSyncWithoutImages()) {
					dim = ImgUtils.readImageDimensions(imgFile);
				} else if (config.getDimensionMap() != null){
					dim = config.getDimensionMap().get(FilenameUtils.getBaseName(imgFileName));
				}
			} catch(CorruptImageException cie) {
				logger.error("Image is corrupt: " + imgFile.getAbsolutePath(), cie);
				imageRemark = getCorruptImgMsg(imgFile.getName());
			}
			
			if(pageXml == null && config.isForceCreatePageXml()) {
				//if no page XML, then create one at this path
				File pageOutFile = new File(pageInputDir.getAbsolutePath() + File.separatorChar 
						+ imgFileName + ".xml");
				File abbyyXml = config.skipAbbyy ? null : findXml(imgFileName, ocrInputDir, config.isFindFilesIgnoringPrefix());
				File altoXml = config.skipAlto ? null : findXml(imgFileName, altoInputDir, config.isFindFilesIgnoringPrefix());
				File txtFile = config.skipTxt ? null : findFile(imgFileName, txtInputDir, "txt", config.isFindFilesIgnoringPrefix());
				
				pageXml = createPageXml(pageOutFile, false, abbyyXml, altoXml, txtFile, 
						config.isPreserveOcrFontFamily(), 
						config.isPreserveOcrTxtStyles(), 
						config.isReplaceBadChars(), imgFile.getName(), dim);
			}
			
			// TODO: merge text of txt files into existing PAGE-XMLs
//			else if (pageXml != null && config.isSyncTextWithExistingPageXml()) {
//				File txtFile = findFile(imgFileName, txtInputDir, "txt");
//				if (txtFile != null) {
//					logger.debug("syncing txt file with exiting PAGE-XML: "+txtFile.getName());
//					String text = readTextFromFile(txtFile);
//					PageXmlUtils.applyTextToLines(page, text);
//					
//					
//				}
//			}
			
			TrpPage page = buildPage(inputDir, pageNr++, imgFile, pageXml, thumbFile, dim, imageRemark);
			pages.add(page);
		}

		doc.setPages(pages);
		doc.getMd().setNrOfPages(doc.getPages().size());
		
		// set editorial declaration:
		List<EdFeature> features = readEditDeclFeatures(doc.getMd().getLocalFolder());
		doc.setEdDeclList(features);

		logger.debug(doc.toString());
		
		//store doc on disk to save time on next load
		LocalDocWriter.writeDocXml(doc, docXml);
		
		return doc;
	}
	
	/**
	 * Update progress via {@link IProgressMonitor#subTask(String)}  if monitor is initialized.
	 *  
	 * @param monitor {@link IProgressMonitor}. If null is passed then the method does nothing.
	 * @param msg the message to send
	 */
	private static void monitorSubTask(IProgressMonitor monitor, String msg) {
		if(monitor == null) {
			return;
		}
		if(msg == null) {
			//Just clear the subtask message on the monitor
			msg = "";
		}
		monitor.subTask(msg);
	}

	public static File getPageXmlInputDir(File inputDir) {
		return new File(inputDir.getAbsolutePath() + File.separatorChar
				+ LocalDocConst.PAGE_FILE_SUB_FOLDER);
	}
	
	public static File getOcrXmlInputDir(File inputDir) {
		return new File(inputDir.getAbsolutePath() + File.separatorChar
				+ LocalDocConst.OCR_FILE_SUB_FOLDER);
	}
	
	public static File getAltoXmlInputDir(File inputDir) {
		return new File(inputDir.getAbsolutePath() + File.separatorChar
				+ LocalDocConst.ALTO_FILE_SUB_FOLDER);
	}
	
	public static File getTxtInputDir(File inputDir) {
		return new File(inputDir.getAbsolutePath() + File.separatorChar
				+ LocalDocConst.TXT_FILE_SUB_FOLDER);
	}

	/**
	 * 
	 * 
	 * @param inputDir
	 * @throws IOException
	 */
	public static void checkInputDir(File inputDir) throws IOException {
		
		if (!inputDir.isDirectory()) {
			throw new IOException(inputDir.getAbsolutePath() + " is not a directory.");
		}
		
		if (!inputDir.canRead()) {
			logger.info("IS NOT READABLE");
			throw new IOException(inputDir.getAbsolutePath() + " is not readable.");
		}
		
		if (!inputDir.canWrite()) {
			throw new IOException(inputDir.getAbsolutePath() + " is not writeable.");
		}
	}

	private static TrpDoc loadDocXml(File docXml) {
		TrpDoc doc;
		try {
			doc = JaxbUtils.unmarshal(docXml, TrpDoc.class, TrpDocMetadata.class, TrpPage.class, TrpTranscriptMetadata.class, EdFeature.class, EdOption.class);
		} catch (Exception e) {
			doc = null;
		}
		return doc;		
	}
	
	/**
	 * Method will create a PAGE XML from the given source files at pageOutFile.
	 * if no supported source file exists (abbyy/alto/txt), then a skeleton will be created if possible.
	 * <br/><br/>
	 * This method must NEVER return null. Many mechanisms in Transkribus
	 * depend on this method reliably creating a file.
	 * 
	 * @param pageOutFile
	 * @param doOverwrite
	 * @param abbyyXml
	 * @param altoXml
	 * @param txtFile
	 * @param preserveOcrFontFamily
	 * @param preserveOcrTxtStyles
	 * @param replaceBadChars
	 * @param imgFile
	 * @param dim
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static File createPageXml(File pageOutFile, boolean doOverwrite, File abbyyXml, 
			File altoXml, File txtFile, boolean preserveOcrFontFamily, boolean preserveOcrTxtStyles, 
			boolean replaceBadChars, final String imgFileName, Dimension dim) 
					throws FileNotFoundException, IOException {
		if(pageOutFile == null) {
			throw new IllegalArgumentException("PAGE XML output File is null.");
		}
		if(pageOutFile.exists() && !doOverwrite) {
			throw new IOException("PAGE XML already exists at: " + pageOutFile.getAbsolutePath());
		}
		if(StringUtils.isEmpty(imgFileName)) {
			throw new IllegalArgumentException("Image filename must not be empty");
		}
		
		PcGtsType pc = null;
		
		if(abbyyXml != null){
			//try find Abbyy XML
			pc = createPageFromAbbyy(imgFileName, abbyyXml, preserveOcrTxtStyles, preserveOcrFontFamily, replaceBadChars);
		}
		
		if(pc == null && altoXml != null){
			//try find ALTO XML
			pc = createPageFromAlto2(imgFileName, altoXml, preserveOcrTxtStyles, preserveOcrFontFamily, replaceBadChars);
		}
		
		//from here we need the dimension of the image
		if(dim == null) {
			//set (0,0) here in order to make the following work
			dim = new Dimension();
		}
		
		if(pc == null && txtFile != null){
			//try find TXT file
			pc = createPageFromTxt(imgFileName, dim, txtFile);
		}
		
		//if still null, there is no suitable file for this page yet => create one
		if (pc == null) {
			logger.warn("No Transcript XML found for img: " + FilenameUtils.getBaseName(imgFileName));
			logger.info("Creating empty PageXml.");
			pc = PageXmlUtils.createEmptyPcGtsType(imgFileName, dim);
		}
		
		//create the file
		try{
			JaxbUtils.marshalToFile(pc, pageOutFile);
		} catch (JAXBException je) {
			throw new IOException("Could not create PageXml on disk!", je);
		}
		
		return pageOutFile;
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
	
	public static List<TrpDocDir> listDocDirs(final String path) throws FileNotFoundException {
		return listDocDirs(path, null);
	}
	
	public static List<TrpDocDir> listDocDirs(final String path, IProgressMonitor monitor) throws FileNotFoundException {
		if(path == null || path.isEmpty()){
			throw new IllegalArgumentException("Path is null or empty!");
		}
		File dir = new File(path);
		if(!dir.isDirectory()){
			throw new FileNotFoundException("Path is not a directory: " + path);
		}
		//TODO list PDFs
		File[] docDirs = dir.listFiles(new FileFilter() {	
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory() || pathname.getName().endsWith(".pdf");
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
			docDir.setDocDir(d);
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
	
	public static PcGtsType createPageFromAlto2(final String imgFileName, File altoXml, boolean preserveOcrTxtStyles, boolean preserveOcrFontFamily,
			boolean replaceBadChars) throws IOException {
		try {
			XmlFormat xmlFormat = XmlUtils.getXmlFormat(altoXml);
			if (xmlFormat.equals(XmlFormat.ALTO_2)) {
				logger.info(altoXml.getAbsolutePath() + ": Transforming ALTO v2 XMLs to PAGE XML.");
				return PageXmlUtils.createPcGtsTypeFromAlto(altoXml, imgFileName, preserveOcrTxtStyles, preserveOcrFontFamily, replaceBadChars);
			}
			else if(xmlFormat.equals(XmlFormat.ALTO_BNF)){
				logger.info(altoXml.getAbsolutePath() + ": Transforming ALTO BnF XMLs to PAGE XML.");
				return PageXmlUtils.createPcGtsTypeFromAltoBnF(altoXml, imgFileName, preserveOcrTxtStyles, preserveOcrFontFamily, replaceBadChars);
			}
			else if(xmlFormat.equals(XmlFormat.ALTO_3)){
				logger.info(altoXml.getAbsolutePath() + ": Transforming ALTO v3 XMLs to PAGE XML.");
				return PageXmlUtils.createPcGtsTypeFromAltov3(altoXml, imgFileName, preserveOcrTxtStyles, preserveOcrFontFamily, replaceBadChars);
			}
			throw new IOException("Not a valid ALTO file.");
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
	
	public static String readTextFromFile(File txtFile) throws IOException {
		byte[] buf = new byte[4096]; 
		java.io.FileInputStream fis = new FileInputStream(txtFile);

		// (1)
		UniversalDetector detector = new UniversalDetector(null);

		// (2)
		int nread;
		while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
		  detector.handleData(buf, 0, nread);
		}
		fis.close();
		// (3)
		detector.dataEnd();

		// (4)
		String encoding = detector.getDetectedCharset();
		if (encoding != null) {
		  logger.debug("Detected encoding = " + encoding);
		} else {
		  logger.debug("No encoding detected - use utf-8");
		  encoding = "utf-8";
		}

		// (5)
		detector.reset();

		String text = FileUtils.readFileToString(txtFile, encoding);
		//String text = FileUtils.readFileToString(txtFile, "ISO-8859-1");
		//logger.debug("text = "+text);
		
		return text;
	}

	/**
	 * use this library to detect the text file encoding automatically
	 * previously we imported utf-8 as default which often brought errors if the encoding was different
	 */
	public static PcGtsType createPageFromTxt(final String imgFileName, Dimension dim, File txtFile) throws IOException {
		logger.debug("creating PAGE file from text file");
		String text = readTextFromFile(txtFile);
		return PageXmlUtils.createPcGtsTypeFromText(imgFileName, dim, text, TranscriptionLevel.LINE_BASED, false);
	}

	private static PcGtsType createPageFromAbbyy(final String imgFileName, File abbyyXml, boolean preserveOcrTxtStyles,
			boolean preserveOcrFontFamily, boolean replaceBadChars) throws IOException {
		try{
			XmlFormat xmlFormat = XmlUtils.getXmlFormat(abbyyXml);
			if(xmlFormat.equals(XmlFormat.ABBYY_10)){
				logger.info(abbyyXml.getAbsolutePath() + ": Transforming Finereader10/11 XML to PAGE XML.");
				PcGtsType pc = PageXmlUtils.createPcGtsTypeFromAbbyy(
						abbyyXml, imgFileName, 
						preserveOcrTxtStyles, preserveOcrFontFamily, 
						replaceBadChars
						);
				return pc;
			}
			throw new IOException("Not a valid Finereader10/11 XML file.");
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
	
	public static File getThumbFile(File inputDir, String imgFileName) {
		//Construct thumb dir name. Its creation is handled by the GUI App
		final String path = getThumbDir(inputDir).getAbsolutePath() + File.separatorChar 
				+ imgFileName + LocalDocConst.THUMB_FILE_EXT;
		return new File(path);
	}

	public static String getThumbFileName(TrpPage page) throws IOException {
		File imgFile = FileUtils.toFile(page.getUrl());
		if (imgFile == null)
			throw new IOException("Cannot retrieve image url from: "+page.getUrl());
				
		File inputDir = new File(FilenameUtils.getFullPath(imgFile.getAbsolutePath()));
		File thumbDir = getThumbDir(inputDir);
		File outFile = new File(thumbDir.getAbsolutePath()+"/"+FilenameUtils.getBaseName(imgFile.getName())+".jpg");
		
		return outFile.getAbsolutePath();
	}
	public static File getThumbDir(File inputDir) {
		return new File(inputDir.getAbsolutePath() + File.separatorChar + LocalDocConst.THUMBS_FILE_SUB_FOLDER);
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
	 * @param useDummyImage
	 * 			  treat the image file as corrupt/missing. 
	 * 			XML may then be null as none could be created due to missing Dimension.
	 * @return a TrpPage object with Transcript. The Transcript is null, if
	 *         pageXml is null.
	 * @throws MalformedURLException if an URL can't be constructed from parentDir
	 */
	public static TrpPage buildPage(File inputDir, int pageNr, File img, 
			File pageXml, File thumb, Dimension dim, final String missingImageRemark) throws IOException {
		logger.debug(pageNr + ": XML = " + (pageXml == null ? "null" : pageXml.getName())
				+ " - IMG = " + (img == null ? "null" : img.getName()));

		//FIXME handle broken images
		
		TrpPage page = new TrpPage();
		page.setPageNr(pageNr);
		page.setKey(null);
		page.setDocId(-1);
		
		if(img != null) {
			page.setImgFileName(img.getName());
			final URL imgUrl = img.toURI().toURL();
			page.setUrl(imgUrl);
		} else {
			page.setImgFileName(LocalDocConst.NO_IMAGE_FILENAME);
		}
		
		if(!StringUtils.isEmpty(missingImageRemark)) {
			URL dummyUrl = LocalDocConst.getDummyImageUrl();
			page.setUrl(dummyUrl);
			page.setImgFileProblem(missingImageRemark);
		}
		
		if(thumb != null) {
			final URL thumbUrl = thumb.toURI().toURL();
			page.setThumbUrl(thumbUrl);
		}
		
		if(dim != null) {
			page.setWidth(dim.width);
			page.setHeight(dim.height);
		}
		
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
	
	public static File findFile(String imgName, File inputDir, String extension, boolean ignorePrefix) {
		File f = findFile(imgName, inputDir, extension);
		if (f != null) {
			return f;
		}
		else if (ignorePrefix && imgName.startsWith("img_")) { // FEP documents have file prefixes equal to their folder names
			return findFile(inputDir.getName()+"_"+imgName.substring(4), inputDir, extension);
		}
		else {
			return null;
		}
	}
	
	public static File findFile(String imgName, File inputDir, String extension) {
		File file = new File(inputDir.getAbsolutePath() + File.separatorChar + imgName
				+ "."+extension.toLowerCase());

		if (file.canRead()) {
			return file;
		} else {
			// try uppercase extension
			file = new File(inputDir.getAbsolutePath() + File.separatorChar + imgName
					+ "."+extension.toUpperCase());
			
			if (file.canRead()) {
				return file;
			} else {
				return null;
			}
		}
	}
	
	public static File findDefaultPageXmlForImage(String imagePath) {
		String folder = FilenameUtils.getFullPathNoEndSeparator(imagePath);
		String basename = FilenameUtils.getBaseName(imagePath);
		
		return findXml(basename, new File(folder+File.separator+LocalDocConst.PAGE_FILE_SUB_FOLDER), false);
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
	public static File findXml(String imgName, File xmlInputDir, boolean ignorePrefix) {
		return findFile(imgName, xmlInputDir, "xml", ignorePrefix);
		
		// OLD CODE
//		//assume XML file
//		File xmlFile = new File(xmlInputDir.getAbsolutePath() + File.separatorChar + imgName
//				+ ".xml");
//
//		if (xmlFile.canRead()) {
//			//			logger.debug("Found XML for page " + imgName + " in " + xmlFile.getAbsolutePath());
//			return xmlFile;
//		} else {
//			//			logger.debug("Found NO XML for page " + imgName + "!");
//			return null;
//		}
	}
	
	/**
	 * Check existence of PAGE XML or txt files and return tree map of (fake) image filenames and files
	 * @param baseDir folder in which images should be found
	 * @return
	 * @throws IOException
	 */
	public static TreeMap<String, File> createDummyImgFiles(File baseDir) throws IOException {
		
		//for syncing page file: the base directory can also be directly the page folder		
		File xmlDir = (baseDir.getName().equals(LocalDocConst.PAGE_FILE_SUB_FOLDER)) ? baseDir : getPageXmlInputDir(baseDir);
		
		//File xmlDir = getPageXmlInputDir(baseDir);
		File txtDir = getTxtInputDir(baseDir);
		
		// check whether xml directory contains files, if not, assume txt directory has content
		File workingDir = (xmlDir==null || !xmlDir.exists())?txtDir:xmlDir;
		File[] fileArr = workingDir.listFiles();
		
		//Use number sensitive ordering so that:		
		//img1 -> img2 -> ... -> img9 -> img10 -> etc.
		//try Java 8: http://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html#naturalOrder--
		Comparator<String> naturalOrderComp = new NaturalOrderComparator();
		TreeMap<String, File> pageMap = new TreeMap<>(naturalOrderComp);

		if (fileArr == null || fileArr.length == 0){
			if (!workingDir.exists()) {
				logger.debug("Could not find directory " +workingDir.getName() + " holding files for synchronisation!");
			}

			logger.debug("Folder " + workingDir.getAbsolutePath() + " does not contain any files!");
			logger.debug("No PAGE XML nor txt files found - returning empty TreeMap");
			return pageMap;
		}
		
		for (File page : fileArr) {
			final String pageName = FilenameUtils.getBaseName(page.getName());
			if (!pageMap.containsKey(pageName)) {
				//new page. add this xml
				File img = new File(baseDir, pageName+".png");
				pageMap.put(pageName, img);
				logger.debug(pageName + ": created fake image " + img.getName());
			} 
		}
		return pageMap;
	}
	
	/**
	 * Check existence of PAGE XML files and return tree map of (fake) image filenames and files
	 * @param baseDir folder in which images should be found
	 * @param xmlDir folder holding all existing xml files - by default named "page"
	 * @return
	 * @throws IOException
	 */
	@Deprecated 
	public static TreeMap<String, File> createDummyImgFilesForXmls(File baseDir, File xmlDir) throws IOException {

		return createDummyImgFiles(baseDir);
	}
	
	private static List<File> listFilesSortedByNaturalComparator(File inputDir, String[] extensions, boolean caseSensitive, String...excludeFilenames) throws IOException {
		List<File> files = CoreUtils.listFilesReturnFiles(1, inputDir.getAbsolutePath(), extensions, caseSensitive, excludeFilenames);
		Collections.sort(files, new NaturalOrderFileComparator());
		return files;
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
//				logger.debug(pageName + ": found image: " + img.getName());
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
	public static TrpDocMetadata loadDocMd(File inputDir) throws IOException {
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
	
	/**
	 * Initiates the metadata object. E.g. Doc ID is set to -1 and the local folder is set to input dir.
	 * If no title is included, then the input dir name is used.
	 * @param docMd
	 * @param inputDir
	 * @param stripAllServerRelatedMetadata if true, then all server related data is removed from the metadata object: collections, symbolic image links, etc.
	 */
	private static TrpDocMetadata initDocMd(TrpDocMetadata docMd, File inputDir, boolean stripAllServerRelatedMetadata) {
		if(inputDir == null) {
			throw new IllegalArgumentException("Input dir must not be null.");
		}
		if(docMd == null) {
			docMd = new TrpDocMetadata();
		}
		docMd.setLocalFolder(inputDir);
		docMd.setDocId(-1);
		if (StringUtils.isEmpty(docMd.getTitle())) {
			docMd.setTitle(inputDir.getName());
		}
		if(stripAllServerRelatedMetadata) {
			docMd.getColList().clear();
			docMd.setFimgStoreColl(null);
			docMd.setOrigDocId(null);
			docMd.setPageId(null);
			docMd.setThumbUrl(null);
			docMd.setUrl(null);
			docMd.setUploaderId(-1);
			docMd.setUploader(null);
			docMd.setUploadTimestamp(0);
		}
		return docMd;
	}

	public static TrpDoc load(TrpUpload upload) throws IOException {
		//validate most necessary things
		if(upload == null) {
			throw new IllegalArgumentException("Upload is null.");
		}
		if(upload.getUploadId() < 1) {
			throw new IllegalArgumentException("Invalid upload ID: " + upload.getUploadId());
		}
		if(!upload.canReadDirectories()) {
			throw new IllegalArgumentException("Directories are not readable: " + upload.getUploadTmpDir().getAbsolutePath());
		}
		//transform the upload object into a TRP document
		TrpDoc doc = new TrpDoc();
		TrpDocMetadata md = upload.getMd();
		md.setLocalFolder(upload.getUploadTmpDir());
		doc.setMd(md);
		
		File baseDir = upload.getUploadTmpDir();
		File xmlDir = upload.getUploadPageTmpDir();
		File thumbDir = new File(baseDir.getAbsolutePath() + File.separatorChar + LocalDocConst.THUMBS_FILE_SUB_FOLDER);
		for(PageUploadDescriptor p : upload.getPages()) {
			final int pageNr = p.getPageNr();
			File img = new File(baseDir.getAbsolutePath() + File.separator + p.getFileName());
			if(!img.isFile()){
				throw new FileNotFoundException("Image for page " + pageNr + " does not exist: " + img.getAbsolutePath());
			}
			
			//try to read image dimension in any case to detect corrupt files immediately!
			Dimension dim = null;
			String imageRemark = null;
			try {
				dim = ImgUtils.readImageDimensions(img);
			} catch(CorruptImageException cie) {
				logger.error("Image is corrupt: " + img.getAbsolutePath(), cie);
				imageRemark = getCorruptImgMsg(img.getName());
			}
			
			final String imgBaseName = FilenameUtils.getBaseName(img.getName());
			File thumb = getThumbFile(thumbDir, imgBaseName);
			
			File pageXml = null;
			if(!StringUtils.isEmpty(p.getPageXmlName())) {
				pageXml = new File(xmlDir.getAbsolutePath() + File.separator + p.getPageXmlName());
				if(!pageXml.isFile()){
					throw new FileNotFoundException("PAGE XML for page " + pageNr + " does not exist: " + img.getAbsolutePath());
				}
			} else {
				
				if(!StringUtils.isEmpty(imageRemark)) {
					//if a problem occured when reading the image dimension create PAGE with zero dimension
					dim = new Dimension(0, 0);
				}
				
				File pageOutFile = new File(xmlDir.getAbsolutePath() + File.separatorChar + imgBaseName
						+ ".xml");
				
				PcGtsType pc = PageXmlUtils.createEmptyPcGtsType(img, dim);
				try{
					pageXml = JaxbUtils.marshalToFile(pc, pageOutFile);
				} catch (JAXBException je) {
					logger.error(je.getMessage(), je);
					throw new IOException("Could not create empty PageXml on disk!", je);
				}
			}
			TrpPage page = buildPage(baseDir, pageNr, img, pageXml, thumb, dim, imageRemark);
			doc.getPages().add(page);
		}
								
		return doc;
	}
	
	public static String getCorruptImgMsg(final String imgFileName) {
		return "Image file is corrupt: " + imgFileName;
	}
	
	/**
	 * do a quick check if all files still exist and if new files have been added
	 * 
	 * @param doc
	 * @return
	 * @throws IOException 
	 */
	public static boolean isValid(TrpDoc doc, final int nrOfImagesFound, final boolean forceCreatePageXml) throws IOException {
		if(doc == null || !doc.isLocalDoc()) {
			return false;
		}
		
		//check if nr of image files is the same:
		if(doc.getPages().size() != nrOfImagesFound) {
			return false;
		}
		
		//even though the number is the same, a file might have been exchanged. Check filenames
		List<TrpPage> faultyPages = new LinkedList<>();
		doc.getPages().stream().forEach(p ->  {
			if(!doFilesExist(p, forceCreatePageXml)){
				faultyPages.add(p);
			}
		});
		logger.info("The files of " + faultyPages.size() + " pages have changed.");
		faultyPages.stream().forEach(p -> logger.info(p.getImgFileName()));
		return faultyPages.isEmpty();
	}
	
	private static boolean doFilesExist(TrpPage p, final boolean forceCreatePageXml) {
		File f = FileUtils.toFile(p.getUrl());
		if(f == null || !f.isFile()) {
			return false;
		}
		if(p.getTranscripts().isEmpty() && forceCreatePageXml) {
			return false;
		}
		if(!p.getTranscripts().isEmpty()) {
			File t = FileUtils.toFile(p.getCurrentTranscript().getUrl());
			if(!t.isFile()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Quick access to docMd without loading and checking all the stuff which load() would do.
	 * 
	 * @param dir
	 * @return existing metadata if found, a freshly initiated instance otherwise
	 */
	public static TrpDocMetadata findDocMd(File dir) {
		final File docXml = new File(dir.getAbsolutePath() + File.separator + LocalDocConst.DOC_XML_FILENAME);
		TrpDocMetadata docMd = null;
		TrpDoc doc;
		if(docXml.isFile() && (doc = loadDocXml(docXml)) != null) {
			docMd = doc.getMd();
		}
		//try find legacy metadata.xml
		if(docMd == null) {
			try {
				docMd = loadDocMd(dir);
			} catch(IOException ioe) {}
		}
		//init object as load() would do
		return initDocMd(docMd, dir, false);
	}
	
	public static List<Pair<File, File>> findImgAndPAGEXMLFiles(File folder) throws IOException {
		File pageXmlInputDir = getPageXmlInputDir(folder);
		TreeMap<String, File> imgs = findImgFiles(folder);
		List<Pair<File, File>> imgXmlPairs = new ArrayList<>();
		
		for (Entry<String, File> e : imgs.entrySet()) {
			File pageXML = findXml(e.getKey(), pageXmlInputDir, false);
			if (pageXML == null) {
				logger.warn("No PAGE XML found for img: "+e.getValue().getAbsolutePath()+" - skipping!");
			}
			else {
				imgXmlPairs.add(Pair.of(e.getValue(), pageXML));	
			}
		}
		
		return imgXmlPairs;
	}
	
	public static class DocLoadConfig {
		protected boolean preserveOcrTxtStyles; //true
		protected boolean preserveOcrFontFamily; //true
		protected boolean replaceBadChars; //false
		protected boolean forceCreatePageXml; //true
		protected boolean enableSyncWithoutImages; //false
		protected boolean syncTextWithExistingPageXml=false;
		protected boolean findFilesIgnoringPrefix=false; // used to load FEP documents
		protected boolean skipAbbyy=false; // set to true to skip finding ABBYY OCR files
		protected boolean skipAlto=false; // set to true to skip findin ALTO files
		protected boolean skipTxt=false; // set to true to skip finding txt files
		
		/**
		 * If set to true, then all server-related data is removed from the TrpDocMetadata if existent
		 */
		protected boolean stripServerRelatedMetadata; //false
		
		protected TreeMap<String, Dimension> dimensionMap;
		
		/**
		 * build the default loadConfig
		 */
		public DocLoadConfig() {
			this.preserveOcrTxtStyles = true;
			this.preserveOcrFontFamily = true;
			this.replaceBadChars = false;
			this.forceCreatePageXml = true;
			this.enableSyncWithoutImages = false;
			this.stripServerRelatedMetadata = false;
			this.syncTextWithExistingPageXml=false;
			dimensionMap = null;
		}
		
		public DocLoadConfig(boolean preserveOcrTxtStyles, 
				boolean preserveOcrFontFamily, boolean replaceBadChars, boolean forceCreatePageXml,
				boolean enableSyncWithoutImages) {
			this(preserveOcrTxtStyles,
					preserveOcrFontFamily,
					replaceBadChars,
					forceCreatePageXml,
					enableSyncWithoutImages, 
					null);
		}
		
		/**
		 * @param preserveOcrTxtStyles when creating the pageXML from alto/finereader XMLs, preserve the text style information
		 * @param preserveOcrFontFamily when creating the pageXML from alto/finereader XMLs, preserve the font information
		 * @param replaceBadChars TODO when creating the pageXML from alto/finereader XMLs, specific characters are replaced. see FinereaderUtils
		 * @param forceCreatePageXml if true, then a Page XML skeleton is created for pages where none exists
		 * @param enableSyncWithoutImages if true, document will be created from XMLs only even if no images exist
		 */
		public DocLoadConfig(boolean preserveOcrTxtStyles, 
				boolean preserveOcrFontFamily, boolean replaceBadChars, boolean forceCreatePageXml,
				boolean enableSyncWithoutImages, TreeMap<String, Dimension> dimensionMap) {
			this();
			this.preserveOcrTxtStyles = preserveOcrTxtStyles;
			this.preserveOcrFontFamily = preserveOcrFontFamily;
			this.replaceBadChars = replaceBadChars;
			this.forceCreatePageXml = forceCreatePageXml;
			this.enableSyncWithoutImages = enableSyncWithoutImages;
			this.dimensionMap = dimensionMap;

		}
		
		public boolean isPreserveOcrTxtStyles() {
			return preserveOcrTxtStyles;
		}

		public void setPreserveOcrTxtStyles(boolean preserveOcrTxtStyles) {
			this.preserveOcrTxtStyles = preserveOcrTxtStyles;
		}

		public boolean isPreserveOcrFontFamily() {
			return preserveOcrFontFamily;
		}

		public void setPreserveOcrFontFamily(boolean preserveOcrFontFamily) {
			this.preserveOcrFontFamily = preserveOcrFontFamily;
		}

		public boolean isReplaceBadChars() {
			return replaceBadChars;
		}

		public void setReplaceBadChars(boolean replaceBadChars) {
			this.replaceBadChars = replaceBadChars;
		}

		public boolean isForceCreatePageXml() {
			return forceCreatePageXml;
		}

		public void setForceCreatePageXml(boolean forceCreatePageXml) {
			this.forceCreatePageXml = forceCreatePageXml;
		}

		public boolean isEnableSyncWithoutImages() {
			return enableSyncWithoutImages;
		}

		public void setEnableSyncWithoutImages(boolean enableSyncWithoutImages) {
			this.enableSyncWithoutImages = enableSyncWithoutImages;
		}
		public boolean isStripServerRelatedMetadata() {
			return stripServerRelatedMetadata;
		}
		public void setStripServerRelatedMetadata(boolean stripServerRelatedMetadata) {
			this.stripServerRelatedMetadata = stripServerRelatedMetadata;
		}
		public boolean isSyncTextWithExistingPageXml() {
			return syncTextWithExistingPageXml;
		}
		public void setSyncTextWithExistingPageXml(boolean syncTextWithExistingPageXml) {
			this.syncTextWithExistingPageXml = syncTextWithExistingPageXml;
		}
		public boolean isFindFilesIgnoringPrefix() {
			return findFilesIgnoringPrefix;
		}

		public void setFindFilesIgnoringPrefix(boolean findFilesIgnoringPrefix) {
			this.findFilesIgnoringPrefix = findFilesIgnoringPrefix;
		}

		public TreeMap<String, Dimension> getDimensionMap() {
			return dimensionMap;
		}
		
		public void setDimensionMap(TreeMap<String, Dimension> dimensionMap) {
			this.dimensionMap = dimensionMap;
		}
		
		public void setDimensionMapFromDoc(TrpDoc source) {
			TreeMap<String, Dimension> dims = new TreeMap<>(new NaturalOrderComparator ());
			
			for (TrpPage page : source.getPages()) {
				dims.put(FilenameUtils.getBaseName(page.getImgFileName()), 
						new Dimension(page.getWidth(), page.getHeight()));
			}
			
			this.dimensionMap = dims;
		}

		public boolean isSkipAbbyy() {
			return skipAbbyy;
		}

		public void setSkipAbbyy(boolean skipAbbyy) {
			this.skipAbbyy = skipAbbyy;
		}

		public boolean isSkipAlto() {
			return skipAlto;
		}

		public void setSkipAlto(boolean skipAlto) {
			this.skipAlto = skipAlto;
		}

		public boolean isSkipTxt() {
			return skipTxt;
		}

		public void setSkipTxt(boolean skipTxt) {
			this.skipTxt = skipTxt;
		}
		
	}
}
