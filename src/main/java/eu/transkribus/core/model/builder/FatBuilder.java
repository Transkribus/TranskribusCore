package eu.transkribus.core.model.builder;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.LocalDocConst;
import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.io.UnsupportedFormatException;
import eu.transkribus.core.io.exec.ExiftoolUtil;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.enums.ScriptType;
import eu.transkribus.core.model.beans.fat.DocumentFolder;
import eu.transkribus.core.model.beans.fat.FepMetadata;
import eu.transkribus.core.model.beans.fat.FileFolder;
import eu.transkribus.core.model.beans.fat.OcrMetadata;
import eu.transkribus.core.model.beans.fat.Order;
import eu.transkribus.core.model.beans.fat.RootFolder;
import eu.transkribus.core.model.beans.fat.File.Metadata;
import eu.transkribus.core.util.ChecksumUtils;
import eu.transkribus.core.util.JaxbUtils;

public class FatBuilder {
	private static final Logger logger = LoggerFactory.getLogger(FatBuilder.class);
	public static final String FAT_FILE_NAME = "FAT.xml";

	public static File writeFatXml(File outputDir, final String languages, final String typeFace) throws UnsupportedFormatException, IOException {
		
		if(!new File(outputDir.getAbsolutePath() + File.separator + LocalDocConst.OCR_MASTER_DIR).isDirectory()){
			throw new IllegalArgumentException("No directory '" + LocalDocConst.OCR_MASTER_DIR 
					+ "' in directory: " + outputDir.getAbsolutePath());
		}
		
		//needs a local doc! Read files separately because we don't want to create Page XMLs
		Map<String, File> imgFiles = LocalDocReader.findImgFiles(outputDir);
		TrpDocMetadata docMd = LocalDocReader.findDocMd(outputDir);
		
//		final DocType docType = doc.getMd().getType();
//		if(!DocType.PRINT.equals(docType)){
//			throw new IllegalArgumentException("DocType " + docType + " not allowed for FAT XML production");
//		}
		
		RootFolder rootFolder = new RootFolder();

		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern("yyyy-MM-dd hh:mm");
		final String dateStr = df.format(new Date());
		
		rootFolder.setDate(dateStr);
		final BigInteger nFiles = getBigIntValue(imgFiles.size());
		rootFolder.setNFiles(nFiles);
		rootFolder.setNDocuments(BigInteger.valueOf(1));
		rootFolder.setNFileWarnings(BigInteger.valueOf(0));
		rootFolder.setNFolders(BigInteger.valueOf(1));
		
		DocumentFolder docFolder = new DocumentFolder();
		docFolder.setName(outputDir.getName());
		docFolder.setPath(LocalDocConst.OCR_MASTER_DIR);
		docFolder.setNFilesPerFolder(nFiles);
		
		//TODO throw exception if missingMetadata is true?
		boolean missingMetadata = false;
		Order order = new Order();
		order.setHasViewingFiles("false");
		order.setServices("(OCR)");
		OcrMetadata ocrM = new OcrMetadata();
		
		if(languages != null && !languages.isEmpty()) {
			ocrM.setLanguages(languages);
		} else if(docMd.getLanguage() != null && !docMd.getLanguage().isEmpty()) {
			ocrM.setLanguages(docMd.getLanguage());
		} else {
			missingMetadata = true;
			ocrM.setLanguages("");
		}
		
		if(typeFace != null && !typeFace.isEmpty()) {
			ocrM.setTexttype(typeFace);
		} else if(docMd.getScriptType() != null) {
			ocrM.setTexttype(docMd.getScriptType().toString());
		} else {
			ocrM.setTexttype(ScriptType.NORMAL.toString());
			missingMetadata = true;
		}

	
		ocrM.setOutput("(ABBYY-XML)");
		
		//check the following!
		int nDocsMissingMetadata = missingMetadata ? 1 : 0;
		rootFolder.setNDocumentsMissingMetadata(BigInteger.valueOf(nDocsMissingMetadata));
		
		FepMetadata fep = new FepMetadata();
		fep.setWorkflow("None");
		
		order.setOcrMetadata(ocrM);
		order.setFepMetadata(fep);
		docFolder.setOrder(order);
		
		FileFolder fileFolder = new FileFolder();
		fileFolder.setType("img");
		//existence of OCR_MASTER_DIR is checked at the beginning
		fileFolder.setName(LocalDocConst.OCR_MASTER_DIR);
		fileFolder.setPath(LocalDocConst.OCR_MASTER_DIR);
		
//		List<TrpPage> pages = doc.getPages();
		
		int checkedFiles = 0;
		int uncheckedFiles = 0;
		int nFileErrors = 0;
		
		for(Entry<String, File> imgE : imgFiles.entrySet()){
			final File img = imgE.getValue();
			
			eu.transkribus.core.model.beans.fat.File file = 
					new eu.transkribus.core.model.beans.fat.File();
			file.setName(img.getName());
			String errorType;
			String message;
			try {
				final Map<String, String> exif = ExiftoolUtil.extractImgMd(img.getAbsolutePath());
				final String mimetype = exif.get("MIMEType");
				final String xRes = exif.get("XResolution");
				final String yRes = exif.get("YResolution");
				final String width = exif.get("ImageWidth");
				final String height = exif.get("ImageHeight");
				
				Metadata md = new Metadata();
				
				md.setMimetype(mimetype);
				md.setXRes(getBigIntValue(xRes));
				md.setYRes(getBigIntValue(yRes));
				md.setWidth(getBigIntValue(width));
				md.setHeight(getBigIntValue(height));
				
				final String md5 = formatChecksum(ChecksumUtils.getMd5SumHex(img));
				md.setChecksum(md5);
				file.setMetadata(md);
				checkedFiles++;
				file.setStatus("Checked");
				errorType = "None";
				message = "";
			} catch (TimeoutException | InterruptedException | NumberFormatException e) {
				uncheckedFiles++;
				nFileErrors++;
				errorType = e.getClass().getName();
				message = e.getMessage();
				file.setStatus("Error");
				logger.error("Could not run file checks for file: " + img.getAbsolutePath(), e);
			}
			file.setErrorType(errorType);
			file.setMessage(message);
			fileFolder.getFile().add(file);
		}
		
		rootFolder.setNCheckedFiles(getBigIntValue(checkedFiles));
		rootFolder.setNUncheckedFiles(getBigIntValue(uncheckedFiles));
		rootFolder.setNFileErrors(getBigIntValue(nFileErrors));
		
		docFolder.getFileFolder().add(fileFolder);
		rootFolder.getDocumentFolder().add(docFolder);
		
		File fatFile = new File(outputDir.getAbsolutePath() + File.separator
				+ FatBuilder.FAT_FILE_NAME);

		try {
			fatFile = JaxbUtils.marshalToFile(rootFolder, fatFile);
		} catch (JAXBException e) {
			throw new IOException("Could not marshal FAT XML to file!", e);
		}
		
		return fatFile;
	}

