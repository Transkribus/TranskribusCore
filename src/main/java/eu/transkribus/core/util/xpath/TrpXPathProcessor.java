package eu.transkribus.core.util.xpath;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
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

/**
 * Provides means parse XML files and to apply XPath Expressions on XML files in various sources.<br/>
 * All heavy initialization tasks are done in the constructor, so reuse instances.<br/>
 * In the first place this class is meant to be used for applying the same xPath expression(s)
 * to a large batch of files. Therefore the {@link #compile(String)} method and the apply-methods can be used.
 * 
 * @author philip
 *
 */
public class TrpXPathProcessor {
	private final static  Logger logger = LoggerFactory.getLogger(TrpXPathProcessor.class);
	public final static DocBuilderFactoryImpl DEFAULT_DOC_BUILDER_FACTORY_IMPL = DocBuilderFactoryImpl.ApacheXerces;
	public final static XPathFactoryImpl DEFAULT_XPATH_FACTORY_IMPL = XPathFactoryImpl.SunApache;
//	public final static DocBuilderFactoryImpl DEFAULT_DOC_BUILDER_FACTORY_IMPL = DocBuilderFactoryImpl.Saxon;
//	public final static XPathFactoryImpl DEFAULT_XPATH_FACTORY_IMPL = XPathFactoryImpl.Saxon;
	private final DocumentBuilder builder;
	private final ClassLoader classLoader;
	private final XPathFactory xPathFactory;
	private final XPath xPath;
	
	/**
	 * Constructs an instance with Saxon implementations
	 *  
	 * @throws XPathFactoryConfigurationException
	 * @throws ParserConfigurationException
	 */
	public TrpXPathProcessor() throws XPathFactoryConfigurationException, ParserConfigurationException {
		this(DEFAULT_DOC_BUILDER_FACTORY_IMPL, DEFAULT_XPATH_FACTORY_IMPL);
	}
	
	public TrpXPathProcessor(DocBuilderFactoryImpl docBuilderFactoryImpl, XPathFactoryImpl xPathFactoryImpl) 
			throws XPathFactoryConfigurationException, ParserConfigurationException {
		this(docBuilderFactoryImpl.getClassName(), xPathFactoryImpl.getClassName());
	}
	
	public TrpXPathProcessor(final String docBuilderFactoryImpl, final String xPathFactoryImpl) 
			throws XPathFactoryConfigurationException, ParserConfigurationException {
		if(docBuilderFactoryImpl == null || xPathFactoryImpl == null) {
			throw new IllegalArgumentException("Arguments must not be null!");
		}
		classLoader = this.getClass().getClassLoader();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(
				docBuilderFactoryImpl, 
				classLoader);
		builder = factory.newDocumentBuilder();
		xPathFactory = XPathFactory.newInstance(
				javax.xml.xpath.XPathFactory.DEFAULT_OBJECT_MODEL_URI, 
				xPathFactoryImpl, 
				classLoader
				);
		xPath = xPathFactory.newXPath();
	}
	
	/**
	 * Compile a reusable XPathExpression to be used in the get-methods of this class
	 * @param xPathStr
	 * @return
	 * @throws XPathExpressionException
	 */
	public XPathExpression compile(final String xPathStr) throws XPathExpressionException {
		return xPath.compile(xPathStr);
	}
	
