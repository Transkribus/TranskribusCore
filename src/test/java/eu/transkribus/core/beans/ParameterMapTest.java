package eu.transkribus.core.beans;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.util.TrpProperties;
import eu.transkribus.core.model.beans.job.KwsParameters;
import eu.transkribus.core.rest.JobConst;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.JobDataUtils;
import eu.transkribus.core.util.ParameterMap;

public class ParameterMapTest {
	private static final Logger logger = LoggerFactory.getLogger(ParameterMapTest.class);
	
	@Test
	public void testMarshal() throws JAXBException {
		ParameterMap map = new ParameterMap();
		map.addParameter("test", "testValue");
		
		System.out.println(JaxbUtils.marshalToString(map));
	}
	
	@Test
	public void testKwsParameters() throws JAXBException {
		KwsParameters map = buildKwsParameters();
		final String test = JaxbUtils.marshalToString(map);
		System.out.println(test);
		
		KwsParameters testMap = JaxbUtils.unmarshal(test, KwsParameters.class);
		Assert.assertTrue(testMap.isExpert());
	}

	@Test
	public void testTrpProperties() {
		TrpProperties props = new TrpProperties();
		KwsParameters map = buildKwsParameters();
		JobDataUtils.setParameterObject(props.getProperties(), JobConst.PROP_PARAMETERS, map);
		System.out.println(props.writeToString());
		KwsParameters testMap = JobDataUtils.getParameterObject(props.getProperties(), 
				JobConst.PROP_PARAMETERS, KwsParameters.class);
		System.out.println(testMap.toString());
		Assert.assertTrue(testMap.equals(map));
	}
	
	private static KwsParameters buildKwsParameters() {
		KwsParameters map = new KwsParameters();
		map.setExpert(true);
		map.setCaseSensitive(true);
		map.setMaxNrOfHits(2);
		map.setPartialMatching(true);
		map.setThreshold(0.5);
		map.setCustomParameter("SomeUndefinedParam", "someArbitraryValue");
		return map;
	}
}
