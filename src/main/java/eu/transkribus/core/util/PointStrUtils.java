package eu.transkribus.core.util;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PointStrUtils {
	
	static Logger logger = LoggerFactory.getLogger(PointStrUtils.class);
	
	public static String affineTransPoints(String ptsStr, double tx, double ty, double sx, double sy, double rot) throws Exception {
		List<Point> pts = parsePoints(ptsStr);
		
		AffineTransform at = new AffineTransform();
		at.scale(sx, sy);
		at.rotate(rot);
		at.translate(tx, ty);

		for (Point p : pts) {
			at.transform(p, p);
		}
		
		return pointsToString(pts);
	}
	
	public static String translatePoints(String ptsStr, int x, int y) throws Exception {
		List<Point> pts = parsePoints(ptsStr);
		
		for (Point p : pts) {
			p.setLocation(p.x+x, p.y+y);
		}
		
		return pointsToString(pts);		
	}
	
	public static String rotatePoints(String ptsStr, double theta) throws Exception {
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
	public static List<Point> parsePoints(String pts) throws IOException {
		logger.trace("parsing points: "+pts);
		List<Point> ptsList = new ArrayList<Point>();
		try {
			for (String pt : pts.trim().split("\\s+")) {
				if (pt.isEmpty())
					continue;
				logger.trace("pt = "+pt);
				String [] tmp = pt.split(",");
				int x = Integer.valueOf(tmp[0].trim());
				int y = Integer.valueOf(tmp[1].trim());
				ptsList.add(new Point(x, y));
			}
		}
		catch (Exception e) {
			throw new IOException("Could not fully parse points: '"+pts+"', message: "+e.getMessage(), e);
//			logger.warn("Could not fully parse points: '"+pts+"'", e);
		}
		
		return ptsList;
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
	
	public static void main(String[] args) {
		try {
			List<Point> pts = parsePoints("");
			System.out.println("nr of pts = "+pts.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