	/**
	 * Applies xPath and returns a nodelist
	 * 
	 * @param xmlFile
	 * @param xPathExp
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public NodeList getNodeList(File xmlFile, XPathExpression xPathExp) throws SAXException, IOException, XPathExpressionException {
		Document doc = this.parse(xmlFile);
		return (NodeList)applyXPathExpression(doc, xPathExp, XPathConstants.NODESET);
	}
	
	/**
	 * Applies xPath and returns a nodelist
	 * 
	 * @param xmlUrl
	 * @param xPathExp
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public NodeList getNodeList(URL xmlUrl, XPathExpression xPathExp) throws SAXException, IOException, XPathExpressionException {
		Document doc = this.parse(xmlUrl);
		return (NodeList)applyXPathExpression(doc, xPathExp, XPathConstants.NODESET);
	}
	
	/**
	 * Applies xPath and returns a nodelist
	 * 
	 * @param xmlUrl
	 * @param xPathExp
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public NodeList getNodeList(Document doc, XPathExpression xPathExp) throws SAXException, IOException, XPathExpressionException {
		return (NodeList)applyXPathExpression(doc, xPathExp, XPathConstants.NODESET);
	}
	
	/**
	 * Applies xPath and returns a single node
	 * 
	 * @param xmlFile
	 * @param xPathExp
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 * @throws IllegalArgumentException if the given xPathExpression does not return a single node
	 */
	public Node getNode(File xmlFile, XPathExpression xPathExp) throws SAXException, IOException, XPathExpressionException {
		Document doc = this.parse(xmlFile);
		return (Node)applyXPathExpression(doc, xPathExp, XPathConstants.NODE);
	}
	
	/**
	 * Applies xPath and returns a single node
	 * 
	 * @param xmlUrl
	 * @param xPathExp
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 * @throws IllegalArgumentException if the given xPathExpression does not return a single node
	 */
	public Node getNode(URL xmlUrl, XPathExpression xPathExp) throws SAXException, IOException, XPathExpressionException {
		Document doc = this.parse(xmlUrl);
		return (Node)applyXPathExpression(doc, xPathExp, XPathConstants.NODE);
	}
	
	/**
	 * Applies xPath and returns a single node
	 * 
	 * @param xmlUrl
	 * @param xPathExp
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 * @throws IllegalArgumentException if the given xPathExpression does not return a single node
	 */
	public Node getNode(Document doc, XPathExpression xPathExp) throws SAXException, IOException, XPathExpressionException {
		return (Node)applyXPathExpression(doc, xPathExp, XPathConstants.NODE);
	}

	private Object applyXPathExpression(Document doc, XPathExpression xPathExp, QName returnType) throws XPathExpressionException {
		if(doc == null || xPathExp == null) {
			throw new IllegalArgumentException("An argument is null!");
		}
		return xPathExp.evaluate(doc, returnType);
	}
	
	/**
	 * Gets Document object from URL.
	 * 
	 * @param xmlUrl
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document parse(URL xmlUrl) throws SAXException, IOException {
		if(xmlUrl == null) {
			throw new IllegalArgumentException("Argument is null!");
		}
		return builder.parse(xmlUrl.openStream());
	}
	/**
	 * Gets Document object from File.
	 * 
	 * @param xmlUrl
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document parse(File xmlFile) throws SAXException, IOException {
		if(xmlFile == null) {
			throw new IllegalArgumentException("Argument is null!");
		}
		return builder.parse(xmlFile);
	}
	
	/**
	 * Currently only contains Apache Xerces implementation.
	 * 
	 * @author philip
	 *
	 */
	public enum XPathFactoryImpl {
		/**
		 * "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl"
		 */
		SunApache("com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl"),
		/**
		 * "net.sf.saxon.xpath.XPathFactoryImpl"
		 */
		Saxon("net.sf.saxon.xpath.XPathFactoryImpl"),
		/**
		 * "org.apache.xpath.jaxp.XPathFactoryImpl"
		 */
		OrgApache("org.apache.xpath.jaxp.XPathFactoryImpl");
		private final String className;
		private XPathFactoryImpl(final String impl) {
			this.className = impl;
		}
		public String getClassName() {
			return this.className;
		}
	}
	
	public enum DocBuilderFactoryImpl {
		/**
		 * "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl"
		 */
		ApacheXerces("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl"),
		/**
		 * "net.sf.saxon.om.DocumentBuilderFactoryImpl"
		 */
		Saxon("net.sf.saxon.om.DocumentBuilderFactoryImpl"),
		/**
		 * "oracle.xml.jaxp.JXDocumentBuilderFactory"
		 */
		Oracle("oracle.xml.jaxp.JXDocumentBuilderFactory");
		private final String className;
		private DocBuilderFactoryImpl(final String impl) {
			this.className = impl;
		}
		public String getClassName() {
			return this.className;
		}
	}
}
