package eu.transkribus.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.util.TextStyleTypeUtils;

import org.junit.Assert;
import org.junit.Test;

public class TextStyleTypeUtilsTest {
	private final static Logger logger = LoggerFactory.getLogger(TextStyleTypeUtilsTest.class);
	
//	@Test public void testTrue() {
//		Assert.assertTrue("yeah!", true==true);
//	}

	@Test public void testMergeEqualTextStyleTypeFields() {
		TextStyleType ts1 = new TextStyleType();
		ts1.setFontFamily("hello");
		
		TextStyleType ts2 = new TextStyleType();
		ts2.setBold(true);
		
		TextStyleType mergedTs = TextStyleTypeUtils.mergeEqualTextStyleTypeFields(ts1, ts2);
		TextStyleType mergedCheck = new TextStyleType();
		
		logger.debug("merged = "+mergedTs);
		Assert.assertTrue("merged should be empty", TextStyleTypeUtils.equalsInEffectiveValues(mergedCheck, mergedTs));
	}
	

}
