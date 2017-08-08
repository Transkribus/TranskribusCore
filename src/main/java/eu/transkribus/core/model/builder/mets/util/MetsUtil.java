package eu.transkribus.core.model.builder.mets.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpDocStructure.TrpDocStructureImage;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.enums.EditStatus;
import eu.transkribus.core.model.beans.mets.AmdSecType;
import eu.transkribus.core.model.beans.mets.DivType;
import eu.transkribus.core.model.beans.mets.DivType.Fptr;
import eu.transkribus.core.model.beans.mets.FileGrpType;
import eu.transkribus.core.model.beans.mets.FileType;
import eu.transkribus.core.model.beans.mets.FileType.FLocat;
import eu.transkribus.core.model.beans.mets.MdSecType;
import eu.transkribus.core.model.beans.mets.MdSecType.MdWrap.XmlData;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.beans.mets.MetsType.FileSec.FileGrp;
import eu.transkribus.core.model.beans.mets.StructMapType;
import eu.transkribus.core.model.builder.mets.TrpMetsBuilder;
import eu.transkribus.core.util.ChecksumUtils;

public class MetsUtil {
	private static final Logger logger = LoggerFactory.getLogger(MetsUtil.class);

	public static boolean isDfgMets(File metsFile)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		
		// TODO check if METS Profile is known
					/*
					 * <mets:rightsMD ID="RIGHTS">
					 * 					<dv:rights xmlns:dv="http://dfg-viewer.de/">
									<dv:owner>Universitätsbibliothek Rostock</dv:owner>
									
									//mets:rightsMD[@ID,'RIGHTS']
									
					 */
					
					//Element rightsElement = (Element) XmlUtils.selectNode((Element) mets.getAmdSec(), "(*[contains(@ID,'RIGHTS')])[1]");
					//Element rightsElement = null;// = (Element) XmlUtils.selectNode((Element) mets.getAmdSec(), "//mets:rightsMD[@ID,'RIGHTS']");
					
		
		boolean isCompliant = false;
		// Create DocumentBuilderFactory for reading xml file
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(metsFile);

		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		XPathExpression expr = xpath.compile("//*[local-name()='rightsMD'][(@ID,'RIGHTS')][1]");
		Element rightsElement = (Element) expr.evaluate(doc, XPathConstants.NODE);

