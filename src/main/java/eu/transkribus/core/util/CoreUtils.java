package eu.transkribus.core.util;

import java.awt.Point;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.DocumentSelectionDescriptor;

public class CoreUtils {
	private final static Logger logger = LoggerFactory.getLogger(CoreUtils.class);
	private static final AtomicLong LAST_TIME_MS = new AtomicLong();
	
	public static int SIZE_OF_LOG_FILE_TAIL = 256;
	
	public final static String DATE_FORMAT_STR = "dd_MM_yyyy_HH:mm";
	public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STR);
	
	public final static String DATE_FORMAT_STR_USER_FRIENDLY = "dd.MM.YY HH:mm:ss";
	public final static SimpleDateFormat DATE_FORMAT_USER_FRIENDLY = new SimpleDateFormat(DATE_FORMAT_STR_USER_FRIENDLY);
	
//	public static void main(String[] args) {
//		String str = " Gregor S,_;amsa wacht eines M\"orgens auf u[]nd stellt fe(st, dass er „zu) einem ungeheueren 2,000 Ungeziefer verwandelt“ wurde 3.000";
//
//		if (false) {
//			String tokenized = tokenizeForCATTI(str);
//			String detokenized = detokenizeForCATTI(tokenized);
//	
//			System.out.println(str);
//			System.out.println(tokenized);
//			System.out.println(detokenized);
//		}
//		
//		if (true) {
//			int N = getNOfRepeatingChars(str, 0, ' ');
//			System.out.println("N = "+N);
//		}		
//		
//	}
	
	public static <T> List<T> addNewElements(List<T> list, List<T> elementsToAdd) {
		List<T> added = new ArrayList<>();
		for (T element : elementsToAdd) {
			if (!list.contains(element)) {
				list.add(element);
				added.add(element);
			}
		}
		return added;
	}
	
	public static List<Path> listFilesRecursive(String Path, String[] extensions, boolean caseSensitive, String... excludeFilenames) throws IOException {
		return Files.walk(Paths.get(Path))
			.filter(Files::isRegularFile)
			.filter(new Predicate<Path>() {
				@Override
				public boolean test(Path t) {
					String name = caseSensitive ? t.toFile().getName() : t.toFile().getName().toLowerCase();
					
					for (String extension : extensions) {
						String ext = caseSensitive ? extension : extension.toLowerCase();
						
						if (!name.endsWith(ext))
							continue;
						
						boolean doExcludeFile=false;
						for (String exclude : excludeFilenames) {
							exclude = caseSensitive ? exclude : exclude.toLowerCase();
							
							if (name.equals(exclude)) {
								doExcludeFile=true;
								break;
							}
						}
						if (doExcludeFile) {
							continue;
						}
	
						return true;
					}
					
					return false;
				}
			})
			.collect(Collectors.toList());
	}
	
	public static void convertDocxFilesToTxtFiles(String inputFolder, String outputFolder, boolean overwrite) throws IOException, Docx4JException, JAXBException {
		File inDir = new File(inputFolder);
		if (!inDir.exists()) {
			throw new IOException("Input folder does not exist: "+inputFolder);
		}
		
		File outDir = createDirectory(outputFolder, overwrite);
		
		File[] docxFiles = inDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".docx");
			}
		});
		
		for (File docx : docxFiles) {
			String txt = extractTextFromDocx(docx.getAbsolutePath());
			String basename = FilenameUtils.getBaseName(docx.getName());
			Files.write(Paths.get(outDir.getAbsolutePath()+File.separator+basename+".txt"), txt.getBytes());
		}
	}
	
	/**
	 * FIXME not sure if this method does extract text from every possible variation of a docx...
	 */
	public static String extractTextFromDocx(String filename) throws Docx4JException, JAXBException {
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new java.io.File(filename));
		MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
		
		String pageTxt = "";
		for (Object o : documentPart.getContent()) {
			if (o == null)
				continue;
			
			pageTxt += o.toString() + "\n";		
		}

		return StringUtils.stripEnd(pageTxt, "\n");
	}
	
	public static String replaceInvalidPathChars(String title, String replacement) {
		return title.replaceAll("([/\\?%*:| \"<>. \\\\])", replacement);	
	}
	
	public static File createDirectory(String path, boolean overwrite) throws IOException {
		File dir = new File(path);
		if (dir.exists()) {
			if (overwrite) {
				FileUtils.forceDelete(dir);	
			}
			else {
				throw new IOException("Output path already exists: "+path);
			}
		}
		
		if (!dir.mkdirs())
			throw new IOException("Could not create directory: "+path);
		
		return dir;
	}
	
	@SafeVarargs
	public static <T> List<T> asList(T... array) {
		if (array==null)
			return new ArrayList<>();
		
		return Arrays.asList(array);
	}
	
	public static void loadTranskribusInterfacesLib() {
		SebisStopWatch sw = new SebisStopWatch();
		String libName = "TranskribusInterfacesWrapper";
		try {
			sw.start();
			System.loadLibrary(libName);
			sw.stop(true, "Loaded transkribus interfaces lib in ", logger);
		} catch (UnsatisfiedLinkError e) {
			throw new RuntimeException("Could not load "+libName+".so: " + e.getMessage(), e);
		}
	}
	
	public static File getFileFromPossiblePaths(String... paths) throws FileNotFoundException {
		for (String path : paths) {
			File f = new File(path);
			if (f.isFile())
				return f;
		}
		
		throw new FileNotFoundException("File not found in paths: "+CoreUtils.join(Arrays.asList(paths)));
	}
	
	public static int countNumberOfFolders(File folder) {
		if (folder != null) {
			return folder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory();
				}
			}).length;
		} else {
			return 0;
		}
	}
		
	public static String[] appendValue(String[] arr, String newObj) {
		List<String> temp = new ArrayList<>();
		if (arr != null) {
			temp = new ArrayList<String>(Arrays.asList(arr));
		}
		
		temp.add(newObj);
		return temp.toArray(new String[0]);
	}
	
	public static <E extends Enum<E>> String[] appendValue(String[] arr, E newObj) {
		if(newObj == null) {
			return appendValue(arr, (String)null);
		} else {
			return appendValue(arr, newObj.toString());
		}
	}
	
	public static String[] appendKeyValue(String[] arr, String key, Object value, boolean skipIfValueIsNull) {
		if (StringUtils.isEmpty(key)) {
			return arr;
		}
		
		if (skipIfValueIsNull && value == null) {
			return arr;
		}
		
		arr = CoreUtils.appendValue(arr, key);
		
		if (value == null) {
			arr = CoreUtils.appendValue(arr, (String) null);	
		} else {
			arr = CoreUtils.appendValue(arr, value.toString());
		}
		
		return arr;
	}
	
	public static List<String> parseStringList(String str, boolean trimEntries) {
		List<String> result = new ArrayList<>();
		
		if(str != null && !str.isEmpty()) {
			String[] arr = str.split(",");
			for(String s : arr) {
				if (trimEntries) {
					s = StringUtils.trim(s);
				}
				result.add(s);
			}
		}
		
		return result;
	}
	
	public static int parseInt(String str, int errorVal) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return errorVal;
		}
	}
	
	public static List<Integer> parseIntList(String str) {
		List<Integer> result = new ArrayList<>();
		
		if(str != null && !str.isEmpty()) {
			String[] arr = str.split(",");
			for(String s : arr) {
				try {
					result.add(Integer.parseInt(s));
				} catch (Exception e) {
					logger.error("Error parsing integer value: "+s+" - skipping!");
				}
			}
		}
		
		return result;
	}
	
	public static Properties copyProperties(Properties props) {
		Properties propsCopy = new Properties();
		if (props != null) {
			propsCopy.putAll(props);
		}
		
		return propsCopy;
	}
	
	public static void print(List l) {
		if (l != null) {
			l.stream().forEach((j)-> {
				if (j != null)
					logger.info(j.toString());
				else
					logger.info("null");
			});
		}
	}
		
	public static Properties loadProperties(String filename) throws IOException {
		logger.debug("Loading properties file: " + filename);
		
		try (InputStream is = CoreUtils.class.getClassLoader().getResourceAsStream(filename)) {
			Properties props = new Properties();
			props.load(is);
			return props;
		} catch (Exception e) {
			throw new IOException("Could not find properties file: " + filename, e);
		}
	}
	
	public static String join(Iterable<?> iterable) {
		return join(iterable, ",", "", "");
	}
	
	/**
	 * Joins the objects of an iterable to a string using a delimiter and a prefix and/or suffix to append to each object
	 */
	public static String join(Iterable<?> iterable, String delimiter, String prefix, String suffix) {
		if (iterable==null)
			return "";
		if (delimiter==null)
			delimiter="";
		if (prefix==null)
			prefix="";
		if (suffix==null)
			suffix="";
		
		String joined="";
		for (Object o : iterable) {
			String str = o==null ? "null" : o.toString();
			str = prefix + str + suffix;
			
			joined += str+delimiter;
		}
		joined = StringUtils.stripEnd(joined, delimiter);
		
		return joined;
	}
	
	public static String neighborString(String str, int startIndex, int maxChars, boolean direction, boolean stopAtWs) {
		if (str == null || startIndex < 0 || startIndex >= str.length())
			return "";

		String txt = "";
		int i = startIndex;
		boolean stop=false;
		do {
			char c = str.charAt(i);
			
			if (direction) {
				txt = c+txt;
				--i;
			} else {
				txt += c;
				++i;
			}
			
			stop = txt.length() >= maxChars;
			if (stop && stopAtWs && !Character.isWhitespace(c)) {
				stop = false;
			}
		} while (!stop && i>=0 && i<str.length());
		
		return txt;
	}
	
	public static String appendIfNotEmpty(String s, String suffix) {
		if (!StringUtils.isEmpty(s))
			return s+suffix;
		else
			return s;
	}
	
	public static <T> HashSet<T> createHashSet(Collection<T> c) {
		if (c==null)
			return null;
		
		HashSet<T> s = new HashSet<>();
		for (T e : c) {
			s.add(e);
		}
		return s;
	}
	
	public static <T> Set<T> createSet(T... elements) {
		Set<T> s = new HashSet<>();
		for (T e : elements) {
			s.add(e);
		}
		
		return s;
	}
	
	public static <T> boolean isEmpty(T... list) {
		return list==null || list.length==0;
	}
		
	public static boolean isEmpty(Collection<?> c) {
		return c==null || c.isEmpty();
	}
	
	public static boolean isEmpty(Map<?, ?> m) {
		return m==null || m.isEmpty();
	}
	
	public static int toInt(Integer i) {
		return i==null ? 0 : i.intValue();
	}
		
	public static boolean containsIgnoreCase(Collection<String> l, String s) {
		Iterator<String> it = l.iterator();
		while (it.hasNext()) {
			if (it.next().equalsIgnoreCase(s))
				return true;
		}
		return false;
	}
	
	public static String readStringFromTxtFile(String fn) throws IOException {
		String content = new String(Files.readAllBytes(Paths.get(fn)));
		return content;
	}
	
	public static void setLibraryPath(String path) throws Exception {
		System.setProperty("java.library.path", path);

		// set sys_paths to null so that java.library.path will be reevalueted next time it is needed
		final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
		sysPathsField.setAccessible(true);
		sysPathsField.set(null, null);
	}
	
	/**
	 * returns string representation of a url. if url is a file, the platform specific path 
	 * is returned i.e. c:\whatever\path\file.jpg instead of file:/c:/whatever/path/file.jpg\n
	 *  for a null input url, an empty string is returned
	 */
	public static String urlToString(URL url) {
		if (url != null) {
			File file = FileUtils.toFile(url);
			if (file!=null)
				return file.getAbsolutePath();
			else
				return url.toString();
		}
		
		return "";
	}
	
	public static void deleteDir(File dir) {
		if (dir != null) {
			logger.debug("deleting dir: "+dir.getAbsolutePath());
			try {
				FileUtils.deleteDirectory(dir);
			} catch (IOException e) {
				logger.error("Error deleting directory: "+e.getMessage(), e);
			}
		}
	}
	
	public static <T> List<T> getFirstCommonSequence(List<T> base, List<T> search) {
		List<T> common = new ArrayList<>();
		
		int li=-1;
		for (T s : search) {
			int i = base.indexOf(s);			
			if (i == -1) { // not found
				if (li==-1)
					continue;
				else // if element has been found already
					break;
			} else if (li != -1 && i-1 != li) { // element found but not after last found elment!
				break;
			} else {
				li = i;
				common.add(s);
			}
		}
		
		return common;
	}
	
	public static int [] getPointArray(List<Point> pts) {
		int [] pointArray = new int[pts.size()*2];
		for (int i=0; i<pts.size(); ++i) {
			pointArray[i*2] = pts.get(i).x;
			pointArray[i*2+1] = pts.get(i).y;
		}
		return pointArray;
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
	
	public static String toListString(String[] list) {
		return "("+StringUtils.join(list)+")";
	}
	
	/**
	 * Generates a comma separated list string out of the given collection, e.g. (2,4,2,1)
	 */
	public static String toListString(Collection<?> list) {
		return "("+StringUtils.join(list)+")";
		
//		String str = "(";
//		for (Object o : list) {
//			str += o+",";
//		}
//		str = StringUtils.removeEnd(str, ",");
//		
//		str += ")";
//		return str;
	}
	
	/**
	 * Checks if the given pathToFile exists and generates an alternative filename, 
	 * trying to add an increasing number suffix of size at most suffixSize
	 */
	public static String createNonExistingFilename(String pathToFile, int suffixSize) {
		String uniquePathToFile = pathToFile;
		
		int i=1;
		while (fileExists(uniquePathToFile)) {
			uniquePathToFile = pathToFile + "_"+ StringUtils.leftPad((""+i), suffixSize, "0");
			
			++i;
		}
		
		return uniquePathToFile;
	}
	
	public static boolean fileExists(String path) {
		return Files.exists(Paths.get(path));
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
	
	public static int indexOf(String txt, String searchStr, int startOffset, boolean previous, boolean caseSensitive, boolean wholeWord) {
		searchStr = Pattern.quote(searchStr);
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
	
	public static String createRegexFromSearchString(String ss, boolean wildcardAlsoWhitespaces, boolean wholeWord, boolean caseSensitive) {
		String anyChar = wildcardAlsoWhitespaces ? "." : "\\w"; // \w for only words charactesr or . for ANY character (+ whitespaces!)

		String nss = new String(ss); // needed???
		nss = nss.replace("*", anyChar+"*");
		nss = nss.replace("?", anyChar);
		
		if (nss.isEmpty())
			return nss;
		
		if (wholeWord)
			nss = "\\b"+nss+"\\b";
		
		if (!caseSensitive)
			nss = "(?i:"+nss+")";
		
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
	
	public static <T> List<T> copyList(List<T> list) {
		List<T> newList = new ArrayList<>();
		if (list != null) {
			for (T i : list) {
				newList.add(i);
			}
		}
		return newList;
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

	public static String propertiesToString(Properties p) {
		if(p == null){
			return "";
		}
		StringWriter writer = new StringWriter();
		String str = "";
		try {
			p.store(new PrintWriter(writer), null);
			str = writer.getBuffer().toString();	
		} catch (IOException e) {
			logger.info("Could not serialize Properties data. Trying alternate serialization...");
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

	public static Properties readPropertiesFromString(String fn) throws IOException {
		final Properties p = new Properties();
		if(fn == null) return p;
	    p.load(new StringReader(fn));
	    return p;
	}
	
	/**
	 * Same as readPropertiesFromString but an error message is logged when not able to read the file and a RuntimeException is thrown
	 */
	public static Properties readPropertiesFromString2(String fn) throws RuntimeException {
		try {
			return readPropertiesFromString(fn);
		} catch (IOException e) {
			logger.error("Could not read properties file: "+e.getMessage(), e);
			throw new RuntimeException("Could not read properties file: "+e.getMessage(), e);
		}
	}
	
	public static boolean isValidRangeListStr(String text, int nrOfPages) {
		try {
			parseRangeListStr(text, nrOfPages);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static String invertRangeListStr(String rangeStringForTrainSet, int nPages) {
		Set<Integer> pageIndicesForTrain;
		try {
			pageIndicesForTrain = CoreUtils.parseRangeListStr(rangeStringForTrainSet, nPages);
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Invalid range string was given!");
		}
		
		Set<Integer> pageIndicesForTest = CoreUtils.invertPageIndices(pageIndicesForTrain, nPages);
			
		if(pageIndicesForTest.isEmpty()) {
			throw new IllegalArgumentException("Given range string covers all the document!");
		}
		
		return CoreUtils.getRangeListStrFromSet(pageIndicesForTest);
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
		if (StringUtils.isEmpty(text))
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
						logger.trace("adding page index: "+i);
						pi.add(i);
					}
				} else if (numbers.length == 1) {
					int s = Integer.parseInt(numbers[0]);
					if (s>=1 && s<=nrOfPages) {
						logger.trace("adding page index: "+(s-1));
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
	

	public static Set<Integer> invertPageIndices(Set<Integer> pageIndices, int nrOfPages) {
		Set<Integer> out = new HashSet<>();
		for(Integer i = 0; i < nrOfPages; i++) {
			if(!pageIndices.contains(i)) {
				out.add(i);
			}
		}
		return out;
	}
	
	public static String getRangeListStrFromSet(Set<Integer> set) {
		if (set == null)
			return "";
		
		List<Integer> list = new ArrayList<>();
		list.addAll(set);
		
		return getRangeListStrFromList(list);
	}
	
	public static String getRangeListStrFromList(List<Integer> l) {
		if (l == null)
			return "";
		
		Collections.sort(l);
		
		String str = "";
		
		Integer last=null;
		for (Integer i : l) {
			if (StringUtils.isEmpty(str)) {
				str += (i+1);
			}
			else if (i==last+1) {
				str = str.replaceAll("\\-\\d+$", "");
				str += "-"+(i+1);
			}
			else {
				str += ","+(i+1);
			}
			
			last = i;
		}

		return str;
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
	
	public static String createSqlStringQuery(String colName, String searchString, boolean exactMatch, boolean caseSensitive) {
		String sql = caseSensitive ? "lower("+colName+")" : colName;
		sql += " like ";
		
		if (!exactMatch) {
			searchString = searchString.replaceAll("*", "%");
			searchString = searchString.replaceAll("?", "_");
		}
		
		if (!caseSensitive) {
			searchString = searchString.toLowerCase();
		}
		
		sql += searchString;
		
		return sql;
	}
	
	public static String removeHtmlAtEnd(String msg) {
		int is = msg.indexOf("<html>");
		if (is > 0) {
			return msg.substring(0, is);
		}
		
		return msg;
	}
	
//	public static String replaceNonPathCharacters(String str, String replace) {
//		return str.replaceAll("[\\/:*?\"<>|]", replace);
//	}
	
	public static void main(String[] args) throws Exception {
//		List<Integer> base = Arrays.asList(1, 3, 4, 5, 7, 10);
//		List<Integer> search = Arrays.asList(111, 45, 3, 4, 6, 8, 12);
//		
//		List<Integer> common = CoreUtils.getFirstCommonSequence(base, search);
		
//		System.out.println("common = "+StringUtils.join(common));
		
//		String pageTxt = CoreUtils.extractTextFromDocx("/home/sebastian/Downloads/Ms__orient__A_2654/Ms__orient__A_2654/docx/0001.docx");
//		
//		logger.info("pageTxt:");
//		logger.info(pageTxt);
//		logger.info("nr of lines: "+pageTxt.split("\n").length);
		
		convertDocxFilesToTxtFiles("/mnt/dea_scratch/TRP/test/Ms__orient__A_2654/Ms__orient__A_2654/docx", "/mnt/dea_scratch/TRP/test/Ms__orient__A_2654/Ms__orient__A_2654/txt", true);
	}

	public static int size(Collection<?> collection) {
		return collection==null ? 0 : collection.size();
	}
}
