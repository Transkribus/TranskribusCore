package eu.transkribus.core.model.beans.customtags;

import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CssSyntaxTagUtil {
	private static final Logger logger = LoggerFactory.getLogger(CssSyntaxTagUtil.class);
	
	public static void correctTagOffsetsForEscapedString(int indexOfCharacter, String escapedReplacement, List<CssSyntaxTag> tags) {
		if (escapedReplacement.length() > 1) {
			int additonalLength = escapedReplacement.length()-1;
			
			for (CssSyntaxTag t : tags) {
				// character before tag -> move offset to the right
				
				Integer offset = t.getIntegerAttribute("offset");
				Integer length = t.getIntegerAttribute("length");
				
//				logger.info("offset = "+offset+" length = "+length);
				
				if (offset == null && length == null) {
					continue;
				}
				
				if (offset!=null && indexOfCharacter < offset) {
					t.setAttributeValue("offset", offset + additonalLength);
//					t.setOffset(t.getOffset() + additonalLength);
				}
				// inserted text inside tag -> extend length!
				else if (offset!=null && length!=null && indexOfCharacter >= offset && indexOfCharacter < (offset+length)) {
					t.setAttributeValue("length", length + additonalLength);
//					t.setLength(t.getLength() + additonalLength);
				}
				// else: do not bother!!
			}
		}
	}
	
	/**
	 * Escapes the given shape text for XML and adjusts offset/length values of given tags accordingly
	 */
	public static String escapeShapeText(String text, List<CssSyntaxTag> tags) {
		String escapedText="";
		for (int i=0; i<text.length(); ++i) {
			char c = text.charAt(i);
			String escaped = StringEscapeUtils.escapeXml(""+c);
			int indexOfChar = escapedText.length();
			escapedText += escaped;
			
			correctTagOffsetsForEscapedString(indexOfChar, escaped, tags);
		}
		return escapedText;
	}
	
	public static String createTagStart(CssSyntaxTag t, boolean closeAlready) {
		String ts = "<"+t.getTagName();
		
		for (String an : t.getAttributeNames()) {
			if (CustomTag.isOffsetOrLengthOrContinuedProperty(an))
				continue;
			
			Object v = t.getAttributeValue(an);
			if (v != null) {
				ts+=" "+StringEscapeUtils.escapeXml(an)+"='"+StringEscapeUtils.escapeXml(v.toString())+"'";
			}
		}
		if (closeAlready) {
			ts+="/>";
		}
		else {
			ts+=">";
		}
		
		return ts;
	}
	
	public static String createTagEnd(CssSyntaxTag t) {
		String te = "</"+t.getTagName()+">";
		return te;
	}
	
	public static void correctTagOffsetsForTagInsertion(int insertOffset, int insertLength, List<CssSyntaxTag> tags) {
		for (CssSyntaxTag t : tags) {
			
//			HashMap<String, Object> attributes = t.getAttributes();
			
			Integer offset = t.getIntegerAttribute("offset");
			Integer length = t.getIntegerAttribute("length");			
			
//			System.out.println("offset = "+offset+" length = "+length);
			
			if ((offset == null && length == null)) {
				continue;
			}
			
			// inserted text before tag -> extend offset!
			if (offset != null && insertOffset <= offset) {
				t.setAttributeValue("offset", offset + insertLength);
//				t.setOffset(t.getOffset() + insertLength);
			}
			// inserted text inside tag -> extend length!
			else if (offset!=null && length!=null && insertOffset > offset && insertOffset < (offset+length)) {
				t.setAttributeValue("length", length + insertLength);
//				t.setLength(t.getLength() + insertLength);
			}
			// else: do not bother!!
		}
	}
	
	public static String insertTag(String text, CssSyntaxTag t, List<CssSyntaxTag> tags) {
		logger.trace("custom tag: "+t);
		logger.trace("text: "+text);
		
		Integer offset = t.getIntegerAttribute("offset");
		Integer length = t.getIntegerAttribute("length");
		
		System.out.println("inserting tag, offset = "+offset+" length = "+length);
		
		if (offset==null) {
			return text;
		}
		
		int end = offset;
		if (length != null) {
			end += length;
		}
		
		if ((offset==null && length==null) || offset < 0 || end > text.length()) {
			logger.warn("Could not insert tag: "+t+" - index out of bounds! Skipping...");
			return text;
		}
		
		boolean closeAlready = (offset == end);
		
		// insert tag start:
		String ts = createTagStart(t, closeAlready);
		
		StringBuilder sb = new StringBuilder(text);
		sb.insert(offset, ts).toString();
		
		correctTagOffsetsForTagInsertion(offset, ts.length(), tags);
		
		// update offset, length, end values
		offset = t.getIntegerAttribute("offset");
		length = t.getIntegerAttribute("length");
		end = offset;
		if (length != null) {
			end += length;
		}

		// insert tag end:
		if (!closeAlready) {
			String te = createTagEnd(t);
			sb.insert(end, te).toString();
			
			correctTagOffsetsForTagInsertion(end, te.length(), tags);
		}
		
		return sb.toString();
	}
	
	public static String getTaggedContent(String text, List<CssSyntaxTag> tags) {
		// escape the shape text here - later on the tag elements would be escaped too
		String escapedText = escapeShapeText(text, tags);
		logger.trace("ShapeText = "+text+" escaped: "+escapedText);
		
		for (CssSyntaxTag t : tags) {
//			if ( commonPars.isTagSelected(t.getTagName()) 
//				|| (commonPars.isDoBlackening() && t.getTagName().equals(BlackeningTag.TAG_NAME)) || t.getTagName().equals(TextStyleTag.TAG_NAME) ) {
				escapedText = insertTag(escapedText, t, tags);
//			}
		}
		logger.trace("escaped text after tag insertion: "+escapedText);
		
		// replace blackened text:
//		if (commonPars.isDoBlackening()) {
//			escapedText = hideBlackenedText(escapedText);
//		}
		
		return escapedText;
	}
	
}
