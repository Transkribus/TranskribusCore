package eu.transkribus.core.io;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.formats.Page2010Converter;
import eu.transkribus.core.io.util.ImgFileFilter;
import eu.transkribus.core.io.util.ImgPriority;
import eu.transkribus.core.io.util.MdFileFilter;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.enums.EditStatus;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.builder.mets.TrpMetsBuilder;
import eu.transkribus.core.util.ImgUtils;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.PageXmlUtils;

/**
 * Reader class for loading a TRP Document from the local filesystem.<br/>
 * The given path should contain:<br/>
 * <ul>
 * <li>Image files of type JPG or TIFF (allowed types and priorities defined in
 * {@link ImgPriority})</li>
 * <li>Optional: PAGE XML files (corresponding images and XMLs must have the
 * same name)</li>
 * <li>Optional: XML file containing metadata. Filename has to match
 * "*metadata.xml".</li>
 * </ul>
 * Order of pages is implied by order of filenames. The metadata XML is
 * marshalled to a TrpDocMetadata Object and thus has to match the bean's
 * fieldnames.<br/>
 * <br/>
 * <b>TODO</b> add example here<br/>
 * <br/>
 * 
 * @author philip
 * 
 */
public class OldLocalDocReader {

	private final static Logger logger = LoggerFactory.getLogger(OldLocalDocReader.class);
	public static String URL_PROT_CONST = "file://";
	public final static String PAGE_FILE_SUB_FOLDER = "page";
	//TODO build alto/finereader to page xslt(?)
	//	public final static String OCR_FILE_SUB_FOLDER = "ocr";
	public final static String METS_FILE_NAME = "mets.xml";

	/**
	 * Loads a document from path.<br/>
	 * 
	 * Document metadata has to be in an XML ending in "metadata.xml".<br/>
	 * 
	 * Image files and corresponding PAGE XML files have to have the same name. <br/>
	 * Lexicographic order of names will imply order of pages.<br/>
	 * Testdoc is in $dea_scratch/TRP/TrpTestDoc <br/>
	 * <b>TODO</b> add support for different versions of transcripts on the
	 * path!? Or no versioning for local use?<br/>
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 * @throws WrongPageFormatVersionException
	 */
	public static TrpDoc load(final String path) throws IOException, UnsupportedFormatException {
		return load(path, false);
	}

