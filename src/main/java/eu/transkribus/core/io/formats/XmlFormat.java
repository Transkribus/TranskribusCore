package eu.transkribus.core.io.formats;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import eu.transkribus.core.model.beans.fat.RootFolder;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;

public enum XmlFormat {
	PAGE_2013(	"http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15", 
				"http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15/pagecontent.xsd", 
				"xsd/pagecontent.xsd", PcGtsType.class),
	PAGE_2010(	"http://schema.primaresearch.org/PAGE/gts/pagecontent/2010-03-19",
				"http://schema.primaresearch.org/PAGE/gts/pagecontent/2010-03-19/pagecontent.xsd",
				"xsd/pagecontent_2010-03-19.xsd", null),
	ABBYY_10(	"http://www.abbyy.com/FineReader_xml/FineReader10-schema-v1.xml",
				"http://www.abbyy.com/FineReader_xml/FineReader10-schema-v1.xml",
				"xsd/FineReader10-schema-v1.xml", null),
	ALTO_2(		"http://www.loc.gov/standards/alto/ns-v2#",
				"http://www.loc.gov/standards/alto/alto-v2.0.xsd",
				"xsd/alto-v2.0.xsd", null),
	FAT(		"http://www.literature.at/schemas/FAT/FAT_1.1.xsd",
				"http://www.literature.at/schemas/FAT/FAT_1.1.xsd",
				"xsd/FAT_1.1.xsd", RootFolder.class),
	METS(		"http://www.loc.gov/METS/",
				"http://www.loc.gov/standards/mets/mets.xsd",
				"xsd/mets.xsd", Mets.class),				
			
	UNKNOWN("Unknown", "Unknown", "Unknown", null);
	
	private final static Logger logger = LoggerFactory.getLogger(XmlFormat.class);
	private SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	
	public final String namespace;
	public final String xsdRemoteUrl;
	public final String xsdLocalUrl;
	public final String xsiSchemaLocation;
	private Schema schema=null;
	public final Class<?> jaxBClazz;
	
	private XmlFormat(String ns, String xsdUrl, String xsdLocalUrl, Class<?> jaxBClazz) {
		this.namespace = ns;
		this.xsdRemoteUrl = xsdUrl;
		this.xsdLocalUrl = xsdLocalUrl;
		this.xsiSchemaLocation = namespace+" "+xsdUrl;
		this.jaxBClazz = jaxBClazz;
	}
	
	public static XmlFormat resolveFromClazz(Class<?> targetClazz) {
		if (targetClazz == null)
			return null;
		
		for (XmlFormat format : XmlFormat.values()) {
			if (targetClazz.equals(format.jaxBClazz))
				return format;
		}
		return null;
	}
	
	public static XmlFormat resolveFromNs(final String namespace) {
		for(XmlFormat format : XmlFormat.values()){
			if(format.namespace.equals(namespace)) {
				return format;
			}
		}
		return XmlFormat.UNKNOWN;
	}
	
	/**
	 * Either compiles the schema or returns the previously compiled schema.
	 * @return The compiled schema
	 * @throws SAXException if parsing or schema compilation fails
	 * @throws MalformedURLException if the schema URL is invalid
	 */
	public Schema getOrCompileSchema() throws SAXException, MalformedURLException  {
		if (schema != null)
			return schema;
		
		logger.debug("compiling schema "+toString()+" from local file...");
		try {			
			schema = sf.newSchema(XmlFormat.class.getClassLoader().getResource(xsdLocalUrl));
		} catch (NullPointerException | SAXException e) {
			logger.error("Could not compile schema from local file, error msg: "+e.getMessage());
			logger.debug("compiling schema "+toString()+" from url...");
			try {
				schema = sf.newSchema(new URL(xsdRemoteUrl));
			} catch (SAXException | MalformedURLException e1) {
				e1.addSuppressed(e);
				throw e1;
			}
		}
		return schema;
	}
	
	public static void main(String[] args) {
		
		long t;
		for(XmlFormat format : XmlFormat.values()) {
			if (format == XmlFormat.UNKNOWN)
				continue;
			
			try {
				format.getOrCompileSchema();
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
		
		logger.info("Finished tests!");
	}
}
