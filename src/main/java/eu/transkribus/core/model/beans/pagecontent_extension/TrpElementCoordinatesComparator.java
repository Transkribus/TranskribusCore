package eu.transkribus.core.model.beans.pagecontent_extension;

import java.awt.Rectangle;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.util.PrimaUtils;

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
				coords1 = ((RegionType) o1).getCoords().getPoints();
				coords2 = ((RegionType) o2).getCoords().getPoints();
			}
			else if (TextLineType.class.isAssignableFrom(o1.getClass())) {
				coords1 = ((TextLineType) o1).getCoords().getPoints();
				coords2 = ((TextLineType) o2).getCoords().getPoints();
			}
//			if (o1 instanceof BaselineType) {
//				coords1 = ((TrpBaselineType) o1).getPoints();
//				coords2 = ((TrpBaselineType) o2).getPoints();
//			}
			else if (WordType.class.isAssignableFrom(o1.getClass())) {
				coords1 = ((WordType) o1).getCoords().getPoints();
				coords2 = ((WordType) o2).getCoords().getPoints();
			}
			
//			if (coords1.isEmpty() || coords2.isEmpty()) {
//				throw new Exception("No coordinates in one of the objects - should not happen!");
//			}
			
			java.awt.Polygon p1 = new java.awt.Polygon();
			try {
				for (java.awt.Point p : PrimaUtils.parsePoints(coords1))
					p1.addPoint(p.x, p.y);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			
			java.awt.Polygon p2 = new java.awt.Polygon();
			try {
				for (java.awt.Point p : PrimaUtils.parsePoints(coords2))
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
	
	/** First compare by x, then y */
	private int compareByXY(int x1, int x2, int y1, int y2) {
		int xCompare = Integer.compare(x1, x2);
		return (xCompare != 0) ? xCompare : Integer.compare(y1, y2);
	}	

}
