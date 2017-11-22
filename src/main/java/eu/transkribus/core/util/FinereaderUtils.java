package eu.transkribus.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.io.UnsupportedFormatException;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpElementCoordinatesComparator;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;

public class FinereaderUtils {
	private static final Logger logger = LoggerFactory.getLogger(FinereaderUtils.class);
	private static Map<String, String> repl;
	private static Map<String, String> eolRepl;
	private static Map<String, String> regexRepl;
	
	static{
		//FIXME different rules for the same char at EOL and anywhere are not possible!
		repl = new HashMap<>();
//		repl.put("\u00BB", "\u201E"); // »: "\u00BB" -> „ U+201E
//		repl.put("\u00AB", "\u201C"); // «: "\u00AB" -> “ U+201C
		
		repl.put("\u00BB", "\u201E"); // »: "\u00BB" -> „ U+201E
		repl.put("\u00AB", "\u201C"); // «: "\u00AB" -> “ U+201C
		eolRepl = new HashMap<>();
		
		// At line end:
//		eolRepl.put("-", "\u00AD"); // "-" -> Soft hyphen: "\u00AD"
//		eolRepl.put("\u00AC", "\u00AD"); // ¬: "NOT" "\u00AC" -> Soft hyphen: "\u00AD"
		
		eolRepl.put("-", "\u00AC"); // "-" -> ¬ "NOT" "\u00AC"
		
		regexRepl = buildRegexReplMap(repl, eolRepl);
	}
	
	/*
	 * EU languages:
	 *  	
    Bulgarian
    Croatian
    Czech
    Danish
    Dutch
    English
    Estonian
    Finnish
    French
    German
    Greek
    Hungarian
    Irish
    Italian
    Latvian
    Lithuanian
    Maltese
    Polish
    Portuguese
    Romanian
    Slovak
    Slovenian
    Spanish
    Swedish
    
    semi official:
    Basque
    Catalan
    Galician
    Scottish Gaelic
    Welsh


	 */
	public final static String[] FINEREADER_LANGUAGES = new String[] {
		//because we have no license for Arabic
	
		//"Arabic",
		"Bulgarian",
		"Croatian",
		"Czech",
		"Danish",
		"Dutch",
		"English",
		"Estonian",
		"Finnish",
		"French",
		"German",
		"Greek",
		"Hungarian",
		"Irish",
		"Italian",
		"Latvian",
	    "Lithuanian",
	    "Maltese",
		"Polish",
		"PortugueseBrazilian",
		"PortugueseStandard",
		"Romanian",
		"Russian",
		"SerbianCyrillic",
		"SerbianLatin",
	    "Slovak",
	    "Slovenian",
		"Spanish",
		"Swedish",
		"Turkish",
		"OldEnglish",
		"OldFrench",
		"OldGerman",
		"OldItalian",
		"LatvianGothic",
		"RussianOldSpelling",
		"Ukrainian",
		"Yiddish",
		"Latin",	
		"Basque",
		"Catalan",
		"GaelicScottish",
		"Galician",
		"Welsh",
	};
	
	public final static String[] FINEREADER_LANGUAGES_NOT_LICENSED = new String[] {
		//no license for Arabic and Hebrew, Ladino and Judeo-Arabic not in Abbyy languages
		"Arabic",
		"Hebrew",
		"Ladino",
		"Judeo-Arabic",
	};
	
	public final static ArrayList<String> ALL_LANGUAGES = new ArrayList<String>(){{
		addAll(Arrays.asList(FINEREADER_LANGUAGES));
		addAll(Arrays.asList(FINEREADER_LANGUAGES_NOT_LICENSED));
	}};
	
	
	static boolean isFinreaderLanguage(String l) {
		return getLanguageIndex(l) != -1;
	}
	
