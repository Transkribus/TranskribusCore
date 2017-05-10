package eu.transkribus.core.model.beans.pagecontent_trp;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.util.IntRange;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.PointStrUtils;

public class TrpElementCoordinatesComparator<T> implements Comparator<T> {
	private final static Logger logger = LoggerFactory.getLogger(TrpElementCoordinatesComparator.class);
	
	private boolean isRegionLineOrWord(T o) {
		return (o instanceof RegionType || TextLineType.class.isAssignableFrom(o.getClass()) || WordType.class.isAssignableFrom(o.getClass()));		
	}

	@Override
	public int compare(T o1, T o2) {
//		if (!isRegionLineOrWord(o1) || !isRegionLineOrWord(o2))
//			return 0;
		
//		try {
			String coords1="", coords2="";
						
//			if (o1 instanceof PrintSpaceType) {
//				coords1 = ((TrpPrintSpaceType) o1).getCoords().getPoints();
//				coords2 = ((TrpPrintSpaceType) o2).getCoords().getPoints();
//			}		
			if (o1 instanceof RegionType) {
				RegionType r1 = (RegionType) o1;
				RegionType r2 = (RegionType) o2;
				if (r1.getCoords() != null && r2.getCoords() != null) {
					coords1 = r1.getCoords().getPoints();
					coords2 = r2.getCoords().getPoints();					
				}
			}
			else if (TextLineType.class.isAssignableFrom(o1.getClass())) {
				/*
				 * take baseline to compare and set the ro in this case
				 */
				if (((TextLineType) o1).getBaseline() != null && ((TextLineType) o2).getBaseline() != null){
					coords1 = ((TextLineType) o1).getBaseline().getPoints();
					coords2 = ((TextLineType) o2).getBaseline().getPoints();
					
					double y1, x1;
					double y2, x2;
					
					List<Point> pts1 = PointStrUtils.parsePoints(coords1);
					List<Point> pts2 = PointStrUtils.parsePoints(coords2);
					
					if (pts1.size() > 0 && pts2.size() > 0) {
						
						x1 = pts1.get(0).getX();
						y1 = pts1.get(0).getY();
						
						x2 = pts2.get(0).getX();
						y2 = pts2.get(0).getY();
						
						return compareByYX((int)x1, (int)x2, (int)y1, (int)y2);
					}
				}

				//fall back if there are no baselines
				coords1 = ((TextLineType) o1).getCoords().getPoints();
				coords2 = ((TextLineType) o2).getCoords().getPoints();
				
			}
//			if (o1 instanceof BaselineType) {
//				coords1 = ((TrpBaselineType) o1).getPoints();
//				coords2 = ((TrpBaselineType) o2).getPoints();
//			}
			else if (WordType.class.isAssignableFrom(o1.getClass())) {
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
			
			java.awt.Polygon p1 = new java.awt.Polygon();
			try {
				for (java.awt.Point p : PointStrUtils.parsePoints(coords1))
					p1.addPoint(p.x, p.y);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			
			java.awt.Polygon p2 = new java.awt.Polygon();
			try {
				for (java.awt.Point p : PointStrUtils.parsePoints(coords2))
					p2.addPoint(p.x, p.y);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			
			Rectangle b1 = p1.getBounds();
			Rectangle b2 = p2.getBounds();
			
			if (WordType.class.isAssignableFrom(o1.getClass())) {
				return compareByXY(b1.x, b2.x, b1.y, b2.y);
			}
			else {
				return compareByYX(b1.x, b2.x, b1.y, b2.y);
				//return compareBy_YOverlap_X(b1, b2);
			}
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//			return 0;
//		}
	}
	
	/** First compare by y, then x */
	private int compareByYX(int x1, int x2, int y1, int y2) {
		int yCompare = Integer.compare(y1, y2);
		return (yCompare != 0) ? yCompare : Integer.compare(x1, x2);
	}
	
	/** First compare by y, then x 
	 * @deprecated not transitive and throws exception when reading the pageXML
	 * */
	private int compareBy_YOverlap_X(Rectangle b1, Rectangle b2) {		
		int yCompare=compareByYOverlap(b1, b2, 0.4); // if overlap is more than 40% -> yCompare=0

		return (yCompare != 0) ? yCompare : Integer.compare(b1.x, b2.x);
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
