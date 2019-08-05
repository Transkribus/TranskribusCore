package eu.transkribus.core.util;

import java.awt.Point;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.transkribus.core.io.LocalDocConst;
import eu.transkribus.core.io.LocalDocReader;

public class PolygonSimplificationUtil {
	private static final Logger logger = LoggerFactory.getLogger(PolygonSimplificationUtil.class);
	
	public static String simplifyPointsStr(String pointsStr, double perc) {
		if (!StringUtils.isEmpty(pointsStr)) {
			List<Point> pts = PointStrUtils.parsePoints2(pointsStr);
			if (pts.size() <= 2) { // either points could not be parsed or less than 3 points --> return original string!
				return pointsStr;
			}
			
			return PointStrUtils.pointsToString(RamerDouglasPeuckerFilter.filterByPercentageOfPolygonLength(perc, pts));
		}
		else {
			return pointsStr;
		}
	}
	
	public static void simplifyPointsOfTextLines(String pageXmlIn, String pageXmlOut, boolean doIndent) throws XPathFactoryConfigurationException, ParserConfigurationException, MalformedURLException, IllegalArgumentException, SAXException, IOException, XPathExpressionException, TransformerFactoryConfigurationError, TransformerException {
		PageXmlFileProcessor fp = new PageXmlFileProcessor();
		Document doc = fp.loadDocument(pageXmlIn);
		NodeList nl = fp.getTextLineCoordsPoints(doc);
		
		logger.debug("got "+nl.getLength()+" lines");
		for (int i=0; i<nl.getLength(); ++i) {
			Node n = nl.item(i);
//			Node pts = n.getAttributes().getNamedItem("points");
//			String oldPoints = pts.getNodeValue();
			String oldPoints = n.getNodeValue();
			logger.trace("old points = "+oldPoints);
			String simplified = simplifyPointsStr(oldPoints, RamerDouglasPeuckerFilter.DEFAULT_PERC_OF_POLYGON_LENGTH);
			logger.trace("simplified = "+simplified);
			n.setNodeValue(simplified);
		}
		
		if (!StringUtils.isEmpty(pageXmlOut)) {
			logger.debug("writing PAGE-XML to: "+pageXmlOut+", doIndent: "+doIndent);
			fp.writeToFile(doc, new File(pageXmlOut), doIndent);	
		}
	}
	
	public static List<Pair<File, File>> simplifyPointsForAllPageXmls(String documentPath) throws IOException {
		List<Pair<File,File>> imgPageXmlPairs = LocalDocReader.findImgAndPAGEXMLFiles(new File(documentPath));
		logger.info("N = "+imgPageXmlPairs.size());
		
		long oldLength = imgPageXmlPairs.stream().map(p -> p.getRight()).collect(Collectors.summingLong(File::length));
		List<Pair<File,File>> errors = new ArrayList<>();
		
		int i=1;
		for (Pair<File,File> p : imgPageXmlPairs) {
			logger.info("processing page "+(i++)+"/"+imgPageXmlPairs.size());
			String pXmlPath = p.getRight().getAbsolutePath();
			
			try {
				simplifyPointsOfTextLines(pXmlPath, pXmlPath, true);
			} catch (XPathFactoryConfigurationException | IllegalArgumentException | XPathExpressionException
					| ParserConfigurationException | SAXException | TransformerFactoryConfigurationError
					| TransformerException e) {
				errors.add(p);
				logger.error("Could not simplify points of text lines: "+e.getMessage(), e);
			}
		}
		if (errors.size()>0) {
			errors.stream().forEach(p -> logger.info("Error in: "+p.getLeft().getAbsolutePath()));
		}
		
		long totalLength2 = imgPageXmlPairs.stream().map(p -> p.getRight()).collect(Collectors.summingLong(File::length));
		
		logger.info("old-length = "+oldLength+", new-length = "+totalLength2);
		
		return errors;
	}
	
	public static void simplifyPointsForAllDocumentsInFolder(String path) throws IOException {
		File folder = new File(path);
		
		File[] subfolders = folder.listFiles(f -> f.isDirectory());
		
		int i=0;
		for (File dir : subfolders) {
			++i;
			logger.info("Simplifying points for folder "+i+"/"+subfolders.length+": "+dir.getAbsolutePath());
			simplifyPointsForAllPageXmls(dir.getAbsolutePath());
		}
	}

}
