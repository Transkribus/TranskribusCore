package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreUtils {
	private final static Logger logger = LoggerFactory.getLogger(CoreUtils.class);
	private static final AtomicLong LAST_TIME_MS = new AtomicLong();
	
	public static int SIZE_OF_LOG_FILE_TAIL = 256;
	
	public final static String DATE_FORMAT_STR = "dd_MM_yyyy_HH:mm";
	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STR);
	
	public final static String DATE_FORMAT_STR_USER_FRIENDLY = "dd.MM.YY HH:mm:ss";
	public final static SimpleDateFormat DATE_FORMAT_USER_FRIENDLY = new SimpleDateFormat(DATE_FORMAT_STR_USER_FRIENDLY);
	
	public static void main(String[] args) {
		String str = "   go,_; - ple\"a[]se ? - \" f*** y(our)self - 2,000 or 3.000 f****' times?";

		if (false) {
			String tokenized = tokenizeForCATTI(str);
			String detokenized = detokenizeForCATTI(tokenized);
	
			System.out.println(str);
			System.out.println(tokenized);
			System.out.println(detokenized);
		}
		
		if (true) {
			int N = getNOfRepeatingChars(str, 0, ' ');
			System.out.println("N = "+N);
		}
//		System.out.println(tokenizeForCATTI("go,_; - ple\"a[]se ? - \" fuck yourself - 2,000 or 3.000 fuckin' times?"));
		
		
	}
	
	/**
	 * Counts the number of repeating characters c in a given string str from a starting position startIndex
	 */
	public static int getNOfRepeatingChars(String str, int startIndex, char c) {
		if (str == null)
			return 0;
		
		int count = 0;
		while (startIndex>= 0 && startIndex < str.length() && str.charAt(startIndex) == c) {
			++startIndex;
			++count;
		}
		return count;
	}
	
	public static void scheduleTimerTask(TimerTask task, long delay) {
		new Timer().schedule(task, delay);
	}
	
	public static <T> void addOrAppend(List<T> list, T element, int index) {
		if (index<0 || index>=list.size())
			list.add(element);
		else
			list.add(index, element);
	}
	
	/**
	 * Tokenizes the input string into words and puncuation marks. Used by the CATTI server
	 */
	public static String tokenizeForCATTI(String str) {
//		return str.replaceAll("([,_.;:!?\"'()\\[\\]])", " $1 ") // problem with that: numbers like 2,000 will also be tokenized!
		return str.replaceAll("([,.;]) ", " $1 ").replaceAll("([,.;])_", " $1 _").replaceAll("([_:!?\"'()\\[\\]])", " $1 ")
				.replaceAll(" +", " ")
				.trim()
				;
	}

	/**
	 * Inverts the tokenized string from the method {@link #tokenizeForCATTI(String)}
	 */
	public static String detokenizeForCATTI(String str) {
		return str.replaceAll(" ([,.;]) ", "$1 ").replaceAll(" ([\\_:!?'\"\\)\\]])", "$1").replaceAll("([\\(\\[]) ", "$1")
				.replaceAll(" +", " ")
				.trim()
				;
	}
	
	/**
	 * Generates a comma separated list string out of the given collection, e.g. (2,4,2,1)
	 */
	public static String toListString(Collection<?> list) {
		String str = "(";
		for (Object o : list) {
			str += o+",";
		}
		str = StringUtils.removeEnd(str, ",");
		
		str += ")";
		return str;
	}
	
	public static String removeFileTypeFromUrl(String urlStr) {
		StringBuffer buf = new StringBuffer(urlStr);
		int s = urlStr.indexOf("&fileType=");
		if (s != -1) {
			int e = urlStr.indexOf('&', s+1);
			if (e==-1) {
				e = urlStr.length();
			}
			
			buf.replace(s, e, ""); 
		}
		
		return buf.toString();
	}
	
	public static int indexOf(String txt, String regex, int startOffset, boolean previous, boolean caseSensitive, boolean wholeWord) {
		String searchStr = new String(regex);
		
		if (wholeWord) {
			searchStr = "\\b"+searchStr+"\\b";
		}
		
		logger.trace("searching for text: "+searchStr+" in text: "+txt);
		
		Pattern p = Pattern.compile(searchStr, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(txt);
		
		int lastMatched = -1;
		while (m.find()) {
			if (!previous && startOffset!=-1 && m.start() < startOffset)
				continue;
			else if (previous && startOffset!=-1 && m.end() >= startOffset)
				break;
			
			lastMatched = m.start();
			if (!previous) {
				break;
			}
		}
		
		return lastMatched;
	}
	
	public static int findNextWordBoundaryIndex(String line, int index) {
		int wordBoundaryIndex = index;
		for (int i=index; i<line.length(); ++i) {
			wordBoundaryIndex = i;
			if (Character.isWhitespace(line.charAt(i))) {
				break;
			}
		}
		return wordBoundaryIndex;
	}
	
	public static boolean isValidColorCode(String colorCode) {
		if (colorCode == null)
			return false;
		
		try {
			java.awt.Color.decode(colorCode);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
		
	public static int bound(int v, int min, int max) {
		if (v < min)
			return min;
		else if (v > max)
			return max;
		else
			return v;	
	}
	
	public static String createRegexFromSearchString(String ss, boolean wildcardAlsoWhitespaces) {
		String anyChar = wildcardAlsoWhitespaces ? "." : "\\w"; // \w for only words charactesr or . for ANY character (+ whitespaces!)

		String nss = new String(ss); // needed???
		nss = nss.replace("*", anyChar+"*");
		nss = nss.replace("?", anyChar);
		
		return nss;
	}
		
	public static long uniqueCurrentTimeMS() {
	    long now = System.currentTimeMillis();
	    while(true) {
	        long lastTime = LAST_TIME_MS.get();
	        if (lastTime >= now) {
	            now = lastTime+1;
	        }
	        if (LAST_TIME_MS.compareAndSet(lastTime, now))
	            return now;
	    }
	}
	
	public static File backupFile(File file) throws IOException {
		
		File backup = File.createTempFile(file.getName().length()>=3 ? file.getName() : "___"+file.getName(), "_backup", file.getParentFile());
		FileUtils.copyFile(file, backup);
		
		return backup;
	}
	
	public static boolean equalsEps(float v1, float v2, float eps) {
		return Math.abs(v1-v2) <= eps;
	}
	
	public static boolean equalsEps(double v1, double v2, double eps) {
		double diff = Math.abs(v1-v2);
//		logger.info("diff = "+diff);
		return diff <= eps;
	}
	
	public static boolean equalsObjects(Object o1, Object o2){
	    return o1 == null ? o2 == null : o1.equals(o2);
	}
	
	public static boolean isValueSet(Object v) {
		if (v == null)
			return false;
		
		if (v instanceof String)
			return !((String) v).isEmpty();
		if (v instanceof Boolean)
			return ((Boolean) v);

		return true;
	}
	
	public static boolean isInIndexRange(int i, int offset, int length) {
		return i>=offset && i < (offset+length);
	}
	
//	public static Object val(Object value) {
//		return value;
//	}
	
	public static Object val(Object value) {
		if (value instanceof Boolean)
			return val((Boolean) value);
		if (value instanceof String)
			return val((String) value);
		if (value instanceof Integer)
			return val((Integer) value);
		if (value instanceof Double)
			return val((Double) value);
		if (value instanceof Float)
			return val((Float) value);
				
		return value;
	}
	
	public static boolean val(Boolean value) {
		return value == null ? false : value;		
	}
	
	public static String val(String value) {
		return value == null ? "" : value;		
	}
	
	public static int val(Integer value) {
		return value == null ? 0 : value;		
	}
	
	public static float val(Float value) {
		return value == null ? 0.0f : value;		
	}

	public static double val(Double value) {
		return value == null ? 0.0d : value;		
	}
	
	public static List<String> splitKeepWs(String str) {
		List<String> splits = new ArrayList<>();
		
		int s=-1;
		
		String tmp= new String("");
		boolean whitespace=false;
		for (int i=0; i<str.length(); ++i) {
			char c = str.charAt(i);
			
			if (Character.isWhitespace(c))
					whitespace=true;
			
			if ( (!Character.isWhitespace(c) && whitespace) && !tmp.isEmpty() ) {
				splits.add(tmp);
				tmp = new String();
				whitespace=false;
			}
			tmp = new String(tmp + c);
		}
		if (!tmp.isEmpty())
			splits.add(tmp);
		
		return splits;
	}
	
//	public static void main(String[] args) throws IOException {
//		String str = " hello  , ein Test-STring   fajl ";
//		String str1 = "hello  , ein Test-STring   f";
//		
//		for (String s : splitKeepWs(str)) {
//			System.out.println("'"+s+"'");
//		}
//		
//		for (String s : splitKeepWs(str1)) {
//			System.out.println("'"+s+"'");
//		}
		
//		List<Integer> set = parseRangeListStrToList("8,10-11",  20);
		
//		for(Integer i : set){
//			System.out.println(i);
//		}
//	}

	public static String writePropertiesToString(Properties p) {
		if(p == null){
			return "";
		}
		StringWriter writer = new StringWriter();
		String str = "";
		try {
			p.store(new PrintWriter(writer), null);
			str = writer.getBuffer().toString();	
		} catch (IOException e) {
			logger.info("Could not serialize job data. Trying alternate serialization...");
			boolean isFirst = true;
			for(Entry<Object, Object> o : p.entrySet()){
				if(isFirst){
					str += o.getKey() + "=" + o.getValue();
					isFirst = false;
				} else {
					str += "\n" + o.getKey() + "=" + o.getValue();
				}
			}
		}
		return str;
	}

	public static Properties readPropertiesFromString(String jobDataStr) throws IOException {
		final Properties p = new Properties();
		if(jobDataStr == null) return p;
	    p.load(new StringReader(jobDataStr));
	    return p;
	}
	
	public static Set<Integer> parseRangeListStr(String text, int nrOfPages) throws IOException {
		Set<Integer> pi = new HashSet<Integer>();
		return (Set<Integer>)parseRangeListStr(text, nrOfPages, pi);
	}
	
	public static List<Integer> parseRangeListStrToList(String text, int nrOfPages) throws IOException {
		List<Integer> pi = new LinkedList<Integer>();
		return (List<Integer>)parseRangeListStr(text, nrOfPages, pi);
	}

	private static Collection<Integer> parseRangeListStr(String text, int nrOfPages, Collection<Integer> pi) throws IOException {
//		List<Integer> pi = new LinkedList<Integer>();
		if(pi == null) throw new IOException("Collection is null!");
		if (text.isEmpty())
			return pi;
		
		String[] ranges = text.split(",");
		for (String r : ranges) {
			r = r.trim();
			if (r.isEmpty())
				continue;
			
			String[] numbers = r.split("-");
			try {
				if (numbers.length == 2) {
					int s = CoreUtils.bound(Integer.parseInt(numbers[0]), 1, nrOfPages);
					int e = CoreUtils.bound(Integer.parseInt(numbers[1]), 1, nrOfPages);
					for (int i=s-1; i<e; ++i) {
						logger.debug("adding page index: "+i);
						pi.add(i);
					}
				} else if (numbers.length == 1) {
					int s = Integer.parseInt(numbers[0]);
					if (s>=1 && s<=nrOfPages) {
						logger.debug("adding page index: "+(s-1));
						pi.add(s-1);
					}
				} else {
					throw new IOException("Could not parse invalid range: "+r);
				}
			} catch (NumberFormatException e) {
				throw new IOException("Error parsing number - "+e.getMessage(), e);
			}
		}
		return pi;
	}
	
	/**
	 * Returns a string of comma seperated ranges (starting from 1) indicating which values are true in the given list.
	 */
	public static String getRangeListStr(List<Boolean> l) {
		String str = "";
		
		int i=1;
		int start=-1;
		for (Boolean b : l) {
			if (b != null && b && start == -1) {
				start = i;
			} else if ((b==null || !b) && start != -1) {
				if (i-1 == start)
					str += ""+start+",";
				else
					str += ""+start+"-"+(i-1)+",";
				
				start=-1;
			}
			++i;
		}
		if (start!=-1) {
			if (i-1 == start)
				str += ""+start+",";
			else
				str += ""+start+"-"+(i-1)+",";
		}

		str = StringUtils.stripEnd(str, ",");
		return str;
	}
	
	/** Whether the URL is a file in the local file system. */
	public static boolean isLocalFile(java.net.URL url) {
		String scheme = url.getProtocol();
		return "file".equalsIgnoreCase(scheme) && !hasHost(url);
	}

	public static boolean hasHost(java.net.URL url) {
		String host = url.getHost();
		return host != null && !"".equals(host);
	}
	
//	public static <T> T mergeInto(T src, T dst, boolean conservative) {
//		if (src==null) return dst;
//		else return src;
//		
////		if (v1==null)
////			return conservative ? v2 : null;
////		else if (v2==null)
////			return conservative ? v1 : null;
////		else
////			return v1.equals(v2) ? v1 : null;
//	}
//	
//	public static Boolean mergeBoolean(Boolean src, Boolean dst) {
//		if (src==null) return dst;
//		else return src;
//		
//		
//		if (v1==v2)
//			return v1;
//		else {
//			if (v1==null)
//				return v2;
//			if (v2==null)
//				return v1;
//			return v1 || v2;
//		}
//	}	

}
