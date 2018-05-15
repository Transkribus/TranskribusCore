package eu.transkribus.core.util;

import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.ValidationEventCollector;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.dea.fimgstoreclient.beans.FimgStoreImgMd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import eu.transkribus.core.io.FimgStoreReadConnection;
import eu.transkribus.core.io.formats.XmlFormat;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.TrpTranscriptStatistics;
import eu.transkribus.core.model.beans.customtags.CustomTagUtil;
import eu.transkribus.core.model.beans.enums.TranscriptionLevel;
import eu.transkribus.core.model.beans.pagecontent.CoordsType;
import eu.transkribus.core.model.beans.pagecontent.MetadataType;
import eu.transkribus.core.model.beans.pagecontent.ObjectFactory;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.PrintSpaceType;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TableRegionType;
import eu.transkribus.core.model.beans.pagecontent.TextEquivType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpElementCoordinatesComparator;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpObjectFactory;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpWordType;
import eu.transkribus.core.model.builder.TrpPageMarshalListener;
import eu.transkribus.core.model.builder.TrpPageUnmarshalListener;

public class PageXmlUtils {
	private static final Logger logger = LoggerFactory.getLogger(PageXmlUtils.class);

	private static final String schemaLocStr = "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15 "
			+ "http://schema.primaresearch.org/PAGE/gts/pagecontent/2013-07-15/pagecontent.xsd";
	
	public static final XmlFormat TRP_PAGE_VERSION = XmlFormat.PAGE_2013;
	private static final String ABBY_TO_PAGE_XSLT = "xslt/Abbyy10ToPage2013.xsl";
	private static final String ALTO_TO_PAGE_XSLT = "xslt/AltoToPage2013.xsl";
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
	
	public static Unmarshaller createUnmarshaller() throws JAXBException {
		JAXBContext jc = createPageJAXBContext();

		Unmarshaller u = jc.createUnmarshaller();
		u.setProperty("com.sun.xml.internal.bind.ObjectFactory", new TrpObjectFactory());
		u.setListener(new TrpPageUnmarshalListener());
		return u;
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
		
		return pageData;
	}
	
	public static PcGtsType unmarshal(InputStream is) throws JAXBException {
		Unmarshaller u = createUnmarshaller();

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

	public static PcGtsType createEmptyPcGtsType(final URL imgUrl, Dimension dim) throws IOException {
		final String prot = imgUrl.getProtocol();
		PcGtsType pcGts;
		if (prot.startsWith("http")) {
			//fimagestore file
			FimgStoreImgMd md = FimgStoreReadConnection.getImgMd(imgUrl);
			pcGts = createEmptyPcGtsTypeForRemoteImg(imgUrl, md);
		} else {
			//try to deal with it as local file
			final File imgFile = FileUtils.toFile(imgUrl);
			pcGts = createEmptyPcGtsType(imgFile, dim);
		}
		return pcGts;
	}

	private static PcGtsType createEmptyPcGtsTypeForRemoteImg(final URL url, FimgStoreImgMd imgMd) throws IOException {
		int xDim = new Double(imgMd.getXResolution()).intValue();
		int yDim = new Double(imgMd.getYResolution()).intValue();
		return createEmptyPcGtsType(imgMd.getFileName(), xDim, yDim);
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
	
	public static PcGtsType createEmptyPcGtsType(final String imgFileName, final int xDim,
			final int yDim) {
		// create md
		MetadataType md = new MetadataType();
		md.setCreator("TRP");
		XMLGregorianCalendar xmlCal = JaxbUtils.getXmlCalendar(new Date());
		md.setCreated(xmlCal);
		md.setLastChange(xmlCal);

		//create TRP (!) pageType
		TrpPageType pt = new TrpPageType();
		pt.setImageFilename(imgFileName);
		pt.setImageHeight(yDim);
		pt.setImageWidth(xDim);

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
				nrOfWordsInRegions += r.getTextEquiv().getUnicode().split(" ").length;
			}
			List<TextLineType> lines = r.getTextLine();
			nrOfLines += lines.size();
			for(TextLineType l : lines) {
				if(l.getTextEquiv() != null && l.getTextEquiv().getUnicode() != null 
						&& !l.getTextEquiv().getUnicode().trim().isEmpty()) {
					nrOfTranscribedLines += 1;
					//TODO use tokenizer here
					nrOfWordsInLines += l.getTextEquiv().getUnicode().split(" ").length;
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
	
	public static void main(String[] args) {
		final String path = "/mnt/dea_scratch/TRP/Bentham_box_002/page/002_080_001.xml";
		try {
			logger.info(""+PageXmlUtils.isValid(new File(path)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
