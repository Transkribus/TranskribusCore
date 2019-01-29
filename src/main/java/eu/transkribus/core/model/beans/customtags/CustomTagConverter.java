package eu.transkribus.core.model.beans.customtags;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;

public class CustomTagConverter {
	private static final Logger logger = LoggerFactory.getLogger(CustomTagConverter.class);
	
	public static final String CUSTOM_ATTRIBUTE_NAME = "custom";
	
	public static final String TEXT_LINE_ELEMENT_NAME = "TextLine";
	public static final String WORD_ELEMENT_NAME = "Word";

	private static final String TEXT_EQUIV_ELEMENT_NAME = "TextEquiv";
	private static final String UNICODE_ELEMENT_NAME = "Unicode";
	
	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder dBuilder;
	
	public CustomTagConverter() throws ParserConfigurationException {
		dBuilder = dbFactory.newDocumentBuilder();
	}
	
	private Document parseXmlFile(String fileName) throws SAXException, IOException {
		return dBuilder.parse(new File(fileName));
	}
	
	private void writeDocumentAsFile(Document doc, String fileName) throws TransformerFactoryConfigurationError, TransformerException {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Result output = new StreamResult(new File("output.xml"));
		Source input = new DOMSource(doc);
		
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		transformer.transform(input, output);
	}
	
	private String createDocumentString(Document doc) throws TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
//		String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
		String output = writer.getBuffer().toString();
		
		return output;
	}
	
	private Node findAttributeNode(Node node, String attName) {
		if (node.hasAttributes()) {
			// get attributes names and values
			NamedNodeMap nodeMap = node.getAttributes();
			for (int i = 0; i < nodeMap.getLength(); i++) {
				if (nodeMap.item(i).getNodeName().equals(attName)) {
					return nodeMap.item(i);
				}
			}
		}
		return null;
	}
	
	private Node getChildNode(Node node, String id) {
		if (node.hasChildNodes()) {
			NodeList nl = node.getChildNodes();
			for (int i=0; i<nl.getLength(); ++i) {
				if (nl.item(i).getNodeName().equals(id)) {
					return nl.item(i);
				}
			}
		}
		return null;
	}
	
	private Node getUnicodeNodeFromShapeNode(Node shapeNode) {
		Node textEquivNode = getChildNode(shapeNode, TEXT_EQUIV_ELEMENT_NAME);
		if (textEquivNode != null) {
			return getChildNode(textEquivNode, UNICODE_ELEMENT_NAME);
		}
		
		return null;
	}
	
	private void transformNodesListToExplicitTags(NodeList shapeNodeList) {
		for (int count = 0; count < shapeNodeList.getLength(); count++) {
			Node shapeNode = shapeNodeList.item(count);
			if (shapeNode.getNodeType() == Node.ELEMENT_NODE) {
				Node customAttribute = findAttributeNode(shapeNode, CUSTOM_ATTRIBUTE_NAME);
				Node unicodeNode = getUnicodeNodeFromShapeNode(shapeNode);
				String text = unicodeNode==null || unicodeNode.getTextContent() == null ? "" : unicodeNode.getTextContent();
				if (customAttribute != null) {
					String customTagValue = customAttribute.getNodeValue();
					List<CssSyntaxTag> tags = CssSyntaxTag.parseTags(customTagValue);
					Collections.sort(tags, new Comparator<CssSyntaxTag>() {
						@Override
						public int compare(CssSyntaxTag o1, CssSyntaxTag o2) {
							return Integer.compare(o1.getOffset(), o2.getOffset());
						}
					});
					
					System.out.println("customTagValue: "+customTagValue);
					System.out.println("original text: "+text);
					
					String taggedText = CssSyntaxTagUtil.getTaggedContent(text, tags);
					System.out.println("tagged text: "+taggedText);
				}
				
				
				
				// get node name and value
//				System.out.println("\nNode Name =" + shapeNode.getNodeName() + " [OPEN]");
//				System.out.println("Node Value =" + shapeNode.getTextContent());
//
//				if (shapeNode.hasAttributes()) {
//					// get attributes names and values
//					NamedNodeMap nodeMap = shapeNode.getAttributes();
//
//					for (int i = 0; i < nodeMap.getLength(); i++) {
//						Node node = nodeMap.item(i);
//						System.out.println("attr name : " + node.getNodeName());
//						System.out.println("attr value : " + node.getNodeValue());
//					}
//				}

//				if (tempNode.hasChildNodes()) {
//
//					// loop again if has child nodes
//					printNote(tempNode.getChildNodes());
//
//				}

//				System.out.println("Node Name =" + shapeNode.getNodeName() + " [CLOSE]");

			}
		}
		
		
	}
	
	public void createExplicitTagFile(String fileName) throws SAXException, IOException, TransformerException {
		Document doc = parseXmlFile(fileName);
		doc.normalize(); // https://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		
		// TODO: modify 
		
		transformNodesListToExplicitTags(doc.getElementsByTagName(TEXT_LINE_ELEMENT_NAME));
		transformNodesListToExplicitTags(doc.getElementsByTagName(WORD_ELEMENT_NAME));

		
//		System.out.println(createDocumentString(doc));
	} // end createExplicitTagFile
	

}
