package eu.transkribus.core.model.beans.pagecontent.filter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.util.PageXmlUtils;

/**
 * Filter that removes line-regions that contain specific text tags.
 * <br>
 * Use case: Users want to have the HTR training ignore lines with unclear or gap tags. 
 */
public class TagPageContentFilter implements IPageContentFilter {
	Logger logger = LoggerFactory.getLogger(TagPageContentFilter.class);
	private int removedLinesCount;
	
	/**
	 * Map with tags to remove and a counter keeping track of removals 
	 */
	private Map<String, Integer> tagMap;
	
	public TagPageContentFilter(String...tags) {
		removedLinesCount = 0;
		tagMap = new HashMap<>();
		for(String t : tags) {
			tagMap.put(t, 0);
		}
	}
	
	@Override
	public void doFilter(PcGtsType pc) {
		int removeCount = 0;
		List<TextRegionType> regions = PageXmlUtils.getTextRegions(pc);
		for(TextRegionType r : regions) {
			Iterator<TrpTextLineType> lineIt = ((TrpTextRegionType) r).getTrpTextLine().iterator();
			while(lineIt.hasNext()) {
				TrpTextLineType l = lineIt.next();
				//What's the difference between getTagList() and getCustomTagList()?
				CustomTagList tagList = l.getCustomTagList();
								
				boolean doRemove = false;
				//use strings to discriminate tags. No easy way for doing this by CustomTag type equivalence!?
				for(CustomTag t : tagList.getTags()) {
					logger.trace("Checking tag: {}", t);
					if(tagMap.containsKey(t.getTagName())) {
						logger.debug("Found tag to be removed in line {}: {}", l.getId(), t.getTagName());
						doRemove = true;
						final int tagCount = tagMap.get(t.getTagName());
						tagMap.put(t.getTagName(), tagCount + 1);
					}
				}
				
				if(doRemove) {
					logger.info("Discarding line {} containing discriminated custom tags: {}", l.getId(), tagList.getCustomTag());
					lineIt.remove();
					removeCount++;
					continue;
				}
			}
		}
		removedLinesCount += removeCount;
		logger.debug("Removed {} lines", removeCount);	
	}
	
	public int getRemovedLinesCount() {
		return removedLinesCount;
	}
}
