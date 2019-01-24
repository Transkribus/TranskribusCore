package eu.transkribus.core.model.beans;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.rest.ParameterMap;
import eu.transkribus.core.util.JaxbUtils;

public class TrpErrorRateTest {
	private final static Logger logger = LoggerFactory.getLogger(TrpErrorRateTest.class);
	@Test
	public void testErrorRateBean() throws JAXBException {
		TrpErrorRate e = new TrpErrorRate();
		ParameterMap map = new ParameterMap();
		map.addBoolParam("test", true);
		e.setParams(map);
		e.setwAcc("1213421%");
		e.setBagTokensF("122");
		e.setBagTokensPrec("1");
		e.setBagTokensRec("45");
		
		
		logger.info(JaxbUtils.marshalToString(e, ParameterMap.class));
	}
}
