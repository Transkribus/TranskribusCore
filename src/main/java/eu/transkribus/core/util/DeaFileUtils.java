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
import java.util.zip.GZIPInputStream;

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

	private static final String SEP = File.separator;
	
	
	/**
	 * Loads a file and returns its content as String
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static String readFileAsString(File file) throws IOException {
		BufferedReader br = null;
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

	/** Uses the classLoader's getResourceAsStream method to obtain the content of file on the classpath as String.
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String readResourceAsString(String fileName)
			throws IOException {
		InputStream in = null;
		String content = null;
		in = DeaFileUtils.class.getClassLoader().getResourceAsStream(fileName);
		if (in == null) {
			throw new FileNotFoundException("File is not at "
					+ DeaFileUtils.class.getClassLoader().getResource("")
							.getPath());
		}

		try {
			content = new java.util.Scanner(in).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			throw new IOException(fileName + " is empty.");
		}

		return content;
	}

	/**
	 * @param fileName
	 *            a file name or file path with a certain extension. Works with
	 *            single file names and with the full path of a file
	 * @return the file name or file path without the extension
	 */
	@Deprecated
	public static String getFileNameWithoutExtension(String fileName) {
		int dotIdx = fileName.lastIndexOf(".");
		return fileName.substring(0, dotIdx);
	}
	@Deprecated
	public static String getFileNameWithoutExtension(File file) {
		return getFileNameWithoutExtension(file.getName());
	}

	/**
	 * @see #getFileNamesWithoutExtension(String[]), but if <i>fileName</i> is a
	 *      full path, the name is extracted before
	 */
	@Deprecated
	public static String getSimpleFileNameWithoutExtension(String fileName) {
		return getFileNameWithoutExtension(new File(fileName).getName());
	}
	@Deprecated
	public static String[] getFileNamesWithoutExtension(String[] fileNames) {
		String[] without = new String[fileNames.length];
		for (int i = 0; i < fileNames.length; i++) {
			String fileName = fileNames[i];
			without[i] = getFileNameWithoutExtension(fileName);
		}
		return without;
	}

	/**
	 * Retrieve to file extension of a single file or the full path of a file
	 * 
	 * @param fileName
	 *            a file name or file path
	 * @return the extension of the file specified by <b>fileName</b>
	 */
	@Deprecated
	public static String getFileExtension(String fileName) {
		String[] parts = fileName.split("\\.");
		if(parts.length == 1){
			//just the filename if no extension, i.e parts.length == 1 
			return "";
		} else {
			return parts[parts.length - 1];
		}
		/*
		 * StringTokenizer tokenizer = new StringTokenizer(fileName, ".");
		 * String ext = ""; while (tokenizer.hasMoreTokens()) { ext =
		 * tokenizer.nextToken(); } return ext;
		 */
	}
	
	/**
	 * Retrieve to file extension of a single file or the full path of a file
	 * 
	 * @param file
	 *            a file 
	 * @return the extension of the file specified by <b>file.getName()</b>
	 */
	@Deprecated
	public static String getFileExtension(File file) {
		return getFileExtension(file.getName());
	}

	/**
	 * FIXME taken from old FileUtils. Needs StreamingUtils. Use
	 * writeStreamToFile for now
	 * 
	 * Store a file under a certain path
	 * 
	 * @param stream
	 *            the stream which should be stored
	 * @param fileName
	 *            the path under which the file should be stored
	 * @throws IOException
	 *             if anything goes wrong while storing
	 */
	// public static void saveFile(InputStream is, String fileName) throws
	// IOException
	// {
	// OutputStream os = new FileOutputStream(fileName);
	// StreamingUtils.in2out(is, os);
	// }

	/**
	 * @param fPath
	 *            an absolute or relative file path
	 * @return true if the file contains an extension
	 */
	@Deprecated
	public static boolean hasExtension(String fPath) {
		return (fPath.split("\\.").length > 1);
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
		return getFileSizeBytes(getReadableFile(filePath));
	}

	/**
	 * Checks if file at path exists, is readable and returns its size in bytes then.
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static Double getFileSizeMB(String filePath) throws IOException {
		return getFileSizeMB(getReadableFile(filePath));
	}
	
	
	/**
	 * Opens File at path and checks if it exists, is readable and returns it then. Otherwise throws Exception
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public static File getReadableFile(String path) throws IOException {
		File f = new File(path);
		if(f.canRead()){
			return f;
		} else {
			throw new IOException("File " + path + " is not readable.");
		}
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
	@Deprecated
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
	 *
	 * Opens file and checks if it exists, is writeable and returns it then. Otherwise throws Exception
	 * 
	 * Use isWriteable instead. Still used by P4BatchIngest
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public static File getWriteableFile(File f) throws IOException {
		if(isWriteable(f)){
			return f;
		} else {
			throw new IOException("File " + f.getAbsolutePath() + " is not writeable.");
		}
	}
}
