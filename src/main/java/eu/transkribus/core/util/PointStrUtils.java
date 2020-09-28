package eu.transkribus.core.util;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import math.geom2d.Point2D;
import math.geom2d.polygon.Polygon2D;
import math.geom2d.polygon.Polygons2D;
import math.geom2d.polygon.SimplePolygon2D;

public class PointStrUtils {
	
	public static class PointParseException extends RuntimeException {
		private static final long serialVersionUID = 6286123241743808364L;

		public PointParseException() {
			super();
		}

		public PointParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public PointParseException(String message, Throwable cause) {
			super(message, cause);
		}

		public PointParseException(String message) {
			super(message);
		}

		public PointParseException(Throwable cause) {
			super(cause);
		}
	}
	
	static Logger logger = LoggerFactory.getLogger(PointStrUtils.class);
	
	public static String affineTransPoints(String ptsStr, double tx, double ty, double sx, double sy, double rot) throws PointParseException {
		AffineTransform at = new AffineTransform();
		at.scale(sx, sy);
		at.rotate(rot);
		at.translate(tx, ty);

		return affineTransPoints(ptsStr, at);
	}
	
	public static String affineTransPoints(String ptsStr, AffineTransform at) throws PointParseException {
		List<Point> pts = parsePoints(ptsStr);

		for (Point p : pts) {
			at.transform(p, p);
		}
		
		return pointsToString(pts);
	}
	
	public static String translatePoints(String ptsStr, int x, int y) throws PointParseException {
		List<Point> pts = parsePoints(ptsStr);
		
		for (Point p : pts) {
			p.setLocation(p.x+x, p.y+y);
		}
		
		return pointsToString(pts);		
	}
	
	public static String rotatePoints(String ptsStr, double theta) throws PointParseException {
		List<Point> pts = parsePoints(ptsStr);
		
		AffineTransform rotT = AffineTransform.getRotateInstance(theta);
		for (Point p : pts) {
			rotT.transform(p, p);
		}
		
		return pointsToString(pts);		
	}

	public static java.awt.Polygon primaToAwtPolygon(org.primaresearch.maths.geometry.Polygon primaPoly) {
		java.awt.Polygon awtPoly = new java.awt.Polygon();
		
		for (int i=0; i<primaPoly.getSize(); ++i) {
			org.primaresearch.maths.geometry.Point pt = primaPoly.getPoint(i);
			awtPoly.addPoint(pt.x, pt.y);
		}
		return awtPoly;
	}
	
	public static Rectangle getBoundingBox(String pts) {		
		int ulx=Integer.MAX_VALUE, uly=Integer.MAX_VALUE, lrx=0, lry=0;
		
		logger.trace("parsing bounding box: "+pts);
		for (String pt : pts.trim().split("\\s+")) {
			try {		
				if (pt.isEmpty())
					continue;
				
				logger.trace("pt = "+pt);
				String [] tmp = pt.split(",");
				int x = Integer.valueOf(tmp[0].trim());
				int y = Integer.valueOf(tmp[1].trim());
				
				if (x < ulx)
					ulx = x;
				if (y < uly)
					uly = y;
				
				if (x > lrx)
					lrx = x;
				if (y > lry)
					lry = y;
			}
			catch (Exception e) {
				logger.warn("Could not parse point: '"+pt+"' ptsStr = "+pts, e);
			}
		}

		if (ulx == Integer.MAX_VALUE)
			ulx = 0;
		if (uly == Integer.MAX_VALUE)
			uly = 0;
		
		int w = lrx - ulx;
		if (w < 0)
			w = 0;
		int h = lry - uly;
		if (h < 0)
			h = 0;
		
		return new Rectangle(ulx, uly, w, h);
	}
	
	/**
	 * Parse points from String and do not throw an exception if some point could not be parsed
	 */
	public static List<Point> parsePoints2(String pts) {		
		logger.trace("parsing points2: "+pts);
		List<Point> ptsList = new ArrayList<Point>();
		for (String pt : pts.trim().split("\\s+")) {
			try {			
				if (pt.isEmpty())
					continue;
				logger.trace("pt = "+pt);
				String [] tmp = pt.split(",");
				int x = Integer.valueOf(tmp[0].trim());
				int y = Integer.valueOf(tmp[1].trim());
				ptsList.add(new Point(x, y));
			}
			catch (Exception e) {
				logger.warn("Could not parse point: '"+pt+"' ptsStr = "+pts, e);
			}
		}

		
		return ptsList;
	}
	
	/** Parse points from String in format "x1,y1 x2,y2 ..." */
	public static List<Point> parsePoints(String pts) throws PointParseException  {
		logger.trace("parsing points: "+pts);
		List<Point> ptsList = new ArrayList<Point>();
		try {
			for (String pt : pts.trim().split("\\s+")) {
				if (pt != null && pt.isEmpty())
					continue;
				logger.trace("pt = "+pt);
				String [] tmp = pt.split(",");
				if (tmp.length>1){
					// check if not float coordinates from TWI 
					BigDecimal xCoord = new BigDecimal(tmp[0]).setScale(0,BigDecimal.ROUND_HALF_UP);
					BigDecimal yCoord = new BigDecimal(tmp[1]).setScale(0,BigDecimal.ROUND_HALF_UP);
					int x = xCoord.intValue();
					int y = yCoord.intValue();
					ptsList.add(new Point(x, y));
				}
			}
		}
		catch (Exception e) {
			throw new PointParseException("Could not fully parse points: '"+pts+"', message: "+e.getMessage(), e);
//			logger.warn("Could not fully parse points: '"+pts+"'", e);
		}
		
		return ptsList;
	}
	
