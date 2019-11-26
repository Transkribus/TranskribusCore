package eu.transkribus.core.model.beans.pagecontent_trp;

import java.awt.Rectangle;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.util.PointStrUtils;

public class TrpShapeTypeXYComparator implements Comparator<ITrpShapeType> {
	private static final Logger logger = LoggerFactory.getLogger(TrpShapeTypeXYComparator.class);

	@Override
	public int compare(ITrpShapeType o1, ITrpShapeType o2) {
		if (o1 == null && o2  == null) {
			return 0;
		}
		else if (o1 == null && o2 != null) {
			return -1;
		}
		else if (o1 != null && o2 == null) {
			return 1;
		}
		else {
			String coords1=o1.getCoordinates();
			String coords2=o2.getCoordinates();
			
			Pair<String, String> fixedCoords = TrpShapeTypeUtils.invertCoordsCommonRegionOrientation(coords1, coords2, o1, o2);
			coords1 = fixedCoords.getLeft();
			coords2 = fixedCoords.getRight();

			Rectangle b1 = PointStrUtils.getBoundingBox(coords1);
			Rectangle b2 = PointStrUtils.getBoundingBox(coords2);
			
			return compareByXY(b1.x, b2.x, b1.y, b2.y);
		}
	}
	
	private int compareByXY(int x1, int x2, int y1, int y2) {
		int xCompare = Integer.compare(x1, x2);
		return (xCompare != 0) ? xCompare : Integer.compare(y1, y2);
	}

}
