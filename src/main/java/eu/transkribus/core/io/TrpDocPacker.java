package eu.transkribus.core.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.mets.FileGrpType;
import eu.transkribus.core.model.beans.mets.FileType;
import eu.transkribus.core.model.beans.mets.FileType.FLocat;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.builder.mets.TrpMetsBuilder;
import eu.transkribus.core.model.builder.mets.util.MetsUtil;
import eu.transkribus.core.util.ChecksumUtils;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.ZipUtils;

/**
 * TODO compute checksums for files
 * TODO techmd extraction
 * 
 * @author philip
 *
 */
public class TrpDocPacker extends Observable {
	private static final Logger logger = LoggerFactory.getLogger(TrpDocPacker.class);
	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
	
	public File packDocFiles(TrpDoc doc, String zipFilePath) throws IOException{		
		File localFolder = doc.getMd().getLocalFolder();
		
		if(localFolder == null){
			throw new IOException("Not a local Document!");
		}
		
		updateStatus("Computing checksums...");
		for (TrpPage p : doc.getPages()) {
			p.setMd5Sum(computeChkSum(p.getUrl()));
			for(TrpTranscriptMetadata t : p.getTranscripts()){
				t.setMd5Sum(computeChkSum(t.getUrl()));
			}
		}
		
		
		if(zipFilePath == null || zipFilePath.isEmpty()){
			logger.info("No zip file path specified.");
			zipFilePath = TEMP_DIR + File.separator + "TRP_DOC_" + System.currentTimeMillis() + ".zip";
		} else if(!(new File(zipFilePath).getParentFile().exists())){
			throw new IllegalArgumentException(zipFilePath + " refers to a non-existent directory!");
		}
		logger.info("Creating zip file at: " + zipFilePath);
		
		String metsFilePath = localFolder.getAbsoluteFile() + File.separator + TrpMetsBuilder.METS_FILE_NAME;
		File metsFile = new File(metsFilePath);
		Mets mets;
		logger.info("Creating METS file at: " + metsFilePath);
		//build a mets that points to all files we need
		//2nd arg: export page files (add to mets filesec), 3rd arg: export alto files, 4th arg: export images
		mets = TrpMetsBuilder.buildMets(doc, true, false, true, null);
		
		try {
			metsFile = JaxbUtils.marshalToFile(mets, metsFile, TrpDocMetadata.class);
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
			throw new IOException("Could not create METS file.", e);
		}
		
		updateStatus("Built METS file");
		
		//prepare the list with files to be packed into the ZIP
		List<String> fileList = new LinkedList<>();
		fileList.add(TrpMetsBuilder.METS_FILE_NAME);
		
		//traverse the METS filesection and add all files to be zipped
		List <FileGrpType> typeGrps = MetsUtil.getMasterFileGrp(mets);
		for(FileGrpType type : typeGrps){
			if(type.getID().equals(TrpMetsBuilder.IMG_GROUP_ID) 
					|| type.getID().equals(TrpMetsBuilder.PAGE_GROUP_ID)){
				List<String> files = getFiles(type);
				fileList.addAll(files);
			}
		}
		updateStatus("Creating ZIP file...");
		File zipFile = ZipUtils.zip(fileList, localFolder.getAbsolutePath(), zipFilePath);
		
		//TADAAAA!
		return zipFile;
	}
	
	public TrpDoc unpackDoc(File zipFile, String path) throws IOException{
		
		if(path == null || path.isEmpty()){
			path = TEMP_DIR + File.separator + FilenameUtils.getBaseName(zipFile.getName());
		}
		
		File dir = ZipUtils.unzip(zipFile, path);
		
		//assume mets path and open file
		final String metsPath = path + File.separator + TrpMetsBuilder.METS_FILE_NAME;
		File metsFile = new File(metsPath);
		
		if(!metsFile.exists()){
			throw new IOException("No METS file included in zip!");
		}
		
//		final File parentDir = new File(metsFile.getParent());

		Mets mets;
		try {
			mets = JaxbUtils.unmarshal(metsFile, Mets.class, TrpDocMetadata.class);
		} catch (JAXBException e) {
			throw new IOException("Could not unmarshal METS file!", e);
		}
		
		TrpDoc doc = LocalDocReader.load(mets, dir);
		return doc;
	}

	private void updateStatus(String string) {
		setChanged();
		notifyObservers(string);
	}
	
	private List<String> getFiles(FileGrpType type) {
		List<String> fileList = new LinkedList<>();
		for(FileType ft : type.getFile()){
			for(FLocat fl : ft.getFLocat()){
				if(fl.getLOCTYPE().equals("OTHER") && fl.getOTHERLOCTYPE().equals("FILE")){
					logger.debug("Adding File: " + fl.getHref());
					fileList.add(fl.getHref());
					updateStatus("Adding File: " + fl.getHref());
				}
			}
		}
		return fileList;
	}
	
	private String computeChkSum(URL url) throws IOException{
		if(!url.getProtocol().contains("file")){
			throw new IOException(url + " is not a local file!");
		}
		final String chkSum;
		File f = FileUtils.toFile(url);
		updateStatus("Computing checksum: " + f.getAbsolutePath());
		chkSum = ChecksumUtils.getMd5SumHex(f);
		return chkSum;
	}
}
