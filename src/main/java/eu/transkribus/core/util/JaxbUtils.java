package eu.transkribus.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.JAXBResult;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.transkribus.core.io.formats.XmlFormat;
import eu.transkribus.core.model.beans.ExportParameters;

public class JaxbUtils {
	private static final Logger logger = LoggerFactory.getLogger(JaxbUtils.class);
	
	// a set of known JAXB classes where we want schemalocations in the XML
//	protected static final Map<Class<?>, String> schemaLocs = new HashMap<>();
//	static {
//		schemaLocs.put(Mets.class, "http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/mets.xsd");
//		schemaLocs.put(PcGtsType.class, "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15 http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15/pagecontent.xsd");
//		schemaLocs.put(RootFolder.class, "http://www.literature.at/schemas/FAT/FAT_1.0.xsd http://www.literature.at/schemas/FAT/FAT_1.0.xsd");
//	}
	
	//	private final static SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	//	private static Schema schema=null;
	//	static {
	//		try {
	//			schema = sf.newSchema(new File("xsd/pagecontent.xsd"));
	//		} catch (SAXException e) {
	//			logger.error(e);
	//		}
	//	}
	//	public static Schema getSchema() { return schema; }

	/**
	 * @param clazzes
	 * @return
	 * @throws JAXBException
	 */
	public static JAXBContext createJAXBContext(Class<?>... clazzes) throws JAXBException {
//		return JAXBContext.newInstance("org.dea.transcript.trp.core.model.beans.pagecontent");
		return JAXBContext.newInstance(clazzes);
	}
	
	private static Class<?>[] merge(Class<?> clazz, Class<?>... nestedClasses) throws JAXBException {
		Class<?>[] targetClasses;
		if(nestedClasses == null || nestedClasses.length == 0){
			targetClasses = new Class[]{clazz};
		} else { //merge into new array
			targetClasses = new Class[nestedClasses.length+1];
			targetClasses[0] = clazz;
			for(int i = 0; i < nestedClasses.length;){
				targetClasses[i+1] = nestedClasses[i++];
			}
		}
		return targetClasses;
	}

	public static <T> T unmarshal(URL url, Class<T> targetClass, Class<?>... nestedClasses) throws JAXBException, IOException {
		try (InputStream is = url.openStream()) {
			return unmarshal(is, targetClass, nestedClasses);
		}
	}
	
	public static <T> T unmarshal(File file, Class<T> targetClass, Class<?>... nestedClasses) throws JAXBException, FileNotFoundException {
		InputStream is = new FileInputStream(file);
		try {
			return unmarshal(is, targetClass, nestedClasses);
		} finally {
			try {
				is.close();
			} catch(IOException ioe) {
				logger.error("Could not close input stream on file: " + file.getAbsolutePath(), ioe);
			}
		}
	}
	
	public static <T> T unmarshal(InputStream is, Class<T> targetClass, Class<?>... nestedClasses) throws JAXBException {
		Unmarshaller u = createUnmarshaller(targetClass, nestedClasses);
		
		@SuppressWarnings("unchecked")
		T object = (T) u.unmarshal(is);
		return object;
	}
	
	/** This is inefficient (but it works) */
	public static <T> T unmarshal2(InputStream is, Class<T> targetClass, boolean namespaceAware, boolean doValidate, Class<?>... nestedClasses) throws JAXBException, ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(namespaceAware);
		dbf.setValidating(doValidate);
		if (doValidate) {
			dbf.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", XMLConstants.W3C_XML_SCHEMA_NS_URI);
		}
		DocumentBuilder builder = dbf.newDocumentBuilder();
		Document doc = builder.parse(is);
		
