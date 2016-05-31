package eu.transkribus.core.model.builder.mets.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MetsUtil {
	private static final Logger logger = LoggerFactory.getLogger(MetsUtil.class);

	public static boolean isDfgMets(File metsFile)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		
		// TODO check if METS Profile is known
					/*
					 * <mets:rightsMD ID="RIGHTS">
					 * 					<dv:rights xmlns:dv="http://dfg-viewer.de/">
									<dv:owner>Universitätsbibliothek Rostock</dv:owner>
									
									//mets:rightsMD[@ID,'RIGHTS']
									
					 */
					
					//Element rightsElement = (Element) XmlUtils.selectNode((Element) mets.getAmdSec(), "(*[contains(@ID,'RIGHTS')])[1]");
					//Element rightsElement = null;// = (Element) XmlUtils.selectNode((Element) mets.getAmdSec(), "//mets:rightsMD[@ID,'RIGHTS']");
					
		
		boolean isCompliant = false;
		// Create DocumentBuilderFactory for reading xml file
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(metsFile);

		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		XPathExpression expr = xpath.compile("//*[local-name()='rightsMD'][(@ID,'RIGHTS')][1]");
		Element rightsElement = (Element) expr.evaluate(doc, XPathConstants.NODE);

		if (rightsElement != null) {
			NodeList actFields = rightsElement.getElementsByTagName("dv:rights");

			for (int i = 0; i < actFields.getLength(); i++) {
				Element dvRights = (Element) actFields.item(i);

				logger.debug("title element found: " + dvRights.getAttribute("xmlns:dv"));

				if (dvRights.getAttribute("xmlns:dv").equals("http://dfg-viewer.de/")) {
					logger.debug("Dfg-Viewer conform Mets delivered....go on");
					isCompliant = true;
				}
			}

		}
		return isCompliant;
	}

}
