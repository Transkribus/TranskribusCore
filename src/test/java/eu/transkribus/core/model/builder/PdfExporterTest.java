package eu.transkribus.core.model.builder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.dea.fimgstoreclient.beans.ImgType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.DocumentException;

import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.builder.pdf.PdfExporter;
import eu.transkribus.core.util.SebisStopWatch.SSW;

public class PdfExporterTest {
	private static final Logger logger = LoggerFactory.getLogger(PdfExporterTest.class);
	
	@Test
	public void testPdfCompression() throws IOException, DocumentException, JAXBException, URISyntaxException, InterruptedException {
//		final String path = "/mnt/dea_scratch/TRP/Bentham_baselines";
		final String path = "/mnt/dea_scratch/TRP/Bentham_box_002_GT";
		//final String path = "C:/Neuer Ordner/t2i_test_with_font/doc";
//		final String path = "/mnt/dea_scratch/TRP/Schauplatz_Small";
		SSW sw = new SSW();
		
		TrpDoc doc = LocalDocReader.load(path);
		
		PdfExporter pe = new PdfExporter();
		
		sw.start();
		//try default export
		File uncompressed = pe.export(doc, "/tmp/test_1.pdf", null, false, false, false, false, false, false, false, null, null, ImgType.view);
		long defaultTime = sw.stop(false);
		
		//try max compression (setter was added for testing only. Filesize diff was minor)
//		pe.setImgCompressionLevel(9);
//		File compressed = pe.export(doc, "/tmp/test_2.pdf", null, false, false, false, false, false, false, false, null, null, ImgType.view);
//		long compressedTime = sw.stop(false);
		
		logger.info("Uncompressed size = {}, time = {}", uncompressed.length(), defaultTime);
//		logger.info("Compressed size = {}, time = {}", compressed.length(), compressedTime);
		
		
		//(new PdfExporter()).export(doc, "/tmp/test.pdf",  null, false, false, false, false, false, false, null, null, null);
		
		//(new PdfExporter()).export(doc, "C:/Neuer Ordner/t2i_test_with_font/no_font_set.pdf", null, false, true, false, false, false, true, null, null);
		//(new PdfExporter()).export(doc, "C:/Neuer Ordner/t2i_test_with_font/arial.pdf",  null, false, true, false, false, false, true, null, "arial");
		//(new PdfExporter()).export(doc, "C:/Neuer Ordner/t2i_test_with_font/arialuni.pdf",  null, false, true, false, false, false, true, null, "arialunicodems");
		//(new PdfExporter()).export(doc, "C:/Neuer Ordner/t2i_test_with_font/freeserif.pdf",  null, false, true, false, false, false, true, null, "FreeSerif");
		//(new PdfExporter()).export(doc, "C:/Neuer Ordner/t2i_test_with_font/junicode.pdf",  null, false, true, false, false, false, true, null, "Junicode");
		//(new PdfExporter()).export(doc, "C:/Neuer Ordner/t2i_test_with_font/noto.pdf",  null, false, true, false, false, false, true, null, "NotoSans-Regular");
		//(new PdfExporter()).export(doc, "C:/Neuer Ordner/t2i_test_with_font/bullshit.pdf",  null, false, true, false, false, false, true, null, "bullshit");
		//(new PdfExporter()).export(doc, "C:/Neuer Ordner/t2i_test_with_font/unifont.pdf",  null, false, true, false, false, false, true, null, "unifont");

	}
}
