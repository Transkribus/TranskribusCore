package eu.transkribus.core.model.builder.mets;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dea.fimagestore.core.util.MimeTypes;
import org.dea.fimgstoreclient.FimgStoreGetClient;
import org.dea.fimgstoreclient.beans.FimgStoreFileMd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.ITrpFile;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.mets.AmdSecType;
import eu.transkribus.core.model.beans.mets.AreaType;
import eu.transkribus.core.model.beans.mets.DivType;
import eu.transkribus.core.model.beans.mets.DivType.Fptr;
import eu.transkribus.core.model.beans.mets.FileGrpType;
import eu.transkribus.core.model.beans.mets.FileType;
import eu.transkribus.core.model.beans.mets.FileType.FLocat;
import eu.transkribus.core.model.beans.mets.MdSecType;
import eu.transkribus.core.model.beans.mets.MdSecType.MdWrap;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.beans.mets.MetsType.FileSec;
import eu.transkribus.core.model.beans.mets.MetsType.FileSec.FileGrp;
import eu.transkribus.core.model.beans.mets.MetsType.MetsHdr;
import eu.transkribus.core.model.beans.mets.StructMapType;
import eu.transkribus.core.util.ChecksumUtils;
import eu.transkribus.core.util.JaxbUtils;

public class TrpMetsBuilder extends Observable {
	private static final Logger logger = LoggerFactory.getLogger(TrpMetsBuilder.class);
	public final static String METS_FILE_NAME = "mets.xml";
	//TODO check the xsd file that is used to build 
	public final static String TRP_METS_PROFILE = "TRP_V1";
	public static final String TRP_DOC_MD_TYPE_CONST = "TRP_DOC_MD";
	public static final String SOURCE_MD_ID_CONST = "SOURCE";
	public static final String SOURCE_DOC_MD_ID_CONST = "MD_ORIG";
	public static final String MASTER_FILE_GRP_ID = "MASTER";
	public static final String IMG_GROUP_ID = "IMG";
	public static final String PAGE_GROUP_ID = "PAGEXML";
	public static final String ALTO_GROUP_ID = "ALTO";
	public static final String TRP_STRUCTMAP_ID = "TRP_STRUCTMAP";
	public static final String TRP_DOC_DIV_ID = "TRP_DOC_DIV";
	
