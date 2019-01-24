package eu.transkribus.core.model.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.builder.CommonExportPars;
import eu.transkribus.core.model.builder.alto.AltoExportPars;
import eu.transkribus.core.model.builder.docx.DocxExportPars;
import eu.transkribus.core.model.builder.pdf.PdfExportPars;
import eu.transkribus.core.model.builder.tei.TeiExportPars;
import eu.transkribus.core.rest.JobConst;
import eu.transkribus.core.util.GsonUtil;
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
		try {
			ExportParameters unmarshalled = JaxbUtils.unmarshalJson(marshalled, ExportParameters.class, DocumentSelectionDescriptor.class);
			logger.debug("Unmarshalled object:\n" + unmarshalled);
			Assert.assertNotNull("Could not unmarshal object from JSON String!", unmarshalled);			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Checks equivalence of ExportParameters object values vs. the legacy JSON String after (un)marshalling 
	 * 
	 * @throws JAXBException
	 */
	@Test
	public void testNewFormat() throws JAXBException {
		ExportParameters params = createTestParams();
		String paramsStr = JaxbUtils.marshalToJsonString(params, false);
		String jsonParsStr = createLegacyJsonString(params);
		
		logger.info("Old format:\n" + jsonParsStr);
		logger.info("\n\nNew format:\n" + paramsStr);
		
		ExportParameters paramsUnmarshalled = JaxbUtils.unmarshalJson(paramsStr, ExportParameters.class, DocumentSelectionDescriptor.class);
		
		Map<String, Object> mapOfPars = GsonUtil.toMap2(jsonParsStr);
		
		Assert.assertEquals(GsonUtil.fromJson((String)mapOfPars.get(CommonExportPars.PARAMETER_KEY), CommonExportPars.class),
				paramsUnmarshalled.getCommonPars());
		
		Assert.assertEquals(GsonUtil.fromJson((String)mapOfPars.get(AltoExportPars.PARAMETER_KEY), AltoExportPars.class),
				paramsUnmarshalled.getAltoPars());
		
		Assert.assertEquals(GsonUtil.fromJson((String)mapOfPars.get(PdfExportPars.PARAMETER_KEY), PdfExportPars.class),
				paramsUnmarshalled.getPdfPars());
		
		Assert.assertEquals(GsonUtil.fromJson((String)mapOfPars.get(TeiExportPars.PARAMETER_KEY), TeiExportPars.class),
				paramsUnmarshalled.getTeiPars());
		
		Assert.assertEquals(GsonUtil.fromJson((String)mapOfPars.get(DocxExportPars.PARAMETER_KEY), DocxExportPars.class),
				paramsUnmarshalled.getDocxPars());
	}
	
	@Test
	public void testDetermineFormat() throws JAXBException {
		ExportParameters params = createTestParams();
		String paramsStr = JaxbUtils.marshalToJsonString(params, false);
		String jsonParsStr = createLegacyJsonString(params);
		
		Map<String, Object> mapOfPars = GsonUtil.toMap2(jsonParsStr);
		Map<String, Object> mapOfPars2 = GsonUtil.toMap2(paramsStr);
		for(Entry<String, Object> e : mapOfPars2.entrySet()) {
			logger.info(e.getKey() + " -> " + e.getValue());
		}
		logger.debug("Legacy map class (should be String) = " + mapOfPars.get(CommonExportPars.PARAMETER_KEY).getClass());
		logger.debug("When reading the ExportParameters class (is a Gson object) = " + mapOfPars2.get(CommonExportPars.PARAMETER_KEY).getClass());

		Assert.assertFalse("Validation method erroneously accepted legacy object!", isExportParameterObject(jsonParsStr));
		Assert.assertTrue("Validation method erroneously rejected parameter object!", isExportParameterObject(paramsStr));
	}
	
	/**
	 * This method is in use in the API to determine the format
	 * 
	 * @param jsonStr
	 * @return
	 */
	private boolean isExportParameterObject(String jsonStr) {
		if(jsonStr == null) {
			return false;
		}
		Map<String, Object> mapOfPars = GsonUtil.toMap2(jsonStr);
		Object o = mapOfPars.get(CommonExportPars.PARAMETER_KEY);
		return !(o instanceof String);
	}

	private ExportParameters createTestParams() {
		CommonExportPars commonPars = new CommonExportPars();
		commonPars.setDoWriteMets(true);
		
		AltoExportPars altoPars = new AltoExportPars();
		PdfExportPars pdfPars = new PdfExportPars();
		TeiExportPars teiPars = new TeiExportPars();
		DocxExportPars docxPars = new DocxExportPars();
		
		DocumentSelectionDescriptor dsd = new DocumentSelectionDescriptor(68, 1234);
		DocumentSelectionDescriptor dsd2 = new DocumentSelectionDescriptor(69, 12345);
		List<DocumentSelectionDescriptor> dsds = new ArrayList<>();
		dsds.add(dsd);
		dsds.add(dsd2);
		return createTestParams(dsds, commonPars, altoPars, pdfPars, teiPars, docxPars);
	}
	
	
	public ExportParameters createTestParams(List<DocumentSelectionDescriptor> dsds,			
			CommonExportPars commonPars,
			AltoExportPars altoPars,
			PdfExportPars pdfPars,
			TeiExportPars teiPars,
			DocxExportPars docxPars
			) {
				
		ExportParameters params = new ExportParameters();
		params.setCommonPars(commonPars);
		params.setAltoPars(altoPars);
		params.setPdfPars(pdfPars);
		params.setTeiPars(teiPars);
		params.setDocxPars(docxPars);
		params.setDocDescriptorList(dsds);
				
		return params;
	}
	
	
	public String createLegacyJsonString(ExportParameters params) {
				
		Map<String, String> parAsJsonMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(params.getDocDescriptorList())) {
			parAsJsonMap.put(JobConst.PROP_DOC_DESCS, GsonUtil.toJson(params.getDocDescriptorList()));
		}
		if (params.getCommonPars() != null) {
			parAsJsonMap.put(CommonExportPars.PARAMETER_KEY, GsonUtil.toJson(params.getCommonPars()));
		}
		if (params.getAltoPars() != null) {
			parAsJsonMap.put(AltoExportPars.PARAMETER_KEY, GsonUtil.toJson(params.getAltoPars()));
		}
		if (params.getPdfPars() != null) {
			parAsJsonMap.put(PdfExportPars.PARAMETER_KEY, GsonUtil.toJson(params.getPdfPars()));
		}
		if (params.getTeiPars() != null) {
			parAsJsonMap.put(TeiExportPars.PARAMETER_KEY, GsonUtil.toJson(params.getTeiPars()));
		}
		if (params.getDocxPars() != null) {
			parAsJsonMap.put(DocxExportPars.PARAMETER_KEY, GsonUtil.toJson(params.getDocxPars()));
		}
				
		return GsonUtil.toJson(parAsJsonMap);
	}
}
