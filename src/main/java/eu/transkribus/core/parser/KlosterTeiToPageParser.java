package eu.transkribus.core.parser;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.transkribus.core.model.beans.pagecontent.CoordsType;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.TextEquivType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpBaselineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.util.PageXmlUtils;

public class KlosterTeiToPageParser {
	private final static Logger logger = LoggerFactory.getLogger(KlosterTeiToPageParser.class);
	
	
	// http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
	
	public static String DIR = "/media/dea_scratch/TRP/kloster/";
//	public static String DIR = "/home/sebastian/tmp/kloster_test/src/";
	public static String FN = "KAE_A_HB_06.xml";
	public static String XML_PATH = DIR + FN;
	
//	public static String DST_DIR = "/home/sebastian/tmp/kloster_test/dst/";
	public static String DST_DIR = "/media/dea_scratch/TRP/kloster/dst/";
	public static String PAGE_DIR = DST_DIR+"page/";
	
	static void parseTeiHeader(Document doc) {
		
		
	}
	
	static CoordsType bbToCoords(int x, int y, int w, int h) {
		CoordsType ct = new CoordsType();
		ct.setPoints(bbToCoordsStr(x, y, w, h));
		return ct;
	}
	
	static String bbToCoordsStr(int x, int y, int w, int h) {
		return x+","+y+" "+x+","+(y+h)+" "+(x+w)+","+(y+h)+" "+(x+w)+","+y;
	}
	
	static void parsePage(Node pbNode, boolean save) throws IOException, JAXBException {
		Element pb = (Element) pbNode;
		String imgFn = pb.getAttribute("facs");
		int pageN = Integer.parseInt(pb.getAttribute("n"));
		int pageHeight = Integer.parseInt(pb.getAttribute("xmlns:h"));
		int pageWidth = Integer.parseInt(pb.getAttribute("xmlns:w"));
		
		PcGtsType page = PageXmlUtils.createEmptyPcGtsType("imgfn", pageWidth, pageHeight);

		TrpTextRegionType region = new TrpTextRegionType();
		region.setId("region_1");
		
		System.out.println("page data: imgFn = "+imgFn+" n = "+pageN+" pageWidth = "+pageWidth+" pageHeight = "+pageHeight);
		
		
		int minX=999999, minY=999999, maxX=-1, maxY=-1;
		
		Node sibling = pbNode.getNextSibling();
		
		int lineCount=0;
		while (sibling != null) {
			if (sibling.getNodeName().equals("pb")) {
				break;
			}

//			System.out.println("sibling type: "+sibling.getTextContent());			
			if (sibling.getNodeType() == Node.ELEMENT_NODE && sibling.getNodeName().equals("lb")) {

				Element lb = (Element) sibling;
				

				
				int n = Integer.parseInt(lb.getAttribute("n"));
				int x = Integer.parseInt(lb.getAttribute("xmlns:x"));
				int y = Integer.parseInt(lb.getAttribute("xmlns:y"));
				int w = Integer.parseInt(lb.getAttribute("xmlns:w"));
				int h = Integer.parseInt(lb.getAttribute("xmlns:h"));
				
				if (x < minX)
					minX = x;
				if (y < minY)
					minY = y;
				if (x+w > maxX)
					maxX = x+w;
				if (y+h > maxY)
					maxY = y+h;
				
				String txt = sibling.getNextSibling().getTextContent();
				txt = StringUtils.stripEnd(txt, " \r\n");
				
//				System.out.println("line: txt = "+txt+" [x,y,w,h] = ["+x+","+y+","+w+","+h+"]");
				
				System.out.format("line: n = %d, txt = %s, coords = [%d,%d,%d,%d]\n", n, txt, x, y, w, h);
				
				TrpTextLineType line = new TrpTextLineType();
				line.setCoords(bbToCoords(x, y, w, h));
				TextEquivType te = new TextEquivType();
				te.setUnicode(txt);
				line.setTextEquiv(te);
				
				line.setId("line_"+(++lineCount));
				
				// create baseline:
				TrpBaselineType bl = new TrpBaselineType();
				int yBl = (int) (y + 0.7 * h);
				bl.setPoints(x+","+yBl+" "+(x+w)+","+yBl);
				line.setBaseline(bl);
				
				region.getTextLine().add(line);
			}
			
			sibling = sibling.getNextSibling();
//			System.out.println("sibling node name: "+sibling.getNodeName());
			
//			if (!sibling.getNodeName().equals("lb"))
//				break;
		}
		
		if (!region.getTextLine().isEmpty()) {
			region.setCoords(bbToCoords(minX, minY, maxX-minX, maxY-minY));
		} else {
			region.setCoords(bbToCoords(0, 0, pageWidth, pageHeight));
		}
		
		page.getPage().getTextRegionOrImageRegionOrLineDrawingRegion().add(region);
		
		if (save && !region.getTextLine().isEmpty()) {
			File xmlFile = new File(PAGE_DIR+FilenameUtils.getBaseName(imgFn)+".xml");
			
			PageXmlUtils.marshalToFile(page, xmlFile);
			FileUtils.copyFile(new File(DIR+imgFn), new File(DST_DIR+imgFn));
			
			System.out.println("written page to: "+xmlFile.getAbsolutePath());
		}
	}
	
	public static void parseFile(String fn) throws ParserConfigurationException, SAXException, IOException, JAXBException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder(); 
		Document doc = db.parse(new File(fn));
		
//		NodeList nl = doc.getElementsByTagName("teiHeader");
		
		//optional, but recommended
		//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
		doc.getDocumentElement().normalize();
		
		NodeList pbs = doc.getElementsByTagName("pb");
		
		System.out.println("nr of pbs: "+pbs.getLength());
		
		for (int i=0; i<pbs.getLength(); ++i) {
			Node pbN = pbs.item(i);
			
			parsePage(pbN, true);
			
//			if (true) {
//				break;
//			}
		}
		
		
		
		
	}
	
	
	public static void main(String[] args) throws Exception {
		
		parseFile(XML_PATH);
		
		
		
	}

}