	/**
	 * Loads a document from path.<br/>
	 * 
	 * Document metadata has to be in an XML ending in "metadata.xml".<br/>
	 * 
	 * Image files and corresponding PAGE XML files have to have the same name. <br/>
	 * Lexicographic order of names will imply order of pages.<br/>
	 * Testdoc is in $dea_scratch/TRP/TrpTestDoc <br/>
	 * <b>TODO</b> add support for different versions of transcripts on the
	 * path!? Or no versioning for local use?<br/>
	 * 
	 * @param path
	 * @param doConvert
	 *            if this is true, then files in old page format will be backed
	 *            up and migrated to version used in TRP
	 * @return
	 * @throws IOException
	 * @throws WrongPageFormatVersionException
	 */
	public static TrpDoc load(final String path, final boolean doConvert) throws IOException, UnsupportedFormatException {
		//check OS and adjust URL protocol
		final String os = System.getProperty("os.name");
		if (os.toLowerCase().contains("win")) {
			URL_PROT_CONST = "file:///";
		} //else: keep default

		File inputDir = new File(path);
		logger.info("inputDir = " + inputDir);

		//validate input path ======================================================

		if (!inputDir.canRead()) {
			logger.info("IS NOT READABLE");
			throw new IOException(inputDir.getAbsolutePath() + " is not readable.");
		}

		if (!inputDir.isDirectory()) {
			throw new IOException(inputDir.getAbsolutePath() + " is not a directory.");
		}

		logger.info("Reading Document at " + inputDir.getAbsolutePath());

		//find metadata file ========================================================
		TrpDocMetadata docMd;
		try {
			docMd = findDocMd(inputDir);
		} catch (IOException e) {
			logger.warn("No metadata is loaded. Reason: " + e.getMessage());
			docMd = new TrpDocMetadata();
			docMd.setTitle("Document at " + inputDir.getAbsolutePath());
			docMd.setDocId(-1);
		}
		docMd.setLocalFolder(inputDir);

		//create the document
		TrpDoc doc = new TrpDoc();
		//and set the docMd
		doc.setMd(docMd);

		// search for IMG files
		TreeMap<String, File> pageMap = findImgFiles(inputDir);
		logger.info("Found " + pageMap.entrySet().size() + " page images.");

		// iterate imgList, search for corresponding XML files and build TrpPages
		int pageNr = 1;
		List<TrpPage> pages = new LinkedList<TrpPage>();
		//Construct the input dir with pageXml Files. 
		//Its existence is unclear until the pageXml file is searched which is done anyway
		File xmlInputDir = new File(inputDir.getAbsolutePath() + File.separatorChar
				+ PAGE_FILE_SUB_FOLDER);
		if (!xmlInputDir.isDirectory()) {
			//fallback: try to search for XMLs in the img-directory 
			xmlInputDir = inputDir;
		}
		for (Entry<String, File> e : pageMap.entrySet()) {

			final String imgFileName = e.getKey();

			File pageXml = findPageXml(imgFileName, xmlInputDir);

			if (pageXml != null) {
				//determine pageFormat and migrate if necessary
				final String pageFormatVer = Page2010Converter.getFormatVersion(pageXml);
				final boolean isCurrentFormat = "2013-07-15".equals(pageFormatVer);
				if (!isCurrentFormat) {
					logger.info("File " + pageXml.getAbsolutePath()
							+ " is an old PAGE XML format version!");
					if (doConvert) {
						final String backupPath = xmlInputDir.getAbsolutePath()
								+ File.separatorChar + pageFormatVer + "_backup";
						final File backup = Files.move(
								pageXml.toPath(), 
								new File(backupPath + File.separator + pageXml.getName()).toPath()
								).toFile();
						
						pageXml = Page2010Converter.convert(backup, pageXml);
					} else {
						throw new UnsupportedFormatException("PAGE XML version \""
								+ pageFormatVer + "\" is not supported by TRP");
					}
				} else {
					logger.debug("File " + pageXml.getAbsolutePath()
							+ " has current PAGE XML format version.");
				}
			} else if (pageXml == null) {
				File pageDir = new File(inputDir.getAbsolutePath() + File.separatorChar
						+ PAGE_FILE_SUB_FOLDER);
				if (!pageDir.exists()) {
					pageDir.mkdir();
				}
				File fileOut = new File(pageDir.getAbsolutePath() + File.separatorChar
						+ imgFileName + ".xml");
				logger.debug("Creating empty PAGE XML: " + fileOut.getAbsolutePath());

				//get ImgDimensions
				File imgFile = e.getValue();
				
				Dimension dim = ImgUtils.readImageDimensions(imgFile);
				int xDim = dim.width;
				int yDim = dim.height;
				
//				try {
//					Map<String, String> imgMd = ExiftoolUtil.extractImgMd(imgFile.getAbsolutePath());
//					
//					final String xDimStr = imgMd.get(ExiftoolUtil.WIDTH_KEY);
//					final String yDimStr = imgMd.get(ExiftoolUtil.HEIGHT_KEY);
//					xDim = Integer.parseInt(xDimStr);
//					yDim = Integer.parseInt(yDimStr);
//					logger.debug("Resolution: " + xDim + "x" + yDim);
//					
//				} catch (TimeoutException | InterruptedException | IOException es) {
//					logger.error("Exiftool error!", es);
//					throw new IOException(es);
//				} catch (NumberFormatException nfe){
//					logger.error("Could not parse img resolution from exiftool output -> " + xDim + " x " + yDim, nfe);
//					throw new IOException(nfe);
//				}
				
				try {
					PcGtsType pc = PageXmlUtils.createEmptyPcGtsType(imgFileName, yDim, xDim);
					//marshall to File in correct subfolder
					pageXml = JaxbUtils.marshalToFile(pc, fileOut);
				} catch (JAXBException je) {
					throw new IOException(je);
				}
			}
			
			File thumbDir = new File(inputDir.getAbsolutePath() + File.separatorChar + LocalDocConst.THUMBS_FILE_SUB_FOLDER);
			File thumbFile = LocalDocReader.getThumbFile(thumbDir, imgFileName);
			
			TrpPage page = buildPage(pageNr++, e.getValue(), pageXml, thumbFile);
			pages.add(page);
		}

		doc.setPages(pages);
		doc.getMd().setNrOfPages(doc.getPages().size());

		logger.debug(doc.toString());
		return doc;
	}

