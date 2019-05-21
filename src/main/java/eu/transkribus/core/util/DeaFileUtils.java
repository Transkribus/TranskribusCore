package eu.transkribus.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.interfaces.util.URLUtils;

/**
 * Some handy functions for file processing.<br/>
 * creation_date: 25.06.2006 <br/>
 * Moved to new DeaFileUtils: 27.09.2013
 * 
 * @author albert
 * @author raphael
 * @author philip
 */
public class DeaFileUtils {
	private static final Logger logger = LoggerFactory.getLogger(DeaFileUtils.class);
	
	private static final String SEP = File.separator;
	public static final Charset DEFAULT_CHARSET = Charsets.UTF_8;
	
	
	/**
	 * Loads a file and returns its content as String
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String readFileAsString(File file) throws IOException {
		String result = "";
		if(file.canRead()){
			// Open the file
			FileInputStream fstream = new FileInputStream(file);
			result = readInputStreamAsString(fstream);
		}
		return result.toString();
	}
	
	/**
	 * Loads a file and returns its content as String
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String readFileAsString(String filePath) throws IOException {
		File file = new File(filePath);
		return readFileAsString(file);
	}
	
	public static String readGzFileAsString(File file) throws FileNotFoundException, IOException {
		String result = "";
		if(file.canRead()){
			// Open the file
			InputStream fstream = new FileInputStream(file);
			InputStream gzipStream = new GZIPInputStream(fstream);
			result = readInputStreamAsString(gzipStream);
		}
		return result;
	}
	
	public static String readGzStreamAsString(InputStream is) throws FileNotFoundException, IOException {
		String result = "";
		InputStream gzipStream = new GZIPInputStream(is);
		result = readInputStreamAsString(gzipStream);
		
		return result;
	}
	
	public static String readGzFileAsString(byte[] data) throws FileNotFoundException, IOException {
		InputStream fstream = new ByteArrayInputStream(data);
		return readGzStreamAsString(fstream);
	}
	
	/**
	 * Loads the first nrOfChars (in blocks of 512 chars) of a file and returns its content as String
	 * 
	 * @param filePath
	 * @param nrOfChars nr of bytes to read
	 * @return
	 * @throws IOException
	 */
	public static String readFileAsString(File file, final int nrOfChars) throws IOException {
		BufferedReader br = null;
		StringBuffer result = new StringBuffer();
		char[] buffer = new char[512];
		if(file.canRead()){
			try {
				// Open the file
				FileInputStream fstream = new FileInputStream(file);
				DataInputStream in = new DataInputStream(fstream);
				br = new BufferedReader(new InputStreamReader(in));
				// Read File
				int i = 0;
				int len;
				while (i < nrOfChars && (len = br.read(buffer)) > 0) {
					result.append(buffer, 0, len);
					i += buffer.length;
				}
			} finally {
				br.close();
			}
		}
		return result.toString();
	}
	
	public static String readInputStreamAsString(InputStream is) throws FileNotFoundException, IOException {
		BufferedReader br = null;
		StringBuffer result = new StringBuffer();
		try {		
			InputStream in = new DataInputStream(is);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				result.append(strLine + "\n");
			}
		} finally {
			br.close();
		}
	
