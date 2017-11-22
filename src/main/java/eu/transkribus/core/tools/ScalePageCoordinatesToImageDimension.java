package eu.transkribus.core.tools;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import eu.transkribus.core.io.util.ExtensionFileFilter;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageTypeUtils;
import eu.transkribus.core.util.PageXmlUtils;

public class ScalePageCoordinatesToImageDimension {
	private static final Logger logger = LoggerFactory.getLogger(ScalePageCoordinatesToImageDimension.class);
	public static void main(String[] args) throws IOException, JAXBException {
		
		heldenbuch600to300dpi();
		
//		final String path = "/media/daten/Dokumente/newseye_testdata/";

//		TrpDoc doc = LocalDocReader.load(path);
		
//		fixAltoFilenames();

//		fixAltoMmToPx(doc);
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
		File input = new File("/tmp/Heldenbuch_600dpi/Heldenbuch_600dpi/page");
		File output = new File("/tmp/Heldenbuch_600dpi/page_edited");
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

			final double imgWidth = p.getWidth();
			final double imgHeight = p.getHeight();

			File f = FileUtils.toFile(p.getCurrentTranscript().getUrl());
			PcGtsType pc = PageXmlUtils.unmarshal(f);
			final double altoWidth = pc.getPage().getImageWidth();
			final double altoHeight = pc.getPage().getImageHeight();

			logger.info("Img: " + imgWidth + "x" + imgHeight + " | ALTO: " + altoWidth + "x" + altoHeight);
			
			double scaleX = (imgWidth / (altoWidth / 100f)) / 100f;
			double scaleY = (imgHeight / (altoHeight / 100f)) / 100f;
			
			logger.info("Scale factor X: " + scaleX);
			logger.info("Scale factor Y: " + scaleY);
			
			TrpPageTypeUtils.applyAffineTransformation(pc.getPage(), 0, 0, scaleX, scaleY, 0);

			PageXmlUtils.marshalToFile(pc, f);
		}

	}

}
