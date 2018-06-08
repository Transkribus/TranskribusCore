package eu.transkribus.core.model.beans.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.util.ValidationEventCollector;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.util.PageXmlUtils;

public class PcGtsTypeMessageBodyReaderTest {
	private static final Logger logger = LoggerFactory.getLogger(PcGtsTypeMessageBodyReaderTest.class);
	@Test
	public void testDamagedXml() throws FileNotFoundException, IOException, JAXBException {
		File f = new File("/media/daten/Dokumente/Bentham_batch2_test/page/116_630_001.xml");
		
//		PcGtsTypeMessageBodyReader reader = new PcGtsTypeMessageBodyReader();
//		
//		reader.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream)
		try(FileInputStream stream = new FileInputStream(f)) {
			
			ValidationEventCollector vec = new ValidationEventCollector();
			PcGtsType pc =  PageXmlUtils.unmarshal(stream, vec);
			
			for(ValidationEvent e : vec.getEvents()) {
				logger.info("Has validation event with severity: " + e.getSeverity());
			}
			
//			List<TextLineType> lines = PageXmlUtils.getLines(pc);
//			logger.info("has lines: " + lines.size());
//			for(TextLineType l : lines) {
//				logger.debug(l.getId());
//			}
		}
		
		
	}

}
