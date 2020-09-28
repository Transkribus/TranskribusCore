package eu.transkribus.core.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * 
 * Specification for {@link LocalDocReader}
 * 
 * @author m3ssman
 *
 */
public class LocalDocReaderTest {
	
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();
	
	/**
	 * 
	 * Load fails if XML exists but is invalid
	 * 
	 * @throws IOException
	 */
	@Test(expected = IOException.class)
	public void testLoadFailsOnInvalidFile() throws IOException {
		String projectName = "1667522809_J_0013_0001";
		File projectFolder = tmpFolder.newFolder(projectName);
		File altoSubFolder = new File(projectFolder, "alto");
		File altoFile = new File(altoSubFolder, "1667522809_J_0013_0001.xml");
		FileUtils.writeStringToFile(altoFile, "");
		String imageName = "1667522809_J_0013_0001";
		File tifFile = new File(projectFolder, imageName + ".tif");
		BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY); 
		ImageIO.write(bufferedImage, "TIFF", tifFile);
		
		String projectPath = projectFolder.getAbsolutePath();
		LocalDocReader.load(projectPath);
	}
	
	/**
	 * 
	 * Test: 
	 * Exact match of image name and XML name (minus file extensions):
	 * XML and Image can be matched
	 * 
	 * {@link LocalDocReader#findXml(imgName, xmlInputDir, ignorePrefix)}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadALTOFileWithSameName() throws IOException {
		String projectName = "1667522809_J_0013_0001";
		File projectFolder = tmpFolder.newFolder(projectName);
		File altoSubFolder = new File(projectFolder, "alto");
		File altoFile = new File(altoSubFolder, "1667522809_J_0013_0001.xml");
		FileUtils.writeStringToFile(altoFile, "");
		
		String imageName = "1667522809_J_0013_0001";
		File actual = LocalDocReader.findXml(imageName, altoSubFolder, true);
		
		assertNotNull(actual);
		assertEquals("1667522809_J_0013_0001.xml", actual.getName());
	}
	
	/**
	 * 
	 * Test: 
	 * Put multiple matching files in the folder, prefer the one with an exact match.
	 * 
	 * {@link LocalDocReader#findXml(imgName, xmlInputDir, ignorePrefix)}
	 * 
	 * @throws IOException
	 */	
	@Test
	public void testLoadALTOFilePreferSameName() throws IOException {
		String projectName = "1667522809_J_0013_0001";
		File projectFolder = tmpFolder.newFolder(projectName);
		File altoSubFolder = new File(projectFolder, "alto");
		File altoFile = new File(altoSubFolder, "1667522809_J_0013_0001.xml");
		FileUtils.writeStringToFile(altoFile, "");
		File altoFile2 = new File(altoSubFolder, "0_i_am_also_matching_but_listed_before_1667522809_J_0013_0001.xml");
		FileUtils.writeStringToFile(altoFile2, "");
		
		String imageName = "1667522809_j_0013_0001"; // note the lowercase 'j' letter -> case insensitity!
		File actual = LocalDocReader.findXml(imageName, altoSubFolder, true);
		
		assertNotNull(actual);
		assertEquals("1667522809_J_0013_0001.xml", actual.getName());
	}	
	
	/**
	 * 
	 * Test: 
	 * Put multiple matching files in the folder, prefer the one with an exact match.
	 * 
	 * {@link LocalDocReader#findXml(imgName, xmlInputDir, ignorePrefix)}
	 * 
	 * @throws IOException
	 */	
	@Test
	public void testLoadALTOFileCaseInsensitive() throws IOException {
		String projectName = "1667522809_J_0013_0001";
		File projectFolder = tmpFolder.newFolder(projectName);
		File altoSubFolder = new File(projectFolder, "alto");
		File altoFile = new File(altoSubFolder, "i_am_a_file.xml");
		FileUtils.writeStringToFile(altoFile, "");
		
		String imageName = "I_AM_A_FILE";
		File actual = LocalDocReader.findXml(imageName, altoSubFolder, true);
		
		assertNotNull(actual);
		assertEquals("i_am_a_file.xml", actual.getName());
	}	

	
	/**
	 * 
	 * Test: 
	 * Match image name and XML name with additional extension?
	 * XML and Image can be matched
	 * 
	 * {@link LocalDocReader#findXml(imgName, xmlInputDir, ignorePrefix)}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadALTOFileWithGTExtension() throws IOException {
		String projectName = "1667522809_J_0013_0001";
		File projectFolder = tmpFolder.newFolder(projectName);
		File altoSubFolder = new File(projectFolder, "alto");
		File altoFile = new File(altoSubFolder, "1667522809_J_0013_0001.gt.art.xml");
		FileUtils.writeStringToFile(altoFile, "");
		
		String imageName = "1667522809_J_0013_0001";
		File actual = LocalDocReader.findFile(imageName, altoSubFolder, "xml");
		
		assertNotNull(actual);
		assertEquals("1667522809_J_0013_0001.gt.art.xml", actual.getName());
	}
	
	
	/**
	 * 
	 * Test: 
	 * Match image name and XML name with large additional name section?
	 * XML and Image can be matched
	 * 
	 * {@link LocalDocReader#findXml(imgName, xmlInputDir, ignorePrefix)}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadALTOFileWithLargeSuffix() throws IOException {
		String projectName = "1667522809_J_0025_0384";
		File projectFolder = tmpFolder.newFolder(projectName);
		File altoSubFolder = new File(projectFolder, "alto");
		File altoFile = new File(altoSubFolder, "1667522809_J_0025_0384_2350x350_6425x5300.xml");
		FileUtils.writeStringToFile(altoFile, "");
		
		String imageName = "1667522809_J_0025_0384";
		File actual = LocalDocReader.findFile(imageName, altoSubFolder, "xml");
		
		assertNotNull(actual);
		assertEquals("1667522809_J_0025_0384_2350x350_6425x5300.xml", actual.getName());
	}
	
	
	@Test
	public void testListImgFiles() throws IOException {
		String projectName = "1667522809_J_0013_0001";
		File projectFolder = tmpFolder.newFolder(projectName);
		String imageName = "1667522809_J_0013_0001";
		File tifFile = new File(projectFolder, imageName + ".tif");
		FileUtils.writeByteArrayToFile(tifFile, new byte[] {});
		
		int nrOfFiles1 = 0, nrOfFiles2 = 0;
		nrOfFiles1 = LocalDocReader.findImgFiles(projectFolder).size();
		nrOfFiles2 = LocalDocReader.findImgFilenames(projectFolder).size();
		assertEquals(nrOfFiles1, nrOfFiles2);
	}

}
