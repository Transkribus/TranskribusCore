package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtrPyLaiaUtils {
	private static final Logger logger = LoggerFactory.getLogger(HtrPyLaiaUtils.class);
	
	public final static String PROVIDER_PYLAIA = "PyLaia";
	public final static String WORKDIR_NAME = PROVIDER_PYLAIA+"Workdir";
	public static final String TEXT_FEATS_CFG_FILENAME = "textFeatsCfg";
	public static final String CREATE_MODEL_PARS_FILENAME = "createModelPars";
	public static final String TRAIN_CTC_PARS_FILENAME = "trainCtcPars";
	public static final String SYMBOLS_FILENAME = "symbols.txt";
	public static final String LM_SUBDIR = "lm";
	public static final String HYPOTHESES_FILENAME = "hypotheses.out";
	
	public static boolean doesDecodingSupportDicts() {
		return false;
	}
	
	public static String getCerSeriesString(File cerTrain, boolean toFraction) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(cerTrain.getAbsolutePath()), Charset.defaultCharset());
		String str = "";
		for (String l : lines) {
			try {
				Double v = Double.parseDouble(l);
				if (toFraction) {
					v /= 100.0d;
				}
				str += v+"\n";
			} catch (Exception e) {
				logger.error("Could not parse CER string: " + l + " - skipping!");
			}
		}
		
		return str;
	}

	public static String getCharsetFromSymbolsFile(File charMapFile) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(charMapFile.getAbsolutePath()), Charset.defaultCharset());
		String str = "";
		for (String l : lines) {
			str += l.split("\\s+")[0]+"\n";
		}
		return str;
	}

}