		Unmarshaller u = createUnmarshaller(targetClass, nestedClasses);
		return (T) u.unmarshal(doc.getDocumentElement());
	}
	
	public static <T> Unmarshaller createUnmarshaller(Class<T> targetClass, Class<?>... nestedClasses) throws JAXBException {
		JAXBContext jc = createJAXBContext(merge(targetClass, nestedClasses));
		return jc.createUnmarshaller();
	}
	
	public static <T> ValidationEvent[] marshalToStream(T object, OutputStream out, Class<?>... nestedClasses) throws JAXBException {
		return marshalToStream(object, out, true, nestedClasses);
	}
	
	public static <T> ValidationEvent[] marshalToStream(T object, OutputStream out, 
			boolean doFormatting, Class<?>... nestedClasses) throws JAXBException {
		return marshalToStream(object, out, doFormatting, MarshallerType.XML, nestedClasses);
	}
	
	/**
	 * Marshal object to stream.
	 * 
	 * FIXME
	 * This method support JSON, but the representation does not match the one produced and accepted by Jersey! See {@link ExportParameters}Test
	 * 
	 * @param object
	 * @param out
	 * @param doFormatting
	 * @param type
	 * @param nestedClasses
	 * @return
	 * @throws JAXBException
	 */
	private static <T> ValidationEvent[] marshalToStream(T object, OutputStream out, 
			boolean doFormatting, MarshallerType type, Class<?>... nestedClasses) throws JAXBException {
		ValidationEventCollector vec = new ValidationEventCollector();

		if(type == null) {
			type = MarshallerType.XML;
		}
		final Marshaller marshaller;
		switch(type) {
		case JSON:
			marshaller = createJsonMarshaller(object, doFormatting, nestedClasses);
			break;
		default:
			marshaller = createXmlMarshaller(object, doFormatting, nestedClasses);
			break;
		}
//		marshaller = createXmlMarshaller(object, doFormatting, nestedClasses);
		marshaller.setEventHandler(vec);
		marshaller.marshal(object, out);
		
		checkEvents(vec);
		
		return vec.getEvents();
	}
	
	private static <T> Marshaller createXmlMarshaller(T object, boolean doFormatting, Class<?>... nestedClasses) throws JAXBException {
		Class<?>[] targetClasses = merge(object.getClass(), nestedClasses);
		JAXBContext jc = createJAXBContext(targetClasses);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, doFormatting);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		logger.debug("Using Marshaller of type: " + marshaller.getClass().getName());
		
		XmlFormat format = XmlFormat.resolveFromClazz(object.getClass());
		if(format != null && !format.equals(XmlFormat.UNKNOWN)) {
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, format.xsiSchemaLocation);
		}
		logger.debug(marshaller.getClass().getCanonicalName());
		return marshaller;
	}
	
	public static <T> T unmarshal(Document doc, Class<T> targetClass, Class<?>... nestedClasses) throws JAXBException {
		JAXBContext jc = createJAXBContext(merge(targetClass, nestedClasses));
		Unmarshaller u = jc.createUnmarshaller();
		@SuppressWarnings("unchecked")
		T object = (T) u.unmarshal(doc.getDocumentElement());
		return object;
	}
	
	public static <T> File marshalToFile(T object, File fileOut, Class<?>... nestedClasses) throws JAXBException, FileNotFoundException {
		FileOutputStream out = new FileOutputStream(fileOut);
		try {
			marshalToStream(object, out, nestedClasses);
		} finally {
			try {
				out.close();
			} catch(IOException ioe) {
				logger.error("Could not close output stream on file: " + fileOut.getAbsolutePath(), ioe);
			}
		}
		return fileOut;
	}
	
	public static <T> void marshalToSysOut(T object, Class<?>... nestedClasses) throws JAXBException {
			marshalToStream(object, System.out, nestedClasses);
	}
	
	public static <T> T unmarshal(String objectStr, Class<T> targetClass, Class<?>... nestedClasses) throws JAXBException {
		JAXBContext jc = createJAXBContext(merge(targetClass, nestedClasses));
		Unmarshaller u = jc.createUnmarshaller();
		StringReader sr = new StringReader(objectStr);
		@SuppressWarnings("unchecked")
		T object = (T) u.unmarshal(sr);
		return object;
	}

	public static <T> String marshalToString(T object, Class<?>... nestedClasses) throws JAXBException {		
		return marshalToString(object, true, nestedClasses);
	}
	
	public static <T> String marshalToString(T object, boolean doFormatting, Class<?>... nestedClasses) throws JAXBException {		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			marshalToStream(object, baos, doFormatting, nestedClasses);
			return baos.toString();	
		} finally {
			try {
				baos.close();
			} catch (IOException e) {
				logger.warn("A ByteArrayOutputStream used for marshalling could not be closed!", e);
			}
		}
	}
	
	public static <T> byte[] marshalToBytes(T object, Class<?>... nestedClasses) throws JAXBException {
		byte[] data;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try{
			try{
				marshalToStream(object, out, nestedClasses);
				data = out.toByteArray();
			} finally {
				out.close();
			}
		} catch (Exception e){
			throw new MarshalException(e);
		}
		
		return data;
	}
	
	public static <T> T transformToObject(File file, final String xslID, Class<T> targetClass, Class<?>... nestedClasses) throws TransformerException, SAXException, IOException, ParserConfigurationException, JAXBException{
		JAXBContext jc = createJAXBContext(merge(targetClass, nestedClasses));
		JAXBResult result = new JAXBResult(jc);  
		XslTransformer.transform(file, xslID, result);
		// obtain the unmarshalled content tree
		@SuppressWarnings("unchecked")
		T object = (T) result.getResult();
		return object;
	}
	
	public static <T> T transformToObject(File file, final String xslID, Map<String, Object> xslParams, Class<T> targetClass, Class<?>... nestedClasses) throws TransformerException, SAXException, IOException, ParserConfigurationException, JAXBException{
		JAXBContext jc = createJAXBContext(merge(targetClass, nestedClasses));
		JAXBResult result = new JAXBResult(jc);  
		XslTransformer.transform(file, xslID, result, xslParams);
		// obtain the unmarshalled content tree
		@SuppressWarnings("unchecked")
		T object = (T) result.getResult();
		return object;
	}

	public static <T> T clone(T object, Class<?>... nestedClasses) throws JAXBException {
		byte[] data = marshalToBytes(object, nestedClasses);
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		try {
			Object newO = unmarshal(in, object.getClass(), nestedClasses);
			@SuppressWarnings("unchecked")
			T newT = (T)newO;
			return newT;
		} finally {
			try {
				in.close();
			} catch(IOException ioe) {
				logger.error("Could not close input stream on byte array", ioe);
			}
		}
	}
	
	private static void checkEvents(ValidationEventCollector vec) {
		if (vec.hasEvents()) {
			logger.info("Events occured while marshalling xml file: " + vec.getEvents().length);
			ValidationEvent[] events = vec.getEvents();
			for(ValidationEvent e : events){
				logger.info(e.getMessage());
			}
		} else {
			logger.debug("No events occured during marshalling xml file!");
		}
	}
	
	public static XMLGregorianCalendar getXmlCalendar(Date date){
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		XMLGregorianCalendar xmlCal = null;
		try {
			xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
		} catch (DatatypeConfigurationException e) {
			logger.error("DatatypeFactory:  implementation is not available or cannot be instantiated. CreateDate will be null!", e);
		}
		return xmlCal;
	}
	
	/**
	 * Experimental method for producing JSON representations similar to the ones the HTTP API uses.
	 * <br><br>
	 * TODO: check usage of the method {@link MOXyJsonProvider#writeTo(Object, Class, java.lang.reflect.Type, java.lang.annotation.Annotation[], MediaType, javax.ws.rs.core.MultivaluedMap, OutputStream)}
	 * 
	 * @param object
	 * @param doFormatting
	 * @return
	 * @throws JAXBException
	 */
	@Deprecated
	public static <T> String marshalToJsonStringMOXy(T object, boolean doFormatting) throws JAXBException {
		MOXyJsonProvider prov = new MOXyJsonProvider();
		prov.setFormattedOutput(doFormatting);
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			//FIXME the JSON representation differs when using JAXB with moxy context factory or using MOXyJsonProvider!
			prov.writeTo(object, object.getClass(), null, null, MediaType.APPLICATION_JSON_TYPE, null, baos);
			return new String(baos.toByteArray(), DeaFileUtils.DEFAULT_CHARSET);
		} catch (IOException e) {
			throw new JAXBException("Could not marshal object of type " + object.getClass() + " to JSON.", e);
		}
	}
	
	/**
	 * Experimental method for reading the JSON representation produced by the MOXyJsonProvider.
	 * 
	 * FIXME: this does not work, but at least it has localized error messages. 
	 * throws DOMException: NAMESPACE_ERR: Es wurde versucht, ein Objekt auf eine Weise zu erstellen oder zu ändern, die falsch in Bezug auf Namespaces ist.
	 * 
	 * @param str
	 * @param targetClass
	 * @param nestedClasses
	 * @return
	 * @throws JAXBException
	 */
	@Deprecated
	public static <T> T unmarshalJsonMOXy(String str, Class<Object> targetClass, Class<?>... nestedClasses) throws JAXBException {
		MOXyJsonProvider prov = new MOXyJsonProvider();
		try(ByteArrayInputStream bais = new ByteArrayInputStream(str.getBytes())) {
			@SuppressWarnings("unchecked")
			T newT = (T) prov.readFrom(targetClass, null, targetClass.getDeclaredAnnotations(), MediaType.TEXT_PLAIN_TYPE, 
					null, bais);
			return newT;
		} catch (IOException e) {
			throw new JAXBException("Could not marshal object of type " + targetClass.getClass() + " to JSON.", e);
		}
	}
	
	/**
	 * Experimental method for producing JSON with MOXy via JAXB.
	 * 
	 * This seems to work fine in combination with {@link #unmarshalJsonWithJaxb(String, Class, Class...)} 
	 * 
	 * @param object
	 * @param doFormatting
	 * @return
	 * @throws JAXBException
	 */
	public static <T> String marshalToJsonString(T object, boolean doFormatting) throws JAXBException {
		MOXyJsonProvider prov = new MOXyJsonProvider();
		prov.setFormattedOutput(doFormatting);
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			marshalToStream(object, baos, doFormatting, MarshallerType.JSON, object.getClass());
			return new String(baos.toByteArray(), DeaFileUtils.DEFAULT_CHARSET);
		} catch (IOException e) {
			throw new JAXBException("Could not marshal object of type " + object.getClass() + " to JSON.", e);
		}
	}
	
	/**
	 * Experimental method for loading an object from a JSON String.
	 * Seems to work with {@link #marshalToJsonStringWithJaxb(Object, boolean)}
	 * 
	 * @param str
	 * @param targetClass
	 * @param nestedClasses
	 * @return
	 * @throws JAXBException
	 */
	public static <T> T unmarshalJson(String str, Class<T> targetClass, Class<?>... nestedClasses) throws JAXBException {
		//this part only works for the representations produced via the JAXB API
		Class<?>[] targetClasses = merge(targetClass, nestedClasses);
		JAXBContext jc = org.eclipse.persistence.jaxb.JAXBContextFactory.createContext(targetClasses, Collections.<String,Object>emptyMap());
		Unmarshaller u = jc.createUnmarshaller();
		u.setProperty(UnmarshallerProperties.MEDIA_TYPE, "application/json");
		u.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
		
		StringReader sr = new StringReader(str);
//		@SuppressWarnings("unchecked")
//		T object = (T) u.unmarshal(sr);
//		return object;
		
		StreamSource source = new StreamSource(sr);
		JAXBElement<T> element = u.unmarshal(source, targetClass);

		return element.getValue();
	}
	
	/**
	 * Do not use. The JSON representations produced will differ from the ones the API produces/accepts!
	 * 
	 * @param object
	 * @param doFormatting
	 * @param nestedClasses
	 * @return
	 * @throws JAXBException
	 */
	private static <T> Marshaller createJsonMarshaller(T object, boolean doFormatting, Class<?>... nestedClasses) throws JAXBException {
		Class<?>[] targetClasses = merge(object.getClass(), nestedClasses);
		JAXBContext jc = org.eclipse.persistence.jaxb.JAXBContextFactory.createContext(targetClasses, Collections.<String,Object>emptyMap());
		Marshaller marshaller = jc.createMarshaller();
		// Set the Marshaller media type to JSON or XML		 
		marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, doFormatting);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        // If true then the JSON root element is included in the JSON output. Keep it false to make it compliant to the API
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        return marshaller;
	}
	
	public enum MarshallerType {
		XML,
		JSON;
	}
}