	public static int getLanguageIndex(String l) {
		for (int i=0; i<FINEREADER_LANGUAGES.length; ++i) {
			if (FINEREADER_LANGUAGES[i].equals(l))
				return i;
		}
		return -1;
	}
	
	
	/** 
	 * Method for replacing certain systematic errors in OCR Text.
	 * Replacements are done with regexes from private static regexReglMap (see above in this class). 
	 * 
	 * TODO add parameters to pass custom maps from a search/replace dialog!?
	 * @param pc
	 * @return
	 */
	public static PcGtsType replaceBadChars(PcGtsType pc) {
		
		List<TrpRegionType> regs = pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
		boolean success = true;
		for(RegionType r : regs){
			
			if(!isTextRegion(r)){
				continue;
			}

			TextRegionType tr = (TextRegionType)r;
			if(tr.getTextEquiv() == null && tr.getTextEquiv().getUnicode() == null){
				//no text at all
				continue;
			}
			

			final String textblockBefore = tr.getTextEquiv().getUnicode();
			final String textblockAfter =  replaceChars(textblockBefore, regexRepl);
			
			//iterate lines
			List<TextLineType> lines = tr.getTextLine();
			if(lines == null || lines.isEmpty()){
				//no lines. FIXME if lines are there but without unicode in it!?
//					textblockAfter = replaceChars(textblockBefore, regexRepl);
				continue;
			}
			//setRegionText
			tr.getTextEquiv().setUnicode(textblockAfter);

			StringBuffer linesBefore = new StringBuffer();
			StringBuffer linesAfter = new StringBuffer();
			//DEBUG END
			boolean isFirstLine = true;
			for(TextLineType l : lines) {
				if(l.getTextEquiv() == null && l.getTextEquiv().getUnicode() == null){
					//empty line
					continue;
				}
				
				//Build the textRegion for later use
				final String textlineBefore = l.getTextEquiv().getUnicode();
				final String textlineAfter = replaceChars(textlineBefore, regexRepl);
				linesBefore.append(isFirstLine ? textlineBefore : "\n"+textlineBefore);
				linesAfter.append(isFirstLine ? textlineAfter : "\n"+textlineAfter);
				if(isFirstLine) isFirstLine = false;
				
				l.getTextEquiv().setUnicode(textlineAfter);
				
				//iterate words
				List<WordType> words = l.getWord();
				if(words == null || words.isEmpty()){
					// replace stuff in lines
					//no words. FIXME if words are there but without unicode in it!?
					continue; //with next line
				}
				
				boolean isFirstWord = true;
				StringBuffer wordsBefore = new StringBuffer();
				StringBuffer wordsAfter = new StringBuffer();
				for(int i = 0; i < words.size(); i++){
					WordType w = words.get(i);
					if(w.getTextEquiv() == null || w.getTextEquiv().getUnicode() == null){
						continue;
					}
					final String wordText = w.getTextEquiv().getUnicode();
					final String wordTextAfter;				
					if(i < words.size()-1){
						//use general replacement map for all words
						wordTextAfter = replaceChars(wordText, repl);
					} else {
						//use regex map for EOL words
						wordTextAfter = replaceChars(wordText, regexRepl);
					}
					//DEBUG
					wordsBefore.append(isFirstWord ? wordText : " "+wordText);
					wordsAfter.append(isFirstWord ? wordTextAfter : " "+wordTextAfter);
					if(isFirstWord) isFirstWord = false;
					//DEBUG END
					
					w.getTextEquiv().setUnicode(wordTextAfter);
				}
				
				boolean lineSuccess = textlineBefore.toString().replace(" ", "").equals(wordsBefore.toString().replace(" ", ""));				
				lineSuccess &= textlineAfter.toString().replace(" ", "").equals(wordsAfter.toString().replace(" ", ""));

				if(!lineSuccess) {
					logger.debug("Line before: " + textlineBefore.toString());
					logger.debug("Words before : " + wordsBefore.toString());
					logger.debug("Line after: " + textlineAfter.toString());
					logger.debug("Words after : " + wordsAfter.toString());
				}
				success &= lineSuccess;
				//TODO propagate words -> lines -> regions
			}
			
			boolean regionSuccess = textblockBefore.replace(" ", "").equals(linesBefore.toString().replace(" ", ""));
			regionSuccess &= textblockAfter.replace(" ", "").equals(linesAfter.toString().replace(" ", ""));
			if(!regionSuccess) {
				logger.debug("\nblock:\n");
				logger.debug(textblockAfter);
				logger.debug("\nblock from lines:\n");
				logger.debug(linesAfter.toString());
			}
			success &= regionSuccess;

		}
		
		logger.info("Bad character replacement: " + (success ? "SUCCESS" : "FAILURE"));
//		if(!success) throw new IllegalArgumentException();
		return pc;
	}

