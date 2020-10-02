package eu.transkribus.core.io.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.util.SysUtils;

public class ImgFilenameFilterTest {
	private static final Logger logger = LoggerFactory.getLogger(ImgFilenameFilterTest.class);
	
	private static File testDir;
	
	@BeforeClass
	public static void setup() {
		testDir = FileUtils.toFile(ImgFilenameFilterTest.class.getClassLoader().getResource("ImgFilenameFilterTest"));
	}
	
	@Test
	public void testAcceptCaseSensitivity() {
		ImgFilenameFilter filter = new ImgFilenameFilter();
		//should find 3 jpeg files with different extensions
		String[] filenames =  testDir.list(filter);
		logFilenames(filenames);
		Assert.assertEquals(3, filenames.length);
	}
	
	private void logFilenames(String[] filenames) {
		for(String s : filenames) {
			logger.info(s);
		}
	}

	/**
	 * B2PUtils crashed on a symlink to a file with "JPG" extension
	 * @throws IOException 
	 */
	@Test
	public void testAcceptSymlink() throws IOException {
		Assume.assumeFalse(SysUtils.isWin());
		File tmp = Files.createTempDirectory("ImgFilenameFilterTest-").toFile();
		File testFile = new File(tmp, "test01.JPG");
		File source = new File(testDir, "test01.JPG");
		
		final String basename = FilenameUtils.getBaseName(testFile.getAbsolutePath());
		String[] filenames;
		//ImgFilenameFilter from B2PUtils
		ImgFilenameFilter filter = new ImgBaseFilenameFilter(basename);
		
		Files.copy(source.toPath(), testFile.toPath());
		filenames = tmp.list(filter);
		logFilenames(filenames);
		Assert.assertTrue("Copy of testfile could not be found", filenames.length == 1);
		
		Assert.assertTrue("Could not delete testFile: " + testFile.getAbsolutePath(), testFile.delete());
		
		Files.createSymbolicLink(testFile.toPath(), source.toPath());
		filenames = tmp.list(filter);
		logFilenames(filenames);
		Assert.assertTrue("Symlink to testfile could not be found", filenames.length == 1);
		
		Assert.assertTrue("Could not delete testFile symlink: " + testFile.getAbsolutePath(), testFile.delete());
		Assert.assertTrue("Source file was deleted!", source.isFile());
		
		//test on different filename than original file
		final String otherFilename = "1543.JPG";
		File otherTestFile = new File(tmp, otherFilename);
		final String otherBasename = FilenameUtils.getBaseName(otherTestFile.getAbsolutePath());
		
		//ImgFilenameFilter from B2PUtils
		filter = new ImgFilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return super.accept(dir, name) && name.startsWith(otherBasename);
			}
		};
		
		Files.createSymbolicLink(otherTestFile.toPath(), source.toPath());
		filenames = tmp.list(filter);
		logFilenames(filenames);
		Assert.assertTrue("Symlink to testfile could not be found", filenames.length == 1);
		
		FileUtils.deleteDirectory(tmp);
		Assert.assertTrue("Source file was deleted!", source.isFile());
	}
}
