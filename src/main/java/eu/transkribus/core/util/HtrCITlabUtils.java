package eu.transkribus.core.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.TrpHtr;

public class HtrCITlabUtils {
	private static final Logger logger = LoggerFactory.getLogger(HtrCITlabUtils.class);
	public final static String PROVIDER_CITLAB = "CITlab";
	public final static String PROVIDER_CITLAB_PLUS = "CITlabPlus";
	public final static String PROVIDER_PYLAIA = "PyLaia";

	public static final String CITLAB_CM_EXT = ".cm";

	/**
	 * this will be located in the virtual FTP user storage
	 */
	public static final String TEMP_DICT_DIR_NAME = "dictTmp";

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("####0.00");
	
	/* 
	 * Constants below where necessary for CITlab legacy HTR. File are handled by CITlabModule now. 
	 * If access is needed use de.uros.citlab.module.types.Key
	 */
	@Deprecated
	public final static String CITLAB_SPRNN_FILENAME = "net.sprnn";	
	@Deprecated
	public final static String CITLAB_SPRNN_FOLDERNAME = "nets";	
	@Deprecated
	public final static String CITLAB_BEST_SPRNN_FILENAME = "best_net.sprnn";	
	@Deprecated
	public final static String CITLAB_CER_FILENAME = "CER.txt";	
	@Deprecated
	public final static String CITLAB_CER_TEST_FILENAME = "CER_test.txt";	
	@Deprecated
	public final static String CHAR_MAP_FILENAME = "chars.txt";
	
	static {
		DECIMAL_FORMAT.setRoundingMode(RoundingMode.UP);
	}
	
	public static File resolveDict(final File baseDir, String dictName) throws FileNotFoundException {
		return resolveDict(baseDir.getAbsolutePath(), dictName);
	}
	
	public static File resolveDict(final String baseDir, String dictName) throws FileNotFoundException {
		if (StringUtils.isEmpty(dictName)) {
			return null;
		}
		File dict = new File(baseDir, dictName);
		if (!dict.isFile()) {
			throw new FileNotFoundException("A dictionary by this name could not be found: " + dictName);
		}
		return dict;
	}
	
	public static List<String> getDictList(final String baseDirPath) {
		if(baseDirPath == null) {
			throw new IllegalArgumentException("baseDirPath argument is null.");
		}
		return getDictList(new File(baseDirPath));
	}
	
	public static List<String> getDictList(final File baseDir) {
		if(baseDir == null || !baseDir.isDirectory()) {
			throw new IllegalArgumentException("baseDir argument is null or not a directory.");
		}
		File[] dicts = baseDir.listFiles(new DictFileFilter());
		List<String> dictList = new ArrayList<>(dicts.length);
		
		for (File dict : dicts) {
			dictList.add(dict.getName());
		}
		return dictList;
	}
	
	public static String getCerSeriesString(File cerTestFile) {
		String str = readFileToString(cerTestFile);
		if(str != null && str.contains(",")) {
			logger.debug("Found CER series text file with decimal separator ','. Replacing with '.'");
			str = str.replace(",", ".");
		}
		return str;
	}

	public static String getCharsetFromCITlabCharMap(File charMapFile) {
		final String charSetStr = readFileToString(charMapFile);
		if(charSetStr == null) {
			return null;
		}
		List<String> charSet = parseCitLabCharMap(charSetStr);
		return charSet.stream().collect(Collectors.joining("\n"));
	}
	
	private static String readFileToString(File file) {
		if(file == null) {
			logger.error("HTR metadata file is null.");
			return null;
		}
		if(!file.isFile()) {
			logger.error("HTR metadata file does not exist: " + file.getAbsolutePath());
			return null;
		}
		String content = null;
		try {
			content = FileUtils.readFileToString(file, DeaFileUtils.DEFAULT_CHARSET);
		} catch (IOException e) {
			logger.error("Could not read HTR metadata file: " + file.getName(), e);
			throw new RuntimeException(e);
		}
		return content;
	}

	public static List<String> parseCitLabCharMap(String charSet) {
		Pattern p = Pattern.compile("(.)=[0-9]+");
		Matcher m = p.matcher(charSet);
		List<String> result = new LinkedList<>();
		while (m.find()) {
			result.add(m.group(1));
		}
		return result;
	}
	
	public static double[] parseCitlabCerFile(File cerFile) throws IOException {
		final String cerString = FileUtils.readFileToString(cerFile);
		return HtrCITlabUtils.parseCitlabCerString(cerString);
	}

	public static double[] parseCitlabCerString(String cerString) {

		if (cerString == null || cerString.isEmpty()) {
			return new double[] {};
		}

		String[] cerStrs = cerString.split("\\s");
		double[] cerVals = new double[cerStrs.length];
		for (int i = 0; i < cerStrs.length; i++) {
			try {
				//FIXME separator should be a dot! but is a comma with locale de_at!!
				cerVals[i] = Double.parseDouble(cerStrs[i].replace(',', '.'));
			} catch (NumberFormatException e) {
				logger.error("Could not parse CER String: " + cerStrs[i]);
			}
		}
		return cerVals;
	}

	public static String printLastCerPercentage(double[] cerVals) {
		if(cerVals.length < 1) {
			return "N/A";
		}
		final double finalCerVal = cerVals[cerVals.length-1];
		return formatCerVal(finalCerVal);
	}
	
	public static String formatCerVal(double val) {
		return DECIMAL_FORMAT.format(val*100) + "%";
	}
	
	public static class DictFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			return pathname.isFile() && pathname.getName().endsWith(".dict");
		}
	}
	
	public static boolean loadDataFromFileSystem(TrpHtr h) {
		boolean hasChanged = false;
		
		switch (h.getProvider()) {
		case HtrCITlabUtils.PROVIDER_CITLAB:
			File bestNetFile = new File(h.getPath(), HtrCITlabUtils.CITLAB_BEST_SPRNN_FILENAME);
			boolean hasBestNet = bestNetFile.isFile();
			hasChanged |= hasBestNet != h.isBestNetStored();
			h.setBestNetStored(hasBestNet);
			//don't break, we need the next section too
		case HtrCITlabUtils.PROVIDER_CITLAB_PLUS:
			if(h.getCerString() == null) {
				File cerFile = new File(h.getPath(), HtrCITlabUtils.CITLAB_CER_FILENAME);
				h.setCerString(HtrCITlabUtils.getCerSeriesString(cerFile));
				hasChanged |= !StringUtils.isEmpty(h.getCerString());
			}
			if(h.getCerTestString() == null) {
				File cerTestFile = new File(h.getPath(), HtrCITlabUtils.CITLAB_CER_TEST_FILENAME);
				h.setCerTestString(HtrCITlabUtils.getCerSeriesString(cerTestFile));
				hasChanged |= !StringUtils.isEmpty(h.getCerTestString());
			}
			if(h.getCharSetString() == null) {
				File charMapFile = new File(h.getPath(), HtrCITlabUtils.CHAR_MAP_FILENAME);
				h.setCharSetString(HtrCITlabUtils.getCharsetFromCITlabCharMap(charMapFile));
				hasChanged |= !StringUtils.isEmpty(h.getCharSetString());
			}		
			break;
		default:
			break;
		}
		return hasChanged;
	}
	
	public static void main(String[] args) {
		double test = 0.33444;
		System.out.println(formatCerVal(test));
	}
}
