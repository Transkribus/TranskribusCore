package eu.transkribus.core.util;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.util.LogUtil.Level;

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
	public final static int DEFAULT_MIN_POINTS = 3;

	private List<Point> pts;
	private int nMin = DEFAULT_MIN_POINTS;

	public RamerDouglasPeuckerFilter(List<Point> pts) {
		Objects.requireNonNull(pts);
		this.pts = pts;
	}
	
	public List<Point> filterByPercentageOfPolygonLength(double perc) {
		return RamerDouglasPeuckerFilter.filterByPercentageOfPolygonLength(perc, pts);
	}

	public List<Point> filter(double eps) {
		return filter(eps, pts, nMin);
	}
	
	public void setNMin(int nMin) {
		this.nMin = nMin;
	}
	
	public static List<Point> filterByPercentageOfPolygonLength(double percentOfLength, List<Point> pts) {
		double eps = (GeomUtils.getPolygonLength(pts) * percentOfLength)/100.0d;
		return filter(eps, pts, DEFAULT_MIN_POINTS);
	}
	
	public static List<Point> filter(double eps, List<Point> pts, int nMin) {
		eps = Math.max(1e-4d, eps);
		logger.debug("filter, n-pts = "+pts.size()+", eps = "+eps+", nMin = "+nMin);
		
		if (pts.size() <= nMin) {
			logger.debug("n-pts <= "+nMin+" -> not simplifiying!");
			return pts;
		}
		
		Stack<Pair<Integer, Integer>> indexStack = new Stack<>();
		indexStack.push(Pair.of(0, pts.size()-1));
		
		List<Point> simpl = new ArrayList<Point>();
		int startIndex=0, endIndex=pts.size()-1;
		
		simpl.add(pts.get(0));
		while (!indexStack.isEmpty()) {
			startIndex = indexStack.peek().getLeft();
			endIndex = indexStack.peek().getRight();
			indexStack.pop();
			
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
			
			if (dmax >= eps) {
				indexStack.push(Pair.of(index, endIndex));
				indexStack.push(Pair.of(startIndex, index));
			} else {
//				simpl.add(pts.get(startIndex)); // do not add this point here -> create duplicates
				simpl.add(pts.get(endIndex));
			}
		}
		
		return simpl;
	}

	/**
	 * @deprecated recursive implementation -> can cause stack overflow
	 */
	public static List<Point> filterRecursive(double eps, List<Point> pts) {
		return filterRecursive(eps, pts, 0, pts.size()-1);
	}
	
	/**
	 * @deprecated recursive implementation -> can cause stack overflow
	 */
	private static List<Point> filterRecursive(double eps, List<Point> pts, int startIndex, int endIndex) {
		logger.debug("filterRecursive, n-pts = "+pts.size());
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
			List<Point> sub1Simpl = filterRecursive(eps, pts, startIndex, index);
			List<Point> sub2Simpl = filterRecursive(eps, pts, index, endIndex);
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
		
		logger.debug("n-pts-filtered = "+simpl.size());
		return simpl;
	}
}
