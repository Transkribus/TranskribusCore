package eu.transkribus.core.model.builder.tei;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.DocExporter;
import eu.transkribus.core.io.ExportFilePatternUtils;
import eu.transkribus.core.model.beans.EdFeature;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.customtags.AbbrevTag;
import eu.transkribus.core.model.beans.customtags.BlackeningTag;
import eu.transkribus.core.model.beans.customtags.CommentTag;
import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.GapTag;
import eu.transkribus.core.model.beans.customtags.OrganizationTag;
import eu.transkribus.core.model.beans.customtags.PersonTag;
import eu.transkribus.core.model.beans.customtags.PlaceTag;
import eu.transkribus.core.model.beans.customtags.SpeechTag;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.RegionTypeUtil;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpElementReadingOrderComparator;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpShapeTypeUtils;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpWordType;
import eu.transkribus.core.model.builder.CommonExportPars;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.PointStrUtils;
import eu.transkribus.core.util.SebisStringBuilder;

public class TrpTeiStringBuilder extends ATeiBuilder {
	private final static Logger logger = LoggerFactory.getLogger(TrpTeiStringBuilder.class);

	SebisStringBuilder sbTotal = new SebisStringBuilder();
	
//	String currentPbXmlId=null;
	
//	boolean writeTextOnWordLevel=true;
//	boolean doBlackening=false;
	
//	public TrpTeiStringBuilder(TrpDoc doc, TeiExportMode mode, boolean writeTextOnWordLevel, boolean doBlackening, IProgressMonitor monitor, Set<Integer> pageIndices, Set<String> selectedTags) {
//		super(doc, mode, monitor, pageIndices, selectedTags);
//		this.pars.writeTextOnWordLevel = writeTextOnWordLevel;
//		this.pars.doBlackening = doBlackening;
//	}
	
	public TrpTeiStringBuilder(TrpDoc doc, CommonExportPars commonPars, TeiExportPars pars, IProgressMonitor monitor) {
		super(doc, commonPars, pars, monitor);
	}

//	public boolean isWriteTextOnWordLevel() {
//		return pars.writeTextOnWordLevel;
//	}
//
//	public void setWriteTextOnWordLevel(boolean writeTextOnWordLevel) {
//		this.pars.writeTextOnWordLevel = writeTextOnWordLevel;
//	}

	@Override public String getTeiAsString() {
		return sbTotal.toString();
	}

	@Override protected void startDocument() throws Exception {
		sbTotal = new SebisStringBuilder();
		
		sbTotal.addLine("<?xml version='1.0' encoding='UTF-8'?>");
		sbTotal.addLine("<TEI xmlns='http://www.tei-c.org/ns/1.0'>");
	}

	@Override protected void endDocument() {
		sbTotal.addLine("</TEI>");
	}

	@Override protected void setHeader(TrpDocMetadata md) {
		SebisStringBuilder sbHeader = new SebisStringBuilder();
		
		sbHeader.incIndent();
		sbHeader.addLine("<teiHeader>");
		sbHeader.incIndent();
		sbHeader.addLine("<fileDesc>");
		
		sbHeader.incIndent();
		
		sbHeader.addLine("<titleStmt>");
		sbHeader.addLineWIndent("<title type='main'>"+md.getTitle()+"</title>");
		sbHeader.addLine("</titleStmt>");
		
		sbHeader.addLine("<publicationStmt>");
		sbHeader.addLineWIndent("<publisher>"+DEFAULT_PUBLISHER+"</publisher>");
		sbHeader.addLine("</publicationStmt>");
		
		sbHeader.addLine("<sourceDesc>");
		sbHeader.addLineWIndent("<bibl><publisher>"+"TRP document creator: " + md.getUploader()+"</publisher></bibl>");
		sbHeader.addLine("</sourceDesc>");		

		sbHeader.decIndent();
		
		sbHeader.addLine("</fileDesc>");
		sbHeader.decIndent();
				
		writeEditorialDeclaration(sbHeader);
		
		sbHeader.addLine("</teiHeader>");
		sbHeader.decIndent();
		
		sbTotal.sb.append(sbHeader);
	}
	
