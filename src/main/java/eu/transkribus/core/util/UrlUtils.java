package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util methods for dealing with URLs
 * @author philip
 */
public class UrlUtils {
	private static final Logger logger = LoggerFactory.getLogger(UrlUtils.class);
	
	/**
	 * This method essentially does the same as FileUtils.copyUrlToFile() but 
	 * it does not throw an IOException if the status code is >= 400.
	 * 
	 * The status code is returned.
	 * 
	 * @param source
	 * @param dest
	 * @return status code
	 * @throws IOException
	 */
	public static int copyUrlToFile(URL source, File dest) throws IOException {
		HttpURLConnection huc = (HttpURLConnection)source.openConnection(); 
		huc.setRequestMethod("GET"); //OR  huc.setRequestMethod ("HEAD"); 
		huc.connect(); 
		final int code = huc.getResponseCode();
		try (InputStream is = huc.getInputStream()){
			//do check on URL and handle 404 etc.
			if(code < 400) {
				FileUtils.copyInputStreamToFile(is, dest);
				logger.info("File loaded from URL: " + source);
			} else {
				logger.error("Could not download file at " + source + ": HTTP Status = " + code);
			}
		} catch(IOException ioe) {
			throw new IOException("Could not get connection to URL: " + source, ioe);
		}
		return code;
	}
}
