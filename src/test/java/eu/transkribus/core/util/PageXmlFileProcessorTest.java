package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PageXmlFileProcessorTest {
	private static final Logger logger = LoggerFactory.getLogger(PageXmlFileProcessorTest.class);
	
//	@Test
	public void testLoadingFromUrl() throws Exception {
		SebisStopWatch sw = new SebisStopWatch();
		String url = "https://files.transkribus.eu/Get?id=AVHESCOEDXZYMYNPBOPGMEAR";
		
		sw.start();
		PageXmlFileProcessor fp = new PageXmlFileProcessor(url);
		Document doc = fp.getDocument();
		sw.stop(true, "time for loading doc: ");
		
		sw.start();
		NodeList nl = fp.getTextRegionCoordsPoints(doc);
		sw.stop(true, "time for loading coords: ");
		logger.info("N-coords = "+nl.getLength());
		
//		NodeList nl = p.getTextRegionCoordsPoints(doc);
		for (int i=0; i<nl.getLength(); ++i) {
			Node n = nl.item(i);
			
			logger.info("coords = "+n.getNodeValue());
		}
	}

//	@Test
	public void testLoadingAndSavingFile() throws MalformedURLException, IllegalArgumentException, SAXException, IOException, XPathFactoryConfigurationException, ParserConfigurationException, XPathExpressionException, TransformerFactoryConfigurationError, TransformerException {
		SebisStopWatch sw = new SebisStopWatch();
		
		sw.start();
		PageXmlFileProcessor fp = new PageXmlFileProcessor("C:\\tmp\\impact_test_page.xml");
		Document doc = fp.getDocument();
		sw.stop(true, "time for loading doc: ");
		sw.start();
		NodeList nl = fp.getTextLineUnicodes(doc);
		sw.stop(true, "time for loading lines: ");
		
//		sw.start();
//		PcGtsType pc = PageXmlUtils.unmarshal(new File("C:\\tmp\\impact_test_page.xml"));
//		((TrpPageType)pc.getPage()).getLines();
//		sw.stop(true, "time for marshalling file : ");
		
		sw.start();
		logger.debug("got nodes = "+nl.getLength());
		for (int i=0; i<nl.getLength(); ++i) {
			Node n = nl.item(i);
			logger.debug("textline node = "+n+" val = "+n.getTextContent());
			n.setTextContent("test_string_"+i);
		}
		sw.stop(true, "time for setting string content: ");
		
		sw.start();
		fp.writeToFile(doc, new File("C:\\tmp\\impact_test_page_xpath_out.xml"), true);
		sw.stop(true, "time for saving: ");	
	}
	
	public static void main(String[] args) throws Exception {
		PageXmlFileProcessorTest t = new PageXmlFileProcessorTest();
		t.testLoadingAndSavingFile();
		t.testLoadingFromUrl();
	}

}
