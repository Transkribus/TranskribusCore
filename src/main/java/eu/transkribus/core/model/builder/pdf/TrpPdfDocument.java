package eu.transkribus.core.model.builder.pdf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.dea.fimgstoreclient.beans.FimgStoreImgMd;
import org.dea.util.pdf.APdfDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.awt.geom.Line2D;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;

import eu.transkribus.core.model.beans.EdFeature;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.customtags.AbbrevTag;
import eu.transkribus.core.model.beans.customtags.CommentTag;
import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagFactory;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.BaselineType;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent.UnknownRegionType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.RegionTypeUtil;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpBaselineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpElementCoordinatesComparator;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpElementReadingOrderComparator;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpWordType;
import eu.transkribus.core.model.builder.ExportUtils;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.PointStrUtils;


/**
 * Wrapper class for building PDFs from TrpDocuments with Itext.
 * Based on FEP's PDF_Document
 * @author philip and schorsch
 *
 */
public class TrpPdfDocument extends APdfDocument {
	private static final Logger logger = LoggerFactory.getLogger(TrpPdfDocument.class);
	private final boolean useWordLevel;
	private final boolean highlightTags;
	private final boolean doBlackening;
	private final boolean createTitle;
	
	InputStream is1 = this.getClass().getClassLoader().getResourceAsStream("fonts/ARIAL.TTF");
	byte[] rBytes1 = IOUtils.toByteArray(is1);
	InputStream is2 = this.getClass().getClassLoader().getResourceAsStream("fonts/ARIALBD.TTF");
	byte[] rBytes2 = IOUtils.toByteArray(is2);
	InputStream is3 = this.getClass().getClassLoader().getResourceAsStream("fonts/ARIALBI.TTF");
	byte[] rBytes3 = IOUtils.toByteArray(is3);
	InputStream is4 = this.getClass().getClassLoader().getResourceAsStream("fonts/ARIALI.TTF");
	byte[] rBytes4 = IOUtils.toByteArray(is4);
	BaseFont bfArial = BaseFont.createFont("arial.ttf", BaseFont.IDENTITY_H, false, false, rBytes1, null);
	BaseFont bfArialBold = BaseFont.createFont("arialbd.ttf", BaseFont.IDENTITY_H, false, false, rBytes2, null);
	BaseFont bfArialBoldItalic = BaseFont.createFont("arialbi.ttf", BaseFont.IDENTITY_H, false, false, rBytes3, null);
	BaseFont bfArialItalic = BaseFont.createFont("ariali.ttf", BaseFont.IDENTITY_H, false, false, rBytes4, null);
	
	Font fontArial = new Font(bfArial);
	Font fontArialBold = new Font(bfArialBold);
	Font fontArialBoldItalic = new Font(bfArialBoldItalic);
	Font fontArialItalic = new Font(bfArialItalic);
	
	/*
	 * divide page into twelth * twelth regions to have a nice print
	 * first column is divisions
	 * second (0 is x direction, 1 is y direction)
	 */
	float[][] twelfthPoints = new float[13][2];

	protected float scaleFactorX = 1.0f;
	protected float scaleFactorY = 1.0f;
	float lineMeanHeight = 0;
	float prevLineMeanHeight = 0;
	//float overallLineMeanHeight = 0;
	float smallerRegionMaxX = 0;
	//Durchschuss (+ Textgröße = Zeilenabstand)
	private int leading = 3;
	int wordOffset = 0;
	
	java.util.List<java.util.Map.Entry<Line2D,String>> lineAndColorList= new java.util.ArrayList<>();
	
	public TrpPdfDocument(final File pdfFile) throws DocumentException, IOException {
		this(pdfFile, 0, 0, 0, 0, false, false, false, false);
	}
	
	public TrpPdfDocument(final File pdfFile, boolean useWordLevel, boolean highlightTags, boolean doBlackening, boolean createTitle) throws DocumentException, IOException {
		this(pdfFile, 0, 0, 0, 0, useWordLevel, highlightTags, doBlackening, createTitle);
	}
	
	public TrpPdfDocument(final File pdfFile, final int marginLeft, final int marginTop, final int marginBottom, final int marginRight, final boolean useWordLevel, final boolean highlightTags, final boolean doBlackening, boolean createTitle) throws DocumentException, IOException {
		super(pdfFile, marginLeft, marginTop, marginBottom, marginRight);
		this.useWordLevel = useWordLevel;
		this.highlightTags = highlightTags;
		this.doBlackening = doBlackening;
		this.createTitle = createTitle;
	}
	

	@SuppressWarnings("unused")
	public void addPage(URL imgUrl, TrpDoc doc, PcGtsType pc, boolean addAdditionalPlainTextPage, boolean imageOnly, Set<String> selectedTags, FimgStoreImgMd md, boolean doBlackening) throws MalformedURLException, IOException, DocumentException, JAXBException, URISyntaxException {
		
		//FIXME use this only on cropped (printspace) images!!
		java.awt.Rectangle printspace = null;
//		if(pc.getPage() != null && pc.getPage().getPrintSpace() != null){
//			java.awt.Polygon psPoly = PageXmlUtils.buildPolygon(pc.getPage().getPrintSpace().getCoords());
//			printspace = psPoly.getBounds();
//		}
		
		BufferedImage imgBuffer = null;
		InputStream input = imgUrl.openStream();
		imgBuffer = ImageIO.read(input);
	    Graphics2D graph = imgBuffer.createGraphics();
	    graph.setColor(Color.BLACK);
	    
		List<TrpRegionType> regions = pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
		
		//regions should be sorted after their reading order at this point - so no need to resort
		//Collections.sort(regions, new TrpElementCoordinatesComparator<RegionType>());
		int nrOfTextRegions = 0;

		for(RegionType r : regions){
			//TODO add paths for tables etc.
			
			//used later to decide if new page is necessary if there is at least one text region 
			if(r instanceof TextRegionType){
				nrOfTextRegions++;
			}
			else if (r instanceof UnknownRegionType && doBlackening){
				UnknownRegionType urt = (UnknownRegionType) r;
				ITrpShapeType trpShape = (ITrpShapeType) r;
				boolean isBlackening = RegionTypeUtil.isBlackening(trpShape);
				if (isBlackening){
					Rectangle blackRect = (Rectangle) PageXmlUtils.buildPolygon(urt.getCoords().getPoints()).getBounds();
					graph.fillRect((int)blackRect.getMinX(), (int)blackRect.getMinY(), (int)blackRect.getWidth(), (int)blackRect.getHeight());
					
				}
			}
		}
		
		graph.dispose();
		
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ImageIO.write(imgBuffer,"JPEG",baos);
		byte[] imageBytes = baos.toByteArray();
		Image img = Image.getInstance(imageBytes);
		
		baos.close();
		imgBuffer.flush();
		imgBuffer = null;
		
		/*
		 * take resolution from metadata of image store, values in img are not always set
		 */
		if(md != null){
			double resolutionX = (float) md.getXResolution();
			double resolutionY = (float) md.getYResolution();
			//logger.debug("Dpi: " + md.getXResolution());
			img.setDpi((int)resolutionX, (int)resolutionY);
		}
		

//		else{
//		
//		 Image img = Image.getInstance(imgUrl);
//		}
		int cutoffLeft=0;
		int cutoffTop=0;
		
		if(printspace==null) {
			/*
			 * 1 Punkt pro cm  = 2,54 dpi
			 * img.getPlainWidth() = horizontal size in Pixel
			 * img.getPlainHeight() = vertical size in Pixel
			 * img.getDpiX() = resolution of x direction
			 * Size in cm: img.getDpiX() / (img.getDpiX()/2,54)
			 */
//			logger.debug("Horizontal size in cm: img.getPlainWidth() / (img.getDpiX()/2,54): " + img.getPlainWidth() / (img.getDpiX()/2.54));
//			logger.debug("Vertical size in cm: img.getPlainHeight() / (img.getDpiY()/2,54): " + img.getPlainHeight() / (img.getDpiY()/2.54));
			
			

			setPageSize(img);
		} else {
			int width=(int)printspace.getWidth();
			int height=(int)printspace.getHeight();
			setPageSize(new com.itextpdf.text.Rectangle(width, height));
			cutoffLeft=printspace.x;
			cutoffTop=printspace.y;
		}
		
		float xSize;
		float ySize;
		
		//FimgStoreImgMd imgMd = storage.getImageMetadata();
		

		
		/*
		 * calculate size of image with respect to Dpi of the image and the default points of PDF which is 72
		 * PDF also uses the same basic measurement unit as PostScript: 72 points == 1 inch
		 */
		if (img.getDpiX() > 72f){
			 xSize = (float) (img.getPlainWidth() / img.getDpiX()*72);
			 ySize = (float) (img.getPlainHeight() / img.getDpiY()*72);
			 scaleFactorX = scaleFactorY = (float) (72f / img.getDpiX());
		}
		else{
			xSize = (float) (img.getPlainWidth() / 300*72);
			ySize = (float) (img.getPlainHeight() / 300*72);
			scaleFactorX = scaleFactorY = 72f / 300;
		}
		
		/*
		 * construct the grid for the added page
		 */
        for (int i=0; i<=12; i++)
        {
                twelfthPoints[i][0] = i*(img.getPlainWidth()/12);
                twelfthPoints[i][1] = i*(img.getPlainHeight()/12);
        }
		
        //TODO use scaleToFit instead?
		img.scaleAbsolute(xSize, ySize);
		img.setAbsolutePosition(0, 0);
		
		/*
		 * calculate physical size of image in inch and assign text size dependent on these values
		 */
		if (img.getScaledWidth()/72f < 9 && img.getScaledHeight()/72f < 12){
			lineMeanHeight = 12/scaleFactorY;
		}
		else{
			lineMeanHeight = 17/scaleFactorY;
		}
		
//		logger.debug("img scaled width: " + img.getScaledWidth());
//		logger.debug("img scaled heigth: " + img.getScaledHeight());
		//System.in.read();

		//img.scalePercent(72f / img.getDpiX() * 100);
		//img.setAbsolutePosition(0, 0);
		//document.setPageSize(new Rectangle(img.getPlainWidth()*2, img.getPlainHeight()));
		
		if(doc != null && createTitle){
			addTitlePage(doc);
			//logger.debug("page number " + getPageNumber());
			if (getPageNumber()%2 != 0){
				logger.debug("odd page number -> add one new page");
				document.newPage();
				//necessary that an empty page can be created
				writer.setPageEmpty(false);
			}
		}
		
		document.newPage();
		addTextAndImage(pc ,cutoffLeft,cutoffTop, img, imageOnly);	
		
		if(addAdditionalPlainTextPage){

			if (nrOfTextRegions > 0){
				document.newPage();			
				addUniformText(pc ,cutoffLeft,cutoffTop);
			}
		}
	}
	


