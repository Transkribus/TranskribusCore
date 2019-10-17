package eu.transkribus.core.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.dea.fimgstoreclient.FimgStoreGetClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.builder.CommonExportPars;
import eu.transkribus.core.util.SysUtils;

public class DocExporterTest {
	private static final Logger logger = LoggerFactory.getLogger(DocExporterTest.class);
	
	private DocExporter exporter;
	
	@Before
	public void init() throws IOException {
		exporter = newDocExporter();
	}
	
	private File createTempDir() throws IOException {
		return Files.createTempDirectory("DocExporterTest-").toFile();
	}
	
	/**
	 * Cleaning export directory from a NFS share failed when streams were not properly closed during the export.
	 * For each open file NFS contains a hidden .nfsXXXX file, that will show up in the IOException message when 
	 * calling FileUtils::deleteDirectory more than once (see http://nfs.sourceforge.net/#faq_d2).
	 * Those files will block deletion and the export dir will be a mess eventually.
	 * 
	 * @return
	 * @throws IOException
	 */
	private File createTempDirOnNfs() throws IOException {
		return Files.createTempDirectory(new File("/mnt/transkribus/export_test").toPath(), "DocExporterTest-").toFile();
	}

	/**
	 * create a standard DocExporter. Method may be overriden to set up a ServerSideDocExporter in specific tests.
	 * 
	 * @return
	 */
	protected DocExporter newDocExporter() {
		return new DocExporter(new FimgStoreGetClient("files-test.transkribus.eu", "/"));
	}

	@Test
	public void testTeiExportAndDelete() throws Exception {
		final String testDocPath;
		if(SysUtils.isWin()){
			testDocPath = "X:/TRP/Bentham_box_002";
		} else if (SysUtils.isLinux()) {
			testDocPath = "/mnt/dea_scratch/TRP/Bentham_box_002";
		} else {
			//do not fail on unsupported OS
			return;
		}
		
		File tmpDir = createTempDir();
		
		TrpDoc doc = LocalDocReader.load(testDocPath);
		File teiFile = exportTei(tmpDir, doc);
		Assert.assertTrue(teiFile.isFile());
		final float filesizeMb = teiFile.length() / 1000000f;
		Assert.assertTrue(filesizeMb > 0);
		logger.info("Wrote TEI file to {}, size = {} MB", teiFile.getAbsolutePath(), filesizeMb);
		
		//test if cleanup of the exportDir works.
		try {
			FileUtils.deleteDirectory(tmpDir);
			Assert.assertTrue(!tmpDir.exists());
		} catch (IOException e) {
			logger.error("Cleanup of tmpDir failed!", e);
			throw e;
		}
	}
	
	@Test
	public void testAltoWordLevelExportAndDelete() throws Exception {
		final String testDocPath;
		if(SysUtils.isWin()){
			testDocPath = "X:/TRP/Bentham_box_002";
		} else if (SysUtils.isLinux()) {
			testDocPath = "/mnt/dea_scratch/TRP/Bentham_box_002";
		} else {
			//do not fail on unsupported OS
			return;
		}
		
		File tmpDir = createTempDir();
		
		TrpDoc doc = LocalDocReader.load(testDocPath);
		File outputDir = exportAlto(tmpDir, doc);
		Assert.assertTrue(outputDir.isDirectory());
		logger.info("Wrote export to {}", outputDir.getAbsolutePath());
		
		//test if cleanup of the exportDir works.
		try {
			FileUtils.deleteDirectory(tmpDir);
			Assert.assertTrue(!tmpDir.exists());
		} catch (IOException e) {
			logger.error("Cleanup of tmpDir failed!", e);
			throw e;
		}
	}
	
	protected File exportAlto(File tmpDir, TrpDoc doc) throws Exception {
		try {
			CommonExportPars commonPars = new CommonExportPars();
			commonPars.setPages("1-" + doc.getNPages());
			commonPars.setDoExportAltoXml(true);
			commonPars.setSplitIntoWordsInAltoXml(true);
			commonPars.setDir(tmpDir.getAbsolutePath());
			commonPars.setDoWriteMets(true);
			return exporter.exportDoc(doc, commonPars);
		} catch (Exception e) {
			logger.error("TEI export failed!", e);
			throw e;
		}
	}
	
	protected File exportTei(File tmpDir, TrpDoc doc) throws Exception {
		try {
			CommonExportPars commonPars = new CommonExportPars();
			commonPars.setPages("1-" + doc.getNPages());
			File exportFile = new File(tmpDir, "teiExportTest.xml");
			String exportFilename = exportFile.getAbsolutePath();
			exporter.writeTEI(doc, exportFilename, commonPars, null);
			return exportFile;
		} catch (Exception e) {
			logger.error("TEI export failed!", e);
			throw e;
		}
	}
}
