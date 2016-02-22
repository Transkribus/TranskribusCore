package eu.transkribus.core.util;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PrimaUtils {
	
	static Logger logger = LoggerFactory.getLogger(PrimaUtils.class);
	
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
	
	/** Parse points from String in format "x1,y1 x2,y2 ..." */
	public static List<Point> parsePoints(String pts) throws Exception {
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
			throw new Exception("Could not fully parse points: '"+pts+"', message: "+e.getMessage(), e);
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
	
	public static void main(String[] args) {
		try {
			List<Point> pts = parsePoints("");
			System.out.println("nr of pts = "+pts.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
