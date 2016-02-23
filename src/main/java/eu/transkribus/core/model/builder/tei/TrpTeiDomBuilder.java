package eu.transkribus.core.model.builder.tei;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent.TextTypeSimpleType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpElementCoordinatesComparator;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.util.XmlUtils;

@Deprecated
/**
 * Outdated class - live with it or go fuck yourself!
 */
public class TrpTeiDomBuilder extends ATeiBuilder {
	private static final Logger logger = LoggerFactory.getLogger(TrpTeiDomBuilder.class);
	
	Element root, header, text, body, currParentSurface;
	
	protected Document tei;
	
	public TrpTeiDomBuilder(TrpDoc doc, TeiExportMode mode, IProgressMonitor monitor, Set<Integer> pageIndices, Set<String> selectedTags) {
		super(doc, mode, monitor, pageIndices, selectedTags);
	}
	
	protected static Document createInitialDoc() throws ParserConfigurationException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.newDocument();
	}
	
	@Override protected void startDocument() throws Exception {
		tei = createInitialDoc();
		
//		final Node pi = tei.createProcessingInstruction(StreamResult.PI_DISABLE_OUTPUT_ESCAPING,"");
//		tei.appendChild(pi);
		
		root = tei.createElementNS(TEI_NS, "TEI");
		tei.appendChild(root);
	}
	
	@Override protected void endDocument() {
	}	

	@Override protected void setHeader(TrpDocMetadata md) {
		/*
		<fileDesc>
         <titleStmt>
            <title/>
         </titleStmt>
         <publicationStmt>
            <publisher>asd</publisher>
         </publicationStmt>
         <sourceDesc>
            <bibl><publisher>TRP</publisher></bibl>
         </sourceDesc>
      </fileDesc>
		 */
		
		header = tei.createElementNS(TEI_NS, "teiHeader");
		
		Element fileDesc = tei.createElementNS(TEI_NS, "fileDesc");
		
		Element titleStmt = tei.createElementNS(TEI_NS, "titleStmt");
		Element title = tei.createElementNS(TEI_NS, "title");
		title.setAttribute("type", "main");
		title.setTextContent(md.getTitle());
		titleStmt.appendChild(title);
		
		fileDesc.appendChild(titleStmt);
		
		Element publicationStmt = tei.createElementNS(TEI_NS, "publicationStmt");
		Element publisher = tei.createElementNS(TEI_NS, "publisher");
		publisher.setTextContent(DEFAULT_PUBLISHER);
		publicationStmt.appendChild(publisher);
		
		fileDesc.appendChild(publicationStmt);

		Element sourceDesc = tei.createElementNS(TEI_NS, "sourceDesc");
		Element bibl = tei.createElementNS(TEI_NS, "bibl");
		Element sourcePub = tei.createElementNS(TEI_NS, "publisher");
		sourcePub.setTextContent("TRP document creator: " + md.getUploader());
		bibl.appendChild(sourcePub);
		sourceDesc.appendChild(bibl);
		
		fileDesc.appendChild(sourceDesc);
		
		header.appendChild(fileDesc);
		
		root.appendChild(header);
		
//		Element author = header.getOwnerDocument().createElementNS(TEI_NS, "author");
//		author.setTextContent(md.getAuthor());
//		header.appendChild(author);
	}
	
	@Override protected void setContent(List<TrpPage> pages) throws JAXBException {
		text = tei.createElementNS(TEI_NS, "text");
		body = tei.createElementNS(TEI_NS, "body");
		
		int totalPages = pageIndices==null ? pages.size() : pageIndices.size();
		if (monitor != null) {
			monitor.beginTask("Creating TEI", totalPages);
		}
		
		int c=0;
		for (int i=0; i<pages.size(); ++i) {
			if (pageIndices!=null && !pageIndices.contains(i))
				continue;
			
			if (monitor != null) {
				if (monitor.isCanceled())
					break;
				
				monitor.subTask("Processing page " + (c+1));
			}
			
			TrpPage p = pages.get(i);
			
			logger.debug("Processing page " + p.getPageNr() + ": " + p.getUrl() + " - XML=" + p.getCurrentTranscript().getUrl());
			//check buffer for transcript or unmarshal the page XML
			PcGtsType pc = this.getPcGtsTypeForPage(p);
			final String graphicUrl;
			if(trpDoc.getMd().getLocalFolder() == null){
				//imagestore url
				graphicUrl = p.getUrl().toString();
			} else {
				//img file name. http://www.tei-c.org/release/doc/tei-p5-doc/en/html/ref-graphic.html
				graphicUrl = pc.getPage().getImageFilename();
			}
			final String facsId = FACS_ID_PREFIX + p.getPageNr();
			final int width = pc.getPage().getImageHeight();
			final int height = pc.getPage().getImageWidth();
			
			//create a facsimile element for each page that are appended to the root element of the TEI after header
			Element facsimile = tei.createElementNS(TEI_NS, "facsimile");
			facsimile.setAttribute("xml:id", facsId);
			Element graphic =  tei.createElementNS(TEI_NS, "graphic");
			graphic.setAttribute("url", graphicUrl);
			facsimile.appendChild(graphic);
			
			//whole page surface
			Element surface = tei.createElementNS(TEI_NS, "surface");
			surface.setAttribute("ulx", ""+0);
			surface.setAttribute("uly", ""+0);
			surface.setAttribute("lrx", ""+width);
			surface.setAttribute("lry", ""+height);
			currParentSurface = surface; // append all coming zones to surface
			
			// create printspace zone if there
			//This is where all content regions are appended. Reference to this is thus needed
			if(pc.getPage().getPrintSpace() != null) {
				//printspace zone is parentToRegions. But there might be none! 
				Element printspace = tei.createElementNS(TEI_NS, "zone");
				printspace.setAttribute("points", pc.getPage().getPrintSpace().getCoords().getPoints());
				printspace.setAttribute("type", "printspace");
				surface.appendChild(printspace);
//				currParentSurface = printspace;
			} 
//			else {
//				currParentSurface = surface;
//			}
			
			facsimile.appendChild(surface);
			tei.getDocumentElement().appendChild(facsimile);
			
			// create page-break element for each page as child of body element:
			Element pb = tei.createElementNS(TEI_NS, "pb");
			pb.setAttribute("n", ""+p.getPageNr());
			pb.setAttribute("facs", "#" + facsId);
			body.appendChild(pb);
			
			// append all text-regions / lines / words to the xml:
			List<TrpRegionType> regions = pc.getPage().getTextRegionOrImageRegionOrLineDrawingRegion();
			Collections.sort(regions, new TrpElementCoordinatesComparator<RegionType>());
			for(TrpRegionType r : regions){
//				System.out.println(r.getClass());
				if(r instanceof TextRegionType){
					setTextRegion((TextRegionType) r, p.getPageNr());
				}
			}
			
			++c;
			if (monitor != null) {
				monitor.worked(c);
			}
		}
		
		text.appendChild(body);
		root.appendChild(text);
	}
	
	/** Method transforms textRegion content to TEI content
	 * <br/><br/>
	 * References on the text-Element and printspace-zone-Element of the TEI are only needed for 
	 * appending children.<br/>
	 * facsId for building a unique zone-Id
	 * 
	 * @param r
	 * @param text
	 * @param printspace
	 * @param facsId
	 */
	protected void setTextRegion(TextRegionType r, int pageNr) {
		String facsId = FACS_ID_PREFIX+pageNr;
		TextTypeSimpleType type = r.getType();
		if(type == null){
			logger.error("type is null");
			type = TextTypeSimpleType.OTHER;
		}
		//text content element
		Element content;
		//facsimile/surface/zone[@type=printspace]/zone element
		Element zone = null;
		final String zoneType;
		//create correct tei element and respective zone
		switch(type){
		case PARAGRAPH:
			content = tei.createElementNS(TEI_NS, "p");
			zoneType = "p";
			break;
		case HEADING:
			//TODO
			content = null;
			zoneType = null;
			break;
		case FOOTER:
			//TODO
			content = null;
			zoneType = null;
			break;
		default:
			content = null;
			zoneType = null;
			break;
		}
		//fill in content regardless of what type this textRegion is exactly
		if(content != null){
			
			String zoneId = null;
			if(this.mode.val > TeiExportMode.SIMPLE.val) {
				zone = tei.createElementNS(TEI_NS, "zone");
				zone.setAttribute("type", zoneType);
				//set regionId and coords on zone
				zone.setAttribute("points", r.getCoords().getPoints());
				zoneId = facsId + "_" + r.getId();
				zone.setAttribute("xml:id", zoneId);
				//link content to zone
				content.setAttribute("facs", "#" + zoneId);
			}
			
			//iterate lines and concatenate all text
			StringBuilder sb = new StringBuilder();
			List<TextLineType> lines = r.getTextLine();
			Collections.sort(lines, new TrpElementCoordinatesComparator<TextLineType>());
			for(int i = 0; i < lines.size(); i++){
				TextLineType l = lines.get(i);
				if(l.getTextEquiv() != null && l.getTextEquiv().getUnicode() != null){
					final String lineText = l.getTextEquiv().getUnicode();
					
					if (this.mode.val >= TeiExportMode.ZONE_PER_LINE.val) {
						final String linePoints = l.getCoords().getPoints();
						final String lineZoneId = zoneId + "_l_" + i;
						Element lineZone = tei.createElementNS(TEI_NS, "zone");
						lineZone.setAttribute("xml:id", lineZoneId);
						lineZone.setAttribute("points", linePoints);
						lineZone.setAttribute("type", "line");
						zone.appendChild(lineZone);
						//FIXME java dom ops do not allow adding an empty node within text content, so we add it as text.
						// This way, the linebreak will be ampersand encoded in the XML 
						// and has to be replaced manually afterwards
						sb.append(lineText + "\u003clb facs=\"#" + lineZoneId + "\"/\u003e\n");
					} else {
						sb.append(lineText + "\u003clb/\u003e\n");
					}
				}
			}
			content.setTextContent(sb.toString());
			body.appendChild(content);
			if(this.mode.val > TeiExportMode.SIMPLE.val){
				currParentSurface.appendChild(zone);
			}
		}
	}
	
	protected String unescapeLineBreaks(String teiStr){
		//FIXME ugly workaround for having empty linebreak nodes within textContent
		char[] chars = teiStr.toCharArray();
		final char amp = '&';
		StringBuffer sb = new StringBuffer(chars.length);
		boolean isOpen = false;

		for (int i = 0; i < chars.length; i++) {
			// Look for ampersand
			while (i < chars.length && chars[i] != amp) {
				//just copy everything meanwhile
				sb.append(chars[i++]);
			}
			//respect array bounds and check if this is an opening <lb tag
			if (!isOpen && i + 6 < chars.length && new String(chars, i, 6).equals("&lt;lb")) {
				//open lb tag
				isOpen = true;
				sb.append("<");
				//step over escaped sequence (mind loop iteration)
				i += 3;
			} else if (isOpen && i + 4 < chars.length && new String(chars, i, 4).equals("&gt;")) {
				//if bracket is open for linebreak, then search for the escaped closing bracket
				sb.append(">");
				//close lb tag
				isOpen = false; 
				//step over escaped sequence
				i += 3;
			} else if (i < chars.length) {
				//this is just some character we are not interested in. copy...
				sb.append(chars[i]);
			}
		}
		return sb.toString();
	}	
	
	@Override public String getTeiAsString() {
		return unescapeLineBreaks(XmlUtils.printXML(tei));
	}
}
