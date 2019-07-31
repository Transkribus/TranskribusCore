package eu.transkribus.core.catti;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @deprecated
 *
 */
public class LocalFimagestoreClient {
	private final static Logger logger = LoggerFactory.getLogger(LocalFimagestoreClient.class);
	
	static int levels = 2;
	
	public final static String FILE_TYPE_ORIG = "orig";
	public final static String FILE_TYPE_METADATA = "metadata";
	public final static String FILE_TYPE_BROWSER = "browser";
	public final static String FILE_TYPE_VIEW = "view";
	public final static String FILE_TYPE_THUMB = "thumb";
	public final static String FILE_TYPE_SCALE_BASE = "scale";
	public final static String FILE_TYPE_SCALE_25 = FILE_TYPE_SCALE_BASE+"25";
	public final static String FILE_TYPE_SCALE_50 = FILE_TYPE_SCALE_BASE+"50";
	public final static String FILE_TYPE_BIN = "bin";
	public final static String METADATA_FN = "metadata.txt";
	
	public static String getStoreFilename(String storeLocation, String fn) {
		String outfn = storeLocation + "/";
		for (int i=0; i<levels; ++i)
			outfn += fn.charAt(i) + "/";
		outfn += fn;
		
		return outfn;
	}
	
	/**
	 * Returns the specified filetype for this file
	 * @param storeLocation
	 * @param key
	 * @param fileType
	 * @return
	 * @throws FileNotFoundException
	 */
	public static File findFile(final String storeLocation, final String key, String fileType) throws FileNotFoundException {
		if(StringUtils.isEmpty(storeLocation) || StringUtils.isEmpty(key)) {
			throw new IllegalArgumentException("StoreLocation and key must not be null!");
		}
		if(fileType == null) {
			fileType = FILE_TYPE_ORIG;
		}
		final File dir = findDir(storeLocation, key);
		if(dir == null) {
			throw new FileNotFoundException("Directory of file could not be found! "
					+ "StoreLocation = " + storeLocation + ", key = " + key);
		}
		return getFile(dir, fileType);
	}
	
	/**
	 * Returns the original file
	 * @param storeLocation
	 * @param key
	 * @return
	 * @throws FileNotFoundException
	 */
	public static File findFile(final String storeLocation, final String key) throws FileNotFoundException {
		return findFile(storeLocation, key, null);
	}
	
	public static File findDir(String storeLocation, String key) {		
		if (key == null || key.length() <= levels) {
			logger.debug("findDir, file does not exist (1): "+key);
			return null;
		}
		
		String storeFn = getStoreFilename(storeLocation, key);
//		logger.debug("storeFn = "+storeFn);
		
		File f = new File(storeFn);
		if (f==null || !f.exists() || !f.isDirectory()) {
			logger.debug("findDir, file does not exist (2): "+storeFn);
			return null;
		}
		else {
//			logger.debug("findDir, found existing file: "+storeFn);
			return f;
		}
	}
		
	/** This one returns null for a FileNotFoundException 
	 * @param dir the directory
	 * @param fileType
	 * @return
	 * @throws Exception
	 */
	public static File getFile2(File dir, String fileType) throws Exception {
		try {
			return getFile(dir, fileType);
		} catch (FileNotFoundException fe) {
			return null;
		}
	}
	
	public static File getFile(File dir, String fileType) throws FileNotFoundException {
		if (fileType.equals(FILE_TYPE_METADATA))
			return getMetadataFile(dir);
		
//		FileMetadata metadata = MetadataParser.parseMetadata(dir);
//		
//		// if fileType = browser and original file is browser compatible then return original file:
//		if (fileType.equals(FImagestoreVars.FILE_TYPE_BROWSER) && 
//				MimeTypes.isImageMimeTypeBrowserCompatible(metadata.Mimetype)) {
////			return getOrigFile(dir);
//			fileType=FImagestoreVars.FILE_TYPE_ORIG;
//		}
//		// else if fileType = view and original file is binarized then return original file:
//		if (fileType.equals(ImagestoreVars.FILE_TYPE_VIEW) && metadata.imgMetadata.NComponents==1) {
//			return getOrigFile(dir);
//		}
		
		// else: get file of type fileType:
//		List<File> files = (List<File>) FileUtils.listFiles(dir, new WildcardFilter(fileType+"_*.*"), null);
		List<File> files = (List<File>) FileUtils.listFiles(dir, new WildcardFileFilter(fileType+"_*.*"), null);
		
		if (files.size()==0)
			throw new FileNotFoundException("File of type "+fileType+" not found in dir: "+dir.getAbsolutePath());
		else if (files.size()>1)
			throw new FileNotFoundException("File of type "+fileType+" is duplicate in dir: "+dir.getAbsolutePath());
		else
			return files.get(0);
	}
	
//	public static boolean isOrigFileBrowserCompatible(File dir) throws Exception {		
//		File origFile = getOrigFile(dir);
//		String ext = FilenameUtils.getExtension(origFile.getName()).toLowerCase();
//		
//		if (ext.endsWith("jpg") || ext.endsWith("jpeg") || ext.endsWith("png"))
//				return true;
//		else
//			return false;
//	}
	
	public static File getOrigFile(File dir) throws FileNotFoundException {
		return getFile(dir, FILE_TYPE_ORIG);
	}

	public static File getBrowserFile(File dir) throws FileNotFoundException {
		return getFile(dir, FILE_TYPE_BROWSER);
	}	
	
	public static File getViewFile(File dir) throws FileNotFoundException {
		return getFile(dir, FILE_TYPE_VIEW);
	}
	
	public static File getBinFile(File dir) throws FileNotFoundException {
		return getFile(dir, FILE_TYPE_BIN);
	}
	
	public static File getThumbFile(File dir) throws FileNotFoundException  {
		return getFile(dir, FILE_TYPE_THUMB);
	}
	
	public static File getMetadataFile(File dir) throws FileNotFoundException{
		File f = new File(dir.getAbsolutePath()+"/"+METADATA_FN);
		if (!f.exists())
			throw new FileNotFoundException("Metadata file not found: "+f.getAbsolutePath());
		
		return f;
	}

}
