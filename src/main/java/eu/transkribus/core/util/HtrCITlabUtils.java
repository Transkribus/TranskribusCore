package eu.transkribus.core.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;

public class HtrCITlabUtils {
	public final static String PROVIDER_CITLAB = "CITlab";
	public final static String CITLAB_SPRNN_FILENAME = "net.sprnn";
	public final static String CITLAB_CER_FILENAME = "CER.txt";
	public final static String CITLAB_CER_TEST_FILENAME = "CER_test.txt";
	public final static String CHAR_MAP_FILENAME = "chars.txt";
	public static final String CITLAB_CM_EXT = ".cm";
	
	public static File resolveDict(String dictName) {
		if (StringUtils.isEmpty(dictName)) {
			return null;
		}
		final String baseDir = "/mnt/dea_scratch/TRP/HTR/RNN";
		File dict = new File(baseDir + "/dict/" + dictName);
		if (!dict.isFile()) {
			return null;
		}
		return dict;
	}
}