	/**
	 * Generate a METS containing
	 * <ul>
	 * <li>TrpDocMetadata embedded in sourceMd</li>
	 * <li>all page images</li>
	 * <li>the most recent PAGE XML files from the Doc</li>
	 * </ul>
	 * 
	 * If a local document is passed, all hrefs will contain the relative paths to files based on the localFolder!
	 * 
	 * @param doc
	 * @param exportImages 
	 * @param pageIndices 
	 * @return
	 * @throws IOException if image/xml files can't be accessed for reading the mimetype etc.
	 */
	public Mets buildMets(TrpDoc doc, boolean exportPage, boolean exportAlto, boolean exportImages, Set<Integer> pageIndices) throws IOException {
		
		Mets mets = new Mets();
		TrpDocMetadata md = doc.getMd();
		File localFolder = md.getLocalFolder();
		boolean isLocalDoc = localFolder != null;
		
		mets.setLABEL(md.getTitle());
		mets.setOBJID(""+md.getDocId());
		mets.setPROFILE(TRP_METS_PROFILE);
		//FIXME remove TYPE
//		mets.setTYPE(TRP_METS_PROFILE);
		
		//metsHdr
		MetsHdr hdr = buildMetsHdr(md);
		mets.setMetsHdr(hdr);
		//TODO dcmd_elec omitted meanwhile
		//md_orig
		AmdSecType amdSec = new AmdSecType();
		amdSec.setID(SOURCE_MD_ID_CONST);
		
		MdSecType sourceMdSec = buildSourceMdSec(md);
		
		amdSec.getSourceMD().add(sourceMdSec);
		mets.getAmdSec().add(amdSec);
		
		//structmap div, linking to the sourceMd section with dmd
		DivType div = new DivType();
		div.getADMID().add(sourceMdSec);
		div.setID(TRP_DOC_DIV_ID);
		
		FileSec fileSec = new FileSec();
		StructMapType structMap = new StructMapType();
		structMap.setID(TRP_STRUCTMAP_ID);
		structMap.setTYPE("MANUSCRIPT");
		structMap.setDiv(div);
		
		List<TrpPage> pages = doc.getPages();
		FimgStoreGetClient client = null;
		
		if(!isLocalDoc){
			//TODO maybe we need this stuff in the docMetadata?
			URL url = pages.get(0).getUrl();
			client = new FimgStoreGetClient(url);
		}
		
		FileGrp masterGrp = new FileGrp();
		masterGrp.setID(MASTER_FILE_GRP_ID);
		
		FileGrpType imgGrp = new FileGrpType();
		imgGrp.setID(IMG_GROUP_ID);
		
		FileGrpType pageGrp = new FileGrpType();
		pageGrp.setID(PAGE_GROUP_ID);
		
		FileGrpType altoGrp = new FileGrpType();
		altoGrp.setID(ALTO_GROUP_ID);
		
		int i = -1;
		for(TrpPage p : pages){
			
			i++;
			if (pageIndices!=null && !pageIndices.contains(i)){
				continue;
			}
			
			
			//build a page div for the structmap
			DivType pageDiv = new DivType();
			pageDiv.setID("PAGE_" + p.getPageNr());
			pageDiv.setTYPE("SINGLE_PAGE");
			pageDiv.setORDER(BigInteger.valueOf(p.getPageNr()));
			final String imgId = "IMG_" + p.getPageNr();
			final String xmlId = PAGE_GROUP_ID+"_" + p.getPageNr();
			final String altoId = ALTO_GROUP_ID+"_" + p.getPageNr();
			
			// do filesection stuff
			/* only the most recent transcript is added here for now
			 * 
			 * TODO how to deal with imagestore files? use orig image? right now, it's just the view file...
			 * TODO thumbnails not yet included
			*/
			if (exportImages){
				FileType img = buildFileType(localFolder, imgId, p, p.getPageNr(), client);
				imgGrp.getFile().add(img);
		
				//linking images
				Fptr imgPtr = buildFptr(img);			
				pageDiv.getFptr().add(imgPtr);
			}
			//TODO error handling.. if no transcript??
			if (exportPage){
				// xmlfiletype: just add the most recent transcript
				TrpTranscriptMetadata tMd; 
				
				//get the transcript chosen for export 
				tMd = p.getCurrentTranscript();
				
				FileType xml = buildFileType(md.getLocalFolder(), xmlId, tMd, p.getPageNr(), client);
				pageGrp.getFile().add(xml);
				Fptr xmlPtr = buildFptr(xml);
				pageDiv.getFptr().add(xmlPtr);
			}
			
			//create ALTO fileGrp
			if (exportAlto){
				FileType altoFt = new FileType();
				altoFt.setCHECKSUMTYPE(ChecksumUtils.ChkSumAlg.MD5.toString());
				//TODO calculate checksum
				altoFt.setCHECKSUM("");
				FLocat fLocat = new FLocat();
				fLocat.setLOCTYPE("OTHER");
				fLocat.setOTHERLOCTYPE("FILE");
					
				altoFt.setID(altoId);
				altoFt.setSEQ(p.getPageNr());

				//String tmpImgName = img.getFLocat().get(0).getHref();
				String relAltoPath = "alto".concat(File.separator).concat(p.getImgFileName().substring(0, p.getImgFileName().lastIndexOf(".")).concat(".xml"));
				fLocat.setHref(relAltoPath);
				
				//String absAltoPath = tMd.getUrl().getPath().replace("page", "alto");
				final String path = FileUtils.toFile(p.getUrl()).getAbsolutePath();
				String absAltoPath = path.substring(0, path.lastIndexOf(File.separator));
				absAltoPath = absAltoPath.concat("/alto/").concat(p.getImgFileName().substring(0, p.getImgFileName().lastIndexOf(".")).concat(".xml"));
				//logger.info("alto path starts with: " + absAltoPath);
				if (absAltoPath.startsWith("\\") /*|| absAltoPath.startsWith("/")*/){
					//logger.info("alto path starts with \\ or /");
					absAltoPath = absAltoPath.substring(1);
				}
				
				String mime = MimeTypes.getMimeType("xml");
				altoFt.setMIMETYPE(mime);
				
				File altoTmp = new File(absAltoPath);
				if(altoTmp.exists()){
					//logger.info("alto file exist at " + absAltoPath);
					Date date = new Date(altoTmp.lastModified());
					XMLGregorianCalendar cal = JaxbUtils.getXmlCalendar(date);
					altoFt.setCREATED(cal);
				}
				else{
					logger.info("alto file does not exist at " + absAltoPath);
				}
				
//				System.out.println("relAltoPath " + relAltoPath);
//				System.out.println("absAltoPath " + absAltoPath);
//				System.in.read();

				altoFt.getFLocat().add(fLocat);
				
				altoGrp.getFile().add(altoFt);
				Fptr altoPtr = buildFptr(altoFt);
				pageDiv.getFptr().add(altoPtr);
				
			}
	
			div.getDiv().add(pageDiv);
			
			
			
		}
		fileSec.getFileGrp().add(masterGrp);
		mets.setFileSec(fileSec);
		
		if (exportImages){
			masterGrp.getFileGrp().add(imgGrp);
		}
		if (exportPage){
			masterGrp.getFileGrp().add(pageGrp);
		}
		if (exportAlto){
			masterGrp.getFileGrp().add(altoGrp);
		}
		
		mets.getStructMap().add(structMap);		
		
		return mets;
		
	}
	
