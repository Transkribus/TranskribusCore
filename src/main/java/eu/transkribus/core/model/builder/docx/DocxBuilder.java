package eu.transkribus.core.model.builder.docx;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringEscapeUtils;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.model.fields.FieldUpdater;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.DocumentSettingsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTFootnotes;
import org.docx4j.wml.CTFtnDocProps;
import org.docx4j.wml.CTFtnEdn;
import org.docx4j.wml.CTFtnEdnRef;
import org.docx4j.wml.CTLanguage;
import org.docx4j.wml.CTSettings;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.Jc;
import org.docx4j.wml.JcEnumeration;
import org.docx4j.wml.P;
import org.docx4j.wml.P.Hyperlink;
import org.docx4j.wml.PPr;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.RStyle;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STVerticalAlignRun;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblGrid;
import org.docx4j.wml.TblGridCol;
import org.docx4j.wml.TblWidth;
import org.docx4j.wml.Tc;
import org.docx4j.wml.TcPr;
import org.docx4j.wml.TcPrInner.GridSpan;
import org.docx4j.wml.TcPrInner.VMerge;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.docx4j.wml.U;
import org.docx4j.wml.UnderlineEnumeration;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.EdFeature;
import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.customtags.AbbrevTag;
import eu.transkribus.core.model.beans.customtags.CommentTag;
import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagFactory;
import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.GapTag;
import eu.transkribus.core.model.beans.customtags.SuppliedTag;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.RegionTypeUtil;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpElementReadingOrderComparator;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTableCellType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTableRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.builder.ExportCache;
import eu.transkribus.core.model.builder.ExportUtils;
import eu.transkribus.core.util.CoreUtils;

public class DocxBuilder {
	
	private final static Logger logger = LoggerFactory.getLogger(DocxBuilder.class);

	
	TrpDoc doc;
	static boolean exportTags = false;
	static boolean doBlackening = false;
	static boolean markUnclearWords = false;
	static boolean expandAbbrevs = false;
	static boolean substituteAbbrevs = false;
	static boolean preserveLineBreaks = false;
	static boolean showSuppliedWithBrackets = false;
	static boolean ignoreSupplied = false;
	
	//static Map<CustomTag, String> tags = new HashMap<CustomTag, String>();
	static Set<String> tagnames = new HashSet<String>();
	
	static org.docx4j.wml.ObjectFactory wmlObjectFactory = new org.docx4j.wml.ObjectFactory();
	static org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
	
	static int footnoteCounter = 0;

	private static WordprocessingMLPackage wordMLPackage;
	
	/*
	 * all lists necessary to export the tags in the right way
	 */
	//contains all gap offsets and the gap tag
	static LinkedHashMap<Integer, GapTag> gapList = new LinkedHashMap<Integer, GapTag>();
	static LinkedHashMap<Integer, String> commentList = new LinkedHashMap<Integer, String>();
	//unclear list contains unclear begin as key and unclear end as value
	static HashMap<Integer, Integer> unclearList = new HashMap<Integer, Integer>();
	//Integer used for storing the offset: this is where the abbrev ends resp. (expansion) starts, and String contains the 'expansion' itself
	static LinkedHashMap<Integer, String> expandAbbrevList = new LinkedHashMap<Integer, String>();
	static LinkedHashMap<Integer, AbbrevTag> substituteAbbrevList = new LinkedHashMap<Integer, AbbrevTag>();
	//index: tags get stored as index in word and pressing F9 in word gives than this index list
	static HashMap<Integer, ArrayList<CustomTag>> idxList = new HashMap<Integer, ArrayList<CustomTag>>();
	
	static LinkedHashMap<Integer, String> showSuppliedList = new LinkedHashMap<Integer, String>();
	static LinkedHashMap<Integer, String> ignoreSuppliedList = new LinkedHashMap<Integer, String>();
	

	public static void main(String[] args) throws Exception {

		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
		MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
		
		// Setup FootnotesPart if necessary,
		// along with DocumentSettings
		FootnotesPart footnotesPart = mdp.getFootnotesPart();
		if (footnotesPart==null) { // that'll be the case in this example
			// initialise it
			footnotesPart = new FootnotesPart();
			mdp.addTargetPart(footnotesPart);
						
			CTFootnotes footnotes = (CTFootnotes)XmlUtils.unwrap(
					XmlUtils.unmarshalString(footnotePartXML));	
			footnotesPart.setJaxbElement(footnotes);
			
			// Usually the settings part contains footnote properties;
			// so add these if not present
			DocumentSettingsPart dsp =mdp.getDocumentSettingsPart();
			if (dsp==null) {
				// create it
				dsp = new DocumentSettingsPart();
				mdp.addTargetPart(dsp);
			} 
			CTSettings settings = dsp.getContents();
			if (settings ==null) {
				settings = wmlObjectFactory.createCTSettings(); 
				dsp.setJaxbElement(settings);				
			}
			
			CTFtnDocProps ftndocprops = settings.getFootnotePr();
			if (ftndocprops==null ) {
				String openXML = "<w:footnotePr xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
			            + "<w:footnote w:id=\"-1\"/>" // these 2 numbers are special, and correspond with string footnotePartXML below
			            + "<w:footnote w:id=\"0\"/>"
			        +"</w:footnotePr>";
				settings.setFootnotePr(
						(CTFtnDocProps)XmlUtils.unmarshalString(openXML, Context.jc, CTFtnDocProps.class	 ));
			}
		}
		
		// Example
		// Create and add p
		org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
		org.docx4j.wml.P  p = factory.createP();
		mdp.getContent().add(p);
		
		// Add a run
		R r = new R();
		p.getContent().add(r);		

		org.docx4j.wml.Text  t = factory.createText();
		t.setValue("Hello world");
		r.getContent().add(t);
		
		// OK, add a footnote
		addFootnote(1, "my footnote", footnotesPart, r);  
			// Note: your footnote ids must be distinct; they don't need to be ordered (though Word will do that when you open the docx)
		
		// Save it
		wordMLPackage.save(new java.io.File("C:/Users/Administrator/footnoteTest2.docx") );
		System.out.println("Saved " + "C:/Users/Administrator/footnoteTest2.docx");
		
		
		/*
		 * add comments example: Kommentar auf der rechten Seite des Dokuments
		 */
		
//		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
//
//		// Create and add a Comments Part
//	      CommentsPart cp = new CommentsPart();
//	      wordMLPackage.getMainDocumentPart().addTargetPart(cp);
//	      	      
//		// Part must have minimal contents
//		Comments comments = factory.createComments();
//		cp.setJaxbElement(comments);
//
//		// Add a comment to the comments part
//		java.math.BigInteger commentId = BigInteger.valueOf(0);
//		Comment theComment = createComment(commentId, "fred", null,
//				"my first comment");
//		comments.getComment().add(theComment);
//		
//		// Add comment reference to document
//		//P paraToCommentOn = wordMLPackage.getMainDocumentPart().addParagraphOfText("here is some content");
//		P p = new P();
//		
//		wordMLPackage.getMainDocumentPart().getContent().add(p);
//		
//	    // Create object for commentRangeStart
//	    CommentRangeStart commentrangestart = factory.createCommentRangeStart(); 
//	        commentrangestart.setId( commentId );  // substitute your comment id
//	        
//	        
//	        // The actual content, in the middle
//	        p.getContent().add(commentrangestart);
//
//			org.docx4j.wml.Text  t = factory.createText();
//			t.setValue("hello");
//
//			org.docx4j.wml.R  run = factory.createR();
//			run.getContent().add(t);	
//			
//	        p.getContent().add(run);
//	        
//	    // Create object for commentRangeEnd
//	    CommentRangeEnd commentrangeend = factory.createCommentRangeEnd(); 
//	        commentrangeend.setId( commentId );  // substitute your comment id
//
//	        p.getContent().add(commentrangeend);
//	        
//		p.getContent().add(createRunCommentReference(commentId));
//		
//		System.out.println(wordMLPackage.getMainDocumentPart().getXML());
//		
//
//		// ++, for next comment ...
//		commentId = commentId.add(java.math.BigInteger.ONE);
//
//		wordMLPackage.save(new java.io.File("C:/Users/Administrator/commentTest.docx") );
//		System.out.println("Saved " + "C:/Users/Administrator/commentTest.docx");
//
//		
//		System.out.println("Done.");
	}
	