	void writeEditorialDeclaration(SebisStringBuilder sb) {		
		if (trpDoc.getEdDeclList()==null || trpDoc.getEdDeclList().isEmpty())
			return;
		
		sb.incIndent();
		sb.addLine("<encodingDesc>");
		
		sb.incIndent();
		sb.addLine("<editorialDecl>");
		
		sb.incIndent();
		for (EdFeature f : trpDoc.getEdDeclList()) {
			if (f.getSelectedOption()!=null) {
				String str = f.getTitle()+" ("+f.getDescription()+"): "+f.getSelectedOption().getText();
				String escapedstr = StringEscapeUtils.escapeXml(str);
				sb.addLine("<p>"+escapedstr+"</p>");
			}
		}
		sb.decIndent();
		
		sb.addLine("</editorialDecl>");
		sb.decIndent();
		
		sb.addLine("</encodingDesc>");
		sb.decIndent();
	}
	
	String getPageGraphicsUrl(TrpPage p, PcGtsType pc) {
		String graphicUrl = ExportFilePatternUtils.buildBaseFileName(commonPars.getFileNamePattern(), p) + "." + FilenameUtils.getExtension(p.getImgFileName());
		logger.trace("graphicUrl = "+graphicUrl);
		
		if (false) {
			if(trpDoc.getMd().getLocalFolder() == null) {
				//imagestore url
				graphicUrl = p.getUrl().toString();
				graphicUrl = CoreUtils.removeFileTypeFromUrl(graphicUrl);
			} else {
				//img file name. http://www.tei-c.org/release/doc/tei-p5-doc/en/html/ref-graphic.html
				graphicUrl = pc.getPage().getImageFilename();
			}
		}
		return graphicUrl;
	}
	
	void openFacsimileElement(SebisStringBuilder sb, TrpPage p, PcGtsType pc) {
		String graphicUrl = getPageGraphicsUrl(p, pc);
		
		final String facsId = FACS_ID_PREFIX + p.getPageNr();
		final int width = pc.getPage().getImageWidth();
		final int height = pc.getPage().getImageHeight();		
		
		sb.incIndent();
		sb.addLine("<facsimile xml:id='"+facsId+"'>");
		
		sb.incIndent();
		
		String surfaceStr = "<surface ulx='0' uly='0' lrx='"+width+"' lry='"+height+"'";
		if (pars.pbImageNameAsXmlId && !StringUtils.isEmpty(graphicUrl)) {
			surfaceStr += " corresp='"+graphicUrl+"'";
		}
		surfaceStr += ">";
		
		sb.addLine(surfaceStr);
		
		sb.addLineWIndent("<graphic url='"+graphicUrl+"' width='"+width+"px' height='"+height+"px'/>");
		
		// add printspace zone if its there:
		if (pc.getPage().getPrintSpace() != null) {
//			sb.addLineWIndent("<zone points='"+pc.getPage().getPrintSpace().getCoords().getPoints()+"' rendition='printspace'/>");
			sb.addLineWIndent("<zone points='"+getValidZonePointsString(pc.getPage().getPrintSpace().getCoords().getPoints())+"' rendition='printspace'/>");
		}
	}
	
	void closeElement(SebisStringBuilder sb, String element) {
		sb.addLine("</"+element+">");
		sb.decIndent();
	}
	
	void closeFacsimilieElement(SebisStringBuilder sb) {
		closeElement(sb, "surface");
		closeElement(sb, "facsimile");
	}
	
	String getValidZonePointsString(String coordsStr) {
		if(StringUtils.isEmpty(coordsStr)) {
			logger.error("A coords String is empty!");
			return "0,0 0,0 0,0";
		}
		List<Point> pts = PointStrUtils.parsePoints2(coordsStr);
		
		// a zone must have at least three coordinates, so fill up with zeros if they are not there
		if (pts.size()==0) {
			coordsStr = "0,0 0,0";
		}
		else if (pts.size()==1) {
			coordsStr += " 0,0 0,0";
		}
		else if (pts.size()==2) {
			coordsStr += " 0,0";
		}
		
		return coordsStr;
	}
	
