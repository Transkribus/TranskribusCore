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
//		final String path = "/mnt/dea_scratch/TRP/Schauplatz_Small";
		
		
		TrpDoc doc = LocalDocReader.load(path);
		
		(new PdfExporter()).export(doc, "/tmp/test.pdf",  null, false, false, false, null, false, false, false);

	}
}
