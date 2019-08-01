package eu.transkribus.core.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dea.fimagestore.core.beans.ImageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import eu.transkribus.core.io.LocalDocConst;
import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.io.formats.XmlFormat;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.TrpTranscriptStatistics;
import eu.transkribus.core.model.beans.customtags.CustomTagUtil;
import eu.transkribus.core.model.beans.enums.TranscriptionLevel;
import eu.transkribus.core.model.beans.pagecontent.CoordsType;
import eu.transkribus.core.model.beans.pagecontent.MetadataType;
import eu.transkribus.core.model.beans.pagecontent.ObjectFactory;
import eu.transkribus.core.model.beans.pagecontent.PageType;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.PrintSpaceType;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TableRegionType;
import eu.transkribus.core.model.beans.pagecontent.TextEquivType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpElementCoordinatesComparator;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpObjectFactory;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpWordType;
import eu.transkribus.core.model.builder.TrpPageMarshalListener;
import eu.transkribus.core.model.builder.TrpPageUnmarshalListener;
import eu.transkribus.interfaces.types.Image;
import eu.transkribus.interfaces.types.util.TrpImageIO;
import eu.transkribus.interfaces.types.util.TrpImageIO.RotatedBufferedImage;
import eu.transkribus.interfaces.types.util.TrpImgMdParser.ImageTransformation;

public class PageXmlUtils {
	private static final Logger logger = LoggerFactory.getLogger(PageXmlUtils.class);

	private static final String schemaLocStr = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15 "
			+ "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15/pagecontent.xsd";
	
	public static final XmlFormat TRP_PAGE_VERSION = XmlFormat.PAGE_2013;
	private static final String ABBY_TO_PAGE_XSLT = "xslt/Abbyy10ToPage2013.xsl";
	private static final String ALTO_TO_PAGE_XSLT = "xslt/AltoToPage2013.xsl";
	private static final String ALTO_V3_TO_PAGE_XSLT = "xslt/Altov3ToPage2013.xsl";
	private static final String ALTO_BNF_TO_PAGE_XSLT = "xslt/AltoBnFToPage.xsl";
	private static final String TEXT_STYLE_XSL_PARAM_NAME = "preserveTextStyles";
	private static final String FONT_FAM_XSL_PARAM_NAME = "preserveFontFam";
	
	private static final String NO_EVENTS_MSG = "No events occured during marshalling xml file!";

	//	private final static SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	//	private static Schema schema=null;
	//	static {
	//		try {
	//			schema = sf.newSchema(new File("xsd/pagecontent.xsd"));
	//		} catch (SAXException e) {
	//			logger.error(e);
	//		}
	//	}
	//	public static Schema getSchema() { return schema; }

	public static JAXBContext createPageJAXBContext() throws JAXBException {
		return JAXBContext.newInstance("eu.transkribus.core.model.beans.pagecontent");
	}

	public static Unmarshaller createUnmarshaller(ValidationEventCollector vec) throws JAXBException {
		JAXBContext jc = createPageJAXBContext();

		Unmarshaller u = jc.createUnmarshaller();
		try {
			u.setProperty("com.sun.xml.internal.bind.ObjectFactory", new TrpObjectFactory());
		} catch(PropertyException pe) {
			u.setProperty("com.sun.xml.bind.ObjectFactory", new TrpObjectFactory());
		}
		u.setListener(new TrpPageUnmarshalListener());

		if(vec != null) {
			u.setEventHandler(vec);
		}
		
		return u;
	}
	
	public static Unmarshaller createUnmarshaller() throws JAXBException {
		return createUnmarshaller(null);
	}
	
	private static Marshaller createMarshaller() throws JAXBException {
		return createMarshaller(new ValidationEventCollector());
	}
	
	private static Marshaller createMarshaller(ValidationEventCollector vec) throws JAXBException {
		JAXBContext jc = createPageJAXBContext();
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocStr);
		m.setEventHandler(vec);
		m.setListener(new TrpPageMarshalListener());
		
		return m;
	}
	
	/**
	 * This method gets called after a PcGtsType was successfully marshalled.
	 * It is needed e.g. because the {@link TrpPageUnmarshalListener#afterUnmarshal(Object, Object)}
	 * method is called <em>before</em> idref' are set and thus this information is not available there.
	 */
	private static void onPostConstruct(PcGtsType pageData) {
		// sync reading order from page with internal reading order using custom tags:
		if (pageData.getPage() instanceof TrpPageType) {
			CustomTagUtil.writeReadingOrderFromPageFormatToCustomTags((TrpPageType) pageData.getPage());
			((TrpPageType) pageData.getPage()).sortContent();
		}
	}
	
	public static PcGtsType unmarshal(TrpTranscriptMetadata md, boolean returnEmptyPageIfUrlIsNull) throws IOException, JAXBException {
		if (md == null)
			throw new IOException("Metadata is null!");
		
		URL url = md.getUrl();
		if (url != null) {
			return PageXmlUtils.unmarshal(url);
		}
		else if (returnEmptyPageIfUrlIsNull)
			return createEmptyPcGtsType(md.getPagePageReferenceForLocalDocs());
		
		return null;
	}

	public static PcGtsType unmarshal(File file) throws JAXBException {	
		Unmarshaller u = createUnmarshaller();
		
		//Use FileInputStream because JaxB handles File objects via URL internally and thus does not
		//allow all POSIX compliant file names, e.g. containing "#"
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new JAXBException("File not found: " + file.getAbsolutePath(), e);
		}
		
		@SuppressWarnings("unchecked")
		PcGtsType pageData = ((JAXBElement<PcGtsType>) u.unmarshal(fis)).getValue();
		onPostConstruct(pageData);
		
		try {
			fis.close();
		} catch (IOException e) {
			logger.warn("A FileInputStream could not be closed after reading PAGE XML.");
		}
		
		return pageData;
	}
	
	public static PcGtsType unmarshal(InputStream is) throws JAXBException {
		return unmarshal(is, null);
	}
	
	public static PcGtsType unmarshal(InputStream is, ValidationEventCollector vec) throws JAXBException {
		Unmarshaller u = createUnmarshaller(vec);

		@SuppressWarnings("unchecked")
		PcGtsType pageData = ((JAXBElement<PcGtsType>) u.unmarshal(is)).getValue();
		onPostConstruct(pageData);
		
		return pageData;
	}

	public static PcGtsType unmarshal(URL url) throws JAXBException {
		Unmarshaller u = createUnmarshaller();

		@SuppressWarnings("unchecked")
		PcGtsType pageData = ((JAXBElement<PcGtsType>) u.unmarshal(url)).getValue();
		onPostConstruct(pageData);
		
		return pageData;
	}
	
	public static PcGtsType unmarshal(String pageStr) throws JAXBException {
		Unmarshaller u = createUnmarshaller();
		
		StringReader sr = new StringReader(pageStr);
		@SuppressWarnings("unchecked")
		PcGtsType pageData = ((JAXBElement<PcGtsType>) u.unmarshal(sr)).getValue();
		return pageData;
	}
	
	public static PcGtsType unmarshal(byte[] bytes) throws JAXBException {	
		Unmarshaller u = createUnmarshaller();
	
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		
		@SuppressWarnings("unchecked")
		PcGtsType pageData = ((JAXBElement<PcGtsType>) u.unmarshal(bis)).getValue();
		onPostConstruct(pageData);
		
		return pageData;
	}
	
