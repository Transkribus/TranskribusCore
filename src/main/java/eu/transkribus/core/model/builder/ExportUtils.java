package eu.transkribus.core.model.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.customtags.BlackeningTag;
import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.ReadingOrderTag;
import eu.transkribus.core.model.beans.customtags.StructureTag;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpWordType;
import eu.transkribus.core.util.CoreUtils;

public class ExportUtils {
	private final static Logger logger = LoggerFactory.getLogger(ExportUtils.class);
	
	static boolean exportTags = true;
	static boolean doBlackening = false;
	static List<JAXBPageTranscript> pageTranscripts = null;
	
	static LinkedHashMap<CustomTag, String> tags = new LinkedHashMap<CustomTag, String>();
	static Set<String> selectedTags = new HashSet<String>();
	
	public static Set<String> getSelectedTags() {
		return selectedTags;
	}

	public static void setSelectedTags(Set<String> selectedTags) {
		ExportUtils.selectedTags = selectedTags;
	}

	static List<String> persons = new ArrayList<String>();
	static List<String> places = new ArrayList<String>();

	public static void storePageTranscripts4Export(TrpDoc doc, Set<Integer> pageIndices, IProgressMonitor monitor, String versionStatus, int pageIdx, TrpTranscriptMetadata loadedTranscript) throws Exception{
		
		pageTranscripts = new ArrayList<JAXBPageTranscript>();

		List<TrpPage> pages = doc.getPages();
		
		int totalPages = pages.size();	
		int c = 0;
		
		for (int i=0; i<totalPages; ++i) {
			//logger.debug(" i " + i);
			if (pageIndices!=null && !pageIndices.contains(i)){
				//fill up with null to have the proper index of each page later on
				pageTranscripts.add(null);
				continue;
			}
			
			if (monitor != null && monitor.isCanceled()){
				throw new Exception("User canceled the export");
			}
			
			TrpPage page = pages.get(i);
			
			TrpTranscriptMetadata md = null; 
			if (versionStatus.contains("Latest")){
				//current transcript
				md = page.getCurrentTranscript();
			}
			else if (versionStatus.contains("Loaded")){
				//if loaded page idx == i than we can export the loaded version and for all other pages the latest
				if (i==pageIdx && loadedTranscript != null){
					md = loadedTranscript;
//					String loadedStatus = loadedTranscript.getStatus().getStr();
//					md = page.getTranscriptWithStatus(loadedStatus);
				}
				else{
					md = page.getCurrentTranscript();
				}
			}
			else{
				//logger.debug("We want to export pages with status: " + versionStatus);
				md = page.getTranscriptWithStatusOrNull(versionStatus);
			}
			
			/*
			 * for pages where we have not found versions with the defined status -> remove from the page list so that
			 * they will not exported
			 */
			if (md==null){
				logger.debug("remove page index " + i);
				if (pageIndices!=null && pageIndices.contains(i)){
					pageIndices.remove(new Integer(i));
				}
				continue;
			}

			JAXBPageTranscript tr = new JAXBPageTranscript(md);
			tr.build();
			pageTranscripts.add(tr);
			
			logger.debug("Loaded Transcript from page " + (i+1));
			
			if (monitor != null){
				monitor.setTaskName("Loaded Transcript from page " + (i+1));
				monitor.worked(++c);
			}

		}
	}
	
