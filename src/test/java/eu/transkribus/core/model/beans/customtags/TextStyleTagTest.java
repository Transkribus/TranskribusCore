package eu.transkribus.core.model.beans.customtags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.customtags.TextStyleTag;

import org.junit.Assert;
import org.junit.Test;

public class TextStyleTagTest {
	private final static Logger logger = LoggerFactory.getLogger(TextStyleTagTest.class);

	@Test public void testAddFields() {
		TextStyleTag t1 = new TextStyleTag();
		TextStyleTag t2 = new TextStyleTag();
		TextStyleTag t3 = new TextStyleTag();
		TextStyleTag t4 = new TextStyleTag();

		t1.setFontFamily("hello1");
		t1.setKerning(1);
		t1.setBold(true);
		t1.setItalic(true);
		t1.setStrikethrough(true);
		t1.setSuperscript(true);
		
		t2.setFontFamily("hello");
		t2.setKerning(0);
		t2.setFontSize(10.0f);
		t2.setMonospace(true);
		t2.setBold(true);
		t2.setItalic(true);
		
		logger.info("t1 before = "+t1);
		logger.info("t2 = "+t2);				
		
		t3.setFontFamily("hello");
		t3.setKerning(0);
		t3.setFontSize(10.0f);
		t3.setBold(true);
		t3.setItalic(true);
		t3.setMonospace(true);
		
		t4.setOffset(2);
		t4.setLength(2);
		t4.setFontFamily("hello");
		t4.setKerning(1);
		t4.setFontSize(10.0f);
		t4.setBold(true);
		t4.setItalic(true);
		t4.setMonospace(true);
		t4.setStrikethrough(false);
		
		t1.setAttributes(t2, false);
		
		
//		Assert.assertTrue("Merged objects do not agree with test object 1", t3.equals(t1));
		logger.info("t1 after merge = "+t1);
		logger.info("t3 = "+t3);
		logger.info("t4 = "+t4);
		
		Assert.assertTrue("Merged objects should equal", t3.equalsEffectiveValues(t1, true));
		Assert.assertTrue("Merged objects should equal in effective values", t3.equalsEffectiveValues(t1, false));
		Assert.assertFalse("Merged objects should equal", t4.equalsEffectiveValues(t1, true));
		
		t3.setOffset(0); t3.setLength(4);
		logger.info("t3 = "+t3);
		Assert.assertTrue("Merged objects should not agree", t3.equalsEffectiveValues(t1, false));
		Assert.assertFalse("Merged objects should not agree", t3.equalsEffectiveValues(t1, true));
//		logger.info("t3 = "+t3);
	}

	@Test public void testEqualsObject() {
		TextStyleTag t1 = new TextStyleTag();
		TextStyleTag t2 = new TextStyleTag();
		TextStyleTag t3 = new TextStyleTag();
		
		t1.setFontFamily("hello");
		t1.setBold(true);
		t1.setItalic(true);
		
		t2 = t1.copy();
		
		t3 = t1.copy();
		t3.setFontFamily("hello1");
		
		Assert.assertTrue("Should be equal!", t1.equalsEffectiveValues(t2, true));
		Assert.assertFalse("Should not be equal!", t3.equalsEffectiveValues(t1, true));
	}
	
	public static void main(String[] args) {
		TextStyleTagTest t = new TextStyleTagTest();
		try {
			t.testEqualsObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
