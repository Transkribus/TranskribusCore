package eu.transkribus.core.model.builder.iob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.ExportFilePatternUtils;
import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagAttribute;
import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.OrganizationTag;
import eu.transkribus.core.model.beans.customtags.PersonTag;
import eu.transkribus.core.model.beans.customtags.PlaceTag;
import eu.transkribus.core.model.beans.pagecontent.MetadataType;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.builder.ExportCache;
import eu.transkribus.core.model.builder.NoTagsException;


public class TrpIobBuilder {
	
	private final static Logger logger = LoggerFactory.getLogger(TrpIobBuilder.class);
	

	
	public TrpIobBuilder() {
		
	}
	
	public void writeIobForDoc(TrpDoc doc, boolean wordBased, File exportFile, Set<Integer> pageIndices, IProgressMonitor monitor, ExportCache cache) throws NoTagsException, Exception {
		writeIobForDoc(doc,wordBased, exportFile, pageIndices, monitor, cache, false);
	}
	
	public void writeIobForDoc(TrpDoc doc, boolean wordBased, File exportFile, Set<Integer> pageIndices, IProgressMonitor monitor, ExportCache cache, boolean exportProperties) throws NoTagsException, Exception {
		
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
					int offset = 0;
					
					HashMap<Integer, CustomTag> tagMap = new HashMap<Integer, CustomTag>();
					// continueMap needed for multi word entities
					HashMap<String, CustomTag> continueMap = new HashMap<String, CustomTag>();
					
					for(CustomTag tag : tagList) {
						tagMap.put(tag.getOffset(), tag);
						String tokenText = tag.getContainedText();
						StringTokenizer st = new StringTokenizer(tokenText);
						while(st.hasMoreTokens()) {
							String token = st.nextToken();
							continueMap.put(token, tag);
						}			
					}
	
					try {
						StringTokenizer st = new StringTokenizer(lineText);
						
						while(st.hasMoreTokens()) {
							String token = st.nextToken();
							offset = lineText.indexOf(token);
										
							//TODO improve export by working with offset 
							
							
							textLinebw.write(token);
							token = token.replace(",", "").replace(".", "").replace(";", "");
							
							if(tagMap.containsKey(offset)) {
								CustomTag tag = tagMap.get(offset);
								logger.info("Token "+token+ " Tag "+tag.getContainedText()+" Offset Token "+offset+ " Offset Tag"+tag.getOffset());
								if(tag.isContinued() && offset == 0) {
									addContinueTag(tag, textLinebw, exportProperties);
								
								}else if(offset == tag.getOffset()) {
									addBeginningTag(tag, textLinebw, exportProperties);
								
								}
								else {
									textLinebw.write("\t O");
								}
							}else if (continueMap.containsKey(token)){
								CustomTag tag = continueMap.get(token);
								addContinueTag(tag, textLinebw, exportProperties);
							}
		
							else {
								textLinebw.write("\t O");
							}
							
							textLinebw.newLine();
							
						}
						
					}catch (IOException e) {
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
	
	private void addBeginningTag(CustomTag temp, BufferedWriter textLinebw, boolean exportProperties) throws IOException {
		if(temp.getTagName().equals("person")) {
				textLinebw.write("\t B-PER");
				if(exportProperties) {
					addPropsToFile(temp, textLinebw);
				}
		}else if(temp.getTagName().equals("place")){
			textLinebw.write("\t B-LOC");
			if(exportProperties) {
				addPropsToFile(temp, textLinebw);
			}
		}else if(temp.getTagName().equals("organization")){
			textLinebw.write("\t B-ORG");
			if(exportProperties) {
				addPropsToFile(temp, textLinebw);
			}
		}else if(temp.getTagName().equals("human_production")){
			textLinebw.write("\t B-HumanProd");
			if(exportProperties) {
				addPropsToFile(temp, textLinebw);
			}
		}
		
	}
	
	private void addContinueTag(CustomTag temp, BufferedWriter textLinebw, boolean exportProperties) throws IOException {
		if(temp.getTagName().equals("person")) {
				textLinebw.write("\t I-PER");
				if(exportProperties) {
					addPropsToFile(temp, textLinebw);
				}
		}else if(temp.getTagName().equals("place")){
			textLinebw.write("\t I-LOC");
			if(exportProperties) {
				addPropsToFile(temp, textLinebw);
			}
		}else if(temp.getTagName().equals("organization")){
			textLinebw.write("\t I-ORG");
			if(exportProperties) {
				addPropsToFile(temp, textLinebw);
			}
		}else if(temp.getTagName().equals("human_production")){
			textLinebw.write("\t I-HumanProd");
			if(exportProperties) {
				addPropsToFile(temp, textLinebw);
			}
		}
		
	}
	
	
	
	private void addPropsToFile(CustomTag temp, BufferedWriter textLinebw) throws IOException {
		
		if(!temp.getAttributes().isEmpty()) {
			Map<String, Object> attrMap = temp.getAttributeNamesValuesMap();
			// Method for NewsEye GT creation
			if(attrMap.get("nel") == null) {
				textLinebw.write("\t null");
			}else {
				textLinebw.write("\t "+attrMap.get("nel"));
			}
			
			if(attrMap.get("stance") == null) {
				textLinebw.write("\t null");
			}else {
				textLinebw.write("\t "+attrMap.get("stance"));
			}
			
			if(temp.getTagName().equals("person")) {
				if(attrMap.get("author") == null) {
					textLinebw.write("\t author=false");
				}else {
					textLinebw.write("\t author="+attrMap.get("author"));
				}
			}
		}	
	}

	public ArrayList<Pair<Integer, PcGtsType >> importIOBForDocPair(TrpDoc doc,File importFile) throws IOException{
		
		// Read IOB file line for line and save words + tag in a map
		List<CustomPair<String,String>> wordTagList = wordTagsToMap(importFile);
		
		ArrayList <Pair <Integer,PcGtsType>> idPageList =  new ArrayList <Pair <Integer,PcGtsType> > (); 
		
		List<TrpPage> pages = doc.getPages();
		
		int wordCount = -1;
		
		for(TrpPage page : pages) {
			TrpTranscriptMetadata md = page.getCurrentTranscript();
			JAXBPageTranscript tr = new JAXBPageTranscript(md);
			tr.build();
			
			MetadataType mdType = tr.getPage().getPcGtsType().getMetadata();
			if (mdType == null) {
				mdType = new MetadataType();
				tr.getPage().getPcGtsType().setMetadata(mdType);
			}		
			
			TrpPageType t = (TrpPageType)tr.getPage();
						
			List<TrpTextLineType> lines = t.getLines();
			
			for(TrpTextLineType line : lines) {
				
				CustomTagList tagLines = line.getCustomTagList();
				List<CustomTag> tagList = tagLines.getIndexedTags();
				
				String lineText = line.getUnicodeText();
				String[] wordToken = lineText.split(" ");
				int offset = 0;
				for(int i=0; i < wordToken.length; i++) {
					wordCount++;
					String word = wordToken[i].replace(" ", "");
					
					if(wordCount < wordTagList.size()) {
						if(word.equals(wordTagList.get(wordCount).getL().replace(" ", "")) && !wordTagList.get(wordCount).getR().replaceAll(" ","").equals("O") ) {
							logger.info("Found word and add to tagline ; word : "  +wordTagList.get(wordCount).getL() + " with tag : "+wordTagList.get(wordCount).getR());
							tagLines = setTagsToList(tagList, tagLines, wordTagList.get(wordCount).getR(), offset ,wordTagList.get(wordCount).getL().length());
						}
					}
					offset += word.length()+1;
					
				}
			}
			idPageList.add(Pair.of(page.getPageId(), t.getPcGtsType()));
		}
	
		return idPageList;
		
	}
	
	public void importIOBForDoc(TrpDoc doc,File importFile, String pathDir) throws IOException {
		
		// Read IOB file line for line and save words + tag in a map
		List<CustomPair<String,String>> wordTagList = wordTagsToMap(importFile);
		
		logger.info("Size of wordTagList : "+wordTagList.size());
				
		List<TrpPage> pages = doc.getPages();
		
		int wordCount = -1;
		
		for(TrpPage page : pages) {
			
			
			final String baseFileName = ExportFilePatternUtils.buildBaseFileName(ExportFilePatternUtils.FILENAME_PATTERN, page);
			
			TrpTranscriptMetadata md = page.getCurrentTranscript();
			JAXBPageTranscript tr = new JAXBPageTranscript(md);
			tr.build();
			
			MetadataType mdType = tr.getPage().getPcGtsType().getMetadata();
			if (mdType == null) {
				mdType = new MetadataType();
				tr.getPage().getPcGtsType().setMetadata(mdType);
			}		
			
			TrpPageType t = (TrpPageType)tr.getPage();
						
			List<TrpTextLineType> lines = t.getLines();
			
			for(TrpTextLineType line : lines) {
				
				CustomTagList tagLines = line.getCustomTagList();
				List<CustomTag> tagList = tagLines.getIndexedTags();
				
				
				String lineText = line.getUnicodeText();
				String[] wordToken = lineText.split(" ");
				int offset = 0;
				for(int i=0; i < wordToken.length; i++) {
					wordCount++;
					String word = wordToken[i].replace(" ", "");
					
					if(wordCount < wordTagList.size()) {
						if(word.equals(wordTagList.get(wordCount).getL().replace(" ", "")) && !wordTagList.get(wordCount).getR().replaceAll(" ","").equals("O") ) {
							logger.info("Found word and add to tagline ; word : "  +wordTagList.get(wordCount).getL() + " with tag : "+wordTagList.get(wordCount).getR());
							tagLines = setTagsToList(tagList, tagLines, wordTagList.get(wordCount).getR(), offset ,wordTagList.get(wordCount).getL().length());
						}
					}
					offset += word.length()+1;
					
				}
			}
			
			tr.setPageData(t.getPcGtsType());
			
			File xmlFile = new File(pathDir  
					+ File.separator +"page"+File.separator+ baseFileName +".xml");
			
			logger.debug("PAGE XMl output file: "+xmlFile.getAbsolutePath());
			tr.write(xmlFile);
			
		}	
	}
	
	private CustomTagList setTagsToList(List<CustomTag> tagList,CustomTagList tagLines, String tagType, int offset, int length){
		
		if(tagType.equals("B-PER") || tagType.equals("I-PER")) {
			PersonTag tag = new  PersonTag( );
			tag.setOffset(offset);
			tag.setLength(length);
			tagLines.addOrMergeTag(tag, null);
			
		}else if (tagType.equals("B-LOC") || tagType.equals("I-LOC")) {
			PlaceTag tag = new  PlaceTag( );
			tag.setOffset(offset);
			tag.setLength(length);
			tagLines.addOrMergeTag(tag, null);
			
			
		}else if (tagType.equals("B-ORG") || tagType.equals("I-ORG")) {
			OrganizationTag tag = new  OrganizationTag( );
			tag.setOffset(offset);
			tag.setLength(length);
			tagLines.addOrMergeTag(tag, null);
		}
		
		return tagLines;
	}
	
	private List<CustomPair<String,String>> wordTagsToMap(File importFile) throws IOException {

		List<CustomPair<String,String>> wordTagList = new ArrayList<CustomPair<String,String>>();
		BufferedReader br = new BufferedReader(new FileReader(importFile));
		try {
		    String line;
		    while ((line = br.readLine()) != null) {
		       String[] list = line.split("\\t");
		       wordTagList.add(new CustomPair<String, String>(list[0].replaceAll(" ",""), list[1].replaceAll(" ","")));
		    }
		} finally {
		    br.close();
		}
		return wordTagList;
	}
	
	
	public static void main(String[] args) throws Exception {
		

		TrpDoc docWithTags = LocalDocReader.load("/home/lateknight/Downloads/barbara_stefan_sarah_annotation/313397/ONB_inter_annotator_doc_v1");
		
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
		exportCache.storeCustomTagMapForDoc(docWithTags, false, pageIndices, null, false);
		
		TrpIobBuilder iob = new TrpIobBuilder();
		iob.writeIobForDoc(docWithTags, false, new File("/home/lateknight/Desktop/barbara_stefan_sarah_inter_annotatorV3.txt"), pageIndices, null, exportCache, true);
		
		System.out.println("finished");
		
		//TODO write test for IOB import
		
		
//		TrpDoc docWithoutTags = LocalDocReader.load("/home/lateknight/Desktop/Iob_Import/ONB_nfp_18950706_NE_GT_duplicated/");
//		File file = new File("/home/lateknight/Desktop/Iob_Import/ONB_NE_GT_tags.txt");
//		
//		iob.importIOBForDoc(docWithoutTags, file, "/home/lateknight/Desktop/Iob_Import/ONB_nfp_18950706_NE_GT_duplicated/");
	}
	
	public class CustomPair<L,R> {
	    private L l;
	    private R r;
	    public CustomPair(L l, R r){
	        this.l = l;
	        this.r = r;
	    }
	    public L getL(){ return l; }
	    public R getR(){ return r; }
	    public void setL(L l){ this.l = l; }
	    public void setR(R r){ this.r = r; }
	}


}

