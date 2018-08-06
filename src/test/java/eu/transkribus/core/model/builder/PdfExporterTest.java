package eu.transkribus.core.model.builder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import com.itextpdf.text.DocumentException;

import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.builder.pdf.PdfExporter;

public class PdfExporterTest {
	public static void main(String[] args) throws MalformedURLException, DocumentException, IOException, JAXBException, URISyntaxException, InterruptedException{
//		final String path = "/mnt/dea_scratch/TRP/Bentham_baselines";
		final String path = "/mnt/dea_scratch/TRP/Bentham_box_002_GT";
		//final String path = "C:/Neuer Ordner/t2i_test_with_font/doc";
//		final String path = "/mnt/dea_scratch/TRP/Schauplatz_Small";
		
		
		TrpDoc doc = LocalDocReader.load(path);
		
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