	/***
	 * 
	 * @param doc
	 * @param wordBased
	 * @param pageIndices
	 * @param blackening 
	 * @return all (custom) tags of the given document
	 * @throws JAXBException
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static void storeCustomTagMapForDoc(TrpDoc doc, boolean wordBased, Set<Integer> pageIndices, IProgressMonitor monitor, boolean blackening) throws JAXBException, IOException, InterruptedException {
		
		doBlackening = blackening;
		tags.clear();
		List<TrpPage> pages = doc.getPages();
		
		int totalPages = pages.size();		
		int c = 0;
		
		for (int i=0; i<totalPages; ++i) {
			if (pageIndices!=null && !pageIndices.contains(i))
				continue;
			
			if (monitor != null && monitor.isCanceled()){
				throw new InterruptedException("User canceled the export");
			}
			
			//pageTranscripts get fetched before the custom tag map is stored - so normally pageTranscripts.get(i) != null
			JAXBPageTranscript tr;
			if (pageTranscripts == null || pageTranscripts.get(i) == null){
				TrpPage page = pages.get(i);
				TrpTranscriptMetadata md = page.getCurrentTranscript();
				tr = new JAXBPageTranscript(md);
			}
			else{
				tr = pageTranscripts.get(i);
				tr.getPageData();
			}
			 
			tr.build();
			TrpPageType trpPage = tr.getPage();
			
			logger.debug("get tags for page "+(i+1)+"/"+doc.getNPages());
			
			List<TrpTextRegionType> textRegions = trpPage.getTextRegions(true);

			for (int j=0; j<textRegions.size(); ++j) {
				TrpTextRegionType r = textRegions.get(j);
								
				List<TextLineType> lines = r.getTextLine();
				
				for (int k=0; k<lines.size(); ++k) {
					TrpTextLineType trpL = (TrpTextLineType) lines.get(k);
					List<WordType> words = trpL.getWord();
					
					getTagsForShapeElement(trpL);
					
					if (wordBased){
						for (int l=0; l<words.size(); ++l) {
							TrpWordType w = (TrpWordType) words.get(l);
							getTagsForShapeElement(w);
						}
					}
//					else{
//						getTagsForShapeElement(trpL);
//					}

				}

			}
			
			if (monitor != null){
				monitor.setTaskName("Loaded tags for page " + (i+1));
				monitor.worked(++c);
			}

		}

	}
	
	/**
	 * 
	 * @param currTagname
	 * @return get all tags with the given tag name
	 */
	public static LinkedHashMap<CustomTag, String> getTags(String currTagname) {
		
		LinkedHashMap<CustomTag, String> resultTags = new LinkedHashMap<CustomTag, String>();
		for (Map.Entry<CustomTag, String> currEntry : tags.entrySet()){
			//logger.debug("current tag name "+currEntry.getKey().getTagName());
			if(currEntry.getKey().getTagName().equals(currTagname)){
				//logger.debug("current tag name found "+currEntry.getKey().getTagName());
				resultTags.put(currEntry.getKey(), currEntry.getValue());
			}
		}
		return resultTags;
	}

	private static void getTagsForShapeElement(ITrpShapeType element) throws IOException{

		String textStr = element.getUnicodeText();
		CustomTagList cl = element.getCustomTagList();
		if (textStr == null || cl == null)
			throw new IOException("Element has no text or custom tag list: "+element+", class: "+element.getClass().getName());
			
		for (CustomTag nonIndexedTag : cl.getNonIndexedTags()) {
			//logger.debug("tagname" + nonIndexedTag.getTagName());
			//logger.debug("nonindexed tag found ");
			if(!nonIndexedTag.getTagName().equals("readingOrder")){
				storeCustomTag(nonIndexedTag, textStr);
			}

		}
		
		/*
		 * blacken String if necessary
		 */
		if(doBlackening){
			for (CustomTag indexedTag : cl.getIndexedTags()) {
				if (indexedTag instanceof BlackeningTag){
					//logger.debug("blackening found " + textStr);
					textStr = blackenString(indexedTag, textStr);
				}
			}
		}
		
		for (CustomTag indexedTag : cl.getIndexedTags()) {
			//logger.debug("tagname " + indexedTag.getTagName());
			storeCustomTag(indexedTag, textStr);

		}

	}
	
	public static LinkedHashMap<CustomTag, String> getAllTagsForShapeElement(ITrpShapeType element) throws IOException{

		LinkedHashMap<CustomTag, String> elementTags = new LinkedHashMap<CustomTag, String>();
		String textStr = element.getUnicodeText();
		CustomTagList cl = element.getCustomTagList();
//		if (textStr == null || cl == null)
//			throw new IOException("Element has no text or custom tag list: "+element+", class: "+element.getClass().getName());
			
		for (CustomTag nonIndexedTag : cl.getNonIndexedTags()) {
			
			if (!nonIndexedTag.getTagName().equals(TextStyleTag.TAG_NAME) && !nonIndexedTag.getTagName().equals(BlackeningTag.TAG_NAME)){
				//logger.debug("nonindexed tag found ");
				elementTags.put(nonIndexedTag, textStr);
			}

		}
		for (CustomTag indexedTag : cl.getIndexedTags()) {
			
			if (!indexedTag.getTagName().equals(TextStyleTag.TAG_NAME) && !indexedTag.getTagName().equals(BlackeningTag.TAG_NAME) && !indexedTag.getTagName().equals(ReadingOrderTag.TAG_NAME)){
				//logger.debug("indexed tag found ");
				elementTags.put(indexedTag, textStr);
			}

		}
		return elementTags;

	}
	
	public static LinkedHashMap<CustomTag, String> getAllTagsOfThisTypeForShapeElement(ITrpShapeType element, String type) throws IOException{

		LinkedHashMap<CustomTag, String> elementTags = new LinkedHashMap<CustomTag, String>();
		String textStr = element.getUnicodeText();
		CustomTagList cl = element.getCustomTagList();
//		if (textStr == null || cl == null)
//			throw new IOException("Element has no text or custom tag list: "+element+", class: "+element.getClass().getName());
			
		for (CustomTag nonIndexedTag : cl.getNonIndexedTags()) {
			
			if (nonIndexedTag.getTagName().equals(type)){
				//logger.debug("nonindexed tag found ");
				elementTags.put(nonIndexedTag, textStr);
			}

		}
		for (CustomTag indexedTag : cl.getIndexedTags()) {
			
			if (indexedTag.getTagName().equals(type)){
				//logger.debug("indexed tag found ");
				elementTags.put(indexedTag, textStr);
			}

		}
		return elementTags;

	}
	
