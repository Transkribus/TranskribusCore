package eu.transkribus.core.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FilenameUtils;
import org.apache.fop.afp.util.StringUtils;
import org.dea.fimgstoreclient.FimgStoreGetClient;
import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.builder.FatBuilder;
import eu.transkribus.core.model.builder.alto.AltoExporter;
import eu.transkribus.core.model.builder.mets.TrpMetsBuilder;
import eu.transkribus.core.util.JaxbUtils;

public class DocExporter extends Observable {
	private static final Logger logger = LoggerFactory.getLogger(DocExporter.class);
	
	public static class ExportOptions {
		public String dir=null;
		public Set<Integer> pageIndices=null; // can be set to null to include all pages!
		public boolean doOverwrite=true;
		public boolean writeMets=true;
		public boolean useOcrMasterDir=true;
		public boolean doWriteImages=true;
		public boolean exportPageXml=true;
		public String pageDirName = LocalDocConst.PAGE_FILE_SUB_FOLDER;
		public boolean exportAltoXml=true;
		public boolean splitIntoWordsInAltoXml=false;
		public boolean exportFatXml=false;
		public String fileNamePattern = "${filename}";
		public boolean useHttps=true;
	}

	public File writeRawDoc(TrpDoc doc, final String dir, boolean doOverwrite, Set<Integer> pageIndices, boolean exportImg, boolean exportPage, boolean exportAlto, boolean splitIntoWordsInAlto) throws IOException,
	IllegalArgumentException, URISyntaxException, JAXBException, TransformerException {
		return writeRawDoc(doc, dir, doOverwrite, pageIndices, exportImg, exportPage, exportAlto, splitIntoWordsInAlto, null);
	}
	
	public File writeRawDoc(TrpDoc doc, final String dir, boolean doOverwrite, Set<Integer> pageIndices, 
			boolean exportImg, boolean exportPage, boolean exportAlto, boolean splitIntoWordsInAlto, String fileNamePattern) throws IOException,
			IllegalArgumentException, URISyntaxException, JAXBException, TransformerException {
		ExportOptions opts = new ExportOptions();
		opts.dir = dir;
		opts.doOverwrite = doOverwrite;
		opts.writeMets = true;
		opts.useOcrMasterDir = false;
		opts.doWriteImages = exportImg;
		opts.exportPageXml = exportPage;
		opts.exportAltoXml = exportAlto;
		opts.splitIntoWordsInAltoXml = splitIntoWordsInAlto;
		opts.pageIndices = pageIndices;
		if(fileNamePattern != null){
			opts.fileNamePattern = fileNamePattern;
		}
		
		return exportDoc(doc, opts);
	}

	public File writeFatDoc(TrpDoc doc, final String dir, boolean doOverwrite) throws IOException,
			IllegalArgumentException, URISyntaxException, JAXBException, TransformerException {
		return writeFatDoc(doc, null, dir, doOverwrite, "${pageNr}_${filekey}");
	}
	
	public File writeFatDoc(TrpDoc doc, final Set<Integer> pages, final String dir, boolean doOverwrite, final String fileNamePattern) throws IOException,
	IllegalArgumentException, URISyntaxException, JAXBException, TransformerException {

		ExportOptions opts = new ExportOptions();
		opts.dir = dir;
		opts.doOverwrite = doOverwrite;
		opts.writeMets = false;
		opts.useOcrMasterDir = true;
		opts.doWriteImages = true;
		opts.exportPageXml = false;
		opts.exportAltoXml = false;
		opts.pageIndices = pages;
		opts.exportFatXml = true;
		opts.fileNamePattern = fileNamePattern;
		
		final File outputDir = exportDoc(doc, opts);
		return outputDir;
	}

