package eu.transkribus.core.util;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;

import eu.transkribus.core.exceptions.CorruptImageException;
import eu.transkribus.core.io.exec.util.ExiftoolUtil;
import eu.transkribus.interfaces.types.util.TrpImageIO;
import eu.transkribus.interfaces.types.util.TrpImgMdParser;
import eu.transkribus.interfaces.types.util.TrpImgMdParser.ImageTransformation;

public class ImgUtils {
	private final static Logger logger = LoggerFactory.getLogger(ImgUtils.class);

	private static boolean isExiftoolAvailable = isExiftoolAvailable();
	
	private static boolean isExiftoolAvailable() {
		boolean isExiftoolAvailable = false;
		try {
			File f = SysUtils.findFileInPath(ExiftoolUtil.exiftool);
			if(f != null) {
				isExiftoolAvailable = true;
			}
		} catch (FileNotFoundException e) {
			isExiftoolAvailable = false;
		}
		if (isExiftoolAvailable){
			logger.debug("Exiftool is available on Path");
		}
		else{
			logger.debug("Exiftool is not available on Path");
		}
		return isExiftoolAvailable;
	}
	
	public static Dimension readImageDimensionsWithExiftool(File imgFile) throws IOException, TimeoutException, InterruptedException { 
		Map<String, String> tags = ExiftoolUtil.parseTags(imgFile.getAbsolutePath(), true);
		String widthStr = tags.get("ImageWidth");
		String heightStr = tags.get("ImageHeight");
		String orientationStr = tags.get("Orientation");
		
		logger.debug("EXIF orientation = " + orientationStr);

		if (widthStr==null || heightStr==null) {
			//Exiftool returns error description on stderr and stdout
			String error = tags.get("Error");
			String msg = "Could not read file " + imgFile.getName(); 
			if(!StringUtils.isEmpty(error)) {
				msg += ": " + error;
			}
			throw new IOException(msg);
		}
		
		int orientation = TrpImgMdParser.DEFAULT_EXIF_ORIENTATION;
		if(orientationStr != null) {
			try {
				orientation = Integer.valueOf(orientationStr);
			} catch (NumberFormatException e) {
				logger.error("Orientation value from exiftool is not a number! Check if installation supports the -n switch.", e);
			}
		}
		final int width = Integer.valueOf(widthStr);
		final int height = Integer.valueOf(heightStr);
		ImageTransformation imgDim = TrpImgMdParser.getTransformation(width, height, orientation);
		logger.debug("success reading img dims with exiftool!");
		return new Dimension(imgDim.getDestinationWidth(), imgDim.getDestinationHeight());
	}
	
	/** 
	 * Reads image dimension for the specified image file.
	 * This method uses exiftool and falls back to metadata-extractor and ultimately imageIO in case of failure. 
	 * For multiimage tiff files, the dimensions of the first image are read. 
	 * @param imgFile
	 * @return java.awt.Dimension
	 * @throws FileNotFoundException if imgFile does not exist or is not a file
	 * @throws CorruptImageException if all attempts to read the Dimension failed, this may point to a broken image file.
	 */
	public static Dimension readImageDimensions(File imgFile) throws FileNotFoundException, CorruptImageException {
		if(imgFile == null) {
			throw new IllegalArgumentException("imgFile must not be null.");
		}
		if(!imgFile.isFile()) {
			throw new FileNotFoundException("Could not find file: " + imgFile.getAbsolutePath());
		}
		
		Dimension dim = null;
		if(isExiftoolAvailable) {
			//try to read with exiftool first:
			try {
				dim = readImageDimensionsWithExiftool(imgFile);			
			} catch (Exception e1) {
				logger.warn("Could not read image dimensions with exiftool: " + e1.getMessage(), e1);
			}
		}
		
		//if exiftool is not installed or failed. Try metadata extractor
		if(dim == null) {
			try {
				dim = readImageDimensionsWithMdParser(imgFile);
			} catch (Exception e) {
				logger.warn("Could not read image dimensions with metadata-extractor: " + e.getMessage(), e);
			}
		}
		
		if(dim == null) {
			throw new CorruptImageException("Could not read image dimension of file: " + imgFile.getAbsolutePath());
		}
		
		return dim;
	}
	
