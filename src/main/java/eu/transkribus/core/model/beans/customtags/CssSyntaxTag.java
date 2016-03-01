package eu.transkribus.core.model.beans.customtags;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A class to parse attribute strings in CSS syntax as e.g. p {color:red;text-align:center;} */
public class CssSyntaxTag {
	private final static Logger logger = LoggerFactory.getLogger(CssSyntaxTag.class);
	
	static String charsToEscape = "\\\"'{}:; \t";
	
	/**
	 * Escapes a string containing the special chars specified in charsToEscape
	 */
	public static String escapeCss(String str) {
		String strEscaped = "";
		for (Character c : str.toCharArray()) {
			if (charsToEscape.contains(""+c)) {
				strEscaped += "\\u" + Integer.toHexString(c | 0x10000).substring(1);
			} else {
				strEscaped += c;
			}
			
		}
		return strEscaped;
		
//		return StringEscapeUtils.escapeXml(str);
	}
	
	public static String unescapeCss(String str) {
		// FIXME is this really sufficient??
		return StringEscapeUtils.unescapeJava(str);
		
//		return StringEscapeUtils.unescapeXml(str);
	}
	
	public final static String CSS_ATTRIBUTE_NAME_STYLE = "[a-zA-Z0-9_\\-]+";
	
//	public final static String CSS_ATTRIBUTE_VALUE_STYLE = "([a-zA-Z0-9_\\-.\\\\]+)"; // BUG: what about characters from different alphabets????
//	public final static String CSS_ATTRIBUTE_VALUE_STYLE = "([\\p{L}0-9\\^\\[\\]_\\-.\\\\ ]+)"; // one more try...
	public final static String CSS_ATTRIBUTE_VALUE_STYLE = "([^{}:;]+)"; // should work now...
//	public final static String CSS_ATTRIBUTE_VALUE_STYLE = "\\s*([a-zA-Z0-9_\\-]+)\\s*:\\s*([a-zA-Z0-9_\\-.]+)\\s*;\\s*"; // orig
	public final static String CSS_ATTRIBUTE_STYLE = "\\s*("+CSS_ATTRIBUTE_NAME_STYLE+")\\s*:\\s*"+CSS_ATTRIBUTE_VALUE_STYLE+"\\s*;?\\s*";
//	public final static String CSS_ATTRIBUTE_STYLE = "\\s*([a-zA-Z0-9_\\-]+)\\s*:\\s*([a-zA-Z0-9_\\-.]+)\\s*;\\s*";
	public final static String CSS_STYLE = "\\s*("+CSS_ATTRIBUTE_NAME_STYLE+")\\s*\\{((\\s*"+CSS_ATTRIBUTE_STYLE+"\\s*)*\\s*)\\}\\s*";
		
	public final static Pattern CSS_ATTRIBUTE_PATTERN = Pattern.compile(CSS_ATTRIBUTE_STYLE, Pattern.UNICODE_CHARACTER_CLASS);
	public final static Pattern CSS_PATTERN = Pattern.compile(CSS_STYLE);
	
//	public final static String VALID_TAG_NAMES = "[_a-zA-Z][_a-zA-Z0-9-]*";
//	public final static Pattern VALID_TAG_NAMES_PATTERN = Pattern.compile(VALID_TAG_NAMES);
	
	String tagName;
	HashMap<String, Object> attributes;
//	HashMap<String, String> attributes;
	
	public CssSyntaxTag() {
		tagName = "";
		attributes = new HashMap<String, Object>();
	}
		
	public CssSyntaxTag(String tagName, Map<String, Object> attributes) {
		this.tagName = tagName;
		this.attributes.putAll(attributes);		
	}
		
	public CssSyntaxTag(String cssStr) throws ParseException {
		Pair<String, String> nameAndAttributes = parseCssTagNameAndAttributes(cssStr);
		tagName = nameAndAttributes.getLeft();
		attributes = parseCssAttributes(nameAndAttributes.getRight());
	}
	
	public HashMap<String, Object> getAttributes() {
		return attributes;
	}

	public String getCssString() {
		String css = tagName + " {";
		for (String key : attributes.keySet()) {
			css += key+":"+escapeCss((String)attributes.get(key))+";";
		}
		css += "}";
		return css;
	}
	
