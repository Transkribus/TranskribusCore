package eu.transkribus.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreLoggingTest {
	
	private final static Logger logger = LoggerFactory.getLogger(CoreLoggingTest.class);
	

	public CoreLoggingTest() {
	}
	
	public static void main(String[] args) {
		System.out.println("start logging test, class = "+CoreLoggingTest.class.getName());
		
		logger.info("info!");
		logger.debug("debug!");
		logger.trace("trace!");
		
		logger.error("error!");
		logger.warn("warn!");
		// logger.fatal("fatal!");
		
	}

}
