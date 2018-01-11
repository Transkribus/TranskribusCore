package eu.transkribus.core.util;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnicodeList implements Comparable<UnicodeList> {
	private final static Logger logger = LoggerFactory.getLogger(UnicodeList.class);
	
	public final static int minStringSize = 4;
	public final static int maxStringSize = 5;
	
	private static int currentUnicodeStart = 0;
	private static int currentUnicodeEnd = 0;
	
	//Astronomie (U+263D-2653) und Alchemie (U+1F700-1F77F)
	public final static String FOUR_DIGIT_HEX_VALUE_REGEX = "[0-9A-Fa-f]{4,5}";
	public final static String UNICODE_VALUE_REGEX = "U\\+"+FOUR_DIGIT_HEX_VALUE_REGEX;
	public final static String UNICODE_VALUE_OPTIONAL_START_REGEX = "(U\\+)?"+FOUR_DIGIT_HEX_VALUE_REGEX;
	public final static String UNICODE_RANGE_REGEX=UNICODE_VALUE_OPTIONAL_START_REGEX+"-"+UNICODE_VALUE_OPTIONAL_START_REGEX;
	
	String name;
	/*
	 * unicode represented as Integer and as String (not as char because Supplementary Character (5 digits long) 
	 * needs two chars
	 */
	List<Pair<Integer, String>> unicodes;
	
	public UnicodeList(String name, String unicodeString) {
		this.name = name;
		this.unicodes = parseUnicodeChars(unicodeString);
	}
	
	public UnicodeList(String name, List<Pair<Integer,String>> unicodes) {
		this.name = name;
		this.unicodes = unicodes;
	}

	public UnicodeList(String name, char unicodeStart, char unicodeEnd) {
		this.name = name;
		
        this.unicodes = new ArrayList<>();
        for (Character c=unicodeStart; c<=unicodeEnd; ++c)
        	unicodes.add(Pair.of((int) c, Character.toString(c)));
	}
	
	public void initChars(String unicodeString) {
		this.unicodes = parseUnicodeChars(unicodeString);
	}

	public String getName() {
		return name;
	}

	public List<Pair<Integer,String>> getUnicodes() {
		return unicodes;
	}
	
	public void setUnicodes(List<Pair<Integer,String>> unicodes) {
		this.unicodes = unicodes;
	}
	
//	public boolean addChar(Character c) {
//		if (!chars.contains(c)) {
//			chars.add(c);
//			return true;
//		}
//		return false;
//	}
	
//	public boolean isPrintableChar( char c ) {
//	    Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
//	    return (!Character.isISOControl(c)) &&
//	            c != KeyEvent.CHAR_UNDEFINED &&
//	            block != null &&
//	            block != Character.UnicodeBlock.SPECIALS;
//	}
	
	public static Character toCharacter(String unicodeString) {
		try {
			return unicodeString.toCharArray()[0];
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String toUnicodeString(int c) {
//		logger.debug("int c " + c);
//		logger.debug("Long.toHexString(c) " + Long.toHexString(c));
//		logger.debug("StringUtils.leftPad(Long.toString(c,16), currLength, 1) " + StringUtils.leftPad(Long.toHexString(c), 4, "0"));
		//return "U+"+StringUtils.leftPad(Integer.toHexString(c), currLength, "0");
		return "U+"+StringUtils.leftPad(Integer.toHexString(c), 4, "0");
	}
	
	private static void appendHexRange(StringBuilder sb, int begin, int end) {
		// new: deprecated
//		sb.append(" ");
//		if (begin == end) { // if only one char, try to add it as 
//			sb.append(toUnicodeString(begin));	
//		}
//		else {
//			sb.append(toUnicodeString(begin)).append("-").append(toUnicodeString(end));
//		}
		
		// old:
	    sb.append(" ").append(toUnicodeString(begin));
	    if (end != begin)
	        sb.append("-").append(toUnicodeString(end));
	}
	
	public String getUnicodeHexRange() {
		List<Pair<Integer,String>> strings = new ArrayList<>(this.unicodes);
				
		Collections.sort(strings);
		
	    StringBuilder sb = new StringBuilder();
	    if (strings.size() == 0) return sb.toString();
	    int begin = strings.get(0).getLeft(), end = strings.get(strings.size()-1).getLeft();
	    ListIterator li = strings.listIterator();
	    while (li.hasNext()){
	    	Pair<Integer,String> pair = (Pair<Integer, String>) li.next();
	    	int cur = pair.getLeft();	       
	    	if (cur - end <= 1)
	            end = cur;
	        else {
	            appendHexRange(sb, begin, end);
	            begin = end = cur;
	        }
	    }
	    appendHexRange(sb, begin, end);
	    return sb.substring(1);
	}

	public static List<Pair<Integer, String>> parseUnicodeChars(String value) {
		List<Pair<Integer,String>> unicodes = new ArrayList<>();
		for (String split : value.split("\\s+")) {
			if (split.matches(UNICODE_RANGE_REGEX)) {
				logger.trace("unicode range matched");
				Pair<String, String> range;
				try {
					range = parseRange(split);				
					for (int c = currentUnicodeStart; c <= currentUnicodeEnd; ++c) {
						char[] codeUnits = Character.toChars(c);
						String result = new String(codeUnits, 0, codeUnits.length);
						logger.trace("adding string: "+result);
						if (!unicodes.contains(result))
							unicodes.add(Pair.of(c,result));
					}					
				} catch (IOException e) {
					logger.error("Could not parse a unicode range: "+e.getMessage()+" - skipping values!");
				}
			}
			else if (split.matches(UNICODE_VALUE_REGEX)) {
				logger.trace("single unicode value matched");
				try {
					Pair<Integer, String> c = parseUnicodeString(split);

					logger.trace("adding char: "+c);
					logger.trace("size before: "+unicodes.size());
					
					if (!unicodes.contains(c)){
						unicodes.add(c);
					}

					logger.trace("size after: "+unicodes.size());
					 
				} catch (IOException e) {
					logger.error("Could not parse a unicode value: "+e.getMessage()+" - skipping value!");
				}
			}
			else {
				logger.trace("single list of characters matched, value length = "+value.length());
				for (int j = 0; j < split.length(); ++j) {
					char c = split.charAt(j);
					if (!Character.isWhitespace(c))
						if (!unicodes.contains(c))
							unicodes.add(Pair.of((int) c, Character.toString(c)));
				}
			}
		}
		logger.debug("returning chars: "+unicodes.size()+" as str: "+StringUtils.join(unicodes, ","));
		return unicodes;
	}
	
	private static Pair<Integer,String> parseUnicodeString(String str) throws IOException {
		if (str == null)
			throw new IOException("Given string is null");
		
		str = StringUtils.removeStart(str, "U+");
		if (str.length() < minStringSize || str.length() > maxStringSize)
			throw new IOException("Given string must have size from: " + minStringSize + " to " + maxStringSize + " "+str);
		
		try {
			//this way we got from an Unicode String its String representation
			int tmp = Integer.parseInt(str, 16);
			char[] codeUnits = Character.toChars(tmp);
			String result = new String(codeUnits, 0, codeUnits.length);
			//logger.debug("char as string " + result);
			return Pair.of(tmp, result);
		} catch (NumberFormatException e) {
			throw new IOException("Error parsing unicode value: "+e.getMessage()+" str = "+str, e);
		}
	}
	
	private static Pair<String, String> parseRange(String value) throws IOException {
		if (!value.contains("-"))
			throw new IOException("Range does not contain a dash: "+value);
		
		String[] splits = value.split("-");
		if (splits.length!=2)
			throw new IOException("Range does not have two values: "+value);
		
		Pair<Integer, String> start = parseUnicodeString(splits[0]);
		Pair<Integer, String> end = parseUnicodeString(splits[1]);
		
		currentUnicodeStart = start.getLeft();
		currentUnicodeEnd = end.getLeft();
		
		return Pair.of(start.getRight(), end.getRight());
	}

	@Override public int compareTo(UnicodeList o) {
		return name.compareTo(o.name);
	}

	public List<String> getUnicodesAsStrings() {
		// TODO Auto-generated method stub
		List<String> strings = new ArrayList<String>();
		List<Pair<Integer, String>> list = getUnicodes();
		for (Pair<Integer, String> pair : list){
			strings.add(pair.getRight());
		}
		return strings;
	}	
	

}
