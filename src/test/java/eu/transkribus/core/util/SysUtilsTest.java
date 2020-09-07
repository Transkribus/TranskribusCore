package eu.transkribus.core.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysUtilsTest {
	private static final Logger logger = LoggerFactory.getLogger(SysUtilsTest.class);
	
	@Test
	public void testGetLinuxUname() {
		final String uname = SysUtils.getLinuxUname();
		logger.info("Uname: " + uname);
	}
}
