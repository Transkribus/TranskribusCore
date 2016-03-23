package eu.transkribus.core.model.beans.customtags;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.util.OverlapType;

import org.junit.Assert;
import org.junit.Test;

public class CustomTagListTest /*extends CustomTagList*/ {
	
//	public CustomTagListTest(String customTag) {
//		super(customTag);
		// TODO Auto-generated constructor stub
//	}

	private final static Logger logger = LoggerFactory.getLogger(CustomTagListTest.class);
	
	static Random rand = new Random();
	
	public static void checkIntegrity(CustomTagList tl) {
		// test if styles were merged correctly:
		int c=0;
		
		String lastTagName=null;
		List<String> tagNamesEncountered=new ArrayList<>();
		boolean indexed=false;
		int length = tl.getTextLength();
		CustomTag wholeRangeTag = new CustomTag("test", 0, length);
		
		logger.trace("checking integrity of all tags in customtaglist!");
		
		for (String tn : tl.getIndexedTagNames()) {
			for (CustomTag ct : tl.getIndexedTags(tn)) {
				if (ct.isIndexed()) {
					Assert.assertTrue("CustomTag not inside list!", wholeRangeTag.getOverlapType(ct)==OverlapType.INSIDE);

					// NEEDED????
					if (!ct.getTagName().equals(lastTagName)) {
						c=0;
						Assert.assertFalse("Indexed tags are mixed!", tagNamesEncountered.contains(ct.getTagName()));
//						
						tagNamesEncountered.add(ct.getTagName());
					}
					
					indexed=true;
					
					Assert.assertTrue("Offset below last range: "+ct.getOffset()+" / "+c, ct.getOffset()>=c);
					Assert.assertTrue("length <= 0!", ct.getLength()>0);
		//			Assert.assertTrue("offset in range!", ct.getOffset()>=0);
					c = ct.getOffset() + ct.getLength();
				} else {
					Assert.assertFalse("Multiple non-indexed tag-name!", ct.getTagName().equals(lastTagName));				
				}
				
				Assert.assertTrue("Non-indexed and indexed tags are mixed!", ct.isIndexed()==indexed);
				lastTagName = ct.getTagName();
			}
		}
		
		for (CustomTag ct : tl.getNonIndexedTags()) {
			Assert.assertFalse("Multiple non-indexed tag-name: "+ct.getTagName(), ct.getTagName().equals(lastTagName));
			lastTagName = ct.getTagName();
		}
		
//		for (CustomTag ct : tl.getTags()) {
//			if (ct.isIndexed()) {
//				Assert.assertTrue("CustomTag not inside list!", wholeRangeTag.getOverlapType(ct)==OverlapType.INSIDE);
//
//				// NEEDED????
//				if (!ct.getTagName().equals(lastTagName)) {
//					c=0;
////					Assert.assertFalse("Indexed tags are mixed!", tagNamesEncountered.contains(ct.getTagName()));
////					
//					tagNamesEncountered.add(ct.getTagName());
//				}
//				
//				indexed=true;
//				
//				Assert.assertTrue("Offset below last range: "+ct.getOffset()+" / "+c, ct.getOffset()>=c);
//				Assert.assertTrue("length <= 0!", ct.getLength()>0);
//	//			Assert.assertTrue("offset in range!", ct.getOffset()>=0);
//				c = ct.getOffset() + ct.getLength();
//			} else {
//				Assert.assertFalse("Multiple non-indexed tag-name!", ct.getTagName().equals(lastTagName));				
//			}
//			
//			Assert.assertTrue("Non-indexed and indexed tags are mixed!", ct.isIndexed()==indexed);
//			lastTagName = ct.getTagName();
//		}
		
		try {
			tl.checkAllTagRanges();
		}
		catch (IndexOutOfBoundsException ie) {
			Assert.assertTrue("Not all tags were in the line range!", false);
			logger.error(ie.getMessage(), ie);
		}		
		
//		for (CustomTag t : tl.getTags()) {
//			System.out.println("t = "+t);
//		}
		
		logger.trace("checking integrity of customtaglist at each position!");
		Set<String> tagNames = tl.getIndexedTagNames();
		for (int i=0; i<tl.getTextLength(); ++i) {
			for (String tn : tagNames) {
//				System.out.println("tn = "+tn);
				List<CustomTag> tagsAtOffset = tl.getOverlappingTags(tn, i, 0);
				Assert.assertTrue("More than two tags with same name at a single selection: "+i+", n = "+tagsAtOffset.size(), tagsAtOffset.size()<=2); // !!!!
				if (tagsAtOffset.size() == 2) {
					Assert.assertTrue("Two tags with same name at a single selection but not in a series: "+tagsAtOffset.get(0)+" - "+tagsAtOffset.get(1),
							tagsAtOffset.get(0).getEnd() == tagsAtOffset.get(1).getOffset());
					
				}
			}
		}
	}
	
//	@Ignore
	@Test public void testSimpleAddOrMergeTagWithTextStyles() {
		TrpTextLineType line = new TrpTextLineType(new TrpTextRegionType(new TrpPageType()));
		line.setUnicodeText("Hello world!", null);
		CustomTagList tl = new CustomTagList(line);
				
		TextStyleTag tst = new TextStyleTag(0, 10);
		tst.setFontFamily("testFont");
		
		tl.addOrMergeTag(tst, null);
		TextStyleTag ts1 = new TextStyleTag(2, 5);
		ts1.setBold(true);
		tl.addOrMergeTag(ts1, null);
		logger.trace(tl.toString());
		Assert.assertEquals("Nr of text styles must be 3!", 3, tl.getTags().size());
		
		TextStyleTag ts2 = new TextStyleTag(3, 4);
		ts2.setItalic(true);
		tl.addOrMergeTag(ts2, null);
		Assert.assertEquals("Nr of text styles must be 4!", 4, tl.getTags().size());
		
		logger.trace(tl.toString());
		
//		Assert.assertEquals("Nr of text styles must be 5!", 5, tl.getTags().size());
		Assert.assertTrue("offset = 0", tl.getTags().get(0).getOffset()==0);
		CustomTag last = tl.getTags().get(tl.getTags().size()-1);
		Assert.assertTrue("offset+length = 10", (last.getOffset()+last.getLength())==10);
	}
	
