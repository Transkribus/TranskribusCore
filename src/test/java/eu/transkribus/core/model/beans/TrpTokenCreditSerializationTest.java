package eu.transkribus.core.model.beans;

import java.util.Date;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.SebisStopWatch.SSW;

public class TrpTokenCreditSerializationTest {
	private static final Logger logger = LoggerFactory.getLogger(TrpTokenCreditSerializationTest.class);
	
	@Test
	public void test() throws JAXBException, JsonProcessingException {
		TrpCreditProduct p = new TrpCreditProduct();
		p.setCreditType("HTR");
		p.setLabel("Test Product");
		p.setNrOfCredits(1543);
		p.setShareable(true);
		p.setSubscription("Monthly");
		
		TrpCreditPackage c = new TrpCreditPackage();
		c.setActive(true);
		c.setBalance(1543d);
		c.setExpirationDate(new Date());
		c.setPurchaseDate(new Date());
		c.setUserId(43);
		
		c.setProduct(p);
		
		SSW sw = new SSW();
		sw.start();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String gsonOut = gson.toJson(c);
		long time = sw.stop(false);
		logger.info("GSON ({} ms) says:\n\n{}\n\n", time, gsonOut);
		
		sw.start();
		String moxyOut = JaxbUtils.marshalToJsonString(c, true);
		time = sw.stop(false);
		logger.info("Moxy ({} ms) says:\n\n{}\n\n", time, moxyOut);
		
		sw.start();
		ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
		String jacksonOut = writer.writeValueAsString(c);
		time = sw.stop(false);
		logger.info("Jackson ({} ms) says:\n\n{}\n\n", time, jacksonOut);
	}
	
}