	private void addTextAndImage(PcGtsType pc, int cutoffLeft, int cutoffTop, Image img, boolean imageOnly) throws DocumentException, IOException {
		lineAndColorList.clear();
		
		PdfContentByte cb = writer.getDirectContentUnder();

		cb.setColorFill(BaseColor.BLACK);
		cb.setColorStroke(BaseColor.BLACK);
		//BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, "UTF-8", BaseFont.NOT_EMBEDDED);
		if (!imageOnly){
			cb.beginLayer(ocrLayer);
			cb.setFontAndSize(bfArial, 32);
					
			List<TrpRegionType> regions = pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
			Collections.sort(regions, new TrpElementCoordinatesComparator<RegionType>());
	
			for(RegionType r : regions){
				//TODO add paths for tables etc.
				if(r instanceof TextRegionType){
					TextRegionType tr = (TextRegionType)r;
					PageXmlUtils.buildPolygon(tr.getCoords().getPoints()).getBounds().getMinX();
					addTextFromTextRegion(tr, cb, cutoffLeft, cutoffTop, bfArial);
				}
			}
			
			//scale after calculating lineMeanHeightForAllRegions
			//lineMeanHeight = lineMeanHeight/scaleFactorX;
			
			cb.endLayer();
		}
				
		cb.beginLayer(imgLayer);		
		cb.addImage(img);
		cb.endLayer();
		
		if (highlightTags){
			
			highlightAllTagsOnImg(lineAndColorList, cb, cutoffLeft, cutoffTop);
		}
		
		/*
		 * draw tag lines
		 */
		

		
//		addTocLinks(doc, page,cutoffTop);
	}
	
	private void addUniformText(PcGtsType pc, int cutoffLeft, int cutoffTop) throws DocumentException, IOException {
		PdfContentByte cb = writer.getDirectContentUnder();
		cb.setColorFill(BaseColor.BLACK);
		cb.setColorStroke(BaseColor.BLACK);
	    /** The path to the font. */
	    //FontFactory.register("c:/windows/fonts/arialbd.ttf");
		//BaseFont bf = BaseFont.createFont("/fonts/arialbd.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		
		cb.beginLayer(ocrLayer);
		//FontFactory.register("arialbd.ttf", "my_bold_font");
		//Font fontTest = FontFactory.getFont("arialbd.ttf", Font.BOLDITALIC);
		
		cb.setFontAndSize(bfArial, 10);
				
		List<TrpRegionType> regions = pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
		Collections.sort(regions, new TrpElementCoordinatesComparator<RegionType>());
		
		float textBlockXStart = 0;

		for(RegionType r : regions){
			//TODO add paths for tables etc.
			if(r instanceof TextRegionType){
				TextRegionType tr = (TextRegionType)r;
				
				//compute average text region start
				//textBlockXStart = (float) (PageXmlUtils.buildPolygon(tr.getCoords().getPoints()).getBounds().getMinX());
				double minX = PageXmlUtils.buildPolygon(tr.getCoords().getPoints()).getBounds().getMinX();
				
				if (hasSmallerColumn(regions, tr)){
					
					//logger.debug("several columns found, minX of text region is : " + minX);
					float startWithThisX = (float) (minX < smallerRegionMaxX ? smallerRegionMaxX : minX);
					textBlockXStart = getPrintregionStartX((float) (startWithThisX));
				}
				//if there is only one text region at this vertical section start the text block at the second thwelfth
				else{
					//logger.debug("just one column column");
					if (minX < twelfthPoints[1][0]){
						textBlockXStart = twelfthPoints[1][0];
					}
					//if textregion contains only one line this is probably a headline
					else if (tr.getTextLine().size() == 1){
						textBlockXStart = getPrintregionStartX((float) (minX));
					}
					else{
						textBlockXStart = twelfthPoints[2][0];
					}
				}
				
				addUniformTextFromTextRegion(tr, cb, cutoffLeft, cutoffTop, bfArial, textBlockXStart);
			}
		}
		
		cb.endLayer();	
		
//		addTocLinks(doc, page,cutoffTop);
	}
	
	private float getPrintregionStartX(float textBlockX) {
		
		float shortestDistance = 0;
		float closestX = twelfthPoints[2][0];
		int j = -1;

        for (int i=2; i<=12; i++)
        {

        	if (textBlockX < twelfthPoints[i][0]){
//                logger.debug("twelfthPoints[i][0]: " + twelfthPoints[i][0]);
//                logger.debug("The closest x of this textRegion is " + closestX);
        		j++;
                float distance = twelfthPoints[i][0] - textBlockX;

                //set shortestDistance and closestPoints to the first iteration
                if (j == 0)
                {
                    shortestDistance = distance;
                    closestX = twelfthPoints[i][0];
                }
                //then check if any further iterations have shorter distances
                else if (distance < shortestDistance)
                {
                    shortestDistance = distance;
                    if(i==12){
                    	closestX = twelfthPoints[i-1][0];
                    }
                    else{
                    	closestX = twelfthPoints[i][0];
                    }
                }
        	}
            
        }
//        logger.debug("The shortest distance is: " + shortestDistance);
//        logger.debug("The closest x of this textRegion is " + closestX);
//        try {
//			System.in.read();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
        return closestX;
        

    }		
	

