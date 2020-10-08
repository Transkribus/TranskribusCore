package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.transkribus.core.io.FimgStoreReadConnection;
import eu.transkribus.core.util.xpath.TrpXPathProcessor;

/**
 * Equivalent to {@link PageXmlUtils} but relying on org.w3c.dom.Document and XPath instead of JaxB.<br/>
 * Use {@link PageXmlProcessorFactory} to get an instance of this.
 * 
 * @author philip
 *
 */
public abstract class PageXmlProcessor extends TrpXPathProcessor {
	private static final Logger logger = LoggerFactory.getLogger(PageXmlProcessor.class);

	/**
	 * @deprecated use PageXmlRemoteFileProcessor and remove this field
	 */
	protected final FimgStoreUriBuilder uriBuilder;
	
	protected final Map<String, Document> docCache;

	public PageXmlProcessor() throws XPathFactoryConfigurationException, ParserConfigurationException {
		this(DEFAULT_DOC_BUILDER_FACTORY_IMPL, DEFAULT_XPATH_FACTORY_IMPL);
	}
	
	public PageXmlProcessor(DocBuilderFactoryImpl docBuilderFactoryImpl, XPathFactoryImpl xPathFactoryImpl)
			throws XPathFactoryConfigurationException, ParserConfigurationException {
		this(docBuilderFactoryImpl.getClassName(), xPathFactoryImpl.getClassName());
	}
	
	public PageXmlProcessor(String docBuilderFactoryImpl, String xPathFactoryImpl)
			throws XPathFactoryConfigurationException, ParserConfigurationException {
		super(docBuilderFactoryImpl, xPathFactoryImpl);
		uriBuilder = FimgStoreReadConnection.getUriBuilder();
		docCache = new HashMap<>();
	}
	
	/**
	 * Retrieves the XML identified by this xmlKey as {@link org.w3c.dom.Document} from the storage system.<br/>
	 * Use {@link #getDocument(String)} instead, as it calls this method and imeplements caching.
	 * 
	 * @param xmlKey
	 * @return
	 * @throws MalformedURLException
	 * @throws IllegalArgumentException
	 * @throws SAXException
	 * @throws IOException
	 */
	protected abstract Document loadDocument(final String xmlKey) throws MalformedURLException, IllegalArgumentException, SAXException, IOException;
	
	/**
	 * Retrieves the file identified by this key and returns a {@link org.w3c.dom.Document}<br/>
	 * Implements caching.
	 * 
	 * @param xmlKey
	 * @return
	 * @throws MalformedURLException
	 * @throws IllegalArgumentException
	 * @throws SAXException
	 * @throws IOException
	 */
	public Document getDocument(final String xmlKey) throws MalformedURLException, IllegalArgumentException, SAXException, IOException {
		final Document doc;
		if(docCache.containsKey(xmlKey)) {
			doc = docCache.get(xmlKey);
		} else {
			doc = loadDocument(xmlKey);
			docCache.put(xmlKey, doc);
		}
		return doc;
	}
	
	public Node getNodeCoordsPoints(Node node) throws XPathExpressionException, SAXException, IOException {
		return super.getNode(node, "./Coords/@points");
	}
	
	public String getNodeId(Node node) {
		return getNodeAttribute(node, "id").getNodeValue();
	}
	
	public Node getLineById(final String xmlKey, final String lineId) throws MalformedURLException, IllegalArgumentException, SAXException, IOException, XPathExpressionException {
		Document doc = getDocument(xmlKey);
		return getLineById(doc, lineId);
	}
	
	public Node getLineById(final Document doc, final String lineId) throws XPathExpressionException, SAXException, IOException {
		final String xPath = "//TextLine[@id='" + lineId + "']";
		XPathExpression exp = super.compile(xPath);
		return super.getNode(doc, exp);
	}
	
