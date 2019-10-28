package eu.transkribus.core.io.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.exec.util.ExiftoolUtil;

public class ExiftoolUtilsTest {
	private static final Logger logger = LoggerFactory.getLogger(ExiftoolUtilsTest.class);
	
	/**
	 * This test resource file makes ExiftoolUtil fail with a TimeoutException (tried 30-60 seconds). 
	 * Replacing the space in the filename does not make it work (filenames with spaces should work since 2015...).
	 * 
	 * I don't know what's going on here...
	 * FIXME
	 * 
	 * @throws IOException
	 * @throws TimeoutException
	 * @throws InterruptedException
	 * @throws ToolException
	 */
	@Test
	public void testOnPageXml() throws IOException, TimeoutException, InterruptedException {
		
		final String badResource = "exiftool/Or 8349_0071.xml";
		final String goodResource = "exiftool/Or 8349_0036.xml";
		
		runExiftoolOnResource(goodResource);
		
		runExiftoolOnResource(badResource);
	}
	
	private void runExiftoolOnResource(final String resourceName) throws IOException, TimeoutException, InterruptedException {
		URL fileUrl = this.getClass().getClassLoader().getResource(resourceName);
		File file = FileUtils.toFile(fileUrl);
		logger.info("Found test resource: {}", file.exists());	
		
		String filename = file.getAbsolutePath();
		logger.info("Running exiftool on filename: {}", filename);
		
		ExiftoolUtil.runExiftool(file.getAbsolutePath());
	}
}