	/**
	 * Convert a hex checksum into FAT compliant format, i.e.:
	 * <br/>
	 * "cf8fc30fa867dc1d5dd88757b43064b7"<br/>
	 * into<br/>
	 * "CF-8F-C3-0F-A8-67-DC-1D-5D-D8-87-57-B4-30-64-B7"
	 * 
	 * @param md5SumHex
	 * @return
	 */
	private static String formatChecksum(String md5SumHex) {
		char[] sum = md5SumHex.toUpperCase().toCharArray();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < sum.length;){
			sb.append(sum[i++]);
			if(i%2 == 0 && i != sum.length){
				sb.append("-");
			}
		}
		return sb.toString();
	}

	private static BigInteger getBigIntValue(int n) {
		return BigInteger.valueOf(new Integer(n).intValue());
	}
	
	private static BigInteger getBigIntValue(String nStr) {
		int val;
		try {
			val = Integer.parseInt(nStr);
		} catch(NumberFormatException e) {
			logger.error("Could not parse number: " + nStr, e);
			val = 0;
		}
		return BigInteger.valueOf(val);
	}
	
	public static void main(String[] args){
		final String target = "CF-8F-C3-0F-A8-67-DC-1D-5D-D8-87-57-B4-30-64-B7";
		final String input = "cf8fc30fa867dc1d5dd88757b43064b7";
		final String result = formatChecksum(input);
		
		System.out.println(result + " -> " + result.equals(target));
		
	}

}
