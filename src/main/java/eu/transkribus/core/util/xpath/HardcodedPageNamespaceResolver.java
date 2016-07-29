package eu.transkribus.core.util.xpath;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HardcodedPageNamespaceResolver implements NamespaceContext {
	
	private final static Logger logger = LoggerFactory.getLogger(HardcodedPageNamespaceResolver.class);
	
	String defaultNamespace = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15";
	String xsiNamespace = "http://www.w3.org/2001/XMLSchema-instance";
	
	public HardcodedPageNamespaceResolver() {
	}
	
	public HardcodedPageNamespaceResolver(String defaultNamespace) {
		this.defaultNamespace = defaultNamespace;
	}

	/**
	 * This method returns the uri for all prefixes needed. Wherever possible it
	 * uses XMLConstants.
	 * 
	 * @param prefix
	 * @return uri
	 */
	public String getNamespaceURI(String prefix) {
		if (prefix == null) {
			throw new IllegalArgumentException("No namespace prefix provided!");
		} else if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
			return defaultNamespace;
		} else if (prefix.equals("xsi")) {
			return xsiNamespace;
		}
		// add additonal else if clauses for more hardcoded namespaces here...
		else {
			return XMLConstants.NULL_NS_URI;
		}
	}

	public String getPrefix(String namespaceURI) {
		// Not needed in this context.
		return null;
	}

	public Iterator getPrefixes(String namespaceURI) {
		// Not needed in this context.
		return null;
	}

}