	protected Object addAttribute(String attribute, Object initialValue) {
		return attributes.put(attribute, initialValue);
	}
	
	public Set<String> getAttributeNames() {
		return attributes.keySet();
	}
	
	public Object getAttributeValue(String attribute) {
		return attributes.get(attribute);
	}
	
	public Integer getAttributeValueAsInt(String attribute) {
		return Integer.parseInt(attributes.get(attribute).toString());
	}
	
	public Object setAttributeValue(String attribute, Object value) {
		if (attributes.containsKey(attribute)) {
			return attributes.put(attribute, value);
		}
		return null;
	}
	
	public Object setAttributeValue(String attribute, Integer value) {
		return setAttributeValue(attribute, ""+value);
	}
	
	public boolean hasAttribute(String attribute) {
		return attributes.containsKey(attribute);
	}
	
//	public String putAttribute(String key, String value) {
//		return attributes.put(key, value);
//	}
//	
//	public void putAttributes(Map<String, String> atts) {
//		attributes.putAll(atts);
//	}
	
//	public HashMap<String, String> getAttributes() {
//		return attributes;
//	}
	
	public String getTagName() { return tagName; }
	
	public void setTagName(String tagName) { this.tagName = tagName; }
	
	public static String getCssString(Collection<CssSyntaxTag> tags) {
		String css = "";
		for (CssSyntaxTag t : tags) {
			css += " "+t.getCssString();
		}
		css = css.trim();
		
		return css;
	}
	
	public static List<CssSyntaxTag> parseTags(String cssStr) {
		List<CssSyntaxTag> tags = new ArrayList<>();
		if (cssStr != null) {
			Matcher matcher = CSS_PATTERN.matcher(cssStr);
			
			while (matcher.find()) {
				String cssTag = matcher.group();
				logger.trace("found css style tag: "+cssTag);
				try {
					tags.add(new CssSyntaxTag(cssTag));
				}
				catch (ParseException e) {
					logger.error("Could not parse css tag inside parsing a css string - should not happen! Message: "+e.getMessage(), e);
				}
			}
		}
		
		return tags;
	}
	
	public static CssSyntaxTag parseSingleCssTag(String cssStr) throws ParseException {
		return new CssSyntaxTag(cssStr);
	}
	
	public static Pair<String, String> parseCssTagNameAndAttributes(String css) throws ParseException {
		Matcher matcher = CSS_PATTERN.matcher(css);
		boolean matches = matcher.matches();
		
		if (matches) {
			return Pair.of(matcher.group(1), matcher.group(2));
		} else {
			throw new ParseException("Could not parse css style string: "+css, 0);
		}
	}
	
	public static HashMap<String, Object> parseCssAttributes(String attributeString) throws ParseException {
//		logger.debug("attribute string: "+attributeString);
		Matcher matcher = CSS_ATTRIBUTE_PATTERN.matcher(attributeString);
		
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		while (matcher.find()) {
			String attrStr = matcher.group();
			attrStr = attrStr.replaceAll(";", "");
			String[] splits = attrStr.split(":");
			
			logger.trace("splits = ");
			for (String s : splits) {
				logger.trace(s);
			}
			
			if (splits.length != 2)
				throw new ParseException("Could not parse css style attribute string: "+attributeString+"\n An error occured during splitting an attribute, which should not happen!!", 0);
			
//			attrs.put(splits[0].trim(), splits[1].trim());
			attrs.put(splits[0].trim(), unescapeCss(splits[1].trim())); // FIXME??
//			logger.debug("css attrbibute: "+attrs.get(attrs.size()-1));
		}
		
		return attrs;
	}
	
	public boolean isSameTag(CssSyntaxTag tag) {
		return getTagName().equals(tag.getTagName());
	}
	
	public String toString() {
	    final String TAB = ", ";
	    String retValue = "CssSyntaxTag ( "+super.toString();
		retValue += TAB + "tagName = " + this.tagName;
		retValue += TAB + "attributes = " + this.attributes;
		retValue += " )";
	    return retValue;
	}	
	

}
