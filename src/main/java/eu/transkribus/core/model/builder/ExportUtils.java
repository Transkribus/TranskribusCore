package eu.transkribus.core.model.builder;

import static com.tutego.jrtf.RtfHeader.color;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tutego.jrtf.Rtf;

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
import eu.transkribus.core.model.builder.rtf.TrpRtfBuilder;

public class ExportUtils {
	private final static Logger logger = LoggerFactory.getLogger(ExportUtils.class);
	
	static boolean exportTags = true;
	static List<JAXBPageTranscript> pageTranscripts = null;
	
	static Map<CustomTag, String> tags = new HashMap<CustomTag, String>();
	static Set<String> tagnames = new HashSet<String>();
	
	static List<String> persons = new ArrayList<String>();
	static List<String> places = new ArrayList<String>();
	
	
	public static void storePageTranscripts4Export(TrpDoc doc, Set<Integer> pageIndices, IProgressMonitor monitor) throws IOException{
		
		pageTranscripts = new ArrayList<JAXBPageTranscript>();
		
		List<TrpPage> pages = doc.getPages();
		
		int totalPages = pages.size();	
		int c = 0;

		for (int i=0; i<totalPages; ++i) {
			if (pageIndices!=null && !pageIndices.contains(i)){
				//fill up with null to have the proper index of each page later on
				pageTranscripts.add(null);
				continue;
			}
			
			TrpPage page = pages.get(i);
			TrpTranscriptMetadata md = page.getCurrentTranscript();
			
			JAXBPageTranscript tr = new JAXBPageTranscript(md);
			tr.build();
			pageTranscripts.add(tr);
			
			logger.debug("Loaded Transcript from page " + (i+1));
			
			monitor.setTaskName("Loaded Transcript from page " + (i+1));
			monitor.worked(++c);

		}
	}
	
	/***
	 * 
	 * @param doc
	 * @param wordBased
	 * @param pageIndices
	 * @return all (custom) tags of the given document
	 * @throws JAXBException
	 * @throws IOException
	 */
	public static void storeCustomTagMapForDoc(TrpDoc doc, boolean wordBased, Set<Integer> pageIndices, IProgressMonitor monitor) throws JAXBException, IOException {
		
		tags.clear();
		List<TrpPage> pages = doc.getPages();
		
		int totalPages = pages.size();		
		int c = 0;
		
		for (int i=0; i<totalPages; ++i) {
			if (pageIndices!=null && !pageIndices.contains(i))
				continue;
			
			TrpPage page = pages.get(i);
			TrpTranscriptMetadata md = page.getCurrentTranscript();
			
			JAXBPageTranscript tr;
			if (pageTranscripts == null || pageTranscripts.get(i) == null){
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
			
			monitor.setTaskName("Loaded tags for page " + (i+1));
			monitor.worked(++c);

		}

	}
	
	/**
	 * 
	 * @param currTagname
	 * @return get all tags with the given tag name
	 */
	public static HashMap<CustomTag, String> getTags(String currTagname) {
		
		HashMap<CustomTag, String> resultTags = new HashMap<CustomTag, String>();
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
			
			//logger.debug("nonindexed tag found ");
			storeCustomTag(nonIndexedTag, textStr);

		}
		for (CustomTag indexedTag : cl.getIndexedTags()) {
			
			//logger.debug("indexed tag found ");
			storeCustomTag(indexedTag, textStr);

		}

	}
	
	public static Map<CustomTag, String> getAllTagsForShapeElement(ITrpShapeType element) throws IOException{

		Map<CustomTag, String> elementTags = new HashMap<CustomTag, String>();
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
	
	public static Map<CustomTag, String> getAllTagsOfThisTypeForShapeElement(ITrpShapeType element, String type) throws IOException{

		Map<CustomTag, String> elementTags = new HashMap<CustomTag, String>();
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
				tags.put(currTag, textStr.substring(currTag.getOffset(), currTag.getOffset()+currTag.getLength()));
			}
			else{
				tags.put(currTag, textStr);
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

	public static void setTags(Map<CustomTag, String> tags) {
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

	public static Map<CustomTag, String> getCustomTagMapForDoc() {
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
}