	//	private static XmlFormat decideOnImportFormat(TreeMap<String, File> pageMap, File pageInputDir,
	//	File ocrInputDir, File altoInputDir) throws IOException {
	////take first entry and decide on strategy
	//Entry<String, File> imgNameAndFile = pageMap.firstEntry();
	//final String imgName = imgNameAndFile.getKey();
	//XmlFormat formatToUse = decideOnImportFormat(imgName, pageInputDir, ocrInputDir,
	//		altoInputDir);
	//logger.info("Reference format: " + formatToUse.toString());
	////check if this decision applies for all other files
	//boolean isConsistent = true;
	//for (Entry<String, File> e : pageMap.entrySet()) {
	//	XmlFormat currFormat = decideOnImportFormat(e.getKey(), pageInputDir, ocrInputDir,
	//			altoInputDir);
	//
	//	//if there are mixed page format versions
	//	boolean doPageFallback = 
	//			(currFormat.equals(XmlFormat.PAGE_2010) && formatToUse.equals(XmlFormat.PAGE_2013))
	//			|| (currFormat.equals(XmlFormat.PAGE_2013) && formatToUse.equals(XmlFormat.PAGE_2010));
	//	if (doPageFallback) {
	////		logger.info("Mixed Page format versions found. Falling back to " + XmlFormat.PAGE_2010.toString());
	//		//fallback to 2010 version and migrate old files later
	//		formatToUse = currFormat = XmlFormat.PAGE_2010;
	//	}
	//	//this check requires an XML for each image
	//	isConsistent &= formatToUse.equals(currFormat);
	//	//this one doesn't
	////	isConsistent &= currFormat == null || formatToUse.equals(currFormat);
	//}
	//
	//if (!isConsistent) {
	//	throw new IOException("There is not a valid Input XML of type '" + formatToUse
	//			+ "' for each image!");
	//}
	//
	////perform necessary steps to fill pageInputDir depending on the found format 
	//switch (formatToUse) {
	//case PAGE_2013:
	//	logger.info("Loading existing PAGE XML in current format.");
	//	break;
	//case PAGE_2010:
	//	logger.info("Migrating old PAGE XML to current format.");
	//	final String backupFolderName = XmlFormat.PAGE_2010.toString().toLowerCase()
	//			+ "_backup";
	//	final String backupPath = pageInputDir.getAbsolutePath() + File.separator
	//			+ backupFolderName;
	//	PageConverterUtils.updatePageFormat(pageInputDir, backupPath);
	//	break;
	//case ABBYY_10:
	//	logger.info("Transforming Finereader10/11 XMLs to PAGE XML.");
	//	logger.error("NOT IMPLEMENTED YET.");
	//	break;
	//case ALTO_2:
	//	logger.info("Transforming ALTO v2 XMLs to PAGE XML.");
	//	logger.error("NOT IMPLEMENTED YET.");
	//	break;
	//default:
	//	logger.info("Only existing PageXmls are used!");
	//	break;
	//}
	//
	//return formatToUse;
	//}
	//
	//private static XmlFormat decideOnImportFormat(final String imgName, File pageInputDir,
	//	File ocrInputDir, File altoInputDir) throws IOException {
	////try to find a pageXML
	//File pageXml = findXml(imgName, pageInputDir);
	//File ocrXml = findXml(imgName, ocrInputDir);
	//File altoXml = findXml(imgName, altoInputDir);
	//
	//XmlFormat format = null;
	//if (pageXml != null) { // favor page XML
	//	format = PageXmlUtils.getXmlFormat(pageXml);
	//	if (!format.equals(XmlFormat.PAGE_2010) && !format.equals(XmlFormat.PAGE_2013)) {
	//		throw new IOException("No Page XML could be found for img " + imgName + " in "
	//				+ pageInputDir.getAbsolutePath());
	//	}
	//} else if (ocrXml != null) {
	//	// use finereader as fallback 
	//	format = PageXmlUtils.getXmlFormat(ocrXml);
	//	if (!format.equals(XmlFormat.ABBYY_10)) {
	//		throw new IOException("No Abbyy XML could be found for img " + imgName + " in "
	//				+ pageInputDir.getAbsolutePath());
	//	}
	//} else if (altoXml != null) { // if nothing helps: alto
	//	format = PageXmlUtils.getXmlFormat(ocrXml);
	//	if (!format.equals(XmlFormat.ALTO_2)) {
	//		throw new IOException("No ALTO XML could be found for img " + imgName + " in "
	//				+ pageInputDir.getAbsolutePath());
	//	}
	//}
	//
	//if (format == null) {
	//	throw new IOException("No valid XML could be found for img " + imgName);
	//}
	//
	////if we are here, we have a supported XML format in the correct place and can go on
	//return format;
	//}

	
	/**
	 * Builds a TrpPage object with file URLs set
	 * 
	 * @param pageNr
	 *            of the page to be built
	 * @param img
	 *            the img file to include
	 * @param pageXml
	 *            the corresponding PAGE XML
	 * @return a TrpPage object with Transcript. The Transcript is null, if
	 *         pageXml is null.
	 * @throws MalformedURLException
	 */
	public static TrpPage buildPage(int pageNr, File img, File pageXml, File thumbFile) throws IOException {
		logger.debug(pageNr + ": XML = " + (pageXml == null ? "null" : pageXml.getName())
				+ " - IMG = " + img.getName());

		TrpPage page = new TrpPage();
		page.setPageNr(pageNr);
		page.setKey(null);
		page.setDocId(-1);
		final URL imgUrl = new URL(URL_PROT_CONST + img.getAbsolutePath());
		page.setUrl(imgUrl);
//		page.setThumbUrl(new File(LocalDocWriter.getThumbFileName(page)).toURI().toURL());
		page.setThumbUrl(thumbFile.toURI().toURL());
		if (pageXml != null) {
			TrpTranscriptMetadata tmd = new TrpTranscriptMetadata();
			tmd.setPageReferenceForLocalDocs(page);
			final URL xmlUrl = new URL(URL_PROT_CONST + pageXml.getAbsolutePath());
			tmd.setKey(null);
			tmd.setUrl(xmlUrl);
			tmd.setStatus(EditStatus.NEW);
			tmd.setPageId(page.getPageId());
//			tmd.setDocId(-1);
			tmd.setTimestamp(new Date().getTime());
			tmd.setUserName("LocalDocReader");
			//TODO real status, time and user parsed from PageXML?
			page.getTranscripts().add(tmd);
		}

		return page;
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
	private static File findPageXml(String imgName, File xmlInputDir) {
		//assume XML file
		File pageXml = new File(xmlInputDir.getAbsolutePath() + File.separatorChar + imgName
				+ ".xml");

		if (pageXml.exists() && pageXml.canRead()) {
			logger.debug("Found page XML for page " + imgName + " -> " + pageXml.getName());
			return pageXml;
		} else {
			logger.debug("Found NO page XML for page " + imgName + "!");
			return null;
		}
	}

	/**
	 * Finds image files and builds a list with distinct pages (based on
	 * filenames).<br/>
	 * If several image files in different formats are found for one page, the
	 * one with the highest priority (according to {@link ImgPriority}) is
	 * returned in the list.
	 * 
	 * @param inputDir
	 *            that is searched for image files
	 * @return a sorted Map with page image files
	 */
	private static TreeMap<String, File> findImgFiles(File inputDir) {
		final File[] imgArr = inputDir.listFiles(new ImgFileFilter());

		TreeMap<String, File> pageMap = new TreeMap<>();

		for (File img : imgArr) {
			final String pageName = FilenameUtils.getBaseName(img.getName());
			logger.debug("pageName = " + pageName);
			if (!pageMap.containsKey(pageName)) {
				//new page. add this img in any case
				pageMap.put(pageName, img);
				logger.debug(pageName + ": adding new page image to list " + img.getName() + ".");
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
	private static TrpDocMetadata findDocMd(File inputDir) throws IOException {
		final File[] mdFiles = inputDir.listFiles(new MdFileFilter());

		TrpDocMetadata docMd = null;
		if (mdFiles == null || mdFiles.length == 0) {
			//no file => no metadata
			throw new FileNotFoundException("No metadata XML was found on path: "
					+ inputDir.getAbsolutePath());

			// unambiguous metadata filename. ignore mdFiles.length
			//		} else if (mdFiles != null && mdFiles.length > 1) {
			//			//obviously there are two or more of them...
			//			//build pretty String with comma separated names of conflicting files
			//			String conflictingFiles = "";
			//			boolean isFirst = true;
			//			for (int i = 0; i < mdFiles.length; i++) {
			//				if (isFirst) {
			//					conflictingFiles += mdFiles[i].getName();
			//					isFirst = false;
			//				} else {
			//					conflictingFiles += ", " + mdFiles[i].getName();
			//				}
			//			}
			//			throw new IOException("Input dir " + inputDir.getAbsolutePath()
			//					+ " contains two or more metadata XMLs");
		} else {
			final File mdFile = mdFiles[0];
			logger.info("Found md File " + mdFile.getAbsolutePath());
			try {//TODO to JaxbUtils
				docMd = JaxbUtils.unmarshal(mdFile, TrpDocMetadata.class);
				//set ID to -1 in order to create confusion
				docMd.setDocId(-1);
			} catch (JAXBException je) {
				//this crappy, useless shit file will be ignored
				throw new IOException("The md File " + mdFile.getName()
						+ " did not obey the correct format. "
						+ "A doc without metadata will be provided.");
			}
		}
		return docMd;
	}

	public static TrpDoc load(File metsFile) throws IOException {

		final File parentDir = new File(metsFile.getParent());

		Mets mets;
		try {
			mets = JaxbUtils.unmarshal(metsFile, Mets.class, TrpDocMetadata.class);
		} catch (JAXBException e) {
			throw new IOException("Could not unmarshal METS file!", e);
		}

		if (!mets.getTYPE().equals(TrpMetsBuilder.TRP_METS_PROFILE)) {
			throw new IOException("Unsupported METS Type: " + mets.getTYPE());
		}

		TrpDoc doc = new TrpDoc();
		//unmarshal TrpDocMetadata
		TrpDocMetadata md = TrpMetsBuilder.getTrpDocMd(mets);

		//collect files
		List<TrpPage> pages = TrpMetsBuilder.getTrpPages(mets, parentDir);

		//TODO check on files if localDoc!
		md.setLocalFolder(parentDir);
		doc.setMd(md);
		doc.setPages(pages);

		return doc;
	}
}
