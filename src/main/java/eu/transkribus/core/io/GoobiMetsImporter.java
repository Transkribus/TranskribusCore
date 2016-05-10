package eu.transkribus.core.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.transkribus.core.io.formats.XmlFormat;
import eu.transkribus.core.io.util.ImgPriority;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.mets.DivType;
import eu.transkribus.core.model.beans.mets.FileGrpType;
import eu.transkribus.core.model.beans.mets.FileType;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.beans.mets.StructMapType;
import eu.transkribus.core.model.beans.mets.DivType.Fptr;
import eu.transkribus.core.model.beans.mets.FileType.FLocat;
import eu.transkribus.core.model.beans.mets.MetsType.FileSec.FileGrp;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.XmlUtils;

/**
 * Reader class for loading a TRP Document from the local filesystem.<br>
 * The files (Images and optionally XML transcipts) get first fetched from URLs (they are read from Mets as well as the metadata)<br>
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
public class GoobiMetsImporter
{

	private final static Logger logger = LoggerFactory.getLogger(GoobiMetsImporter.class);
	
	
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
	public static TrpDoc unmarshalMetsAndLoadDocFromGoobiMets(String localDirPath) throws IOException, JAXBException, SAXException {
		
		String metsPath = localDirPath + File.separator + "mets.xml";
		
		Mets mets = unmarshalMets(new File(metsPath), true);
		
		return loadDocFromGoobiMets(mets, localDirPath);

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
	public static TrpDoc loadDocFromGoobiMets(Mets mets, String localDirPath) throws IOException, JAXBException, SAXException {
		
		TrpDocMetadata md;
		
		String metsPath = localDirPath + File.separator + "mets.xml";

		//unmarshal TrpDocMetadata
		md = readModsMetadata(XmlUtils.getDocumentFromFileWOE(metsPath));
		
		//String localDir = System.getProperty("user.home") + File.separator + "GoobiTest" + File.separator + md.getTitle() + File.separator; 
		
		logger.debug("the local user home dir = " + localDirPath);
		//System.in.read();
			
		//collect files into "user.home" + "/GoobiTest/" + mods title
		fetchFiles(localDirPath, mets);

		md.setLocalFolder(null);

		final TrpDoc doc = LocalDocReader.load(localDirPath, true);
		
		//overwrite metadata with the metadata read from the MODS section in the METS file
		doc.setMd(md);
		
		return doc;
	}
	
	static Mets unmarshalMets(File metsFile, boolean validate) throws IOException, JAXBException, SAXException {
		Mets mets;
//		try {
			Unmarshaller u = JaxbUtils.createUnmarshaller(Mets.class);
			
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
	public static boolean fetchFiles(String dir, Mets mets) throws IOException {

		List<FileGrp> fileGrps = mets.getFileSec().getFileGrp();
		List<FileType> xmlGrp = null;
		List<FileType> imgGrp = null;
		for (FileGrpType type : fileGrps) {
			switch (type.getUSE()) {
			//unclear if img in MAX are the wright one, abklären ob noch bessere Bilder verlinkt werden können
			case "MAX":
				imgGrp = type.getFile();
				break;
				/*
				 * could also be that USE='Content' and ID="AltoFiles" or ID="AbbyyXmlFiles"  is necessara to get the transcriptions
				 */
			case "DEFAULT":
				imgGrp = type.getFile();
				break;
			case "XML":
				//possibility to load also an existent Alto or Abbyy XML and convert it to Page later on
				//TODO: Abklären
				xmlGrp = type.getFile();
				break;
			default:
				break;
			}
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
		
		for(DivType div : pageDivs){
			//fetch all files and store them locally
			
			fetchFilesFromUrl(div, imgGrp, xmlGrp, dir);
			//pages.add(page);
		}
		return true;
	}
	
	
	private static void fetchFilesFromUrl(DivType div, List<FileType> imgGrp, List<FileType> xmlGrp, String dir) throws IOException {
		TrpPage page = new TrpPage();
		int nr = div.getORDER().intValue();
		page.setPageNr(nr);
		
		File imgFile = null;
		File xmlFile = null;
		
		String imgDir = dir + File.separator + "img";
		String xmlDir = dir + File.separator + "ocr";
		String altoDir = dir + File.separator + "alto";
		
		//FIXME this will only work for local files
		for(Fptr ptr : div.getFptr()){
			FileType type = (FileType) ptr.getFILEID();
			//logger.debug("tmp.getID().contains MAX " + tmp.getID().contains("MAX"));
			if(type.getID().contains("MAX") || type.getID().contains("DEFAULT")){
								
				if(imgGrp.contains(type)){ 
					FLocat fLocat = type.getFLocat().get(0);
					if(fLocat.getLOCTYPE() != null && fLocat.getLOCTYPE().equals("URL")){
						String href = fLocat.getHref();				
						
						String filename = type.getID();
						String fileEnding = href.substring(href.lastIndexOf("."));
						
						imgFile = new File(imgDir + File.separator + filename + fileEnding);
						
						//fetch file from this URL and store locally
						FileUtils.copyURLToFile(new URL(href), imgFile);
					
						logger.debug("file loaded from URL: " + href);
						//System.in.read();
					}
				}
			}
			else if (type.getID().contains("AbbyyXml")){
				logger.debug("fptr id equals: " + type.getID());
				//TODO: implement
				if (xmlGrp.contains(type)){
					FLocat fLocat = type.getFLocat().get(0);
					if(fLocat.getLOCTYPE() != null && fLocat.getLOCTYPE().equals("URL")){
						String href = fLocat.getHref();
						xmlFile = new File(xmlDir + href.substring(href.lastIndexOf("/")));
						//fetch file from this URL and store locally
						FileUtils.copyURLToFile(new URL(href), xmlFile);
						logger.debug("file loaded from URL: " + href);
					}
				}
			}
			else if (type.getID().contains("Alto")){
				logger.debug("fptr id equals: " + type.getID());
				//TODO: implement
				if (xmlGrp.contains(type)){
					FLocat fLocat = type.getFLocat().get(0);
					if(fLocat.getLOCTYPE() != null && fLocat.getLOCTYPE().equals("URL")){
						String href = fLocat.getHref();
						xmlFile = new File(altoDir + href.substring(href.lastIndexOf("/")));
						//fetch file from this URL and store locally
						FileUtils.copyURLToFile(new URL(href), xmlFile);
						logger.debug("file loaded from URL: " + href);
					}
				}
			}
		}
		
		if(imgFile == null) {
			logger.error("No master image mapped for page " + nr + " in the structmap!");
		} else {
			logger.info("Page " + page.getPageNr() + " image: " + imgFile.getAbsolutePath());
		}
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
	

	
	/**
	 * Read the metadata from the Mods section into the corresponding TrpDocMetadata attributes
	 * TODO: not all attributes are filled till now#
	 * mising are: genre, writer, language, script type, description
	 * 
	 * @param mets
	 * @return
	 */
	public static TrpDocMetadata readModsMetadata(Document mets) {
		TrpDocMetadata result = new TrpDocMetadata();
			
		Element modsSection = (Element) XmlUtils.selectNode(mets.getDocumentElement(), "(*[contains(@ID,'DMDLOG_0000')])[1]");
				
		if (modsSection!=null)
		{
			NodeList actFields = modsSection.getElementsByTagName("mods:title");
			for (int i = 0; i < actFields.getLength(); i++) {
				logger.debug("title element found: " + actFields.getLength());
				Element act = (Element)actFields.item(i);
				String typeAttribute = ((Element)act.getParentNode()).getAttribute("type");
				
				String actValue = (actFields.item(i)).getTextContent();
				
				if (typeAttribute==null || typeAttribute.equals("")){
					//logger.debug("set actValue: " + actValue);
					result.setTitle(actValue);
				}
				
				//would be used to add title with special type, e.g. an uniform title
//				if (typeAttribute!=null && typeAttribute.equals("uniform"))
//					result.setTitle(actValue);
			}
//			actFields = modsSection.getElementsByTagName("mods:genre");
//			for (int i = 0; i < actFields.getLength(); i++) {
//			}
			actFields = modsSection.getElementsByTagName("mods:dateIssued");
			for (int i = 0; i < actFields.getLength(); i++) {
				String dateString = actFields.item(i).getTextContent();
				DateFormat format = new SimpleDateFormat("yyyy", Locale.GERMAN);
				Date date;
				try {
					date = format.parse(dateString);
					//System.out.println(date); // 2010-01-02
					result.setCreatedFromDate(date);
					result.setCreatedToDate(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 
			}
//			actFields = modsSection.getElementsByTagName("mods:languageTerm");
//			for (int i = 0; i < actFields.getLength(); i++) {
//				result.addLanguage(actFields.item(i).getTextContent());
//			}
//
			actFields = modsSection.getElementsByTagName("mods:name");
			for (int i = 0; i < actFields.getLength(); i++) {
				Element act = (Element)actFields.item(i);
				String typeAttribute = act.getAttribute("type");
				if (typeAttribute!=null && typeAttribute.equals("personal")){
					
					String role = XmlUtils.getFirstSubElementFromElement(act, "mods:roleTerm");
					//author
					if (role!=null && role.equals("aut")){
						String author = XmlUtils.getFirstSubElementFromElement(act, "mods:displayForm");
						logger.debug("Author found is " + author);
						if (author == null || author.equals("")){
							NodeList nl = act.getElementsByTagName("mods:namePart");
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
						        logger.debug("Author found is " + author);
								
								
							}

						}
						result.setAuthor(author);
						
					}  
				}
			}

		}
		else{
			logger.debug("mods section is null");
		}

		return result;

	}


}