	public static void writeDocxForDoc(TrpDoc doc, boolean wordBased, boolean writeTags, boolean doBlackeningSensibleData, File file, Set<Integer> pageIndices, IProgressMonitor monitor, boolean createTitle, boolean markUnclear, boolean expandAbbreviations, boolean replaceAbbrevs, boolean keepLineBreaks, boolean showSuppliedInBrackets, boolean ignoreSuppliedTags, ExportCache cache) throws JAXBException, IOException, Docx4JException, InterruptedException {
		
	    //ch.qos.logback.classic.Logger root = logger.getClass().get(ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
	    ((ch.qos.logback.classic.Logger) logger).setLevel(ch.qos.logback.classic.Level.DEBUG);
	    
		exportTags = writeTags;
		doBlackening = doBlackeningSensibleData;
		tagnames = cache.getOnlySelectedTagnames(ExportUtils.getOnlyWantedTagnames(CustomTagFactory.getRegisteredTagNames()));
		markUnclearWords = markUnclear;
		expandAbbrevs = expandAbbreviations;
		preserveLineBreaks = keepLineBreaks;
		substituteAbbrevs = replaceAbbrevs;
		showSuppliedWithBrackets = showSuppliedInBrackets;
		ignoreSupplied = ignoreSuppliedTags;
		
		/*
		 * get all names of tags
		 */
		//tagnames = CustomTagFactory.getRegisteredTagNames();
		
		//main document part
		wordMLPackage = WordprocessingMLPackage.createPackage();
		MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();

		org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
		
		List<TrpPage> pages = doc.getPages();
		
		int totalPages = pageIndices==null ? pages.size() : pageIndices.size();
		if (monitor!=null) {
			monitor.beginTask("Exporting to docx", totalPages);
		}
		
		int c=0;
		boolean atLeastOnePageWritten = false;
		
		//can be used as page break every time we need one
		Br objBr = new Br();
		objBr.setType(STBrType.PAGE);
		P pageBreakP = factory.createP();
		pageBreakP.getContent().add(objBr);
		
		for (int i=0; i<pages.size(); ++i) {
				
			if (pageIndices!=null && !pageIndices.contains(i))
				continue;
			
			if (!atLeastOnePageWritten && createTitle){
				addTitlePage(doc, mdp);
				//add page break
				mdp.addObject(pageBreakP);
			}
			
			if (monitor!=null) {
				if (monitor.isCanceled()) {					
					throw new InterruptedException("Export canceled by the user");
//					logger.debug("docx export cancelled!");
//					return;
				}
				monitor.subTask("Processing page "+(c+1));
			}
//			TrpPage page = pages.get(i);
//			TrpTranscriptMetadata md = page.getCurrentTranscript();
//			JAXBPageTranscript tr = new JAXBPageTranscript(md);
//			tr.build();
			JAXBPageTranscript tr = null;
			if(cache != null) {
				tr = cache.getPageTranscriptAtIndex(i);
			}
			if (tr == null){
				TrpPage page = pages.get(i);
				TrpTranscriptMetadata md = page.getCurrentTranscript();
				//md.getStatus().equals("Done");
				tr = new JAXBPageTranscript(md);
				tr.build();
			}
			
			TrpPageType trpPage = tr.getPage();
			
			logger.debug("writing docx for the page "+(i+1)+"/"+doc.getNPages());
			

			writeDocxForTranscriptWithTables(mdp, trpPage, wordBased, preserveLineBreaks);
			atLeastOnePageWritten = true;
			++c;
			
			/* Old:
			 * page break after first page for each page except the last one
			 * 
			 * New:
			 * no page break - lets do this automatically
			 */
//			if (atLeastOnePageWritten && c < pageIndices.size()){
//				mdp.addObject(pageBreakP);
//			}
			
			
			if (monitor!=null) {
				monitor.worked(c);
			}
		}
		
		P p = factory.createP();
		mdp.getContent().add(p);
		addComplexField(p, " INDEX \\e \"", "\" \\c \"1\" \\z \"1031\""); 
		
		FieldUpdater updater = new FieldUpdater(wordMLPackage);
		updater.update(true);

		//addComplexField(p, "\" \\c \"1\" \\z \"1031\"", "");
		
		//write tags at end of last page
		if (false){
			//RtfText headline = RtfText.text("Person names in this document (amount of found persons: " + persons.size() + ")", "\n");
			
			logger.debug("export tags ");
			boolean firstExport = true;

			/*
			 * for all different tagnames:
			 * find all custom tags in doc
			 * create list and 
			 */
			
			//tagnames = all user choosen tags via export dialog
			for (String currTagname : tagnames){
				//logger.debug("curr tagname " + currTagname);
				//get all custom tags with currTagname and text
				HashMap<CustomTag, String> allTagsOfThisTagname = cache.getTags(currTagname);
				
				//one paragraph for each tagname
				org.docx4j.wml.P  p4Tag = factory.createP();
				if(allTagsOfThisTagname.size()>0 && !currTagname.equals("textStyle") && !currTagname.equals("gap") && !currTagname.equals("comment")){
					//new page if tag export starts
					if (firstExport){
//						Br objBr = new Br();
//						objBr.setType(STBrType.PAGE);
						p4Tag.getContent().add(objBr);
						firstExport = false;
					}
					//logger.debug("allTagsOfThisTagname " + allTagsOfThisTagname.size());
					//one run for headline and thanfor each entry
					org.docx4j.wml.Text  t = factory.createText();
					t.setValue(currTagname + " tags in this document: " + allTagsOfThisTagname.size());
					t.setSpace("preserve");
					
					org.docx4j.wml.R  run = factory.createR();
					run.getContent().add(t);				
					
					org.docx4j.wml.RPr rpr = factory.createRPr();	
					
					org.docx4j.wml.BooleanDefaultTrue b = new org.docx4j.wml.BooleanDefaultTrue();
					b.setVal(true);	
					
					U u = factory.createU();
					u.setVal(UnderlineEnumeration.SINGLE);
					rpr.setB(b);
					rpr.setU(u);
					
					run.setRPr(rpr);
					
					Br br = factory.createBr(); // this Br element is used break the current and go for next line
					run.getContent().add(br);
					
					p4Tag.getContent().add(run);
					//ArrayList<RtfText> tagTexts = new ArrayList<RtfText>(); 
					Collection<String> valueSet = allTagsOfThisTagname.values();

					int l = 0;
					for (String currEntry : valueSet){
						org.docx4j.wml.R  currRun = factory.createR();
						org.docx4j.wml.Text  currText = factory.createText();
						currText.setValue(currEntry);
						currText.setSpace("preserve");
						currRun.getContent().add(currText);
						//reuse linebreak
						currRun.getContent().add(br);
						
						p4Tag.getContent().add(currRun);
					}

				}
				mdp.getContent().add(p4Tag);
				
			}

		}
		
		//finally save the file
		wordMLPackage.save( file );
		

		logger.info("Saved " + file.getAbsolutePath());
		
	}
	
