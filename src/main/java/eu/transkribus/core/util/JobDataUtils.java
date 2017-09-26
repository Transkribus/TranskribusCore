package eu.transkribus.core.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class JobDataUtils {
	private static final String LIST_SEP = ".";
	
	/**
	 * Set a list of strings to the properties.<br/>
	 * This method uses a separate key for each entry in the list and won't break on commas in the value.
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static Properties setStringList(Properties props, final String key, List<String> values) {
		if(props == null) {
			props = new Properties();
		}
		for(int i = 0; i < values.size(); i++) {
			final String value = values.get(i);
			if(value != null && !value.trim().isEmpty()) {
				props.setProperty(buildKey(key, i), value);
			}
		}
		return props;
	}
	/**
	 * get a list of strings from the properties, that have been set with {@link #setStringList(String, List)}. 
	 * In contrast to {@link #getCsvStringListProperty(String, boolean)}
	 * those methods use a separate key for each entry in the list and won't break on commas in the value.
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static List<String> getStringList(Properties props, final String key) {
		List<String> values = new LinkedList<>();
		if(StringUtils.isEmpty(key)) {
			return values;
		}
		int i = 0;
		String val;
		while((val = props.getProperty(buildKey(key, i))) != null) {
			values.add(i++, val);
		}
		return values;
//		props.stringPropertyNames()
//			.stream()
//			.filter(s -> s.startsWith(key) && s.contains(LIST_SEP))
//			.forEach(s -> {
//				final String value = props.getProperty(s);
//				final String iStr = s.substring(s.lastIndexOf(LIST_SEP));
//				try {
//					final int i = Integer.parseInt(iStr);
//					values.add(i, value);
//				} catch (NumberFormatException nfe) {
//					logger.debug(iStr);
//				}
//			});
	}
	
	private static String buildKey(String key, int i) {
		return key + LIST_SEP + i;
	}
}