	public static Dimension readImageDimensionsWithMdParser(File imgFile) throws FileNotFoundException, IOException {
		try {
			ImageTransformation imgDim = TrpImgMdParser.readImageDimension(imgFile);
			return new Dimension(imgDim.getDestinationWidth(), imgDim.getDestinationHeight());
		} catch(ImageProcessingException | MetadataException e) {
			logger.warn("Metadata extractor did not find EXIF data. Falling back to reading raw image data dimension.");
			return TrpImageIO.readImageDimensions(imgFile);
		}
	}
	
	/** Reads image in the specified image file. For multiimage tiff files, the first image is read. */
	public static BufferedImage readImage(byte[] data) throws IOException {
		return TrpImageIO.read(data);
	}
	
	/** Reads image in the specified image file. For multiimage tiff files, the first image is read. */
	public static BufferedImage readImage(File imgFile) throws FileNotFoundException, IOException {
		return TrpImageIO.read(imgFile);
	}
	
	/**
	 * Bound the given rectangle to the range [0, 0, width, height]
	 * @return A new bound rectangle
	 */
	public static Rectangle boundRectangle(Rectangle rect, int width, int height) {
		Rectangle nr = new Rectangle(rect);
		if (nr.x < 0) { 
			nr.x = 0; 
			nr.width += rect.x; 
		}
		if (nr.y < 0) { 
			nr.y = 0; 
			nr.height += rect.y; 
		}
		if (nr.width > width)
			nr.width = width;
		if (nr.height > height)
			nr.height = height;
		
		return nr;
	}	
	
	/**
	 * compute the scale factor for an image with dimension origX x origY for fitting it into a
	 * frame with size destX x destY while keeping the aspect ratio
	 * 
	 * @param origX
	 * @param origY
	 * @param destX
	 * @param destY
	 * @return
	 */
	public static double computeScaleFactor(final int origX, final int origY, 
			final int destX, final int destY) {
		final double xScale = (double)destX/origX;
        final double yScale = (double)destY/origY;
        return Math.min(xScale, yScale);
	}
	
	public static File killBorder(File baseImageFile, Polygon p, String outPng) throws IOException {
		BufferedImage baseImage = readImage(baseImageFile);		
        return killBorder(baseImage, p, outPng);
	}
	
	public static File killBorder(BufferedImage baseImage, Polygon p, String outPng) throws IOException {		
        // Creates a background BufferedImage with an Alpha layer so that AlphaCompositing works
    	final int height = baseImage.getHeight();
		final int width = baseImage.getWidth();
        BufferedImage bg = new BufferedImage (width, height, BufferedImage.TYPE_INT_ARGB);
        // Sets AlphaComposite to type SRC_OUT with a transparency of 100%
        // Convert BufferedImage to G2D. Effects applied to G2D are applied to the original BufferedImage automatically.
        Graphics2D g2d = bg.createGraphics();
        //create Area with dimension of the image to be cropped
        Area wholeImageArea = new Area(new Rectangle(width, height));
        //create Area from given polygon 
        Area psArea = new Area(p);
        //invert the printspace area
        wholeImageArea.subtract(psArea);
        // Covers the whole image to provide a layer to be cropped, revealing the imported image underneath
        g2d.fill(wholeImageArea);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OUT, 1.0f));
        // Draws the imported image into the background BufferedImage (bg).
        g2d.drawImage(baseImage, 0, 0, null);
        // Writes the image to a PNG
        if(!outPng.endsWith(".png")){
        	outPng += ".png";
        }
        File out = new File(outPng);
        if(!ImageIO.write(bg, "png", out)){
        	//should not happen with png
        	throw new IOException("No appropriate writer was found!");
        }
        return out;        
	}
}
