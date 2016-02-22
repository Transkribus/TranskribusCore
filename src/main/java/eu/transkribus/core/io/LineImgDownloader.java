package eu.transkribus.core.io;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent_extension.TrpElementCoordinatesComparator;
import eu.transkribus.core.model.builder.TrpPageTranscriptBuilder;
import eu.transkribus.core.util.HtrUtils;
import eu.transkribus.core.util.ImgUtils;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.PageXmlUtils;

import org.dea.fimgstoreclient.FimgStoreGetClient;
import org.dea.fimgstoreclient.beans.FimgStoreImg;

/**
 * @Deprecated this is in HtrManager now!
 */
public class LineImgDownloader {
	private static final Logger logger = LoggerFactory.getLogger(LineImgDownloader.class);
	private static FimgStoreGetClient getter = FimgStoreReadConnection.getGetClient();
	
	public static void extractLineImages(final String path, TrpDoc doc, boolean killBorder) throws Exception {
		File pathF = new File(path);
		FileUtils.forceMkdir(pathF);
		
		for (TrpPage p : doc.getPages()) {
			String subpath = pathF.getAbsolutePath()+"/"+StringUtils.leftPad(""+p.getPageNr(), 4, '0');
			File subpathF = new File(subpath);
			logger.debug("extracting line images for page "+p.getPageNr()+" to "+subpathF.getAbsolutePath());
			FileUtils.forceMkdir(subpathF);
			
			JAXBPageTranscript tr = new JAXBPageTranscript(p.getCurrentTranscript());
			tr.build();
//			JAXBPageTranscript tr = TrpPageTranscriptBuilder.build(p.getCurrentTranscript());
			PcGtsType pc = tr.getPageData();
			
			storeLineImagesLocalAndRemote(subpath, p.getUrl(), p.getKey(), pc, killBorder);
		}
	}
	
	public static void extractLineImages(final String path, final String imgKey, final String xmlKey) throws IOException, JAXBException {
		URI xmlUri = getter.getUriBuilder().getFileUri(xmlKey);
		PcGtsType pc = JaxbUtils.unmarshal(xmlUri.toURL(), PcGtsType.class);
		storeLineImages(path, imgKey, xmlKey, pc);		
	}
		
	/**
	 * Same as storeLineImages but does download whole image from the imgUrl and then crops directly on this image
	 */
	public static void storeLineImagesLocalAndRemote(final String path, final URL imgUrl, final String pageFileId, final PcGtsType pc, boolean doKillBorder) throws IOException{
		if(pc == null) {
			throw new IllegalArgumentException("PcGtsType is null!");
		}
		if(path == null || path.isEmpty()){
			throw new IllegalArgumentException("Path is null or empty!");
		}
		File dir = new File(path);
		if(!dir.exists() || !dir.isDirectory()){
			throw new IllegalArgumentException("Path is not an existing directory: " + path);
		}
		
		// --- LOAD IMAGE:
		BufferedImage mainImg = ImageIO.read(imgUrl);
		
		
		logger.debug("loaded main image");
		
		List<TextRegionType> trList = PageXmlUtils.getTextRegions(pc);
		//sort according to reading order
		Collections.sort(trList, new TrpElementCoordinatesComparator<TextRegionType>());
		logger.debug("Processing " + trList.size() + " TextRegions.");
		TextRegionType tr;
		for(int i = 0; i < trList.size(); i++){
			tr = trList.get(i);
			List<TextLineType> lines = tr.getTextLine();
			if(lines != null && !lines.isEmpty()){
				//sort according to reading order
				Collections.sort(lines, new TrpElementCoordinatesComparator<TextLineType>());
				logger.debug("Processing " + lines.size() + " lines in TextRegion " + tr.getId());
				TextLineType l;
				for(int j = 0; j < lines.size(); j++){
					l = lines.get(j);
					//get surrounding rectangle coords
					Polygon poly = PageXmlUtils.buildPolygon(l.getCoords());
					Rectangle boundRect = poly.getBounds();
					boundRect = ImgUtils.boundRectangle(boundRect, mainImg.getWidth(), mainImg.getHeight());
					
					//get the polygonal area. offset coords w.r.t. rectangle
					Polygon offsetPoly = PageXmlUtils.getOffsetPolygon(poly, boundRect);
					
					//download the rectangular image
					logger.debug("Line image crop: x=" + boundRect.x + ", y=" + boundRect.y + 
							", width=" + boundRect.width + ", height=" + boundRect.height);
//					FimgStoreImg imgData = getter.getImgCropped(imgKey, boundRect.x, boundRect.y, boundRect.width, boundRect.height);
//					BufferedImage img = ImgUtils.readImage(imgData.getData());
					
					BufferedImage img = mainImg.getSubimage(boundRect.x, boundRect.y, boundRect.width, boundRect.height);
					
					//build filename and path
					final String filename = HtrUtils.buildFileName(pageFileId, i, j, tr.getId(), l.getId(), HtrUtils.LINE_IMG_EXT);
					final String filePath = dir.getAbsolutePath() + File.separator + filename;
					
					//kill the border and store result according to name scheme
					
					File lineImg;
					if (doKillBorder) {
						lineImg = ImgUtils.killBorder(img, offsetPoly, filePath);
					}
					else {
				        lineImg = new File(filePath);
				        if(!ImageIO.write(img, "png", lineImg)){
				        	//should not happen with png
				        	throw new IOException("No appropriate writer was found!");
				        }
					}
					
				
					logger.info("Line image written: " + lineImg.getAbsolutePath());
				}
			}
		}
	}
	
