package eu.transkribus.core.util;

import java.io.IOException;
import java.util.Properties;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.HtrTrainConfig;
import eu.transkribus.core.model.beans.rest.ParameterMap;

public class JaxbTest {
	private static final Logger logger = LoggerFactory.getLogger(JaxbTest.class);

	@Test
	public void testJson() throws JAXBException, WebApplicationException, IOException {
		HtrTrainConfig config = new HtrTrainConfig();
		
		ParameterMap map = new ParameterMap();
		map.addParameter("test", "testValue");
		config.setCustomParams(map);
		
//		Properties props = new Properties();
//		props.setProperty("test", "testValue");
//		config.setParams(props);
		
		logger.info(JaxbUtils.marshalToString(config));
		
		String moxyStr = JaxbUtils.marshalToJsonString(config, true);
		logger.info("MOXy says:\n" + moxyStr);
		
		String jacksonStr = marshalToJacksonJsonString(config);
		logger.info("Jackson says:\n" + jacksonStr);
	}
	
	/**
	 * Produce a JSON String using Jackson. Representation is very different to MOXy so we can't switch easily as it will break clients.
	 * <br>add this to pom.xml for making it work but don't leave it there as it might break client/server:
	 * <pre>
		&lt;dependency&gt;
			&lt;groupId&gt;com.fasterxml.jackson.jaxrs&lt;/groupId&gt;
			&lt;artifactId&gt;jackson-jaxrs-json-provider&lt;/artifactId&gt;
			&lt;version&gt;2.9.0&lt;/version&gt;
		&lt;/dependency&gt;
		<pre>
	 * 
	 * 
	 * @param object
	 * @return
	 * @throws JAXBException
	 */
	public static <T> String marshalToJacksonJsonString(T object) throws JAXBException {
//		JacksonJsonProvider prov = new JacksonJsonProvider();
//		prov.enable(SerializationFeature.INDENT_OUTPUT);
//		try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//			prov.writeTo(object, object.getClass(), null, null, MediaType.APPLICATION_JSON_TYPE, null, baos);
//			return new String(baos.toByteArray(), DeaFileUtils.DEFAULT_CHARSET);
//		} catch (IOException e) {
//			throw new JAXBException("Could not marshal object of type " + object.getClass() + " to JSON.", e);
//		}
		return "N/A";
	}

}