		return result.toString();
	}

	/**
	 * Create a full file path out of a list of directories (and possibly a file
	 * name at the end)
	 * 
	 * @param subdirs
	 *            The first parameter is the root directory, followed by one or
	 *            more subDirectories, which are concatenated to the root
	 *            directoy remark: all dirparts should be set without any path
	 *            separators
	 * @return the full path (operating system dependent) whith all subdirs
	 */
	public static String buildPath(String... subDirs) {
		String wholePath = subDirs[0];
		if (wholePath.endsWith(SEP)) {
			wholePath = wholePath.substring(0, wholePath.length() - 1); // StringUtils.deleteLastChar(wholePath);
		}

		for (int i = 1; i < subDirs.length; i++) {
			wholePath += SEP + subDirs[i];
		}
		return wholePath;
	}

	/**
	 * Create a file with all the necessary directories lying within the path
	 * 
	 * @param filePath
	 *            the file path for which the directories and the file be
	 *            created
	 */
	public static void createFileWithPath(String filePath) throws IOException {
		File f = new File(filePath);
		f.getParentFile().mkdirs();
		f.createNewFile();
	}

	/**
	 * @see #createFileWithPath(String)
	 */
	public static void createFileWithPathIfNotExists(String filePath)
			throws IOException {
		File f = new File(filePath);
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			f.createNewFile();
		}
	}

	/**
	 * Reads an input stream and writes its content to a file
	 * 
	 * @param is
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static File writeStreamToFile(InputStream is, String path)
			throws IOException {
		File file = new File(path);
		BufferedInputStream bis = new BufferedInputStream(is);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(file));
		int inByte;

		while ((inByte = bis.read()) != -1) {
			bos.write(inByte);
		}
		bis.close();
		bos.close();
		return file;
	}

	/**
	 * writes the content of a byte[] to a File at the specified filePath.
	 * 
	 * @param bytes
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public File writeByteArrayToFile(byte[] bytes, String filePath)
			throws IOException {
		File file = new File(filePath);
		BufferedOutputStream bos = null;
		bos = new BufferedOutputStream(new FileOutputStream(file));
		for (int d : bytes) {
			bos.write(d);
		}
		bos.close();

		return file;
	}

	/**
	 * writes the content of a String to a File at the specified filePath.
	 * 
	 * @param text
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static boolean writeStringToFile(String text, String filePath)
			throws IOException {
		FileWriter fstream = null;
		BufferedWriter out = null;
		// Create file
		try {
			fstream = new FileWriter(new File(filePath));
			out = new BufferedWriter(fstream);

			out.write(text);
		} catch (Exception e) {
			throw e;
		} finally {
			// Close the output stream
			out.flush();
			out.close();
		}
		// if we are here => no exception
		return true;
	}

	/**
	 * Get File size in Bytes
	 * @param input
	 * @return
	 */
	public static Long getFileSizeBytes(File input) {
		return new Long(input.length());
	}

	/**
	 * Get File Size in MB
	 * @param input
	 * @return
	 */
	public static Double getFileSizeMB(File input) {
		Double tmp = getFileSizeBytes(input).doubleValue();
		return tmp / 1048576;
	}

	/**
	 * Checks if file at path exists, is readable and returns its size in bytes then.
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static Long getFileSizeBytes(String filePath) throws IOException {
		return getFileSizeBytes(getReadableFile(new File(filePath)));
	}

	/**
	 * Checks if file at path exists, is readable and returns its size in bytes then.
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static Double getFileSizeMB(String filePath) throws IOException {
		return getFileSizeMB(getReadableFile(new File(filePath)));
	}
	
	/**
	 * Opens File f and checks if it exists, is readable and returns it then. Otherwise throws Exception
	 * 
	 * Use isReadable instead. Still used by P4BatchIngest
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static File getReadableFile(File f) throws IOException {
		if(f.canRead()){
			return f;
		} else {
			throw new IOException("File " + f.getAbsolutePath() + " is not readable.");
		}
	}

	/**
	 * 
	 * FIXME only throws exception if file exists but is not writeable. 
	 * What if the file does NOT exist? Check directory...
	 * FIXME rename to isWriteable?
	 * 
	 * Opens file and checks if it exists, is writeable and returns it then. Otherwise throws Exception
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	public static boolean isWriteable(File f) throws IOException {
		//TODO check dir here.
		return (!f.exists() || f.canWrite());
	}

	/**
	 *
	 * Opens file and checks if it exists, is writeable and returns it then. Otherwise throws Exception
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static File getWriteableFile(String path) throws IOException {
		File f = new File(path);
		if(isWriteable(f)){
			return f;
		} else {
			throw new IOException("File " + path + " is not writeable.");
		}
	}
	
	/**
	 * This method essentially does the same as FileUtils.copyUrlToFile() but 
	 * it does not throw an IOException if the status code is >= 400.
	 * <br><br>
	 * The status code is returned.
	 * <br><br>
	 * Method was moved here from the legacy core.util.URLUtils.
	 * Redirects are not handled specifically like methods in new {@link URLUtils} do!
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
				logger.debug("Got URL now copy to file ");
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
	
}