	private static Map<String, String> buildRegexReplMap(Map<String, String> replacements,
			Map<String, String> eolReplacements) {
		Map<String, String> regexRepl  = new HashMap<>();
		//general replacements as they are
		regexRepl.putAll(replacements);
		
//		for(Entry<String, String> e : replacements.entrySet()){
//			//new line case
//			regexRepl.put(".*("+e.getKey()+").+", e.getValue());
//		}
//		
		
		for(Entry<String, String> e : eolReplacements.entrySet()){
			//new line case
			regexRepl.put(e.getKey()+"\n", e.getValue()+"\n");
			//end of block case
			regexRepl.put(e.getKey()+"$", e.getValue());
		}
		return regexRepl;
	}

	private static String replaceChars(String textblock, Map<String, String> regexRepl) {
		for(Entry<String, String> e : regexRepl.entrySet()){
			final String regex = e.getKey();
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(textblock);
			if(m.find()){
				logger.debug("FOUND: \"" + regex.replace("\n", "\\n") + "\"");
				textblock = textblock.replaceAll(regex, e.getValue());
			}
		}
		return textblock;
	}

	private static boolean isTextRegion(RegionType r) {
		return r instanceof TextRegionType || r instanceof TrpTextRegionType;
	}
	
	public static void addTextStyleToWords(TrpDoc doc) throws JAXBException, FileNotFoundException{
	/*
	 * Ich hab im folgenden Ordner das Buch abgelegt, wo die Sprecherangaben automatisiert als „letter-spaced“ zu markieren wären (sofern sich das mit angemessenem Aufwand bewältigen lässt):
		ftp://ftp.uibk.ac.at/private/x3061015_20140902_78e054475d7532953c204ce6d392d8e9/Andy_Barbara_Bettina/zu_bearbeiten/
		dabei handelt es sich um folgende Namen, sofern sie am Zeilenanfang stehen:

		Ernst
		Albrecht
		Preising
		Marschall
		Pappenheim
		Pienzenau
		Bern
		Törring
		Nothafft von Wernberg
		Frauenhoven
		Hans von Läubelfing
		Caspar Bernauer
		Agnes
		Theobald
		Knippeldollinger
		Bürgermeister
		Barbara
		Martha
		Stachus
		Der Kastellan
		Herold
		Legat
		
		FIXME Der Herold
		FIXME Der Legat

	 */
		String[] names = {
			"Ernst",
			"Albrecht",
			"Preising",
			"Marschall",
			"Pappenheim",
			"Pienzenau",
			"Bern",
			"Törring",
			"Nothafft von Wernberg",
			"Frauenhoven",
			"Hans von Läubelfing",
			"Caspar Bernauer",
			"Agnes",
			"Theobald",
			"Knippeldollinger",
			"Bürgermeister",
			"Barbara",
			"Martha",
			"Stachus",
			"Der Kastellan",
			"Der Herold",
			"Der Legat"
		};
		
		List<String[]> nameList = new ArrayList<>(names.length);
		List<String> nameStartList = new ArrayList<>(names.length);
//		List<String> nameSet = new ArrayLilst<>();
		String[] tmp;
		for(int i = 0; i < names.length; i++){
			String s = names[i];
			tmp = s.split(" ");
			String tmpStr = "{ ";
			for(String t : tmp){
				tmpStr += t + "|";
			}
			System.out.println(i + "\t- splitting: " + tmpStr + "}");
			nameList.add(i, tmp);
			nameStartList.add(tmp[0]);
		}
		
		TrpElementCoordinatesComparator<WordType> wordComp = new TrpElementCoordinatesComparator<WordType>();
		for(TrpPage p : doc.getPages()){
			System.out.println("Processing page: " + p.getPageNr());
			URL url = p.getCurrentTranscript().getUrl();
			final String xmlPath = FileUtils.toFile(url).getAbsolutePath();
			File xmlFile = new File(xmlPath);
			PcGtsType pc = JaxbUtils.unmarshal(xmlFile, PcGtsType.class);
			List<TextRegionType> regions = PageXmlUtils.getTextRegions(pc);
			for(TextRegionType r : regions){
//				System.out.println("Processing region: " + r.getId());
				List<Integer> candidatesIndex;
				int i;
				for(TextLineType l : r.getTextLine()){
					candidatesIndex = new LinkedList<>();
					i = 0;
//					System.out.println("Processing line: " + l.getId());
					List<WordType> words = l.getWord();
					if(words != null && !words.isEmpty()){
						Collections.sort(words, wordComp);
						//read first word and iterate to second
						WordType w1 = words.get(i);
//						List<Integer> candidates = new LinkedList<>();
						for(int j = 0; j < nameStartList.size(); j++){
							String e = nameStartList.get(j);
							if(w1.getTextEquiv() != null && w1.getTextEquiv().getUnicode() != null 
									&& isMatch(w1.getTextEquiv().getUnicode(), e)){
								candidatesIndex.add(j);
//								System.out.println("Found candidate word: " + j + " - " + w1.getTextEquiv().getUnicode());
							}
						}
						
						if(!candidatesIndex.isEmpty()){
							for(Integer index : candidatesIndex){
								String[] name = nameList.get(index);
								if(name.length == 1){
									//Done.
									w1.getTextStyle().setLetterSpaced(true);
									System.out.println("OK: " + name[i]);
									break;
								} else {
									List<WordType> wordList = new ArrayList<>(name.length);
									boolean isName = true;
									wordList.add(w1);
									String nameStr = w1.getTextEquiv().getUnicode() + " ";
									//check subsequent words
									for(i = 1; i < name.length; i++){
										WordType wi = words.get(i);
										if(isMatch(wi.getTextEquiv().getUnicode(), name[i])) {
											nameStr += wi.getTextEquiv().getUnicode() + " ";
											wordList.add(wi);
										} else {
											System.out.println("NEGATIVE: " + nameStr + words.get(i).getTextEquiv().getUnicode() + " != " + name[i]);
											isName = false;
											break;
										}
									}
									
									if(isName){
										System.out.println("OK : " + nameStr);
										for(WordType w : wordList){
	//										System.out.println(w.getTextEquiv().getUnicode());
											w.getTextStyle().setLetterSpaced(true);
										}
										break;
									}
								}
							}
						}
					}					
				}
			}
			//TODO store pageXML
			JaxbUtils.marshalToFile(pc, xmlFile);
		}
	}
	