	public File exportDoc(TrpDoc doc, final ExportOptions opts) throws IOException, IllegalArgumentException,
			URISyntaxException, JAXBException, TransformerException {
		FimgStoreGetClient getter = null;
		FimgStoreUriBuilder uriBuilder = null;
		if (doc.isRemoteDoc()) {
			//FIXME fimagestore path should be read from docMd!
			getter = new FimgStoreGetClient("dbis-thure.uibk.ac.at", "f");
			final String scheme = opts.useHttps ? "https" : "http";
			final int port = opts.useHttps ? 443 : 80;
			uriBuilder = new FimgStoreUriBuilder(scheme, getter.getHost(), port,
					getter.getServerContext());
		}

		File outputDir = new File(opts.dir);
		if (!opts.doOverwrite && outputDir.exists()) {
			throw new IOException("File path already exists.");
		}
		outputDir.mkdir();
		
		File pageOutputDir = null, altoOutputDir = null;
				
		if(opts.exportPageXml){
			pageOutputDir = new File(outputDir.getAbsolutePath() + File.separatorChar
					+ opts.pageDirName);
			pageOutputDir.mkdir();
		}
		
		AltoExporter altoEx = new AltoExporter();
		if (opts.exportAltoXml){
			altoOutputDir = altoEx.createAltoOuputDir(doc, outputDir.getAbsolutePath());
			
//			try {
//
//				altoEx.export(doc, outputDir.getAbsolutePath());
//			} catch (DocumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}

		if (doc.getMd() != null) {
			File fileOut = new File(outputDir.getAbsolutePath() + File.separatorChar
					+ "metadata.xml");
			try {
				JaxbUtils.marshalToFile(doc.getMd(), fileOut);
			} catch (JAXBException e) {
				throw new IOException("Could not marshal metadata to file.", e);
			}
		}
		
		//decide where to put the images
		final File imgOutputDir;
		if (opts.useOcrMasterDir) {
			imgOutputDir = new File(outputDir.getAbsolutePath() + File.separatorChar
					+ LocalDocConst.OCR_MASTER_DIR);
			imgOutputDir.mkdir();
		} else {
			imgOutputDir = outputDir;
		}

		List<TrpPage> pages = doc.getPages();
		
		for (int i=0; i<pages.size(); ++i) {
//			for (TrpPage p : pages) {
			if (opts.pageIndices!=null && !opts.pageIndices.contains(i))
				continue;
		
			TrpPage p = pages.get(i);
			File imgFile = null, xmlFile = null, altoFile = null;
			
			final String baseFileName = buildFileName(opts.fileNamePattern, p);
			final String imgExt = "." + FilenameUtils.getExtension(p.getImgFileName());
			final String xmlExt = ".xml";
			
			if (doc.isRemoteDoc()) {
				final URI imgUri = uriBuilder.getFileUri(p.getKey());
				if (opts.doWriteImages) {
					imgFile = getter.saveFile(imgUri, imgOutputDir.getAbsolutePath(), baseFileName + imgExt);
					p.setUrl(imgFile.toURI().toURL());
					p.setKey(null);
				}
				if(opts.exportPageXml) {
					TrpTranscriptMetadata t = p.getCurrentTranscript();
					xmlFile = getter.saveFile(t.getUrl().toURI(), pageOutputDir.getAbsolutePath(), baseFileName + xmlExt);
					p.getTranscripts().clear();
					t.setUrl(xmlFile.toURI().toURL());
					p.getTranscripts().add(t);
				}
			} else {
				if (opts.doWriteImages) {
					imgFile = LocalDocWriter.copyImgFile(p, p.getUrl(), imgOutputDir.getAbsolutePath(), baseFileName + imgExt);
				}
				if(opts.exportPageXml) {
					xmlFile = LocalDocWriter.copyTranscriptFile(p, pageOutputDir.getAbsolutePath(), baseFileName + xmlExt);
				}
			}
			// export alto:
			if (opts.exportAltoXml) {
				altoFile = altoEx.exportAltoFile(p, baseFileName + xmlExt, altoOutputDir, opts.splitIntoWordsInAltoXml);
			}
			
			if (imgFile != null)
				logger.debug("Written image file " + imgFile.getAbsolutePath());
			
			if (xmlFile != null) {
				logger.debug("Written transcript xml file " + xmlFile.getAbsolutePath());
			} else {
//				logger.warn("No transcript was exported for page " + p.getPageNr());
			}
			if (altoFile != null) {
				logger.debug("Written ALTO xml file " + altoFile.getAbsolutePath());
			}
			
			notifyObservers(Integer.valueOf(p.getPageNr()));
			setChanged();
		}
		
		if (opts.writeMets) {
			//load the exported doc from its new location
			//FIXME this does not work for export of PAGE XMLs only!
//			final TrpDoc localDoc = LocalDocReader.load(outputDir.getAbsolutePath(), false);
			
			//set local folder or else TrpMetsBuilder will treat this as remote doc!
			doc.getMd().setLocalFolder(outputDir);
			//write mets with file pointers to local files
			Mets mets = TrpMetsBuilder.buildMets(doc, opts.exportPageXml, opts.exportAltoXml, opts.doWriteImages, opts.pageIndices);
			File metsFile = new File(outputDir.getAbsolutePath() + File.separator
					+ TrpMetsBuilder.METS_FILE_NAME);
	
			try {
				JaxbUtils.marshalToFile(mets, metsFile, TrpDocMetadata.class);
			} catch (JAXBException e) {
				throw new IOException("Could not marshal METS to file!", e);
			}
		}
		
		if(opts.exportFatXml) {
			//doc root of fat xml is named RootFolder and so is the Bean
			FatBuilder.writeFatXml(outputDir);
		}
		return outputDir;
	}

	private String buildFileName(String fileNamePattern, TrpPage p) {
		if(fileNamePattern == null || fileNamePattern.equals("${filename}")) {
			return FilenameUtils.getBaseName(p.getImgFileName());
		} else {
			String fileName = buildFileName(fileNamePattern, p.getImgFileName(), p.getPageId(), 
					p.getDocId(), p.getKey(), p.getPageNr());
			return fileName;
		}
	}

	private static String buildFileName(String fileNamePattern, String imgFileName, int pageId, int docId,
			String key, int pageNr) {
		
		if(!isFileNamePatternValid(fileNamePattern)){
			throw new IllegalArgumentException("Filename pattern is invalid: " + fileNamePattern);
		}
		
		final String pageNrStr = StringUtils.lpad(""+pageNr, '0', 4);
		final String docIdStr = StringUtils.lpad(""+docId, '0', 6);
		
		String fileName = fileNamePattern
		.replaceAll("\\$\\{filename\\}", FilenameUtils.getBaseName(imgFileName))
		.replaceAll("\\$\\{pageId\\}", ""+pageId)
		.replaceAll("\\$\\{docId\\}", ""+docIdStr)
		.replaceAll("\\$\\{filekey\\}", key)
		.replaceAll("\\$\\{pageNr\\}", pageNrStr);
		
		return fileName;
	}

	public static boolean isFileNamePatternValid(final String fnp) {
		//filename must have a unique component with respect to document
		boolean isValid = fnp.contains("${filename}") || fnp.contains("${filekey}") 
				|| fnp.contains("${pageId}") || fnp.contains("${pageNr}");
		if(!isValid){
			return false;
		}
		//remove all valid placeholders
		String fnpRemainder = fnp
				.replaceAll("\\$\\{filename\\}", "")
				.replaceAll("\\$\\{pageId\\}", "")
				.replaceAll("\\$\\{docId\\}", "")
				.replaceAll("\\$\\{filekey\\}", "")
				.replaceAll("\\$\\{pageNr\\}", "");	
		//check for occurence of illegal chars
		final String[] illegalChars = {"\\", "/", ":", "*", "?", "\"", "<", ">", "|", "~", "{", "}"};
		for(String s : illegalChars){
			if(fnpRemainder.contains(s)){
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args){
		final String p = "${filename}_${${pageId}_${pageNr}";
		System.out.println(isFileNamePatternValid(p));
		System.out.println(buildFileName(p, "test.jpg", 123, 456, "AAAAA", 7));
	}
}