	public static void addTitlePage(TrpDoc doc, MainDocumentPart mdp) {
		
		mdp.getPropertyResolver().activateStyle("Light Shading");
		mdp.getPropertyResolver().activateStyle("Medium List 1");

		addParagraph("", "Title Page", mdp, "Title");
		
		TrpDocMetadata docMd = doc.getMd();
		
		addParagraph("Title: ", docMd.getTitle(), mdp, "Subtitle");
	    
		addParagraph("Author: ", docMd.getAuthor(), mdp, "Subtitle");

		addParagraph("Description: ", docMd.getDesc(), mdp, "Subtitle");
		
		addParagraph("Genre: ", docMd.getGenre(), mdp, "Subtitle");
		
		addParagraph("Writer: ", docMd.getWriter(), mdp, "Subtitle");
		
		if (docMd.getScriptType() != null){
			addParagraph("Sripttype: ", docMd.getScriptType().toString(), mdp, "Subtitle");
		}
		
		addParagraph("Language: ", docMd.getLanguage(), mdp, "Subtitle");
		
		addParagraph("Number of Pages in whole Document: ", String.valueOf(docMd.getNrOfPages()), mdp, "Subtitle");
		
		if (docMd.getCreatedFromDate() != null){
			addParagraph("Created From: ", docMd.getCreatedFromDate().toString(), mdp, "Subtitle");
		}
		
		if (docMd.getCreatedToDate() != null){
			addParagraph("Created To: ", docMd.getCreatedToDate().toString(), mdp, "Subtitle");
		}
		
		/*
		 * 	static boolean exportTags = true;
			static boolean doBlackening = true;
			static boolean markUnclearWords = false;
			static boolean expandAbbrevs = false;
			static boolean substituteAbbrevs = false;
			static boolean preserveLineBreaks = false;
		 */
		addParagraph("", "Export Settings", mdp, "Title");
		String tagSettings = (exportTags ? "Custom tags are indexed" : "Custom tags are not exported");
		String blackeningSetting = (doBlackening ? "Sensible data is blackened" : "All data is visible");
		String abbrevsSettings = (expandAbbrevs ? "Abbreviations are expanded (abbrev [expansion])" : (substituteAbbrevs ? "Abbreviations are subsituted by there expansion": "Abbreviations as they are (diplomatic text)"));
		String unclearSettings = (markUnclearWords ? "Unclear words are marked" : "");
		String lineBreakSettings = (preserveLineBreaks ? "Keep the line breaks as in the original document" : "Line breaks does not conform to the original text");
		String suppliedSettings = (showSuppliedWithBrackets ? "Supplied tags are shown in brackets" : (ignoreSupplied ? "Supplied tags get ignored" : "Supplied tags are not marked specifically"));
		
		addParagraph("", blackeningSetting + " / " + tagSettings + " / " + abbrevsSettings + " / " + unclearSettings + " / " + lineBreakSettings + " / " + suppliedSettings, mdp, "Subtitle");

		addParagraph("", "Editorial Declaration: ", mdp, "Title");
		for (EdFeature edfeat : doc.getEdDeclList()){
			addParagraph("", edfeat.getTitle() + ": " + edfeat.getDescription() +"\n" + edfeat.getSelectedOption().toString(), mdp, "Subtitle");
		}		
	}
	
	private static void addParagraph(String mdName, String mdValue, MainDocumentPart mdp, String style) {
		
		if (mdValue != null && !mdValue.equals("")){
			org.docx4j.wml.P  p = mdp.createStyledParagraphOfText(style, mdName + mdValue);
			
			mdp.addObject(p);
			
//			org.docx4j.wml.Text  t = factory.createText();
//			t.setValue(mdName + mdValue);
//			t.setSpace("preserve");
//					
//			org.docx4j.wml.R  run = factory.createR();
//			p.getContent().add(run);
//			run.getContent().add(t);
//			
//		    org.docx4j.wml.PPr  pPr = factory.createPPr();
//	        p.setPPr(pPr);
//	        		
//		    org.docx4j.wml.PPrBase.PStyle pStyle = factory.createPPrBasePStyle();
//		    pPr.setPStyle(pStyle);
//		    pStyle.setVal(style);
		}
		
	}
	
