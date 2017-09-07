package eu.transkribus.core.io;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.util.MdFileFilter;
import eu.transkribus.core.model.beans.EdFeature;
import eu.transkribus.core.model.beans.EdOption;
import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.builder.ExportUtils;
import eu.transkribus.core.util.ImgUtils;
import eu.transkribus.core.util.JaxbList;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.XmlUtils;

public class LocalDocWriter {
	private static Logger logger = LoggerFactory.getLogger(LocalDocWriter.class);
	
	public static boolean isLocalDoc(TrpDoc doc) {
		try {
			checkIfLocalDoc(doc);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void checkIfLocalDoc(TrpDoc doc) throws Exception {
		if (doc.getMd() == null)
			throw new Exception("No metadata there!");
		if (doc.getMd().getLocalFolder()==null)
			throw new Exception("No local folder specified!");
	}
	
	public static void writeEditDeclFeatures(List<EdFeature> feats, File folder) throws JAXBException, FileNotFoundException {
		JaxbList list = new JaxbList(feats);
		JaxbUtils.marshalToFile(list, new File(folder + "/" + LocalDocConst.EDITORIAL_DECLARATION_FN), EdFeature.class, EdOption.class);
	}
	
//	public static String getThumbFileName(TrpPage page) throws IOException {
//		File imgFile = FileUtils.toFile(page.getUrl());
//		if (imgFile == null)
//			throw new IOException("Cannot retrieve image url from: "+page.getUrl());
//				
//		File thmbsDir = new File(FilenameUtils.getFullPath(imgFile.getAbsolutePath())+"/"+LocalDocConst.THUMBS_FILE_SUB_FOLDER);
//		File outFile = new File(thmbsDir.getAbsolutePath()+"/"+FilenameUtils.getBaseName(imgFile.getName())+".jpg");
//		
//		return outFile.getAbsolutePath();
//	}
	
	/* @deprecated Does not work on mac */
	@Deprecated
	public static void createThumbsForDoc(TrpDoc doc, boolean overwrite) throws Exception {
		checkIfLocalDoc(doc);
		
		File thmbsDir = new File(doc.getMd().getLocalFolder().getAbsolutePath() + File.separator + LocalDocConst.THUMBS_FILE_SUB_FOLDER);
		FileUtils.forceMkdir(thmbsDir);
		
		int newHeight = LocalDocConst.THUMB_SIZE_HEIGHT;
		for (TrpPage p : doc.getPages()) {
			File imgFile = FileUtils.toFile(p.getUrl());
			if (imgFile == null) 
				throw new IOException("Cannot retrieve image url from: "+p.getUrl());
			
			File thumbsFile = FileUtils.toFile(p.getThumbUrl());
			if (thumbsFile == null)
				throw new IOException("Cannot retrieve thumbs url from: "+p.getThumbUrl());
			
			if (thumbsFile.exists() && !overwrite) // skip if already there and overwrite not specified 
				continue;
			
			logger.debug("creating thumb file: "+thumbsFile);
			long st = System.currentTimeMillis();
			
			if (true)  {
				
				
			}
			
			if (false) {
			BufferedImage originalImage = ImgUtils.readImage(imgFile);
			if (originalImage==null)
				throw new IOException("Cannot load image "+imgFile.getAbsolutePath());
			
			double sf = (double)newHeight / (double)originalImage.getHeight();
			int newWidth = (int)(sf * originalImage.getWidth());

			BufferedImage thmbImg = new BufferedImage(newWidth, newHeight, originalImage.getType());
			Graphics2D g = thmbImg.createGraphics();
			RenderingHints rh = new RenderingHints(
		             RenderingHints.KEY_INTERPOLATION,
		             RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHints(rh);
			g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
			g.dispose();
//				logger.debug("thmbImg: "+originalImage+ " size: "+thmbImg.getWidth()+"x"+thmbImg.getHeight());
			
			if (!ImageIO.write(thmbImg, FilenameUtils.getExtension(thumbsFile.getName()), thumbsFile))
				throw new Exception("Could not write thumb file - no appropriate writer found!");
			}
			
		    logger.debug("created thumb file: "+thumbsFile.getAbsolutePath()+" time = "+(System.currentTimeMillis()-st));
		}
	}
	
	public static void updateImageDimension(JAXBPageTranscript transcript) throws FileNotFoundException, IOException {
		TrpPage page = transcript.getMd().getPagePageReferenceForLocalDocs();
		if (page == null) {
			logger.warn("Could not update transcript image dimensions - page object is null - should not happen for local docs: "+transcript.getMd().getUrl());
			return;
		}
		
		//update img file name and dimensions in page XML
		Dimension dim = ImgUtils.readImageDimensions(FileUtils.toFile(page.getUrl()));
		final String imgFileName = page.getImgFileName();
		transcript.getPage().setImageFilename(imgFileName);
		transcript.getPage().setImageHeight(dim.height);
		transcript.getPage().setImageWidth(dim.width);
		logger.debug("Updated PAGE XML: ImgFileName = " + imgFileName 
				+ ", dim = " + dim.width + "x" + dim.height);
	}
				
	public static List<TrpTranscriptMetadata> updateTrpPageXml(JAXBPageTranscript tr) throws Exception {		
		File xmlFile = FileUtils.toFile(tr.getMd().getUrl());
		if (xmlFile == null) 
			throw new Exception("Cannot retrieve file url from: "+tr.getMd().getUrl());
		
		updateImageDimension(tr);
		
		//set last change date
		tr.getPageData().getMetadata().setLastChange(XmlUtils.getXmlGregCal());
		
		PageXmlUtils.marshalToFile(tr.getPageData(), xmlFile);
		
		List<TrpTranscriptMetadata> mds = new ArrayList<>();
		mds.add(tr.getMd());
		
		return mds;
//		PageXmlDao.writeJAXBPageTranscript(tr, xmlFile);
	}
	
	public static void updateTrpDocMetadata(TrpDoc doc) throws Exception {
		checkIfLocalDoc(doc);
						
		writeTrpDocMetadata(doc, doc.getMd().getLocalFolder().getAbsolutePath());
	}
	
	public static void writeTrpDocMetadata(TrpDoc doc, String path) throws Exception {
		checkIfLocalDoc(doc);
		
		File dir = new File(path);
		FileUtils.forceMkdir(dir);
		
		// write metadata:
		File mF = new File(FilenameUtils.normalize(path)+ "/" + LocalDocConst.METADATA_FILENAME);
		JaxbUtils.marshalToFile(doc.getMd(), mF);
//		doc.getMd().writeXml(mF);
		logger.debug("Written metadata file "+mF.getAbsolutePath());
	}
	
	@Deprecated
	/*
	 * Use {@link DocExporter#writeRawDoc}
	 * @deprecated
	 */
	public static void writeTrpDoc(TrpDoc doc, String path) throws Exception {
		if (doc == null) {
			throw new Exception("Null document given!");
		}
//		if (doc.getMd().getLocalFolder()==null)
//			throw new Exception("This is not a local document!");
				
		File dir = new File(path);
		FileUtils.forceMkdir(dir);
		
		String pathN = FilenameUtils.normalize(path);
		
		// write metadata:
		if (doc.getMd()!=null) {
			writeTrpDocMetadata(doc, path);
		}
		
		// write all images and page xml:
		for (TrpPage p : doc.getPages()) {
			File imgFile = copyImgFile(p, p.getUrl(), pathN);
			logger.debug("Written image file "+imgFile.getAbsolutePath());
			
			File xmlFile = copyTranscriptFile(p, pathN+"/"+LocalDocConst.PAGE_FILE_SUB_FOLDER);
			if (xmlFile != null)
				logger.debug("Written transcript xml file "+xmlFile.getAbsolutePath());	
			else
				logger.debug("No transcript found for page "+p.getPageNr());
		}
	}
	
	public static File copyImgFile(TrpPage p, URL u, String path) throws IOException {
		String imgFn =  "Image_"+p.getPageNr();
		if (u.getProtocol().toLowerCase().contains("file")) {
			logger.debug("path: "+u.getPath());
							
			imgFn = FilenameUtils.getName(FileUtils.toFile(u).getAbsolutePath());
		}
		else if (u.getProtocol().toLowerCase().contains("http")) {
			// parse filename from
			String fn = getFilenameFromUrl(u);
			if (fn!=null)
				imgFn = fn;
		}
		return copyImgFile(p, u, path, imgFn);
	}
	
	public static File copyImgFile(TrpPage p, URL u, String path, String fileName) throws IOException {
		File imgFile = new File(path+"/"+fileName);
		FileUtils.copyURLToFile(u, imgFile);
		return imgFile;
	}
	
	public static File copyTranscriptFile(TrpPage p, String path) throws IOException, JAXBException {
	
		if (!p.getTranscripts().isEmpty()) {
			String xmlFn =  "Transcript_"+p.getPageNr()+".xml";
			TrpTranscriptMetadata tmd = p.getTranscripts().get(p.getTranscripts().size()-1);
			URL u = tmd.getUrl();
			if (u.getProtocol().toLowerCase().contains("file")) {
				logger.debug("path: "+u.getPath());
								
				xmlFn = FilenameUtils.getName(FileUtils.toFile(u).getAbsolutePath());
			}
			else if (u.getProtocol().toLowerCase().contains("http")) {
				// parse filename from
				String fn = getFilenameFromUrl(u);
				if (fn!=null)
					xmlFn = fn;
			}
			return copyTranscriptFile(p, path, xmlFn);
		}		
		else
			return null;
	}
	
	public static File copyTranscriptFile(TrpPage p, String path, String fileName) throws IOException, JAXBException {
		if (!p.getTranscripts().isEmpty()) {
			TrpTranscriptMetadata tmd = p.getTranscripts().get(p.getTranscripts().size()-1);

			//load page transcript only once during export
			JAXBPageTranscript tr = ExportUtils.getPageTranscriptAtIndex(p.getPageNr()-1);
			if (tr == null){
				tr = new JAXBPageTranscript(tmd);
				tr.build();
			}
			
			File xmlFile = new File(path+"/"+fileName);
			PageXmlUtils.marshalToFile(tr.getPageData(), xmlFile);
			return xmlFile;
		}		
		else
			return null;
	}
	
	private static String getFilenameFromUrl(URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();
		String raw = conn.getHeaderField("Content-Disposition");
		// raw = "attachment; filename=abc.jpg"
		
		if(raw != null && raw.indexOf("=") != -1) {
			String[] splits = raw.split("=");
			if (splits.length > 1) {
				return raw.split("=")[1];
			}
		}
		conn.disconnect();
		return null;		
	}
	
	public static void main(String[] args) {
		try {
//			TrpDoc doc = LocalDocReader.load("/media/dea_scratch/TRP/TrpTestDoc_20140127");
			TrpDoc doc = LocalDocReader.load("C:/TrpTestDoc_20140127", true);
			LocalDocWriter.createThumbsForDoc(doc, true);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		if (true) {
			return;
		}
		
		
		URL url;
		try {
			url = new URL("https://dbis-thure.uibk.ac.at/fimagestore/Get?id=PZOFOVIFVQPMXDGPKJNLQVUH");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
	//		conn.setAllowUserInteraction(false);
	//		conn.setDoInput(true);
	//		conn.setDoOutput(true);
			conn.connect();
			String raw = conn.getHeaderField("Content-Disposition");
			// raw = "attachment; filename=abc.jpg"
			
			if(raw != null && raw.indexOf("=") != -1) {
			    String fileName = raw.split("=")[1];
			    System.out.println("filename = "+fileName);
			} else {
			    // fall back to random generated file name?
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	

}