	void writeZoneForShape(SebisStringBuilder sb, ITrpShapeType s, String facsId, boolean close) {
		String id = facsId+"_"+s.getId();
		
		String zoneStr;
		if (pars.boundingBoxCoords) {
			Rectangle bb = s.getBoundingBox();
			zoneStr = "<zone ulx='"+(int)bb.getX()+"' uly='"+(int)bb.getY()+"' lrx='"+(int)bb.getMaxX()+"' lry='"+(int)bb.getMaxY()+"'";
		}
		else {
			if(StringUtils.isEmpty(s.getCoordinates())) {
				logger.error("Coordinates are empty on shape with ID = " + s.getId());
				zoneStr = "<zone points=''";
			} else {
				zoneStr = "<zone points='"+getValidZonePointsString(s.getCoordinates())+"'";
			}
		}
		
		// write type of shape:
		String type = RegionTypeUtil.getRegionType(s);
		
//		if (s instanceof TrpTextRegionType) {
//			type = "textregion";
//		}
//		else if (s instanceof TrpTextLineType) {
//			type = "line";
//		}
//		else if (s instanceof TrpWordType) {
//			type = "word";
//		}
		
		if (!type.isEmpty()) {
			zoneStr += " rendition='"+type+"'";
		}
		
		// write struct type:
		String struct = s.getStructure();
		if (struct!=null && !struct.isEmpty()) {
			zoneStr += " subtype='"+struct+"'";
		}
		
		zoneStr += " xml:id='"+id+"'";
		
		zoneStr += close ? "/>" : ">";
		
		sb.incIndent();
		sb.addLine(zoneStr);
		if (close)
			sb.decIndent();
	}
			
	void writeZonesForTextRegion(SebisStringBuilder sb, TextRegionType r, int pageNr) {
		String facsId = FACS_ID_PREFIX+pageNr;

		if (pars.regionZones) {
			writeZoneForShape(sb, (TrpTextRegionType) r, facsId, !pars.lineZones && !pars.wordZones);
		}
		
		if (!pars.lineZones && !pars.wordZones)
			return;
		
		for (TextLineType tl : r.getTextLine()) {
			TrpTextLineType ttl = (TrpTextLineType) tl;

			if (pars.lineZones) {
				writeZoneForShape(sb, ttl, facsId, !pars.wordZones);
			}
			if (pars.wordZones) {
				for (WordType w : ttl.getWord()) {
					TrpWordType tw = (TrpWordType) w;
					writeZoneForShape(sb, tw, facsId, true);
				}
				
				if (pars.lineZones) {
					closeElement(sb, "zone");
				}
			}
		}
	
		if (pars.regionZones) {
			closeElement(sb, "zone");
		}
		
	}
	
	void writePageBreak(SebisStringBuilder sb, TrpPage p, PcGtsType pc) {
		String pbStr = "<pb ";
		
		if (pars.hasZones()) {
			String facsId = "#"+FACS_ID_PREFIX+p.getPageNr();
			String facsStr = "facs='"+facsId+"'";
			
			pbStr += facsStr+" ";
		}
		
		String graphicUrl = getPageGraphicsUrl(p, pc);
		if (pars.pbImageNameAsXmlId && !StringUtils.isEmpty(graphicUrl)) {
			String xmlIdStr = "xml:id='"+graphicUrl+"'";
			pbStr += xmlIdStr+" ";
		}
		
		pbStr += "n='"+p.getPageNr()+"'/>";
		
		sb.addLineWIndent(pbStr);
	}
	
	void writeTextRegion(SebisStringBuilder sb, TextRegionType region, String facsId) {
		String pStr = "<p";
		if (pars.hasZones()) {
			String id = "#"+facsId+"_"+region.getId();	
			pStr += " facs='"+id+"'";
		}
		pStr += ">";
		
		sb.incIndent();
		sb.addLine(pStr);
		if (pars.isLineTagType()) {
			sb.incIndent();
			sb.addLine("<lg>");
		}
	}
		