	private MetsHdr buildMetsHdr(TrpDocMetadata md) {
		MetsHdr hdr = new MetsHdr();
		
		XMLGregorianCalendar xmlCal = JaxbUtils.getXmlCalendar(new Date());
		
		hdr.setCREATEDATE(xmlCal);
		hdr.setLASTMODDATE(xmlCal);
		//TODO set some real value for record status
		hdr.setRECORDSTATUS("SUBMITTED");
		MetsHdr.Agent agent = new MetsHdr.Agent();
		agent.setTYPE("ORGANIZATION");
		agent.setROLE("CREATOR");
		//FIXME what is the real agent here?
		agent.setName("UIBK");
		agent.getNote().add("This METS file was generated by Transkribus");
		hdr.getAgent().add(agent);
		return hdr;
	}
	
	private MdSecType buildSourceMdSec(TrpDocMetadata md) {
		MdWrap wrap = new MdWrap();
		wrap.setMDTYPE("OTHER");
		wrap.setID(TRP_DOC_MD_TYPE_CONST);
		MdWrap.XmlData xmlData = new MdWrap.XmlData();
		
		//clone md and set local folder to null
//		TrpDocMetadata mdClone = md.clone();
//		mdClone.setLocalFolder(null);
//		xmlData.getAny().add(mdClone);
		//FIXME if the localFolder is null, this will be treated as remote doc, but the fileKeys are null!! => nullpointerexception
		//md.setLocalFolder(null);
		xmlData.getAny().add(md);
		
		wrap.setXmlData(xmlData);
		MdSecType sec = new MdSecType();
		sec.setMdWrap(wrap);
		//link to the sourceMD with TrpDocMetadata
		sec.setID(SOURCE_DOC_MD_ID_CONST);
		return sec;
	}
	
	/**
	 * @param localFolder null if isLocalDoc
	 * @param id
	 * @param o
	 * @param client
	 * @return
	 * @throws IOException
	 */
	private FileType buildFileType(File localFolder, String id, ITrpFile o, final int seq, FimgStoreGetClient client) throws IOException {
		FileType fType = new FileType();
		fType.setID(id);
		String mime = null;
		Date date = null;
		FLocat fLocat = new FLocat();
		String loc = null;
		if(localFolder != null){
			URL url = o.getUrl();
			if(!url.getProtocol().contains("file")){
				throw new IOException("Doc contains local folder reference but an URL refers to a non-local file! " + url.toString());
			}
			final String path = FileUtils.toFile(url).getAbsolutePath();
			File f = new File(path);
			mime = MimeTypes.getMimeType(FilenameUtils.getExtension(f.getName()));
			date = new Date(f.lastModified());
			fLocat.setLOCTYPE("OTHER");
			fLocat.setOTHERLOCTYPE("FILE");
			//remove protocol and localfolder, i.e. get relative path to this file
//			loc = path.substring(localFolder.getAbsolutePath().length() + 1); // BUG: localFolder != path!!
			
			loc = FilenameUtils.getName(path);
			if (id.startsWith(PAGE_GROUP_ID)) { // append relative folder for PAGE XML files
				loc = "page/"+loc;
			}
			
			logger.debug("loc = "+loc);
			
			if(o.getMd5Sum() != null){
				fType.setCHECKSUMTYPE(ChecksumUtils.ChkSumAlg.MD5.toString());
				fType.setCHECKSUM(o.getMd5Sum());
			}
		} else {
			try {
				FimgStoreFileMd fMd = client.getFileMd(o.getKey());
				date = fMd.getUploadDate();
				mime = fMd.getMimetype();
				fLocat.setLOCTYPE("URL");
				//full URL in case of remote file
				loc = o.getUrl().toString();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new IOException("FileMetadata could not be retrieved from imagestore for key: " + o.getKey());
			}
		}
		fType.setMIMETYPE(mime);
		
		XMLGregorianCalendar cal = JaxbUtils.getXmlCalendar(date);
		fType.setCREATED(cal);
		fType.setSEQ(seq);
		fLocat.setHref(loc);
		fType.getFLocat().add(fLocat);
		return fType;
	}
	
	private Fptr buildFptr(FileType img) {
		Fptr ptr = new Fptr();
		AreaType area = new AreaType();
		area.setFILEID(img);
		ptr.setArea(area);
		return ptr;
	}
}
