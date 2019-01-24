package eu.transkribus.core.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.xpath.XPathAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.transkribus.core.io.formats.XmlFormat;

public class XmlUtils {
	private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class);
	
	public static File printXML(Document doc, File file) throws IOException {
		OutputStream os = new FileOutputStream(file);
		StreamResult result = new StreamResult(os);
		try{
			printXML(doc, result);
		} finally {
			try{
				os.close();
			} catch (IOException e) {}
		}
		
		return file;
	}
	
	public static String printXML(Document doc) {
		String xmlStr;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStreamWriter osw = null;
		try{
			osw = new OutputStreamWriter(out, "UTF-8");
			StreamResult result = new StreamResult(osw);			
			printXML(doc, result);
			xmlStr = new String(out.toByteArray(), "UTF-8");
		} catch(UnsupportedEncodingException uee){
			//logger.error(uee);
			xmlStr = null;
		} finally {
			try{
//				writer.close();
				osw.close();
			} catch (IOException e) {}
		}
		return xmlStr;
	}
	
	public static StreamResult printXML(Document doc, StreamResult result) {
		if(result == null) {
			throw new IllegalArgumentException("Writer is null!");
		}
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
		} catch (Exception e) {
			logger.error("XML is not well-formed!");
			logger.error(e.getMessage());
		}
		return result;
	}
	
	public static XmlFormat getXmlFormat(final URL url) throws IOException{
		try {
			File file = new File(url.toURI());
			return getXmlFormat(file);
		} catch (URISyntaxException e) {
			throw new IOException("Could not read XML file at " + url.toString(), e);
		}
	}

	/**
	 * Returns supported XML format or throws Exception
	 * @param xmlFile
	 * @return
	 * @throws IOException
	 */
	public static XmlFormat getXmlFormat(File xmlFile) throws IOException {
		String namespace = null;
		//build pattern to extract "xmlns="(http://my.namespa.ce)""
		final String URL_CHARSET = "[a-zA-Z0-9:/_&?~#%=\\.\\-]*";
		final String xmlnsRegex = ".*\\s*xmlns=\"(" + URL_CHARSET + ")\".*";
		Pattern pattern = Pattern.compile(xmlnsRegex);
		//read first 2048 characters of this file. 
		//if the root element is too long and the namespace is not included here, this won't work
		final String content = DeaFileUtils.readFileAsString(xmlFile, 2048);
		
		Matcher m = pattern.matcher(content);
		XmlFormat format;
		if(m.find()){
			namespace = m.group(1);
			format = XmlFormat.resolveFromNs(namespace);
			logger.debug(xmlFile.getName() + ": " + format.toString() + " - namespace: " + namespace);
		} else {
			final String msg = "No namespace found in file: " + xmlFile.getAbsolutePath();
			logger.error(msg);
			format = XmlFormat.UNKNOWN;
		}
		return format;
		
		/* alternative:
		 * read the whole file as DOM. This is less performant but does well-formedness check
		 */
//		try{
//			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//			Document doc = dBuilder.parse(xmlFile);
//			logger.info(doc.getDocumentElement().getAttribute("xsi:schemaLocation"));
//			namespace = doc.getDocumentElement().getAttribute("xmlns");
//			logger.info(namespace);
//		} catch (SAXException | ParserConfigurationException e){
//			throw new IOException(e);
//		}
	}
	
	public static Document parseDomFromFile(File sourceFile, boolean namespaceAware) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(namespaceAware);
		
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(sourceFile);
	}
	
	public static void writeDomToFile(File dst, Document doc) throws TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		DOMSource source = new DOMSource(doc);
		StreamResult streamResult = new StreamResult(dst);
		transformer.transform(source, streamResult);
	}
	
	public static int checkForDuplicateElementsById(Document d) {
		NodeList allNodes = d.getElementsByTagName("*");
		
//		System.out.println(allNodes.getLength());
		
		HashMap<String, Element> idMap=new HashMap<>();
		
		int c = 0;
		final String attr="id";
		
		for (int i=0; i<allNodes.getLength(); ++i) {
			Node n = allNodes.item(i);
			if (n instanceof Element) {
				Element e = (Element) n;
				
				if (e.hasAttribute(attr)) {
					String attrValue = e.getAttribute(attr);
					if (!idMap.containsKey(attrValue))
						idMap.put(attrValue, e);
					else {
						System.out.println("Detected duplicate id: "+attrValue+" file: "+d.getDocumentURI());
						c++;
						
					}
					
//					c++;
//					System.out.println("Element: "+e.getTagName()+" pts = "+attrValue);
				}
				
			}
			
//			n.hasAttributes() && n.getAttributes().
			
			
		}
		return c;
		
	}
	
	public static XMLGregorianCalendar getXmlGregCal() throws DatatypeConfigurationException {
		return getXmlGregCal(System.currentTimeMillis());
	}

	public static XMLGregorianCalendar getXmlGregCal(long millis) throws DatatypeConfigurationException {
		//this is now
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(millis);
		XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
		return xmlCal;
	}	
	
	/*
	 * helper methods for reading Mods Metadata
	 */
	public static Document getDocumentFromFileWOE(String xmlLocation) {
		
		try {
			return getDocumentFromFile(new File(xmlLocation), "UTF-8");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	
	/**
	 * @param xmlFile the full path of the xml document 2 parse
	 * @param encoding set the encoding in which the file should be read
	 * @return an xml-Document object based on the file content
	 */
	public static Document getDocumentFromFile(File xmlFile, String encoding) throws ParserConfigurationException,
			SAXException, IOException
	{
		/*
		 * InputSource input = new InputSource(new FileInputStream(xmlFile));
		 * input.setEncoding("ISO-8859-1");
		 */ 
		InputSource input = new InputSource();
		input.setEncoding(encoding);
		input.setByteStream(new FileInputStream(xmlFile));
		return getDocument(input);
	}


	/**
	 * Reads the document from an input source
	 * @param input the input source which contains the XML content
	 * @return an xml-Document object based on the inputsource content
	 */
	private static Document getDocument(InputSource input) throws ParserConfigurationException, SAXException,
			IOException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// dbf.setValidating(true);
		// dbf.setExpandEntityReferences(true);

		DocumentBuilder db = dbf.newDocumentBuilder();
		// DefaultHandler handler = new DefaultHandler();
		// db.setErrorHandler(handler);
		// db.setEntityResolver(new XHTMLEntityResolver());
		return db.parse(input);
	}
	
	public static Node selectNode(Element documentElement, String xpath)
	{
		Node res = null;
		try {
			Node result = XPathAPI.selectSingleNode(documentElement, xpath);
			if (result!=null)
				res = result;
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static String getFirstSubElementFromElement(Node elem2, String nodeName)
	{
		String res = null;
		NodeList childs = elem2.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++)
		{
			Node elem = childs.item(i);
//			System.out.println(elem.getNodeName()+" = " +elem.getNodeType());
			if (elem.getNodeName().equals(nodeName))
			{
				res = elem.getTextContent();
			}
			else if (elem.hasChildNodes()) 
			{
				res = getFirstSubElementFromElement(elem, nodeName);
				if (res!=null)
					break;
			}
		}
		return res;
	}

	public static boolean isValid(File xmlFile, File schemaFile) throws IOException {
		if(xmlFile == null || schemaFile == null) {
			throw new IllegalArgumentException("An argument is null.");
		}
		URL schemaUrl = schemaFile.toURI().toURL();
		return isValid(xmlFile, schemaUrl);
	}
	
	public static boolean isValid(File xmlFile, URL schemaUrl) throws IOException {
		if(xmlFile == null || schemaUrl == null) {
			throw new IllegalArgumentException("An argument is null.");
		}
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema;
		try {
			schema = schemaFactory.newSchema(schemaUrl);
		} catch (SAXException e) {
			throw new IllegalArgumentException("Schema could not be parsed! " + schemaUrl.toExternalForm());
		}
		return isValid(xmlFile, schema);
	}

	private static boolean isValid(File xmlFile, Schema schema) throws IOException {
		Source xmlSource = new StreamSource(xmlFile);
		try {
		  Validator validator = schema.newValidator();
		  validator.validate(xmlSource);
		  logger.debug(xmlSource.getSystemId() + " is valid");
		  return true;
		} catch (SAXException e) {
		  logger.debug(xmlSource.getSystemId() + " is NOT valid reason:" + e);
		  return false;
		}
	}
}
