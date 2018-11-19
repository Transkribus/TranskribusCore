package eu.transkribus.core.model.beans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.DocumentSelectionDescriptor.PageDescriptor;
import eu.transkribus.core.util.HtrCITlabUtils;
import eu.transkribus.core.util.JaxbUtils;

public class HtrTrainConfigTest {
	private static final Logger logger = LoggerFactory.getLogger(HtrTrainConfigTest.class);
	
	public static void main(String[] args) throws JAXBException{
		CitLabHtrTrainConfig htc = createConfig();
		
//		System.out.println(JaxbUtils.marshalToString(htc, DocumentSelectionDescriptor.class, PageDescriptor.class));
		Properties props = new Properties();
		props.put("test", JaxbUtils.marshalToString(htc, DocumentSelectionDescriptor.class, PageDescriptor.class));
		props.put("test2", "test2");
		
		try {
			props.store(new FileOutputStream(new File("/tmp/test.properties")), null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Properties props2 = new Properties();
		try {
			props2.load(new FileReader(new File("/tmp/test.properties")));
			
			String objectStr = (String)props2.get("test");
			CitLabHtrTrainConfig config2 = JaxbUtils.unmarshal(objectStr, CitLabHtrTrainConfig.class, DocumentSelectionDescriptor.class, PageDescriptor.class);
			System.out.println(config2.getDescription());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static CitLabHtrTrainConfig createConfig() {
		CitLabHtrTrainConfig htc = new CitLabHtrTrainConfig();
		
		htc.setColId(2);
		htc.setDescription("A description");
		htc.setLanguage("German");
		htc.setLearningRate("2e-3");
		htc.setNoise("both");
		htc.setNumEpochs(200);
		htc.setTrainSizePerEpoch(1000);
		htc.setModelName("Test Model");
		
		for(int i = 1; i < 3; i++) {
			DocumentSelectionDescriptor d = new DocumentSelectionDescriptor();
			d.setDocId(i);
			for(int j = 1; j < 3; j++) {
				PageDescriptor p = new PageDescriptor();
				p.setPageId(j);
				p.setTsId(j);
				d.getPages().add(p);
			}
			htc.getTrain().add(d);
		}
		return htc;
	}
	
	private static CitLabHtrTrainConfig createPlusConfig() {
		CitLabHtrTrainConfig htc = new CitLabHtrTrainConfig();
		
		htc.setColId(2);
		htc.setDescription("A description");
		htc.setLanguage("German");
		htc.setLearningRate("2e-3");
		htc.setNoise("both");
		htc.setNumEpochs(200);
		htc.setTrainSizePerEpoch(1000);
		htc.setProvider(HtrCITlabUtils.PROVIDER_CITLAB_PLUS);
		htc.setModelName("Test Model");
		
		for(int i = 1; i < 3; i++) {
			DocumentSelectionDescriptor d = new DocumentSelectionDescriptor();
			d.setDocId(i);
			for(int j = 1; j < 3; j++) {
				PageDescriptor p = new PageDescriptor();
				p.setPageId(j);
				p.setTsId(j);
				d.getPages().add(p);
			}
			htc.getTrain().add(d);
		}
		return htc;
	}
	
	@Test
	public void testConfig() throws JAXBException {
		CitLabHtrTrainConfig htc = createPlusConfig();
		
		logger.info(JaxbUtils.marshalToString(htc, DocumentSelectionDescriptor.class, PageDescriptor.class));
		logger.info(JaxbUtils.marshalToJsonString(htc, true));
	}
}