		if (rightsElement != null) {
			NodeList actFields = rightsElement.getElementsByTagName("dv:rights");

			for (int i = 0; i < actFields.getLength(); i++) {
				Element dvRights = (Element) actFields.item(i);

				logger.debug("title element found: " + dvRights.getAttribute("xmlns:dv"));

				if (dvRights.getAttribute("xmlns:dv").equals("http://dfg-viewer.de/")) {
					logger.debug("Dfg-Viewer conform Mets delivered....go on");
					isCompliant = true;
				}
			}

		}
		return isCompliant;
	}

	public static List<FileGrpType> getMasterFileGrp(Mets mets) {
		List<FileGrp> fileGrps = mets.getFileSec().getFileGrp();
		
		FileGrp master = null;
		for(FileGrp grp : fileGrps){
			if(grp.getID().equals(TrpMetsBuilder.MASTER_FILE_GRP_ID)){
				master = grp;
				break;
			}
		}
		if(master == null) throw new IllegalArgumentException("METS file has no MASTER fileGrp!");
		
		return master.getFileGrp();
	}

	public static TrpDocMetadata getTrpDocMd(Mets mets) {
		TrpDocMetadata md = null;
		List<AmdSecType> secList = mets.getAmdSec();
		List<MdSecType> mdSecList = null;
		for (AmdSecType sec : secList) {
			if (sec.getID().equals(TrpMetsBuilder.SOURCE_MD_ID_CONST)) {
				mdSecList = sec.getSourceMD();
				break;
			}
		}
		if (mdSecList == null)
			logger.error("No SourceMd Section found!");
		else {
			XmlData xmlData = null;
			for (MdSecType mdSec : mdSecList) {
				if (mdSec.getID().equals(TrpMetsBuilder.SOURCE_DOC_MD_ID_CONST)
						&& mdSec.getMdWrap().getID().equals(TrpMetsBuilder.TRP_DOC_MD_TYPE_CONST)) {
					xmlData = mdSec.getMdWrap().getXmlData();
					break;
				}
			}
			if (xmlData != null && xmlData.getAny().size() > 0) {
				Object o = xmlData.getAny().get(0);
				if (o instanceof TrpDocMetadata) {
					md = (TrpDocMetadata) o;
					logger.info("Found metadata: " + md.toString());
				} else {
					logger.error("No doc MD found! ");
				}
			}
		}
		return md;
	}

	/**
	 * Builds the set of TrpPage objects with 
	 * local file references from the mets master file group and structmap.
	 * The method is strict regarding PAGE XML existence! Each image file must have a correspondent PAGE XML.
	 * @param mets
	 * @param parentDir
	 * @return
	 * @throws IOException
	 */
	public static List<TrpPage> getTrpPages(Mets mets, File parentDir) throws IOException {
		//check filesection. needs img group and xml group to distinguish them without going for mimetypes
		List<FileGrpType> typeGrps = getMasterFileGrp(mets);
		List<FileType> xmlGrp = null;
		List<FileType> imgGrp = null;
		for (FileGrpType type : typeGrps) {
			switch (type.getID()) {
			case TrpMetsBuilder.IMG_GROUP_ID:
				imgGrp = type.getFile();
				break;
			case TrpMetsBuilder.PAGE_GROUP_ID:
				xmlGrp = type.getFile();
				break;
			default:
				break;
			}
		}
		if (imgGrp == null)
			throw new IOException("METS file has no image file list!");
		if (xmlGrp == null)
			throw new IOException("METS file has no xml file list!");
		
		List<DivType> pageDivs = getPageDivsFromStructMap(mets);
		if(pageDivs == null) {
			throw new IOException("No valid StructMap was found!");
		}
		
		List<TrpPage> pages = new ArrayList<TrpPage>(pageDivs.size());
		for(DivType div : pageDivs){
			TrpPage page = buildPage(div, imgGrp, xmlGrp, parentDir);
			pages.add(page);
		}
		return pages;
	}

	public static File getFile(FileType type, File parentDir) throws IOException{
		File file = null;
		FLocat fLocat = type.getFLocat().get(0);
		if(fLocat.getOTHERLOCTYPE() != null && fLocat.getOTHERLOCTYPE().equals("FILE")){
			//localdoc
			file = new File(parentDir.getAbsolutePath() + File.separator + fLocat.getHref());
			if(!file.exists()){
				throw new IOException("File does not exist: " + file.getAbsolutePath());
			}
			
			if(!type.isSetCHECKSUMTYPE()){
				logger.error("No checksum set!");
			} else if(!type.getCHECKSUMTYPE().equals(ChecksumUtils.ChkSumAlg.MD5.toString())){
				logger.error("Unknown checksum algorithm: " + type.getCHECKSUMTYPE());
			} else {
				final String metsChkSum = type.getCHECKSUM();
				final String chkSum = ChecksumUtils.getMd5SumHex(file);
				if(!metsChkSum.equals(chkSum)){
					throw new IOException("Checksum error: METS=" + metsChkSum + " <-> FILE=" + chkSum + " | " + file.getAbsolutePath());
				}
				logger.debug("Checksum is correct: " + file.getAbsolutePath());
			}
			
		} else { 
			//TODO implement for URL type
			throw new IOException("METS file does not belong to a local document!");
		}
		return file;
	}
	
	public static Pair<String, String> getFileNameAndChecksum(FileType type) throws IllegalArgumentException {
		String name = null;
		String checksum = null;
		FLocat fLocat = type.getFLocat().get(0);
		if(fLocat.getOTHERLOCTYPE() != null && fLocat.getOTHERLOCTYPE().equals("FILE")){
			//localdoc
			name = fLocat.getHref();
			if(StringUtils.isEmpty(name)){
				throw new IllegalArgumentException("File name is empty on FLocat ID=" + fLocat.getID());
			}
			
			if(!type.isSetCHECKSUMTYPE()){
				logger.info("No checksum set!");
			} else if(!type.getCHECKSUMTYPE().equals(ChecksumUtils.ChkSumAlg.MD5.toString())){
				logger.info("Unknown checksum algorithm: " + type.getCHECKSUMTYPE());
			} else {
				checksum = type.getCHECKSUM();
//				final String chkSum = ChecksumUtils.getMd5SumHex(file);
//				if(!metsChkSum.equals(chkSum)){
//					throw new IOException("Checksum error: METS=" + metsChkSum + " <-> FILE=" + chkSum + " | " + file.getAbsolutePath());
//				}
//				logger.debug("Checksum is correct: " + file.getAbsolutePath());
			}
		} else {
			throw new IllegalArgumentException("METS file does not belong to a local document!");
		}
		return Pair.of(name, checksum);
	}
	
	public static List<TrpDocStructureImage> getImagesToUpload(Mets mets) {
		//check filesection. needs img group and xml group to distinguish them without going for mimetypes
		List<FileGrpType> typeGrps = getMasterFileGrp(mets);
		boolean hasXml = true;
		List<FileType> xmlGrp = null;
		List<FileType> imgGrp = null;
		for (FileGrpType type : typeGrps) {
			switch (type.getID()) {
			case TrpMetsBuilder.IMG_GROUP_ID:
				imgGrp = type.getFile();
				break;
			case TrpMetsBuilder.PAGE_GROUP_ID:
				xmlGrp = type.getFile();
				break;
			default:
				break;
			}
		}
		if (imgGrp == null) {
			throw new IllegalArgumentException("METS file has no image file list!");
		}
		if (xmlGrp == null) {
			logger.debug("METS file has no xml file list!");
		}
		List<DivType> pageDivs = getPageDivsFromStructMap(mets);
		if(pageDivs == null)
			throw new IllegalArgumentException("No valid StructMap was found!");
		
		List<TrpDocStructureImage> images = new ArrayList<TrpDocStructureImage>(pageDivs.size());
		for(DivType div : pageDivs){
			TrpDocStructureImage image = buildUploadImage(div, imgGrp, xmlGrp);
			images.add(image);
		}
		return images;
	}

	private static List<DivType> getPageDivsFromStructMap(Mets mets) {
		List<DivType> pageDivs = null;
		for(StructMapType sMap : mets.getStructMap()){
			if(sMap.getID().equals(TrpMetsBuilder.TRP_STRUCTMAP_ID) 
					&& sMap.getDiv().getID().equals(TrpMetsBuilder.TRP_DOC_DIV_ID)){
				pageDivs = sMap.getDiv().getDiv();
				break;
			}
		}
		return pageDivs;
	}

	private static TrpPage buildPage(DivType div, List<FileType> imgGrp, List<FileType> xmlGrp, File parentDir) throws IOException {
		TrpPage page = new TrpPage();
		int nr = div.getORDER().intValue();
		page.setPageNr(nr);
		
		File imgFile = null;
		File xmlFile = null;
		
		
		//FIXME this will only work for local files
		for(Fptr ptr : div.getFptr()){
			FileType type = (FileType)ptr.getArea().getFILEID();				
			if(imgGrp.contains(type)){
				imgFile = MetsUtil.getFile(type, parentDir);
			} else if (xmlGrp.contains(type)){
				xmlFile = MetsUtil.getFile(type, parentDir);
			}
		}
		
		if(imgFile == null) {
			logger.error("No master image mapped for page " + nr + " in the structmap!");
		} else {
			logger.info("Page " + page.getPageNr() + " image: " + imgFile.getAbsolutePath());
		}
		page.setUrl(imgFile.toURI().toURL());
		page.setKey(null);
		page.setDocId(-1);
		page.setImgFileName(imgFile.getName());

		if(xmlFile == null) {
			logger.error("No master xml mapped for page " + nr + " in the structmap!");
		} else {
			logger.info("Page " + page.getPageNr() + " xml: " + xmlFile.getAbsolutePath());
		}
		TrpTranscriptMetadata tmd = new TrpTranscriptMetadata();
		tmd.setPageReferenceForLocalDocs(page);
		tmd.setPageId(page.getPageId());
		tmd.setUrl(xmlFile.toURI().toURL());
		tmd.setKey(null);
		tmd.setStatus(EditStatus.NEW);
		tmd.setTimestamp(new Date().getTime());
		tmd.setUserName("LocalDocReader");

		page.getTranscripts().add(tmd);
		return page;
	}
	
	private static TrpDocStructureImage buildUploadImage(DivType div, List<FileType> imgGrp, List<FileType> xmlGrp) {
		TrpDocStructureImage image = new TrpDocStructureImage();
		int pageIndex = div.getORDER().intValue() - 1;
		image.setIndex(pageIndex);
		
		String imgFileName = null;
		String xmlFileName = null;
		String imgChecksum = null;
		String xmlChecksum = null;
		
		for(Fptr ptr : div.getFptr()){
			FileType type = (FileType)ptr.getArea().getFILEID();				
			if(imgGrp.contains(type)){
				final Pair<String, String> imgPair = MetsUtil.getFileNameAndChecksum(type);
				imgFileName = imgPair.getLeft();
				imgChecksum = imgPair.getRight();
			} else if (xmlGrp != null && xmlGrp.contains(type)){
				final Pair<String, String> xmlPair = MetsUtil.getFileNameAndChecksum(type);
				xmlFileName = xmlPair.getLeft();
				xmlChecksum = xmlPair.getRight();
			}
		}
		
		if(StringUtils.isEmpty(imgFileName)) {
			logger.error("No master image mapped for page index = " + pageIndex + " in the structmap!");
		} else {
			logger.info("Page " + image.getIndex() + " image: " + imgFileName);
		}
		image.setFileName(imgFileName);
		image.setImgChecksum(imgChecksum);
		image.setPageXmlName(xmlFileName);
		image.setXmlChecksum(xmlChecksum);
		
		return image;
	}
}