	public static void storeLineImages(final String path, final String imgKey, final String pageFileId, final PcGtsType pc) throws IOException{
		if(pc == null) {
			throw new IllegalArgumentException("PcGtsType is null!");
		}
		if(path == null || path.isEmpty()){
			throw new IllegalArgumentException("Path is null or empty!");
		}
		File dir = new File(path);
		if(!dir.exists() || !dir.isDirectory()){
			throw new IllegalArgumentException("Path is not an existing directory: " + path);
		}
		List<TextRegionType> trList = PageXmlUtils.getTextRegions(pc);
		//sort according to reading order
		Collections.sort(trList, new TrpElementCoordinatesComparator<TextRegionType>());
		logger.debug("Processing " + trList.size() + " TextRegions.");
		TextRegionType tr;
		for(int i = 0; i < trList.size(); i++){
			tr = trList.get(i);
			List<TextLineType> lines = tr.getTextLine();
			if(lines != null && !lines.isEmpty()){
				//sort according to reading order
				Collections.sort(lines, new TrpElementCoordinatesComparator<TextLineType>());
				logger.debug("Processing " + lines.size() + " lines in TextRegion " + tr.getId());
				TextLineType l;
				for(int j = 0; j < lines.size(); j++){
					l = lines.get(j);
					//get surrounding rectangle coords
					Polygon poly = PageXmlUtils.buildPolygon(l.getCoords());
					Rectangle boundRect = poly.getBounds();
					
					//get the polygonal area. offset coords w.r.t. rectangle
					Polygon offsetPoly = PageXmlUtils.getOffsetPolygon(poly, boundRect);
					
					//download the rectangular image
					logger.debug("Line image crop: x=" + boundRect.x + ", y=" + boundRect.y + 
							", width=" + boundRect.width + ", height=" + boundRect.height);
					FimgStoreImg imgData = getter.getImgCropped(imgKey, boundRect.x, boundRect.y, boundRect.width, boundRect.height);
					
					BufferedImage img = ImgUtils.readImage(imgData.getData());
					
					//build filename and path
					final String filename = HtrUtils.buildFileName(pageFileId, i, j, tr.getId(), l.getId(), HtrUtils.LINE_IMG_EXT);
					final String filePath = dir.getAbsolutePath() + File.separator + filename;
					
					//kill the border and store result according to name scheme
					File lineImg = ImgUtils.killBorder(img, offsetPoly, filePath);
					logger.info("Line image written: " + lineImg.getAbsolutePath());
				}
			}
		}
	}
}