	public NodeList getLines(Document doc) throws XPathExpressionException, SAXException, IOException {
		final String xPath = "//TextLine";
		XPathExpression exp = super.compile(xPath);
		return super.getNodeList(doc, exp);
	}
	
	public NodeList getTextLineUnicodes(Document doc) throws XPathExpressionException, SAXException, IOException {
		final String xPath = "//TextLine/TextEquiv/Unicode";
		XPathExpression exp = super.compile(xPath);
		return super.getNodeList(doc, exp);
	}
	
	public NodeList getCoordsPointsFromElement(Document doc, String element) throws XPathExpressionException, SAXException, IOException {
		final String xPath = "//"+element+"/Coords/@points";
		XPathExpression exp = super.compile(xPath);
		return super.getNodeList(doc, exp);
	}
	
	public NodeList getTextLineCoordsPoints(Document doc)  throws XPathExpressionException, SAXException, IOException {
		return getCoordsPointsFromElement(doc, "TextLine");
		
//		final String xPath = "//TextLine/Coords/@points";
//		XPathExpression exp = super.compile(xPath);
//		return super.getNodeList(doc, exp);
	}
	
	public NodeList getTextRegionCoordsPoints(Document doc)  throws XPathExpressionException, SAXException, IOException {
		return getCoordsPointsFromElement(doc, "TextRegion");
	}
	
	public NodeList getTextRegions(Document doc) throws XPathExpressionException, SAXException, IOException {
		final String xPath = "//TextRegion";
		XPathExpression exp = super.compile(xPath);
		return super.getNodeList(doc, exp);
	}
	
	public NodeList getRegions(Document doc) throws XPathExpressionException, SAXException, IOException {
		final String xPath = "//*[substring(name(), string-length(name()) - 5) = 'Region']"; // https://stackoverflow.com/questions/4203119/xpath-wildcards-on-node-name
		XPathExpression exp = super.compile(xPath);
		return super.getNodeList(doc, exp);
	}	
	
	public NodeList getBaselines(Document doc) throws XPathExpressionException, SAXException, IOException {
		final String xPath = "//Baseline";
		XPathExpression exp = super.compile(xPath);
		return super.getNodeList(doc, exp);
	}
	
	public List<String> getBaselinesPointStrs(Document doc) throws XPathExpressionException, SAXException, IOException {
		final String xPath = "//Baseline/@points";
		XPathExpression exp = super.compile(xPath);
		NodeList nl = super.getNodeList(doc, exp);
		return getNodeListValuesAsList(nl);
	}

	public List<String> getNodeListValuesAsList(NodeList nl) throws XPathExpressionException, SAXException, IOException {
		List<String> vals = new ArrayList<>();
		for (int i=0; i<nl.getLength(); ++i) {
			Node n = nl.item(i);
			vals.add(n.getNodeValue());
		}
		return vals;
	}
	
	public Node getTextRegionById(final Document doc, final String regionId) throws XPathExpressionException, SAXException, IOException {
		final String xPath = "//TextRegion[@id='" + regionId + "']";
		XPathExpression exp = super.compile(xPath);
		return super.getNode(doc, exp);
	}
	
	public Node getNodeById(final Document doc, final String regionId) throws XPathExpressionException, SAXException, IOException {
		final String xPath = "//*[@id='" + regionId + "']";
		XPathExpression exp = super.compile(xPath);
		return super.getNode(doc, exp);
	}	
	
	public String getUnicodeFromLineById(final String xmlKey, final String lineId) throws MalformedURLException, IllegalArgumentException, SAXException, IOException, XPathExpressionException {
		Document doc = getDocument(xmlKey);
		return getUnicodeFromLineById(doc, lineId);
	}
	
	public String getUnicodeFromLineById(final Document doc, final String lineId) throws XPathExpressionException, SAXException, IOException {
		final String xPath = "//TextLine[@id='" + lineId + "']/TextEquiv/Unicode";
		XPathExpression exp = super.compile(xPath);
		Node unicodeNode = super.getNode(doc, exp);
		return unicodeNode.getTextContent();
	}