	@Test public void testCommonTag() {
		TrpTextLineType line = new TrpTextLineType(new TrpTextRegionType(new TrpPageType()));
		line.setUnicodeText("Hello world!", null);
		CustomTagList tl = new CustomTagList(line);
		
		TextStyleTag ts1 = new TextStyleTag(0, 10);
		ts1.setBold(true);
		tl.addOrMergeTag(ts1, null);
		logger.debug("ts1 = "+tl);
		
		TextStyleTag ts2 = new TextStyleTag(3, 3);
		ts2.setItalic(true);
		tl.addOrMergeTag(ts2, "italic");
		logger.debug("ts2 = "+tl);
//		ts2.setFontFamily("hello");
//		tl.addOrMergeTag(ts2, null);
		
//		TextStyleTag t1 = tl.getCommonIndexedCustomTag(TextStyleTag.TAG_NAME, 3, 3);
//		logger.debug("t1 = "+t1);
		
		Assert.assertEquals("Nr. of merged elements must be 3", 3, tl.getTags().size());
		
		TextStyleTag common = tl.getCommonIndexedCustomTag(TextStyleTag.TAG_NAME, 2, 6);
		TextStyleTag check = new TextStyleTag();
		check.setBold(true);
		check.setOffset(2);
		check.setLength(6);
		logger.debug("common = "+common);
		logger.debug("check = "+common);
		
		Assert.assertTrue("", common.equalsEffectiveValues(check, true));
	}
	
//	@Ignore
	@Test public void testAddBothOverlap() {
		TrpTextLineType line = new TrpTextLineType(new TrpTextRegionType(new TrpPageType()));
		line.setUnicodeText("Hello world and all parallel worlds!", null);
//		line.setUnicodeText("hello", who);
		
		CustomTagList tl = new CustomTagList(line);
//		int l=10;
		
		TextStyleTag ts1 = new TextStyleTag(2,3);
		ts1.setBold(true);
		ts1.setItalic(true);
		tl.addOrMergeTag(ts1, null);
		logger.debug("tl1 = "+tl);
		
		TextStyleTag ts2 = new TextStyleTag(6, 2);
		ts2.setBold(true);
		ts2.setSuperscript(true);
		tl.addOrMergeTag(ts2, null);
		logger.debug("tl2 = "+tl);
		
		TextStyleTag ts3 = new TextStyleTag(9, 11);
		tl.addOrMergeTag(ts3, null);
		logger.debug("tl3 = "+tl);
				
		Assert.assertEquals("Nr. of merged elements must be 2 --> empty tags shall be removed!", 2, tl.getTags().size());
	}
			
//	@Ignore
	@Test public void testMultipleRandomIndexedAddOrMergeTag() {
		TrpTextLineType line = new TrpTextLineType(new TrpTextRegionType(new TrpPageType()));
		line.setUnicodeText("Hello world!", null);
		CustomTagList tl = new CustomTagList(line);
		int textLength = tl.getTextLength();
		CustomTag wholeRangeTag = new CustomTag("test", 0, textLength);
		
		String[] nonIndexedTags = new String[] { "a_non_indexed", "b_ni", "c_balbla_non_indexed" };
		
		int rangeOfTags=textLength + 5; // + overlap to test exceptions when index out of bounds!
		final int N = (int)1e3;
		
		for (int i=0; i<N; ++i) {
//			int sizeBefore = tl.getIndexedTags("test").size();
			
			int o = rand.nextInt(rangeOfTags);
			int l = rand.nextInt(rangeOfTags-o)+1;
			
			CustomTag ct = null;
			
			int d = rand.nextInt(3);
			
			if (d==0) { // structure tag
				ct = new CustomTag("a_test_indexed", o, l);
			} else  if (d==1) { // text style tag
				ct = new TextStyleTag(o, l);
				
				((TextStyleTag)ct).setBold(rand.nextBoolean());
				((TextStyleTag)ct).setItalic(rand.nextBoolean());
				((TextStyleTag)ct).setMonospace(rand.nextBoolean());
			} else {
				ct = new CustomTag(nonIndexedTags[rand.nextInt(3)]);
				
//				ct = new CustomTag("non-indexed");
			}
			
//			logger.info("range: "+o+","+l);
//			CustomTag ct = new CustomTag("test", o, l);
			
			logger.trace("i="+i+"/"+N);
			logger.trace("adding custom tag: "+ct);
			logger.trace("list before = "+tl);
			try {
				tl.addOrMergeTag(ct, null);
				Assert.assertTrue("Indexed CustomTag was not inside but no exception thrown: "+ct,
						!ct.isIndexed() || wholeRangeTag.getOverlapType(ct)==OverlapType.INSIDE);
			} catch (IndexOutOfBoundsException ie) {
				Assert.assertTrue("CustomTag was inside but exception thrown: "+ct,
						wholeRangeTag.getOverlapType(ct)!=OverlapType.INSIDE);
				logger.trace("Exception for tag not inside: "+ct);
			}
			logger.trace("list after = "+tl);
			checkIntegrity(tl);
//			int sizeAfter = tl.getIndexedTags("test").size();
//			logger.debug("sizeAfter = "+sizeAfter);
		}
		logger.info("list = "+tl);
	}
	
	// TODO: add multiple inserts!
	
	
	
	public static void main(String [] args) {
		
		
		CustomTagListTest t = new CustomTagListTest();
		t.testCommonTag();
		
//		t.testAddBothOverlap();
//		t.testSimpleAddOrMergeTagWithTextStyles();
//		t.testMultipleRandomIndexedAddOrMergeTag();
	}

}
