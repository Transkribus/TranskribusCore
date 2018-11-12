package eu.transkribus.core.model.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.builder.CommonExportPars;
import eu.transkribus.core.util.JaxbUtils;

public class ExportParametersTest {
	private static final Logger logger = LoggerFactory.getLogger(ExportParametersTest.class);
	
	@Test
	public void testMarshallingXML() throws JAXBException {
		ExportParameters params = createTestParams();
		
		String marshalled = JaxbUtils.marshalToString(params, DocumentSelectionDescriptor.class);
		logger.info("Marshalled export params:\n" + marshalled);
		
		ExportParameters unmarshalled = JaxbUtils.unmarshal(marshalled, ExportParameters.class, DocumentSelectionDescriptor.class);
		logger.info("Unmarshalled:\n" + unmarshalled);
	}
	
	@Test
	public void testMarshallingJSON() throws JAXBException {
		ExportParameters params = createTestParams();
		
		String marshalled = JaxbUtils.marshalToJsonString(params, true);
		logger.info("Marshalled JSON export params:\n" + marshalled);
	}

	private ExportParameters createTestParams() {
		ExportParameters params = new ExportParameters();
		CommonExportPars commonPars = new CommonExportPars();
		commonPars.setDoWriteMets(true);
		params.setCommonPars(commonPars);
		
		DocumentSelectionDescriptor dsd = new DocumentSelectionDescriptor(68, 1234);
		DocumentSelectionDescriptor dsd2 = new DocumentSelectionDescriptor(69, 12345);
		List<DocumentSelectionDescriptor> dsds = new ArrayList<>();
		dsds.add(dsd);
		dsds.add(dsd2);
		params.setDocDescriptorList(dsds);
		return params;
	}
}