	private static void writeDocxForTranscriptWithTables(MainDocumentPart mdp, TrpPageType trpPage,
			boolean wordBased, boolean preserveLineBreaks) {
		boolean rtl = false;
		
		//TrpTableRegionType is contained in the regions too
		List<TrpRegionType> regions = trpPage.getRegions();
		Collections.sort(regions, new TrpElementReadingOrderComparator<RegionType>(true));
		
		for (int j=0; j<regions.size(); ++j) {
			TrpRegionType r = regions.get(j);
			
			if (r instanceof TrpTableRegionType){
				logger.debug("is table");
				TrpTableRegionType table = (TrpTableRegionType) r;
				
				int cols = table.getNCols();
				int rows = table.getNRows();
				
	        	double maxX = table.getBoundingBox().getMaxX();//PageXmlUtils.buildPolygon(table.getCoords().getPoints()).getBounds().getMaxX();
	        	double minX = table.getBoundingBox().getMinX();//PageXmlUtils.buildPolygon(table.getCoords().getPoints()).getBounds().getMinX();
	        	int tablesize = (int) (maxX - minX);
				
				List<List<TrpTableCellType>> allRowCells = new ArrayList<List<TrpTableCellType>>();
				for (int k = 0; k<rows; k++){
					allRowCells.add(table.getRowCells(k));
				}
				
	            List<HashMap<Integer, TrpTableCellType>> allRows = new ArrayList<HashMap<Integer, TrpTableCellType>>();
	            
	            HashMap<Integer, TrpTableCellType> nextRowMap = new HashMap<Integer, TrpTableCellType>();
	           
	            for (List<TrpTableCellType> rowCells : allRowCells){
	          
	            	HashMap<Integer, TrpTableCellType> currRowMap = new HashMap<Integer, TrpTableCellType>();
	            	
	            	/*
	            	 * fill up all cells which are not set in TRP (needed for vertical cell merge)
	            	 * the nextRowMap contains already all cells which span vertically with the cells above - means they got merged 
	            	 * in the table but have to be considered here 
	            	 */
    				currRowMap.putAll(nextRowMap);
    				nextRowMap.clear();
	            	
	            	for (TrpTableCellType cell : rowCells){
		            	//logger.debug("table cell text " + cell.getUnicodeTextFromLines());
		            	currRowMap.put(cell.getCol(), cell);
		            	if (cell.getRowSpan() > 1){
		            		nextRowMap.put(cell.getCol(), null);
		            	}
	            	}
	            	allRows.add(currRowMap);
	            }

	      		Tbl thisTable;
				try {
					thisTable = getDocxTable(wordMLPackage, wordBased, rows, cols, allRows, tablesize, mdp);
					mdp.addObject(thisTable);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//Br br = factory.createBr(); // this Br element is used break the current and go for next line
				org.docx4j.wml.P  p = factory.createP();
				mdp.addObject(p);
				//p.getContent().add(br);

			}
			else if (r instanceof TrpTextRegionType){
				
				TrpTextRegionType tr = (TrpTextRegionType) r;
			
				/*
				 * create one paragraph for each text region
				 * but only if there is some text in it
				 */
				String helper = tr.getUnicodeText().replaceAll("\n", "");
				
				//logger.debug("region unicode text " + helper);
				
				if (!helper.equals("")){
					
					exportTextRegion(tr, wordBased, null, null, mdp);
		
				}
			}
		}				
	}
	
	private static void exportTextRegion(TrpTextRegionType tr, boolean wordBased, P p, Tc cell, MainDocumentPart mdp) {
		
		if (p == null){
			p = factory.createP();
			mdp.addObject(p);
		}

		List<TextLineType> lines = tr.getTextLine();
		for (int i=0; i<lines.size(); ++i) {
			TrpTextLineType trpL = (TrpTextLineType) lines.get(i);
												
			try {
				if (wordBased && trpL.getWord().size()>0){
					getFormattedTextForLineElement(trpL.getWord(), p, mdp);
				}
				else {
					getFormattedTextForShapeElement(trpL, p, mdp);
					
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/* with ¶ the user can mark a new paragraph inside a text region
			 * unicode is \u00B6
			 */
			if (trpL.getCustomTagList().containsParagraphTag()){
				//then new paragraph should be used;
				//logger.debug("paragraph sign detected ");
				p = factory.createP();
				if (cell != null){
					cell.getContent().add(p);
				}
				else{
					mdp.addObject(p);
				}
				
			}
			/*add line break after each text line
			 * or omit this if explicitely wished to have dense lines
			 * No line break at end of paragraph
			 */
			else if (preserveLineBreaks && !(i+1==lines.size()) ){
				Br br = factory.createBr(); // this Br element is used break the current and go for next line
				p.getContent().add(br);
			}
		}
		
	}

	private static Tbl getDocxTable(WordprocessingMLPackage wPMLpackage, boolean isWordBased, int rows, int cols, List<HashMap<Integer, TrpTableCellType>> allRows, int tablesize, MainDocumentPart mdp) throws Exception {

	    int writableWidthTwips = wPMLpackage.getDocumentModel().getSections()
	                                        .get(0).getPageDimensions()
	                                        .getWritableWidthTwips();

	    int cellWidthTwips = new Double(
	            Math.floor((writableWidthTwips / cols))
	        ).intValue();

	    Tbl table = TblFactory.createTable(0 , 0, cellWidthTwips);
	          
        TblGrid tblGrid = factory.createTblGrid();
        
        for( int i = 0; i < cols; i++) {
            TblGridCol col = factory.createTblGridCol();
            col.setW( BigInteger.valueOf( cellWidthTwips ) );
            tblGrid.getGridCol().add( col );
        }
       
        table.setTblGrid( tblGrid );

	    int i = 0;

	    for (HashMap<Integer, TrpTableCellType> entry : allRows) {
	        
	        Tr row = factory.createTr();
	        table.getContent().add(row);

	        i++;
	        int d = 0;
	        
	        if (entry.keySet().size() != cols){
	        	logger.debug("size of entries does not match columns ");
	        }
	        
	        for (Integer key : entry.keySet()) {
	        	Tc cell = factory.createTc();
	        	
	        	row.getContent().add(cell);
	        	
	        	String rowSpan = null;
	        	int colSpan = 1;
	        	boolean mergedVertical = false;
	        	
	        	int colsize = cellWidthTwips;
	        	
	        	if (entry.get(key) != null){
		        	if (entry.get(key).getRowSpan() > 1){
		        		mergedVertical = true;
		        		rowSpan = "restart";
		        	}
		        	
		        	double maxX = entry.get(key).getBoundingBox().getMaxX();//PageXmlUtils.buildPolygon(entry.get(key).getCoords().getPoints()).getBounds().getMaxX();
		        	double minX = entry.get(key).getBoundingBox().getMinX();//PageXmlUtils.buildPolygon(entry.get(key).getCoords().getPoints()).getBounds().getMinX();
		        	double colsizeRel = maxX - minX;
		        			        	
		        	double colsizetmp = colsizeRel/ (double) tablesize;
//		        	logger.debug("colsizetmp " + colsizetmp);
		        	colsize=(int) (writableWidthTwips*colsizetmp);
		        	
		        	colSpan = entry.get(key).getColSpan();
		        	
//		        	logger.debug("colsize " + colsize);
//		        	logger.debug("text in this cell is " + entry.get(key).getUnicodeTextFromLines());
		        	
		        	int colID = entry.get(key).getCol();
		        	int rowID = entry.get(key).getRow();
	        	}
	        	else{
	        		//logger.debug("no cell for this column ");
	        		mergedVertical = true;
	        	}
	        		        	
	        	applyGridSpan(cell, colSpan, rowSpan, colsize, mergedVertical);
	             
	            P columnPara = factory.createP();
	            //P columnPara = (P) column.getContent().get(0);
	            
	            cell.getContent().add(columnPara);
//	            d++;
//	            Text tx = factory.createText();
//	            R run = factory.createR();

	            if (entry.get(key) != null){            	
	            	//old solution till now: tx.setValue(entry.get(key).getUnicodeTextFromLines());
	            	if(entry.get(key).getUnicodeTextFromLines() != ""){
	            		exportTextRegion(entry.get(key), isWordBased, columnPara, cell, mdp);
	            	}

	            }
//	            run.getContent().add(tx);
//	            columnPara.getContent().add(run);
	            
	        }
	    }
	    return table;
	}
	
    private static void applyGridSpan( final Tc cell, final int colSpan, final String rowSpan, int w, boolean mergedVertical ) {
    	
        TcPr tcPr = factory.createTcPr();
        TblWidth tblWidth = factory.createTblWidth();
        tblWidth.setType( "dxa" );
        tblWidth.setW( BigInteger.valueOf( w*colSpan ) );
        tcPr.setTcW( tblWidth  );
        
        if ( colSpan > 1) {
            GridSpan gridSpan = factory.createTcPrInnerGridSpan();
            gridSpan.setVal(BigInteger.valueOf(colSpan));
            tcPr.setGridSpan(gridSpan);
        }
        
        if ( mergedVertical ) {
        	//logger.debug(" this is vertical span");
        	VMerge gridVSpan = factory.createTcPrInnerVMerge();
        	if (rowSpan != null)
        		gridVSpan.setVal(rowSpan);
            tcPr.setVMerge(gridVSpan);
                        
        }
       
        cell.setTcPr(tcPr);
    }
   
    
/*
 * was version before we esported tables too
 */
//	private static void writeDocxForTranscript(MainDocumentPart mdp, TrpPageType trpPage,
//			boolean wordBased, boolean preserveLineBreaks) {
//		boolean rtl = false;
//		
//		List<TrpTextRegionType> textRegions = trpPage.getTextRegions(true);
//
//		for (int j=0; j<textRegions.size(); ++j) {
//			TrpTextRegionType r = textRegions.get(j);
//			
//			
////			if (exportTags){
////				getTagsForShapeElement(r);
////			}
//			
//			/*
//			 * create one paragraph for each text region
//			 * but only if there is some text in it
//			 */
//			String helper = r.getUnicodeText().replaceAll("\n", "");
//			
//			//logger.debug("region unicode text " + helper);
//			
//			if (!helper.equals("")){
//				
//				org.docx4j.wml.P  p = factory.createP();
//				mdp.addObject(p);
//
//				List<TextLineType> lines = r.getTextLine();
//				for (int i=0; i<lines.size(); ++i) {
//					TrpTextLineType trpL = (TrpTextLineType) lines.get(i);
//					
//					try {
//						if (wordBased && trpL.getWord().size()>0){
//							getFormattedTextForLineElement(trpL.getWord(), p, mdp);
//						}
//						else {
//							getFormattedTextForShapeElement(trpL, p, mdp);
//							
//						}
//
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					
//					/*add line break after each text line
//					 * or omit this if explicitely wished to have dense lines
//					 * No line break at end of paragraph
//					 */
//					if (preserveLineBreaks && !(i+1==lines.size()) ){
//						Br br = factory.createBr(); // this Br element is used break the current and go for next line
//						p.getContent().add(br);
//					}
//
//	
//				}
////								
////				linesTexts[i] = ((trpL.getUnicodeText().equals("") || wordBased) && trpL.getWord().size()>0) ? getRtfTextForLineFromWords(trpL) : getRtfTextForShapeElement(trpL);
////				linesTexts[i] = RtfText.text(linesTexts[i], "\n");
//			}
//			
//		}
//				
//	}
	
	private static void getFormattedTextForLineElement(List<WordType> words, P p, MainDocumentPart mdp) throws Exception{
		
		int wordCount = 0;
		int nrWords = words.size();
		
		for (WordType word : words){
			getFormattedTextForShapeElement((ITrpShapeType) word, p, mdp);
			//add empty space after each word
			if (wordCount < nrWords-1){
				org.docx4j.wml.Text  t = factory.createText();
				t.setValue(" ");
				t.setSpace("preserve");
				
				org.docx4j.wml.R  run = factory.createR();
				p.getContent().add(run);
				run.getContent().add(t);
			}
			wordCount++;
		}

	}

	private static void getFormattedTextForShapeElement(ITrpShapeType element, P p, MainDocumentPart mdp) throws Exception {
		
		ArrayList<R> listOfallRuns = new ArrayList<R>();
		
		String textStr = element.getUnicodeText();
		
		CustomTagList cl = element.getCustomTagList();
		
		if (textStr == null || cl == null)
			throw new IOException("Element has no text or custom tag list: "+element+", class: "+element.getClass().getName());
		
		if (textStr.isEmpty()){
			return;
		}
		
		boolean rtl = false;
		//from right to left
		if (Character.getDirectionality(textStr.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT
			    || Character.getDirectionality(textStr.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
			    || Character.getDirectionality(textStr.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING
			    || Character.getDirectionality(textStr.charAt(0)) == Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE
			    ) {
			logger.debug("&&&&&&&& STRING IS RTL : ");
			deleteCharAtIndex(0, textStr);
			rtl = true;
		}

			    // it is a RTL string
		
		
//		if(rtl){
//			//textStr = new StringBuilder(textStr).reverse().toString();
//			
////			TextDirection textdirection = wmlObjectFactory.createTextDirection();
////			textdirection.setVal( "tbRl"); 
//			
//			PPr paragraphProperties = factory.createPPr();
////			Jc justification = factory.createJc();
////			justification.setVal(JcEnumeration.RIGHT);
////			paragraphProperties.setJc(justification);
//			
//	        org.docx4j.wml.BooleanDefaultTrue b = new org.docx4j.wml.BooleanDefaultTrue();
//	        b.setVal(true); 
//			paragraphProperties.setBidi(b);
//			
//			p.setPPr(paragraphProperties);
//			
//			
//		}
		
		
		// format according to tags:CustomTagList
		for (CustomTag nonIndexedTag : cl.getNonIndexedTags()) {
			
			//exchange chars with * if wished to be blackened
			if (doBlackening && nonIndexedTag.getTagName().equals(RegionTypeUtil.BLACKENING_REGION.toLowerCase())){
				//logger.debug("nonindexed tag found ");
				textStr = ExportUtils.blackenString(nonIndexedTag, textStr);
			}
			
			/*
			 * for gap and comment: remember their position to find and add them to their corresponding 'run' later on 
			 * 
			 */
			if (nonIndexedTag.getTagName().equals("gap")){
				GapTag gap = (GapTag) nonIndexedTag;
				gapList.put(nonIndexedTag.getOffset(), gap);
			}
			
			//unclear and comments can not be non-indexed
			
//			if (nonIndexedTag.getTagName().equals("comment")){
//				logger.debug("nonindexed comment tag found ");
//				CommentTag ct = (CommentTag) nonIndexedTag;
//				commentList.put(nonIndexedTag.getEnd()-1, ct.getComment());
//			}

//			if(nonIndexedTag.getTagName().equals("unclear")){
//				logger.debug("unclear tag found ");
//				unclearList.put(nonIndexedTag.getOffset(), nonIndexedTag.getOffset()+nonIndexedTag.getLength());
//			}
			
			
		}
		for (CustomTag indexedTag : cl.getIndexedTags()) {
			if (doBlackening && indexedTag.getTagName().equals(RegionTypeUtil.BLACKENING_REGION.toLowerCase())){
				textStr = ExportUtils.blackenString(indexedTag, textStr);
			}
			
			/*
			 * find all gaps and store the offset
			 */
			if (indexedTag.getTagName().equals("gap")){
				GapTag gap = (GapTag) indexedTag;
				gapList.put(indexedTag.getOffset(), gap);
			}
			
			if (indexedTag.getTagName().equals("comment")){
				//logger.debug("indexed comment tag found at pos " + (indexedTag.getEnd()-1));
				CommentTag ct = (CommentTag) indexedTag;
				commentList.put(indexedTag.getEnd()-1, ct.getComment());
			}
			
			//if(exportTags){
			if(markUnclearWords && indexedTag.getTagName().equals("unclear")){
//				logger.debug("unclear tag found ");
//				logger.debug("unclear start is: " + indexedTag.getOffset());
//				logger.debug("unclear end is: " + (indexedTag.getEnd()-1));
				unclearList.put(indexedTag.getOffset(), indexedTag.getEnd()-1);
			}
			
			if(expandAbbrevs && indexedTag.getTagName().equals("abbrev")){
				logger.debug("abbrev tag found ");
				AbbrevTag at = (AbbrevTag) indexedTag;
				String expansion = at.getExpansion();
				//only add if an expansion was typed
				if (!expansion.equals("")){
					expandAbbrevList.put(indexedTag.getEnd(), at.getExpansion());
				}
			}
			
			if(substituteAbbrevs && indexedTag.getTagName().equals("abbrev")){
				//logger.debug("abbrev tag found ");
				AbbrevTag at = (AbbrevTag) indexedTag;
				String expansion = at.getExpansion();
				//key is the start of the abbrev
				if (!expansion.equals("")){
					substituteAbbrevList.put(indexedTag.getOffset(), at);
				}
			}
			
			if (showSuppliedWithBrackets && indexedTag.getTagName().equals("supplied")){
				//logger.debug("supplied tag found ");
				SuppliedTag at = (SuppliedTag) indexedTag;
				String text = at.getContainedText();
				//only add if an expansion was typed
				if (!text.equals("")){
					showSuppliedList.put(indexedTag.getOffset(), text);
				}
			}
			
			if (ignoreSupplied && indexedTag.getTagName().equals("supplied")){
				//logger.debug("supplied tag found ");
				SuppliedTag at = (SuppliedTag) indexedTag;
				String text = at.getContainedText();
				//only add if an expansion was typed
				if (!text.equals("")){
					ignoreSuppliedList.put(indexedTag.getOffset(), text);
				}
			}
			
			//create index for all choosen tagnames
			if (exportTags && tagnames.contains(indexedTag.getTagName()) && !indexedTag.getTagName().equals("gap")){
				//logger.debug("export tag as idx entry " + indexedTag.getOffset());
				addValuesToIdxList(idxList, indexedTag.getEnd(), indexedTag);
			}
			//}
	
		}
		
		
		List<TextStyleTag> textStylesTags = element.getTextStyleTags();
		
		//ArrayList<R> runs = new ArrayList<R>();
		
		boolean shapeEnded = false;
		
		for (int i=0; i<=textStr.length(); ++i) {
			
			//use of abbrevIdx: this is necessary for the appearance at the end of a textline
			//otherwise the abbrev expansion would not appear at the end of a line because then the index i would be too small

			shapeEnded = (i+1 >= textStr.length() ? true : false);
			
			/*
			 * is this case the abbrev gets totally replaced by its expansion
			 * so if the start of the abbrev was found the expansion is written and we can break the writing of the abbrev
			 */
			if(substituteAbbrevList.containsKey(i)){
				String exp = substituteAbbrevList.get(i).getExpansion();
				
				if(rtl){
					exp = reverseString(exp);
				}
				
				org.docx4j.wml.Text  abbrevText = factory.createText();
				abbrevText.setValue(exp);
				
				org.docx4j.wml.R  abbrevRun = factory.createR();
				//p.getContent().add(abbrevRun);
				abbrevRun.getContent().add(abbrevText);
				
				listOfallRuns.add(abbrevRun);
				//go to end of the abbreviation and proceed with remaining text
				i += substituteAbbrevList.get(i).getLength();
				shapeEnded = (i == textStr.length() ? true : false);
			}
			
			/*
			 * add expansion in brackets behind the abbrev		
			 * the abbrev list contains as key the end index of the abbrev	
			 */
			if(expandAbbrevList.containsKey(i)){
				String exp = expandAbbrevList.get(i);
				if(rtl){
					exp = reverseString(exp);
				}
				
				org.docx4j.wml.Text  abbrevText = factory.createText();
				abbrevText.setValue("["+exp+"]");
				
				org.docx4j.wml.R  abbrevRun = factory.createR();
				//p.getContent().add(abbrevRun);
				abbrevRun.getContent().add(abbrevText);
				
				listOfallRuns.add(abbrevRun);
			}
			
			/*
			 * in this case the supplied tag is expanded either with or without brackets
			 * 
			 */
			if(showSuppliedList.containsKey(i)){
				String exp = showSuppliedList.get(i);
				if(rtl){
					exp = reverseString(exp);
				}
				
				org.docx4j.wml.Text suppliedText = factory.createText();
				suppliedText.setValue("["+exp+"]");
				
				org.docx4j.wml.R  suppliedRun = factory.createR();
				suppliedRun.getContent().add(suppliedText);
				
				listOfallRuns.add(suppliedRun);
				
				//supplied is handled now - so set i to the end of supplied
				i += showSuppliedList.get(i).length();
				shapeEnded = (i == textStr.length() ? true : false);
			}
			
			/*
			 * in this case the supplied tag gets ignored
			 * this means that index i must be incremented by the length of this supplied tag text
			 */
			if(ignoreSuppliedList.containsKey(i)){
				
				i += ignoreSuppliedList.get(i).length();
				shapeEnded = (i == textStr.length() ? true : false);
				
			}
			
			/*
			 * gap is at this position
			 * hence create extra run with [...] as value and then go on
			 * of if suppied attribute is set handle supplied as set in the export settings
			 */
			if (gapList.containsKey(i)){
				org.docx4j.wml.Text  t = factory.createText();
//				if (!rtl)
//					t.setValue("[...] ");
//				else
//					t.setValue(" [...]");
				
				GapTag gt = gapList.get(i);
				String cta = (String) gt.getAttributeValue("supplied");

				//attribute supplied is set in the gap tag -> handle supplied as wanted
				if (cta != null && !cta.equals("")){
					//logger.debug("attribute: " + cta);
					
					//may the gap with supplied attribute gets ignored 
					if(!ignoreSupplied){
						if (showSuppliedWithBrackets){
							t.setValue("["+cta+"]");
						}
						//do not show supplied attribute by default!?
//						else{
//							t.setValue(cta);
//						}
					}
				}
				//nothing supplied, so show [...] for the gap tag 
				else{
					t.setValue("[...]");
					t.setSpace("preserve");
				}
				
				org.docx4j.wml.R  run = factory.createR();
				//p.getContent().add(run);
				run.getContent().add(t);

				listOfallRuns.add(run);
					
			}
			
			//begin of unclear word should be marked with [ and end with ]
			if(unclearList.containsKey(i)){
				org.docx4j.wml.Text  t = factory.createText();
				if (!rtl)
					t.setValue("[");
				else
					t.setValue("]");
				
				org.docx4j.wml.R  run = factory.createR();
				//p.getContent().add(run);
				run.getContent().add(t);
				
				listOfallRuns.add(run);
			}
			
			/*
			 * if so we create an index entry for this text string in the docx
			 */
			if (idxList.containsKey(i)){
				
				addIndexEntry(i, p, textStr, rtl);
			}
			
			String currText = "";
			if (i+1 <= textStr.length()){
				currText = textStr.substring(i, i+1);
				//logger.debug("&&&&&&&& current single char : " + currText);
			}
			
			/*
			 * 2nd is (should be) soft hyphen with Unicode U+00AD
			 * First arg is not sign and was initially used for soft hyphen by Diggitexx
			 * need to be at the line end - otherwise 
			 * 
			 */
			if ( (currText.equals("¬") || currText.equals("­") || currText.equals("-") ) && !preserveLineBreaks && shapeEnded){
				break;
			}				
			
			org.docx4j.wml.Text  t = factory.createText();
			t.setValue(currText);
			t.setSpace("preserve");

			org.docx4j.wml.R  run = factory.createR();
			//p.getContent().add(run);
			run.getContent().add(t);

			listOfallRuns.add(run);
			
			//end of unclear tag
			if(unclearList.containsValue(i)){
				
				org.docx4j.wml.Text unclearEnd = factory.createText();
				
				if (!rtl)
					unclearEnd.setValue("]");
				else
					unclearEnd.setValue("[");
				
				org.docx4j.wml.R  unclearRun = factory.createR();
				//p.getContent().add(unclearRun);
				unclearRun.getContent().add(unclearEnd);
				
				listOfallRuns.add(unclearRun);
			}

			//the properties of this text section
			org.docx4j.wml.RPr rpr = factory.createRPr();

						
			/*
			 * format according to custom style tag - check for each char in the text if a special style should be set
			 */
			for (TextStyleTag styleTag : textStylesTags){
				if (i >= styleTag.getOffset() && i < (styleTag.getOffset()+styleTag.getLength())){
					
					org.docx4j.wml.BooleanDefaultTrue b = new org.docx4j.wml.BooleanDefaultTrue();
					b.setVal(true);	
					
					TextStyleType ts = styleTag.getTextStyle();
					if (ts == null)
						continue;
					
					if (CoreUtils.val(ts.isBold())) {
						rpr.setB(b);
					}			
					if (CoreUtils.val(ts.isItalic())) {
						rpr.setI(b);
					}
					if (CoreUtils.val(ts.isLetterSpaced())) {
						// ????
					}
					if (CoreUtils.val(ts.isMonospace())) {
						// ????
						
					}
					if (CoreUtils.val(ts.isReverseVideo())) {
						// ????
					}
					if (CoreUtils.val(ts.isSerif())) {
						// ????
					}
					if (CoreUtils.val(ts.isSmallCaps())) {
						rpr.setSmallCaps(b);
					}
					if (CoreUtils.val(ts.isStrikethrough())) {
						rpr.setStrike(b);
					}
					if (CoreUtils.val(ts.isSubscript())) {
						org.docx4j.wml.CTVerticalAlignRun al = factory.createCTVerticalAlignRun();
						al.setVal(STVerticalAlignRun.SUBSCRIPT);
						
						rpr.setVertAlign(al);
					}
					if (CoreUtils.val(ts.isSuperscript())) {
						org.docx4j.wml.CTVerticalAlignRun al = factory.createCTVerticalAlignRun();
						al.setVal(STVerticalAlignRun.SUPERSCRIPT);
						
						rpr.setVertAlign(al);
					}
					if (CoreUtils.val(ts.isUnderlined())) {
						U u = factory.createU();
						u.setVal(UnderlineEnumeration.SINGLE);
						rpr.setU(u);
					}
					
//					BooleanDefaultTrue bdt = Context.getWmlObjectFactory().createBooleanDefaultTrue();
//					bdt.setVal(Boolean.TRUE);
//					rpr.setRtl(bdt);
					
					//rpr.setHighlight(new Highlight());
						
				}
			}
			
			//at the run properties (= text styles) to the run
			run.setRPr(rpr);
			/*
			 * abbrev at end of shape (= line) -> means use (index + 1)
			 */
//			if (shapeEnded){
//				if(expandAbbrevList.containsKey(i+1)){
//					logger.debug("abbrev is at end of shape!");
//					String exp = expandAbbrevList.get(i+1);
//					
//					if (!exp.equals("")){
//					
//						org.docx4j.wml.Text  abbrevText = factory.createText();
//						abbrevText.setValue("["+exp+"]");
//						
//						org.docx4j.wml.R  abbrevRun = factory.createR();
//						//p.getContent().add(abbrevRun);
//						abbrevRun.getContent().add(abbrevText);
//						
//						listOfallRuns.add(abbrevRun);
//					}
//				}
//				
//				if (idxList.containsKey(i+1)){
//
//					addIndexEntry(i+1, p, textStr, rtl);
//					
//				}
//			}
			
			//find position of footnote/comment
			if (commentList.containsKey(i)){
//				logger.debug("position of comment: " + i);
//				logger.debug("value of comment: " + commentList.get(i));
				
				//creates the footnote at the end of the wished text - this position was found at the beginning of this method
				org.docx4j.wml.R  fnRun = factory.createR();
				//p.getContent().add(fnRun);
				createFootnote(commentList.get(i), fnRun, mdp);
				
				listOfallRuns.add(fnRun);
			}
			
			/*
			 * add space at end of line if line breaks are not preserved
			 */
			if (!preserveLineBreaks && shapeEnded){
				org.docx4j.wml.Text  space = factory.createText();
				space.setValue(" ");
				space.setSpace("preserve");
				
				org.docx4j.wml.R  runSpace = factory.createR();
				//p.getContent().add(runSpace);
				runSpace.getContent().add(space);
				
				listOfallRuns.add(runSpace);
			}

			//runs.add(run);
			
		}
		
		if(rtl){
			PPr paragraphProperties = factory.createPPr();
			Jc justification = factory.createJc();
			justification.setVal(JcEnumeration.RIGHT);
			paragraphProperties.setJc(justification);
		
			p.setPPr(paragraphProperties);
		}
		
		/*
		 * add all runs after the actual shape chars are analized and finished
		 * for text right to left (rtl) we need to reverse the content
		 */
		
		for (int i = listOfallRuns.size()-1; i>=0; i--){
			if (rtl){
				p.getContent().add(listOfallRuns.get(i));
			}
			else{
				p.getContent().addAll(listOfallRuns);
				break;
			}
		}
		
		clearAllLists();

	}
	
	private static void addIndexEntry(int idx, P p, String textStr, boolean rtl) {
		
		ArrayList<CustomTag> allTagsAtThisPlace = idxList.get(idx);
		for (CustomTag ct : allTagsAtThisPlace){
			int begin = ct.getOffset();
			String tagname = ct.getTagName();
			String idxText = textStr.substring(begin, idx);
			
			if (rtl){
				idxText = reverseString(idxText);
				//logger.debug("reversed index text is " + idxText);
			}					
			
			if (ct instanceof AbbrevTag){
				AbbrevTag at = (AbbrevTag) ct;
				
				if(!at.getExpansion().equals("")){
				
					if (!rtl)
						idxText = idxText.concat(" ["+at.getExpansion()+"]");
					else{
						String tmp = reverseString(at.getExpansion());
						idxText = "["+tmp+"] ".concat(idxText);
					}
				}
			}
//			logger.debug("index entry is " + idxText);
//			logger.debug("XE\""+tagname + ":" + idxText+"\"");
			
			if(!idxText.matches("[*]+")){
				addComplexField(p, "XE\""+tagname + ":" + idxText+"\"", "");
			}
		}
		
	}


	private static String reverseString(String text) {
		StringBuilder sb = new StringBuilder(text);
		sb.reverse();
		return (sb.toString());
	}
	
	private static String deleteCharAtIndex(int index, String text) {
		StringBuilder sb = new StringBuilder(text);
		sb.deleteCharAt(index);
		return (sb.toString());
	}

	/*
	 * 	static ArrayList<Integer> gapList = new ArrayList<Integer>();
	static LinkedHashMap<Integer, String> commentList = new LinkedHashMap<Integer, String>();
	//unclear list contains unclear begin as key and unclear end as value
	static HashMap<Integer, Integer> unclearList = new HashMap<Integer, Integer>();
	//Integer used for storing the offset: this is where the abbrev ends resp. (expansion) starts, and String contains the 'expansion' itself
	static LinkedHashMap<Integer, String> expandAbbrevList = new LinkedHashMap<Integer, String>();
	static LinkedHashMap<Integer, AbbrevTag> substituteAbbrevList = new LinkedHashMap<Integer, AbbrevTag>();
	//index: tags get stored as index in word and pressing F9 in word gives than this index list
	HashMap<Integer, ArrayList<CustomTag>> idxList = new HashMap<Integer, ArrayList<CustomTag>>();
	 */
	private static void clearAllLists() {
		gapList.clear();
		commentList.clear();
		unclearList.clear();
		expandAbbrevList.clear();
		substituteAbbrevList.clear();
		idxList.clear();
		showSuppliedList.clear();
		ignoreSuppliedList.clear();
		
	}

	private static void addComplexField(P p, String instrText, String instrText2) {

	    org.docx4j.wml.ObjectFactory wmlObjectFactory = Context.getWmlObjectFactory();

	    // Create object for r
	    R r = wmlObjectFactory.createR(); 
	    p.getContent().add( r); 
        // Create object for fldChar (wrapped in JAXBElement) 
        FldChar fldchar = wmlObjectFactory.createFldChar(); 
        JAXBElement<org.docx4j.wml.FldChar> fldcharWrapped = wmlObjectFactory.createRFldChar(fldchar); 
        r.getContent().add( fldcharWrapped); 
            fldchar.setFldCharType(org.docx4j.wml.STFldCharType.BEGIN);
        // Create object for instrText (wrapped in JAXBElement) 
        Text text = wmlObjectFactory.createText(); 
        JAXBElement<org.docx4j.wml.Text> textWrapped = wmlObjectFactory.createRInstrText(text); 
        r.getContent().add( textWrapped); 
        text.setValue( instrText); 
        text.setSpace( "preserve");    
        
	    R r2 = wmlObjectFactory.createR(); 
	    p.getContent().add( r2); 
	    
        if (!instrText2.equals("")){
    	    
        	r2.getContent().add(wmlObjectFactory.createRTab());
        	
            text = wmlObjectFactory.createText(); 
            JAXBElement<org.docx4j.wml.Text> textWrapped2 = wmlObjectFactory.createRInstrText(text); 
            r2.getContent().add( textWrapped2); 
            text.setValue( instrText2); 
            text.setSpace( "preserve");
        	
        }

        
//        R r = factory.createR();
//        r.setRsidRPr("TOC entry text");
//        R.Tab tab = new R.Tab();
//        r.getRunContent().add(tab);             
//        p.getParagraphContent().add(r);
        
//		org.docx4j.wml.Text idx = wmlObjectFactory.createText();
//		JAXBElement<org.docx4j.wml.Text> textWrapped2 = wmlObjectFactory.createRInstrText(idx); 
//        r.getContent().add( textWrapped2); 
//        idx.setValue( idxText); 
//        idx.setSpace( "preserve"); 

        // Create object for fldChar (wrapped in JAXBElement) 
        fldchar = wmlObjectFactory.createFldChar(); 
        fldcharWrapped = wmlObjectFactory.createRFldChar(fldchar); 
        r2.getContent().add( fldcharWrapped); 
            fldchar.setFldCharType(org.docx4j.wml.STFldCharType.END);

	}

	public static void createFootnote(String fnComment, R r, MainDocumentPart mdp) throws Exception{
		
		// Setup FootnotesPart if necessary,
		// along with DocumentSettings
		FootnotesPart footnotesPart = mdp.getFootnotesPart();
		if (footnotesPart==null) { // that'll be the case in this example
			// initialise it
			footnotesPart = new FootnotesPart();
			mdp.addTargetPart(footnotesPart);
			
			CTFootnotes footnotes = (CTFootnotes)XmlUtils.unwrap(
					XmlUtils.unmarshalString(footnotePartXML));	
			footnotesPart.setJaxbElement(footnotes);
						
			// Usually the settings part contains footnote properties;
			// so add these if not present
			DocumentSettingsPart dsp =mdp.getDocumentSettingsPart();
			if (dsp==null) {
				// create it
				dsp = new DocumentSettingsPart();
				mdp.addTargetPart(dsp);
			} 
			CTSettings settings = dsp.getContents();
			if (settings ==null) {
				settings = wmlObjectFactory.createCTSettings(); 
				dsp.setJaxbElement(settings);				
			}
			
			CTFtnDocProps ftndocprops = settings.getFootnotePr();
			
			if (ftndocprops==null ) {
				String openXML = "<w:footnotePr xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">"
			            + "<w:footnote w:id=\"-1\"/>" // these 2 numbers are special, and correspond with string footnotePartXML below
			            + "<w:footnote w:id=\"0\"/>"
			        +"</w:footnotePr>";
				settings.setFootnotePr(
						(CTFtnDocProps)XmlUtils.unmarshalString(openXML, Context.jc, CTFtnDocProps.class	 ));
			}
			
			
		}
		
		mdp.getPropertyResolver().activateStyle("FootnoteReference");
		//mdp.getPropertyResolver().activateStyle("FootnoteText");
		
		// OK, add a footnote
		addFootnote(footnoteCounter++, fnComment, footnotesPart, r);  
			// Note: your footnote ids must be distinct; they don't need to be ordered (though Word will do that when you open the docx)
		
	}
	
	public static void addFootnote(int i, String text, FootnotesPart footnotesPart, R r) throws JAXBException, Docx4JException {
		
		
		
		// Add the note number in the run
	    CTFtnEdnRef ftnednref = factory.createCTFtnEdnRef(); 
	    JAXBElement<org.docx4j.wml.CTFtnEdnRef> ftnednrefWrapped = factory.createRFootnoteReference(ftnednref); 
	    r.getContent().add( ftnednrefWrapped); 
	    
	    ftnednref.setId( BigInteger.valueOf(i) );
	    
	    //escape the special chars for XML
	    String escapedText = StringEscapeUtils.escapeXml(text);
	        
	    /*
	     * Test: try to set footnote reference
	     */
	    org.docx4j.wml.RPr props = factory.createRPr();
		RStyle rStyle = new RStyle();
		rStyle.setVal("FootnoteReference");
		props.setRStyle(rStyle);
		
		r.setRPr(props);

	        
		    // Create a footnote in the footnotesPart
	        String openXML = "<w:footnote w:id=\"" + i + "\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\">"
	                + "<w:p>"
	                    + "<w:pPr>"
	                        + "<w:pStyle w:val=\"FootnoteText\"/>"
	                        + "<w:rPr>"
	                            + "<w:lang w:val=\"en-AU\"/>"
	                        +"</w:rPr>"
	                    +"</w:pPr>"
	                    + "<w:r>"
	                        + "<w:rPr>"
	                            + "<w:rStyle w:val=\"FootnoteReference\"/>"
	                            + "<w:vertAlign w:val=\"superscript\" />"
	                        +"</w:rPr>"
	                        + "<w:footnoteRef/>"
	                    +"</w:r>"
	                    + "<w:r>"
	                        + "<w:t xml:space=\"preserve\"> </w:t>"
	                    +"</w:r>"
	                    + "<w:r>"
	                        + "<w:rPr>"
	                            + "<w:lang w:val=\"en-AU\"/>"
	                        +"</w:rPr>"
	                        + "<w:t>" + escapedText +"</w:t>"
	                    +"</w:r>"
	                +"</w:p>"
	            +"</w:footnote>";		 
	        
		
	        CTFtnEdn ftnedn = (CTFtnEdn)XmlUtils.unmarshalString(openXML, Context.jc, CTFtnEdn.class);	        
	        footnotesPart.getContents().getFootnote().add(ftnedn);
	}
	
	
	
	static String footnotePartXML = "<w:footnotes mc:Ignorable=\"w14 wp14\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\" xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\">"
            + "<w:footnote w:id=\"-1\" w:type=\"separator\">"  // matching CTFtnDocProps above
                + "<w:p>"
                    + "<w:pPr>"
                        + "<w:spacing w:after=\"0\" w:line=\"240\" w:lineRule=\"auto\"/>"
                    +"</w:pPr>"
                    + "<w:r>"
                        + "<w:separator/>"
                    +"</w:r>"
                +"</w:p>"
            +"</w:footnote>"
            + "<w:footnote w:id=\"0\" w:type=\"continuationSeparator\">"
                + "<w:p>"
                    + "<w:pPr>"
                        + "<w:spacing w:after=\"0\" w:line=\"240\" w:lineRule=\"auto\"/>"
                    +"</w:pPr>"
                    + "<w:r>"
                        + "<w:continuationSeparator/>"
                    +"</w:r>"
                +"</w:p>"
            +"</w:footnote>"           
        +"</w:footnotes>";
	

	
	
	public static Hyperlink createHyperlink(MainDocumentPart mdp, String url) {
		
		try {

			// We need to add a relationship to word/_rels/document.xml.rels
			// but since its external, we don't use the 
			// usual wordMLPackage.getMainDocumentPart().addTargetPart
			// mechanism
			org.docx4j.relationships.ObjectFactory factory =
				new org.docx4j.relationships.ObjectFactory();
			
			org.docx4j.relationships.Relationship rel = factory.createRelationship();
			rel.setType( Namespaces.HYPERLINK  );
			rel.setTarget(url);
			rel.setTargetMode("External");  
									
			mdp.getRelationshipsPart().addRelationship(rel);
			
			// addRelationship sets the rel's @Id
			
			String hpl = "<w:hyperlink r:id=\"" + rel.getId() + "\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" " +
            "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" >" +
            "<w:r>" +
            "<w:rPr>" +
            "<w:rStyle w:val=\"Hyperlink\" />" +  // TODO: enable this style in the document!
            "</w:rPr>" +
            "<w:t>Link</w:t>" +
            "</w:r>" +
            "</w:hyperlink>";

//			return (Hyperlink)XmlUtils.unmarshalString(hpl, Context.jc, P.Hyperlink.class);
			return (Hyperlink)XmlUtils.unmarshalString(hpl);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	public static P createIt() {

		org.docx4j.wml.ObjectFactory wmlObjectFactory = new org.docx4j.wml.ObjectFactory();

		P p = wmlObjectFactory.createP();
		    // Create object for pPr
		    PPr ppr = wmlObjectFactory.createPPr();
		    p.setPPr(ppr);
		        // Create object for rPr
		        ParaRPr pararpr = wmlObjectFactory.createParaRPr();
		        ppr.setRPr(pararpr);
		            // Create object for u
		            U u = wmlObjectFactory.createU();
		            pararpr.setU(u);
		                u.setVal(org.docx4j.wml.UnderlineEnumeration.SINGLE);
		            // Create object for lang
		            CTLanguage language = wmlObjectFactory.createCTLanguage();
		            pararpr.setLang(language);
		                language.setVal( "en-AU");
		        // Create object for jc
		        Jc jc = wmlObjectFactory.createJc();
		        ppr.setJc(jc);
		            jc.setVal(org.docx4j.wml.JcEnumeration.CENTER);
		    // Create object for r
		    R r = wmlObjectFactory.createR();
		    p.getContent().add( r);
		        // Create object for rPr
		        RPr rpr = wmlObjectFactory.createRPr();
		        r.setRPr(rpr);
		            // Create object for u
		            U u2 = wmlObjectFactory.createU();
		            rpr.setU(u2);
		                u2.setVal(org.docx4j.wml.UnderlineEnumeration.SINGLE);
		            // Create object for lang
		            CTLanguage language2 = wmlObjectFactory.createCTLanguage();
		            rpr.setLang(language2);
		                language2.setVal( "en-AU");
		        // Create object for t (wrapped in JAXBElement)
		        Text text = wmlObjectFactory.createText();
		        JAXBElement<org.docx4j.wml.Text> textWrapped = wmlObjectFactory.createRT(text);
		        r.getContent().add( textWrapped);
		            text.setValue( "Underlined and centred");
		            
		            

		return p;
		}
	
    private static org.docx4j.wml.Comments.Comment createComment(java.math.BigInteger commentId,
    		String author, Calendar date, String message) {

		org.docx4j.wml.Comments.Comment comment = factory.createCommentsComment();
		comment.setId( commentId );
		if (author!=null) {
			comment.setAuthor(author);
		}
		if (date!=null) {
//			String dateString = RFC3339_FORMAT.format(date.getTime()) ;	
//			comment.setDate(value)
			// TODO - at present this is XMLGregorianCalendar
		}
		org.docx4j.wml.P commentP = factory.createP();
		comment.getEGBlockLevelElts().add(commentP);
		org.docx4j.wml.R commentR = factory.createR();
		commentP.getContent().add(commentR);
		org.docx4j.wml.Text commentText = factory.createText();
		commentR.getContent().add(commentText);
		
		commentText.setValue(message);
    	
    	return comment;
    }
    
    private static org.docx4j.wml.R createRunCommentReference(java.math.BigInteger commentId) {
    	
		org.docx4j.wml.R run = factory.createR();
		org.docx4j.wml.R.CommentReference commentRef = factory.createRCommentReference();
		run.getContent().add(commentRef);
		commentRef.setId( commentId );	
		
		return run;
    	
    }
    
    private static void addValuesToIdxList(HashMap<Integer, ArrayList<CustomTag>> hashMap, Integer key, CustomTag value) {
    	   ArrayList<CustomTag> tempList = null;
    	   if (hashMap.containsKey(key)) {
    	      tempList = (ArrayList<CustomTag>) hashMap.get(key);
    	      if(tempList == null)
    	         tempList = new ArrayList<CustomTag>();
    	      tempList.add(value);  
    	   } else {
    	      tempList = new ArrayList<CustomTag>();
    	      tempList.add(value);               
    	   }
    	   hashMap.put(key,tempList);
    	}

}