	private static boolean isMatch(String s1, String s2){
		return s1.equals(s2)
				|| s1.equals(s2 + ".")
				|| s1.equals(s2 + ",");
	}

	public static void main(String[] args) throws JAXBException, UnsupportedFormatException, IOException{

//		File f = new File("/mnt/dea_scratch/TRP/sample_buch_done/page/bsb00087391_00009.xml");
//		
//		PcGtsType pc = PageXmlUtils.unmarshal(f);
//		try{
//			pc = replaceBadChars(pc);
//		} catch (IllegalArgumentException e){
//			logger.debug(f.getName());
//		}
		
		TrpDoc doc = LocalDocReader.load("/mnt/dea_scratch/tmp_philip/Transidee/bsb00087908_hebbel_bernauer", false);
		addTextStyleToWords(doc);
		
		
//		final String dirPath = "/mnt/dea_scratch/TRP/sample_buch_done/page";
//		
//		File dir = new File(dirPath);
//		File[] files = dir.listFiles(new FilenameFilter(){
//			@Override
//			public boolean accept(File dir, String name) {
//				return name.endsWith("xml");
//			}
//		});
//		
//		for(File f : files){
//			PcGtsType pc = PageXmlUtils.unmarshal(f);
//			try{
//				replaceBadChars(pc);
//			} catch (IllegalArgumentException e){
//				logger.debug(f.getName());
//			}
//		}
//		String softHyphen = "\u00AD";
		
//		System.out.println( " ---" + softHyphen + "--- ");
	}
}
