package eu.transkribus.core.io;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.exceptions.CorruptImageException;
import eu.transkribus.core.util.ImgUtils;
import eu.transkribus.core.util.SysUtils;
import eu.transkribus.core.util.SebisStopWatch.SSW;

public class LocalDocReaderTestOld {
	private static final Logger logger = LoggerFactory.getLogger(LocalDocReaderTestOld.class);

	private static final String BASE = "/mnt/dea_scratch/TRP/";

	public static final String TEST_DOC1 = BASE + "TrpTestDoc_20140127/";
	public static final String TEST_DOC2 = BASE + "TrpTestDoc_20140508/";
		
//	@Test
	public void testListImgFiles() throws IOException {
		Assume.assumeTrue(SysUtils.isLinux());
		
		int nrOfFiles1 = 0, nrOfFiles2 = 0;
		final String path = "/mnt/transkribus/user_storage/philip.kahle@transkribus.eu/AAK_scans_scaled";
		SSW sw = new SSW();
		
		sw.start();
		new File(path).list();
		nrOfFiles1 = LocalDocReader.findImgFiles(new File(path)).size();
		sw.stop();
	
		sw.start();
		new File(path).list();
		nrOfFiles2 = LocalDocReader.findImgFilenames(new File(path)).size();
		sw.stop();
		
		Assert.assertEquals(nrOfFiles1, nrOfFiles2);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		String pageDirPath = "C:/Neuer Ordner/Briefe_aus_allen_Jahrhunderten_der_christlichen_Zeitrechnung_1/Briefe_aus_allen_Jahrhunderten_der_christlichen_Zeitrechnung_1/alto";
		
	    File altoFile = new File("C:/Neuer Ordner/Briefe_aus_allen_Jahrhunderten_der_christlichen_Zeitrechnung_1/Briefe_aus_allen_Jahrhunderten_der_christlichen_Zeitrechnung_1/alto/0014_ubr16515_0014.xml");
		File pageDirFile = new File(pageDirPath);
		File pageOutFile = new File(pageDirPath + File.separatorChar + "pageTest.xml");
		
		File imgFile = new File("C:/Neuer Ordner/Briefe_aus_allen_Jahrhunderten_der_christlichen_Zeitrechnung_1/Briefe_aus_allen_Jahrhunderten_der_christlichen_Zeitrechnung_1/0014_ubr16515_0014.jpg");
		Dimension dim = null;
		
		if(imgFile.isFile()) {
			try {
				dim = ImgUtils.readImageDimensions(imgFile);
			} catch (CorruptImageException cie) {
				logger.error("Image is corrupted!", cie);
				//the image dimension can not be read from the downloaded file
			}
		}
		
		File pageXml = LocalDocReader.createPageXml(pageOutFile, true, null, altoFile, 
				null, true, true, false, imgFile.getName(), dim, false);
		
//		logger.debug("Setting up doc loading process...");
//		try {
//
//			
//			TrpDoc doc = LocalDocReader.load("C:\\Users\\lange\\Desktop\\testimages");
//			System.out.print("Logging messages from this / LocalDocReader: ");
//			System.out.println(logger.isDebugEnabled() + " / " +LoggerFactory.getLogger(LocalDocReader.class).isDebugEnabled());
//			System.out.println(doc.toString());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			logger.error(e.toString());
//		}

//		String[] docs = {
		//				BASE + "TrpTestDoc_20131209/"
		//				TEST_DOC1, 
		//				BASE + "Bentham_box_002/",
		//				BASE + "test/JustImages", 
		//				BASE + "test/OneImage", 
		//				BASE + "test/ImagesOldPageXml",
		//				BASE + "test/Schauplatz_test"
		//				BASE + "test/page_xsl_test2"
//		BASE + "test/bsb00089816",
//		BASE + "test/II._ZvS_1908_1.Q"};

		//		//Just 28 images w/o metadata
		//		final String polenTagebuch = "/mnt/dea_scratch/tmp_philip/04_polen_tagebuecher/tagebuch";
		//		//a test document with metadata and 3 pages with PAGE XMLs
		//		final String testDocWithMd = "/mnt/dea_scratch/TRP/TrpTestDoc_20131209_convert/";

//				for (String d : docs) {
//					try {
//						try {
//							TrpDoc doc = LocalDocReader.load(d);
//							System.out.println(doc.toString());
		//					//				writeMdFile(doc.getMd(), d + "new_metadata.xml");
		//
		//									Mets mets = MetsBuilder.buildMets(doc);
		//					//				JaxbUtils.marshalToFile(mets, new File("/tmp/mets.xml"), TrpDocMetadata.class);
		//									JaxbUtils.marshalToSysOut(mets, TrpDocMetadata.class);
//						} catch (UnsupportedFormatException ufe) {
//							logger.error("Caught: " + ufe.getMessage(), ufe);
//							//					PageXmlUtils.updatePageFormat(d);
//						}
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						logger.error(e);
//					}
//				}

		//		try {
		//			try {
		//				TrpDoc doc = LocalDocReader.load(TEST_DOC2);
		//				System.out.println(doc.toString());
		//				//				writeMdFile(doc.getMd(), d + "new_metadata.xml");
		//
		//								Mets mets = MetsBuilder.buildMets(doc);
		//								JaxbUtils.marshalToFile(mets, new File(TEST_DOC2 + "/mets.xml"), TrpDocMetadata.class);
		//								JaxbUtils.marshalToSysOut(mets, TrpDocMetadata.class);
		//			} catch (UnsupportedFormatException ufe) {
		//				logger.error("Caught: " + ufe.getMessage(), ufe);
		//				//					PageXmlUtils.updatePageFormat(d);
		//			}
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			logger.error(e);
		//		}
	}
}
