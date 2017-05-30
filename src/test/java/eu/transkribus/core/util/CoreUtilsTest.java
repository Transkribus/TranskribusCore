package eu.transkribus.core.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreUtilsTest {
//	private static final Logger logger = LoggerFactory.getLogger(CoreUtilsTest.class);
	
	/**
	 * Test strategy: convert a pages string into page indices, convert those to a pages string,
	 * then convert this string again to pages indices and check if both page indices are the same! 
	 */
	@Test
	public void testRangeStringToListAndBack() throws Exception {
		String[] strs = new String[] {
				"1-2,4,7,10-12", // regular test case for a compressed pages string
				"1,2,3,4,5,10-21,45,46,47,48-50" // test case where multiple subsequent values get "compressed" when converting into a pages string 
		};
		int nPages = 50;
		
		for (int i=0; i<2; ++i) {
			String str = strs[i];
			
			List<Integer> pageIndices = CoreUtils.parseRangeListStrToList(str, nPages);
			System.out.println("pageIndices = "+pageIndices);

			String createdPagesStr = CoreUtils.getRangeListStrFromList(pageIndices);
			System.out.println("createdPagesStr = "+createdPagesStr);
			
			List<Integer> createdPageIndices = CoreUtils.parseRangeListStrToList(createdPagesStr, nPages);
			System.out.println("createdPageIndices = "+pageIndices);
			
			Assert.assertTrue("createdPageIndices contains wrong values!", 
					createdPageIndices.size() == pageIndices.size() && createdPageIndices.containsAll(pageIndices));		
		}
		
	}
	
	/**
	 * Test page range String parsing and inversion of result
	 */
	@Test
	public void testRangeStringMethods() {
		final String testString = "1-2,4-5,7-10";
		final int nPages = 14;
		
		Set<Integer> expectedSet = new HashSet<>();
		Set<Integer> expectedSetInverted = new HashSet<>();
		final Integer[] pageIndices = {0, 1, 3, 4, 6, 7, 8, 9};
		final Integer[] invertedPageIndices = {2, 5, 10, 11, 12, 13};
		expectedSet.addAll(Arrays.asList(pageIndices));
		expectedSetInverted.addAll(Arrays.asList(invertedPageIndices));
		
		Set<Integer> result = null;
		try {
			result = CoreUtils.parseRangeListStr(testString, nPages);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		
		Assert.assertTrue("Result contains wrong values!", 
				result.size() == expectedSet.size() && result.containsAll(expectedSet));
		
		Set<Integer> invertedResult = CoreUtils.invertPageIndices(result, 14);
		
		Assert.assertTrue("Inverted result contains wrong values!", 
				invertedResult.size() == expectedSetInverted.size() && invertedResult.containsAll(expectedSetInverted));
	}
}
