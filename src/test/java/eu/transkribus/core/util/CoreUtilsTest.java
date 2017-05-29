package eu.transkribus.core.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class CoreUtilsTest {
	
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
