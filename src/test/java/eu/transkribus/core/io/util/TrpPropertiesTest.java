package eu.transkribus.core.io.util;

import org.junit.Assert;
import org.junit.Test;

public class TrpPropertiesTest {
	
	@Test
	public void testGetBoolProperty() {
		TrpProperties p = new TrpProperties();
		
		p.setProperty("true", "true");
		p.setProperty("True", "True");
		p.setProperty("tRue", "tRue");
		p.setProperty("TRUE", "TRUE");
		p.setProperty("1", "1");
		
		p.setProperty("false", "false");
		p.setProperty("0", "0");
		p.setProperty("shouldBeFalse", "shouldBeFalse");
		
		Assert.assertTrue(p.getBoolProperty("true"));
		Assert.assertTrue(p.getBoolProperty("True"));
		Assert.assertTrue(p.getBoolProperty("tRue"));
		Assert.assertTrue(p.getBoolProperty("TRUE"));
		Assert.assertTrue(p.getBoolProperty("1"));
		
		Assert.assertFalse(p.getBoolProperty("false"));
		Assert.assertFalse(p.getBoolProperty("0"));
		Assert.assertFalse(p.getBoolProperty("shouldBeFalse"));
		Assert.assertFalse(p.getBoolProperty("thisKeyDoesNotEvenExist"));
	}
}
