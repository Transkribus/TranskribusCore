package eu.transkribus.core.io.util;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImgPriorityTest {
	private static final Logger logger = LoggerFactory.getLogger(ImgPriorityTest.class);
	
	@Test
	public void testGetAllowedFilenameExtensions() {
		List<String> exts = ImgPriority.getAllowedFilenameExtensions();
		Assert.assertNotNull(exts);
		logger.debug("Collected allowed file extensions: " + exts.stream().collect(Collectors.joining(", ")));
		Assert.assertFalse(exts.isEmpty());
	}

}
