package eu.transkribus.core.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.HtrModel;
import eu.transkribus.core.model.beans.WordHypothesis;

public class HtrUtils {
	private static final Logger logger = LoggerFactory.getLogger(HtrUtils.class);

	public static final String MODEL_PATH = "/mnt/dea_scratch/TRP/HTR/models/";

	public static final String SEP = ".";
	public static final String SEP_PATTERN = "\\.";

	// the file extension of line imgs
	public static final String LINE_IMG_EXT = ".png";
	// preprocessed lines have to be stored as pgm file!
	public static final String PREPROC_LINE_EXT = ".pgm";
	// files containing features of line imgs
	public static final String FEAT_FILE_EXT = ".fea";
	// this file stores relative (to lineImgDir i.e. workdir) paths to feature
	// files
	public static final String TEST_LIST_NAME = "list.test";

	// 300 594 recapitulatory -7436.380371
	private static final String REC_LINE_PATTERN = "[0-9]+\\s[0-9]+\\s(.*)\\s-?[0-9]+\\.[0-9]+";
	public static final String FILENAME_PATTERN = "([A-Z]{24})" + SEP_PATTERN + "([0-9]+)" + SEP_PATTERN + "([0-9]+)"
			+ SEP_PATTERN + "([a-zA-Z0-9[_-]]+)" + SEP_PATTERN + "([a-zA-Z0-9[_-]]+)";

	private static final String FLOAT_NR = "[+-]?(?:[0-9]*\\.[0-9]+|[0-9]+)"; // regex
																				// for
																				// floating
																				// point
																				// nr.
																				// with
																				// optional
																				// -
																				// sign
	// private static final String LATTICE_TOOL_LINE_PATTERN_STR =
	// "-?[0-9]+\\.[0-9]+\\s-?[0-9]+\\.[0-9]+\\s[0-9]+\\s<s>\\s(.*)\\s</s>";
	private static final String LATTICE_TOOL_LINE_PATTERN_STR = FLOAT_NR + "\\s" + FLOAT_NR
			+ "\\s[0-9]+\\s<s>\\s(.*)\\s</s>";
	static final Pattern LATTICE_TOOL_LINE_PATTERN = Pattern.compile(LATTICE_TOOL_LINE_PATTERN_STR);

	public static final String NET_PATH = "/mnt/dea_scratch/TRP/HTR/RNN/net";
	public static final String DICT_PATH = "/mnt/dea_scratch/TRP/HTR/RNN/dict";

	/**
	 * Each extracted line image is named with the PAGE file's ID plus the
	 * positional number of region and line according to the reading order, and
	 * the original IDs of region and line appearing in the PAGE file.
	 * 
	 * TODO implement mapping of name schema in both directions, e.g. for
	 * mapping the results back to entities
	 */
	public static String buildFileName(final String pageFileId, final int regIndex, final int lineIndex,
			final String regId, final String lineId, String fileExt) throws IllegalArgumentException {
		fileExt = fileExt.startsWith(".") ? fileExt : "." + fileExt;
		final String fileName = buildBaseFileName(pageFileId, regIndex, lineIndex, regId, lineId);
		// TODO check pattern
		return fileName + fileExt;
	}

	public static String buildBaseFileName(final String pageFileId, final int regIndex, final int lineIndex,
			final String regId, final String lineId) throws IllegalArgumentException {
		if (regId.contains(SEP) || lineId.contains(SEP)) {
			throw new IllegalArgumentException(
					"A regionID contains the illegal character \"" + SEP + "\":" + regId + " | " + lineId);
		}
		final String fileName = pageFileId + SEP + regIndex + SEP + lineIndex + SEP + regId + SEP + lineId;
		// TODO check pattern
		return fileName;
	}

	public static String readRecFile(File rec) throws IOException {
		final String fileText = DeaFileUtils.readFileAsString(rec);
		final String text = extractRecText(fileText);
		return text;
	}

	public static String readFeaGzFile(File rec) throws IOException {
		final String fileText = DeaFileUtils.readGzFileAsString(rec);
		final String text = extractRecText(fileText);
		return text;
	}

	// public static TrpWordgraph decodeFilename()