	private static void storeCustomTag(CustomTag currTag, String textStr) {
		if (!currTag.getTagName().equals(TextStyleTag.TAG_NAME)){
			
			if (currTag.getOffset() != -1 && currTag.getLength() != -1 && (currTag.getOffset()+currTag.getLength() <= textStr.length())){
				//guarantee that string is not blackened
				if(!textStr.substring(currTag.getOffset(), currTag.getOffset()+currTag.getLength()).matches("[*]+")){
					tags.put(currTag, textStr.substring(currTag.getOffset(), currTag.getOffset()+currTag.getLength()));
				}
			}
			else{
				if(!textStr.matches("[*]+")){
					tags.put(currTag, textStr);
				}
			}
//			logger.debug("++tag name is " + currTag.getTagName());
//			logger.debug("text " + tags.get(currTag));
		}
		
		if (currTag.getTagName().equals("Person")){
			if (currTag.getOffset() != -1 && currTag.getLength() != -1 && (currTag.getOffset()+currTag.getLength() <= textStr.length())){
				persons.add(textStr.substring(currTag.getOffset(), currTag.getOffset()+currTag.getLength()));
			}
			else{
				logger.debug("with index is something wrong: offset " + currTag.getOffset() + " length " + currTag.getLength()) ;
				//throw new Exception("Something wrong with indexed tag for text: " + textStr);
			}
		}
		else if (currTag.getTagName().equals("Place")){
			if (currTag.getOffset() != -1 && currTag.getLength() != -1 && (currTag.getOffset()+currTag.getLength() <= textStr.length())){
				places.add(textStr.substring(currTag.getOffset(), currTag.getOffset()+currTag.getLength()));
			}
		}
		
	}

	public static void setTags(LinkedHashMap<CustomTag, String> tags) {
		ExportUtils.tags = tags;
	}

	public static Set<String> getOnlyWantedTagnames(Set<String> regTagNames) {
		Set<String> tagnames = new HashSet<String>();
		for (String currTagname : regTagNames){
			if (!currTagname.equals(ReadingOrderTag.TAG_NAME) && !currTagname.equals(StructureTag.TAG_NAME) 
					&& !currTagname.equals(TextStyleTag.TAG_NAME) && !currTagname.equals(BlackeningTag.TAG_NAME)){
				tagnames.add(currTagname);
			}
		}
		return tagnames;
		
	}
	
	/*
	 * return the selected tagnames or, if nothing selected, all registered tagnames (except text style, reading order etc.)
	 */
	public static Set<String> getOnlySelectedTagnames(Set<String> registeredTagNames) {
		// TODO Auto-generated method stub
		Set<String> usefulTagsnames = getOnlyWantedTagnames(registeredTagNames);
		if (selectedTags != null && selectedTags.size() > 0){
			return selectedTags;
		}
		return usefulTagsnames;
	}
	
	public static String blackenString(CustomTag blackeningTag, String lineText) {
		int beginIndex = blackeningTag.getOffset();
		int endIndex = beginIndex + blackeningTag.getLength();
		
//		logger.debug("lineText before : " + lineText);
//		logger.debug("lineText length : " + lineText.length());
//		logger.debug("begin : " + beginIndex);
//		logger.debug("end : " + endIndex);
		
		String beginString = "";
		if (beginIndex > 0)
			beginString = lineText.substring(0, beginIndex);
		String tagString = lineText.substring(beginIndex, endIndex);
		tagString = tagString.replaceAll(".", "*");
		String postString = lineText.substring(endIndex);
		
		return beginString.concat(tagString).concat(postString);
		
	}

	public static LinkedHashMap<CustomTag, String> getCustomTagMapForDoc() {
		// TODO Auto-generated method stub
		return tags;
	}

	public static List<JAXBPageTranscript> getPageTranscripts() {
		return pageTranscripts;
	}
	
	public static JAXBPageTranscript getPageTranscriptAtIndex(int index) {
//		return pageTranscripts.get(index);
		if (pageTranscripts != null && pageTranscripts.size() > index)
			return pageTranscripts.get(index);
		else	
			return null;
	}
	
	public static String getAdjustedDocTitle(String title) {
		return CoreUtils.replaceInvalidPathChars(title, "_");
	}

}
