package eu.transkribus.core.io;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.dea.fimagestore.core.util.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.transkribus.core.exceptions.CorruptImageException;
import eu.transkribus.core.io.formats.XmlFormat;
import eu.transkribus.core.io.util.ImgFilenameFilter;
import eu.transkribus.core.io.util.ImgPriority;
import eu.transkribus.core.misc.APassthroughObservable;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.mets.DivType;
import eu.transkribus.core.model.beans.mets.DivType.Fptr;
import eu.transkribus.core.model.beans.mets.FileGrpType;
import eu.transkribus.core.model.beans.mets.FileType;
import eu.transkribus.core.model.beans.mets.FileType.FLocat;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.beans.mets.MetsType.FileSec.FileGrp;
import eu.transkribus.core.model.beans.mets.StructMapType;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.util.DeaFileUtils;
import eu.transkribus.core.util.ImgUtils;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.XmlUtils;
import eu.transkribus.interfaces.util.URLUtils;

/**
 * Reader class for loading a TRP Document from the local filesystem.<br>
 * The files (Images and optionally XML transcripts) get first fetched from URLs (they are read from Mets as well as the metadata)<br>
 * The given path/mets should contain:<br>
 * <ul>
 * <li>Image files of type JPG or TIFF (allowed types and priorities defined in
 * {@link ImgPriority})</li>
 * <li>Optional: Alto or Abbyy XML files (corresponding images and XMLs must have the
 * same name), Will be later on converted to Page Xml</li>
 * <li>Metadata is given in the Mods section from the Mets</li>
 * </ul>
 * Order of pages is implied by order of filenames. The metadata XML is
 * marshalled to a TrpDocMetadata Object and thus has to match the bean's
 * fieldnames.<br>
 * <br/>
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
 * @author giorgio
 * 
 */
public class GoobiMetsImporter extends APassthroughObservable
{

	private final static Logger logger = LoggerFactory.getLogger(GoobiMetsImporter.class);
	private final ImgFilenameFilter imgFilter;
	
	public GoobiMetsImporter() {
		imgFilter = new ImgFilenameFilter();
	}
	
	/**
	 * Read the Mets created by Goobi, fetches all files with help of the contained URLs
	 * into an temporarily folder and creates a TrpDoc out of it
	 * 
	 * @param metsPath: path to the Goobi mets file
	 * @return
	 * @throws IOException
	 * @throws SAXException 
	 * @throws JAXBException 
	 */
	public TrpDoc unmarshalMetsAndLoadDocFromGoobiMets(File metsFile) throws IOException, JAXBException, SAXException {
		
		String localDirPath = metsFile.getParent();
	
		return loadDocFromGoobiMets(metsFile, localDirPath);

	}
	
	/**
	 * Reads the Mets metadata and fetches all files with help of the contained URLs
	 * into an temporarily folder and creates a TrpDoc out of it
	 * 
	 * @param metsPath: path to the Goobi mets file
	 * @return
	 * @throws IOException
	 * @throws SAXException 
	 * @throws JAXBException 
	 */
	public TrpDoc loadDocFromGoobiMets(File metsFile, String localDirPath) throws IOException, JAXBException, SAXException {
		
		TrpDocMetadata md;
		
		Mets mets = JaxbUtils.unmarshal(metsFile, Mets.class, TrpDocMetadata.class);
		
		String metsPath = metsFile.getAbsolutePath();

		updateStatus("Reading metadata...");
		//unmarshal TrpDocMetadata
		md = readModsMetadata(XmlUtils.getDocumentFromFileWOE(metsPath));
		
		//String localDir = System.getProperty("user.home") + File.separator + "GoobiTest" + File.separator + md.getTitle() + File.separator; 
		
		logger.debug("the local user home dir = " + localDirPath);
		//System.in.read();
			
		//collect files into "user.home" + "/GoobiTest/" + mods title
		//fetchFiles(localDirPath, mets);

		md.setLocalFolder(new File(localDirPath));

		/*
		 * next line can disorder the ORDER of the pages of the Mets when filename length is not equal and we store 
		 * the files temporary in a local folder instead of importing directly as we did now
		 */
		//final TrpDoc doc = LocalDocReader.load(localDirPath, true);
		
		//overwrite metadata with the metadata read from the MODS section in the METS file
		
		final TrpDoc doc = new TrpDoc();
		doc.setMd(md);
		doc.setPages(fetchFiles(localDirPath, mets));
		return doc;

	}
	
