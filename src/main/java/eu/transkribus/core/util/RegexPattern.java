package eu.transkribus.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegexPattern {
	private final static Logger logger = LoggerFactory.getLogger(RegexPattern.class);
	
	public static final RegexPattern TAG_DEFINITIONS_PATTERN = new RegexPattern("([\\w_-]+)\\s*(\\{([\\#\\w-,\\s]*)\\})?",
			"Specify a tagname, then optionally a list of attributes contained in curly braces");
	
	public static final RegexPattern TAG_NAME_PATTERN = new RegexPattern("[_a-zA-Z][_a-zA-Z0-9-]*",
			"First letter must be underscore or letter, afterwards letters, underscores or hyphens are allowed");
	
	public static final RegexPattern RANGE_PATTERN = new RegexPattern("([0-9]+)[\\-([0-9]+)]?", 
			"Either a single number or a range of numbers divided by a dash");
	
	private String regex;
	private Pattern pattern;
	private String description;
	
	public RegexPattern(String regex, String description) {
		this.regex = regex;
		pattern = Pattern.compile(this.regex);
		this.description = description;
	}
	
	public boolean matches(CharSequence input) {
		if (input == null)
			return false;
		
		return pattern.matcher(input).matches();
	}
	
	public Matcher matcher(CharSequence input) {
		return pattern.matcher(input);
	}

	public String getRegex() {
		return regex;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public String getDescription() {
		return description;
	}
	
	
	// TESTING:
	static void testTagDefPattern() {
		logger.info(""+TAG_DEFINITIONS_PATTERN.matches("tagname{asdf, asdf}"));
		logger.info(""+TAG_DEFINITIONS_PATTERN.matches(""));
		
		Matcher m = TAG_DEFINITIONS_PATTERN.matcher("tagname ");
		logger.info("matches = "+m.matches());
		
		logger.info("tagname = "+m.group(1));
		for (String s : m.group(3).split(",")) {
			logger.info("attribute = "+s.trim());	
		}
	}
	
//	static void testRangePattern() {
//		String valid = "1-9";
//		String invalid = "1-";
//		
//		String[] strs = new String[] { valid, invalid, "100" };
//		
//		for (String s : strs) {
//		
//			Matcher m = RANGE_PATTERN.matcher(s);
//			if (m.matches()) {
//				logger.info(s+" does match!");
//				
//				
//			} else
//				logger.info(s+" does not match!");
//		
//		}
//		
//	}
	
//	public static void main(String[] args) {
//		testRangePattern();
//	}
	
	

}
