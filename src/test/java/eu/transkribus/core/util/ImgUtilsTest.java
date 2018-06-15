package eu.transkribus.core.util;

import java.awt.Dimension;
import java.awt.Polygon;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.CoordsType;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.util.SebisStopWatch.SSW;

public class ImgUtilsTest {
	private static final Logger logger = LoggerFactory.getLogger(ImgUtilsTest.class);
	
	private static void testImageReaders() {
		
		final String path = "/mnt/dea_scratch/TRP/upload/TRP_DOC_1442297400230";
		File dir = new File(path);
		File[] tiffs = dir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".tiff");
			}
		});
		
//		String[] imgs = new String[] {
////				"/home/sebastianc/Bilder/StAZ-Sign.2-1_032.jpg",
////				"/home/sebastianc/Bilder/StAZ-Sign.2-1_032_bin.tif",
////				"/home/sebastianc/Bilder/star_trek_logo.png",
////				"/home/sebastianc/Bilder/ocr_icon.gif",
//				"/mnt/dea_scratch/tmp_philip/testdoc_kaputt/StAZ-Sign.2-1_001.tif",
//				"/mnt/dea_scratch/tmp_philip/testdoc_kaputt/StAZ-Sign.2-1_002.tif",
//				"/mnt/dea_scratch/tmp_philip/testdoc_kaputt/StAZ-Sign.2-1_003.tif",
//				"/mnt/dea_scratch/tmp_philip/testdoc_kaputt/StAZ-Sign.2-1_004.tif",
//		};
		for(File f : tiffs){
//		for (String s : imgs) {
//			logger.debug("READING IMAGE "+s);
			logger.debug("READING IMAGE "+f.getAbsolutePath());
			try {
//				File f = new File(s);
				long t = System.currentTimeMillis();
//				BufferedImage i = ImgUtils.readImage(f);
				t = System.currentTimeMillis() - t;
				logger.debug("read image, time = : "+t);
				t = System.currentTimeMillis();
				Dimension d  = ImgUtils.readImageDimensions(f);
				t = System.currentTimeMillis() - t;
				logger.debug("img dimensions: "+d+", time = "+t);
			} catch (Exception e) {
				e.printStackTrace();
				break;
//				logger.error("EXCEPTION: "+e.getMessage());
			}
		}	
	}
	
	private static void testBorderRemoval() throws IOException, JAXBException{
		File testImg = new File("/mnt/dea_scratch/TRP/test/I._ZvS_1902_4.Q/ZS-I-1902-198 (1).jpg");
		File testXml = new File("/mnt/dea_scratch/TRP/test/I._ZvS_1902_4.Q/page/ZS-I-1902-198 (1).xml");
		// Open the image. 
//		BufferedImage baseImage = ImageIO.read(testImg);
		
		PcGtsType pc = PageXmlUtils.unmarshal(testXml);
		final CoordsType coords = pc.getPage().getPrintSpace().getCoords();
	
		// build printspace polygon
		Polygon p = PageXmlUtils.buildPolygon(coords);
		
		String outPng = "/tmp/output.png";
		
		File out = ImgUtils.killBorder(testImg, p, outPng);
		
//		File bin = NcsrTools.binarize(out, new File("/tmp/bin.tiff"));
//		
//		File reg = NcsrTools.segmentRegions(out, bin, new File("/tmp/reg.xml"));
//		File lines = NcsrTools.segmentLines(bin, reg, new File("/tmp/output.xml"));
	}
	
//	@Test
	public void testExiftoolVsImageIO() throws FileNotFoundException, IOException, TimeoutException, InterruptedException {
		File imgFile = new File("/mnt/dea_scratch/TRP/test/I._ZvS_1902_4.Q/ZS-I-1902-198 (1).jpg");
		SSW sw = new SSW();
		sw.start();
		Dimension dim = ImgUtils.readImageDimensionsWithExiftool(imgFile);
		sw.stop(true);
		sw.start();
		Dimension dim2 = ImgUtils.readImageDimensionsWithImageIO(imgFile);
		sw.stop(true);
	}
	
//	@Test
	public void testReadDimension() throws FileNotFoundException, IOException, TimeoutException, InterruptedException {
		File imgFile = new File("/mnt/dea_scratch/TRP/test/I._ZvS_1902_4.Q/ZS-I-1902-198 (1).jpg");
		SSW sw = new SSW();
		sw.start();
		Dimension dim = ImgUtils.readImageDimensions(imgFile);
		sw.stop(true);
	}
	
	public static void main(String[] args) {
		testImageReaders();
//		try {
//			testBorderRemoval();
//		} catch (IOException | JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
