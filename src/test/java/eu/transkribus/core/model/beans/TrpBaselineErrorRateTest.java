package eu.transkribus.core.model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.rest.ParameterMap;
import eu.transkribus.core.util.JaxbUtils;

public class TrpBaselineErrorRateTest {
	private static final Logger logger = LoggerFactory.getLogger(TrpBaselineErrorRateTest.class);

	@Test
	public void testMarshalAndUnmarshal() throws JAXBException {
		ParameterMap m = new ParameterMap();
		Map<String, String> entries = new HashMap<>();
		entries.put("key1", "value1");
		entries.put("key2", "value2");
		m.setParamMap(entries);
		
		TrpBaselineErrorRate err = new TrpBaselineErrorRate();
		err.setParams(m);
		err.setPrecision(0.1);
		err.setRecall(0.2);
		List<TrpBaselineErrorRateListEntry> list = new ArrayList<>();
		double[][] vals = new double[][] {
			{ 0.9, 0.8 },
			{ 0.7, 0.6 },
			{ 0.5, 0.4 },
		};
		for (int i=0; i<3; ++i) {
			TrpBaselineErrorRateListEntry e = new TrpBaselineErrorRateListEntry();
			e.setPageNumber(i+1);
			e.setPrecision(vals[i][0]);
			e.setRecall(vals[i][1]);
			list.add(e);
		}
		err.setList(list);
		
//		return JaxbUtils.marshalToString(err, false, TrpBaselineErrorRate.class, TrpBaselineErrorRateListEntry.class);
//		String errStr = JaxbUtils.marshalToString(err, false, TrpBaselineErrorRate.class);
		String errStr = JaxbUtils.marshalToString(err, false);
		logger.info("errStr = "+errStr);
		
		TrpBaselineErrorRate err2 = JaxbUtils.unmarshal(errStr, TrpBaselineErrorRate.class);
		String errStr2 = JaxbUtils.marshalToString(err2, false);
		logger.info("errStr2 = "+errStr2);
//		logger.info("err2 params = "+err2.getParams().equals(m)+" - "+err2.getPrRcF1Str());
		
		Assert.assertEquals("Unmarshalled object not equal to original one!", err, err2);
		Assert.assertEquals("Marshalled strings not equal!", errStr, errStr2);
	}

}
