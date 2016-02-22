package eu.transkribus.core.model.beans.customtags;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.customtags.CustomTag;

public class CustomTagTest {
	private final static Logger logger = LoggerFactory.getLogger(CustomTagTest.class);

	@Test public void testSetAndMergeAttributes() throws IOException {
//		fail("Not yet implemented");
		
		CustomTag t1 = new CustomTag("testTag", 2, 4);
		CustomTag t2 = new CustomTag("testTag", 2, 4);
		
		logger.info("t1 = "+t1.toString());
		logger.info("t2 = "+t2.toString());
		
		t1.setAttribute("att1", "hello", true);
		t1.setAttribute("att2", "hi!", true);
		
		t2.setAttribute("att1", "hello", true);
		t2.setAttribute("att3", null, true);
		
		CustomTag t4 = new CustomTag("testTag");
		
		t4.setAttributes(t1, true);
		
		assertTrue("t4 == t1 because t4 got attributes from t1", t1.equalsEffectiveValues(t4, true));
		assertFalse("t2 != t1", t1.equalsEffectiveValues(t2, true));
		
		t2.mergeEqualAttributes(t1, true);
		
		CustomTag t5 = new CustomTag("testTag", 2, 4);
		t5.setAttribute("att1", "hello", true);
		
		logger.info("t2 = "+t2.toString());
		assertTrue("t2 == t1 with indices because got merged", t2.equalsEffectiveValues(t5, true));
//		CustomTag t3 = new CustomTag("testTag");
//		t3.setAttribute(name, value, forceAdd);
	}

}
