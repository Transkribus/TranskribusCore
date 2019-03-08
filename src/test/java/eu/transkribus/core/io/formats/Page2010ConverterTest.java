package eu.transkribus.core.io.formats;

import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.SysUtils;

public class Page2010ConverterTest {
	private static final Logger logger = LoggerFactory.getLogger(Page2010ConverterTest.class);
	
	@Test
	public void test2010ConversionSingleFile() throws IOException, JAXBException {
		assumeTrue(SysUtils.IS_LINUX);
		/**
		 * To investigate:
		 * This test fails on Windows at the move File operations in Page2010Converter with IOException: "concurrent access"
		 */
		
		File page2010Xml = new File("src/test/resources/page2010/002_080_001.xml");
		Assert.assertTrue("Could not find test resource: " + page2010Xml.getAbsolutePath(), page2010Xml.exists());
		
		File tmpDir = new File(System.getProperty("java.io.tmpdir"), "page2010test-" + UUID.randomUUID());
		if(!tmpDir.mkdir()) {
			logger.error("Could not create temp dir!");
			return;
		}
		
		File testFile = new File(tmpDir, page2010Xml.getName());
		FileUtils.copyFile(page2010Xml, testFile);
		
		File backupDir = new File(tmpDir, "backup");
		
		Page2010Converter.updatePageFormatSingleFile(testFile, backupDir.getAbsolutePath());
		
		try {
			Assert.assertNotNull("Updated PAGE XML could not be read!", PageXmlUtils.unmarshal(testFile));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		
		File backup = new File(backupDir, page2010Xml.getName());
		Assert.assertTrue("Backup file was not created at: " + backup.getAbsolutePath(), backup.isFile());
		
		try {
			FileUtils.deleteDirectory(tmpDir);
		} catch(IOException e) {
			logger.error("Could not delete temp dir: " + tmpDir.getAbsolutePath(), e);
		}
	}
	
	@Test
	public void test2010ConversionSingleFileOldMethod() throws IOException, JAXBException {		
		File page2010Xml = new File("src/test/resources/page2010/002_080_001.xml");
		Assert.assertTrue("Could not find test resource: " + page2010Xml.getAbsolutePath(), page2010Xml.exists());
		
		File tmpDir = new File(System.getProperty("java.io.tmpdir"), "page2010test-" + UUID.randomUUID());
		if(!tmpDir.mkdir()) {
			logger.error("Could create temp dir!");
			return;
		}
		
		File testFile = new File(tmpDir, page2010Xml.getName());
		FileUtils.copyFile(page2010Xml, testFile);
		
		File backupDir = new File(tmpDir, "backup");
		Page2010Converter.updatePageFormatSingleFileOld(testFile, backupDir.getAbsolutePath());
		
		try {
			Assert.assertNotNull("Updated PAGE XML could not be read!", PageXmlUtils.unmarshal(testFile));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		
		File backup = new File(backupDir, page2010Xml.getName());
		Assert.assertTrue("Backup file was not created at: " + backup.getAbsolutePath(), backup.isFile());
		
		try {
			FileUtils.deleteDirectory(tmpDir);
		} catch(IOException e) {
			logger.error("Could not delete temp dir: " + tmpDir.getAbsolutePath(), e);
		}
	}
}