	String createTagStart(CustomTag t) {
		String ts="";
		if (t instanceof TextStyleTag) {
			// TODO!!
			TextStyleTag tst = (TextStyleTag) t;
			ts = "<hi rend='"+tst.getAttributeCssStr()+"'>";
		}
		else if (t instanceof AbbrevTag) {
			AbbrevTag at = (AbbrevTag) t;
			ts = "<choice><expan>"+StringEscapeUtils.escapeXml(at.getExpansion())+"</expan><abbr>";
		}
		else if (t instanceof PersonTag) {
			PersonTag pt = (PersonTag) t;
			ts = "<persName>";
			
			if (!StringUtils.isEmpty(pt.getFirstname())) {
				ts +="<forename>"+StringEscapeUtils.escapeXml(pt.getFirstname())+"</forename>";
			}
			if (!StringUtils.isEmpty(pt.getLastname())) {
				ts +="<surname>"+StringEscapeUtils.escapeXml(pt.getLastname())+"</surname>";
			}
			if (!StringUtils.isEmpty(pt.getDateOfBirth())) {
				ts +="<birth>"+StringEscapeUtils.escapeXml(pt.getDateOfBirth())+"</birth>";
			}
			if (!StringUtils.isEmpty(pt.getDateOfBirth())) {
				ts +="<death>"+StringEscapeUtils.escapeXml(pt.getDateOfDeath())+"</death>";
			}
			if (!StringUtils.isEmpty(pt.getNotice())) {
				ts +="<notice>"+StringEscapeUtils.escapeXml(pt.getNotice())+"</notice>";
			}
		}
		else if (t instanceof PlaceTag) {
			PlaceTag pt = (PlaceTag) t;
			ts = "<placeName>";
			if (!StringUtils.isEmpty(pt.getCountry())) {
				ts += "<country>"+StringEscapeUtils.escapeXml(pt.getCountry())+"</country>";
			}
		}
		else if (t instanceof OrganizationTag) {
			OrganizationTag ot = (OrganizationTag) t;
			ts = "<orgName>";
		}
		else if (t instanceof SpeechTag) {
			SpeechTag st = (SpeechTag) t;
			ts = "<sp>";
			if (!StringUtils.isEmpty(st.getSpeaker())) {
				ts += "<speaker>"+StringEscapeUtils.escapeXml(st.getSpeaker())+"</speaker>";
			}
		}
		else if (t instanceof GapTag) {
			ts = "<gap />";
		}
		//do nothing because comment tag is added at the end of the tag entry as note in the createTagEnd method
		else if (t instanceof CommentTag){
			ts = "";
		}
		else { // general tag
			ts = "<"+t.getTagName();
						
			for (String an : t.getAttributeNames()) {
				if (CustomTag.isOffsetOrLengthOrContinuedProperty(an))
					continue;
				
				Object v = t.getAttributeValue(an);
				if (v != null) {
					ts+=" "+StringEscapeUtils.escapeXml(an)+"='"+StringEscapeUtils.escapeXml(v.toString())+"'";
				}
			}
			ts+=">";
		}
		
		return ts;
	}
	
	String createTagEnd(CustomTag t) {
		String te="";
		if (t instanceof TextStyleTag) {
			te = "</hi>";
		}
		else if (t instanceof AbbrevTag) {
			te = "</abbr></choice>";
		}
		else if (t instanceof PersonTag) {
			te = "</persName>";
		}
		else if  (t instanceof PlaceTag) {
			te = "</placeName>";
		}
		else if (t instanceof OrganizationTag) {
			 te = "</orgName>";
		}
		else if (t instanceof SpeechTag) {
			te = "</sp>";
		}
		else if (t instanceof GapTag) {
			te = "";
		}
		//no comment element in TEI - we use note instead
		else if (t instanceof CommentTag){
			CommentTag ct = (CommentTag) t;
			te = "<note>";
			if (!StringUtils.isEmpty(ct.getComment())) {
				te += StringEscapeUtils.escapeXml(ct.getComment());
			}
			te += "</note>";
		}
		else {
			te = "</"+t.getTagName()+">";
		}
		
		return te;
	}
	
	void correctTagOffsetsForTagInsertion(int insertOffset, int insertLength, List<CustomTag> tags) {
		for (CustomTag t : tags) {
			// inserted text before tag -> extend offset!
			if (insertOffset <= t.getOffset()) {
				t.setOffset(t.getOffset() + insertLength);
			}
			// inserted text inside tag -> extend length!
			else if (insertOffset > t.getOffset() && insertOffset < t.getEnd()) {
				t.setLength(t.getLength() + insertLength);
			}
			// else: do not bother!!
		}
	}
	
	String insertTag(String text, CustomTag t, List<CustomTag> tags) {
		logger.trace("custom tag: "+t);
		logger.trace("text: "+text);
		
		if (t.getOffset() < 0 || t.getEnd() > text.length()) {
			logger.warn("Could not insert tag: "+t+" - index out of bounds! Skipping...");
			return text;
		}
		
		// insert tag start:
		String ts = createTagStart(t);
		
		StringBuilder sb = new StringBuilder(text);
		sb.insert(t.getOffset(), ts).toString();
		
		correctTagOffsetsForTagInsertion(t.getOffset(), ts.length(), tags);

		// insert tag end:
		String te = createTagEnd(t);
		
		sb.insert(t.getEnd(), te).toString();
		correctTagOffsetsForTagInsertion(t.getEnd(), te.length(), tags);		
		
		return sb.toString();
	}
	
