package eu.transkribus.core.model.builder.iiif;

import java.awt.Dimension;
import java.awt.Image;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.dea.fimagestore.core.util.MimeTypes;
import org.dea.fimgstoreclient.FimgStoreGetClient;
import org.dea.fimgstoreclient.FimgStorePostClient;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import de.digitalcollections.iiif.model.sharedcanvas.Sequence;

import eu.transkribus.core.model.beans.DocumentUploadDescriptor.PageUploadDescriptor;
import eu.transkribus.core.model.beans.TrpUpload.UploadType;
import eu.transkribus.core.exceptions.CorruptImageException;
import eu.transkribus.core.io.FimgStoreReadConnection;
import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.model.beans.TrpFImagestore;
import eu.transkribus.core.model.beans.TrpImage;
import eu.transkribus.core.model.beans.TrpPage;

import eu.transkribus.core.model.builder.iiif.IIIFUtils;
import eu.transkribus.core.util.DeaFileUtils;
import eu.transkribus.core.util.ImgUtils;
import eu.transkribus.interfaces.util.URLUtils;




public class IIIFUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(IIIFUtils.class);
	

	
	public static List<TrpPage> getPagesFromIIIF(Manifest manifest,String dir) throws MalformedURLException, IOException{
	
	List<TrpPage> pages = new ArrayList<>();
	File imgFile = null;
	String imgDirPath = "/home/lateknight/Desktop" + File.separator + "img";
	List<Sequence> sequences = manifest.getSequences();
			for(Sequence sequence : sequences) {
				List<Canvas> canvases = sequence.getCanvases();
				for(int i = 0; i<canvases.size(); i++) {
					List<Annotation> images = canvases.get(i).getImages();
					int pageNr = i;
					for(Annotation image : images) {
						
						final String mimetype = image.getResource().getType();
						String ext = MimeTypes.lookupExtension(mimetype);
						
						String filename = i + ".jpg";
						URL url = new URL(image.getResource().getIdentifier().toString());
			
						imgFile = new File(imgDirPath + File.separator + filename);
						
						String problemMsg = "";
						
						URLConnection connection = url.openConnection();
						String redirect = connection.getHeaderField("Location");
						if(redirect != null) {
							url = new URL(redirect);
						}	
						int imgDownloadStatus = DeaFileUtils.copyUrlToFile(url, imgFile);
						
						if(imgDownloadStatus >= 400) {
							//the image URL connection attempt returns a response with code > 400
							problemMsg = getBrokenUrlMsg(url, imgDownloadStatus);
						}
						
						File pageXml = null;
						File thumb = null;
						File imgDir = new File(imgDirPath);
						Dimension dim = null;
						
						logger.info("Page " + pageNr + " image: " + imgFile.getAbsolutePath());

						if(imgFile.isFile()) {
							try {
								dim = ImgUtils.readImageDimensions(imgFile);
							} catch (CorruptImageException cie) {
								logger.error("Image is corrupted!", cie);
								//the image dimension can not be read from the downloaded file
								problemMsg = LocalDocReader.getCorruptImgMsg(imgFile.getName());
							}
						}
						
						thumb = LocalDocReader.getThumbFile(imgDir, imgFile.getName());
						
						TrpPage page = LocalDocReader.buildPage(new File(dir), pageNr, imgFile, pageXml, thumb, dim,
								problemMsg);
						pages.add(page);
					}
				}
			}
			return pages;
	}
	
	public static TrpPage buildPage() {
		
		TrpPage page = new TrpPage();
		
		
		return page;
	}
	
	public static String getBrokenUrlMsg(final URL url, final Integer statusCode) {
		String msg = "Image could not be loaded from " + url.toString();
		if(statusCode != null) {
			msg += "(" + statusCode + " " + Status.fromStatusCode(statusCode).getReasonPhrase() + ")";
		}
		return msg;
	}

}
