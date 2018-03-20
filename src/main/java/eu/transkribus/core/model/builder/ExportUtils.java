package eu.transkribus.core.model.builder;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import eu.transkribus.core.model.beans.customtags.BlackeningTag;
import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.ReadingOrderTag;
import eu.transkribus.core.model.beans.customtags.StructureTag;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.util.CoreUtils;

public class ExportUtils {
	
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

	public static String getAdjustedDocTitle(String title) {
		return CoreUtils.replaceInvalidPathChars(title, "_");
	}
}
