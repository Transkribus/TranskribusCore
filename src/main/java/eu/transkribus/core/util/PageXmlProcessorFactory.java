package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.transkribus.core.catti.LocalFimagestoreClient;
import eu.transkribus.core.util.xpath.TrpXPathProcessor;
import eu.transkribus.core.util.xpath.TrpXPathProcessor.DocBuilderFactoryImpl;
import eu.transkribus.core.util.xpath.TrpXPathProcessor.XPathFactoryImpl;

public class PageXmlProcessorFactory {
	private static final Logger logger = LoggerFactory.getLogger(PageXmlProcessorFactory.class);
	private static final String STORE_LOCATION = "/mnt/nmtera1/Content/fimagestore_trp";
	
	/**
	 * Determines if fimagestore netshare is available and returns a fitting implementation 
	 * of {@link PageXmlProcessor}, either accessing files directly or via URLs/HTTPS.
	 * 
	 * @return
	 * @throws XPathFactoryConfigurationException
	 * @throws ParserConfigurationException
	 */
	public static PageXmlProcessor newInstance() throws XPathFactoryConfigurationException, ParserConfigurationException {
		DocBuilderFactoryImpl dbImpl = TrpXPathProcessor.DEFAULT_DOC_BUILDER_FACTORY_IMPL;
		XPathFactoryImpl xpImpl = TrpXPathProcessor.DEFAULT_XPATH_FACTORY_IMPL;
		return newInstance(dbImpl, xpImpl);
	}
	
	public static PageXmlProcessor newInstance(DocBuilderFactoryImpl docBuilderFactoryImpl, 
			XPathFactoryImpl xPathFactoryImpl) throws XPathFactoryConfigurationException, ParserConfigurationException {
		File store = new File(STORE_LOCATION);
		if(store.isDirectory() && store.canRead()) {
			logger.debug("Returning Instance with netShare access.");
			return buildNetShareInstance(docBuilderFactoryImpl, xPathFactoryImpl);
		} else {
			logger.debug("Returning Instance with HTTPS access.");
			return buildHttpInstance(docBuilderFactoryImpl, xPathFactoryImpl);
		}
	}
	
	private static PageXmlProcessor buildHttpInstance(DocBuilderFactoryImpl dbImpl, XPathFactoryImpl xpImpl) throws XPathFactoryConfigurationException, ParserConfigurationException {
		return new PageXmlProcessor(dbImpl, xpImpl) {
			@Override
			protected Document loadDocument(final String xmlKey)
					throws MalformedURLException, IllegalArgumentException, SAXException, IOException {
				return super.parse(uriBuilder.getFileUri(xmlKey).toURL());
			}
		};
	}
	
	private static PageXmlProcessor buildNetShareInstance(DocBuilderFactoryImpl dbImpl, XPathFactoryImpl xpImpl) throws XPathFactoryConfigurationException, ParserConfigurationException {
		return new PageXmlProcessor(dbImpl, xpImpl) {
			@Override
			protected Document loadDocument(final String xmlKey)
					throws MalformedURLException, IllegalArgumentException, SAXException, IOException {
				File file = LocalFimagestoreClient.findFile(STORE_LOCATION, xmlKey);
				return super.parse(file);
			}
		};
	}
	
	
}
