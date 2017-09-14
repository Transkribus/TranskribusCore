package eu.transkribus.core.tools;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.io.FilenameUtils;
import org.dea.util.pdf.PDFTextExtractor;
import org.dea.util.pdf.beans.PDFLine;
import org.dea.util.pdf.beans.PDFPage;
import org.dea.util.pdf.beans.PDFRegion;
import org.dea.util.pdf.beans.PDFString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.model.beans.pagecontent.CoordsType;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPrintSpaceType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpWordType;
import eu.transkribus.core.util.ImgUtils;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.PointStrUtils;

public class Pdf2TrpDoc {
	private static final Logger logger = LoggerFactory.getLogger(Pdf2TrpDoc.class);
	
	static List<File> input;
	static PdfFileFilter filter = new PdfFileFilter();
	
	public static void main(String[] args) {
		if(args.length != 1) {
			return;
		}
		
		File in = new File(args[0]);

		final String name = in.getName();
		
		File outDir = new File("/tmp/");
		outDir.mkdirs();
		
		
		
		try {
			
//			PageImageWriter imgWriter = new PageImageWriter();
//			String imgDirPath = imgWriter.extractImages(in.getAbsolutePath(), outDir.getAbsolutePath());
			
			String imgDirPath = "/tmp/Kurzgefaßte_Geschichte_Statistik_und_Topographie_von_Tirol";
			
			File pageDir = new File(imgDirPath + File.separator + "page");
			pageDir.mkdirs();
			
			TreeMap<String, File> imgs = LocalDocReader.findImgFiles(new File(imgDirPath));
			ArrayList<PDFPage> pages = PDFTextExtractor.processPDF(in.getAbsolutePath());
			
			if(imgs.size() != pages.size()) {
				logger.error("Nr. of image files does not match nr. of text pages!");
				return;
			}
			
			int i = 0;
			for(Entry<String, File> img : imgs.entrySet()) {
				PDFPage pdfPage = pages.get(i++);
				Dimension dim = ImgUtils.readImageDimensions(img.getValue());
				PcGtsType pc = PageXmlUtils.createEmptyPcGtsType(img.getValue(), dim);
				final File xmlOut = new File(pageDir.getAbsolutePath() + File.separator + img.getKey() +".xml");
				
				
				Rectangle printspace = pdfPage.getContentRect();
				if(printspace != null) {
					TrpPrintSpaceType psType = new TrpPrintSpaceType();
					psType.setCoords(rect2Coords(printspace));
					
					TrpPageType pageType = (TrpPageType)pc.getPage();
	//				((ITrpShapeType) pageType).getObservable().setActive(false);
					pageType.setPrintSpace(psType);
					
					for(PDFRegion r : pdfPage.regions) {
						TrpTextRegionType rType = new TrpTextRegionType(pageType);
						rType.setCoords(rect2Coords(r.getRect()));
						rType.setUnicodeText(r.getText(), null);
						
						for(PDFLine l : r.lines) {
							TrpTextLineType lType = new TrpTextLineType(rType);
							lType.setCoords(rect2Coords(l.getRect()));
							lType.setUnicodeText(l.getText(), null);
							
							//TODO
//							TrpBaselineType baseLine = new TrpBaselineType();
//							baseLine.setPoints(l.get);
//							lType.setBaseline(baseLine);
							
//							logger.debug(l.getText());
							
							for(PDFString s : l.strings) {
								TrpWordType wType = new TrpWordType(lType);
								wType.setCoords(rect2Coords(s.getRect()));
								wType.setUnicodeText(s.value, null);
								lType.getWord().add(wType);
							}
							
							rType.getTextLine().add(lType);
						}
						pageType.getRegions().add(rType);
					}
				}
				PageXmlUtils.marshalToFile(pc, xmlOut);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private static CoordsType rect2Coords(Rectangle r) {
		CoordsType coords = new CoordsType();
		coords.setPoints(PointStrUtils.pointsToString(r));
		return coords;
	}
	
	private static class PdfFileFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			if(pathname.isDirectory()) {
				return false;
			}
			
			final String ext = FilenameUtils.getExtension(pathname.getName());
			return ext.equalsIgnoreCase("pdf");
		}
		
	}
}
