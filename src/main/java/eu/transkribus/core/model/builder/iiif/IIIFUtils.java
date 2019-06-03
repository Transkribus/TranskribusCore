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
import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.io.FileInput;
import org.primaresearch.dla.page.io.InputSource;
import org.primaresearch.dla.page.io.UrlInput;
import org.primaresearch.dla.page.io.xml.XmlInputOutput;
import org.primaresearch.dla.page.io.xml.XmlPageReader;
import org.primaresearch.io.UnsupportedFormatVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.digitalcollections.core.model.api.MimeType;
import de.digitalcollections.iiif.model.ImageContent;
import de.digitalcollections.iiif.model.MetadataEntry;
import de.digitalcollections.iiif.model.OtherContent;
import de.digitalcollections.iiif.model.image.ImageApiProfile;
import de.digitalcollections.iiif.model.image.ImageService;
import de.digitalcollections.iiif.model.jackson.IiifObjectMapper;
import de.digitalcollections.iiif.model.openannotation.Annotation;
import de.digitalcollections.iiif.model.sharedcanvas.AnnotationList;
import de.digitalcollections.iiif.model.sharedcanvas.Canvas;
import de.digitalcollections.iiif.model.sharedcanvas.Manifest;
import de.digitalcollections.iiif.model.sharedcanvas.Resource;
import de.digitalcollections.iiif.model.sharedcanvas.Sequence;

import eu.transkribus.core.model.beans.DocumentUploadDescriptor.PageUploadDescriptor;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpUpload.UploadType;
import eu.transkribus.core.exceptions.CorruptImageException;
import eu.transkribus.core.io.FimgStoreReadConnection;
import eu.transkribus.core.io.LocalDocConst;
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
	
	
	public static TrpDoc createDocFromIIIF(URL url, String path) throws JsonParseException, JsonMappingException, IOException, SQLException, ReflectiveOperationException {
	
		
		ObjectMapper iiifMapper = new IiifObjectMapper();
		
		logger.debug("Url transmitted to UploadManager : "+url.toString());
		
		Manifest manifest =  iiifMapper.readValue(url, Manifest.class);

		TrpDocMetadata md = null;
		
		//TODO read metadata from IIIF
		logger.debug("the local user home dir = " + path);
		
		List<TrpPage> pages = null;
		try {
			pages = IIIFUtils.getPagesFromIIIF(manifest,path);
		} catch (UnsupportedFormatVersionException e) {
			e.printStackTrace();
		}
		
		final TrpDoc doc = new TrpDoc();
		md = IIIFUtils.readIiifMetadata(manifest);
		doc.setMd(md);
		doc.setPages(pages);	
		
		return doc;
	
	}
	
	public static List<TrpPage> getPagesFromIIIF(Manifest manifest,String dir) throws MalformedURLException, IOException, UnsupportedFormatVersionException{
	
		XmlPageReader reader = XmlInputOutput.getReader();
		List<TrpPage> pages = new ArrayList<>();
		File imgFile = null;
		File abbyyFile = null;
		File altoFile = null;
		
		String imgDirPath = dir + File.separator + "img";
		String altoDirPath = dir + File.separator + LocalDocConst.ALTO_FILE_SUB_FOLDER;
		String pageDirPath = dir + File.separator + LocalDocConst.PAGE_FILE_SUB_FOLDER;
		
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
							
							//TODO import alto if available
							
							List<OtherContent> seeAlso = image.getSeeAlso();
							
							if(seeAlso != null) {
								for(OtherContent content : seeAlso) {
									if(content.getFormat() == MimeType.MIME_APPLICATION_XML  ) {
	
										altoFile = new File(altoDirPath + File.separator + filename);
										logger.debug("Create Alto File");
										if(DeaFileUtils.copyUrlToFile(content.getIdentifier().toURL(), altoFile) >= 400) {
											logger.error("Could not download ALTO XML and it will be ignored!");
											//don't fail if ALTO XML could not be retrieved
											altoFile = null;
										}
									}		
								}
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
							
							File pageOutFile = new File(pageDirPath + File.separatorChar + FilenameUtils.getBaseName(imgFile.getName()) + ".xml");
							
							pageXml = LocalDocReader.createPageXml(pageOutFile, true, null, altoFile, 
									null, true, true, false, imgFile.getName(), dim);
							
							thumb = LocalDocReader.getThumbFile(imgDir, imgFile.getName());
							
							TrpPage page = LocalDocReader.buildPage(new File(dir), pageNr, imgFile, pageXml, thumb, dim,
									problemMsg);
							pages.add(page);
						}
					}
				}
				return pages;
	}
	
	public static TrpDocMetadata readIiifMetadata(Manifest manifest) {
		TrpDocMetadata md = new TrpDocMetadata();
		List<MetadataEntry> metaData = manifest.getMetadata();
		for(MetadataEntry entry : metaData) {
			switch(entry.getLabelString()) {
			case "Title":
				md.setTitle(entry.getValueString());
			case "Creator":
			case "Author":
				md.setAuthor(entry.getValueString());
			}
		}		
		
		return md;
	}
	
	
	public static String exportIiifManifest(TrpDoc doc) throws MalformedURLException, JsonProcessingException {
		
		String productionBaseUrl = "https://dbis-thure.uibk.ac.at/iiif/2";
		String testBaseUrl = "https://files-test.transkribus.eu/iiif/2/";
		
		ObjectMapper iiifMapper = new IiifObjectMapper();
		Manifest manifest = new Manifest(testBaseUrl+""+doc.getId()+"/manifest");
		
		//TODO add more metadata to manifest
		manifest.addMetadata("Title", doc.getMd().getTitle());
		
		if(doc.getMd().getAuthor() != null) {
			manifest.addMetadata("Author", doc.getMd().getAuthor());
		}
			
		Sequence sequence = new Sequence(doc.getMd().getUrl().toString());
		
		List<TrpPage> pages = doc.getPages();
		List<Canvas> canvasList = new ArrayList<>();
		
		for(TrpPage page : pages) {
			
			TrpImage pageImage = page.getImage();
			
			Canvas canvas = new Canvas(page.getUrl().toString());
			canvas.setWidth(pageImage.getWidth());
			canvas.setHeight(pageImage.getHeight());
			canvas.addIIIFImage(testBaseUrl+""+pageImage.getKey(), ImageApiProfile.LEVEL_ONE);
			
			ImageContent thumbnail = new ImageContent(pageImage.getThumbUrl().toString());
			thumbnail.addService(new ImageService(pageImage.getThumbUrl().toString(), ImageApiProfile.LEVEL_ONE));
			canvas.addThumbnail(thumbnail);
			
			canvas.addSeeAlso(new OtherContent(page.getCurrentTranscript().getUrl().toString(), "application/xml"));
			AnnotationList annoList = new AnnotationList(testBaseUrl+""+page.getKey()+"/contentAsText/"+page.getPageNr());
			annoList.addLabel("Text of page "+page.getPageNr());
			canvas.addOtherContent(annoList);
			canvasList.add(canvas);
			
				
		}
		sequence.setCanvases(canvasList);
		manifest.addSequence(sequence);
		
		String manifestJson = iiifMapper.writerWithDefaultPrettyPrinter().writeValueAsString(manifest);
		
		logger.debug(manifestJson);
		
		return manifestJson;
		
	}
	
	public static String getBrokenUrlMsg(final URL url, final Integer statusCode) {
		String msg = "Image could not be loaded from " + url.toString();
		if(statusCode != null) {
			msg += "(" + statusCode + " " + Status.fromStatusCode(statusCode).getReasonPhrase() + ")";
		}
		return msg;
	}

}