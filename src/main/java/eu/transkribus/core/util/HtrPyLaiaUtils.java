package eu.transkribus.core.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
	public static final String LM_CREATED_SUCCESS_FN = "created.success";
	public static final String MODEL_DIR_NAME = "model";
	
	public static final String PYLAIA_PROVIDER_STRING="READ-COOP";
	public static final String PYLAIA_NAME_STRING = "PyLaia@TranskribusPlatform";
	public static final String PYLAIA_LM_PROVIDED_STRING = "provided";
	public static final String PYLAIA_LM_NONE_STRING = "none";
	
	public static final String CHECKPOINT_PREFIX_BEST_CER = "experiment.ckpt.lowest-valid-cer-";
	public static final String CHECKPOINT_PREFIX_BEST_WER = "experiment.ckpt.lowest-valid-wer-";
	public static final String CHECKPOINT_PREFIX_LATEST = "experiment.ckpt-";
	
	public static final String BASE_MODEL_ID_KEY = "baseModelId";
	public static final String BASE_MODEL_NAME_KEY = "baseModelName";
	
	public static final String[] CHECKPOINT_PREFIXES = { CHECKPOINT_PREFIX_LATEST, CHECKPOINT_PREFIX_BEST_CER, CHECKPOINT_PREFIX_BEST_WER };
	
	public static void removeOldCheckpoints(File modelDir, int nKeep) {
		logger.debug("removing old checkpoints from "+modelDir+", nKeep = "+nKeep);
		int c=0;
		for (String prefix : CHECKPOINT_PREFIXES) {
			List<File> ckpts = listCheckpoints(modelDir, prefix);
			ckpts.sort(new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					int e1 = getEpochFromCheckpointFile2(o1);
					int e2 = getEpochFromCheckpointFile2(o2);
					return Integer.compare(e1, e2);
				}
			});
			if (ckpts.size() > nKeep) {
				for (int i=0; i<ckpts.size()-nKeep; ++i) {
					File f = ckpts.get(i);
					logger.debug("deleting old checkpoint file: "+f.getName());
					f.delete();
					++c;
				}
			}
		}
		logger.debug("deleting "+c+" old checkpoints!");
	}
	
	public static String getCheckpointParameter(File modelDir, String preferred, boolean forTraining) throws IOException {
		String p = getCheckpointPrefix(modelDir, preferred);
		
		return forTraining ? StringUtils.removeStart(p, "experiment.")+"*" : p+"*";
	}
	
	public static int getEpochFromCheckpoint(File modelDir, String preferredPrefix) throws IOException {
		String p = getCheckpointPrefix(modelDir, preferredPrefix);
		List<File> ckpts = listCheckpoints(modelDir, p);
		Collections.sort(ckpts);
		File f = ckpts.get(ckpts.size()-1);
		return getEpochFromCheckpointFile(f);
	}
	
	public static int getEpochFromCheckpointFile2(File f) {
		try {
			return getEpochFromCheckpointFile(f);
		} catch (IOException e) {
			return -1;
		}
	}
	
	public static int getEpochFromCheckpointFile(File f) throws IOException {
		String epochStr = f.getName().substring(f.getName().lastIndexOf("-")+1);
		try {
			return Integer.parseInt(epochStr);
		} catch (NumberFormatException e) {
			throw new IOException("Could not Parse epochStr: "+epochStr, e);
		}
	}
	
	public static String getCheckpointPrefix(File modelDir, String preferredPrefix) throws IOException {
		List<String> possible = listPossibleCheckpointPrefixes(modelDir);
		if (possible.isEmpty()) {
			throw new IOException("No checkpoint found in model directory: "+modelDir);
		}
		
		String p = possible.get(0);
		if (preferredPrefix != null && possible.contains(preferredPrefix)) {
			p = preferredPrefix;
		}
		
		return p;
	}	
	
	public static List<String> listPossibleCheckpointPrefixes(File modelDir) {
		List<String> possible = new ArrayList<>();
		for (String p : CHECKPOINT_PREFIXES) {
			List<File> ckpts = listCheckpoints(modelDir, p);
			if (!ckpts.isEmpty()) {
				possible.add(p);
			}
		}
		return possible;
	}
	
	public static List<File> listCheckpoints(File modelDir, String prefix) {
    	File[] ckpts = modelDir.listFiles(new FilenameFilter() {
			@Override public boolean accept(File dir, String name) {
				return name.startsWith(prefix);
			}
		});
    	return Arrays.asList(ckpts);
	}
	
	public static String getCreatorString(String version, String modelId, boolean usedInternalLM) {
		String str = "prov="+PYLAIA_PROVIDER_STRING;
		str += ":name="+PYLAIA_NAME_STRING;
		if (!StringUtils.isEmpty(version)) {
			str += ":version="+version;
		}
		if (!StringUtils.isEmpty(modelId)) {
			str += ":model_id="+modelId;
		}
		if (usedInternalLM) {
			str += ":lm="+PYLAIA_LM_PROVIDED_STRING;
		} else {
			str += ":lm="+PYLAIA_LM_NONE_STRING;
		}
		if (true) {
			str += ":date="+CoreUtils.newDateFormat().format(new Date());
		}

		return str;
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
		List<String> lines = Files.readAllLines(Paths.get(charMapFile.getAbsolutePath()), Charset.forName("UTF-8"));
		String str = "";
		for (String l : lines) {
			str += l.split("\\s+")[0]+"\n";
		}
		return str;
	}
	
	public static void main(String[] args) throws Exception {
		File modelDir = new File("T:\\HTR\\DEAT\\PyLaiaWorkdir\\pylaiaTrain_20499_3\\model");
//		String preferredPrefix = CHECKPOINT_PREFIX_LATEST;
//		String preferredPrefix = "irgendwas";
		String preferredPrefix = CHECKPOINT_PREFIX_BEST_WER;
		
		int epoch = HtrPyLaiaUtils.getEpochFromCheckpoint(modelDir, preferredPrefix);
		System.out.println("epoch = "+epoch);
		
		System.out.println(HtrPyLaiaUtils.getCheckpointParameter(modelDir, preferredPrefix, true));
		System.out.println(HtrPyLaiaUtils.getCheckpointParameter(modelDir, preferredPrefix, false));	
	}

}
