package eu.transkribus.core.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import math.geom2d.AffineTransform2D;
import math.geom2d.Point2D;
import math.geom2d.line.Line2D;

public class GeomUtils {
	private static final Logger logger = LoggerFactory.getLogger(GeomUtils.class);
	
	public static List<Point> getRectPoints(java.awt.Rectangle rectangle, boolean clockwise) {
		List<Point> pts = new ArrayList<>();
		if (clockwise) {
			pts.add(new Point(rectangle.x, rectangle.y));
			pts.add(new Point(rectangle.x+rectangle.width, rectangle.y));
			pts.add(new Point(rectangle.x+rectangle.width, rectangle.y+rectangle.height));
			pts.add(new Point(rectangle.x, rectangle.y+rectangle.height));
		} else {
			pts.add(new Point(rectangle.x, rectangle.y));
			pts.add(new Point(rectangle.x, rectangle.y+rectangle.height));
			pts.add(new Point(rectangle.x+rectangle.width, rectangle.y+rectangle.width));
			pts.add(new Point(rectangle.x+rectangle.width, rectangle.y));
		}
		
		return pts;
	}
	
	public static List<Point> getPoints(java.awt.Polygon polygon, boolean removeSucceedingEqualPts) {
		List<java.awt.Point> pts = new ArrayList<java.awt.Point>();
		
		Integer xOld = null;
		Integer yOld = null;
		
		for (int i=0; i<polygon.npoints; ++i) {
			if (removeSucceedingEqualPts && xOld!=null && yOld!=null && xOld==polygon.xpoints[i] && yOld==polygon.ypoints[i]) {
				logger.trace("removing equal succeeding point, i="+i+", x="+xOld+", y="+yOld);
				continue;
			}
			
			pts.add(new java.awt.Point(polygon.xpoints[i], polygon.ypoints[i]));
			xOld = polygon.xpoints[i];
			yOld = polygon.ypoints[i];			
		}
		
		return pts;
	}
	
	public static java.awt.Polygon createPolygon(List<Point> pts, boolean removeSucceedingEqualsPts) {
//		removeSucceedingEqualsPts = false; // TEST
		
		java.awt.Polygon poly = new java.awt.Polygon();
		Point lastPt=null;
		for (Point p : pts) {
			logger.trace("p = "+p+" lastPt = "+lastPt+" remove = "+removeSucceedingEqualsPts);
			if (removeSucceedingEqualsPts && lastPt != null && p.equals(lastPt)) {
				logger.trace("removing pt: "+p);
				continue;
			}
			
			poly.addPoint(p.x, p.y);
			lastPt = p;
		}
		
		return poly;
	}
	
	public static Rectangle extend(Rectangle r, int ext) {
		return new Rectangle(r.x-ext, r.y-ext, r.width+ext, r.height+ext);
	}
	
	public static boolean isInside(int x, int y, Rectangle b, int thresh) {
		return extend(b, thresh).contains(x, y);
	}
	
	public static double angleWithHorizontalLine(Point p1, Point p2) {
		return (float) Math.atan2(-p1.y+p2.y, -p1.x+p2.x);
//		return (float) Math.atan(-p1.y+p2.y, -p1.x+p2.x);
	}
	
	public static int bound(int v, int min, int max) {
		if (v < min)
			return min;
		else if (v > max)
			return max;
		else
			return v;	
	}
		
	/**
	   * Returns closest point on segment to point
	   * 
	   * @param ss
	   *            segment start point
	   * @param se
	   *            segment end point
	   * @param p
	   *            point to found closest point on segment
	   * @return closest point on segment to p
	   */
	  public static Point getClosestPointOnSegment(Point ss, Point se, Point p)
	  {
	    return getClosestPointOnSegment(ss.x, ss.y, se.x, se.y, p.x, p.y);
	  }

	  /**
	   * Returns closest point on segment to point
	   * 
	   * @param sx1
	   *            segment x coord 1
	   * @param sy1
	   *            segment y coord 1
	   * @param sx2
	   *            segment x coord 2
	   * @param sy2
	   *            segment y coord 2
	   * @param px
	   *            point x coord
	   * @param py
	   *            point y coord
	   * @return closets point on segment to point
	   */
	  public static Point getClosestPointOnSegment(int sx1, int sy1, int sx2, int sy2, int px, int py)
	  {
	    double xDelta = sx2 - sx1;
	    double yDelta = sy2 - sy1;

	    if ((xDelta == 0) && (yDelta == 0)) { // segment is a point -> return this point as it must be the closest one!
	    	return new Point(sx1, sy1);
//	      throw new IllegalArgumentException("Segment start equals segment end");
	    }

	    double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

	    final Point closestPoint;
	    if (u < 0)
	    {
	      closestPoint = new Point(sx1, sy1);
	    }
	    else if (u > 1)
	    {
	      closestPoint = new Point(sx2, sy2);
	    }
	    else
	    {
	      closestPoint = new Point((int) Math.round(sx1 + u * xDelta), (int) Math.round(sy1 + u * yDelta));
	    }

	    return closestPoint;
	  }
	