	void correctTagOffsetsForEscapedString(int indexOfCharacter, String escapedReplacement, List<CustomTag> tags) {
		if (escapedReplacement.length() > 1) {
			int additonalLength = escapedReplacement.length()-1;
			
			for (CustomTag t : tags) {
				// character before tag -> move offset to the right
				if (indexOfCharacter < t.getOffset()) {
					t.setOffset(t.getOffset() + additonalLength);
				}
				// inserted text inside tag -> extend length!
				else if (indexOfCharacter >= t.getOffset() && indexOfCharacter < t.getEnd()) {
					t.setLength(t.getLength() + additonalLength);
				}
				// else: do not bother!!
			}
		}
	}
	
	/**
	 * Escapes the given shape text for XML and adjusts offset/length values of given tags accordingly
	 */
	String escapeShapeText(String text, List<CustomTag> tags) {
		String escapedText="";
		for (int i=0; i<text.length(); ++i) {
			char c = text.charAt(i);
			String escaped = StringEscapeUtils.escapeXml(""+c);
			int indexOfChar = escapedText.length();
			escapedText += escaped;
			
			correctTagOffsetsForEscapedString(indexOfChar, escaped, tags);
			
//			// adapt tag indices for the additional characters that may have been inserted due to escape
//			if (escaped.length() > 1) {
//				logger.trace("escaped = "+escaped+" length = "+escaped.length());
//				correctTagOffsetsForTagInsertion(insertOffset, escaped.length(), tags);
//			}
		}
		return escapedText;
	}
	
	String getTaggedContent(ITrpShapeType shape) {
		CustomTagList cl = shape.getCustomTagList();
		List<CustomTag> ctList = new ArrayList<CustomTag>();
//		for (CustomTag t : cl.getTags()) {
		for (CustomTag t : cl.getIndexedTags()) {
			ctList.add(t.copy());
		}
		Collections.sort(ctList);
		
		String text = shape.getUnicodeText();
		
		// escape the shape text here - later on the tag elements would be escaped too
		String escapedText = escapeShapeText(text, ctList);
		logger.trace("ShapeText = "+text+" escaped: "+escapedText);
		
		for (CustomTag t : ctList) {
			if ( commonPars.isTagSelected(t.getTagName()) 
				|| (commonPars.isDoBlackening() && t.getTagName().equals(BlackeningTag.TAG_NAME)) || t.getTagName().equals(TextStyleTag.TAG_NAME) ) {
				escapedText = insertTag(escapedText, t, ctList);
			}
		}
		logger.trace("escaped text after tag insertion: "+escapedText);
		
		// replace blackened text:
		if (commonPars.isDoBlackening()) {
			escapedText = hideBlackenedText(escapedText);
		}
		
		return escapedText;
	}
	
	public static String hideBlackenedText(String text) {
		String BLACKENING_REPLACEMENT = "<gap reason='blackening'/>";
		return text.replaceAll("<"+BlackeningTag.TAG_NAME+">.*?</"+BlackeningTag.TAG_NAME+">", BLACKENING_REPLACEMENT);
		
//		Pattern regex = Pattern.compile("<"+BlackeningTag.TAG_NAME+">.*?</"+BlackeningTag.TAG_NAME+">", Pattern.DOTALL);
//		Matcher matcher = regex.matcher(text);
//		while (matcher.find()) {
//			String textToBlacken = matcher.group(1);
//			
//		}
		
	}
	
	String getLineOrWordStart(ITrpShapeType shape, String facsId) {
		
		boolean isLine = shape instanceof TrpTextLineType;
		String el = isLine ? "l" : "w";
		String id = "#"+facsId+"_"+shape.getId();
		String xml_id = shape.getId();
		
		if (!isLine || pars.isLineTagType()) {
			String lStr = "<"+el;
			if (pars.lineXmlIds) {
				lStr += " xml:id='"+xml_id+"'";
			}
			if (pars.lineZones && isLine) {
				lStr += " facs='"+id+"'";
			} else if (pars.wordZones && !isLine) {
				lStr += " facs='"+id+"'";
			}
			lStr += ">";
			return lStr;
		} 
		else { // if line and linebreak mode != LINE_TAG
			if (pars.lineBreakAtBeginningOfLine) {
				if (! (shape instanceof TrpTextLineType) ) {
					logger.error("Unexpected error: not a line given!");
					return "";
				}
				
				return getLbElement((TrpTextLineType) shape, id, xml_id);
			} else {
				return "";
			}
		}
	}
	
