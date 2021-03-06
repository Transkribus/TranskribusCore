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

import org.apache.commons.lang3.Range;
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
import eu.transkribus.core.util.IntRange;


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
		
		
		if(exportProperties) {
			textLinebw.write("# Token\tTag\tNested-Tag\tFine-Grained\tWikidataID\tStance\tIsSpaceAfter");
			textLinebw.newLine();

		}else {
			textLinebw.write("# Token\tTag\tNested-Tag\tIsSpaceAfter/EndOfLine");
			textLinebw.newLine();

		}
			
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
					StringTokenizer st = new StringTokenizer(lineText, " .,();\"„“?!»«'’—-",true);
					
					// Split textLine by tag offset 
					if(tagList.isEmpty()) {
						
					while(st.hasMoreTokens()) {
							String token = st.nextToken();
							if(token.equals(" ")) {
								textLinebw.write("\tSpaceAfter");
								continue; 
							}
							textLinebw.newLine();
							textLinebw.write(token);
							textLinebw.write("\tO\tO\tO\tnull\tnull");
							
						}
					}else {
						
						HashMap<String, CustomTag> tagMap = new HashMap<String, CustomTag>();
						
						List<String> elements = new ArrayList<String>();
						
						
						

						ArrayList<IntRange> rangeList = new ArrayList<>();
						
						for(CustomTag tag : tagList) {
							
							String tagText = lineText.substring(tag.getOffset(), tag.getEnd());	
							
							IntRange tagRange = tag.getRange();
				            System.out.println("TagRange "+tagRange.getOffset()+ " " + tagRange.getEnd()+" Sentence length : "+lineText.length() );

							rangeList.add(tagRange);
							
							StringTokenizer tagToken = new StringTokenizer(tagText, " .,();\"„“?!»«'’—-",true);		
							List<String> tagElements = new ArrayList<String>();
							
							 while(tagToken.hasMoreTokens()) {
								 tagElements.add(tagToken.nextToken());
					        }
							// get offset of tag in tag Text
							 int offset = tag.getOffset()-1;
							 for(String s: tagElements) {
							     offset = lineText.indexOf(s, offset + 1);
							     tagMap.put(s+""+offset, tag);
							    
							 }
							
						}
						int tokenOffset = -1;
						
						while(st.hasMoreTokens()) {
							String token = st.nextToken();
							tokenOffset = lineText.indexOf(token, tokenOffset + 1); 
							
							if(!token.equals(" ")) {
								boolean written = false;
								textLinebw.newLine();
								
								for(IntRange iRange : rangeList)
						        {
									String tagText = lineText.substring(iRange.getOffset(), iRange.getEnd()).replace(" ", "");
							    	List<CustomTag> overlap = tagLines.getOverlappingTags(null,iRange.getOffset(),iRange.getLength());
									
									CustomTag first = overlap.get(0);
									CustomTag nested = null;
									
									//solve continuation issue
//									
									
						    		if(overlap.size() > 1) {	    			
						    			nested = overlap.get(1);
						    		}
									if(iRange.isInside(tokenOffset)) {
							            
								    	//Split subwords
								    	 String[] splitted = token.split(tagText.replace("(", "\\(").replace(")", "\\)"));
								    	 if(splitted.length > 1 ) {										 
											 if(token.startsWith(tagText)) {
												textLinebw.write(tagText);
												addBeginningTag(first, textLinebw, exportProperties);
									    		textLinebw.newLine();
												textLinebw.write(splitted[1]);
										    	textLinebw.write("\tO\tO\tO\tnull\tnull");
										    	written= true;
											 }
											 
								    	 }else {
								    		 // get nestings
								    		 if(nested != null && !written && nested.getContainedText().contains(token)) {
								    			 if(tagText.startsWith(token) && nested.getContainedText().startsWith(token)) {
											    		textLinebw.write(token);
											    		addNestedTag(first, textLinebw, "B");
														addNestedTag(nested, textLinebw, "B");
														addPropsToFile(first, textLinebw);
														written= true;
									    			}else if(tagText.contains(token) && nested.getContainedText().startsWith(token)) {
											    		textLinebw.write(token);
											    		addNestedTag(first, textLinebw, "I");
														addNestedTag(nested, textLinebw, "B");
														addPropsToFile(first, textLinebw);
														written= true;
											    	}else {
											    		textLinebw.write(token);
											    		addNestedTag(first, textLinebw, "I");
														addNestedTag(nested, textLinebw, "I");
														addPropsToFile(first, textLinebw);
														written= true;
											    	}
								    		 }else if(!written){
								    			 //Unnested
								    			 if(tokenOffset == 0 && first.isContinued()) {
								    				 	Pair<CustomTagList, CustomTag> continuedPrevoiusPair = tagLines.getPreviousContinuedCustomTag(first);
														if(continuedPrevoiusPair != null && continuedPrevoiusPair.getValue().getContainedText().equals(tagText) ) {
															textLinebw.write(token);
										    				addContinueTag(first, textLinebw, exportProperties);
														}else {
															textLinebw.write(token);
															addBeginningTag(first, textLinebw, exportProperties);
														}	
									    				written= true;
								    				}else if(tagText.replace(" ", "").startsWith(token) ) {
									    				textLinebw.write(token);
														addBeginningTag(first, textLinebw, exportProperties);
														written= true;
									    			}else {
									    				textLinebw.write(token);
									    				addContinueTag(first, textLinebw, exportProperties);
									    				written= true;
									    			}
								    			 
								    		 }  		 
					    		 
								    	 }

							    	}
		
						        }
								if(!written) {
									 textLinebw.write(token);
							    	 textLinebw.write("\tO\tO\tO\tnull\tnull");
								}
							}else {
								textLinebw.write("\tSpaceAfter");
								continue; 
							}
						}
					}	
				}
				
				++c;
				if (monitor!=null) {
					monitor.worked(c);
				}
			}
		}
		textLinebw.close();
	}
	
	private void addNestedTag(CustomTag temp, BufferedWriter textLinebw, String preFix) throws IOException {
		if(temp.getTagName().equals("person")) {
				textLinebw.write("\t"+preFix+"-PER");
		}else if(temp.getTagName().equals("place")){
			textLinebw.write("\t"+preFix+"-LOC");
		}else if(temp.getTagName().equals("organization")){
			textLinebw.write("\t"+preFix+"-ORG");
		}else if(temp.getTagName().equals("human_production")){
			textLinebw.write("\t"+preFix+"-HumanProd");
		}
		
	}
	
	private void addBeginningTag(CustomTag temp, BufferedWriter textLinebw, boolean exportProperties) throws IOException {
		if(temp.getTagName().equals("person")) {
				textLinebw.write("\tB-PER\tO");
				if(exportProperties) {
					addPropsToFile(temp, textLinebw);
				}
		}else if(temp.getTagName().equals("place")){
			textLinebw.write("\tB-LOC\tO");
			if(exportProperties) {
				addPropsToFile(temp, textLinebw);
			}
		}else if(temp.getTagName().equals("organization")){
			textLinebw.write("\tB-ORG\tO");
			if(exportProperties) {
				addPropsToFile(temp, textLinebw);
			}
		}else if(temp.getTagName().equals("human_production")){
			textLinebw.write("\tB-HumanProd\tO");
			if(exportProperties) {
				addPropsToFile(temp, textLinebw);
			}
		}
		
	}
	
	private void addContinueTag(CustomTag temp, BufferedWriter textLinebw, boolean exportProperties) throws IOException {
		if(temp.getTagName().equals("person")) {
				textLinebw.write("\tI-PER\tO");
				if(exportProperties) {
					addPropsToFile(temp, textLinebw);
				}
		}else if(temp.getTagName().equals("place")){
			textLinebw.write("\tI-LOC\tO");
			if(exportProperties) {
				addPropsToFile(temp, textLinebw);
			}
		}else if(temp.getTagName().equals("organization")){
			textLinebw.write("\tI-ORG\tO");
			if(exportProperties) {
				addPropsToFile(temp, textLinebw);
			}
		}else if(temp.getTagName().equals("human_production")){
			textLinebw.write("\tI-HumanProd\tO");
			if(exportProperties) {
				addPropsToFile(temp, textLinebw);
			}
		}
		
	}
	
	
	
	private void addPropsToFile(CustomTag temp, BufferedWriter textLinebw) throws IOException {
		
		if(!temp.getAttributes().isEmpty()) {
			Map<String, Object> attrMap = temp.getAttributeNamesValuesMap();
			// Method for NewsEye GT creation
			
			if(temp.getTagName().equals("person")) {
				if(attrMap.get("author") == null) {
					textLinebw.write("\tO");
				}else {
					String isAuthor = (String) attrMap.get("author");
					if(isAuthor.equals("true")) {
						textLinebw.write("\tPER.author");
					}else {
						textLinebw.write("\tO");
					}
					
				}
			}else {
				textLinebw.write("\tO");
			}
			
			if(attrMap.get("nel") == null) {
				textLinebw.write("\tnull");
			}else {
				// replace space
				String nelLink = (String) attrMap.get("nel");
				textLinebw.write("\t"+nelLink.replace("https://www.wikidata.org/wiki/", ""));
			}
			
			if(attrMap.get("stance") == null) {
				textLinebw.write("\tn");
			}else {
				textLinebw.write("\t"+attrMap.get("stance"));
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
		

		TrpDoc docWithTags = LocalDocReader.load("/home/lateknight/Documents/NewsEye/NE_GT_dataset_v0.95/BNF/BNF_GT/269290/BnF_GT_Document");
		Set<Integer> pageIndices = null;
		
		if (pageIndices == null){
			pageIndices = new HashSet<Integer>();
			for (int i = 0; i<docWithTags.getNPages(); i++){
				pageIndices.add(i);
			}
		}
		ExportCache exportCache = new ExportCache();
		exportCache.storeCustomTagMapForDoc(docWithTags, false, pageIndices, null, false);
		
		TrpIobBuilder iob = new TrpIobBuilder();
		iob.writeIobForDoc(docWithTags, false, new File("/home/lateknight/Documents/NewsEye/NE_GT_dataset_v0.95/BNF/BNF_GT-IOB.txt"), pageIndices, null, exportCache, true);

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

