package eu.transkribus.core.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RamerDouglasPeuckerFilterTest {
	private static final Logger logger = LoggerFactory.getLogger(RamerDouglasPeuckerFilterTest.class);
	
	@Test
	public void testSimplificationOnDegenerateCases() {
		List<Point> pts1 = new ArrayList<>();
		List<Point> simpl;

		simpl = RamerDouglasPeuckerFilter.filterByPercentageOfPolygonLength(RamerDouglasPeuckerFilter.DEFAULT_PERC_OF_POLYGON_LENGTH, pts1);
		logger.info("pts = "+pts1);
		logger.info("simpl = "+simpl);
		
		pts1.add(new Point(0,0));
		
		simpl = RamerDouglasPeuckerFilter.filterByPercentageOfPolygonLength(RamerDouglasPeuckerFilter.DEFAULT_PERC_OF_POLYGON_LENGTH, pts1);
		logger.info("pts = "+pts1);
		logger.info("simpl = "+simpl);		
		
		pts1.add(new Point(0,0));
		
		simpl = RamerDouglasPeuckerFilter.filterByPercentageOfPolygonLength(RamerDouglasPeuckerFilter.DEFAULT_PERC_OF_POLYGON_LENGTH, pts1);
		logger.info("pts = "+pts1);
		logger.info("simpl = "+simpl);		
		
		pts1.add(new Point(0,0));
		pts1.add(new Point(0,0));
		
		simpl = RamerDouglasPeuckerFilter.filterByPercentageOfPolygonLength(RamerDouglasPeuckerFilter.DEFAULT_PERC_OF_POLYGON_LENGTH, pts1);
		logger.info("pts = "+pts1);
		logger.info("simpl = "+simpl);
	}

}
