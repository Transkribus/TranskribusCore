package eu.transkribus.core.util.xpath;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class TrpXPathProcessorTest {
	private static final Logger logger = LoggerFactory.getLogger(TrpXPathProcessorTest.class);
	
	private final static String docPath = "TrpTestDoc_20131209/StAZ-Sign.2-1_001.xml";
	
	@Test
	public void testXPath() {
		final String lineId = "r28";
		final String docPath = "TrpTestDoc_20131209/StAZ-Sign.2-1_001.xml";
		TrpXPathProcessor proc;
		try {
			proc = new TrpXPathProcessor();
		} catch (XPathFactoryConfigurationException | ParserConfigurationException e) {
			Assert.fail("Could not initiate XPathProcessor: " + e.getMessage());
			return;
		}
		URL url = this.getClass().getClassLoader().getResource(docPath);
		final String xPath = "//TextLine[@id='" + lineId + "']";
		XPathExpression exp;
		try {
			exp = proc.compile(xPath);
		} catch (XPathExpressionException e) {
			Assert.fail("Could not compile xPath: " + e.getMessage());
			return;
		}
		Node node;
		try {
			node = proc.getNode(url, exp);
		} catch (XPathExpressionException | SAXException | IOException e) {
			Assert.fail("Could not evaluate xPath: " + e.getMessage());
			return;
		}
		Assert.assertEquals("Element name does not match!", "TextLine", node.getNodeName());
		final String resultId = node.getAttributes().getNamedItem("id").getTextContent();
		Assert.assertEquals("Element ID does not match!", lineId, resultId);
	}
	
	@Test
	public void testNonWorkingXPath() {
		final String lineId = "esGibtSicherKeineLineDieDieseIdHat";
		
		TrpXPathProcessor proc;
		try {
			proc = new TrpXPathProcessor();
		} catch (XPathFactoryConfigurationException | ParserConfigurationException e) {
			Assert.fail("Could not initiate XPathProcessor: " + e.getMessage());
			return;
		}
		URL url = this.getClass().getClassLoader().getResource(docPath);
		final String xPath = "//TextLine[@id='" + lineId + "']";
		XPathExpression exp;
		try {
			exp = proc.compile(xPath);
		} catch (XPathExpressionException e) {
			Assert.fail("Could not compile xPath: " + e.getMessage());
			return;
		}
		Node node;
		try {
			node = proc.getNode(url, exp);
		} catch (XPathExpressionException | SAXException | IOException e) {
			Assert.fail("Could not evaluate xPath: " + e.getMessage());
			return;
		}
		Assert.assertNull(node);
	}
}
