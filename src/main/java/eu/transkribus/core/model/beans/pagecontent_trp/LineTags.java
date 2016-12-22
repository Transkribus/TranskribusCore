package eu.transkribus.core.model.beans.pagecontent_trp;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LineTags {
	static Logger logger = LoggerFactory.getLogger(LineTags.class);
	
	public static List<TaggedWord> getTaggedWords(TrpTextLineType line) {
		List<TaggedWord> tagWords = new ArrayList<>();
		
		int start=0;
		
		for (String s : line.getUnicodeText().split(" ")) {
			Matcher m = TaggedWord.REGION_PATTERN.matcher(s);
			
			start = line.getUnicodeText().indexOf(s, start);
			
			if (m.matches()) {
				try {
					TaggedWord tw = new TaggedWord(line, start, s.length());
					tagWords.add(tw);
				}
				catch (Exception e) {
					logger.debug("Could not parse a tagged word: "+s+": "+e.getMessage(), e);
				}
			}
		}
		return tagWords;
	}
		
	public static void main(String [] args){		
		String [] strings = new String[] {
				"@[asdfasdf]hellllllllo",
				"@[a]lllllllo",
				"@nurdaswort",
				"nix"
		};
		
//		Pattern.quote("[");
		System.out.println("quoted: "+Pattern.quote("["));
		System.out.println("quoted: "+"[");
		
		String regex = TaggedWord.REGION_REG_EX;
		Pattern p = Pattern.compile(regex);
		
		
		for (String s : strings) {
			Matcher m = p.matcher(s);
			System.out.println(s+": "+m.matches()+ " nr of groups = "+m.groupCount());
			if (m.matches()) {
				for (int i=1; i<=m.groupCount(); ++i) {
					String g = m.group(i);
					System.out.println("group "+i+": "+g);
				}
				

			}
			
			
			
		}
		
		
	}
	
	

}