	String getLineOrWordEnd(ITrpShapeType shape, String facsId) {
		boolean isLine = shape instanceof TrpTextLineType;
		String el = isLine ? "l" : "w";
		String id = "#"+facsId+"_"+shape.getId();
		String xml_id = shape.getId();
		
		if (!isLine || pars.isLineTagType()) {
			return "</"+el+">";
		} else {
			if (pars.lineBreakAtBeginningOfLine) {
				return "";
			} else {
				if (! (shape instanceof TrpTextLineType) ) {
					logger.error("Unexpected error: not a line given!");
					return "";
				}
				
				return getLbElement((TrpTextLineType) shape, id, xml_id);
			}
		}
	}
	
	String getLbElement(TrpTextLineType line, String id, String xml_id) {
		String nStr = "N"+StringUtils.leftPad(""+(line.getIndex()+1), 3, '0');
		
		String lbStr = "<lb";
		if (pars.lineXmlIds) {
			lbStr += " xml:id='"+xml_id+"'";
		}
		lbStr += " facs='"+id+"'";
		lbStr += " n='"+nStr+"'/>";
		
//		String lbStr = "<lb facs='"+id+"' n='"+nStr+"'/>";
		return lbStr;
	}
	
	void writeLineOrWord(SebisStringBuilder sb, ITrpShapeType shape, String facsId) {
		if (!(shape instanceof TrpWordType) && !(shape instanceof TrpTextLineType))
			return;
		boolean isLine = shape instanceof TrpTextLineType;
		
		String lStr = getLineOrWordStart(shape, facsId);
				
		String content = getTaggedContent(shape);
		
		/*
		 * do not use escape content here - then we have &lt;Tagname&gt; for the tags in the text!!
		 * now is done a bit earlier before tags are added (in getTaggedContent)
		 */
//		final String escapedContent = StringEscapeUtils.escapeXml(content);
//		logger.debug("CONTENT = "+content+" escaped: "+escapedContent);

		lStr+=content+getLineOrWordEnd(shape, facsId);
		
//		if (isLine)
			sb.incIndent();
		
		sb.addLine(lStr);
		
//		if (isLine)
			sb.decIndent();
	}
		
//	void writeLine(SebisStringBuilder sb, TrpTextLineType line, String facsId) {
//		String id = "#"+facsId+"_"+line.getId();
//		sb.incIndent();
//		
//		String lStr = "<l";
//		if (mode.val > TeiExportMode.ZONE_PER_PAR.val) {
//			lStr += " facs='"+id+"'";
//		}
//		lStr += ">";
//		
//		String content = getTaggedContent(line);
//		String escapedContent = ESCAPE_XML_CONTENT.translate(content);
//		
//		logger.debug("CONTENT = "+content+" escaped: "+escapedContent);
//		lStr+=escapedContent+"</l>";
//		
//		sb.addLine(lStr);
//		sb.decIndent();
//	}
//	
//	void writeWord(SebisStringBuilder sb, TrpWordType word, String facsId) {
//		String id = "#"+facsId+"_"+word.getId();
//		sb.incIndent();
//		
//		String wStr = "<w";
//		if (mode.val > TeiExportMode.ZONE_PER_PAR.val) {
//			wStr += " facs='"+id+"'";
//		}
//		wStr += ">";
//		
//		String content = getTaggedContent(word);
//		String escapedContent = ESCAPE_XML_CONTENT.translate(content);
//		
//		logger.debug("CONTENT = "+content+" escaped: "+escapedContent);
//		wStr+=escapedContent+"</w>";
//		
//		sb.append(wStr);
//		sb.decIndent();
//	}
	
	void closeTextRegion(SebisStringBuilder sb) {
		if (pars.isLineTagType()) {
			closeElement(sb, "lg");
		}
		
		closeElement(sb, "p");
	}
	