	private static String extractRecText(String fileText) {
		String[] lines = fileText.split("\n");
		Pattern p = Pattern.compile(REC_LINE_PATTERN);
		StringBuffer sb = new StringBuffer();
		for (String l : lines) {
			Matcher m = p.matcher(l);
			if (m.find()) {
				final String word = m.group(1);
				logger.debug("extracted word: " + word);
				sb.append(word + " ");
			} else {
				logger.error("found NO word in line: " + l);
			}
		}
		return sb.toString().trim();
	}

	// private static void test(){
	// final String n = "ULQSYKGJXPMXQQFUHRGLEPDT_2_12_r2_r136";
	// final String regex = HtrUtils.FILENAME_PATTERN;
	// Pattern p = Pattern.compile(regex);
	// Matcher m = p.matcher(n);
	//
	// if(m.find()){
	// String xmlKey = m.group(1);
	// String lineId = m.group(5);
	// logger.info("xmlKey = " + xmlKey + " | lineId = " + lineId);
	// } else {
	// logger.error("Filename is corrupt: " + n);
	// }
	//
	// }

	// public static ArrayList<> getNBestMatrix(String latticeToolOutput) {
	// String[] lines = latticeToolOutput.split("\n");
	// ArrayList<String[]> wordsList = new ArrayList<>();
	//
	// int maxWords = 0;
	// for(String l : lines) {
	// Matcher m = LATTICE_TOOL_LINE_PATTERN.matcher(l);
	// String transcript;
	// if(m.find()) {
	// transcript = m.group(1);
	// logger.debug("transcript: " + transcript);
	// } else {
	// logger.error("found NO words in line: " + l);
	// continue;
	// }
	// String[] words = transcript.split(" ");
	// if (words.length > maxWords)
	// maxWords = words.length;
	// wordsList.add(words);
	// }
	//
	//
	// for (String[] words : wordsList) {
	//
	//
	//
	//
	// }
	//
	//
	// }

	@Deprecated
	public static String[][] getnBestMatrix(String latticeToolOutput, boolean removeDuplicate) {
		List<String[]> matrix = new LinkedList<>();

		String[] lines = latticeToolOutput.split("\n");
		Pattern p = Pattern.compile(LATTICE_TOOL_LINE_PATTERN_STR);
		for (String l : lines) {
			Matcher m = p.matcher(l);
			String transcript;
			if (m.find()) {
				transcript = m.group(1);
				logger.debug("extracted transcript: " + transcript);
			} else {
				logger.error("found NO word in line: " + l);
				continue;
			}
			String[] words = transcript.split(" ");
			int dupl = 0;
			if (!matrix.isEmpty()) {
				// check occurrence of words..
				for (int i = 0; i < words.length; i++) {
					for (int j = 0; j < matrix.size(); j++) {
						String[] tmp = matrix.get(j);
						if (i < tmp.length) {
							if (!tmp[i].isEmpty() && tmp[i].equals(words[i]) && removeDuplicate) {
								logger.trace("Removing duplicate: " + words[i]);
								words[i] = "";
								dupl++;
							} else {
								if (!words[i].isEmpty())
									logger.trace("Keeping word: " + words[i]);
							}
						}
					}
				}
			}
			if (words.length > dupl) {
				matrix.add(words);
			}
		}
		return matrix.toArray(new String[matrix.size()][]);
	}

	public static String[][] getnBestMatrixUpvlc(String upvlcToolOutput, boolean removeDuplicate) {
		List<String[]> matrix = new LinkedList<>();

		String[] lines = upvlcToolOutput.split("\n");
		List<String> lineList = new ArrayList<>(Arrays.asList(lines));
		List<List<WordHypothesis>> hyp = parseUpvlcNBest(lineList);

		for (List<WordHypothesis> whs : hyp) {

			// copy words and remove leading <s> and trailing </s> !NULL
			String[] words = new String[whs.size() - 3];
			for (int k = 1; k < whs.size() - 2; k++) {
				words[k - 1] = whs.get(k).getWord();
			}

			int dupl = 0;
			if (!matrix.isEmpty()) {
				// check occurrence of words..
				for (int i = 0; i < words.length; i++) {
					for (int j = 0; j < matrix.size(); j++) {
						String[] tmp = matrix.get(j);
						if (i < tmp.length) {
							if (!tmp[i].isEmpty() && tmp[i].equals(words[i]) && removeDuplicate) {
								logger.trace("Removing duplicate: " + words[i]);
								words[i] = "";
								dupl++;
							} else if (words[i].equals("<s>") || words[i].equals("</s>") || words[i].equals("!NULL")) {
								logger.trace("Removing token: " + words[i]);
								words[i] = "";
							} else {
								if (!words[i].isEmpty()) {
									logger.trace("Keeping word: " + words[i]);
								}
							}
						}
					}
				}
			}
			if (words.length > dupl) {
				matrix.add(words);
			}
		}
		return matrix.toArray(new String[matrix.size()][]);
	}

