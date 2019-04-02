package eu.transkribus.core.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class PageXmlProcessorTest {
	
	private static final Logger logger = LoggerFactory.getLogger(PageXmlProcessorTest.class);
	SebisStopWatch ssw = new SebisStopWatch();
	
//	@Test
//	public void testGetAllRegionsIds() throws XPathFactoryConfigurationException, ParserConfigurationException, MalformedURLException, IllegalArgumentException, XPathExpressionException, SAXException, IOException {
//		final String key = "CMTSKWAFFAQPTGSHECQTKDCM";
//		PageXmlProcessor proc = PageXmlProcessorFactory.newInstance();
//		List<String> ids = proc.getAllTextRegionIds(key);
//		ids.stream().forEach(id -> logger.debug(id));
//	}
//	
	@Test
	public void testGetAllLines() {
		final String keyGT = "CMTSKWAFFAQPTGSHECQTKDCM";
		final String keyHyp = "MXDDRBWLXBQMEVAGMGDQMJJS";
		PageXmlProcessor proc = null;
		List<String> idsGT = null;
		List<String> idsHyp = null;
		ssw.start();
		try {
			proc = PageXmlProcessorFactory.newInstance();
		} catch (XPathFactoryConfigurationException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		try {
			idsGT = proc.getAllLineIds(keyGT);
			idsHyp = proc.getAllLineIds(keyHyp);
		} catch (IllegalArgumentException | XPathExpressionException | SAXException | IOException e) {	
			e.printStackTrace();
		}
		ssw.stop(true);
		logger.debug("GT Id size : "+idsGT.size());
		logger.debug("Hyp Id size : "+idsHyp.size());
		
		ssw.start();
		if(idsGT.size() != idsHyp.size()) {
			logger.debug("Cannot compare document with diffrent Layout Analysis");
		}
		ssw.stop(true);
//		for(String s : idsGT) {
//			logger.debug("Line id : "+s);
//		}
		String a = idsGT.parallelStream().sorted().collect(Collectors.joining(""));
		String b = idsHyp.parallelStream().sorted().collect(Collectors.joining(""));
		a.equals(b);
		logger.debug(a);
		logger.debug(b);
		
	}
}