	void writeTextForTextRegion(SebisStringBuilder sb, TextRegionType r, int pageNr) {
		String facsId = FACS_ID_PREFIX+pageNr;
		
		if (r.getTextLine().isEmpty()) {
			logger.warn("skipping empty region: "+r.getId());
			return;
		}
		
		writeTextRegion(sb, r, facsId);
		
		for (TextLineType tl : r.getTextLine()) {
			TrpTextLineType ttl = (TrpTextLineType) tl;
			
			if (!commonPars.isWriteTextOnWordLevel()) {
				writeLineOrWord(sb, ttl, facsId);
			} else {
				String lStart = getLineOrWordStart(ttl, facsId);
				sb.incIndent();
				sb.addLine(lStart);
				
				// TODO: write text for words???
				for (WordType w : ttl.getWord()) {
					writeLineOrWord(sb, (TrpWordType) w, facsId);
				}
				
				String lEnd = getLineOrWordEnd(ttl, facsId);
				sb.addLine(lEnd);
//				sb.append("\n");
				sb.decIndent();
			}

		}
		
		closeTextRegion(sb);
	}	

	@Override protected void setContent(List<TrpPage> pages) throws JAXBException, InterruptedException {		
		SebisStringBuilder sbFacsimile = new SebisStringBuilder();
		
		SebisStringBuilder sbText = new SebisStringBuilder();
		sbText.incIndent();
		sbText.addLine("<text>");
		sbText.incIndent();
		sbText.addLine("<body>");
//		sbText.incIndent();
		
//		text = tei.createElementNS(TEI_NS, "text");
//		body = tei.createElementNS(TEI_NS, "body");
		
		int totalPages = pageIndices==null ? pages.size() : pageIndices.size();
		if (monitor != null) {
			monitor.beginTask("Creating TEI", totalPages);
		}
		
		int c=0;
		for (int i=0; i<pages.size(); ++i) {
			if (pageIndices!=null && !pageIndices.contains(i))
				continue;
			
			if (monitor != null) {
				if (monitor.isCanceled()){
					throw new InterruptedException("Export was canceled by user");
					//break;
				}
				
				monitor.subTask("Processing page " + (c+1));
			}
			
			TrpPage p = pages.get(i);
			logger.debug("1Processing page " + p.getPageNr() + ": " + p.getUrl() + " - XML=" + p.getCurrentTranscript().getUrl());
			//check buffer for transcript or unmarshal the page XML
			PcGtsType pc = this.getPcGtsTypeForPage(p);
		
			if (pars.hasZones()) {
				// create a facsimile element for each page that are appended to the root element of the TEI after header
				openFacsimileElement(sbFacsimile, p, pc);
			}
			
			// create page-break element for each page as child of body element:
			writePageBreak(sbText, p, pc);
//			
//			// append all text-regions / lines / words to the xml:
			List<TrpRegionType> regions = pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
//			Collections.sort(regions, new TrpElementReadingOrderComparator<RegionType>(true));
			TrpShapeTypeUtils.sortShapesByReadingOrderOrCoordinates(regions);
			for(TrpRegionType r : regions){
//				System.out.println(r.getClass());
				if(r instanceof TextRegionType) {
					if (pars.hasZones()) {
						writeZonesForTextRegion(sbFacsimile, (TrpTextRegionType) r, p.getPageNr());
					}
					writeTextForTextRegion(sbText, (TrpTextRegionType) r, p.getPageNr());
				}
				else { // write other regions
					if (pars.hasZones()) {
						String facsId = FACS_ID_PREFIX+p.getPageNr();
						writeZoneForShape(sbFacsimile, r, facsId, true);
					}
				}
			}

			if (pars.hasZones()) {
				closeFacsimilieElement(sbFacsimile);
			}
			
			++c;
			if (monitor != null) {
				monitor.worked(c);
			}
		}
		
//		text.appendChild(body);
//		root.appendChild(text);
		
//		sbText.decIndent();
		sbText.addLine("</body>");
		sbText.decIndent();
		sbText.addLine("</text>");
		sbText.decIndent();
		
		sbTotal.sb.append(sbFacsimile.toString());
		sbTotal.sb.append(sbText.toString());
	}
	
	public static void main(String[] args) {
		String text = "<blackening>asdflajsdfk</blackening> laskdj kajsdl klajsdf ddkd <blackening>asdf   </blackening> hello! ";
		System.out.println(hideBlackenedText(text));
		
		
	}
}
