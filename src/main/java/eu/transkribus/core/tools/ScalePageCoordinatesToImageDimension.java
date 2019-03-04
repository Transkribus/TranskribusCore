package eu.transkribus.core.tools;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.io.util.ExtensionFileFilter;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageTypeUtils;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.PageXmlUtils;

public class ScalePageCoordinatesToImageDimension {
	private static final Logger logger = LoggerFactory.getLogger(ScalePageCoordinatesToImageDimension.class);
	public static void main(String[] args) throws IOException, JAXBException {
		
		/*
		 * use LocalDocReader to create pageXMLs from Abbyy XMLs for a complete directory
		 */
		final String startDir = "Y:/HTR/Wiener_Adressbuch/4OCR/batch2";
		loadAllDocsInDir(startDir);
		//loadSingleDoc(startDir);
		
//		fixNLF_GT();
//		heldenbuch600to300dpi();
		
//		final String path = "Y:/Newseye/NLF_GT/nlf_ocr_groundtruth_sv";
//
//		TrpDoc doc = LocalDocReader.load(path);
		
//		fixAltoFilenames();

		//fixAltoMmToPx(doc);
	}
	
	private static void loadSingleDoc(String startDir) throws IOException {
		File doc = new File(startDir);
		logger.debug("try to load doc at path " + doc.getAbsolutePath());
		LocalDocReader.load(doc.getAbsolutePath());
		
	}

	private static void loadAllDocsInDir(String startDir) throws IOException {
		File[] allDocDirs = new File(startDir).listFiles();
		for (File doc : allDocDirs){
			
			if (new File(doc.getAbsolutePath()+"/page").exists()){
				logger.debug("page XMLs for " + doc.getAbsolutePath() + "already created");
			}
			else{
				logger.debug("try to load doc at path " + doc.getAbsolutePath());
				LocalDocReader.load(doc.getAbsolutePath());
			}
			
		}
		
	}

	private static void fixAltoFilenames() throws IOException {
		final String path = "/media/daten/Dokumente/newseye_testdata/alto/";
		ExtensionFileFilter filt = new ExtensionFileFilter("xml", true, false);
		File[] files = new File(path).listFiles(filt);
		for(File f : files) {
			File target = new File(f.getAbsolutePath().replaceAll("-gt2", ""));
			Files.move(f, target);
		}
	}
	
	/*
	 * images size is in Pixel, alto file coordinates are in mm10 (calculated for physical page and 300dpi)
	 * means to correct it for Transkribus: scale with 1,1811: 300dpi/2,54cm(=1Zoll) 
	 */
	private static void fixNLF_GT() throws JAXBException, IOException {
		File input = new File("C:/01_Projekte/READ/Projekte/NewsEye/NLF_GT/nlf_ocr_groundtruth_fi/nlf_ocr_groundtruth_fi/alto");
		File output = new File("C:/01_Projekte/READ/Projekte/NewsEye/NLF_GT/nlf_ocr_groundtruth_fi/nlf_ocr_groundtruth_fi/page");
		if (!output.isDirectory()) {
			output.mkdirs();
		}

		File[] files = input.listFiles(new ExtensionFileFilter("xml", true, false));
		for (File f : files) {
			System.out.println("Processing file: " + f.getName());
			//(1) create pageXML from alto
			PcGtsType pc = null;		
			if(pc == null && f != null){
				//try find ALTO XML
				pc = LocalDocReader.createPageFromAlto2(f.getName().replace(".xml", ".tif"), f, true, true, false);
			}
			
			File pageTmp = new File(input+"/tmp_"+f.getName());
			//create the file
			try{
				JaxbUtils.marshalToFile(pc, pageTmp);
			} catch (JAXBException je) {
				throw new IOException("Could not create PageXml on disk!", je);
			}
			PcGtsType pc2 = PageXmlUtils.unmarshal(pageTmp);
			
			TrpPageTypeUtils.applyAffineTransformation(pc2.getPage(), 0, 0, 1.1811, 1.1811, 0);

			final String filename = f.getName();

			System.out.println("Writing file: " + filename);
			PageXmlUtils.marshalToFile(pc2, new File(output.getAbsolutePath() + File.separator + filename));
			pageTmp.delete();
		}
	}

	private static void heldenbuch300to600dpi() throws JAXBException, IOException {
		File input = new File("/tmp/Ambraser_Heldenbuch/Ambraser_Heldenbuch/page");
		File output = new File("/tmp/Ambraser_Heldenbuch/page_edited");
		if (!output.isDirectory()) {
			output.mkdirs();
		}

		File[] files = input.listFiles(new ExtensionFileFilter("xml", true, false));
		for (File f : files) {
			System.out.println("Processing file: " + f.getName());
			PcGtsType pc = PageXmlUtils.unmarshal(f);
			TrpPageTypeUtils.applyAffineTransformation(pc.getPage(), 0, 0, 2, 2, 0);

			final String filename;
			if (f.getName().contains("_")) {
				filename = f.getName().split("_")[1];
			} else {
				filename = f.getName();
			}
			System.out.println("Writing file: " + filename);
			PageXmlUtils.marshalToFile(pc, new File(output.getAbsolutePath() + File.separator + filename));
		}
	}
	
	private static void heldenbuch600to300dpi() throws JAXBException, IOException {
		File input = new File("C:/01_Projekte/READ/Projekte/Tengnagel/export_job_309921/page");
		File output = new File("C:/01_Projekte/READ/Projekte/Tengnagel/export_job_309921/page_edited");
		if (!output.isDirectory()) {
			output.mkdirs();
		}

		File[] files = input.listFiles(new ExtensionFileFilter("xml", true, false));
		for (File f : files) {
			System.out.println("Processing file: " + f.getName());
			PcGtsType pc = PageXmlUtils.unmarshal(f);
			TrpPageTypeUtils.applyAffineTransformation(pc.getPage(), 0, 0, 0.5, 0.5, 0);

			final String filename;
			if (f.getName().contains("_")) {
				filename = f.getName().split("_")[1];
			} else {
				filename = f.getName();
			}
			System.out.println("Writing file: " + filename);
			PageXmlUtils.marshalToFile(pc, new File(output.getAbsolutePath() + File.separator + filename));
		}
	}

	private static void fixAltoMmToPx(final TrpDoc doc) throws IOException, JAXBException {
		

		for (TrpPage p : doc.getPages()) {

//			final double imgWidth = p.getWidth();
//			final double imgHeight = p.getHeight();

			File f = FileUtils.toFile(p.getCurrentTranscript().getUrl());
			PcGtsType pc = PageXmlUtils.unmarshal(f);
//			final double altoWidth = pc.getPage().getImageWidth();
//			final double altoHeight = pc.getPage().getImageHeight();

//			logger.info("Img: " + imgWidth + "x" + imgHeight + " | ALTO: " + altoWidth + "x" + altoHeight);
//			
//			double scaleX = (imgWidth / (altoWidth / 100f)) / 100f;
//			double scaleY = (imgHeight / (altoHeight / 100f)) / 100f;
			
//			logger.info("Scale factor X: " + scaleX);
//			logger.info("Scale factor Y: " + scaleY);
			
			TrpPageTypeUtils.applyAffineTransformation(pc.getPage(), 0, 0, 1.1811, 1.1811, 0);

			PageXmlUtils.marshalToFile(pc, f);
		}

	}

}