	@Deprecated
	public static String getBestFromNBest(String latticeToolOutput) {
		String[] lines = latticeToolOutput.split("\n");
		Pattern p = Pattern.compile(LATTICE_TOOL_LINE_PATTERN_STR);
		for (String l : lines) {
			Matcher m = p.matcher(l);
			if (m.find()) {
				String transcript = m.group(1);
				return transcript;
			} else {
				logger.error("found NO word in line: " + l);
				continue;
			}
		}
		return null;
	}

	public static String getBestFromNBest(List<List<WordHypothesis>> hyps) {
		String text = "";
		if (hyps != null && !hyps.isEmpty()) {
			List<WordHypothesis> line = hyps.get(0);
			for (WordHypothesis w : line) {
				text += w.getWord() + " ";
			}
		}
		return text.replace("<s>", "").replace("</s>", "").replace("!NULL", "").trim();
	}

	public static List<List<WordHypothesis>> parseUpvlcNBest(List<String> stdOut) {

		final String wordPatternStr = "([0-9]+)\\s+([0-9]+)\\s+(.+)\\s+([0-9]*\\.[0-9]+)";
		Pattern wordPattern = Pattern.compile(wordPatternStr);
		final String seperatorStr = "///";
		List<List<WordHypothesis>> list = new LinkedList<>();
		List<WordHypothesis> wordList = new LinkedList<>();
		for (String s : stdOut) {
			if (s.startsWith("#") || s.startsWith("\"")) {
				// ignore comment or filename
				continue;
			}
			if (s.equals(seperatorStr) || s.equals(".")) {
				// end and begin new hypothesis
				// add current hypothesis
				list.add(wordList);
				// create new hypothesis
				wordList = new LinkedList<>();
				continue;
			}
			Matcher m = wordPattern.matcher(s);
			if (m.find()) {
				String tiStr = m.group(1);
				String tfStr = m.group(2);
				String word = m.group(3).trim();
				String confStr = m.group(4);
				// logger.debug(tiStr + tfStr + word + confStr);
				WordHypothesis hyp = new WordHypothesis(Integer.parseInt(tiStr), Integer.parseInt(tfStr), word,
						Double.parseDouble(confStr));
				wordList.add(hyp);
			} else {
				logger.warn("Unknown sequence: " + s);
			}
		}
		return list;
	}

	public static void main(String[] args) {
		Pattern fnPattern = Pattern.compile(HtrUtils.FILENAME_PATTERN);

		final String baseFileName2 = "ERKUAKDNNECUAQNOIOOGDEBE_3_2_region_1432283929907_16_line_1432284042556_28";
		final String baseFileName = "ERKUAKDNNECUAQNOIOOGDEBE.3.2.region_1432283929907-16.line-1432284042556_28";

		Matcher m = fnPattern.matcher(baseFileName);
		if (!m.find()) {
			logger.error("Filename does not match pattern: " + baseFileName);
		}
		final String lineId = m.group(5);
		logger.debug("Extracted lineId=" + lineId);
		// logger.info(extractText(text));
		// test();

	}

	public static List<HtrModel> getModelList() {
		File[] models = new File(HtrUtils.MODEL_PATH).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		List<HtrModel> modelList = new ArrayList<>(models.length);
		for (File model : models) {
			try {
				modelList.add(new HtrModel(model));
			} catch (IOException e) {
				logger.error("Could not load HTR model: " + model.getAbsolutePath(), e);
			}
		}
		logger.debug("Found " + modelList.size() + " HTR models.");
		return modelList;
	}

	public static String getModelListStr() {
		File[] models = new File(HtrUtils.MODEL_PATH).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

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

	public static File[] getNetList() {
		File[] models = new File(HtrUtils.NET_PATH).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".sprnn");
			}
		});
		return models;
	}

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
		File[] models = new File(HtrUtils.DICT_PATH).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && pathname.getName().endsWith(".dict");
			}
		});

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
		return HtrUtils.parseCitlabCerString(cerString);
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
		return (finalCerVal*100)+"%";
	}
}
