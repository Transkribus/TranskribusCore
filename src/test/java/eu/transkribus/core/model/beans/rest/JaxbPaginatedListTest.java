package eu.transkribus.core.model.beans.rest;

import java.io.IOException;
import java.net.URL;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.TrpCreditPackage;
import eu.transkribus.core.util.JaxbUtils;

public class JaxbPaginatedListTest {
	private static final Logger logger = LoggerFactory.getLogger(JaxbPaginatedListTest.class);
	
//	@Test
	public void testReadFromJson() throws IOException, JAXBException {
		URL resource = this.getClass().getClassLoader().getResource("PageXmlUtilsTest/creditList.json");
		String jsonStr = FileUtils.readFileToString(FileUtils.toFile(resource));
		logger.debug(jsonStr);
		TrpCreditPackageList items = JaxbUtils.unmarshalJson(jsonStr, TrpCreditPackageList.class, TrpCreditPackage.class);
		
		logger.debug("Loaded page. Type = " + items.getClass());
		logger.debug("Total count = {}", items.getTotal());
		logger.debug("List size = {}", items.getList().size());
		//the next line fails with: java.lang.ClassCastException: com.sun.org.apache.xerces.internal.dom.ElementNSImpl cannot be cast to eu.transkribus.core.model.beans.TrpCreditPackage
		items.getList().stream().map(e -> "" + e).forEach(logger::debug);
	}
}
