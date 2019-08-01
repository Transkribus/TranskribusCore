package eu.transkribus.core.util;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Polygon simplification using Ramer-Douglas-Peucker algorithm with specified
 * tolerance
 * 
 * @see <a
 *      href="http://en.wikipedia.org/wiki/Ramer-Douglas-Peucker_algorithm">Ramer-Douglas-Peucker
 *      algorithm</a>
 */
public class RamerDouglasPeuckerFilter {
	private static Logger logger = LoggerFactory.getLogger(RamerDouglasPeuckerFilter.class);
	
	public final static int DEFAULT_PERC_OF_POLYGON_LENGTH = 1;

	List<Point> pts;

	public RamerDouglasPeuckerFilter(List<Point> pts) {
		Objects.requireNonNull(pts);
		this.pts = pts;
	}
	
	public List<Point> filterByPercentageOfPolygonLength(double perc) {
		return RamerDouglasPeuckerFilter.filterByPercentageOfPolygonLength(perc, pts);
	}

	public List<Point> filter(double eps) {
		return filter(eps, pts);
	}
	
	public static List<Point> filterByPercentageOfPolygonLength(double percentOfLength, List<Point> pts) {
		double eps = (GeomUtils.getPolygonLength(pts) * percentOfLength)/100.0d;
		return filter(eps, pts);
	}

	public static List<Point> filter(double eps, List<Point> pts) {
		return filter(eps, pts, 0, pts.size()-1);
	}

	public static List<Point> filter(double eps, List<Point> pts, int startIndex, int endIndex) {
		double dmax = 0;
		int index = 0;
		Line2D.Double line = new Line2D.Double(pts.get(startIndex), pts.get(endIndex));

		for (int i = startIndex + 1; i < endIndex; ++i) {
			double dist = line.ptSegDist(pts.get(i));
			logger.trace("dist = " + dist);
			if (dist > dmax) {
				index = i;
				dmax = dist;
			}
		}

		List<Point> simpl = new ArrayList<Point>();

		if (dmax >= eps) {
			List<Point> sub1Simpl = filter(eps, pts, startIndex, index);
			List<Point> sub2Simpl = filter(eps, pts, index, endIndex);
			simpl.addAll(sub1Simpl);
			simpl.addAll(sub2Simpl.subList(1, sub2Simpl.size())); // do not add
																	// first
																	// point
																	// since it
																	// was added
																	// by
																	// sub1Simpl
																	// already!
		} else {
			simpl.add(pts.get(startIndex));
			simpl.add(pts.get(endIndex));
		}

		return simpl;
	}
}
