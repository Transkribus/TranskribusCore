package eu.transkribus.core.util;

public class StrUtil {
	
	public final static String EMPTY_STR = "";
	
	/**
	 * Returns an empty string if the input is null, the original string otherwise.
	 */
	public static String get(String s) {
		return s==null ? EMPTY_STR : s;
	}
	
	

}
