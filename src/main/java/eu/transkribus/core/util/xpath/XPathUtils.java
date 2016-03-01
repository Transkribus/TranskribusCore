package eu.transkribus.core.util.xpath;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPathUtils {
	
	public static Document parseDocument(DocumentBuilderFactory factory, URL xmlFile) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		return builder.parse(xmlFile.openStream());
	}
	
	public static String getNodeListText(NodeList list) {
		String txt = "";
		for (int i = 0; i < list.getLength(); i++) {
		    Node node = list.item(i);
		    txt += node.getTextContent();
		}
		
		return txt;
	}

}
