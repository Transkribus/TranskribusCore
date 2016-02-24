package eu.transkribus.core.model.beans.pagecontent_trp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaggedWord {
		public final static String TAGGING_START_CHAR  = "@";
		
		public final static String REGION_OPEN_BRACKET = "[";
		public final static String REGION_CLOSE_BRACKET = "]";
		
		public final static String REGION_OPEN_BRACKET_ESC = "\\"+REGION_OPEN_BRACKET;
		public final static String REGION_CLOSE_BRACKET_ESC = "\\"+REGION_CLOSE_BRACKET;
		
		public final static String REGION_REG_EX = 
				"^"+"("+TAGGING_START_CHAR+")"+"("+REGION_OPEN_BRACKET_ESC+".+"+REGION_CLOSE_BRACKET_ESC+")?"+"(.*)$";
		
		public final static Pattern REGION_PATTERN = Pattern.compile(REGION_REG_EX);
		
		int start=0;
		int length=0;
		
		TrpTextLineType line=null;
		String word="";
		
		String wordRegionId ="";
		TrpWordType wordRegion = null;
		Matcher matcher;
		
		public TaggedWord(TrpTextLineType line, int start, int length) throws Exception {
			this.start = start;
			this.length = length;
			this.line = line;
			
			this.word = line.getUnicodeText().substring(start, start+length);
			matcher = REGION_PATTERN.matcher(word);
			LineTags.logger.debug("word: "+word+" group count = "+matcher.groupCount() + " matches = "+matcher.matches());
			
			if (!matcher.matches() || matcher.groupCount()!=3) {
				throw new Exception("Word does not start with the tagging character "+TAGGING_START_CHAR);
			}
			
			tryParseRegion();
		}

		private void tryParseRegion() {
			wordRegionId = "";
			wordRegion = null;
			
//			Matcher m = REGION_PATTERN.matcher(word);
			LineTags.logger.debug("group count = "+matcher.groupCount() + " matches = "+matcher.matches());
			
//			if (matcher.matches() && matcher.groupCount()==3) {
				String wordRegionId = matcher.group(2);
//				logger.debug("wordRegionId = "+wordRegionId);
//				String wordItself = m.group(3);
//				logger.debug("wordItself = "+wordItself);
				if (wordRegionId != null) {
					// strip brackets from region id:
					wordRegionId = wordRegionId.replaceAll(REGION_OPEN_BRACKET_ESC, "").replaceAll(REGION_CLOSE_BRACKET_ESC,"");
					LineTags.logger.debug("wordRegionId stripped = "+wordRegionId);
					
					wordRegion = line.getWordWithId(wordRegionId);
				}
				LineTags.logger.debug("wordRegion = "+wordRegion);
//			}
		}
		
		public void assignRegionToWord(TrpWordType region) {			
//			String newWord = "";
//			if (m.matches() && m.groupCount()==3) {
			
//			newWord = TAGGING_START_CHAR+REGION_OPEN_BRACKET+region.getId()+REGION_CLOSE_BRACKET+wordItself;
			String newWord = matcher.replaceAll("$1"+REGION_OPEN_BRACKET+region.getId()+REGION_CLOSE_BRACKET+"$3");
							
				LineTags.logger.debug("oldWord / newWord = "+word + "/"+ newWord);
				
				String oldText = line.getUnicodeText();
				String newText = oldText.substring(0, start) + newWord + oldText.substring(start+length);
				
				
				word = newWord;
				length = newWord.length();
				matcher = REGION_PATTERN.matcher(word);
				tryParseRegion();
				
				line.setUnicodeText(newText, this);				
//			}
		}

		public int getStart() {
			return start;
		}

		public int getLength() {
			return length;
		}

		public TrpTextLineType getLine() {
			return line;
		}

		public String getWord() {
			return word;
		}
		
		public String getWordItself() {
			return matcher.group(3);
		}

//		public String getWordRegionId() {
//			return wordRegionId;
//		}
//
		public TrpWordType getWordRegion() {
			return wordRegion;
		}
		
		public String getParentRegionId() throws IllegalStateException {
			String id = null;
			if(wordRegion != null){
				id = wordRegion.getId();
			} else if (line != null){
				id = line.getId();
			} else {
				throw new IllegalStateException("No parent region found for tag: " + word);
			}
			return id;
		}

		public String toString() {
		    final String TAB = "    ";
		    String retValue = "";
		    retValue = "TaggedWord ( "
		        + super.toString() + TAB
		        + "start = " + this.start + TAB
		        + "length = " + this.length + TAB
		        + "line = " + this.line + TAB
		        + "word = " + this.word + TAB
		        + "wordRegionId = " + this.wordRegionId + TAB
		        + "wordRegion = " + this.wordRegion + TAB
		        + " )";
		
		    return retValue;
		}
		

	}