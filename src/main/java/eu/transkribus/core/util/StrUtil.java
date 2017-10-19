package eu.transkribus.core.util;

public class StrUtil {
	
	public final static String EMPTY_STR = "";
	
	/**
	 * Returns an empty string if the input is null, the original string otherwise.
	 */
	public static String get(String s) {
		return s==null ? EMPTY_STR : s;
	}
	
	
	/**
	 * Returns true if
	 * <ul>
	 * <li>both Strings are either null or empty but not necessarily the same. E.g. s1 == null and s2 == "" </li>
	 * <li>String content is equal</li>
	 * </ul>
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static boolean equalsContent(final String s1, final String s2) {
		final String tmp1 = get(s1);
		final String tmp2 = get(s2);
		return tmp1.equals(tmp2);
	}
}
