package eu.transkribus.core.model.builder.iob;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagFactory;
import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpWordType;
import eu.transkribus.core.model.builder.ExportCache;
import eu.transkribus.core.model.builder.ExportUtils;
import eu.transkribus.core.model.builder.NoTagsException;


public class TrpIobBuilder {
	private final static Logger logger = LoggerFactory.getLogger(TrpIobBuilder.class);
	
	
	public TrpIobBuilder() {
		
	}
	
	public void writeIobForDoc(TrpDoc doc, boolean wordBased, File exportFile, Set<Integer> pageIndices, IProgressMonitor monitor, ExportCache cache) throws NoTagsException, Exception {
		
		if(cache == null) {
			throw new IllegalArgumentException("ExportCache must not be null.");
		}
		
		String exportPath = exportFile.getPath();
		
		FileOutputStream fOut;
		try {

		
			fOut = new FileOutputStream(exportPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		
		BufferedWriter textLinebw = new BufferedWriter(new OutputStreamWriter(fOut));
				
		/*
		 * write IOB only if tags are available - otherwise say 'No tags available for the chosen export' when exporting on Server
		 * otherwise the user doesn't know what's happening
		 */
		if (!cache.getCustomTagMapForDoc().isEmpty()) {
			logger.info("Tags available for export!");

			List<TrpPage> pages = doc.getPages();
			
			//TODO get first 3 letters of all selected tags and create IOB schmema
			// example : abbreviation -> B-abb
//			Set<String> selectedTags = cache.getOnlySelectedTagnames(ExportUtils.getOnlyWantedTagnames(CustomTagFactory.getRegisteredTagNames()));
								
			int totalPages = pageIndices==null ? pages.size() : pageIndices.size();
			if (monitor!=null) {
				monitor.beginTask("Exporting to IOB", totalPages);
			}

			int c=0;
			for (int i=0; i<pages.size(); ++i) {
				if (pageIndices!=null && !pageIndices.contains(i))
					continue;
				
				if (monitor!=null) {
					if (monitor.isCanceled()) {
						throw new InterruptedException("Export was canceled by user");
					}
					monitor.subTask("Processing page "+(c+1));
				}
				
				TrpPage page = pages.get(i);
				//try to get previously loaded JAXB transcript
				JAXBPageTranscript tr = null;
				if(cache != null) {
					tr = cache.getPageTranscriptAtIndex(i);
				}
				if (tr == null){
					TrpTranscriptMetadata md = page.getCurrentTranscript();
					tr = new JAXBPageTranscript(md);
					tr.build();
				}
			
				TrpPageType t = (TrpPageType)tr.getPage();
				
				List<TrpTextLineType> lines = t.getLines();
				
				
					for(TrpTextLineType line : lines) {
						
						CustomTagList tagLines = line.getCustomTagList();
						List<CustomTag> tagList = tagLines.getIndexedTags();

						String lineText = line.getUnicodeText();
						HashMap<String, CustomTag> tagMap = new HashMap<String, CustomTag>();
						
						for(CustomTag tag : tagList) {
							tagMap.put(tag.getContainedText(), tag);
						}
		
							try {
								StringTokenizer st = new StringTokenizer(lineText);
								while(st.hasMoreTokens()) {
									String token = st.nextToken();
									textLinebw.write(token);
									token = token.replace(",", "").replace(".", "").replace(";", "");
		
									boolean entityWritten= false;
									CustomTag tag = tagMap.get(token);
									// Handle continued tags
									if(tagMap.containsKey(token)) {
										if(tag.getTagName().equals("person")) {
											if(tag.isContinued() && tag.getOffset() == 0) {
												textLinebw.write("\t I-PER");
											}else {
												textLinebw.write("\t B-PER");
											}
											
											entityWritten = true;
										}else if(tag.getTagName().equals("place")) {
											if(tag.isContinued() && tag.getOffset() == 0) {
												textLinebw.write("\t I-LOC");
											}else {
												textLinebw.write("\t B-LOC");
											}
											entityWritten = true;
										}else if(tag.getTagName().equals("organization")) {
											if(tag.isContinued() && tag.getOffset() == 0) {
												textLinebw.write("\t I-ORG");
											}else {
												textLinebw.write("\t B-ORG");
											}
											entityWritten = true;
										}
									}else {
										for(Map.Entry<String, CustomTag> entry : tagMap.entrySet()) {
											CustomTag temp = entry.getValue();
											if(entry.getKey().startsWith(token)) {
												if(temp.getTagName().equals("person")) {
													textLinebw.write("\t B-PER");
													entityWritten = true;
												}else if(temp.getTagName().equals("place")) {
													textLinebw.write("\t B-LOC");
													entityWritten = true;
												}else if(temp.getTagName().equals("organization")) {
													textLinebw.write("\t B-ORG");
													entityWritten = true;
												}
											}
											Set<String> tokenSplit = new HashSet<String>(
													Arrays.asList(entry.getKey().split(" "))
													);
											if(!entityWritten && (tokenSplit.contains(token) || entry.getKey().endsWith(token))) {
												if(temp.getTagName().equals("person")) {
													textLinebw.write("\t I-PER");
													entityWritten = true;
												}else if(temp.getTagName().equals("place")) {
													textLinebw.write("\t I-LOC");
													entityWritten = true;
												}else if(temp.getTagName().equals("organization")) {
													textLinebw.write("\t I-ORG");
													entityWritten = true;
												}
											}
										}
									}
									if(!entityWritten) {
										textLinebw.write("\t O");
									}
								
									textLinebw.newLine();
	
								}							
							} catch (IOException e) {
								e.printStackTrace();
							}
						
					}

				++c;
				if (monitor!=null) {
					monitor.worked(c);
				}
			}
			textLinebw.close();

		}
	}
	
	
	public static void main(String[] args) throws Exception {
		

		TrpDoc docWithTags = LocalDocReader.load("/home/lateknight/Documents/Master_Thesis/datensatz/151202/ONB_nfp_18950706_duplicated/");
		
		/*
		 * here we store the page transcripts for all later exports regarding to the wished version status
		 * if status can not be found -> we get the latest one, so values 
		 *  
		 */
		
		Set<Integer> pageIndices = null; // null means every page
		
		//pageIndices must be set here instead of being null because it gets used in ExportUtils
		if (pageIndices == null){
			pageIndices = new HashSet<Integer>();
			for (int i = 0; i<docWithTags.getNPages(); i++){
				pageIndices.add(i);
			}
		}
		ExportCache exportCache = new ExportCache();
//		exportCache.storePageTranscripts4Export(docWithTags, pageIndices, null, "Latest", -1, null);
		exportCache.storeCustomTagMapForDoc(docWithTags, false, pageIndices, null, false);
		
		TrpIobBuilder iob = new TrpIobBuilder();
		iob.writeIobForDoc(docWithTags, false, new File("/home/lateknight/Desktop/TagsText.txt"), pageIndices, null, exportCache);
		
		System.out.println("finished");
	}


}