//	public static File marshalToFile(PcGtsType page, File fileOut) throws JAXBException, IOException {
//		return marshalToFile(page, fileOut, true);
//	}
	public static File marshalToFile(PcGtsType page, File fileOut) throws JAXBException, IOException {
		ValidationEventCollector vec = new ValidationEventCollector();
		Marshaller marshaller = createMarshaller(vec);
		
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<PcGtsType> je = objectFactory.createPcGts(page);
			
		File backup=null;
		if (fileOut.exists()) {
			logger.debug("file exists: "+fileOut.getAbsolutePath()+ " - backing up!");
			backup = CoreUtils.backupFile(fileOut);
		}
		
		try {
			marshaller.marshal(je, fileOut);
		} catch (Exception e) {
			if (backup!=null) {
				logger.debug("restoring backup: "+backup.getAbsolutePath());
				FileUtils.copyFile(backup, fileOut);
			}
			if (e instanceof JAXBException)
				throw e;
			else			
				throw new JAXBException(e.getMessage(), e);
		} finally {
			if (backup!=null)
				backup.delete();
		}
		String msg=buildMsg(vec, page);
		if (!msg.startsWith(NO_EVENTS_MSG))
			logger.info(msg);
		
		return fileOut;
	}

	public static byte[] marshalToBytes(PcGtsType page) throws JAXBException {
		ValidationEventCollector vec = new ValidationEventCollector();
		Marshaller marshaller = createMarshaller(vec);
		
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<PcGtsType> je = objectFactory.createPcGts(page);
		byte[] data;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			try {
				marshaller.marshal(je, out);
				data = out.toByteArray();
			} finally {
				out.close();
			}
		} catch (Exception e) {
			throw new MarshalException(e);
		}
		
		String msg=buildMsg(vec, page);
		if (!msg.startsWith(NO_EVENTS_MSG))
			logger.info(msg);
		
		return data;
	}

	private static String buildMsg(ValidationEventCollector vec, PcGtsType page) {
		String imgFn = "";
		if (page.getPage()!=null) {
			imgFn = page.getPage().getImageFilename();
		}
		
		String msg;
		if (vec.hasEvents()) {
			msg="Events occured while marshalling xml file: " + vec.getEvents().length;
		} else {
			msg=NO_EVENTS_MSG;
		}
		if (!imgFn.isEmpty()) msg += " (img: "+imgFn+")";
		return msg;
	}
	
	public static Polygon buildPolygon(final CoordsType coords) {
    	return buildPolygon(coords.getPoints());
	}
	
	/**
	 * 
	 * See PointStrUtils
	 * @param pointsStr
	 * @return
	 */
	@Deprecated 
	public static Polygon buildPolygon(String pointsStr) {
		Polygon p = new Polygon();
		//pointsStr MIGHT contain leading or trailing whitespace from some tool..
		pointsStr = pointsStr.trim();
		if(pointsStr == null || pointsStr.isEmpty()){
    		return p;
    	}
		try{
			final String[] coordsArr = pointsStr.split(" ");
			for (int i = 0; i < coordsArr.length; i++) {
				final String[] xy = coordsArr[i].split(",");
				final int x = Integer.parseInt(xy[0]);
				final int y = Integer.parseInt(xy[1]);
				p.addPoint(x, y);
			}
		} catch(NumberFormatException e){
			logger.error("Bad coords String: " + pointsStr);
			throw e;
		}
		return p;
	}
	
	public static Polygon getOffsetPolygon(Polygon poly, Rectangle boundRect) {
		final int x0 = boundRect.x;
		final int y0 = boundRect.y;
		int[] xpoints = poly.xpoints;
		int[] ypoints = poly.ypoints;
		int npoints = poly.npoints;
		for(int i = 0; i < xpoints.length; i++){
			xpoints[i] -= x0;
		}
		for(int i = 0; i < ypoints.length; i++){
			ypoints[i] -= y0;
		}
		return new Polygon(xpoints, ypoints, npoints);
	}
	
	public static PcGtsType createEmptyPcGtsType(final TrpPage p) throws IOException {
		final String fn = p.getImgFileName();
		Dimension dim = new Dimension(p.getWidth(), p.getHeight());
		return createEmptyPcGtsType(fn, dim);
	}
	
	public static File createEmptyPAGEFile(String imgFilename, Integer width, Integer height, File xmlFile) throws IOException {
		PcGtsType emptyPcGtsType = PageXmlUtils.createEmptyPcGtsType(imgFilename, width, height);
        try {
			return PageXmlUtils.marshalToFile(emptyPcGtsType, xmlFile);
		} catch (JAXBException e) {
			throw new IOException("Could not create empty PAGE XML at: " + xmlFile.getAbsolutePath(), e);
		}
	}

	private static PcGtsType createEmptyPcGtsTypeForRemoteImg(final URL url, ImageMetadata imgMd) throws IOException {
		int xDim = new Double(imgMd.getxResolution()).intValue();
		int yDim = new Double(imgMd.getyResolution()).intValue();
		return createEmptyPcGtsType(imgMd.getOrigFilename(), xDim, yDim);
	}

	public static PcGtsType createEmptyPcGtsType(final File imgFile, Dimension dim) {
		return createEmptyPcGtsType(imgFile.getName(), dim);
	}
	
	public static PcGtsType createEmptyPcGtsType(final String imgFileName, Dimension dim) {
		if(dim == null) {
			logger.error("Dimension is null! The resulting PAGE XML will be created with (0,0).");
			dim = new Dimension();
		}
		logger.debug("width = " + dim.width + " | height = " + dim.height);
		return createEmptyPcGtsType(imgFileName, dim.width, dim.height);
	}
	
	public static PcGtsType createEmptyPcGtsType(final String imgFileName, final Integer xDim,
			final Integer yDim) {
		// create md
		MetadataType md = new MetadataType();
		md.setCreator("TRP");
		XMLGregorianCalendar xmlCal = JaxbUtils.getXmlCalendar(new Date());
		md.setCreated(xmlCal);
		md.setLastChange(xmlCal);

		//create TRP (!) pageType
		TrpPageType pt = new TrpPageType();
		if (imgFileName != null) {
			pt.setImageFilename(imgFileName);
		}
		if (yDim != null) {
			pt.setImageHeight(yDim);
		}
		if (xDim != null) {
			pt.setImageWidth(xDim);
		}

		//create root and set stuff
		PcGtsType pc = new PcGtsType();
		pc.setMetadata(md);
		pc.setPage(pt);
		return pc;
	}
		
	public static PcGtsType createPcGtsTypeFromText(final String imgFileName, Dimension dim, String text, TranscriptionLevel level, boolean skipEmptyLines) throws IOException {
		// create empty page
		PcGtsType pcGtsType = createEmptyPcGtsType(imgFileName, dim);
		TrpPageType page = (TrpPageType) pcGtsType.getPage();
		
		// create and add text region with size of image
		Rectangle r = new Rectangle(0, 0, page.getImageWidth(), page.getImageHeight());
		String defaultCoords = PointStrUtils.pointsToString(r);
		TrpTextRegionType region = new TrpTextRegionType((TrpPageType) page);
		region.setId("region_1");
		region.setCoordinates(defaultCoords, null);
		page.getTextRegionOrImageRegionOrLineDrawingRegion().add(region);
		
		if (level == null) {
			level = TranscriptionLevel.LINE_BASED;
		}
		if (level != TranscriptionLevel.REGION_BASED && level != TranscriptionLevel.LINE_BASED && level != TranscriptionLevel.WORD_BASED) {
			throw new IOException("Invalide TranscriptionLevel: "+level);
		}
		
		if (level == TranscriptionLevel.REGION_BASED) {
			region.setUnicodeText(text, null);
		}
		else {
			String splitRegex = skipEmptyLines ? "[\\r\\n]+" : "\\r?\\n";
			
			String[] lines = text.split(splitRegex);
			logger.debug("nr of lines = "+lines.length);
			
			int lc=1;
			for (String lineText : lines) {
				TrpTextLineType line = new TrpTextLineType(region);
				line.setId("line_"+(lc++));
				line.setCoordinates(defaultCoords, null);
				region.getTextLine().add(line);
				if (level == TranscriptionLevel.LINE_BASED) {
					line.setUnicodeText(lineText, null);
				}
				else if (level == TranscriptionLevel.WORD_BASED) {
					int wc=1;
					for (String wordText : lineText.split(" ")) { // TODO: better word splitting??
						TrpWordType word = new TrpWordType(line);
						word.setId("word_"+(wc++));
						word.setCoordinates(defaultCoords, null);
						word.setUnicodeText(wordText, null);
						line.getWord().add(word);
					}
				}
			}
		}		
		
		return pcGtsType;
	}
	
	public static PcGtsType createPcGtsTypeFromAbbyy(File abbyyXml, final String imgFileName, 
			boolean preserveOcrTxtStyles, boolean preserveOcrFontFamily, boolean replaceBadChars) throws TransformerException, SAXException, IOException, ParserConfigurationException, JAXBException {
		// simple transform to file. Does not set imageFileName!!
		// pageXml = XslTransformer.transform(abbyyXml, ABBY_TO_PAGE_XSLT, pageOutFile);
		
		Map<String, Object> params = null;
		//set parameter for textStyle preservation
		params = new HashMap<>();
		params.put(TEXT_STYLE_XSL_PARAM_NAME, new Boolean(preserveOcrTxtStyles));
		params.put(FONT_FAM_XSL_PARAM_NAME, new Boolean(preserveOcrFontFamily));
		
		// transform into Object and set imgFileName as it is not avail in abbyy XML
		PcGtsType pc = JaxbUtils.transformToObject(abbyyXml, ABBY_TO_PAGE_XSLT, params, PcGtsType.class);
		pc.getPage().setImageFilename(imgFileName);
		if(replaceBadChars){
			pc = FinereaderUtils.replaceBadChars(pc);
		}
		
		return pc;
	}
	
	public static PcGtsType createPcGtsTypeFromAlto(File altoXml, String imgFileName,
			boolean preserveOcrTxtStyles, boolean preserveOcrFontFamily, boolean replaceBadChars) throws TransformerException, SAXException, IOException, ParserConfigurationException, JAXBException {
		// simple transform to file. Does not set imageFileName!!
		// pageXml = XslTransformer.transform(abbyyXml, ABBY_TO_PAGE_XSLT, pageOutFile);
		
		Map<String, Object> params = null;
		//set parameter for textStyle preservation
		params = new HashMap<>();
		params.put(TEXT_STYLE_XSL_PARAM_NAME, new Boolean(preserveOcrTxtStyles));
		params.put(FONT_FAM_XSL_PARAM_NAME, new Boolean(preserveOcrFontFamily));
		
		// transform into Object and set imgFileName as it is not avail in abbyy XML
		PcGtsType pc = JaxbUtils.transformToObject(altoXml, ALTO_TO_PAGE_XSLT, params, PcGtsType.class);
		pc.getPage().setImageFilename(imgFileName);
		if(replaceBadChars){
			pc = FinereaderUtils.replaceBadChars(pc);
		}
		
		return pc;
	}
	
	public static PcGtsType createPcGtsTypeFromAltoBnF(File altoXml, String imgFileName,
			boolean preserveOcrTxtStyles, boolean preserveOcrFontFamily, boolean replaceBadChars) throws TransformerException, SAXException, IOException, ParserConfigurationException, JAXBException {
		// simple transform to file. Does not set imageFileName!!
		// pageXml = XslTransformer.transform(abbyyXml, ABBY_TO_PAGE_XSLT, pageOutFile);
		
		Map<String, Object> params = null;
		//set parameter for textStyle preservation
		params = new HashMap<>();
		params.put(TEXT_STYLE_XSL_PARAM_NAME, new Boolean(preserveOcrTxtStyles));
		params.put(FONT_FAM_XSL_PARAM_NAME, new Boolean(preserveOcrFontFamily));
		
		// transform into Object and set imgFileName as it is not avail in abbyy XML
		PcGtsType pc = JaxbUtils.transformToObject(altoXml, ALTO_BNF_TO_PAGE_XSLT, params, PcGtsType.class);
		pc.getPage().setImageFilename(imgFileName);
		if(replaceBadChars){
			pc = FinereaderUtils.replaceBadChars(pc);
		}
		
		return pc;
	}
	
	public static PcGtsType createPcGtsTypeFromAltov3(File altoXml, String imgFileName,
			boolean preserveOcrTxtStyles, boolean preserveOcrFontFamily, boolean replaceBadChars) throws TransformerException, SAXException, IOException, ParserConfigurationException, JAXBException {
		// simple transform to file. Does not set imageFileName!!
		// pageXml = XslTransformer.transform(abbyyXml, ABBY_TO_PAGE_XSLT, pageOutFile);
		
		Map<String, Object> params = null;
		//set parameter for textStyle preservation
		params = new HashMap<>();
		params.put(TEXT_STYLE_XSL_PARAM_NAME, new Boolean(preserveOcrTxtStyles));
		params.put(FONT_FAM_XSL_PARAM_NAME, new Boolean(preserveOcrFontFamily));
		
		// transform into Object and set imgFileName as it is not avail in abbyy XML
		PcGtsType pc = JaxbUtils.transformToObject(altoXml, ALTO_V3_TO_PAGE_XSLT, params, PcGtsType.class);
		pc.getPage().setImageFilename(imgFileName);
		if(replaceBadChars){
			pc = FinereaderUtils.replaceBadChars(pc);
		}
		
		return pc;
	}

	public static void removeAllLines(PcGtsType pc) {
		if (!hasRegions(pc)){
			return;
		}
		
		List<TrpRegionType> regions = pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
		for (RegionType r : regions) {
			if (r instanceof TextRegionType) {
				TextRegionType tr = (TextRegionType) r;
				logger.debug("Clearing text region: " + tr.getId());
				tr.getTextLine().clear();
			}
		}
	}
	
	public static List<TextLineType> getLines(PcGtsType pc) {
		List<TextLineType> lines = new ArrayList<>();
		List<TextRegionType> regions = PageXmlUtils.getTextRegions(pc);
		for (TextRegionType r : regions) {
			lines.addAll((r.getTextLine()));
		}
		return lines;
	}

	public static List<TextLineType> getLinesInRegion(PcGtsType pc, final String regId) {
		if (!hasRegions(pc)){
			return new ArrayList<>();
		}
		
		List<TextRegionType> regions = PageXmlUtils.getTextRegions(pc);
		for (TextRegionType r : regions) {
			if (r.getId().equals(regId)) {
				return r.getTextLine();
			}
		}
		return new ArrayList<>();
	}
	
	public static void copyTextContent(PcGtsType origPc, PcGtsType newPc) {
		if(!hasRegions(origPc) || !hasRegions(newPc)){
			return;
		}
		List<TrpRegionType> origRegs = origPc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
		List<TrpRegionType> newRegs = newPc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
		//map the regions where we want to keep the textContent
		Map<String, TextRegionType> textMap = new HashMap<>();
		//iterate all old regions. Map the ones containing lines
		for(RegionType r : origRegs){
			if(!(r instanceof TextRegionType)){
				continue;
			} 
			TextRegionType tr =  (TextRegionType) r;
			boolean hasTextLines = tr.getTextLine() != null && !tr.getTextLine().isEmpty();
			
			//disregard region content
//			boolean hasTextContent = tr.getTextEquiv() != null;
//			final String unicode = tr.getTextEquiv().getUnicode();
//			hasTextContent &= unicode != null && !unicode.isEmpty();
					
			if(hasTextLines){
				textMap.put(tr.getId(), tr);
			}
		}
		
		//iterate the new regions and move all the line contents from the old one
		for(RegionType r : newRegs){
			if(!(r instanceof TextRegionType) || !textMap.containsKey(r.getId())){
				continue;
			}
			//this region corresponds with an old one
			TextRegionType newTr = (TextRegionType) r;
			TextRegionType oldTr = textMap.get(newTr.getId());
			copyTextRegionContent(oldTr, newTr);
		}
	}

	private static void copyTextRegionContent(TextRegionType oldTr, TextRegionType newTr) {
		List<TextLineType> oldLines = oldTr.getTextLine();
		List<TextLineType> newLines =  newTr.getTextLine();
		if(newLines == null || newLines.isEmpty()){
			//nothing to do here		for(String id : regIds){
			logger.error("There are no textLines to copy to in new Region " + newTr.getId());
			return;
		}
		TrpElementCoordinatesComparator<TextLineType> comp = new TrpElementCoordinatesComparator<>();
		Collections.sort(oldLines, comp);
		Collections.sort(newLines, comp);
		for(int i = 0; i < oldLines.size(); i++){
			TextLineType l = oldLines.get(i);
			if(i < newLines.size()){
				TextLineType nl = newLines.get(i);
				nl.setTextEquiv(l.getTextEquiv());
				nl.setTextStyle(l.getTextStyle());
				if(l.getTextEquiv() != null && l.getTextEquiv().getUnicode() != null){
					logger.debug("Copy line: " + l.getTextEquiv().getUnicode());
				}
			} else {
				//the new segmentation has less lines than the old one. 
				//concat the remaining text in last line
				TextLineType nl = newLines.get(newLines.size()-1);
				if(nl.getTextEquiv() != null && nl.getTextEquiv().getUnicode() != null 
						&& l.getTextEquiv() != null && l.getTextEquiv().getUnicode() != null){
					final String text = nl.getTextEquiv().getUnicode() + " " + l.getTextEquiv().getUnicode();
					nl.getTextEquiv().setUnicode(text);
					logger.debug("Concat to last line: " + l.getTextEquiv().getUnicode());
				}
			}
		}
	}
	
	public static void moveTextRegion(final String regId, PcGtsType sourcePc, PcGtsType targetPc) {
		TextRegionType source = findTextRegion(regId, sourcePc);
		if(source == null){
			throw new IllegalArgumentException("TextRegion ID=" + regId + " could not be found!");
		}
		TextRegionType target = findTextRegion(regId, targetPc);
		if(target == null){
			throw new IllegalArgumentException("TextRegion ID=" + regId + " could not be found!");
		}
		setTextRegion(regId, targetPc, source);
		target = source;
	}

	private static TextRegionType findTextRegion(String regId, PcGtsType pc) {
		RegionType reg = findRegion(regId, pc);
		TextRegionType textReg = null;
		if(reg != null && reg instanceof TextRegionType){
			textReg = (TextRegionType) reg;
			logger.debug("Found textRegion: " + textReg.getId());
		}
		return textReg;
	}
	
	public static void moveAllRegions(PcGtsType source, PcGtsType target){
		if(source == null || target == null){
			throw new IllegalArgumentException("A parameter is null!");
		}
		if(target.getPage() == null){
			throw new IllegalArgumentException("The target pageType is null!");
		}
		if(source.getPage() == null){
			throw new IllegalArgumentException("The source pageType is null!");
		}
		List<TrpRegionType> oldRegions = target.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
		//clear the target from all regions
		if(oldRegions != null && !oldRegions.isEmpty()){
			logger.debug("Clearing regions in target PAGE.");
			oldRegions.clear();
		}
		List<TrpRegionType> newRegions = source.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
		if(newRegions != null && !newRegions.isEmpty()){
			oldRegions.addAll(newRegions);
		}
		
		//TODO keep the text if any: 
		//what if more/less regions than before?
		//put stuff from source to target
	}
	
	private static void setTextRegion(String regId, PcGtsType pc, TextRegionType region) {
		if(regId == null){ 
			throw new IllegalArgumentException("RegId is null!");
		}
		if(!hasRegions(pc)){
			throw new IllegalArgumentException("PAGE XML has no regions!");
		}
		List<TrpRegionType> regions = pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
		for(int i = 0; i < regions.size(); i++){
			final String id = regions.get(i).getId();
			if(id != null && id.equals(regId)){
				logger.debug("Setting new TextRegion in PAGE region at index=" + i);
				regions.set(i, region);
				return;
			}
		}
		logger.error("The region to replace (ID=" + regId + ") could not be found!");
	}
	
	private static RegionType findRegion(String regId, PcGtsType pc) {
		if(regId == null){ 
			throw new IllegalArgumentException("RegId is null!");
		}
		if(!hasRegions(pc)){
			throw new IllegalArgumentException("PAGE XML has no regions!");
		}
		RegionType item = null;
		for(RegionType r : pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion()){
			if(regId.equals(r.getId())){
				item = r;
				break;
			}
		}
		return item;
	}

	public static boolean hasRegions(PcGtsType pc) {
		if (pc.getPage() == null){
			return false;
		}
		List<TrpRegionType> regions = pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
		if (regions == null || regions.isEmpty()){
			return false;
		}
		return true;
	}
	
	public static boolean regionExists(PcGtsType pc, final String regId) {
		if (pc.getPage() == null){
			return false;
		}
		List<TrpRegionType> regions = pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
		if (regions == null || regions.isEmpty()){
			return false;
		}
		for(RegionType r : regions){
			if(r.getId() != null && r.getId().equals(regId)){
				return true;
			}
		}
		return false;
	}

	public static Polygon getPrintspacePolygon(PcGtsType pc) {
		if(pc == null){
			throw new IllegalArgumentException("PcGtsType object is null!");
		}
		PrintSpaceType psType = pc.getPage().getPrintSpace();
		if(psType == null){
			throw new IllegalArgumentException("No printspace available.");
		}
		return buildPolygon(psType.getCoords());
	}
	
	public static List<TextRegionType> getTextRegions(PcGtsType pc) {
		List<TrpRegionType> regions = pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
		
		List<TextRegionType> tRegions = new ArrayList<>();
		if (regions == null || regions.isEmpty()) {
			return tRegions;
		}
		
		for(RegionType r : regions) {
			if (r == null)
				continue;
			
			if (TextRegionType.class.isAssignableFrom(r.getClass())) {
				tRegions.add((TextRegionType) r);
			}
			
			if (TableRegionType.class.isAssignableFrom(r.getClass())) {
				TableRegionType table = (TableRegionType) r;
				tRegions.addAll(table.getTableCell());				
			}
		}
		return tRegions;
	}

	public static void removeExcludedRegions(PcGtsType pc, List<String> regIds) {
		List<TrpRegionType> regions = pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
		if (regions == null || regions.isEmpty()){
			return;
		}		
		for(int i = 0; i < regions.size();){
			RegionType r = regions.get(i);
			if(!regIds.contains(r.getId())){
				logger.debug("Removing excluded region: " + r.getId());
				regions.remove(r);
			} else {
				i++;
			}
		}
	}

	/**
	 * If regions overlap the img border, reset offlimit coordinates to min/max
	 * 
	 * @param pc
	 */
	public static void cutPolysAtImgBorder(PcGtsType pc) {
		final int maxX = pc.getPage().getImageWidth();
		final int maxY = pc.getPage().getImageHeight();
		List<TrpRegionType> regions = pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
		if (regions == null || regions.isEmpty()){
			return;
		}
		StringBuilder sb;
		for(RegionType r : regions){
			sb = new StringBuilder();
			CoordsType c = r.getCoords();
			final String pointsStr = c.getPoints();
	    	if(pointsStr == null || pointsStr.isEmpty()){
	    		continue;
	    	}
	    	final String[] coordsArr = pointsStr.split(" ");
	    	
			for (int i = 0; i < coordsArr.length; i++) {
				final String[] xy = coordsArr[i].split(",");
				final int x = Integer.parseInt(xy[0]);
				final int y = Integer.parseInt(xy[1]);
				sb.append(x < 0 ? 0 : (x > maxX ? maxX : x));
				sb.append(",");
				sb.append(y < 0 ? 0 : (y > maxY ? maxY : y));
				sb.append(" ");
			}
			c.setPoints(sb.toString().trim());
		}
	}

	public static void setTextToLine(String text, PcGtsType pc, String lineId) {
		TextLineType tl = findLineById(pc, lineId);
		
		if(tl == null) {
			logger.info("Line does not exist: " + lineId);
			return;
		}
		
		logger.debug("Setting text in line=" + lineId + ": " + text);
		
		if(tl.getTextEquiv() == null){
			logger.debug("Creating new TextEquiv element.");
			TextEquivType textEquiv = new TextEquivType();
			textEquiv.setUnicode(text);
			tl.setTextEquiv(textEquiv);
		} else {
			logger.debug("Setting text in existing TextEquiv element.");
			tl.getTextEquiv().setUnicode(text);
		}
	}
	
	public static TextLineType findLineById(PcGtsType pc, final String lineId) {
		if(pc == null || lineId == null) {
			throw new IllegalArgumentException("Arguments must not be null");
		}
		List<TextRegionType> trList = getTextRegions(pc);
		for(TextRegionType tr : trList){
			List<TextLineType> tlList = tr.getTextLine();
			if(tlList != null && !tlList.isEmpty()){
				for(TextLineType tl : tlList){
					if(tl.getId().equals(lineId)){
						return tl;
					}
				}
			}
		}
		return null;
	}

	public static String getFulltextFromLines(PcGtsType pc) {
		List<TextRegionType> regions = PageXmlUtils.getTextRegions(pc);
		TrpElementCoordinatesComparator<TextLineType> comp = new TrpElementCoordinatesComparator<>();
		StringBuilder sb = new StringBuilder();
		for(TextRegionType r : regions){
			List<TextLineType> lines = r.getTextLine();
			if(lines != null && !lines.isEmpty()){
				Collections.sort(lines, comp);
				for(TextLineType l : lines){
					if(l.getTextEquiv() != null && l.getTextEquiv().getUnicode() != null){
						sb.append(l.getTextEquiv().getUnicode() + " ");
					}
				}
			}
		}
		
		return sb.toString();
	}

	public static void removeAllIndexedTags(PcGtsType pc) {
		TrpPageType p = (TrpPageType) pc.getPage();
		List<TrpTextRegionType> trList = p.getTextRegions(true);
		for(TrpTextRegionType tr : trList){
			tr.getCustomTagList().removeIndexedTags();
			List<TextLineType> lineList = tr.getTextLine();
			for(TextLineType l : lineList){
				TrpTextLineType trpL = (TrpTextLineType)l;
				trpL.getCustomTagList().removeIndexedTags();
				List<WordType> wordList = trpL.getWord();
				for(WordType w : wordList){
					TrpWordType trpW = (TrpWordType)w;
					trpW.getCustomTagList().removeIndexedTags();
				}
			}
		}
	}
	
	public static TrpTranscriptStatistics extractStats(PcGtsType page) {
		TrpTranscriptStatistics s = new TrpTranscriptStatistics();
		
		int nrOfRegions, nrOfTranscribedRegions, nrOfWordsInRegions, 
		nrOfLines, nrOfTranscribedLines, nrOfWordsInLines, nrOfWords, nrOfTranscribedWords;
		nrOfRegions = nrOfTranscribedRegions = nrOfWordsInRegions =  
		nrOfLines = nrOfTranscribedLines = nrOfWordsInLines = nrOfWords = nrOfTranscribedWords = 0;
		
		List<TextRegionType> regs = PageXmlUtils.getTextRegions(page);
		nrOfRegions = regs.size();
		
		for(TextRegionType r : regs) {
			if(r.getTextEquiv() != null && r.getTextEquiv().getUnicode() != null 
					&& !r.getTextEquiv().getUnicode().trim().isEmpty()) {
				nrOfTranscribedRegions += 1;
				//TODO use tokenizer here
				//next line is needed for non-breaking whitespace characters; that are excluded from the Character.isWhitespace() method
				String tmp = r.getTextEquiv().getUnicode().replaceAll("[\\u00A0\\u2007\\u202F]+", " ");
				nrOfWordsInRegions += tmp.split(" ").length;
			}
			List<TextLineType> lines = r.getTextLine();
			nrOfLines += lines.size();
			for(TextLineType l : lines) {
				if(l.getTextEquiv() != null && l.getTextEquiv().getUnicode() != null 
						&& !l.getTextEquiv().getUnicode().trim().isEmpty()) {
					nrOfTranscribedLines += 1;
					//TODO use tokenizer here
					//next line is needed for non-breaking whitespace characters; that are excluded from the Character.isWhitespace() method
					String tmp = l.getTextEquiv().getUnicode().replaceAll("[\\u00A0\\u2007\\u202F]+", " ");
					nrOfWordsInLines += tmp.split(" ").length;
				}
				List<WordType> words = l.getWord();
				nrOfWords += words.size();
				for(WordType w : words) {
					if(w.getTextEquiv() != null && w.getTextEquiv().getUnicode() != null 
							&& !w.getTextEquiv().getUnicode().trim().isEmpty()) {
						nrOfTranscribedWords += 1;
					}
				}
			}
		}
		
		s.setNrOfLines(nrOfLines);
		s.setNrOfRegions(nrOfRegions);
		s.setNrOfTranscribedLines(nrOfTranscribedLines);
		s.setNrOfTranscribedWords(nrOfTranscribedWords);
		s.setNrOfTranscribedRegions(nrOfTranscribedRegions);
		s.setNrOfWords(nrOfWords);
		s.setNrOfWordsInLines(nrOfWordsInLines);
		s.setNrOfWordsInRegions(nrOfWordsInRegions);
		
		return s;
	}
	
	public static boolean isValid(File xmlFile) throws IOException {
		if(xmlFile == null || !xmlFile.isFile()) {
			throw new IllegalArgumentException("Bad argument: " + xmlFile);
		}
		//FIXME TranskribusCore issue #20: Schema location should be set in XmlFormat.java!
//		URL schemaFile = new URL("http://host:port/filename.xsd");
		URL schemaUrl = PageXmlUtils.class.getClassLoader().getResource("xsd/pagecontent_extension.xsd");
		return XmlUtils.isValid(xmlFile, schemaUrl);
	}

