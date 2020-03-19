package eu.transkribus.core.model.beans.pagecontent.filter;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.PointStrUtils;

/**
 * Filter that removes line-regions that contain specific text tags.
 * <br><br>
 * Yet untested!<br> 
 * <br>
 * Use case: Users want to have the HTR training ignore lines with unclear or gap tags.<br>
 * TODO: This will alter the nrOfTranscribedLines and nrOfWordsInLines of the transcript.<br>
 * Those numbers are summed up and written to the HTR's metadata.<br>
 * The computation has to be updated when applying this filter during HTR training!<br>
 * See https://github.com/Transkribus/TranskribusAppServerModules#69
 * 
 */
public class TagPageContentFilter implements IPageContentFilter {
	Logger logger = LoggerFactory.getLogger(TagPageContentFilter.class);
	
	private Set<String> tagSet;
	
	public TagPageContentFilter(String...tags) {
		tagSet = new HashSet<>();
		tagSet.addAll(Arrays.asList(tags));
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
					if(tagSet.contains(t.getTagName())) {
						logger.debug("Found tag to be removed: {}", t.getTagName());
						doRemove = true;
					}
				}
				
				if(doRemove) {
					logger.info("Discarding line {} containing discrimanated custom tag: {}", l.getId(), tagList.getCustomTag());
					lineIt.remove();
					removeCount++;
					continue;
				}
			}
		}
		logger.debug("DeterioratedPolygonFilter removed {} elements", removeCount);	
	}
	
	boolean isDeteriorated(String pointStr) {
		Collection<Point> regionPoints;
		try {
			regionPoints = PointStrUtils.parsePoints3(pointStr);
		} catch (NumberFormatException e) {
			logger.debug("Invalid number format in points: {}", pointStr);
			return true;
		}
		
		//check number of points
		if(regionPoints.size() < 3) {
			logger.debug("Less than 3 points: {}", pointStr);
			return true;
		}
		return false;
	}
}
