package eu.transkribus.core.model.beans.customtags;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.customtags.CssSyntaxTag;

public class CssSyntaxTagTest {
	private final static Logger logger = LoggerFactory.getLogger(CssSyntaxTagTest.class);
		
	String TAG_FILENAME = "./cssTagText.txt";
	String OUT_FN = "cssTagOut.txt";
	
	public static String readFileAsString(String fn) throws IOException {
		InputStream stream = Thread.currentThread().getContextClassLoader()
			    .getResourceAsStream(fn);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		String css="";
		
		String line;
		while ((line = in.readLine()) != null) {
			css += line+"\n";
		}
		
		in.close();
		
		return css;
	}
	
	@Test public void testReadWriteAndReadAgainEqualityOfTags() throws Exception {
		String css = readFileAsString(TAG_FILENAME);
	
		// 1st step: read tags from input file
		List<CssSyntaxTag> tags = CssSyntaxTag.parseTags(css);
		
		String outTxt = "";
		for (CssSyntaxTag t : tags) {
			logger.info("found tag: "+t);
			
			outTxt += t.getCssString()+" ";
		}
		
		// 2nd step: write the to an output file
		PrintWriter out = new PrintWriter("src/test/resources/"+OUT_FN);
		out.println(outTxt);
		out.flush();
		out.close();
		
		// 3rd step: read them again from the output file just written and check if tag names and attributes are equal!
//		String css2 = readFileAsString(OUT_FN);
		List<CssSyntaxTag> tags2 = CssSyntaxTag.parseTags(outTxt);

		Assert.assertEquals("Number of tags must be equal!", tags.size(), tags2.size());
		
		for (int i=0; i<tags.size(); ++i) {
			CssSyntaxTag t1 = tags.get(i);
			CssSyntaxTag t2 = tags.get(i);
			
			logger.info("tag 1 = "+t1);
			logger.info("tag 2 = "+t2);
			
			Assert.assertEquals("Tag names are not the same!", t1.getTagName(), t2.getTagName());
			Assert.assertEquals("Number of attributes are not equal!", t1.getAttributes().size(), t2.getAttributes().size());
			
			for (String attName : t1.getAttributeNames()) {
				String v1 = (String) t1.getAttributeValue(attName);
				String v2 = (String) t2.getAttributeValue(attName);
				
				Assert.assertNotNull("No attribute with name = "+attName+" in t2!", v2);
				
				Assert.assertEquals("Tag attributes are not the same!", v1, v2);	
				
			}
			
			
		}
		
//		Files.write(Paths.get(OUT_FN), outTxt);
	}

	@Test public void testParseCssTagString() throws IOException {
		String css = readFileAsString(TAG_FILENAME);
		logger.info("css tag string:\n"+css);
		
		List<CssSyntaxTag> tags = CssSyntaxTag.parseTags(css);
		for (CssSyntaxTag t : tags) {
			logger.info("found tag: "+t);
		}
		logger.info("nr of tags found: "+tags.size());
		
		Assert.assertEquals("Number of parsed tags must be 3!", 3, tags.size());
		
		for (CssSyntaxTag t : tags) {
			Assert.assertEquals("Number of attributes must be 2 for tag: "+t, 2, t.getAttributes().size()); 
		}

//		fail("Not yet implemented");
	}
	
	public static void main(String[] args) throws Exception {
//		StructureTag st = new StructureTag("paragraph, marginalia");
		
		CssSyntaxTag t = CssSyntaxTag.parseSingleCssTag("structure { type: paragraph, marginalia; }");
//		logger.info(t.toString());
		
		System.out.println(t.toString());
		System.out.println(t.getAttributeValue("type"));
		
		
		
	}


}
