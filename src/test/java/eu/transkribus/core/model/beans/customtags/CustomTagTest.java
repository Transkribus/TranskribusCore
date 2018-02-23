package eu.transkribus.core.model.beans.customtags;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomTagTest {
	private final static Logger logger = LoggerFactory.getLogger(CustomTagTest.class);
	
//	@Test
//	public void testSetAttributes() throws Exception {
//		
//		
//		List<CustomTag> tags = new ArrayList<>();
//		int NTags = 100;
//		for (int i=0; i<NTags; ++i) {
//			CustomTag pTag = CustomTagFactory.create(PersonTag.TAG_NAME);
//			tags.add(pTag);
//		}
//		
//		for (int i=0; i<1000; ++i) {
//			int index = ThreadLocalRandom.current().nextInt(0, NTags);
//			index = 0;
//			PersonTag t = (PersonTag) tags.get(index);
//			
////			String attName = UUID.randomUUID().toString();
////			String attName = ""+i;
//			String attName = "abbrev";
//			String attValue = "sebi";
//			t.setAttribute(attName, attValue, true);
////			t.setFirstname("sebi");
//			
//			System.out.println("i = "+i+" index = "+index+" Natts = "+t.getAttributes().size());
//			
////			Assert.assertTrue("1 attribute not set!", t.getFirstname().equals("sebi"));
//			
//			Assert.assertTrue("2 attribute not set!", t.getAttributeValue(attName).equals(attValue));
//		}
//	}

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
