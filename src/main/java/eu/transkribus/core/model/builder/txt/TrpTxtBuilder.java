package eu.transkribus.core.model.builder.txt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.model.beans.EdFeature;
import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpElementReadingOrderComparator;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpShapeTypeUtils;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTableRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.builder.ExportCache;
import eu.transkribus.core.model.builder.ExportUtils;

public class TrpTxtBuilder {
	
	private final static Logger logger = LoggerFactory.getLogger(TrpTxtBuilder.class);
	
	public static Charset utf8 = StandardCharsets.UTF_8;

	public static void main(String[] args) {
		//final String path = "/mnt/dea_scratch/TRP/Bentham_box_002_GT";
		final String path = "X:/TRP/Bentham_box_002_GT";
//		final String path = "/mnt/dea_scratch/TRP/Schauplatz_Small";
		
		try {
			TrpDoc doc = LocalDocReader.load(path);
			TrpTxtBuilder txtBuilder = new TrpTxtBuilder();
			txtBuilder.writeTxtForDoc(doc, true, false, true, new File("TxtExportTest.txt"), null, null, new ExportCache());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Docx4JException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void writeTxtForDoc(TrpDoc doc, boolean addTitle, boolean wordBased, boolean preserveLineBreaks, File file, Set<Integer> pageIndices, IProgressMonitor monitor, ExportCache cache) throws JAXBException, IOException, Docx4JException, InterruptedException {
	
		//delete file if already exists
		Files.deleteIfExists(Paths.get(file.getAbsolutePath()));
		if(addTitle){
			addTitlePage(doc, file);
		}
		
		List<TrpPage> pages = doc.getPages();
		
		int totalPages = pageIndices==null ? pages.size() : pageIndices.size();
		if (monitor!=null) {
			monitor.beginTask("Exporting to text file", totalPages);
		}
		
		int c=0;
				
		for (int i=0; i<pages.size(); ++i) {
				
			if (pageIndices!=null && !pageIndices.contains(i))
				continue;
						
			if (monitor!=null) {
				if (monitor.isCanceled()) {					
					throw new InterruptedException("Export canceled by the user");
				}
				monitor.subTask("Processing page "+(c+1));
			}
			
			JAXBPageTranscript tr = null;
			if(cache != null) {
				tr = cache.getPageTranscriptAtIndex(i);
			}
			if (tr == null){
				TrpPage page = pages.get(i);
				TrpTranscriptMetadata md = page.getCurrentTranscript();
				//md.getStatus().equals("Done");
				tr = new JAXBPageTranscript(md);
				tr.build();
			}
			
			TrpPageType trpPage = tr.getPage();
			
			logger.debug("writing text file for the page "+(i+1)+"/"+pages.size());

			writeTxtForSinglePage(file, trpPage, wordBased, preserveLineBreaks);
			++c;		
			
			if (monitor!=null) {
				monitor.worked(c);
			}
		}
		
		logger.debug("Saved " + file.getAbsolutePath());
		
	}
	
	private void writeTxtForSinglePage(File file, TrpPageType trpPage, boolean wordBased, boolean preserveLineBreaks) {
		boolean rtl = false;
		
		//TrpTableRegionType is contained in the regions too
		List<TrpRegionType> regions = trpPage.getRegions();
//		Collections.sort(regions, new TrpElementReadingOrderComparator<RegionType>(true));
		TrpShapeTypeUtils.sortShapesByReadingOrderOrCoordinates(regions);
		
		List<String> content = new ArrayList<String>();
		
		for (int j=0; j<regions.size(); ++j) {
			TrpRegionType r = regions.get(j);
			
			//logger.debug("region " + j);
			
			if (r instanceof TrpTableRegionType){
				/*
				 * TODO: for simple txt export: how to handle tables
				 */
				continue;
			}
			else if (r instanceof TrpTextRegionType){
				
				TrpTextRegionType tr = (TrpTextRegionType) r;
				List<TextLineType> lines = tr.getTextLine();
				
				for (int i=0; i<lines.size(); ++i) {
					TrpTextLineType trpL = (TrpTextLineType) lines.get(i);
											
					String textOfCurrLine = trpL.getUnicodeText();
					
					if (wordBased && trpL.getWord().size()>0){
						for (WordType word : trpL.getWord()){
							content.add(((ITrpShapeType) word).getUnicodeText());
						}
					}
					else if (textOfCurrLine != ""){
						content.add(textOfCurrLine);
					}
//					if(preserveLineBreaks){
//						content.add(System.lineSeparator());
//					}
				}
				
				if (lines.size() > 0){
					content.add(System.lineSeparator());
//					try {
//						//Add line separator after each region
//						Files.write(Paths.get(file.getAbsolutePath()), new ArrayList<String>() {{ add(System.lineSeparator()); }}, utf8,
//						        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				}
			}

		}
	    try {
	    	logger.debug("path " + Paths.get(file.getAbsolutePath()));
			Files.write(Paths.get(file.getAbsolutePath()), content, utf8,
			        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	public void addTitlePage(TrpDoc doc, File file) {
		
		List<String> titleContent = new ArrayList<String>();
		
		titleContent.add("----------------------------");
		titleContent.add("Metadata section of document");
		titleContent.add("----------------------------");
		
		TrpDocMetadata docMd = doc.getMd();
		
		titleContent.add("Title: " + docMd.getTitle());
	    
		titleContent.add("Author: " + docMd.getAuthor());

		titleContent.add("Description: " + docMd.getDesc());
		
		titleContent.add("Genre: " + docMd.getGenre());
		
		titleContent.add("Writer: " + docMd.getWriter());
		
		if (docMd.getScriptType() != null){
			titleContent.add("Sripttype: " + docMd.getScriptType().toString());
		}
		
		titleContent.add("Language: " + docMd.getLanguage());
		
		titleContent.add("Number of Pages in whole Document: " + String.valueOf(docMd.getNrOfPages()));
		
		if (docMd.getCreatedFromDate() != null){
			titleContent.add("Created From: " + docMd.getCreatedFromDate().toString());
		}
		
		if (docMd.getCreatedToDate() != null){
			titleContent.add("Created To: " + docMd.getCreatedToDate().toString());
		}
		
		titleContent.add("Editorial Declaration: ");
		for (EdFeature edfeat : doc.getEdDeclList()){
			titleContent.add(edfeat.getTitle() + ": " + edfeat.getDescription() + System.lineSeparator() + edfeat.getSelectedOption().toString());
		}
		
		titleContent.add("-----------------------");
		titleContent.add("End of metadata section");
		titleContent.add("-----------------------");
		titleContent.add(System.lineSeparator());
		
	    try {
			Files.write(Paths.get(file.getAbsolutePath()), titleContent, utf8,
			        StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
