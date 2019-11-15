package eu.transkribus.core.model.beans.pagecontent_trp;

import java.awt.Rectangle;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.util.IntRange;
import eu.transkribus.core.util.PointStrUtils;

/**
 * Updated version (should) sort *regions* according to multi-column order 
 *  
 * @fixme visibility is set to package for now because the multi-column-sensitive sorting is non-transitive for degenerate cases and thus you should use
 * TrpShapeTypeUtils::sortShapesByCoordinates where Exceptions are handled!
 * @see {@link TrpShapeTypeUtils#sortShapesByReadingOrderOrCoordinates(java.util.List)}
 */
 class TrpElementCoordinatesComparator<T> implements Comparator<T> {
	private final static Logger logger = LoggerFactory.getLogger(TrpElementCoordinatesComparator.class);
	
	boolean forceCompareByYX = false;

	public TrpElementCoordinatesComparator() {
	}
	
	public TrpElementCoordinatesComparator(boolean forceCompareByYX) {
		this.forceCompareByYX=forceCompareByYX;
	}
	
	private boolean isRegionLineOrWord(T o) {
		return (o instanceof RegionType || TextLineType.class.isAssignableFrom(o.getClass()) || WordType.class.isAssignableFrom(o.getClass()));		
	}

	@Override
	public int compare(T o1, T o2) {
//		if (!isRegionLineOrWord(o1) || !isRegionLineOrWord(o2))
//			return 0;
		
		logger.trace("compare in TrpElementCoordinatesComparator4Columns");
		
		String coords1="", coords2="";
					
//			if (o1 instanceof PrintSpaceType) {
//				coords1 = ((TrpPrintSpaceType) o1).getCoords().getPoints();
//				coords2 = ((TrpPrintSpaceType) o2).getCoords().getPoints();
//			}		
		
		boolean performColumnSensitiveComparison = false;
		boolean sortByXY=false;
		
		if (o1 instanceof RegionType) {
			performColumnSensitiveComparison = true;
			RegionType r1 = (RegionType) o1;
			RegionType r2 = (RegionType) o2;
//				System.out.println("region1 id: " + r1.getId());
//				System.out.println("region2 id: " + r2.getId());
			if (r1.getCoords() != null && r2.getCoords() != null) {
				coords1 = r1.getCoords().getPoints();
				coords2 = r2.getCoords().getPoints();					
			}
		}
		else if (TextLineType.class.isAssignableFrom(o1.getClass())) {
			// if existing, take baseline to compare position of lines
			if (((TextLineType) o1).getBaseline() != null && ((TextLineType) o2).getBaseline() != null){
				coords1 = ((TextLineType) o1).getBaseline().getPoints();
				coords2 = ((TextLineType) o2).getBaseline().getPoints();
			} else { //fall back if there are no baselines
				coords1 = ((TextLineType) o1).getCoords().getPoints();
				coords2 = ((TextLineType) o2).getCoords().getPoints();					
			}
		}
		else if (o1 instanceof TrpBaselineType) {
			coords1 = ((TrpBaselineType) o1).getPoints();
			coords2 = ((TrpBaselineType) o2).getPoints();
		}
		else if (WordType.class.isAssignableFrom(o1.getClass())) {
			sortByXY=true;
			WordType w1 = (WordType) o1;
			WordType w2 = (WordType) o2;
			
			if (w1.getCoords()!=null && w2.getCoords()!=null) {
				coords1 = w1.getCoords().getPoints();
				coords2 = w2.getCoords().getPoints();					
			}
		}
		
//			if (coords1.isEmpty() || coords2.isEmpty()) {
//				throw new Exception("No coordinates in one of the objects - should not happen!");
//			}
		
		// determine orientation of (parent) text regions
		Double orientationInRadiants = null;
		if (o1 instanceof ITrpShapeType && o2 instanceof ITrpShapeType && !(o1 instanceof RegionType) && !(o2 instanceof RegionType)) {
			TrpTextRegionType tr1 = TrpShapeTypeUtils.getTextRegion((ITrpShapeType) o1);
			TrpTextRegionType tr2 = TrpShapeTypeUtils.getTextRegion((ITrpShapeType) o2);
			
			if (tr1!=null && tr2!=null && StringUtils.equals(tr1.getId(), tr2.getId()) && tr1.getOrientation()!=null) {
				orientationInRadiants = Math.toRadians(tr1.getOrientation());
			}
		}
		// --------------------------
		
		if (orientationInRadiants!=null) {
			coords1 = PointStrUtils.rotatePoints(coords1, orientationInRadiants);
			coords2 = PointStrUtils.rotatePoints(coords2, orientationInRadiants);
			logger.trace("orientation set: "+orientationInRadiants+" rotated points: "+coords1+", "+coords2);
		}
		
		Rectangle b1 = PointStrUtils.getBoundingBox(coords1);
		Rectangle b2 = PointStrUtils.getBoundingBox(coords2);
//			Point pt1 = new Point(b1.x, b1.y);
//			Point pt2 = new Point(b2.x, b2.y);	
		
//		performColumnSensitiveComparison = true; // for testing purposes to force column sensitive comparison on all elements
		
		if (!forceCompareByYX && performColumnSensitiveComparison) { // FIXME: transitivity broken 
			return compareByYXIfOverlappingOnXAxis(b1, b2, 0.8d);
			
//			int v1 = (int) (b1.getX()+(b1.getWidth()/2));
//			int v2 = (int) (b2.getX()+(b2.getWidth()/2));
//			int l = 20;
//			
//			if (Math.abs(v1-v2) < l) {
//				return compareByYX(b1.x, b2.x, b1.y, b2.y); 
//			}
//			else {
//				return Integer.compare(v1, v2);
//			}

//			String id1 = o1 instanceof ITrpShapeType ? ((ITrpShapeType)o1).getId() : "NA";
//			String id2 = o2 instanceof ITrpShapeType ? ((ITrpShapeType)o2).getId() : "NA";
//			String idStr = id1+"/"+id2;
//			if ( v1 < b2.x) {
//				logger.debug("h1 "+idStr);
//				return -1;
//			}
//			else if(v1 > (b2.x+b2.getWidth())) {
//				logger.debug("h2 "+idStr);
//				return 1;
//			}
//			else {
//				logger.debug("h3 "+idStr);
//				return compareByYX(b1.x, b2.x, b1.y, b2.y); 
//			}
		}
		else if (sortByXY) {
			return compareByXY(b1.x, b2.x, b1.y, b2.y);
		}
		else {
			return compareByYX(b1.x, b2.x, b1.y, b2.y);
		}
			
	}
	
	/** First compare by y, then x */
	private int compareByYX(int x1, int x2, int y1, int y2) {
//		int yCompare = Integer.compare(y1, y2);
		//System.out.println("yCompare " +yCompare);
//		return (yCompare != 0) ? yCompare : -1;
		
		int yCompare = Integer.compare(y1, y2);
		return (yCompare != 0) ? yCompare : Integer.compare(x1, x2);
	}
	
	private int compareByYXIfOverlappingOnXAxis(Rectangle b1, Rectangle b2, double frac) {
		int o = IntRange.getOverlapLength(b1.x, b1.width, b2.x, b2.width);
		o = Math.max(o, 0);
		int minOverlap = (int) (Math.min(b1.width, b2.width)*frac); // fraction of the width of the smaller rectangle
		minOverlap=20;
		logger.trace("overlap = "+o+", minOverlap = "+minOverlap);
		if (o > minOverlap) {
			logger.trace("yx compare");
			return compareByYX(b1.x, b2.x, b1.y, b2.y);
		}
		else {
			logger.trace("xy compare");
//			return compareByXY(b1.x, b2.x, b1.y, b2.y);
			return Integer.compare(b1.x, b2.x);
		}
	}
		
	private int compareByYOverlap(Rectangle b1, Rectangle b2, double frac) {
		int o = IntRange.getOverlapLength((int) b1.getY(), (int) b1.getHeight(), (int) b2.getY(), (int) b2.getHeight());		
		int yCompare=0;
		if ( o < (b1.getHeight()+b2.getHeight())*frac/2) { // if overlap is less than fraction of average height -> compare by y coordinate
			yCompare = Integer.compare(b1.y, b2.y);
		}
		return yCompare;
	}
	
	/** First compare by x, then y */
	private int compareByXY(int x1, int x2, int y1, int y2) {
		int xCompare = Integer.compare(x1, x2);
		return (xCompare != 0) ? xCompare : Integer.compare(y1, y2);
	}	

}
