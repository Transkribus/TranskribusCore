package eu.transkribus.core.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author albert
 * @author philip
 * 
 */
public class XslTransformer {
	private static Logger logger = LoggerFactory.getLogger(XslTransformer.class);
	
	public static File transform(File sourceFile, String xslID, File resultFile) throws ParserConfigurationException, SAXException, IOException, TransformerException {
		Document doc = XmlUtils.parseDomFromFile(sourceFile, false);
		return transform(doc, xslID, resultFile);
	}
	
	/**
	 * Show simple transformation from input stream to output stream. Taken from
	 * Saxon samples (slightly modified)
	 * 
	 * @param sourceID
	 *            file name of the source file
	 * @param xslID
	 *            file name of the stylesheet file
	 * @param result
	 *            the path where the generated file should be written to
	 */
	public static File transform(Document sourceXML, String xslID, File resultFile) throws TransformerException,
			FileNotFoundException {
		final long start = System.currentTimeMillis();
		// Transform the source XML to to a file
		FileOutputStream fos = new FileOutputStream(resultFile);
		PrintWriter pw = new PrintWriter(fos);
		StreamResult result = new StreamResult(pw);
		result = (StreamResult) transform(sourceXML, xslID, result, null);
		pw.flush();
		pw.close();
		try{
			fos.close();
		} catch (IOException e) {
			logger.debug("Closing File FileOutputStream caused " + e.getMessage());
			e.printStackTrace();
		}
		final long stop = System.currentTimeMillis();
		logger.debug("time for transforming: " + getSeconds(start, stop) + " seconds");
		return resultFile;
	}

	/**
	 * Show simple transformation from input stream to output stream. Taken from
	 * Saxon samples (slightly modified)
	 * 
	 * @param sourceID
	 *            file name of the source file
	 * @param xslID
	 *            file name of the stylesheet file
	 * @param result
	 *            the path where the generated file should be written to
	 */
	public static String transformToString(Document sourceXML, String xslID) throws TransformerException, FileNotFoundException {
		final long start = System.currentTimeMillis();
		StreamResult result = new StreamResult(new StringWriter());
		result = (StreamResult) transform(sourceXML, xslID, result, null);
		
		final long stop = System.currentTimeMillis();
		logger.debug("time for transforming: " + getSeconds(start, stop) + " seconds");
		return result.getWriter().toString();
	}

	public static Result transform(File xmlFile, String xslFileResource, Result result, Map<String, Object> params) throws TransformerException, SAXException, IOException, ParserConfigurationException{
		Document doc = XmlUtils.parseDomFromFile(xmlFile, false);
		return transform(doc, xslFileResource, result, params);
	}
	
	public static Result transform(File xmlFile, String xslFileResource, Result result) throws TransformerException, SAXException, IOException, ParserConfigurationException{
		Document doc = XmlUtils.parseDomFromFile(xmlFile, false);
		return transform(doc, xslFileResource, result, null);
	}
	
	public static Result transform(Document sourceXML, String xslFileResource, Result result)
			throws TransformerException, FileNotFoundException {
		return transform(sourceXML, xslFileResource, result, null);
	}
	
	/**
	 * Show simple transformation from input stream to output stream. Taken from
	 * Saxon samples (slightly modified)
	 * 
	 * @param sourceID
	 *            file name of the source file
	 * @param xslFileResource
	 *            file name of the stylesheet file
	 * @param result
	 *            the path where the generated file should be written to
	 */
	public static Result transform(Document sourceXML, String xslFileResource, Result result, Map<String, Object> params)
			throws TransformerException, FileNotFoundException {

		// Create a transform factory instance.
		TransformerFactory tfactory = TransformerFactory.newInstance();
		InputStream is = XslTransformer.class.getClassLoader().getResourceAsStream(xslFileResource);
//		InputStream xslIS = new BufferedInputStream(new FileInputStream(xslID));
		InputStream xslIS = new BufferedInputStream(is);
		StreamSource xslSource = new StreamSource(xslIS);

		// Create a transformer for the stylesheet.

		DOMSource dom = new DOMSource(sourceXML);
		javax.xml.transform.Transformer transformer = tfactory.newTransformer(xslSource);
		
		if(params != null && !params.entrySet().isEmpty()){
			for(Entry<String, Object> e : params.entrySet()){
				transformer.setParameter(e.getKey(), e.getValue());
			}
		}
		
		transformer.transform(dom, result);
		return result;
	}
	
	private static float getSeconds(long start, long stop){
		return (float) (stop - start) / 1000;
	}
}
