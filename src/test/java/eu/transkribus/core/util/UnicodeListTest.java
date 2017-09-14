package eu.transkribus.core.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class UnicodeListTest {

	@Test public void testRegexes() {
		assertTrue("3456".matches(UnicodeList.FOUR_DIGIT_HEX_VALUE_REGEX));
		assertTrue("34567".matches(UnicodeList.FOUR_DIGIT_HEX_VALUE_REGEX));
		assertFalse("asdfasdf".matches(UnicodeList.FOUR_DIGIT_HEX_VALUE_REGEX));
		assertTrue("0fD1".matches(UnicodeList.FOUR_DIGIT_HEX_VALUE_REGEX));
		
		assertTrue("U+34567".matches(UnicodeList.UNICODE_VALUE_REGEX));
		assertFalse("+3456".matches(UnicodeList.UNICODE_VALUE_REGEX));
	}
	
	@Test public void testRanges() {
//		// test old ranges:
//		assertFalse("U+3456-8907".matches(TrpVirtualKeyboards.OLD_UNICODE_RANGE_REGEX));
//		assertTrue("3456-8907".matches(TrpVirtualKeyboards.OLD_UNICODE_RANGE_REGEX));
//		assertFalse("U+3456-U+8907".matches(TrpVirtualKeyboards.OLD_UNICODE_RANGE_REGEX));
		
		// test new ranges:
		assertTrue("U+3456-8907".matches(UnicodeList.UNICODE_RANGE_REGEX));
		assertTrue("3456-U+8907".matches(UnicodeList.UNICODE_RANGE_REGEX));
		assertTrue("3456-8907".matches(UnicodeList.UNICODE_RANGE_REGEX));
		assertTrue("U+3456-U+8907".matches(UnicodeList.UNICODE_RANGE_REGEX));
		
		assertFalse("U++3456-U+8907".matches(UnicodeList.UNICODE_RANGE_REGEX));
		assertFalse("++3456-8907".matches(UnicodeList.UNICODE_RANGE_REGEX));
		assertFalse("U3456-U+8907".matches(UnicodeList.UNICODE_RANGE_REGEX));
	}

}