	public List<String> getAllLineIds(final String xmlKey) throws MalformedURLException, IllegalArgumentException, SAXException, IOException, XPathExpressionException {
		Document doc = getDocument(xmlKey);
		return getAllLineIds(doc);
	}
	
	public List<String> getAllLineIds(final Document doc) throws MalformedURLException, IllegalArgumentException, SAXException, IOException, XPathExpressionException {
		final String xPath = "//TextLine";
		return getElementIds(doc, xPath);
	}
	
	private List<String> getElementIds(final Document doc, final String xPath) throws MalformedURLException, IllegalArgumentException, SAXException, IOException, XPathExpressionException {
		XPathExpression exp = super.compile(xPath);
		NodeList lines = super.getNodeList(doc, exp);
		List<String> ids = new ArrayList<>(lines.getLength());
		for(int i = 0; i < lines.getLength(); i++) {
			final String id = lines.item(i)
					.getAttributes()
					.getNamedItem("id")
					.getTextContent();
			if(StringUtils.isEmpty(id)) {
				logger.error("A line ID is empty!");
			} else {
				ids.add(id);
			}
		}
		return ids;
	}
	
	public List<String> getLineIdsByRegion(final String xmlKey, final String regionId) throws MalformedURLException, IllegalArgumentException, SAXException, IOException, XPathExpressionException {
		NodeList lines = getLinesByRegion(xmlKey, regionId);
		List<String> ids = new ArrayList<>(lines.getLength());
		for(int i = 0; i < lines.getLength(); i++) {
			final String id = lines.item(i)
					.getAttributes()
					.getNamedItem("id")
					.getTextContent();
			if(StringUtils.isEmpty(id)) {
				logger.error("A line ID is empty in region: " + regionId);
			} else {
				ids.add(id);
			}
		}
		return ids;
	}
	
	public NodeList getLinesByRegion(final String xmlKey, final String regionId) throws MalformedURLException, IllegalArgumentException, SAXException, IOException, XPathExpressionException {
		Document doc = getDocument(xmlKey);
		final String xPath = "//TextRegion[@id='" + regionId + "']/TextLine";
		XPathExpression exp = super.compile(xPath);
		return super.getNodeList(doc, exp);
	}
	
	/*
	 * to delete a node found with the xPathExpression
	 * used for e.g deleting the TranskribusMetadata during import
	 * PageXmlProcessor.deleteNode(pageXml, "//Metadata/TranskribusMetadata");
	 */
	public static void deleteNode(File pageXml, String xPathExpression) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, TransformerException {
		
		logger.info("called the new delete method");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document document;

		document = dbf.newDocumentBuilder().parse(pageXml);

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        XPathExpression expression = xpath.compile(xPathExpression);

        Node mdNode = (Node) expression.evaluate(document, XPathConstants.NODE);
        if (mdNode != null) {
        	Node parent = mdNode.getParentNode();
	        parent.removeChild(mdNode);
	        deleteEmptyNode(parent);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.transform(new DOMSource(document), new StreamResult(pageXml));
	        
	}
	
    /*
     *  remove empty text nodes (ie nothing else than spaces and carriage return)
     *  this means also the linebreaks are removed is the PAGE XML in the Metadata
     */
    // 
     private static void deleteEmptyNode(Node node){                      
         NodeList childs = node.getChildNodes();
         for (int i = 0; i < childs.getLength(); i++) {
        	 
        	 Node childNode = childs.item(i);
        	 //logger.debug("&&&&&text content of child: " + childNode.getTextContent());
        	 if(childNode.getNodeType() == Node.TEXT_NODE
        	            && childNode.getNodeValue().trim().isEmpty()){
        		 logger.debug("empty node found - will be removed");
        		 childNode.getParentNode().removeChild(childNode);
        		 i--;
        	 }
         }
     }
}
