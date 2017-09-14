package eu.transkribus.core.util.xpath;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.transkribus.core.util.SebisStopWatch;

/**
 * Utility class to parse a PAGE file using XPath
 * @deprecated not used or tested
 */
public class XPathPage {
	
	private final static Logger logger = LoggerFactory.getLogger(XPathPage.class);
	
	public static enum TranscriptionLevel {
		TEXT_REGION, LINE, WORD;
	}
	
	DocumentBuilderFactory docBuilderFactory;
	XPathFactory xpathfactory;
	XPath xpath;
	
	final XPathExpression getAllUnicodeRegionLevelExpr;
	final XPathExpression getAllUnicodeLineLevelExpr;
	final XPathExpression getAllUnicodeWordLevelExpr;

	private static XPathPage instance;
	
	public static XPathPage getInstance() {
		if (instance == null) {
			instance = new XPathPage();
		}
		return instance;
	}
	
	private XPathPage() {
		ClassLoader cl = XPathPage.class.getClassLoader();
		String docBuilderFactoryImpl = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
		String xpathfactoryImpl = "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl";
		
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		docBuilderFactory = DocumentBuilderFactory.newInstance(docBuilderFactoryImpl, cl);

		// init expressions:
		try {
			xpathfactory = XPathFactory.newInstance(javax.xml.xpath.XPathFactory.DEFAULT_OBJECT_MODEL_URI, xpathfactoryImpl, cl);
			xpath = xpathfactory.newXPath();
			
			getAllUnicodeRegionLevelExpr = xpath.compile("//TextRegion/TextEquiv/Unicode");
			getAllUnicodeLineLevelExpr = xpath.compile("//TextLine/TextEquiv/Unicode");
			getAllUnicodeWordLevelExpr = xpath.compile("//Word/TextEquiv/Unicode");
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		} catch (XPathFactoryConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getUnicodeText(URL xmlFile, TranscriptionLevel level, String id) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		logger.debug("xmlFile = "+xmlFile);
		
		if (level == null) // default level is line level
			level = TranscriptionLevel.LINE;

		Document doc = XPathUtils.parseDocument(docBuilderFactory, xmlFile);

//		xpath.setNamespaceContext(new HardcodedPageNamespaceResolver()); // hardcoded namespaces
		xpath.setNamespaceContext(new UniversalNamespaceResolver(doc)); // more secure... takes just some ms more...
		
		if (id == null) {
			XPathExpression expr = null;
			switch (level) {
			case TEXT_REGION:
				expr = getAllUnicodeRegionLevelExpr;
				break;
			case LINE:
				expr = getAllUnicodeLineLevelExpr;
				break;
			case WORD:
				expr = getAllUnicodeWordLevelExpr;
				break;
			default:
				expr = getAllUnicodeLineLevelExpr;
				break;
			}
			
			NodeList list = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			logger.debug("nr of nodes = "+list.getLength());

			return XPathUtils.getNodeListText(list);
		} else {
			String idStrExprStr = "//*[@id='"+id+"'][1]";
			
			logger.debug("exprStr = "+idStrExprStr);
			XPathExpression expr = xpath.compile(idStrExprStr);
			
			NodeList list= (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			if (list.getLength() == 1) {
//				Node node = list.item(0);
//				String name = list.item(0).getLocalName();
				
				NodeList l2 = null;
				if (level == TranscriptionLevel.TEXT_REGION) { // assume that id was on region level too...
					l2 = (NodeList) xpath.compile(idStrExprStr+"/TextEquiv/Unicode").evaluate(doc, XPathConstants.NODESET);
				}
				else if (level == TranscriptionLevel.LINE) {
					l2 = (NodeList) xpath.compile(idStrExprStr+"//TextLine/TextEquiv/Unicode").evaluate(doc, XPathConstants.NODESET);
				}
				else if (level == TranscriptionLevel.WORD) {
					l2 = (NodeList) xpath.compile(idStrExprStr+"//Word/TextEquiv/Unicode").evaluate(doc, XPathConstants.NODESET);
				}
				
				return XPathUtils.getNodeListText(l2);
			}
			
			return "";
		}
	}
	
	
	
	public static void main(String[] args) throws Exception{
		
//		File f = new File("/home/sebastianc/Downloads/035_320_001.xml");
		SebisStopWatch ss = new SebisStopWatch();
		
//		ss.start();
//		PageXmlUtils.unmarshal(new File("/home/sebastianc/Downloads/035_320_001.xml"));
//		ss.stop(true);
		
		XPathPage utils = XPathPage.getInstance();
		
//		String id = "r188";
		String id = null;
		TranscriptionLevel level = TranscriptionLevel.LINE;
		
		ss.start();
		String txt = utils.getUnicodeText(new File("/home/sebastianc/Downloads/035_320_001.xml").toURI().toURL(), level, id);
		ss.stop(true);
		
		System.out.println("txt = "+txt);
		
		if (true)
			return;
		
		ClassLoader cl = XPathPage.class.getClassLoader();
//		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl", cl);
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		

//		domFactory.setNamespaceAware(true);
//		XPathFactory xpathFactory = XPathFactory.newInstance(javax.xml.xpath.XPathFactory.DEFAULT_OBJECT_MODEL_URI, "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl", cl);
		XPathFactory xpathFactory = XPathFactory.newInstance();

		XPath xpath = xpathFactory.newXPath();
		xpath.getNamespaceContext();
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		File f = new File("/mnt/dea_scratch/TRP/test/Klassikerausgaben_test/page/bsb00087391_00009.xml");
		
		Document catalog = builder.parse(f);

		final XPathExpression expr = xpath.compile("//*[contains(name(), 'Page')]/@imageWidth");

//		Object result = expr.evaluate(catalog, XPathConstants.NUMBER);
//		Double res = (Double)result;
//		System.out.println(res);
		
		Object result = expr.evaluate(catalog, XPathConstants.STRING);
		String res = (String)result;
		System.out.println(res);
		
//		Object result = expr.evaluate(catalog, XPathConstants.NODESET);
//		NodeList nodes = (NodeList) result;
//		if (nodes.getLength() > 0) {
//			String[] parents = new String[nodes.getLength()];
//			for (int i = 0; i < nodes.getLength(); i++) {
//				parents[i] = nodes.item(i).getNodeValue();
//				System.out.println(parents[i]);
//			}
//		}
	}
}
