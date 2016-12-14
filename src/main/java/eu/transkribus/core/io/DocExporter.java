package eu.transkribus.core.io;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FilenameUtils;
import org.apache.fop.afp.util.StringUtils;
import org.dea.fimgstoreclient.FimgStoreGetClient;
import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.DocumentException;

import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.builder.ExportUtils;
import eu.transkribus.core.model.builder.alto.AltoExporter;
import eu.transkribus.core.model.builder.docx.DocxBuilder;
import eu.transkribus.core.model.builder.mets.TrpMetsBuilder;
import eu.transkribus.core.model.builder.ms.TrpXlsxBuilder;
import eu.transkribus.core.model.builder.ms.TrpXlsxTableBuilder;
import eu.transkribus.core.model.builder.pdf.PdfExporter;
import eu.transkribus.core.model.builder.tei.ATeiBuilder;
import eu.transkribus.core.model.builder.tei.TeiExportPars;
import eu.transkribus.core.model.builder.tei.TrpTeiStringBuilder;
import eu.transkribus.core.util.JaxbUtils;

public class DocExporter extends Observable {
	private static final Logger logger = LoggerFactory.getLogger(DocExporter.class);
	
	public static class ExportOptions implements Serializable {
		private static final long serialVersionUID = -3767885415954377017L;
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
		public String fileNamePattern = "${filename}";
		public boolean useHttps=true;
		
		@Override
		public String toString() {
			return "ExportOptions [dir=" + dir + ", pageIndices=" + pageIndices + ", doOverwrite=" + doOverwrite
					+ ", writeMets=" + writeMets + ", useOcrMasterDir=" + useOcrMasterDir + ", doWriteImages="
					+ doWriteImages + ", exportPageXml=" + exportPageXml + ", pageDirName=" + pageDirName
					+ ", exportAltoXml=" + exportAltoXml + ", splitIntoWordsInAltoXml=" + splitIntoWordsInAltoXml
					+ ", fileNamePattern=" + fileNamePattern + ", useHttps="
					+ useHttps + "]";
		}
		
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

//	public File writeFatDoc(TrpDoc doc, final String dir, boolean doOverwrite) throws IOException,
//			IllegalArgumentException, URISyntaxException, JAXBException, TransformerException {
//		return writeFatDoc(doc, null, dir, doOverwrite, "${pageNr}_${filekey}", null, null);
//	}
//	
//	public File writeFatDoc(TrpDoc doc, final Set<Integer> pages, final String dir, boolean doOverwrite, 
//			final String fileNamePattern, final String language, final String typeFace) throws IOException,
//	IllegalArgumentException, URISyntaxException, JAXBException, TransformerException {
//
//		
//		
//		final File outputDir = exportDoc(doc, opts);
//		return outputDir;
//	}
	
	public void writePDF(final TrpDoc doc, final String path, Set<Integer> pageIndices, final boolean addTextPages, final boolean imagesOnly, final boolean highlightTags, final boolean wordBased, final boolean doBlackening, boolean createTitle) throws MalformedURLException, DocumentException, IOException, JAXBException, URISyntaxException, InterruptedException{
		PdfExporter pdfWriter = new PdfExporter();
		pdfWriter.export(doc, path, pageIndices, addTextPages, imagesOnly, highlightTags, wordBased, doBlackening, createTitle);
	}
	
	public void writeTEI(final TrpDoc doc, final String path, final TeiExportPars pars) throws Exception{
		ATeiBuilder builder = new TrpTeiStringBuilder(doc, pars, null);
		builder.buildTei();
		builder.writeTeiXml(new File(path));
	}
	
	public void writeDocx(final TrpDoc doc, final String path, Set<Integer> pageIndices, final boolean highlightTags, final boolean wordBased, final boolean doBlackening, final boolean createTitle, boolean doDocxMarkUnclear, boolean doDocxExpandAbbrevs, boolean doDocxSubstituteAbbrevs, boolean doDocxPreserveLineBreaks) throws MalformedURLException, DocumentException, IOException, JAXBException, URISyntaxException, InterruptedException, Docx4JException{
		DocxBuilder.writeDocxForDoc(doc, wordBased, highlightTags, doBlackening, new File(path), pageIndices, null, createTitle, doDocxMarkUnclear, doDocxExpandAbbrevs, doDocxSubstituteAbbrevs, doDocxPreserveLineBreaks);
	}
	
	public void writeTagExcel(final TrpDoc doc, final String path, Set<Integer> pageIndices, boolean wordBased) throws Exception{
		TrpXlsxBuilder.writeXlsxForDoc(doc, wordBased, new File(path), pageIndices, null);
	}
	
