package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UrlUtilsTest {
	final String workingUrlStr = "https://transkribus.eu";
	final String workingImageUrlStr = "http://digi.landesbibliothek.at/viewer/content/AC10109954/1000/0/00000024.jpg";
	final String workingImageUrlStr2 = "https://dbis-thure.uibk.ac.at/f/Get?id=AMUTANLJEUKWITZQJGFFDZYC";
	final String nonWorkingUrlStr = "https://dbis-thure.uibk.ac.at/f/Get?id=thisDoesNotWork";
	
	final String[] workingUrls = {workingUrlStr, workingImageUrlStr, workingImageUrlStr2};
	final String[] nonWorkingUrls = {nonWorkingUrlStr};
	
	File tmpDir = null;
	@Before
	public void setup() throws IOException {
		File tmp = new File(System.getProperty("java.io.tmpdir"));
		tmpDir = Files.createTempDirectory(tmp.toPath(), "UrlUtilsTest_").toFile();
	}
	
//	@Test
	public void testWorkingUrls() throws MalformedURLException {
		File out = new File(tmpDir.getAbsolutePath() + File.separator + "outFile");
		for(String s : workingUrls) {
			URL url = new URL(s);
			try {
				int code = UrlUtils.copyUrlToFile(url, out);
			} catch (Throwable t) {
				Assert.fail(t.getMessage());
			}
		}
	}
	
//	@Test
	public void testNonWorkingUrls() throws MalformedURLException {
		File out = new File(tmpDir.getAbsolutePath() + File.separator + "outFile");
		for(String s : nonWorkingUrls) {
			URL url = new URL(s);
			try {
				int code = UrlUtils.copyUrlToFile(url, out);
				System.out.println(code);
			} catch (Throwable t) {
				Assert.fail(t.getMessage());
			}
		}
	}
	
	@After
	public void cleanup() throws IOException {
		if(tmpDir != null && tmpDir.isDirectory()) {
			FileUtils.deleteDirectory(tmpDir);
		}
	}
}
