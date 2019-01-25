package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Util methods for dealing with URLs
 * @author philip
 */
public class UrlUtils {
	private static final Logger logger = LoggerFactory.getLogger(UrlUtils.class);
	
	public static String urlEncode(String s) throws UnsupportedEncodingException {
		return URLEncoder.encode(s, "UTF-8");
	}
	
	public static String urlDecode(String s, boolean specialTreatmentForPlusAndPercentSign) throws UnsupportedEncodingException {
		if (specialTreatmentForPlusAndPercentSign) {
			s = s.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
			s = s.replaceAll("\\+", "%2B");
		}
        return URLDecoder.decode(s, "utf-8");
	}
	
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
		if(code < 400) {
			try (InputStream is = huc.getInputStream()){
				//do check on URL and handle 404 etc.
				FileUtils.copyInputStreamToFile(is, dest);
				logger.info("File loaded from URL: " + source);
				
			} catch(IOException ioe) {
				throw new IOException("Could not get connection to URL: " + source, ioe);
			} finally {
				huc.disconnect();
			}
		} else {
			logger.error("Could not download file at " + source + ": HTTP Status = " + code);
		}
		return code;
	}
	
	public static String getFilenameFromHeaderField(URL source) throws IOException {
		HttpURLConnection huc = (HttpURLConnection)source.openConnection(); 
		huc.setRequestMethod("GET"); //OR  huc.setRequestMethod ("HEAD"); 
		huc.connect(); 
		final int code = huc.getResponseCode();
		if(code < 400) {
			//do check on URL and handle 404 etc.
			String raw = huc.getHeaderField("Content-Disposition");
			// raw = "attachment; filename=abc.jpg"
			if(raw != null && raw.indexOf("=") != -1) {
			    String fileName = raw.split("=")[1]; //getting value after '='
			    logger.debug("Filename from Content-Disposition " + fileName);
			    return fileName;
			} else {
				logger.debug("No filename found in Content-Disposition");
				return null;
			}
		}
		return null;
	}
}
