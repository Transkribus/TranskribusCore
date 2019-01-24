package eu.transkribus.core.util;

import java.io.File;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextProperties;

import eu.transkribus.core.model.beans.searchresult.KeywordSearchResult;
import eu.transkribus.core.model.beans.searchresult.KwPage;
public class KwsJSONUtils {
	
	
	public static KwPage getFromFile(File file) throws JAXBException{
		
//		Sets system variable to prevent the need for a new properties file just for this...
		System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
		
		JAXBContext jc = JAXBContext.newInstance(KwPage.class);
		Unmarshaller ums = jc.createUnmarshaller();
		ums.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
		ums.setProperty(JAXBContextProperties.JSON_INCLUDE_ROOT, true);
		
		
		KwPage page = (KwPage) ums.unmarshal(file);
		
		return page;
	}
	
	public static String searchResultToXML(KeywordSearchResult result) throws JAXBException{
		
		JAXBContext jc = JAXBContext.newInstance(KeywordSearchResult.class);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		StringWriter sw = new StringWriter();
		m.marshal(result, sw);
		String xmlString = sw.toString();
		
		return xmlString;
		
		
	}
	
	public static String searchResultToJSON(KeywordSearchResult result) throws JAXBException{
		System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
		JAXBContext jc = JAXBContext.newInstance(KeywordSearchResult.class);
		Marshaller m = jc.createMarshaller();
		m.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
		m.setProperty(JAXBContextProperties.JSON_INCLUDE_ROOT, true);
		
		StringWriter sw = new StringWriter();
		m.marshal(result, sw);
		String jsonString = sw.toString();
		
		return jsonString;
		
		
	}


}