	/** Parse points from String in format "x1,y1 x2,y2 ..." */
	public static List<Point> parsePoints3(String pts) throws PointParseException  {
		return (List<Point>) buildPointContainer(pts, Collectors.toList(), Point::new, false);
	}
	
	public static List<Point2D> buildPoints2DList(String pointsStr) {
		return (List<Point2D>)PointStrUtils.buildPointContainer(
				pointsStr, Collectors.toList(), Point2D::new, false);
	}
	
	/**
	 * parse the points string and construct objects using constr BiFunction
	 * 
	 * @param pointsStr PAGE XML style points string
	 * @param collector incorporates objects into a structure
	 * @param constr constructur for the objects to be collected
	 * @return
	 */
	public static <T, A, R> A buildPointContainer(String pointsStr, Collector<T, A, R> collector, BiFunction<Integer, Integer, T> constr, boolean omitPointsOnParseException) {
		Supplier<A> sup = collector.supplier();
		A container = sup.get();
		//pointsStr MIGHT contain leading or trailing whitespace from some tool..
		pointsStr = pointsStr.trim();
		if(pointsStr == null || pointsStr.isEmpty()){
    		return container;
    	}
		
		try{
			final String[] coordsArr = pointsStr.split(" ");
			for (int i = 0; i < coordsArr.length; i++) {
				final String[] xy = coordsArr[i].split(",");
				//handle floats gracefully (they should not occur though)
				final Integer x = new BigDecimal(xy[0])
						.setScale(0, BigDecimal.ROUND_HALF_UP)
						.intValue();
				final Integer y = new BigDecimal(xy[1])
						.setScale(0, BigDecimal.ROUND_HALF_UP)
						.intValue();
				collector.accumulator().accept(container, constr.apply(x, y));
			}
		} catch(NumberFormatException e){
			logger.error("Bad coords String: " + pointsStr, e);
			if(!omitPointsOnParseException) {
				throw e;
			}
		}
		return container;
	}
	
	public static Polygon buildPolygon(String pointsStr) {
		List<Point> coords = (List<Point>)PointStrUtils.buildPointContainer(
				pointsStr, Collectors.toList(), Point::new, false);
		int n = coords.size();
		int[] xValues = new int[n];
		int[] yValues = new int[n];
		for(int i = 0; i < n; i++) {
			final Point p = coords.get(i);
			xValues[i] = p.x;
			yValues[i] = p.y;
		}
		return new Polygon(xValues, yValues, n);
	}
	
	public static String pointsToString(List<Point> pts) {
		String ptsStr="";
		for (Point pt : pts) {
			ptsStr += pt.x+","+pt.y+" ";
		}
		return ptsStr.trim();
	}
	
	public static String pointsToString(java.awt.Rectangle rect) {
		String ptsStr = rect.x+","+rect.y+" "+
						(rect.x+rect.width)+","+rect.y+" "+
						(rect.x+rect.width)+","+(rect.y+rect.height)+" "+
						rect.x+","+(rect.y+rect.height);
		return ptsStr.trim();
	}
	
	public static String cornerPtsToString(int[] corners) {
		return corners[0]+" "+corners[1]+" "+corners[2]+" "+corners[3];
	}
	
	public static int[] parseCornerPts(String str) {
		String[] splits = str.split(" ");
		if (splits==null || splits.length!=4)
			throw new RuntimeException("invalid corner point string: "+str);
		
		int[] corners = new int[4];
		
		for (int i=0; i<4; ++i) {
			try {
				corners[i] = Integer.parseInt(splits[i]);
			} catch (Exception e) {
				throw new RuntimeException("invalid corner point string: "+str, e);	
			}
		}
		
		return corners;
	}
	
	public static Pair<Integer, Integer> getXBounds(String baseline) {
		Polygon baselinePoly = PointStrUtils.buildPolygon(baseline);
		int baselineMinX = Integer.MAX_VALUE;
		int baselineMaxX = Integer.MIN_VALUE;
		for(int i = 0; i < baselinePoly.xpoints.length; i++) {
			int x = baselinePoly.xpoints[i];
			if(x < baselineMinX) {
				baselineMinX = x;
			}
			if(x > baselineMaxX) {
				baselineMaxX = x;
			}
		}
		return Pair.of(baselineMinX, baselineMaxX);
	}
	
	public static double getArea(String coords) {
		SimplePolygon2D p = new SimplePolygon2D(PointStrUtils.buildPoints2DList(coords));
		return Math.abs(p.area());
	}
	
	public static String getBoundsPointStr(String pointsStr) {
		return PointStrUtils.pointsToString(PointStrUtils.buildPolygon(pointsStr).getBounds());
	}
	
	public static double getPolygonIntersectionArea(String coords1, String coords2) {
		List<Point2D> p1 = PointStrUtils.buildPoints2DList(coords1);
		List<Point2D> p2 = PointStrUtils.buildPoints2DList(coords2);
		
		Polygon2D i = Polygons2D.intersection(SimplePolygon2D.create(p1), SimplePolygon2D.create(p2));
		return i==null ? 0 : i.area();
	}
	
	public static void main(String[] args) {
		try {
			List<Point> pts = parsePoints("");
			System.out.println("nr of pts = "+pts.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String test = "1,2 3,4 5,6";
		for(Point p : parsePoints3(test)) {
			System.out.println(p);
		}
		
	}


}