	private Mets unmarshalMets(File metsFile, boolean validate) throws IOException, JAXBException, SAXException {
		Mets mets;
//		try {
			Unmarshaller u = JaxbUtils.createUnmarshaller(Mets.class, TrpDocMetadata.class);
			
			long t = System.currentTimeMillis();
			if (validate) {
				Schema schema = XmlFormat.METS.getOrCompileSchema();
				u.setSchema(schema);
			}
			Object o = u.unmarshal(metsFile);
			mets = (Mets) o;
			logger.debug("time for unmarshalling: "+(System.currentTimeMillis()-t)+", validated: "+validate);
//			mets = JaxbUtils.unmarshal(metsFile, Mets.class, nestedClassed);
//			mets = JaxbUtils.unmarshal2(new FileInputStream(metsFile), Mets.class, true, false);
//		} catch (Exception e) {
//			throw new IOException("Could not unmarshal METS file!", e);
//		}
		logger.debug("unmarshalled mets file");
		
		return mets;
	}
	
	/**
	 * 
	 * @param mets: The unmarshalled Goobi Mets file 
	 * @return
	 * @throws IOException
	 */
	public List<TrpPage> fetchFiles(String dir, Mets mets) throws IOException {

		List<FileGrp> fileGrps = mets.getFileSec().getFileGrp();
		List<FileType> xmlGrp = null;
		List<FileType> imgGrp = null;
		List<FileType> defaultImgGrp = null;
		List<FileType> altXmlGrp = null;
		for (FileGrpType type : fileGrps) {
			switch (type.getUSE()) {
			case "MAX":
				imgGrp = type.getFile();
				break;
				/*
				 * could also be that USE='Content' and ID="AltoFiles" or ID="AbbyyXmlFiles"  is necessary to get the transcriptions
				 */
			case "DEFAULT":
				defaultImgGrp = type.getFile();
				break;
			case "XML":
				//possibility to load also an existent Alto or Abbyy XML and convert it to Page later on
				//TODO: Abklären
				xmlGrp = type.getFile();
				break;
			case "FULLTEXT":
				//e.g. visual library uses 'FULLTEXT' 
				altXmlGrp = type.getFile();
				break;
			default:
				break;
			}
		}
	
		//take default images if no MAX images are available
		if (imgGrp == null && defaultImgGrp != null){
			imgGrp = defaultImgGrp;
		}
		
		//take alternative if availabe
		if (xmlGrp == null && altXmlGrp != null){
			xmlGrp = altXmlGrp;
		}
		
		if (imgGrp == null)
			throw new IOException("METS file has no image file list!");
		if (xmlGrp == null){
			logger.debug("no xml file list");
			//throw new IOException("METS file has no xml file list!");
		}
		
		List<DivType> pageDivs = null;
		for(StructMapType sMap : mets.getStructMap()){
			if(sMap.getTYPE().equals("PHYSICAL") 
					//&& sMap.getDiv().getID().equals("PHYS_0000")){
					&& sMap.getDiv().getTYPE().equals("physSequence")){
				pageDivs = sMap.getDiv().getDiv();
				break;
			}
		}
		if(pageDivs == null)
			throw new IOException("No valid StructMap was found!");
		
		List<TrpPage> pages = new ArrayList<TrpPage>(pageDivs.size());
		
		// Implement a reverse-order Comparator by lambda function
		Comparator<DivType> comp = (DivType a, DivType b) -> {
		    return a.getORDER().compareTo(b.getORDER());
		};

		pageDivs.sort(comp);

		for(DivType div : pageDivs){
			//fetch all files and store them locally
			TrpPage p = fetchFilesFromUrl(div, imgGrp, xmlGrp, dir);
			pages.add(p);
		}
		return pages;
	}
		
	
	private TrpPage fetchFilesFromUrl(DivType div, List<FileType> imgGrp, List<FileType> xmlGrp, String dir) throws IOException {
		final int pageNr = div.getORDER().intValue();
		updateStatus("Downloading file for page nr. " + pageNr);
		File imgFile = null;
		File abbyyFile = null;
		File altoFile = null;
		
		//String imgDirPath = dir + File.separator + "img";
		String imgDirPath = dir;
		String abbyyDirPath = dir + File.separator + LocalDocConst.OCR_FILE_SUB_FOLDER;
		String altoDirPath = dir + File.separator + LocalDocConst.ALTO_FILE_SUB_FOLDER;
		String pageDirPath = dir + File.separator + LocalDocConst.PAGE_FILE_SUB_FOLDER;
		
		File pageDirFile = new File(pageDirPath);
		if(!pageDirFile.isDirectory() && !pageDirFile.mkdir()) {
			throw new IOException("Could not create page dir at: " + pageDirPath);
		}
		
		/**
		 * handle cases where no image can be retrieved/stored for this page:
		 * -image URL is broken
		 * -the image dimension can not be read from the downloaded file
		 * -no image file is mapped in the structmap for this page
		 * 
		 * problemMsg is used to store info on that.
		 */
		String problemMsg = null;
		
		for(Fptr ptr : div.getFptr()){
			FileType type = (FileType) ptr.getFILEID();
			FLocat fLocat = type.getFLocat().get(0);
			
			//FIXME at the moment only remote files are supported here!
			final String locType = fLocat.getLOCTYPE();
			if(!"URL".equals(locType)){
				throw new IOException("Bad or no LOCTYPE in an FLocat element: " + locType);
			}
			
			final String mimetype = type.getMIMETYPE();//MIMETYPE="image/jpeg"
			logger.debug("mimetype " + mimetype);
			final URL url = new URL(fLocat.getHref());
			String ext = MimeTypes.lookupExtension(mimetype);
			
			//MimeTypes does not contain text/xml
			if (ext==null){
				ext = mimetype.substring(mimetype.indexOf("/")+1);
			}
			
			logger.debug("ext2 " + ext);
			
			/*
			 * brought problems with file/img links without the filname + ext at the end of the URL 
			 */
			//final String filename = determineFilename(url, type.getID(), mimetype);
			
			/*
			 * Preferred filename is the name in the getHeaderField("Content-Disposition");
			 * as fallback we use the fileID and mimetype extension
			 * 
			 */
			String filename = type.getID() + "." + ext;
			logger.debug("url.getProtocol() " + url.getProtocol());
			if (url.getProtocol().startsWith("http")){
				String tmpFn = URLUtils.getFilenameFromHeaderField(url);
				//logger.debug("tmpFn " + tmpFn);
				if (tmpFn != null){
					filename = tmpFn;
				}
			}
			
			//logger.debug("mimetype " + mimetype);
			logger.debug("imported filename " + filename);
										
			if(imgGrp.contains(type)){
				imgFile = new File(imgDirPath + File.separator + filename);
				logger.debug("Downloading: " + url);
				//fetch file from this URL and store locally
				int imgDownloadStatus = DeaFileUtils.copyUrlToFile(url, imgFile);
				if(imgDownloadStatus >= 400) {
					//the image URL connection attempt returns a response with code > 400
					problemMsg = getBrokenUrlMsg(url, imgDownloadStatus);
				}
			}
			
			if(xmlGrp != null && xmlGrp.contains(type)) {
				//check for ALTO or Abbyy XML
				String xmlId = type.getID();
				//FIXME check on ID string might not be reliable
				if(xmlId.contains("AbbyyXml")){
					logger.debug("Found potential Abbyy XML: " + type.getID());
					//TODO: implement
					abbyyFile = new File(abbyyDirPath + File.separator + filename);
					if(DeaFileUtils.copyUrlToFile(url, abbyyFile) >= 400) {
						logger.error("Could not download Abbyy XML and it will be ignored!");
						//don't fail if abbyy XML could not be retrieved
						abbyyFile = null;
					}
				} else if (xmlId.contains("Alto") || xmlId.contains("ALTO")){
					logger.debug("Found potential ALTO XML: " + type.getID());
					//TODO: implement
					altoFile = new File(altoDirPath + File.separator + filename);
					if(DeaFileUtils.copyUrlToFile(url, altoFile) >= 400) {
						logger.error("Could not download ALTO XML and it will be ignored!");
						//don't fail if ALTO XML could not be retrieved
						altoFile = null;
					}
				}
			}
		}
		
		File pageXml = null;
		File thumb = null;
		File imgDir = new File(imgDirPath);
		Dimension dim = null;
		if(imgFile == null) {
			//the divType did not include an image pointer
			logger.error("No image mapped for page " + pageNr + " in the structmap!");
			problemMsg = getMissingImgMsg(pageNr);
		} else {
			logger.info("Page " + pageNr + " image: " + imgFile.getAbsolutePath());

			if(imgFile.isFile()) {
				try {
					dim = ImgUtils.readImageDimensions(imgFile);
				} catch (CorruptImageException cie) {
					logger.error("Image is corrupted!", cie);
					//the image dimension can not be read from the downloaded file
					problemMsg = LocalDocReader.getCorruptImgMsg(imgFile.getName());
				}
			}
			
			File pageOutFile = new File(pageDirPath + File.separatorChar + FilenameUtils.getBaseName(imgFile.getName()) + ".xml");
			pageXml = LocalDocReader.createPageXml(pageOutFile, true, abbyyFile, altoFile, 
					null, true, true, false, imgFile.getName(), dim);
			thumb = LocalDocReader.getThumbFile(imgDir, imgFile.getName());
		}
		TrpPage page = LocalDocReader.buildPage(new File(dir), pageNr, imgFile, pageXml, thumb, dim,
				problemMsg);
		
//		//try to create TrpPage at this time instead of LocalDocReader
//		TrpPage page = new TrpPage();
//		page.setPageNr(pageNr);
//		page.setUrl(imgFile.toURI().toURL());
//		page.setKey(null);
//		page.setDocId(-1);
//		page.setImgFileName(imgFile.getName());
//
//		if(pageXml == null) {
//			logger.error("No master xml mapped for page " + pageNr + " in the structmap!");
//		} else {
//			logger.info("Page " + page.getPageNr() + " xml: " + pageXml.getAbsolutePath());
//		}
//		TrpTranscriptMetadata tmd = new TrpTranscriptMetadata();
//		tmd.setPageReferenceForLocalDocs(page);
//		tmd.setPageId(page.getPageId());
//		tmd.setUrl(pageXml.toURI().toURL());
//		tmd.setKey(null);
//		tmd.setStatus(EditStatus.NEW);
//		tmd.setTimestamp(new Date().getTime());
//		tmd.setUserName("GoobiMetsImporter");
//		page.getTranscripts().add(tmd);
		return page;
	}

