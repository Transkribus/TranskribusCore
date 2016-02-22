package eu.transkribus.core.io;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.TrpDoc;

public class LocalDocReaderTest {
	private static final Logger logger = LoggerFactory.getLogger(LocalDocReaderTest.class);

	private static final String BASE = "/mnt/dea_scratch/TRP/";

	public static final String TEST_DOC1 = BASE + "TrpTestDoc_20140127/";
	public static final String TEST_DOC2 = BASE + "TrpTestDoc_20140508/";
	
	public static void main(String[] args) {
		
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