	/**
	 * Returns the distance and the closest segment of a point (x,y) to a polygon given as a series of points
	 * @param isClosedShape True if this is a closes polygon or false if its a polyline
	 */
	public static Pair<Double, java.awt.geom.Line2D.Double> 
		getDistToPolygonAndClosestSegment(List<Point> pts, double x, double y, boolean isClosedShape) {
		double minDist = Integer.MAX_VALUE;
		java.awt.geom.Line2D.Double minLine = new java.awt.geom.Line2D.Double(0, 0, 0, 0);
		
		int N = isClosedShape ? pts.size() : pts.size()-1;
		
		for (int i=0; i<N; ++i) {
			java.awt.geom.Line2D.Double line = new java.awt.geom.Line2D.Double(pts.get(i), pts.get( (i+1) % pts.size() ));
			double d = line.ptSegDistSq(x, y);
//			logger.debug("d = "+d);
			if (d < minDist) {
				minDist = d;
				minLine = line;
			}
		}

		return Pair.of(minDist, minLine);
	}
	
	public static Point intersection(Line2D l1, Line2D l2) {
		return GeomUtils.intersection((int)l1.getX1(), (int)l1.getY1(), (int)l1.getX2(), (int)l1.getY2(),
				(int)l2.getX1(), (int)l2.getY1(), (int)l2.getX2(), (int)l2.getY2());
	}

	/**
	 * Computes the intersection between two lines. The calculated point is
	 * approximate, since integers are used. If you need a more precise result,
	 * use doubles everywhere. (c) 2007 Alexander Hristov. Use Freely (LGPL
	 * license). http://www.ahristov.com
	 * 
	 * @param x1
	 *            Point 1 of Line 1
	 * @param y1
	 *            Point 1 of Line 1
	 * @param x2
	 *            Point 2 of Line 1
	 * @param y2
	 *            Point 2 of Line 1
	 * @param x3
	 *            Point 1 of Line 2
	 * @param y3
	 *            Point 1 of Line 2
	 * @param x4
	 *            Point 2 of Line 2
	 * @param y4
	 *            Point 2 of Line 2
	 * @return Point where the segments intersect, or null if they don't
	 */
	public static Point intersection(int x1, int y1, int x2, int y2, int x3,
			int y3, int x4, int y4) {
		int d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
		if (d == 0)
			return null;

		int xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2)
				* (x3 * y4 - y3 * x4))
				/ d;
		int yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2)
				* (x3 * y4 - y3 * x4))
				/ d;

		return new Point(xi, yi);
	}
	
	public static double vecAngle(double x1, double y1, double x2, double y2) {
		return Math.acos( (x1*x2+y1*y2) / (Math.sqrt(x1*x1+y1*y1)+Math.sqrt(x2*x2+y2*y2)) );
	}
	
	public static Point2D rotate(double x, double y, double angle) {
		return AffineTransform2D.createRotation(angle).transform(new Point2D(x, y));
	}
	
	public static java.awt.Point rotate(java.awt.Point pt, double angle) {
		Point2D pt2d = rotate(pt.x, pt.y, angle);
		return new java.awt.Point((int) pt2d.x(), (int) pt2d.y());
	}
	
//	public static java.awt.Rectangle rotate(java.awt.Rectangle r, double angleRad) {
//		AffineTransform2D rotation = AffineTransform2D.createRotation(angleRad);
//		Point2D pt = rotation.transform(new Point2D(r.getX(), r.getY()));
//		
//		return new java.awt.Rectangle((int) pt.x(), (int) pt.y(), r.width, r.height);
//	}
	
	public static double angleWithHorizontalLineRotated(Point p1, Point p2, double angle) {		
		Point2D v1 = new Point2D(p2.x-p1.x, p2.y-p1.y);
		Point2D v2 = GeomUtils.rotate(1, 0, angle);
		
		System.out.println(v1+" - "+v2);
		
		return GeomUtils.vecAngle(v1.x(), v1.y(), v2.x(), v2.y());
	}
	
	public static Point invertRotation(double x, double y, double angleRad) {
		return AffineTransform2D.createRotation(angleRad).invert().transform(new Point2D(x, y)).getAsInt();
	}

}