	private void addUniformTextFromTextRegion(final TextRegionType tr, final PdfContentByte cb, int cutoffLeft, int cutoffTop, BaseFont bf, float lineStartX) throws IOException, DocumentException {
		List<TextLineType> lines = tr.getTextLine();
		if(lines != null && !lines.isEmpty()){
			int i = 0;
			float lineStartY = 0;
			
			//sort according to reading order
			Collections.sort(lines, new TrpElementReadingOrderComparator<TextLineType>(true));
			
			double minY = 0;
			double maxY = 0;
			
			//get min and max values of region y direction for later calculation of textline height
			//java.awt.Rectangle regionRect = PageXmlUtils.buildPolygon(tr.getCoords().getPoints()).getBounds();
			
			int maxIdx = lines.size()-1;
			java.awt.Rectangle firstLineRect = PageXmlUtils.buildPolygon(lines.get(0).getCoords().getPoints()).getBounds();
			java.awt.Rectangle lastLineRect = PageXmlUtils.buildPolygon(lines.get(maxIdx).getCoords().getPoints()).getBounds();
			
			double firstLineRotation = computeRotation((TrpBaselineType) lines.get(0).getBaseline());
			double lastLineRotation = computeRotation((TrpBaselineType) lines.get(maxIdx).getBaseline());
			
			//first and last line rotated -> seems to be vertical
			//use X coords to compute the total line gap
			if (firstLineRotation == 90 && lastLineRotation == 90){
				
				//since the reading order is not clear if the text is vertically -> could be right to left or vice versa
				double tmpMinX1 = firstLineRect.getMinX();
				double tmpMinX2 = lastLineRect.getMinX();
				
				double tmpMaxX1 = firstLineRect.getMaxX();
				double tmpMaxX2 = lastLineRect.getMaxX();
				
				minY = Math.min(tmpMinX1, tmpMinX2);
				maxY = Math.max(tmpMaxX1, tmpMaxX2);
			}
			else{
			
				minY = firstLineRect.getMinY();
				maxY = lastLineRect.getMaxY();
			}
			
			/*
			 * if start of line is too tight on the upper bound - set to the first 1/12 of t page from above
			 * Is not good since page number and other informations are often in this section
			 */
//			if (minY < twelfthPoints[1][1]){
//				minY = twelfthPoints[1][1];
//			}
			
			
//			for(TextLineType lt : lines){
//				
//				TrpTextLineType l = (TrpTextLineType)lt;
//				java.awt.Rectangle lineRect = PageXmlUtils.buildPolygon(l.getCoords().getPoints()).getBounds();
//				
//
//				
//				if (lines.size() == 1){
//					minY = lineRect.getMinY();
//					maxY = lineRect.getMaxY();
//					
//				}
//				else if (l.getIndex() == 0){
//					minY = lineRect.getMinY();
//				}
//				else if (l.getIndex() == lines.size()-1){
//					maxY = lineRect.getMaxY();
//				}
//				
//			}
			
			double lineGap = (maxY - minY)/lines.size();
			
			//use default values if only one line  and no previous line mean height computed
			if (lines.size() == 1){
				lineMeanHeight = (prevLineMeanHeight != 0 ? prevLineMeanHeight : lineMeanHeight); 
			}
			else if (lines.size() > 1){
				lineMeanHeight = (float) (2*(lineGap/3));
				leading = (int) (lineGap/3);
				prevLineMeanHeight = lineMeanHeight;
				//logger.debug("Line Mean Height for Export " + lineMeanHeight);
				//overallLineMeanHeight = ( (overallLineMeanHeight != 0) ? overallLineMeanHeight+lineMeanHeight/2 : lineMeanHeight);
			}
			
			logger.debug("Line Mean Height for Export " + lineMeanHeight);
			
			for(TextLineType lt : lines){

				wordOffset = 0;
				TrpTextLineType l = (TrpTextLineType)lt;
				TrpBaselineType baseline = (TrpBaselineType) l.getBaseline();

				java.awt.Rectangle lineRect = PageXmlUtils.buildPolygon(l.getCoords().getPoints()).getBounds();
				java.awt.Rectangle baseLineRect = baseline == null ? null : PageXmlUtils.buildPolygon(baseline.getPoints()).getBounds();
				
				float tmpLineStartX = lineStartX;
				float regionStartMinX = (float) PageXmlUtils.buildPolygon(tr.getCoords().getPoints()).getBounds().getMinX();
				//first line
				if (i == 0){
					
					lineStartY = (float) (minY + lineMeanHeight);

					/*
					 * if first line of a text region is indented then take this into account in printed text
					 */
					if (lineRect.getMinX() > regionStartMinX){
						if (lineRect.getMinX() - regionStartMinX > twelfthPoints[2][0]){
							tmpLineStartX = (float) lineStartX + twelfthPoints[1][0];
						}
					}
					
				}
				
				//for subsequent lines
				else{
					lineStartY = lineStartY + lineMeanHeight + leading;
					
					
//					for (TrpTextRegionType region : tr.getPage().getTextRegions(true)){
//
//						double regionMinX = PageXmlUtils.buildPolygon(region.getCoords().getPoints()).getBounds().getMinX();
//						double regionMaxX = PageXmlUtils.buildPolygon(region.getCoords().getPoints()).getBounds().getMaxX();
//						Rectangle rec = PageXmlUtils.buildPolygon(region.getCoords().getPoints()).getBounds();
//
//						if (rec.contains(tmpLineStartX, lineStartY) && !tr.getId().equals(region.getId()) && tmpLineStartX < regionMaxX){
//							logger.debug("region contains point " + tr.getId() + " region ID " + region.getId());
//							tmpLineStartX = (float) regionMaxX;
//							break;
//						}
//					
//						
//					}
					
					
//					if (lineRect.getMinX() > lineStartX){
//						if (lineRect.getMinX() - lineStartX > twelfthPoints[1][0]){
//							tmpLineStartX = (float) lineRect.getMinX();
//						}
//					}
					
				}
				
				if(baseLineRect != null && regionStartMinX < baseLineRect.getMinX() && (baseLineRect.getMinX()-regionStartMinX) > twelfthPoints[1][0]){
					
					//logger.debug("try to find smaller region for baseline !!!!!!! " );
					for (TrpTextRegionType region : tr.getPage().getTextRegions(false)){

						if (!region.getId().equals(tr.getId())){

							double regionMinX = PageXmlUtils.buildPolygon(region.getCoords().getPoints()).getBounds().getMinX();
							double regionMaxX = PageXmlUtils.buildPolygon(region.getCoords().getPoints()).getBounds().getMaxX();
							double regionMinY = PageXmlUtils.buildPolygon(region.getCoords().getPoints()).getBounds().getMinY();
							double regionMaxY = PageXmlUtils.buildPolygon(region.getCoords().getPoints()).getBounds().getMaxY();
							double meanX = regionMinX+(regionMaxX-regionMinX)/2;
	
							//Rectangle rec = PageXmlUtils.buildPolygon(region.getCoords().getPoints()).getBounds();
							
//							logger.debug("meanX " + meanX);
//							logger.debug("regionStartMinX " + regionStartMinX);
//							logger.debug("baseLineRect.getMinX() " + baseLineRect.getMinX());
//							
//							logger.debug("regionMaxY " + regionMaxY);
//							logger.debug("baseLineRect.getMaxY() " + baseLineRect.getMaxY());
//							
//							logger.debug("baseLineRect.getMinY() " + baseLineRect.getMinY());
//							logger.debug("regionMinY " + regionMinY);
													
							//another region before the lines 
							if (meanX > regionStartMinX && meanX < baseLineRect.getMinX() && baseLineRect.getMinY() < regionMaxY && baseLineRect.getMinY() > regionMinY ){
								tmpLineStartX = (float) regionMaxX + lineMeanHeight;
								logger.debug("region " + region.getId() + " overlaps this other region " + tr.getId());
								//logger.debug("new tmplineStartX is " + regionMaxX);
								break;
							}	
						}
						
					}
					
					//tmpLineStartX = (float) baseLineRect.getMinX();
				}
				
				i++;

				/*
				 * word level bei uniform output nicht sinnvoll?
				 * besser nur ganze lines ausgeben
				 */
//				if(useWordLevel && !l.getWord().isEmpty()){
//					List<WordType> words = l.getWord();
//					for(WordType wt : words){
//						TrpWordType w = (TrpWordType)wt;
//						if(!w.getUnicodeText().isEmpty()){
//							java.awt.Rectangle boundRect = PageXmlUtils.buildPolygon(w.getCoords()).getBounds();
//							
//							addUniformString(boundRect, lineMeanHeight, lineStartX, lineStartY, w.getUnicodeText(), cb, cutoffLeft, cutoffTop, bf);
//						} else {
//							logger.info("No text content in word: " + w.getId());
//						}
//					}
//				} else if(!l.getUnicodeText().isEmpty()){
				
				
				/*
				 * make chunks out of the lineText
				 * so it is possible to have differnt fonts, underlines and other text styles in one line
				 * 
				 * possible text styles are:
				 * 		new CustomTagAttribute("fontFamily", true, "Font family", "Font family"),
						new CustomTagAttribute("serif", true, "Serif", "Is this a serif font?"),
						new CustomTagAttribute("monospace",true, "Monospace", "Is this a monospace (i.e. equals width characters) font?"),
						new CustomTagAttribute("fontSize", true, "Font size", "The size of the font in points"),
						new CustomTagAttribute("kerning", true, "Kerning", "The kerning of the font, see: http://en.wikipedia.org/wiki/Kerning"),
						new CustomTagAttribute("textColour", true, "Text colour", "The foreground colour of the text"),
						new CustomTagAttribute("bgColour", true, "Background colour", "The background colour of the text"),
						new CustomTagAttribute("reverseVideo", true, "Reverse video", "http://en.wikipedia.org/wiki/Reverse_video"),
						new CustomTagAttribute("bold", true, "Bold", "Bold font"),
						new CustomTagAttribute("italic", true, "Italic", "Italic font"),
						new CustomTagAttribute("underlined", true, "Underlined", "Underlined"),
						new CustomTagAttribute("subscript", true, "Subscript", "Subscript"),
						new CustomTagAttribute("superscript", true, "Superscript", "Superscript"),
						new CustomTagAttribute("strikethrough", true, "Strikethrough", "Strikethrough"),
						new CustomTagAttribute("smallCaps", true, "Small caps", "Small capital letters at the height as lowercase letters, see: http://en.wikipedia.org/wiki/Small_caps"),
						new CustomTagAttribute("letterSpaced", true, "Letter spaced", "Equals distance between characters, see: http://en.wikipedia.org/wiki/Letter-spacing"),
				 */ 
				List<Chunk> chunkList = new ArrayList<Chunk>();

				/*
				 * if line is empty -> use the words of this line as line text
				 * otherwise take the text in the line
				 */
				List<TextStyleTag> styleTags = new ArrayList<TextStyleTag>();
				
				if(l.getUnicodeText().isEmpty() || useWordLevel){
					//logger.debug("in word based path " + useWordLevel);
					List<WordType> words = l.getWord();
					
					int chunkIndex = 0;
					for(WordType wt : words){
						TrpWordType w = (TrpWordType)wt;
						String lineText = "";
						//add empty space after each word
						if (chunkIndex > 0){
							chunkList.add(chunkIndex, new Chunk(" "));
							chunkIndex++;
						}
						if(!w.getUnicodeText().isEmpty()){
							//remember all style tags for text formatting later on
							styleTags.addAll(w.getTextStyleTags());
							if (!lineText.equals("")){
								lineText = lineText.concat(" ");
							}
							lineText = lineText.concat(w.getUnicodeText());	
							for (int j=0; j<lineText.length(); ++j) {
								
								String currentCharacter = lineText.substring(j, j+1);

								chunkList.add(chunkIndex, formatText(currentCharacter, styleTags, j, w));
								chunkIndex++;

							}
							styleTags.clear();
						}
					}
				}
				else if (!l.getUnicodeText().isEmpty()){
					String lineText = l.getUnicodeText();
					//logger.debug("line Text is " + lineText);
					styleTags.addAll(l.getTextStyleTags());
					for (int j=0; j<lineText.length(); ++j) {
						
						String currentCharacter = lineText.substring(j, j+1);

						chunkList.add(j, formatText(currentCharacter, styleTags, j, l));

					}
				}
				
				Phrase phrase = new Phrase();
				phrase.addAll(chunkList);
				
				//logger.debug("curr phrase is: " + phrase.getContent());
				
				
				//compute rotation of text, if rotation higher PI/16 than rotate otherwise even text
				double rotation = (baseline != null ? computeRotation(baseline) : 0);
				
				if(rotation != 0){
					/*
					 * if we rotate e.g. 90° than we should use the actual x location of the line
					 * so vertical text must be treated different than horizontal text 
					 */
					if (baseLineRect != null){
						tmpLineStartX = baseLineRect.x;
						lineStartY = (float) baseLineRect.getMaxY();
					}
					else if(lineRect != null){
						tmpLineStartX = lineRect.x;
						lineStartY = (float) lineRect.getMaxY();
					}
					
				}

				//blacken Strings if wanted
//				Set<Entry<CustomTag, String>> blackSet = CustomTagUtils.getAllTagsOfThisTypeForShapeElement(l, RegionTypeUtil.BLACKENING_REGION.toLowerCase()).entrySet();
//				
//				if (!lineText.equals("") && doBlackening && blackSet.size() > 0){
//					
//					//for all blackening regions replace text with ****
//					for (Map.Entry<CustomTag, String> currEntry : blackSet){
//						
//						if (!currEntry.getKey().isIndexed()){
//							//logger.debug("line not indexed : " + lineText);
//							lineText = lineText.replaceAll(".", "*");
//						}
//						else{		
//							lineText = blackenString(currEntry, lineText);
//							//logger.debug("lineText after blackened : " + lineText);
//						}
//					}
//				}

				//first add uniform String (=line), ,after that eventaully highlight the tags in this line using the current line information like x/y position, 
				//addUniformString(lineMeanHeight, tmpLineStartX, lineStartY, lineText, cb, cutoffLeft, cutoffTop, bf, twelfthPoints[1][0], false, null, rotation);
				addUniformString(lineMeanHeight, tmpLineStartX, lineStartY, phrase, cb, cutoffLeft, cutoffTop, bf, twelfthPoints[1][0], false, null, rotation);
				
				/*
				 * old:
				 * highlight all tags of this text line if property is set
				 * no highlighting is done during chunk formatting and not in an extra step
				 */
//				if (highlightTags){
//		
//
//					Set<Entry<CustomTag, String>> entrySet = CustomTagUtils.getAllTagsForShapeElement(l).entrySet();
//					
//					highlightUniformString(entrySet, tmpLineStartX, lineStartY, l, cb, cutoffLeft, cutoffTop, bf);
//					
//					List<WordType> words = l.getWord();
//					for(WordType wt : words){
//						TrpWordType w = (TrpWordType)wt;
//						
//						Set<Entry<CustomTag, String>> entrySet2 = CustomTagUtils.getAllTagsForShapeElement(w).entrySet();
//						
//						highlightUniformString(entrySet2, tmpLineStartX, lineStartY, l, cb, cutoffLeft, cutoffTop, bf);
//					}					
//
//				}

			}
		}	
	}
	
	
	private double computeRotation(TrpBaselineType baseline) {
		
		double rotation = 0;
		List<Point> lp;
		try {
			
			lp = PointStrUtils.parsePoints(baseline.getCoordinates());
	
			Point p2 = lp.get(lp.size()-1);
			Point p1 = lp.get(0);
			
			double gk = Math.abs(p2.getY() - p1.getY());
			double ak = Math.abs(p2.getX() - p1.getX());
			double alpha = (Math.atan2(gk, ak));
			
			if(p1.y < p2.y){
				alpha = -alpha;
			}
			
	//		logger.debug("p1.y " + p1.y);
	//		logger.debug("p2.y " + p2.y);
	//		
	//		logger.debug("Rotate this content? " + phrase.getContent());
	//		logger.debug("alpha to rotate " + alpha);
			
			// if rotation is not over this border keep it straight
			if (Math.abs(alpha) > Math.PI/16){
				//convert from RAD to DEG
				rotation = alpha*57.296;
			}
			
			//with this steepness we change to a 90° rotation
			if (Math.abs(alpha) > 7*Math.PI/16){
	//			tmpLineStartX = p1.x;
	//			lineStartY = p1.y;
				rotation = 90;
			}
					
	//					if (alpha > Math.PI/8 && alpha <= Math.PI/3){
	//						rotation = alpha;
	//					}
	//					//=90° in degrees
	//					else if (alpha > Math.PI/3){ 
	//						rotation = Math.(Math.PI/9);
	//					}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rotation;
	}

	private Chunk formatText(String currCharacter, List<TextStyleTag> styleTags, int currentIndex, ITrpShapeType currShape) throws IOException {
		
		//first blacken char if needed
		Set<Entry<CustomTag, String>> blackSet = ExportUtils.getAllTagsOfThisTypeForShapeElement(currShape, RegionTypeUtil.BLACKENING_REGION.toLowerCase()).entrySet();
		
		if (!currCharacter.equals("") && doBlackening && blackSet.size() > 0){

			//for all blackening regions replace text with ****
			for (Map.Entry<CustomTag, String> currEntry : blackSet){
				
				int beginIndex = currEntry.getKey().getOffset();
				int endIndex = beginIndex + currEntry.getKey().getLength();
				
				if(currentIndex >= beginIndex && currentIndex < endIndex){
					currCharacter = "*";
				}
			}
		}
		
		//create new chunk
		Chunk currChunk = new Chunk(currCharacter);
//		Font arial = new Font(bfArial, lineMeanHeight);
//		Font arialBold = new Font(bfArialBold, lineMeanHeight);
//		Font arialItalic = new Font(bfArialItalic, lineMeanHeight);
		currChunk.setFont(fontArial);
		
		Set<Entry<CustomTag, String>> commentSet = ExportUtils.getAllTagsOfThisTypeForShapeElement(currShape, "comment").entrySet();
		for (Map.Entry<CustomTag, String> currEntry : commentSet){
			
			int beginIndex = currEntry.getKey().getOffset();
			int endIndex = beginIndex + currEntry.getKey().getLength();
			
			if(currentIndex >= beginIndex && currentIndex < endIndex){
				//hex string #FFF8B0: yellow color
				currChunk.setBackground(new BaseColor(Color.decode("#FFF8B0").getRGB()));
			}
		}
//		TrpWordType w = null;
//		if (useWordLevel){
//			List<WordType> words = currShape.getWord();
//
//			for(WordType wt : words){
//				w = (TrpWordType)wt;
////				logger.debug("index " + j);
////				logger.debug("word " + w.getUnicodeText());
//				if(!w.getUnicodeText().isEmpty()){
//					if (!(currentIndex < (wordOffset+w.getUnicodeText().length()+1))){
//						wordOffset = wordOffset+w.getUnicodeText().length()+1;
//						
//					}
//					break;
//				}
//			}
//		}
//		
//		logger.debug("curr offset und index " + wordOffset + " " + currentIndex);
		

		
		
		/*
		 * format according to custom style tag - check for each char in the text if a special style should be set
		 */
		for (TextStyleTag styleTag : styleTags){
			
			if (currentIndex >= (wordOffset+styleTag.getOffset()) && currentIndex < (wordOffset+styleTag.getOffset()+styleTag.getLength())){
								
				if (CoreUtils.val(styleTag.getBold())) {
					//logger.debug("BOOOOOOOOOLD");
					
					currChunk.setFont(fontArialBold);
				}			
				if (CoreUtils.val(styleTag.getItalic())) {
					//logger.debug("ITAAAAAAAAAAAALIC");
					currChunk.setFont(fontArialItalic);
				}
				if (CoreUtils.val(styleTag.getStrikethrough())) {
					//logger.debug("Striiiiiiiiikethrough");
					currChunk.setUnderline(0.2f, 3f);
				}
//				if (CoreUtils.val(ts.isSubscript())) {
//					text = RtfText.subscript(text);
//				}
//				if (CoreUtils.val(ts.isSuperscript())) {
//					text = RtfText.superscript(text);
//				}
				if (CoreUtils.val(styleTag.getUnderlined())) {
					//logger.debug("Underliiiiiiined");
					currChunk.setUnderline(0.2f, -3f);
				}
				
			}

		}
		
		if (highlightTags){
			
			Set<Entry<CustomTag, String>> entrySet;
			entrySet = ExportUtils.getAllTagsForShapeElement(currShape).entrySet();
			
			
			int k = 1;
			int tagId = 0;
			int [] prevLength = new int[entrySet.size()];
			int [] prevOffset = new int[entrySet.size()];
			
			for (Map.Entry<CustomTag, String> currEntry : entrySet){
				
				Set<String> wantedTags = ExportUtils.getOnlyWantedTagnames(CustomTagFactory.getRegisteredTagNames());
				
				if (wantedTags.contains(currEntry.getKey().getTagName())){

//					logger.debug("current tag name "+ currEntry.getKey().getTagName());
//					logger.debug("current tag text "+ currEntry.getKey().getContainedText());
					String color = CustomTagFactory.getTagColor(currEntry.getKey().getTagName());
										
					int currLength = currEntry.getKey().getLength();
					int currOffset = wordOffset+currEntry.getKey().getOffset();

					if (color!=null && currentIndex >= (currOffset) && currentIndex <= (currOffset+currLength)){
						
						/**
						 * if the current tag overlaps one of the previous tags
						 * -> increase the distance of the line under the textline
						 */
						if (isOverlaped(prevOffset, prevLength, currOffset, currLength)){
							k++;
							//logger.debug("overlapped is true, k = " + k);
							
						}
						else{
							k=1;
							//logger.debug("overlapped is not true, k = " + k);
							
						}
						
						currChunk.setUnderline(new BaseColor(Color.decode(color).getRGB()), 0.8f, 0.0f, -2f*+1f*k, 0.0f, PdfContentByte.LINE_CAP_BUTT);
						
						//logger.debug("UNDERLINE curr chunk " + currChunk.getContent() + " k = " + k);
					}
										
					prevOffset[tagId] = currOffset;
					prevLength[tagId] = currLength;
					tagId++;
					

					
					//yShift -> vertical shift of underline if several tags are at the same position
					//float yShift = (lineMeanHeight/6) * k;
				}
			}
			
		}
	
		//logger.debug("chunk content is " + currChunk.getContent());
		return currChunk;
	}
	
	
	//not used anymore
	private void highlightUniformString(Set<Entry<CustomTag, String>> entrySet, float tmpLineStartX, float lineStartY, TrpTextLineType l, PdfContentByte cb, int cutoffLeft, int cutoffTop, BaseFont bf) throws IOException {
		
		int k = 1;
		int tagId = 0;
		int [] prevLength = new int[entrySet.size()];
		int [] prevOffset = new int[entrySet.size()];
		
		for (Map.Entry<CustomTag, String> currEntry : entrySet){
			
			Set<String> wantedTags = ExportUtils.getOnlyWantedTagnames(CustomTagFactory.getRegisteredTagNames());
			
			if (wantedTags.contains(currEntry.getKey().getTagName())){

				
				//logger.debug("current tag text "+ currEntry.getKey().getContainedText());
				String color = CustomTagFactory.getTagColor(currEntry.getKey().getTagName());
				
				int currLength = currEntry.getKey().getLength();
				int currOffset = currEntry.getKey().getOffset();
				
				/**
				 * if the current tag overlaps one of the previous tags
				 * -> increase the distance of the line under the textline
				 */
				if (isOverlaped(prevOffset, prevLength, currOffset, currLength)){
					k++;
				}
				else{
					k=1;
				}
				
				prevOffset[tagId] = currOffset;
				prevLength[tagId] = currLength;
				tagId++;
				
				//yShift -> vertical shift of underline if several tags are at the same position
				float yShift = (lineMeanHeight/6) * k;
				highlightUniformTagString(lineMeanHeight, tmpLineStartX, lineStartY, l.getUnicodeText(), currEntry.getKey().getContainedText(), cb, cutoffLeft, cutoffTop, bf, twelfthPoints[1][0], color, yShift, currOffset);
			}
		}
		
	}

	/*
	 * to find out if two tags overlap each other
	 */
	private boolean isOverlaped(int[] prevOffset, int[] prevLength,
			int currOffset, int currLength) {
		int currX1 = currOffset;
		int currX2 = currOffset+currLength;
		for (int i = 0; i < prevOffset.length; i++){
			int prevX1 = prevOffset[i];
			int prevX2 = prevOffset[i]+prevLength[i];
			if ( (currX1>=prevX1 && currX1<prevX2) || (currX2 > prevX1 && currX2 <= prevX2) ){
				return true;
			}
		}
		return false;
	}
	
	/*
	 * to find out if two tags overlap each other
	 */
	private int getAmountOfOverlaps(int[] prevOffset, int[] prevLength,
			int currOffset, int currLength) {
		int currX1 = currOffset;
		int currX2 = currOffset+currLength;
		
		int countOverlaps = 1;
		for (int i = 0; i < prevOffset.length; i++){
			int prevX1 = prevOffset[i];
			int prevX2 = prevOffset[i]+prevLength[i];
			if ( (currX1>=prevX1 && currX1<prevX2) || (currX2 > prevX1 && currX2 <= prevX2) ){
				countOverlaps++;
			}
		}
		return countOverlaps;
	}

	private void addTextFromTextRegion(final TextRegionType tr, final PdfContentByte cb, int cutoffLeft, int cutoffTop, BaseFont bf) throws IOException {
		List<TextLineType> lines = tr.getTextLine();
		
		boolean firstLine;
		if(lines != null && !lines.isEmpty()){
			//sort according to reading order

			Collections.sort(lines, new TrpElementCoordinatesComparator<TextLineType>());
			
			double baseLineMeanY = 0;
			double baseLineMeanYPrev = 0;
			double baseLineMeanGap = 0;
			//logger.debug("Processing " + lines.size() + " lines in TextRegion " + tr.getId());
			for(TextLineType lt : lines){
				
				TrpTextLineType l = (TrpTextLineType)lt;
				java.awt.Rectangle lineRect = PageXmlUtils.buildPolygon(l.getCoords().getPoints()).getBounds();
				
				//compute rotation of text, if rotation higher PI/16 than rotate otherwise even text
				TrpBaselineType baseline = (TrpBaselineType) l.getBaseline();
				double rotation = (baseline != null ? computeRotation(baseline) : 0);
				
//				if (lineRect.height > 0){
//					float lineHeight = lineRect.height /3;
//					
//					logger.debug("line height: "+ lineHeight);
//					
//					//ignore actual lineHeigth if three times the size of the actual line mean heigth
//					if (!(lineHeight > lineMeanHeight*4) || lineMeanHeight == 0){
//						//calculate line mean Height
//						lineMeanHeight = (lineMeanHeight == 0 ? lineHeight : (lineMeanHeight + lineHeight)/2);
//						logger.debug("lineMeanHeight: "+ lineMeanHeight);
//					}
//				}
				//get the mean baseline y-value
				baseLineMeanYPrev = baseLineMeanY;
				if(baseline != null){
					//use lowest point in baseline and move up one half of the distance to the topmost point
					java.awt.Rectangle baseLineRect = PageXmlUtils.buildPolygon(l.getBaseline().getPoints()).getBounds();
					baseLineMeanY =  baseLineRect.getMaxY() - ((baseLineRect.getMaxY() - baseLineRect.getMinY())/2);
					if (baseLineMeanYPrev != 0){
						baseLineMeanGap = baseLineMeanY - baseLineMeanYPrev;
					}
				}
					
				if( (l.getUnicodeText().isEmpty() || useWordLevel) && !l.getWord().isEmpty()){

					List<WordType> words = l.getWord();
					for(WordType wt : words){
						TrpWordType w = (TrpWordType)wt;
						if(!w.getUnicodeText().isEmpty()){
							java.awt.Rectangle boundRect = PageXmlUtils.buildPolygon(w.getCoords()).getBounds();
							addString(boundRect, baseLineMeanY, w.getUnicodeText(), cb, cutoffLeft, cutoffTop, bf, rotation);
						} else {
							logger.info("No text content in word: " + w.getId());
						}
						
					}
				} else if(!l.getUnicodeText().isEmpty()){
					
					String lineTextTmp = l.getUnicodeText();
					//get surrounding rectangle coords of this line
					java.awt.Rectangle boundRect = PageXmlUtils.buildPolygon(l.getCoords()).getBounds();
					
					Set<Entry<CustomTag, String>> blackSet = ExportUtils.getAllTagsOfThisTypeForShapeElement(l, RegionTypeUtil.BLACKENING_REGION.toLowerCase()).entrySet();
					
					if (doBlackening && blackSet.size() > 0){
						
						//for all blackening regions replace text with ****
						for (Map.Entry<CustomTag, String> currEntry : blackSet){
							
							if (!currEntry.getKey().isIndexed()){
								//logger.debug("line not indexed : " + lineTextTmp);
								lineTextTmp = lineTextTmp.replaceAll(".", "*");
							}
							else{
								//logger.debug("lineText before blackened : " + lineTextTmp);
								lineTextTmp = blackenString(currEntry, lineTextTmp);
								//logger.debug("lineText after blackened : " + lineTextTmp);

							}
						}
					}

					addString(boundRect, baseLineMeanY, lineTextTmp, cb, cutoffLeft, cutoffTop, bf, rotation);
					/*
					 * highlight all tags of this text line if property is set
					 */
//					if (highlightTags){
//						highlightTagsForShape(l);
//						
//					}

				} else {
					logger.info("No text content in line: " + l.getId());
				}
				
				
				
				if (highlightTags){
					
					if ((l.getUnicodeText().isEmpty() || useWordLevel) && !l.getWord().isEmpty()){
					
						List<WordType> words = l.getWord();
						for(WordType wt : words){
							TrpWordType w = (TrpWordType)wt;
							highlightTagsForShape(w);
						}
					}
					else{
						highlightTagsForShape(l);
					}
					
				}
			}
			

			
		}	
	}
	



	private void highlightTagsForShape(ITrpShapeType shape) throws IOException {
		int tagId = 0;
		int k = 1;
		Set<Entry<CustomTag, String>> entrySet = ExportUtils.getAllTagsForShapeElement(shape).entrySet();
		
		Set<String> wantedTags = ExportUtils.getOnlyWantedTagnames(CustomTagFactory.getRegisteredTagNames());

		int [] prevLength = new int[entrySet.size()];
		int [] prevOffset = new int[entrySet.size()];
		boolean falling = true;
		
		BaselineType baseline = null;
		if (shape instanceof TrpTextLineType){
			TrpTextLineType l = (TrpTextLineType) shape;
			baseline = l.getBaseline();
		}
		else if (shape instanceof TrpWordType){
			TrpWordType w = (TrpWordType) shape;
			TrpTextLineType l = (TrpTextLineType) w.getParentShape();
			baseline = l.getBaseline();
		}
		
		
		try {
			List<Point> ptsList = null;
			if (baseline != null){
				ptsList = PointStrUtils.parsePoints(baseline.getPoints());
			}
			if (ptsList != null){
				int size = ptsList.size();
				//logger.debug("l.getBaseline().getPoints() " + l.getBaseline().getPoints());
				if (size >= 2 && ptsList.get(0).y < ptsList.get(size-1).y){
					//logger.debug("falling is false ");
					falling = false;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Map.Entry<CustomTag, String> currEntry : entrySet){
			
			if (wantedTags.contains(currEntry.getKey().getTagName())){
				

				String color = CustomTagFactory.getTagColor(currEntry.getKey().getTagName());
				
				int currLength = currEntry.getKey().getLength();
				int currOffset = currEntry.getKey().getOffset();
				
				/**
				 * if the current tag overlaps one of the previous tags
				 * -> increase the distance of the line under the textline
				 */
//				if (isOverlaped(prevOffset, prevLength, currOffset, currLength)){
//					k++;
//				}
//				else{
//					k=1;
//				}
				
				k = getAmountOfOverlaps(prevOffset, prevLength, currOffset, currLength);
				
//				logger.debug("current tag name "+ currEntry.getKey().getTagName() + " k is " + k);
//				logger.debug("current tag text "+ currEntry.getKey().getContainedText());
				
				prevOffset[tagId] = currOffset;
				prevLength[tagId] = currLength;
				tagId++;
				
				float yShift = (lineMeanHeight/6) * k;
				/*
				 * remember where to draw line with help of a list
				 */
				if(baseline != null){
					//use lowest point in baseline and move up one half of the distance to the topmost point
					java.awt.Rectangle baseLineRect = PageXmlUtils.buildPolygon(baseline.getPoints()).getBounds();
					calculateTagLines(baseLineRect, shape, currEntry.getKey().getContainedText(), currOffset, currLength, color, yShift, falling);
				}
			}
			
		}
		
	}

	private String blackenString(Entry<CustomTag, String> currEntry, String lineText) {
		int beginIndex = currEntry.getKey().getOffset();
		int endIndex = beginIndex + currEntry.getKey().getLength();
		
//		logger.debug("lineText before : " + lineText);
//		logger.debug("lineText length : " + lineText.length());
//		logger.debug("begin : " + beginIndex);
//		logger.debug("end : " + endIndex);
		
		String beginString = "";
		if (beginIndex > 0)
			beginString = lineText.substring(0, beginIndex);
		String tagString = lineText.substring(beginIndex, endIndex);
		tagString = tagString.replaceAll(".", "*");
		String postString = lineText.substring(endIndex);
		
		return beginString.concat(tagString).concat(postString);
		
	}

	private void calculateTagLines(java.awt.Rectangle baseLineRect, ITrpShapeType shape, String tagText, int offset, int length, String color, float yShift, boolean falling) {
		
		String lineText = shape.getUnicodeText();
		
		java.awt.Rectangle shapeRect = null;
		if (shape instanceof TrpWordType){
			shapeRect = PageXmlUtils.buildPolygon(((TrpWordType) shape).getCoords()).getBounds();
		}
		else{
			shapeRect = baseLineRect;
		}
		
		float shapeMinX = (float) shapeRect.getMinX();
		
		float minX = (float) baseLineRect.getMinX();
		float maxX = (float) baseLineRect.getMaxX();
		
		float minY = (float) baseLineRect.getMinY();
		float maxY = (float) baseLineRect.getMaxY();
		
		float a = maxY - minY;
		float b = maxX - minX;
		
		float angleAlpha = (float) Math.atan(a/b);
		
//		logger.debug("line Text " + lineText);
//		logger.debug("tag text " + tagText);
//		logger.debug("angle alpha " + angleAlpha);
//		
//		logger.debug("offset " + offset);
//		logger.debug("lineText.length() " + lineText.length());
//		logger.debug("offset+length " + offset+length);
				
		//relation of tagStart to entire text length
		float ratioOfTagStart = 0;
		if (offset != 0){
			ratioOfTagStart = (float) offset / (float) lineText.length();
		}
		float ratioOfTagEnd = (float) (offset+length) / (float) lineText.length();
				
		float tagStartX = shapeMinX + (ratioOfTagStart * baseLineRect.width);
		float tagEndX = shapeMinX + (ratioOfTagEnd * shapeRect.width);
		
		float tagStartHeight = 0;
		if (tagStartX != shapeMinX){
			tagStartHeight = (float) (Math.tan(angleAlpha) * (tagStartX-shapeMinX)); 
		}

		float tagEndHeight = (float) (Math.tan(angleAlpha) * (tagEndX-shapeMinX));
		
		float tagStartY;
		float tagEndY;
		
		if (falling){
//			logger.debug("tagStartHeight > tagEndHeight; tagStartY = maxY - tagStartHeight;" + (maxY - tagStartHeight));
//			logger.debug("tagStartHeight > tagEndHeight; tagEndY = maxY - tagEndHeight;" + (maxY - tagEndHeight));
			tagStartY = maxY - tagStartHeight;
			tagEndY = maxY - tagEndHeight;
		}
		else{
			tagStartY = maxY - tagEndHeight;
			tagEndY = maxY - tagStartHeight;
		}

//		logger.debug("tag startX " + tagStartX);
//		logger.debug("tag endX " + tagEndX);
//		
//		logger.debug("tag startY " + tagStartY);
//		logger.debug("tag endY " + tagEndY);
		
		Line2D line = new Line2D.Double(tagStartX, tagStartY + yShift, tagEndX, tagEndY + yShift);
		java.util.Map.Entry<Line2D,String> pair= new java.util.AbstractMap.SimpleEntry<>(line,color);
		lineAndColorList.add(pair);
		
	}

	/*
	 * checks if there is at least one text region on the left of the actual one
	 * But: if text region is completely contained in the other it should not have an effect
	 */
	private boolean hasSmallerColumn(List<TrpRegionType> regions, TextRegionType regionToCompare) throws DocumentException, IOException {
						
		float minX = 0;
		float minY = 0;
		float maxX = 0;
		float maxY = 0;
		float meanX = 0;
		float meanY = 0;
		
		java.awt.Rectangle compareBlock = PageXmlUtils.buildPolygon(regionToCompare.getCoords().getPoints()).getBounds();
		float compareMinX = (float) compareBlock.getMinX();
		float compareMinY = (float) compareBlock.getMinY();
		float compareMaxX = (float) compareBlock.getMaxX();
		float compareMaxY = (float) compareBlock.getMaxY();
		
		float compareMeanX = compareMinX+(compareMaxX - compareMinX)/2;
		float compareMeanY = compareMinY+(compareMaxY - compareMinY)/2;
		
		boolean foundSmallerColumn = false;
		smallerRegionMaxX = 0;
		
//		logger.debug("nr of regions " + regions.size());
//		logger.debug("regionToCompare id " + regionToCompare.getId());
		
		if (regions.size() == 1){
			return false;
		}
		else{

			for(RegionType r : regions){
				//TODO add paths for tables etc.
				if(r instanceof TextRegionType && r.getId() != regionToCompare.getId()){
					TextRegionType tr = (TextRegionType)r;
					
					//empty region can be ignored
					if (tr.getTextLine().isEmpty())
						continue;
					else{
						//region with empty lines can also be ignored
						boolean textFound = false;
						for (TextLineType tlt : tr.getTextLine()){
							TrpTextLineType l = (TrpTextLineType)tlt;
							textFound = !l.getUnicodeText().isEmpty();
							if (textFound){
								break;
							}
						}
						//no text in region -> go to next region
						if (!textFound){
							continue;
						}
					}
					//logger.debug("tr id " + tr.getId());

					//compute average text region start
					java.awt.Rectangle block = PageXmlUtils.buildPolygon(tr.getCoords().getPoints()).getBounds();
					minX = (float) block.getMinX();
					maxX = (float) block.getMaxX();
					minY = (float) block.getMinY();
					maxY = (float) block.getMaxY();
					
					//meanX = minX+(maxX - minX)/2;
					meanY = minY+(maxY - minY)/2;
					
					if ( ( (meanY > compareMinY && meanY < compareMaxY) ||
							(compareMeanY > minY && compareMeanY < maxY) )
							&& (maxX < compareMeanX) ){
						//to find the biggest maxX if there are several smaller columns
						if (maxX > smallerRegionMaxX){
							smallerRegionMaxX = maxX;
						}
						foundSmallerColumn = true;
					}						
	
				}
			}
		}
		return foundSmallerColumn;

	}

	public void addTags(TrpDoc doc, Set<Integer> pageIndices, boolean useWordLevel2, Set<String> selectedTags) throws DocumentException, IOException {
		PdfContentByte cb = writer.getDirectContentUnder();
		document.newPage();
				
		int l = 0;
		float posY;
		//BaseFont bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, "UTF-8", BaseFont.NOT_EMBEDDED, true, null, null);
		
		//logger.debug("selectedTags Size " + selectedTags.size());
		for (String currTagname : selectedTags){
			double lineHeight = 12/scaleFactorY;
			double lineGap = 4/scaleFactorY;
			//logger.debug("currTagname " + currTagname);
			//get all custom tags with currTagname and text
			HashMap<CustomTag, String> allTagsOfThisTagname = ExportUtils.getTags(currTagname);
			//logger.debug("all Tags Of This Tagname " + currTagname);
			if(allTagsOfThisTagname.size()>0){
				
				posY = (float) (twelfthPoints[1][1]+(lineHeight+lineGap)*l);
				if (posY > twelfthPoints[10][1]){
					document.newPage();
					posY = twelfthPoints[1][1];
					l = 0;
				}
					
				l++;
				String color = CustomTagFactory.getTagColor(currTagname);
				

				addUniformTagList(lineHeight, twelfthPoints[1][0], posY, currTagname + " Tags:", "", cb, 0, 0, bfArial, twelfthPoints[1][0], false, color, 0);
				//addUniformStringTest(lineMeanHeight, twelfthPoints[1][0], posY, currTagname + " Tags:", cb, 0, 0, bfArial, twelfthPoints[1][0], false, color, 0);
				
				Collection<String> valueSet = allTagsOfThisTagname.values();
				Collection<CustomTag> keySet = allTagsOfThisTagname.keySet();
				
				HashSet<String> uniqueValues = new HashSet<String>();
				
				Iterator<CustomTag> it = keySet.iterator();

				while (it.hasNext()){
					
					CustomTag currEntry = it.next();
					
					String currValue = allTagsOfThisTagname.get(currEntry);
					String expansion = "";
					
					//handles continued tags over several lines
					while (currEntry.isContinued() && it.hasNext()){
						currEntry = it.next();
						if (currEntry.isContinued()){
							String continued = allTagsOfThisTagname.get(currEntry);
							currValue = currValue.concat(continued);
							
							//soft hyphen
							currValue = currValue.replaceAll("\u00AD", "");
							//minus
							currValue = currValue.replaceAll("\u002D", "");
							//not sign
							currValue = currValue.replaceAll("\u00AC", "");
							
							//char c = 0xFFFA; String.valueOf(c).replaceAll("\\p{C}", "?");
							
						}
					}
					
					if (currTagname.equals(CommentTag.TAG_NAME)){
						
						CommentTag ct = (CommentTag) currEntry;
						if (ct.getComment() != "")
							expansion = ": " + ct.getComment();
						//currValue = currValue.concat(": " + ct.getComment());
						//logger.debug("comment " + currValue);
					}
					
					else if (currTagname.equals(AbbrevTag.TAG_NAME)){
						
						AbbrevTag at = (AbbrevTag) currEntry;
						if (at.getExpansion() != "")
							expansion = ": " + at.getExpansion();
					}
					
					//make sure that similar tags are only exported once
					if (!uniqueValues.contains(currValue)){
						uniqueValues.add(currValue);
						
						posY = (float) (twelfthPoints[1][1]+(lineHeight+lineGap)*l);	
						if (posY > twelfthPoints[11][1]){
							document.newPage();
							posY = twelfthPoints[1][1];
							l = 1;
						}
						addUniformTagList(lineHeight, twelfthPoints[1][0], posY, currValue, expansion, cb, 0, 0, bfArial, twelfthPoints[1][0], true, null, 0);
						//logger.debug("tag value is " + currValue);
						l++;
					}

				}
				
				l++;
				
				
			}
			
		}
		
	}

	public void addTitlePage(TrpDoc doc) {
		document.newPage();
		PdfContentByte cb = writer.getDirectContentUnder();
		
		float lineHeight = twelfthPoints[1][0]/3;
		float posY = twelfthPoints[1][1];
		
		addTitleString("Title Page", posY, 0, (float) (lineHeight*1.5), cb, bfArialBoldItalic);
		posY += lineHeight*2;
		
		TrpDocMetadata docMd = doc.getMd();
		
		if (writeDocMd("Title: ", docMd.getTitle(), posY, 0, lineHeight, cb, bfArialItalic)){
			posY += lineHeight*1.5;
		}
		
		if (writeDocMd("Author: ", docMd.getAuthor(), posY, 0, lineHeight, cb, bfArialItalic)){
			posY += lineHeight*1.5;
		}

		lineHeight = twelfthPoints[1][0]/6;
		
		if (writeDocMd("Description: ", docMd.getDesc(), posY, 0, lineHeight, cb, bfArialItalic)){
			posY += lineHeight*1.2;
		}
		
		if (writeDocMd("Genre: ", docMd.getGenre(), posY, 0, lineHeight, cb, bfArialItalic)){
			posY += lineHeight*1.2;
		}
		
		if (writeDocMd("Writer: ", docMd.getWriter(), posY, 0, lineHeight, cb, bfArialItalic)){
			posY += lineHeight*1.2;
		}
		
		if (docMd.getScriptType() != null){
			if (writeDocMd("Sripttype: ", docMd.getScriptType().toString(), posY, 0, lineHeight, cb, bfArialItalic)){
				posY += lineHeight*1.2;
			}
		}
		
		if (writeDocMd("Language: ", docMd.getLanguage(), posY, 0, lineHeight, cb, bfArialItalic)){
			posY += lineHeight*1.2;
		}
		
		if (writeDocMd("Number of Pages in whole Document: ", String.valueOf(docMd.getNrOfPages()), posY, 0, lineHeight, cb, bfArialItalic)){
			posY += lineHeight*1.2;
		}
		
		if (docMd.getCreatedFromDate() != null){
			if (writeDocMd("Created From: ", docMd.getCreatedFromDate().toString(), posY, 0, lineHeight, cb, bfArialItalic)){
				posY += lineHeight*1.2;
			}
		}
		
		if (docMd.getCreatedToDate() != null){
			if (writeDocMd("Created To: ", docMd.getCreatedToDate().toString(), posY, 0, lineHeight, cb, bfArialItalic)){
				posY += lineHeight*1.5;
			}
		}
		
		lineHeight = twelfthPoints[1][0]/3;
		//posY += lineHeight*1.5;
		
		List<EdFeature> efl = doc.getEdDeclList();
		
		if (efl.size() >= 0){
			addTitleString("Editorial Declaration: ", posY, twelfthPoints[1][0], lineHeight, cb, bfArialBoldItalic);
			posY += lineHeight*1.5;
			
			lineHeight = twelfthPoints[1][0]/6;
		}

		for (EdFeature edfeat : efl){
			addTitleString(edfeat.getTitle() + ": " + edfeat.getDescription() +"\n" + edfeat.getSelectedOption().toString(), posY, twelfthPoints[1][0], lineHeight, cb, bfArial);
			//posY += lineHeight;
			//addTitleString(edfeat.getSelectedOption().toString(), posY, twelfthPoints[1][0], lineHeight, cb, bfArial);
			posY += lineHeight*1.5;
		}
				
		// TODO Auto-generated method stub
		
	}

	private boolean writeDocMd(String mdName, String mdValue, float posYdirection, int horizontalPlacement,
			float lineHeight, PdfContentByte cb, BaseFont bfArialItalic) {
		if (mdValue != null && !mdValue.equals("")){
			
			if (posYdirection > (twelfthPoints[11][1])){
				posYdirection = twelfthPoints[1][1];
			}
			addTitleString(mdName + mdValue, posYdirection, horizontalPlacement, lineHeight, cb, bfArialItalic);
			return true;
		}
		return false;
		
	}



	

}
