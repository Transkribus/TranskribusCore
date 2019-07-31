package eu.transkribus.core.util;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.dea.fimgstoreclient.IFimgStoreGetClient;
import org.dea.fimgstoreclient.beans.FimgStoreXml;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Implementation of {@link PageXmlProcessor} that handles files stored on FImagestore, identified by a store key. 
 *
 */
public class PageXmlRemoteFileProcessor extends PageXmlProcessor {

	protected final IFimgStoreGetClient fClient;
	
	public PageXmlRemoteFileProcessor(IFimgStoreGetClient fClient) throws XPathFactoryConfigurationException, ParserConfigurationException {
		super();
		this.fClient = fClient;
	}
	
	@Override
	protected Document loadDocument(String xmlKey)
			throws MalformedURLException, IllegalArgumentException, SAXException, IOException {
		if(fClient.hasFileAccess()) {
			FimgStoreXml xml = fClient.getXml(xmlKey);
			return super.parse(xml.getData());
		} else {
			//for remote files this is faster than reading the data to a byte[] first
			return super.parse(fClient.getUriBuilder().getFileUri(xmlKey).toURL());
		}
	}

}
