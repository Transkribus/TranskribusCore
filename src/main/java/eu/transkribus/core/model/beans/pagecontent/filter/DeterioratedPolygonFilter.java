package eu.transkribus.core.model.beans.pagecontent.filter;

import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.util.PointStrUtils;

/**
 * Filter that removes "deteriorated" text- and (optionally) line-regions, 
 * i.e. pointStr in Coords does not obey correct format or contains less than 3 points.
 * <br><br>
 * FIXME: tables and tableCells are not taken into account!
 */
public class DeterioratedPolygonFilter implements IPageContentFilter {
	
	Logger logger = LoggerFactory.getLogger(DeterioratedPolygonFilter.class);
	
	private final boolean doFilterLines;
	private int overallRemovedLineCount, overallRemovedRegionCount;
	
	public DeterioratedPolygonFilter(boolean doFilterLines) {
		this.doFilterLines = doFilterLines;
		overallRemovedLineCount = 0;
		overallRemovedRegionCount = 0;
	}
	
	@Override
	public void doFilter(PcGtsType pc) {
		int removedLineCount = 0;
		int removedRegionCount = 0;
		Iterator<TrpRegionType> regionIt = pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion().iterator();
		while(regionIt.hasNext()) {
			RegionType r = regionIt.next();
			
			if(isDeteriorated(r.getCoords().getPoints())) {
				logger.info("Discarding region {} due to invalid points string: {}", r.getId(), r.getCoords().getPoints());
				regionIt.remove();
				removedRegionCount++;
				continue;
			}
								
			if(doFilterLines && r instanceof TextRegionType) {
				Iterator<TextLineType> lineIt = ((TextRegionType) r).getTextLine().iterator();
			
				while(lineIt.hasNext()) {
					TextLineType l = lineIt.next();
					if(isDeteriorated(l.getCoords().getPoints())) {
						logger.info("Discarding line {} due to invalid points string: {}", l.getId(), l.getCoords().getPoints());
						lineIt.remove();
						removedLineCount++;
						continue;
					}
				}
			}
		}
		logger.debug("Removed {} elements from PcGtsType: {} lines, {} regions", 
				(removedLineCount + removedRegionCount), removedLineCount, removedRegionCount);
		overallRemovedLineCount += removedLineCount;
		overallRemovedRegionCount += removedRegionCount;
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
	
	public int getRemovedLineCount() {
		return overallRemovedLineCount;
	}
	
	public int getRemovedRegionCount() {
		return overallRemovedRegionCount;
	}
}
