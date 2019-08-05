package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;

public class PageXmlFileProcessor extends PageXmlProcessor {
	private static final Logger logger = LoggerFactory.getLogger(PageXmlFileProcessor.class);

	public PageXmlFileProcessor() throws XPathFactoryConfigurationException, ParserConfigurationException {
		super();
	}

	@Override
	protected Document loadDocument(String xmlKey)
			throws MalformedURLException, IllegalArgumentException, SAXException, IOException {
		return parse(new File(xmlKey));
	}
	
	public static void main(String[] args) throws Exception {
		SebisStopWatch sw = new SebisStopWatch();
		
		sw.start();
		PageXmlFileProcessor fp = new PageXmlFileProcessor();
		Document doc = fp.loadDocument("C:\\tmp\\impact_test_page.xml");
		sw.stop(true, "time for loading doc: ");
		sw.start();
		NodeList nl = fp.getTextLineUnicodes(doc);
		sw.stop(true, "time for loading lines: ");
		
//		sw.start();
//		PcGtsType pc = PageXmlUtils.unmarshal(new File("C:\\tmp\\impact_test_page.xml"));
//		((TrpPageType)pc.getPage()).getLines();
//		sw.stop(true, "time for marshalling file : ");
		
		sw.start();
		logger.debug("got nodes = "+nl.getLength());
		for (int i=0; i<nl.getLength(); ++i) {
			Node n = nl.item(i);
			logger.debug("textline node = "+n+" val = "+n.getTextContent());
			n.setTextContent("test_string_"+i);
		}
		sw.stop(true, "time for setting string content: ");
		
		sw.start();
		fp.writeToFile(doc, new File("C:\\tmp\\impact_test_page_xpath_out.xml"), true);
		sw.stop(true, "time for saving: ");
	}

}
