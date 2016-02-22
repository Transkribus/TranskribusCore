package eu.transkribus.core.io;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.transkribus.core.util.XmlUtils;

public class XPathTest {
	
	
	private static void someOtherTest() {
		try {
			String inFolder="/media/iza_retro/DIG_Auftraege/D_2014_0022_BSB_Klassikerausgaben/sample_buch_done/page/";
			File folder=new File(inFolder);
			File[] xmlFiles = folder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File arg0, String arg1) {
					return arg1.toLowerCase().endsWith(".xml");
				}
			});
			System.out.println("Nr fo files = "+xmlFiles.length);
			
			HashMap<File, Integer> duplicateEntriesFiles = new HashMap<>();
			for (File xml : xmlFiles) {
				Document doc = XmlUtils.parseDomFromFile(xml, false);
//				System.out.println("Checking for duplicate entries in: "+xml.getName());
				int c = XmlUtils.checkForDuplicateElementsById(doc);
				if (c>0) {
					duplicateEntriesFiles.put(xml, c);
				}
			}
			System.out.println("Summary: ---");
			for (File f : duplicateEntriesFiles.keySet()) {
				System.out.println(duplicateEntriesFiles.get(f)+" duplicate entries in "+f.getName());
			}
			
			
			if (true) return;
			
			Document doc = XmlUtils.parseDomFromFile(new File("/media/iza_retro/DIG_Auftraege/D_2014_0022_BSB_Klassikerausgaben/sample_buch_done/page/bsb00070246_00008.xml"), false);
			
			XmlUtils.checkForDuplicateElementsById(doc);
			
			if (true) return;
			
			NodeList allNodes = doc.getElementsByTagName("*");
			System.out.println(allNodes.getLength());
			
			int c = 0;
			for (int i=0; i<allNodes.getLength(); ++i) {
				Node n = allNodes.item(i);
				if (n instanceof Element) {
					Element e = (Element) n;
					if (e.hasAttribute("points")) {
						String ptsStr = e.getAttribute("points");
						c++;
						System.out.println("Element: "+e.getTagName()+" pts = "+ptsStr);
						
						
					}
					
				}
				
//				n.hasAttributes() && n.getAttributes().
				
				
			}
			System.out.println("nr of points: "+c);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, XPathFactoryConfigurationException{
		ClassLoader cl = XPathTest.class.getClassLoader();
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
