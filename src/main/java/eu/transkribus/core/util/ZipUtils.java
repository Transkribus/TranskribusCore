package eu.transkribus.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipUtils {
	private static final Logger logger = LoggerFactory.getLogger(ZipUtils.class);

	public static void main(String[] args) throws Exception {

		//		List<String> fileList = generateFileList(new File(SOURCE_FOLDER));
		//		zipIt(fileList, OUTPUT_ZIP_FILE);
		
		createZipFromFolder("/home/sebastian/Downloads/1312", "/home/sebastian/testZip.zip", true);
	}

	/**
	 * Zip all files contained in path
	 * 
	 * @param path
	 * @param zipFileName
	 * @return
	 * @throws IOException
	 */
	public static File zipFiles(File path, final String zipFileName) throws IOException {
		String basePath;
		if (path.isDirectory()) {
			basePath = path.getAbsolutePath();
		} else {
			basePath = path.getParent();
		}

		System.out.println(path+" / "+basePath);
		
		List<String> fileList = generateFileList(path, basePath);
		
		System.out.println(fileList);
		
		File zip = zip(fileList, basePath, zipFileName);
		return zip;
	}

	/**
	 * Zip all files contained in fileList
	 * 
	 * @param fileList
	 *            a String list, such that: basePath + File.separator +
	 *            fileList.get(i)
	 * @param basePath
	 * @param zipFilePath
	 *            output ZIP file location
	 * @throws IOException
	 */
	public static File zip(List<String> fileList, final String basePath, String zipFilePath)
			throws IOException {

		byte[] buffer = new byte[1024];
		FileOutputStream fos = null;
		ZipOutputStream zos = null;

		File zipFile = new File(zipFilePath);

		try {
			fos = new FileOutputStream(zipFile);
			zos = new ZipOutputStream(fos);

			logger.info("Output to Zip : " + zipFilePath);
			
			FileInputStream in;
			ZipEntry ze;
			for (String file : fileList) {

				logger.info("File Added : " + file);
				ze = new ZipEntry(file);
				zos.putNextEntry(ze);

				in = new FileInputStream(basePath + File.separator + file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}
				in.close();
			}
			zos.closeEntry();
		} finally {
			zos.close();
		}
		return zipFile;
	}

	/**
	 * Traverse a directory and get all files, and add the file into fileList
	 * 
	 * @param node
	 *            file or directory
	 * @return
	 */
	public static List<String> generateFileList(File node, final String basePath) {
		List<String> fileList = new ArrayList<String>();
		//add file only
		if (node.isFile()) {
			fileList.add(generateZipEntry(node.getAbsoluteFile().toString(), basePath));
		}

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				generateFileList(new File(node, filename), basePath);
			}
		}
		return fileList;
	}
	
	/*
	 * create ZIP file for folders and files
	 */
	
	public static File createZipFromFolder(String srcFolder, String destZipFile, boolean onlyContent) throws IOException {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		try {
			File srcFolderFile = new File(srcFolder);
			if (!srcFolderFile.isDirectory()) {
				throw new IOException("Not a directory: "+srcFolder);
			}
			
			fileWriter = new FileOutputStream(destZipFile);

			zip = new ZipOutputStream(fileWriter);

			if (!onlyContent) {
				addFolderToZip("", srcFolder, zip);	
			} else {
				File zipFile = new File(destZipFile);
				
				for (File f : srcFolderFile.listFiles()) {
					if (zipFile.compareTo(f)==0) {
						logger.debug("skipping output zip file that was created in input directory: "+f.getAbsolutePath());
						continue;
					}
					
					logger.trace("f = "+f.getAbsolutePath());
					addFileToZip("", f.getAbsolutePath(), zip);
				}
			}

			zip.flush();
			zip.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw e;
		} finally {
			if (zip != null) {
				zip.close();
			}
		}
		return new File(destZipFile);
	}

	static private void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws IOException {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip);
		} else {
			logger.trace("adding file to zip: "+srcFile);
			
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
			in.close();
		}
	}
	
	static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws IOException {
		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFileToZip(folder.getName(), srcFolder+"/"+fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder+"/"+fileName, zip);
			}
		}
	}

	/**
	 * Format the file path for zip
	 * 
	 * @param file
	 *            file path
	 * @return Formatted file path
	 */
	private static String generateZipEntry(String file, final String basePath) {
		return file.substring(basePath.length() + 1, file.length());
	}

	public static File unzip(File zipFile, String path) throws IOException {
		File dir = new File(path);
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}
		if (!dir.exists()) {
			dir.mkdirs();
		}

		byte[] buffer = new byte[1024];
		FileInputStream fis = null;
		ZipInputStream zis = null;

		try {
			fis = new FileInputStream(zipFile);
			zis = new ZipInputStream(fis);

			logger.info("Reading Zip : " + zipFile.getName());
			logger.info("Writing content to: " + path);

			ZipEntry e;
			while ((e = zis.getNextEntry()) != null) {
				//TODO
//				long crc = e.getCrc();
				String name = e.getName();
				File newFile = new File(path + name);
				logger.info("Unpacking " + newFile.getAbsolutePath());
				//create folders in between
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
			}
			zis.closeEntry();
		} finally {
			zis.close();
		}
		return dir;
	}

	public static File unzipGzFile(File gzFile, String path) throws IOException {
		InputStream is = new FileInputStream(gzFile);
		InputStream gzIs = new GZIPInputStream(is);
		Reader decoder = new InputStreamReader(gzIs, "UTF-8");
		BufferedReader br = new BufferedReader(decoder);
		
		File out = new File(path);
		FileWriter writer = new FileWriter(out);
		char[] buffer = new char[1024];
		int length;
		while((length = br.read(buffer)) != -1){
			writer.write(buffer, 0, length);
		}
		writer.close();
		br.close();
		return out;
	}
}