//	public static boolean isBaselineInLineBounds(TextLineType tl, String baseline, final int threshold) {
//		final Polygon linePoly = PageXmlUtils.buildPolygon(tl.getCoords());
//		Rectangle boundRect = linePoly.getBounds();
//		List<Point> blPoints = PointStrUtils.parsePoints(baseline);
//		boolean isIncluded = true;
//		for(Point p : blPoints) {
//			if(!GeomUtils.isInside(p.x, p.y, boundRect, threshold)) {
//				isIncluded = false;
//				break;
//			}
//		}
//		return isIncluded;
//	}
	
//	public static double getOverlap(TextLineType tl, String baseline) {
//		final String linePoints = tl.getCoords().getPoints();
//		logger.debug("Line points: " + linePoints);
//		List<Point2D> pointsLine = PointStrUtils.buildPoints2DList(linePoints);
//		List<Point2D> pointsBaseline = PointStrUtils.buildPoints2DList(baseline);
//		double o = GeomUtils.getOverlap(pointsLine, pointsBaseline);
//		if(o > 0) {
//			logger.debug("Overlap is: " + o);
//		}
//		return o;
//	}
	
	public static boolean doesIntersect(TextLineType tl, String baseline) {
		final String linePoints = tl.getCoords().getPoints();
		Polygon linePoly = PointStrUtils.buildPolygon(linePoints);
		Polygon baselinePoly = PointStrUtils.buildPolygon(baseline);
//		logger.debug(linePoly.getBounds2D().toString());
//		logger.debug(baselinePoly.getBounds2D().toString());
		Rectangle2D baselineRect = baselinePoly.getBounds2D();
		if(baselineRect.getHeight() == 0) {
			/*
			 * if the baseline is horizontal, the boundRect includes no area and thus
			 * there will not be an intersection...
			 */
			baselineRect.setRect(
					baselineRect.getX(), 
					baselineRect.getY(), 
					baselineRect.getWidth(), 
					1); //blow this up to be height 1
		}
		return linePoly.intersects(baselineRect);
	}
	
	public static List<TextLineType> findLinesByBaseline(PcGtsType pc, String baseline) {
		List<TextRegionType> regions = getTextRegions(pc);
		List<TextLineType> matchingLines = new LinkedList<>();
		for(TextRegionType r : regions) {
			r.getTextLine()
			.stream()
			.filter(l -> doesIntersect(l, baseline))//isBaselineInLineBounds(l, baseline, threshold))
			.forEach(l -> matchingLines.add(l));
		}
		if(matchingLines.size() > 1) {
			TrpElementCoordinatesComparator<TextLineType> comp = new TrpElementCoordinatesComparator<>(true);
			Collections.sort(matchingLines, comp);
		}
		return matchingLines;
	}
	
	public static void applyAffineTransformation(ITrpShapeType shape, double tx, double ty, double sx, double sy, double rot) throws Exception {
		AffineTransform at = new AffineTransform();
		at.scale(sx, sy);
		at.rotate(rot);
		at.translate(tx, ty);
		applyAffineTransformation(shape, at);
	}
	
	public static void applyAffineTransformation(ITrpShapeType shape, AffineTransform at) throws Exception {
		String coords = shape.getCoordinates();
		logger.trace("applyAffineTransformation, old coords = "+coords);
		String newCoords = PointStrUtils.affineTransPoints(coords, at);
		logger.trace("applyAffineTransformation, new coords = "+newCoords);
		
		shape.setCoordinates(newCoords, null);
	}
	
	/**
	 * Applies an affine transformation, i.e. a translation, scaling and rotation (in radiants!) to all the coordinates of the page
	 */
	public static void applyAffineTransformation(PageType page, double tx, double ty, double sx, double sy, double rot) {		
		page.setImageWidth((int) (page.getImageWidth()*sx));
		page.setImageHeight((int) (page.getImageHeight()*sy));
		
		for (ITrpShapeType shape : ((TrpPageType) page).getAllShapes(true)) {
			try {
				applyAffineTransformation(shape, tx, ty, sx, sy, rot);
			} catch (Exception e) {
				logger.error("Error transforming coordinates of shape "+shape.getId()+": "+e.getMessage(), e);
			}
		}
	}
	
	public static PcGtsType applyAffineTransformation(File xmlFile, ImageTransformation imageTransformation) throws JAXBException {
		return applyAffineTransformation(unmarshal(xmlFile), imageTransformation);
	}
	
	public static PcGtsType applyAffineTransformation(PcGtsType pc, ImageTransformation imageTransformation) {
		if(pc == null) {
			throw new IllegalArgumentException("Given PcGtsType is null.");
		}
		if(imageTransformation == null) {
			return pc;
		}
		pc.getPage().setImageWidth(imageTransformation.getDestinationWidth());
		pc.getPage().setImageHeight(imageTransformation.getDestinationHeight());
		
		for (ITrpShapeType shape : ((TrpPageType) pc.getPage()).getAllShapes(true)) {
			try {
				applyAffineTransformation(shape, imageTransformation.getTransformation());
			} catch (Exception e) {
				logger.error("Error transforming coordinates of shape "+shape.getId()+": "+e.getMessage(), e);
			}
		}
		String comment = "Auto-rotated according to EXIF orientation = " + imageTransformation.getExifOrientation();
		if(!StringUtils.isEmpty(pc.getMetadata().getComments())) {
			comment = pc.getMetadata().getComments() + "\n" + comment;
		}
		pc.getMetadata().setComments(comment);
		return pc;
	}
	
	/** 
	 * Assigns unique IDs to the elements in the page using the current order of the elements. 
	 */
	public static void assignUniqueIDs(PageType page) {
		int i = 1;
		for (RegionType r : page.getTextRegionOrImageRegionOrLineDrawingRegion()) {
			if (r instanceof TextRegionType) {
				TextRegionType region = (TextRegionType) r;
				String rid = "r" + i;

				region.setId(rid);
				int j = 1;
				for (TextLineType l : region.getTextLine()) {
					String lid = rid + "l" + j;
					l.setId(lid);

					int k = 1;
					for (WordType word : l.getWord()) {
						String wid = lid + "w" + k;
						word.setId(wid);

						k++;
					}
					++j;
				}
				++i;
			}
		}
	}
	
	/**
	 * Reads the dimension and exif orientation from the {@link Image} instance and checks if the PAGE XML dimension matches.
	 * If not, it rotates the PAGE XML according to the EXIF orientation tag value stored in the image.<br>
	 * This is only necessary for transcriptions that were produced on the basis of an image that was not correctly oriented
	 * due to issue <a href="https://github.com/Transkribus/TranskribusSwtGui/issues/154">TranskribusSwtGui#154</a>.<br>
	 * This will only work for Image instances that were produced via the constructor {@link Image#Image(URL)} or 
	 * {@link Image#Image(BufferedImage)} where the BufferedImage was created by any of the {@link TrpImageIO}::read methods.
	 * Standard ImageIO will not extract the necessary information.
	 * 
	 * @param image
	 * @param xmlFile
	 * @return the updated xmlFile File instance at the same location as the input XML
	 * @throws IOException
	 */
	public static File checkAndFixXmlOrientation(Image image, File xmlFile) throws IOException {
		BufferedImage bi = image.getImageBufferedImage(true);
		if(!(bi instanceof RotatedBufferedImage)) {
			//nothing to do
			return xmlFile;
		}
		ImageTransformation t = ((RotatedBufferedImage)bi).getImageTransformation();			
		//image data was re-oriented during load. Check if XML fits
		try {
			PcGtsType pc = PageXmlUtils.unmarshal(xmlFile);
			if(isOrientationBroken(t, pc)) {
				/*
				 * this won't catch XMLs were the image was rotated 180°. 
				 * On the other hand, we would also mess up transcriptions that 
				 * were done after the EXIF fix on 180° images.
				 */
				pc = PageXmlUtils.applyAffineTransformation(pc, t);
				PageXmlUtils.marshalToFile(pc, xmlFile);
			}
		} catch (JAXBException e) {
			throw new IOException("PAGE XML could not be read.", e);
		}
		return xmlFile;
	}
	
	/**
	 * Reads the dimension and exif orientation from the {@link Image} instance and checks if the PAGE XML dimension matches.
	 * If not, it rotates the PAGE XML according to the EXIF orientation tag value stored in the image.<br>
	 * This is only necessary for transcriptions that were produced on the basis of an image that was not correctly oriented
	 * due to issue <a href="https://github.com/Transkribus/TranskribusSwtGui/issues/154">TranskribusSwtGui#154</a>.<br>
	 * This will only work for Image instances that were produced via the constructor {@link Image#Image(URL)} or 
	 * {@link Image#Image(BufferedImage)} where the BufferedImage was created by any of the {@link TrpImageIO}::read methods.
	 * Standard ImageIO will not extract the necessary information.
	 * 
	 * @param image
	 * @param xmlFile
	 * @return the PcGtsType
	 * @throws IOException
	 */
	public static PcGtsType checkAndFixXmlOrientation(Image image, PcGtsType pc) throws IOException {
		BufferedImage bi = image.getImageBufferedImage(true);
		if(!(bi instanceof RotatedBufferedImage)) {
			//nothing to do
			return pc;
		}
		//image data was re-oriented during load. Check if XML fits
		return checkAndFixXmlOrientation(((RotatedBufferedImage)bi).getImageTransformation(), pc);			
	}
	/**
	 * Reads the dimension and exif orientation from the {@link ImageTransformation} instance and checks if the PAGE XML dimension matches.
	 * If not, it rotates the PAGE XML according to the EXIF orientation tag value stored in the transformation.<br>
	 * This is only necessary for transcriptions that were produced on the basis of an image that was not correctly oriented
	 * due to issue <a href="https://github.com/Transkribus/TranskribusSwtGui/issues/154">TranskribusSwtGui#154</a>.
	 * 
	 * @param image
	 * @param xmlFile
	 * @return the PcGtsType
	 * @throws IOException
	 */
	public static PcGtsType checkAndFixXmlOrientation(ImageTransformation t, PcGtsType pc) {
		if(isOrientationBroken(t, pc)) {
			logger.debug("Image Dimension does not match PAGE dimension. Applying transformation for EXIF orientation tag value = " + t.getExifOrientation());
			/*
			 * this won't catch XMLs were the image was rotated 180°. 
			 * On the other hand, we would also mess up transcriptions that 
			 * were done after the EXIF fix on 180° images.
			 */
			pc = PageXmlUtils.applyAffineTransformation(pc, t);
		}
		return pc;
	}
	
	private static boolean isOrientationBroken(ImageTransformation t, PcGtsType pc) {
		if(t.isDefaultOrientation()) {
			//no need to inspect further
			return false;
		}
		if(pc.getPage().getImageWidth() != t.getDestinationWidth()) {
			/*
			 * EXIF orientation tag value and dimension mismatch suggest to rotate
			 * This should catch 90° and 270° rotations
			 */
			return true;
		}
		/*
		 * We could use the date when this was fixed and compare it to the LastChange date in the PAGE XML Metadata element.
		 * There seems to be no other (easy) way to determine if a 180° rotation is necessary or not.
		 * However, this would mess up PAGE XML created by third-party applications, so it's deactivated for now.
		 */
		final boolean fixBasedOnDate = false;
		if(fixBasedOnDate) {
			Calendar cal = Calendar.getInstance();
			cal.set(2018, 12, 1);
			long fixTime = cal.getTimeInMillis();
			
			if(pc.getMetadata().getLastChange().getMillisecond() < fixTime) {
				/*
				 * EXIF orientation tag value and the time this XML was written suggest to rotate
				 */
				return true;
			}
		}
		return false;
	}
	
	public static List<File> listAllPageXmlFilesInFolderRecursively(String path) throws IOException {
		return Files.walk(Paths.get(path)).filter(Files::isRegularFile).map(p -> p.toFile()).filter(f -> {
			boolean isXml = f.getName().toLowerCase().endsWith(".xml");
			return isXml && StringUtils.equals(FilenameUtils.getName(f.getParent()), LocalDocConst.PAGE_FILE_SUB_FOLDER);
		}).collect(Collectors.toList());
	}
	
	public static List<Pair<File, File>> listAllImgPageXmlPairsInFolderRecursively(String path) throws IOException {
		List<File> folders = Files.walk(Paths.get(path)).filter(Files::isDirectory).map(p -> p.toFile())
				.filter(folder -> new File(folder.getAbsolutePath()+"/"+LocalDocConst.PAGE_FILE_SUB_FOLDER).exists()).collect(Collectors.toList());
		
//		List<File> folders = new ArrayList<>();
//		folders.add(new File(path));
//		File[] subfolders = new File(path).listFiles(new FileFilter() {
//			@Override
//			public boolean accept(File pathname) {
//				return pathname.isDirectory();
//			}
//		});
//		folders.addAll(Arrays.asList(subfolders));
		
		logger.info("got "+folders.size()+" PAGE XML folders");
		List<Pair<File, File>> pairs = new ArrayList<>();
		for (int i=0; i<folders.size(); ++i) {
			File folder = folders.get(i);
			logger.info("parsing folder "+(i+1)+"/"+folders.size()+", N-pairs = "+pairs.size());
			try {
				pairs.addAll(LocalDocReader.findImgAndPAGEXMLFiles(folder));
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			logger.info("N-pairs = "+pairs.size());
		}
		return pairs;
		
		
//		return Files.walk(Paths.get(path)).filter(Files::isDirectory).map(p -> p.toFile())
//								.filter(folder -> new File(folder.getAbsolutePath()+"/"+LocalDocConst.PAGE_FILE_SUB_FOLDER).exists())
//								.map(folder -> {
//									List<Pair<File, File>> pairs = new ArrayList<>();
//									try {
//										pairs = LocalDocReader.findImgAndPAGEXMLFiles(folder);
//									} catch (IOException e) {
//										logger.error(e.getMessage(), e);
//									}
//									return pairs;
//								})
//								.flatMap(List::stream)
//								.collect(Collectors.toList());
		
//		return Files.walk(Paths.get(path)).filter(Files::isRegularFile).map(p -> p.toFile()).filter(f -> {
//			boolean isXml = f.getName().toLowerCase().endsWith(".xml");
//			return isXml && StringUtils.equals(FilenameUtils.getName(f.getParent()), LocalDocConst.PAGE_FILE_SUB_FOLDER);
//		})
//		.map(f -> f.getParentFile().getParentFile())		
//		.collect(Collectors.toList());
	}
	
	public static List<Pair<File, Exception>> checkPageXMLInFolder(String path, boolean printErrors) throws IOException {
		List<File> pageXmlFiles = listAllPageXmlFilesInFolderRecursively(path);
		List<Pair<File, Exception>> errorFiles = new ArrayList<>();
		for (File f : pageXmlFiles) {
        	try {
				PageXmlUtils.unmarshal(f);
			} catch (JAXBException e) {
				if (printErrors) {
					logger.error(e.getMessage(), e);	
				}
				errorFiles.add(Pair.of(f, e));
			}
		}
		return errorFiles;
	}
	
	public static void applyTextFromWordsToLinesAndRegions(TrpPageType page) {
		for (TrpTextRegionType region : page.getTextRegions(true)) {
			region.applyTextFromWords();
		}
	}
	
	public static void simplifyPoints(RegionType r, double perc) {
		if (r != null) {
			simplifyPoints(r.getCoords(), perc);
		}
	}
	
	public static void simplifyPoints(TextLineType tl, double perc) {
		if (tl != null) {
			simplifyPoints(tl.getCoords(), perc);
		}
	}
	
	public static void simplifyPoints(CoordsType coords, double perc) {
		if (coords!=null && !StringUtils.isEmpty(coords.getPoints())) {
			List<Point> pts = PointStrUtils.parsePoints2(coords.getPoints());
			List<Point> simplified = RamerDouglasPeuckerFilter.filterByPercentageOfPolygonLength(perc, pts);
			coords.setPoints(PointStrUtils.pointsToString(simplified));
		}
	}
	
	public static void simplifyPoints(List<TextLineType> lines, double perc) {
		lines.stream().forEach(tl -> {
			PageXmlUtils.simplifyPoints(tl, RamerDouglasPeuckerFilter.DEFAULT_PERC_OF_POLYGON_LENGTH);
		});
		
	}
	
	public static void simplifyPointsOfAllLines(PcGtsType pcGtsType, double perc) {
		for (TextLineType tl : PageXmlUtils.getLines(pcGtsType)) {
			PageXmlUtils.simplifyPoints(tl, RamerDouglasPeuckerFilter.DEFAULT_PERC_OF_POLYGON_LENGTH);
		}
	}
	
	/**
	 * Applies a given text to all text-lines of the given TrpPageType object.<br/>
	 * If the text does not fit, i.e. there are more lines in the given text than there are lines in the layout,
	 * the overlapping text is squeezed into the last line, i.e. newlines are replaced by whitespaces. 
	 * @param page The TrpPageType object with the layout that the text is fit into
	 * @param text The text which is fit into the lines of the page
	 */
	public static void applyTextToLines(TrpPageType page, String text) {
		String lines[] = text.split("\\r?\\n");
//		for (String line : lines) {
//			logger.info("line: '"+line+"'");
//		}	
//		logger.info("---------------");
		
		List<TrpTextLineType> trpLines = page.getLines();
		
		// match text-lines with trp-lines
		int i=0;
		String txtForLine="";
		for (TrpTextLineType trpLine : trpLines) {
			txtForLine = "";
			if (i<lines.length) {
				txtForLine = lines[i];
			}
			trpLine.setUnicodeText(txtForLine, null);
			++i;
		}
		// if text has more lines than trp-lines, squeeze-in text, i.e. replace newline's with spaces for last line
		if (lines.length>trpLines.size()) {
			for (int j=i; j<lines.length; ++j) {
				txtForLine+=lines[j];
				if (i!=lines.length-1) {
					txtForLine+=" ";
				}
			}
			trpLines.get(trpLines.size()-1).setUnicodeText(txtForLine, null);
		}
		
		for (TrpTextLineType trpLine : trpLines) {
			logger.info("textLine: '"+trpLine.getUnicodeText()+"'");
		}
	}
	
	public static boolean setLastChangedNow(PcGtsType pcGtsType) {
		XMLGregorianCalendar cal;
		try {
			cal = XmlUtils.getXmlGregCal();
			if (pcGtsType != null && pcGtsType.getMetadata() != null) {
				pcGtsType.getMetadata().setLastChange(cal);
				return true;
			}
			else {
				logger.error("pcGtsType or associated metadata is null (should not happen) - cannot update PageXML Metadata!");
				return false;
			}
		} catch (DatatypeConfigurationException e) {
			logger.error("Severe configuration exception! Datatype XMLGregorianCalendar is not available! "
					+ "LastChange date in Page XML Metadata cannot be updated! " + e.getMessage(), e);
			return false;
		}
	}
	
	public static void main(String[] args) throws Exception {
		applyTextToLines(null, "I am a\ntext\n\nover\nmultiple   \nlines ! \n");
		
//		List<Pair<File, Exception>> errorFiles = checkPageXMLInFolder("\\\\na03.uibk.ac.at\\dea_scratch\\tmp_sebastian\\VeryLargeDocument", true);
//		for (Pair<File, Exception> p : errorFiles) {
//			Exception e = p.getRight();
//			String msg = e.getCause()!=null ? e.getCause().getMessage() : e.getMessage();
//			logger.info("Error in file: "+p.getLeft().toString()+", m = "+msg);
//		}
		
//		final String path = "/mnt/dea_scratch/TRP/Bentham_box_002/page/002_080_001.xml";
//		try {
//			logger.info(""+PageXmlUtils.isValid(new File(path)));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		PcGtsType page = PageXmlUtils.createEmptyPcGtsType("imgfilename.jpg", 45, 500);
//		String str = Arrays.toString(PageXmlUtils.marshalToBytes(page));
//		System.out.println(str);
		
		
	}
}
