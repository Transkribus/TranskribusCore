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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtrCITlabUtils {
	private static final Logger logger = LoggerFactory.getLogger(HtrCITlabUtils.class);
	public final static String PROVIDER_CITLAB = "CITlab";
	public final static String CITLAB_SPRNN_FILENAME = "net.sprnn";
	public final static String CITLAB_SPRNN_FOLDERNAME = "nets";
	public final static String CITLAB_BEST_SPRNN_FILENAME = "best_net.sprnn";
	public final static String CITLAB_CER_FILENAME = "CER.txt";
	public final static String CITLAB_CER_TEST_FILENAME = "CER_test.txt";
	public final static String CHAR_MAP_FILENAME = "chars.txt";
	public static final String CITLAB_CM_EXT = ".cm";
	
	@Deprecated
	public static final String NET_PATH = "/mnt/dea_scratch/TRP/HTR/RNN/net";
	public static final String DICT_PATH = "/mnt/dea_scratch/TRP/HTR/RNN/dict";

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("####0.00");
	static {
		DECIMAL_FORMAT.setRoundingMode(RoundingMode.UP);
	}
	
	public static File resolveDict(String dictName) throws FileNotFoundException {
		if (StringUtils.isEmpty(dictName)) {
			return null;
		}
		final String baseDir = HtrCITlabUtils.DICT_PATH;
		File dict = new File(baseDir + File.separator + dictName);
		if (!dict.isFile()) {
			throw new FileNotFoundException("A dictionary by this name could not be found: " + dictName);
		}
		return dict;
	}
	

	@Deprecated
	public static File[] getNetList() {
		File[] models = new File(HtrCITlabUtils.NET_PATH).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".sprnn");
			}
		});
		return models;
	}

	@Deprecated
	public static String getNetListStr() {
		File[] models = getNetList();

		String modelStr = "";
		boolean isFirst = true;
		for (File model : models) {
			if (isFirst) {
				modelStr += model.getName();
				isFirst = false;
			} else {
				modelStr += "\n" + model.getName();
			}
		}
		return modelStr;
	}

	public static String getDictListStr() {
		File[] dicts = new File(HtrCITlabUtils.DICT_PATH).listFiles(new DictFileFilter());

		String modelStr = "";
		boolean isFirst = true;
		for (File dict : dicts) {
			if (isFirst) {
				modelStr += dict.getName();
				isFirst = false;
			} else {
				modelStr += "\n" + dict.getName();
			}
		}
		return modelStr;
	}
	
	public static List<String> getDictList() {
		File[] dicts = new File(HtrCITlabUtils.DICT_PATH).listFiles(new DictFileFilter());
		List<String> dictList = new ArrayList<>(dicts.length);
		
		for (File dict : dicts) {
			dictList.add(dict.getName());
		}
		return dictList;
	}

	public static List<String> parseCitLabCharSet(String charSet) {
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
	
	public static void main(String[] args) {
		double test = 0.33444;
		System.out.println(formatCerVal(test));
	}
}
