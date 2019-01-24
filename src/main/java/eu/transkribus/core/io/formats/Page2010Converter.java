package eu.transkribus.core.io.formats;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.primaresearch.dla.page.Page;
import org.primaresearch.dla.page.converter.PageConverter;
import org.primaresearch.dla.page.io.FileInput;
import org.primaresearch.dla.page.io.InputSource;
import org.primaresearch.dla.page.io.UrlInput;
import org.primaresearch.dla.page.io.xml.XmlInputOutput;
import org.primaresearch.dla.page.io.xml.XmlPageReader;
import org.primaresearch.io.FormatVersion;
import org.primaresearch.io.UnsupportedFormatVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.LocalDocConst;
import eu.transkribus.core.io.UnsupportedFormatException;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.XmlUtils;

public class Page2010Converter {
	private static final Logger logger = LoggerFactory.getLogger(Page2010Converter.class);
	// JPageConverter target constants
	private static final Map<XmlFormat, String> convTargets = new HashMap<>();
	static {
		convTargets.put(XmlFormat.PAGE_2013, "2013-07-15");
		convTargets.put(XmlFormat.PAGE_2010, "2010-03-19");
	}
	
	/**
	 * Migrate PAGE to current version on complete dir.<br>
	 * 
	 * This method works on directories that contain different page format
	 * versions. Backups of old files are created. Calling the method on a folder
	 * with all new page XMLs has no effect.
	 * @param inputDir the directory with page XMLs
	 * @param backupDirStr the subdirectory name where to put backups
	 * @throws IOException if something can't be read or written
	 * @throws UnsupportedFormatException some file is not a valid page XML version
	 */
	public static void updatePageFormat(final File inputDir, final String backupDirStr)
			throws IOException, UnsupportedFormatException {
		logger.info("Converting old Page Xml files in inputDir: " + inputDir.getAbsolutePath());

		//validate input path (needed, in case this method is called separately)

		if (!inputDir.canWrite()) {
			logger.info("IS NOT WRITEABLE!");
			throw new IOException(inputDir.getAbsolutePath() + " is not writeable.");
		}

		if (!inputDir.isDirectory()) {
			throw new IOException(inputDir.getAbsolutePath() + " is not a directory.");
		}

		File[] xmlFiles = inputDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml") && !name.equals(LocalDocConst.METADATA_FILENAME);
			}
		});

		if (xmlFiles.length == 0) {
			throw new IOException(inputDir.getAbsolutePath() + " does not contain XML files.");
		}

		logger.info("Found XML files: " + xmlFiles.length);

		//prepare tmp Dir for new Files
		File tmpDir = new File(inputDir.getAbsolutePath() + File.separator + "page_new_tmp");
		if (!tmpDir.exists()) {
			tmpDir.mkdir();
		}
		File backupDir = new File(backupDirStr);
		if (!backupDir.exists()) {
			backupDir.mkdirs();
		}

		//keep lists of converted old and new files
		List<File> toBeConverted = new LinkedList<>();
		List<File> newFiles = new LinkedList<>();
		for (File f : xmlFiles) {
			XmlFormat format = XmlUtils.getXmlFormat(f);
			if (Page2010Converter.isSupportedFormat(format)
					&& !format.equals(PageXmlUtils.TRP_PAGE_VERSION)) {
				logger.debug("Converting file: " + f.getAbsolutePath());
				toBeConverted.add(f);
				File outFile = new File(tmpDir.getAbsolutePath() + File.separator + f.getName());
				Page2010Converter.convert(f, outFile);
				newFiles.add(outFile);
			} else {
				logger.debug("Keeping file: " + f.getAbsolutePath());
			}
		}

		logger.debug("Moving old files to backup location.");
		for (File f : toBeConverted) {
			Path source = f.toPath();
			Path target = new File(backupDir.getAbsolutePath() + File.separator + f.getName())
					.toPath();
			logger.debug(source.toAbsolutePath() + " -> " + target.toAbsolutePath());
			Files.move(source, target);
		}
		logger.debug("Moving new files to original location.");
		for (File f : newFiles) {
			Path source = f.toPath();
			Path target = new File(inputDir.getAbsolutePath() + File.separator + f.getName())
					.toPath();
			logger.debug(source.toAbsolutePath() + " -> " + target.toAbsolutePath());
			Files.move(source, target);
		}

		logger.debug("Removing tmp directory...");
		if (tmpDir.listFiles().length != 0) {
			logger.error("tmp directory is not empty! Aborting");
			throw new IOException("Some files might have not been converted correctly! Check "
					+ tmpDir.getAbsolutePath());
		}

		if (tmpDir.delete()) {
			logger.info("Conversion done!");
		} else {
			throw new IOException("Tmp Dir could not be deleted! " + tmpDir.getAbsolutePath());
		}
	}

	public static File convert(File input, File output) throws IOException {
		
		PageConverter conv = new PageConverter();
		conv.setTargetSchema(convTargets.get(PageXmlUtils.TRP_PAGE_VERSION));
		logger.info("Writing conversion output to: " + output.getAbsolutePath());

		//this method may fail but throws no Exception
		conv.run(input.getAbsolutePath(), output.getAbsolutePath());
		//check result roughly
		if (!output.exists() || output.length() == 0) {
			throw new IOException("PageConverter failed on file: " + input.getAbsolutePath());
		}
		return output;
	}
	
	public static File updatePageFormatSingleFile(File pageConverted, File pageSource) throws IOException {
				
//		File backupDir = new File(backupDirStr);
//		if(!backupDir.exists()){
//			backupDir.mkdirs();
//		}
		
		//output
		File tmpFile = new File(pageSource.getAbsolutePath() + ".tmp"); 

		//do conversion
		convert(pageSource, tmpFile);
		
		//move input to backup path
//		File backup = new File(backupDir.getAbsolutePath() + File.separator + input.getName());
//		Files.move(input.toPath(), backup.toPath());
		//move output into original file's place
		Path outputPath = Files.move(tmpFile.toPath(), pageConverted.toPath());
		
		return outputPath.toFile();
	}
	
	public static boolean isSupportedFormat(XmlFormat format){
		return convTargets.containsKey(format);
	}

	@Deprecated
	public static String getFormatVersion(final URL tsUrl) throws IOException {
		InputSource source = new UrlInput(tsUrl);
		return getFormatVersion(source);
	}

	@Deprecated
	public static String getFormatVersion(final File tsFile) throws IOException {
		InputSource source = new FileInput(tsFile);
		return getFormatVersion(source);
	}

	/**
	 * TODO Remove this proprietary stuff
	 * @param source source page File
	 * @return the version String 
	 * @throws IOException if the format version can't be determined
	 */
	@Deprecated
	private static String getFormatVersion(InputSource source) throws IOException {
		XmlPageReader reader = XmlInputOutput.getReader();
		String vStr = "";
		try {
			Page page = reader.read(source);
			//this call returns an instance of XmlFormatVersion
			FormatVersion v = page.getFormatVersion();
			//this will return the date String identifying the format version, e.g.: 2010-03-19	
			vStr = v.toString();
		} catch (UnsupportedFormatVersionException ufve) {
			throw new IOException("The XML at " + source.toString()
					+ " is not a known pagecontent XML format!");
		}

		return vStr.toString();
	}
}
