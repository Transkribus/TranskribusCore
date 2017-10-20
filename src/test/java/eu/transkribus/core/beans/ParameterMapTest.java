package eu.transkribus.core.beans;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.job.ParameterMap;
import eu.transkribus.core.util.JaxbUtils;

public class ParameterMapTest {
	private static final Logger logger = LoggerFactory.getLogger(ParameterMapTest.class);
	
	@Test
	public void testMarshal() throws JAXBException {
		ParameterMap map = new ParameterMap();
		map.addParameter("test", "testValue");
		
		System.out.println(JaxbUtils.marshalToString(map));
	}
}
