package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * An XPath PAGE-XML Processor that can load PAGE-XMLs from files and URLs 
 */
public class PageXmlFileProcessor extends PageXmlProcessor {
	private static final Logger logger = LoggerFactory.getLogger(PageXmlFileProcessor.class);
	
	private String filePathOrUrl;
	private Document doc;

	public PageXmlFileProcessor(String filePathOrUrl) throws XPathFactoryConfigurationException, ParserConfigurationException {
		super();
		this.filePathOrUrl = filePathOrUrl;
	}

	@Override protected Document loadDocument(String filePathOrUrl)
			throws MalformedURLException, IllegalArgumentException, SAXException, IOException {
		logger.debug("filePathOrUrl = "+filePathOrUrl+" isFile = "+new File(filePathOrUrl).exists());
		
		this.doc = new File(filePathOrUrl).exists() ? parse(new File(filePathOrUrl)) : parse(new URL(filePathOrUrl));
//		this.doc = CoreUtils.fileExists(filePathOrUrl) ? parse(new File(filePathOrUrl)) : parse(new URL(filePathOrUrl));
		return this.doc;
	}
	
	public Document getDocument() throws MalformedURLException, IllegalArgumentException, SAXException, IOException {
		if (this.doc == null) {
			loadDocument(filePathOrUrl);
		}
		return this.doc;
	}

}