	private String determineFilename(URL url, String xmlId, String mimetype) throws IOException {
		String ext = MimeTypes.lookupExtension(mimetype);
		String filename = new File(url.getPath()).getName();
		String extFromPath = FilenameUtils.getExtension(filename);
		logger.debug("Extension according to mimetype: " + ext);
		logger.debug("Filename from URL: " + filename);
		logger.debug("Extension from filename: " + extFromPath);
		//check if extracted filename from path is empty
		if(StringUtils.isEmpty(filename)) {
			filename = xmlId + "." + ext;
			logger.debug("Inconsistency -> new filename: " + filename);
		} else {
			//if no extension is included in URL path
			if(StringUtils.isEmpty(extFromPath)) {
				filename += "." + ext;
			}
			logger.debug("Using orig filename: " + filename);
		}
		if(!imgFilter.accept(null, filename)) {
			throw new IOException("Unsupported image type in METS: " + mimetype);
		}
		return filename;
	}

	/**
	 *  create a page file from the given Alto file
	 * 	
	 * @param imgFile
	 * @param altoXml
	 * @param pageOutFile
	 * @param preserveOcrTxtStyles
	 * @param preserveOcrFontFamily
	 * @param replaceBadChars
	 * @return
	 * @throws IOException
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws JAXBException
	 */
	public File createPageFromAlto2(File imgFile, File altoXml, File pageOutFile, boolean preserveOcrTxtStyles, boolean preserveOcrFontFamily,
			boolean replaceBadChars) throws IOException, TransformerException, SAXException, ParserConfigurationException, JAXBException {
		XmlFormat xmlFormat = XmlUtils.getXmlFormat(altoXml);
		if (xmlFormat.equals(XmlFormat.ALTO_2)) {
			logger.info(altoXml.getAbsolutePath() + ": Transforming ALTO v2 XMLs to PAGE XML.");
			PcGtsType pc = PageXmlUtils.createPcGtsTypeFromAlto(altoXml, imgFile.getName(), preserveOcrTxtStyles, preserveOcrFontFamily, replaceBadChars);
			return JaxbUtils.marshalToFile(pc, pageOutFile);
		}
		throw new IOException("Could not determine xml file as valid alto2: " + altoXml.getAbsolutePath());
	}
	

	
	/**
	 * Read the metadata from the Mods section into the corresponding TrpDocMetadata attributes
	 * TODO: not all attributes are filled till now#
	 * mising are: genre, writer, language, script type, description
	 * 
	 * @param mets
	 * @return
	 */
	public TrpDocMetadata readModsMetadata(Document mets) {
		TrpDocMetadata result = new TrpDocMetadata();
			
		//Element modsSection = (Element) XmlUtils.selectNode(mets.getDocumentElement(), "(*[contains(@ID,'DMDLOG_0000')])[1]");
		Element modsSection = (Element) XmlUtils.selectNode(mets.getDocumentElement(), "//*[local-name()='mods']");
//		if (modsSection == null){
//			modsSection = (Element) XmlUtils.selectNode(mets.getDocumentElement(), "mods:mods");
//		}
						
		if (modsSection!=null)
		{
			NodeList actFields = modsSection.getElementsByTagName("mods:title");
			if (actFields.getLength() == 0){
				actFields = modsSection.getElementsByTagName("title");
			}
			logger.debug("actFields for title: " + actFields.getLength());
			for (int i = 0; i < actFields.getLength(); i++) {
				
				Element act = (Element)actFields.item(i);
				String typeAttribute = ((Element)act.getParentNode()).getAttribute("type");
				
				String actValue = (actFields.item(i)).getTextContent();
				logger.debug("title element found: " + actValue);
				if (typeAttribute==null || typeAttribute.equals("")){
					//logger.debug("set actValue: " + actValue);
					result.setTitle(actValue);
					break;
				}
				
				//would be used to add title with special type, e.g. an uniform title
//				if (typeAttribute!=null && typeAttribute.equals("uniform"))
//					result.setTitle(actValue);
			}
//			actFields = modsSection.getElementsByTagName("mods:genre");
//			for (int i = 0; i < actFields.getLength(); i++) {
//			}
			
	
			actFields = modsSection.getElementsByTagName("mods:dateCreated");
			if (actFields.getLength() == 0){
				actFields = modsSection.getElementsByTagName("dateCreated");
			}

			for (int i = 0; i < actFields.getLength(); i++) {
				Element act = (Element)actFields.item(i);
				String keyAttribute = act.getAttribute("keyDate");
				if (keyAttribute.equals("yes")){
					String dateString = actFields.item(i).getTextContent();
					logger.debug("found date string: " + dateString);
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
					DateFormat formatAlt = new SimpleDateFormat("yyyy", Locale.GERMAN);
					Date date;
					try {
						date = format.parse(dateString);
						result.setCreatedFromDate(date);
						result.setCreatedToDate(date);
					} catch (ParseException e) {
							try {
								date = formatAlt.parse(dateString);
								result.setCreatedFromDate(date);
								result.setCreatedToDate(date);
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
					}
				}
			}
			
			if (result.getCreatedFromDate()== null && result.getCreatedToDate()==null){
				actFields = modsSection.getElementsByTagName("mods:dateIssued");
				if (actFields.getLength() == 0){
					actFields = modsSection.getElementsByTagName("dateIssued");
				}
				
				for (int i = 0; i < actFields.getLength(); i++) {
					String dateString = actFields.item(i).getTextContent();
					DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
					DateFormat formatAlt = new SimpleDateFormat("yyyy", Locale.GERMAN);
					Date date;
					try {
						date = format.parse(dateString);
						result.setCreatedFromDate(date);
						result.setCreatedToDate(date);
					} catch (ParseException e) {
							try {
								date = formatAlt.parse(dateString);
								result.setCreatedFromDate(date);
								result.setCreatedToDate(date);
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
					}

				}
			
			}
			
			logger.debug("date element found: " + result.getCreatedFromDate());
//			actFields = modsSection.getElementsByTagName("mods:languageTerm");
//			for (int i = 0; i < actFields.getLength(); i++) {
//				result.addLanguage(actFields.item(i).getTextContent());
//			}
			
			actFields = modsSection.getElementsByTagName("mods:name");
			if (actFields.getLength() == 0){
				actFields = modsSection.getElementsByTagName("name");
			}

			for (int i = 0; i < actFields.getLength(); i++) {
				Element act = (Element)actFields.item(i);
				String typeAttribute = act.getAttribute("type");
				if (typeAttribute!=null && typeAttribute.equals("personal")){
					
					String role = XmlUtils.getFirstSubElementFromElement(act, "mods:roleTerm");
					if (role == null){
						role = XmlUtils.getFirstSubElementFromElement(act, "roleTerm");
					}
					//author
					if (role!=null && role.equals("aut")){
						String author = XmlUtils.getFirstSubElementFromElement(act, "mods:displayForm");
						if (author == null){
							author = XmlUtils.getFirstSubElementFromElement(act, "displayForm");
						}
						logger.debug("Author found is " + author);
						if (author == null || author.equals("")){
							NodeList nl = act.getElementsByTagName("mods:namePart");
							if (nl.getLength() == 0){
								nl = act.getElementsByTagName("namePart");
							}
							for (int j = 0; j < nl.getLength(); j++){
								logger.debug("NodeList length " + nl.getLength());
								Element value = (Element) nl.item(j);
						        String valueType = value.getAttribute("type");
						        
						        logger.debug("valueType " + valueType);
						        
						        if (valueType.equals("family")){
						        	if (author == null)
						        		author = value.getTextContent();
						        	else
						        		author = value.getTextContent() + " " + author;
						        }
						        else if (valueType.equals("given")){
						        	if (author != null)
						        		author = author.concat(" " + value.getTextContent());
						        	else
						        		author = value.getTextContent();
						        }
						        else if (valueType.equals("date")){
						        	//do nothing
						        }
						        else{
						        	if (author == null)
						        		author = value.getTextContent();
						        	else
						        		author = author.concat(" " + value.getTextContent());
						        }
						        logger.debug("Author found is " + author);
								
								
							}

						}
						result.setAuthor(author);
						
					}  
				}
			}
			

			
			/*
			 * extract external ID
			 * 
			 * https://github.com/Transkribus/TranskribusCore/issues/16
			 * 
			 * TODO add possible type attribute values here
			 */
			actFields = modsSection.getElementsByTagName("mods:identifier");
			if (actFields.getLength() == 0){
				actFields = modsSection.getElementsByTagName("identifier");
			}
			
			for (int i = 0; i < actFields.getLength(); i++) {
				Element act = (Element)actFields.item(i);
				String typeAttribute = act.getAttribute("type");
				//NAF uses type="CatalogueIdentifier"
				if (typeAttribute!=null && (typeAttribute.equals("CatalogueIdentifier") || typeAttribute.equals("urn"))){
					final String extId = act.getTextContent();
					result.setExternalId(extId);
					logger.debug("ext id found: " + result.getExternalId());
				}
			}
		
		}
		else{
			logger.debug("mods section is null");
			result.setTitle("unknownTitle");
			result.setAuthor("unknownAuthor");
		}
		
		try {
			System.in.read();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return result;

	}

	public static URL extractMetsUrlFromDfgViewerUrl(URL dfgViewerUrl) throws MalformedURLException {
		String extractedUrlStr = null;
		URL extractedUrl = null;
		try {
			logger.info("Extracting METS URL from DFG-Viewer URL");
			//set[mets]=https%3A%2F%2Farchive.thulb.uni-jena.de%2Fufb%2Fservlets%2FMCRMETSServlet%2Fufb_derivate_00003259%3FXSL.Style%3Ddfg&set[image]=2
			String query = dfgViewerUrl.getQuery();
			final String metsParamName = "set[mets]";
			String[] queryParams = query.split("&");
			for(String s : queryParams) {
				String[] keyVal = s.split("=");
				if(keyVal.length < 2) {
					continue;
				}
				if(keyVal[0].equals(metsParamName)) {
					extractedUrlStr = URLDecoder.decode(keyVal[1], "UTF-8");
					logger.debug("Extracted URL = " + extractedUrlStr);
				}
			}
			if(extractedUrlStr == null) {
				throw new Exception("Could not find METS URL in: " +  dfgViewerUrl);
			}
			extractedUrl = new URL(extractedUrlStr);
		} catch(MalformedURLException mue) {
			logger.error("Extracted METS URL is not valid: " + extractedUrlStr);
			throw mue;
		} catch(Throwable t) {
			logger.error("Could not parse input URL: " + dfgViewerUrl, t);
			throw new MalformedURLException("There seems to be no METS URL included: " + dfgViewerUrl);
		}
		return extractedUrl;
	}
	
	public static String getMissingImgMsg(final int pageNr) {
		return "No image file was mapped to page nr. " + pageNr + " in METS XML";
	}
	
	public static String getBrokenUrlMsg(final URL url, final Integer statusCode) {
		String msg = "Image could not be loaded from " + url.toString();
		if(statusCode != null) {
			msg += "(" + statusCode + " " + Status.fromStatusCode(statusCode).getReasonPhrase() + ")";
		}
		return msg;
	}
}
