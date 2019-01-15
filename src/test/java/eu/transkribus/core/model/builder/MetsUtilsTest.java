package eu.transkribus.core.model.builder;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.transkribus.core.model.builder.mets.util.MetsUtil;
import eu.transkribus.core.util.xpath.TrpXPathProcessor;

public class MetsUtilsTest {
	private static final Logger logger = LoggerFactory.getLogger(MetsUtilsTest.class);
	
//	@Test
	public void test1() throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
		File mets = new File("/tmp/mets/ppn778418405.dv.mets.xml");
		logger.info("mets1 = " + MetsUtil.isDfgMets(mets));
		
		File mets2 = new File("/tmp/mets/trp-mets.xml");
		logger.info("mets2 = " + MetsUtil.isDfgMets(mets2));
	}
	
	
//	@Test
	public void test2() throws XPathExpressionException, SAXException, IOException, XPathFactoryConfigurationException, ParserConfigurationException {
	
		// this Xpath did not compile anymore, so it was changed to the one below
		String oldXpath = "//*[local-name()='rightsMD'][(@ID,'RIGHTS')][1]";
		
		String xpath = "//*[local-name()='rightsMD'][@ID='RIGHTS'][1]";
		
		File mets = new File("/tmp/mets/ppn778418405.dv.mets.xml");
		TrpXPathProcessor proc = new TrpXPathProcessor();
		XPathExpression exp = proc.compile(xpath);
		Element rightsElement = (Element) proc.getNode(mets, exp);
		
		if (rightsElement != null) {
			NodeList actFields = rightsElement.getElementsByTagName("dv:rights");

			for (int i = 0; i < actFields.getLength(); i++) {
				Element dvRights = (Element) actFields.item(i);

				logger.debug("title element found: " + dvRights.getAttribute("xmlns:dv"));

				if (dvRights.getAttribute("xmlns:dv").equals("http://dfg-viewer.de/")) {
					logger.debug("Dfg-Viewer conform Mets delivered....go on");
					logger.debug("success");
				}
			}

		}
	}
}
