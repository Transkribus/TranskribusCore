package eu.transkribus.core.model.builder.pdf;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Observable;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.dea.fimgstoreclient.FimgStoreGetClient;
import org.dea.fimgstoreclient.beans.FimgStoreImgMd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.DocumentException;

import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.builder.ExportCache;
import eu.transkribus.core.util.PageXmlUtils;

public class PdfExporter extends Observable {
	private static final Logger logger = LoggerFactory.getLogger(PdfExporter.class);
	
	public boolean cancel=false;
	
	public PdfExporter(){}
	
	public File export(final TrpDoc doc, final String path, Set<Integer> pageIndices, boolean extraTextPages, boolean highlightTags, boolean wordBased, boolean doBlackening, boolean createTitle) throws DocumentException, MalformedURLException, IOException, JAXBException, URISyntaxException, InterruptedException{
		return export(doc, path, pageIndices, false, false, true, true, false, false, null);
	}
	
	public File export(final TrpDoc doc, final String path, Set<Integer> pageIndices, ExportCache cache) throws DocumentException, MalformedURLException, IOException, JAXBException, URISyntaxException, InterruptedException{
		return export(doc, path, pageIndices, false, true, false, true, true, true, cache);
	}
		
	public File export(final TrpDoc doc, final String path, Set<Integer> pageIndices, final boolean useWordLevel, final boolean addTextPages, final boolean imagesOnly, final boolean highlightTags, final boolean doBlackening, boolean createTitle, ExportCache cache) throws DocumentException, MalformedURLException, IOException, JAXBException, URISyntaxException, InterruptedException{
		if(doc == null){
			throw new IllegalArgumentException("TrpDoc is null!");
		}
		if(path == null){
			throw new IllegalArgumentException("path is null!");
		}
		if(cache == null) {
			cache = new ExportCache();
		}
//		if(startPage == null || startPage < 1) startPage = 1;
//		final int nrOfPages = doc.getPages().size();
//		if(endPage == null || endPage > nrOfPages+1) endPage = nrOfPages;
//		
//		if(startPage > endPage){
//			throw new IllegalArgumentException("Start page must be smaller than end page!");
//		}
		
	
		File pdfFile = new File(path);
		TrpPdfDocument pdf = new TrpPdfDocument(pdfFile, useWordLevel, highlightTags, doBlackening, createTitle);
		
		setChanged();
		notifyObservers("Creating PDF document...");
		
		boolean onePagePrinted = false;

//		for(int i = startPage-1; i <= endPage-1; i++){
		for(int i=0; i<doc.getPages().size(); ++i) {
			if (pageIndices!=null && !pageIndices.contains(i))
				continue;

			logger.info("Processing page " + (i+1));			
			TrpPage p = doc.getPages().get(i);
			URL imgUrl = p.getUrl();	

			/*
			 * md is only needed for getting resolution because in the image it may be missing
			 * But if it is a local doc we have to try to get from img because md is null
			 */
			FimgStoreImgMd md = null;
			if(doc.isRemoteDoc()){
				FimgStoreGetClient getter = new FimgStoreGetClient(p.getUrl());
				md = (FimgStoreImgMd)getter.getFileMd(p.getKey());
			}
		
			URL xmlUrl = p.getCurrentTranscript().getUrl();
			
			logger.debug("output with tags " + highlightTags);
			//PcGtsType pc = PageXmlUtils.unmarshal(xmlUrl);
			
			//should be the same as above
			
			JAXBPageTranscript pt = null;
			if(cache != null) {
				pt = cache.getPageTranscriptAtIndex(i);
			}
			
			PcGtsType pc;
			if (pt != null){
				pc = pt.getPageData();
			}
			else{
				pc = PageXmlUtils.unmarshal(xmlUrl);
			}

			if (!onePagePrinted){
				//add first page and previously add a title page with doc metadata and editorial declarations (if this option is set)
				pdf.addPage(imgUrl, doc, pc, addTextPages, imagesOnly, md, doBlackening, cache);
				onePagePrinted = true;
			}
			else{
				pdf.addPage(imgUrl, null, pc, addTextPages, imagesOnly, md, doBlackening, cache);
			}
			
			setChanged();
			notifyObservers(Integer.valueOf(i+1));
			if (cancel){
				pdf.close();
				File file = new File(path);
				if (!file.delete()) {
				   throw new IOException("Could not delete the incomplete PDF file during export cancel");
				}
				throw new InterruptedException("Export canceled by the user");
				//break;
			}
		}
		if (highlightTags){
			pdf.addTags(doc, pageIndices, useWordLevel, cache);
		}
		pdf.close();
		
		setChanged();
		notifyObservers("PDF written at: " + path);
		
		logger.info("PDF written at: " + path);
		return pdfFile;
	}
}
