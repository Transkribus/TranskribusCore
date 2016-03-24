package eu.transkribus.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnicodeList implements Comparable<UnicodeList> {
	private final static Logger logger = LoggerFactory.getLogger(UnicodeList.class);
	
	public final static String FOUR_DIGIT_HEX_VALUE_REGEX = "[0-9A-Fa-f]{4}";
	public final static String UNICODE_VALUE_REGEX = "U\\+"+FOUR_DIGIT_HEX_VALUE_REGEX;
	public final static String UNICODE_VALUE_OPTIONAL_START_REGEX = "(U\\+)?"+FOUR_DIGIT_HEX_VALUE_REGEX;
	public final static String UNICODE_RANGE_REGEX=UNICODE_VALUE_OPTIONAL_START_REGEX+"-"+UNICODE_VALUE_OPTIONAL_START_REGEX;
	
	String name;
	List<Character> chars;
	
	public UnicodeList(String name, String unicodeString) {
		this.name = name;
		this.chars = parseUnicodeChars(unicodeString);
	}
	
	public UnicodeList(String name, List<Character> chars) {
		this.name = name;
		this.chars = chars;
	}

	public UnicodeList(String name, char unicodeStart, char unicodeEnd) {
		this.name = name;
		
        this.chars = new ArrayList<>();
        for (Character c=unicodeStart; c<=unicodeEnd; ++c)
        	chars.add(c);
	}
	
	public void initChars(String unicodeString) {
		this.chars = parseUnicodeChars(unicodeString);
	}

	public String getName() {
		return name;
	}

	public List<Character> getChars() {
		return chars;
	}
	
//	public boolean addChar(Character c) {
//		if (!chars.contains(c)) {
//			chars.add(c);
//			return true;
//		}
//		return false;
//	}
	
	public static String toUnicodeString(int c) {
		return "U+"+StringUtils.leftPad(Integer.toHexString(c), 4, "0");
	}
	
	private static void appendHexRange(StringBuilder sb, int begin, int end) {
	    sb.append(" ").append(toUnicodeString(begin));
	    if (end != begin)
	        sb.append("-").append(toUnicodeString(end));
	}
	
	public String getUnicodeHexRange() {
		List<Character> chars = new ArrayList<>(this.chars);
		
		Collections.sort(chars);
		
	    StringBuilder sb = new StringBuilder();
	    if (chars.size() == 0) return sb.toString();
	    int begin = chars.get(0), end = chars.get(0);
	    for (int cur : chars)
	        if (cur - end <= 1)
	            end = cur;
	        else {
	            appendHexRange(sb, begin, end);
	            begin = end = cur;
	        }
	    appendHexRange(sb, begin, end);
	    return sb.substring(1);
	}
	
	

	private static List<Character> parseUnicodeChars(String value) {
		List<Character> chars = new ArrayList<>();
		for (String split : value.split(" ")) {
			logger.trace("split = "+split);
			if (split.matches(UNICODE_RANGE_REGEX)) {
				logger.trace("h1");
				Pair<Character, Character> range;
				try {
					range = parseRange(split);
					for (Character c = range.getLeft(); c <= range.getRight(); ++c) {
						logger.trace("adding char: "+c);
						if (!chars.contains(c))
							chars.add(c);
					}					
				} catch (IOException e) {
					logger.error("Could not parse a unicode range: "+e.getMessage()+" - skipping values!");
				}
			}
			else if (split.matches(UNICODE_VALUE_REGEX)) {
				logger.trace("h2");
				try {
					Character c = parseUnicodeString(split);
					logger.trace("adding char: "+c);
					logger.trace("size before: "+chars.size());
					if (!chars.contains(c))
							chars.add(c);
					
					logger.trace("size after: "+chars.size());
				} catch (IOException e) {
					logger.error("Could not parse a unicode value: "+e.getMessage()+" - skipping value!");
				}
			}
			else {
				logger.trace("h3, value length = "+value.length());
				for (int j = 0; j < split.length(); ++j) {
					Character c = new Character(split.charAt(j));
					if (!Character.isWhitespace(c))
						if (!chars.contains(c))
							chars.add(c);
				}
			}
		}
		logger.debug("returning chars: "+chars.size()+" as str: "+StringUtils.join(chars, ","));
		return chars;
	}
	
	private static Character parseUnicodeString(String str) throws IOException {
		if (str == null)
			throw new IOException("Given string is null");
		
		str = StringUtils.removeStart(str, "U+");
		if (str.length() != 4)
			throw new IOException("Given string must have size 4: "+str);
		
		try {
			return (char) Integer.parseInt(str, 16);
		} catch (NumberFormatException e) {
			throw new IOException("Error parsing unicode value: "+e.getMessage()+" str = "+str, e);
		}
	}
	
	private static Pair<Character, Character> parseRange(String value) throws IOException {
		if (!value.contains("-"))
			throw new IOException("Range does not contain a dash: "+value);
		
		String[] splits = value.split("-");
		if (splits.length!=2)
			throw new IOException("Range does not have two values: "+value);
		
		Character start = parseUnicodeString(splits[0]);
		Character end = parseUnicodeString(splits[1]);
		
		return Pair.of(start, end);
	}

	@Override public int compareTo(UnicodeList o) {
		return name.compareTo(o.name);
	}	
	

}