	public void writeTableExcel(final TrpDoc doc, final String path, Set<Integer> pageIndices) throws Exception{
		TrpXlsxTableBuilder.writeXlsxForTables(doc, new File(path), pageIndices, null);
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
		
	

		//create copy of object, as we alter it here while exporting
		TrpDoc doc2;
		doc2 = new TrpDoc(doc);
		
		File outputDir = new File(opts.dir);
		if (!opts.doOverwrite && outputDir.exists()) {
			throw new IOException("File path already exists.");
		}
		outputDir.mkdir();
		
		File pageOutputDir = null, altoOutputDir = null;
				
		if(opts.exportPageXml){
			pageOutputDir = new File(outputDir.getAbsolutePath() + File.separatorChar
					+ opts.pageDirName);
			if (pageOutputDir.mkdir()){
				logger.debug("pageOutputDir created successfully ");
			}
			else{
				logger.debug("pageOutputDir could not be created!");
			}
			
		}
		
		AltoExporter altoEx = new AltoExporter();
		if (opts.exportAltoXml){
			altoOutputDir = altoEx.createAltoOuputDir(doc2, outputDir.getAbsolutePath());
			
//			try {
//
//				altoEx.export(doc, outputDir.getAbsolutePath());
//			} catch (DocumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}

		if (doc2.getMd() != null) {
			File fileOut = new File(outputDir.getAbsolutePath() + File.separatorChar
					+ "metadata.xml");
			try {
				JaxbUtils.marshalToFile(doc2.getMd(), fileOut);
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

		List<TrpPage> pages = doc2.getPages();
		
		for (int i=0; i<pages.size(); ++i) {
//			for (TrpPage p : pages) {
			if (opts.pageIndices!=null && !opts.pageIndices.contains(i))
				continue;
		
			TrpPage p = pages.get(i);
			File imgFile = null, xmlFile = null, altoFile = null;
			
			final String baseFileName = buildFileName(opts.fileNamePattern, p);
			final String imgExt = "." + FilenameUtils.getExtension(p.getImgFileName());
			final String xmlExt = ".xml";
			
			if (doc2.isRemoteDoc()) {
				final URI imgUri = uriBuilder.getFileUri(p.getKey());
				if (opts.doWriteImages) {
					imgFile = getter.saveFile(imgUri, imgOutputDir.getAbsolutePath(), baseFileName + imgExt);
					p.setUrl(imgFile.toURI().toURL());
					p.setKey(null);
				}
				if(opts.exportPageXml) {
					//old
					//TrpTranscriptMetadata t = p.getCurrentTranscript();
					/*
					 * new: to get the previously stored choosen version
					 */
					TrpTranscriptMetadata t = ExportUtils.getPageTranscriptAtIndex(i).getMd();
					xmlFile = getter.saveFile(t.getUrl().toURI(), pageOutputDir.getAbsolutePath(), baseFileName + xmlExt);
					p.getTranscripts().clear();
					TrpTranscriptMetadata tCopy = new TrpTranscriptMetadata(t, p);
					tCopy.setUrl(xmlFile.toURI().toURL());
					//so the current (and only) transcript is set to the one selected in the export dialog
					p.getTranscripts().add(tCopy);
					
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
				logger.warn("No transcript was exported for page ");
			}
			if (altoFile != null) {
				logger.debug("Written ALTO xml file " + altoFile.getAbsolutePath());
			} else {
				logger.warn("No alto was exported for page ");
			}
			
			notifyObservers(Integer.valueOf(p.getPageNr()));
			setChanged();
		}
		
		if (opts.writeMets) {
			//load the exported doc from its new location
			//FIXME this does not work for export of PAGE XMLs only!
//			final TrpDoc localDoc = LocalDocReader.load(outputDir.getAbsolutePath(), false);
			
			//set local folder or else TrpMetsBuilder will treat this as remote doc!
			doc2.getMd().setLocalFolder(outputDir);
			//write mets with file pointers to local files
			Mets mets = TrpMetsBuilder.buildMets(doc2, opts.exportPageXml, opts.exportAltoXml, opts.doWriteImages, opts.pageIndices);
			File metsFile = new File(outputDir.getAbsolutePath() + File.separator
					+ TrpMetsBuilder.METS_FILE_NAME);
	
			try {
				JaxbUtils.marshalToFile(mets, metsFile, TrpDocMetadata.class);
			} catch (JAXBException e) {
				throw new IOException("Could not marshal METS to file!", e);
			}
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
