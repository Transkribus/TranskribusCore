package eu.transkribus.core.model.beans.customtags;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class CustomTagConverterTest {
	
//	private final static String docPath = "src/test/resources/TrpTestDoc_20131209/StAZ-Sign.2-1_001.xml";
	private final static String docPath = "src/test/resources/many_tags.xml";

	@Test
	public void testSth() throws SAXException, IOException, TransformerException, ParserConfigurationException {
//		fail("Not yet implemented");
		CustomTagConverter c = new CustomTagConverter();
		c.createExplicitTagFile(docPath);
		
		
	}
	
	public static void main(String[] args) throws Exception {
		new CustomTagConverterTest().testSth();
		
	}

}
