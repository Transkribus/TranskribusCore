package eu.transkribus.core.io;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class ExportFilePatternUtilsTest {
	
	@Test
	public void testFilenamePattern() {
		final String invalidPattern = "${filename}_${${pageId}_${pageNr}";
		Assert.assertFalse("Filename pattern was erroneously evaluated to be valid: " + invalidPattern, 
				ExportFilePatternUtils.isFileNamePatternValid(invalidPattern));
		
		final String validPattern = "${filename}_${pageId}_${pageNr}";
		Assert.assertTrue("Filename pattern was erroneously evaluated to be invalid: " + validPattern, 
				ExportFilePatternUtils.isFileNamePatternValid(validPattern));
		
		final String exampleFilename = "test.jpg";
		final String exampleBaseName = FilenameUtils.getBaseName(exampleFilename);
		final int pageNr = 7;
		final String actualResult = ExportFilePatternUtils.buildBaseFileName(validPattern, exampleFilename, 123, 456, "AAAAA", pageNr);
		//ExportFilePatternUtils will leftpad the pageNr
		final String pageIdStr =  StringUtils.leftPad(""+pageNr, ExportFilePatternUtils.PAGE_NR_LEFTPAD_TO_SIZE, '0');
		final String expectedResult = exampleBaseName + "_123_" + pageIdStr;
		Assert.assertEquals(expectedResult, actualResult);
		
		try {
			String thisShouldNeverBeAssigned = ExportFilePatternUtils.buildBaseFileName(invalidPattern, exampleFilename, 123, 456, "AAAAA", 7);
			Assert.fail("The filename '" + thisShouldNeverBeAssigned + "' was created by pattern '" + invalidPattern +"' although it was evaluated to be invalid!");
		} catch (IllegalArgumentException e) {
			//IllegalArgumentException == success
		}
	}
}
