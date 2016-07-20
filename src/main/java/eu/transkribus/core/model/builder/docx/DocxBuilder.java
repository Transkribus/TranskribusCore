package eu.transkribus.core.model.builder.docx;

import static com.tutego.jrtf.RtfHeader.color;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import java.util.Calendar;

import org.apache.commons.lang.StringEscapeUtils;
import org.docx4j.convert.out.flatOpcXml.FlatOpcXmlCreator;
import org.docx4j.jaxb.Context;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.DocumentSettingsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.FootnotesPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.wml.CTSettings;
import org.docx4j.wml.P.Hyperlink;
import org.docx4j.wml.R;
import org.docx4j.wml.CTFootnotes;
import org.docx4j.wml.CTFtnDocProps;
import org.docx4j.wml.CTFtnEdn;
import org.docx4j.wml.CTFtnEdnRef;
import org.docx4j.wml.CTSettings;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import org.docx4j.wml.Br;
import org.docx4j.wml.CTBookmark;
import org.docx4j.wml.CTLanguage;
import org.docx4j.wml.CTMarkupRange;
import org.docx4j.wml.CommentRangeEnd;
import org.docx4j.wml.CommentRangeStart;
import org.docx4j.wml.Comments;
import org.docx4j.wml.Comments.Comment;
import org.docx4j.wml.Highlight;
import org.docx4j.wml.Jc;
import org.docx4j.wml.NumberFormat;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.ParaRPr;
import org.docx4j.wml.RPr;
import org.docx4j.wml.RStyle;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STVerticalAlignRun;
import org.docx4j.wml.Style;
import org.docx4j.wml.Text;
import org.docx4j.wml.U;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.UnderlineEnumeration;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.pdf.PdfContentByte;
import com.tutego.jrtf.Rtf;
import com.tutego.jrtf.RtfPara;
import com.tutego.jrtf.RtfText;

import eu.transkribus.core.model.beans.EdFeature;
import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.customtags.AbbrevTag;
import eu.transkribus.core.model.beans.customtags.CommentTag;
import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.customtags.UnclearTag;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.RegionTypeUtil;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.builder.ExportUtils;
import eu.transkribus.core.model.builder.rtf.TrpRtfBuilder;
import eu.transkribus.core.util.CoreUtils;

public class DocxBuilder {
	
	private final static Logger logger = LoggerFactory.getLogger(DocxBuilder.class);
	
	TrpDoc doc;
	static boolean exportTags = true;
	static boolean doBlackening = true;
	static boolean markUnclearWords = false;
	static boolean expandAbbrevs = false;
	static boolean substituteAbbrevs = false;
	static boolean preserveLineBreaks = false;
	
	//static Map<CustomTag, String> tags = new HashMap<CustomTag, String>();
	static Set<String> tagnames = new HashSet<String>();
	
	static org.docx4j.wml.ObjectFactory wmlObjectFactory = new org.docx4j.wml.ObjectFactory();
	static org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
	
	static int footnoteCounter = 0;

	private static WordprocessingMLPackage wordMLPackage;
	
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
	
	public static void writeDocxForDoc(TrpDoc doc, boolean wordBased, boolean writeTags, boolean doBlackening, File file, Set<Integer> pageIndices, IProgressMonitor monitor, Set<String> selectedTags, boolean createTitle, boolean markUnclear, boolean expandAbbreviations, boolean replaceAbbrevs, boolean keepLineBreaks) throws JAXBException, IOException, Docx4JException, InterruptedException {
		
		exportTags = writeTags;
		tagnames = selectedTags;
		markUnclearWords = markUnclear;
		expandAbbrevs = expandAbbreviations;
		preserveLineBreaks = keepLineBreaks;
		substituteAbbrevs = replaceAbbrevs;
		
		/*
		 * get all names of tags
		 */
		//tagnames = CustomTagFactory.getRegisteredTagNames();
		
		//main document part
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
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
			
			JAXBPageTranscript tr = ExportUtils.getPageTranscriptAtIndex(i);
			if (tr == null){
				TrpPage page = pages.get(i);
				TrpTranscriptMetadata md = page.getCurrentTranscript();
				//md.getStatus().equals("Done");
				tr = new JAXBPageTranscript(md);
				tr.build();
			}
			
			TrpPageType trpPage = tr.getPage();
			
			logger.debug("writing docx for page "+(i+1)+"/"+doc.getNPages());
			

			writeDocxForTranscript(mdp, trpPage, wordBased, preserveLineBreaks);
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
		
		//write tags at end of last page
		if (exportTags){
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
				HashMap<CustomTag, String> allTagsOfThisTagname = ExportUtils.getTags(currTagname);
				
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
		System.out.println("Saved " + file.getAbsolutePath());
		
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

	private static void writeDocxForTranscript(MainDocumentPart mdp, TrpPageType trpPage,
			boolean wordBased, boolean preserveLineBreaks) {
		boolean rtl = false;
		List<TrpTextRegionType> textRegions = trpPage.getTextRegions(true);

		for (int j=0; j<textRegions.size(); ++j) {
			TrpTextRegionType r = textRegions.get(j);
			
			
//			if (exportTags){
//				getTagsForShapeElement(r);
//			}
			
			/*
			 * create one paragraph for each text region
			 * but only if there is some text in it
			 */
			String helper = r.getUnicodeText().replaceAll("\n", "");
			
			//logger.debug("region unicode text " + helper);
			
			if (!helper.equals("")){
				org.docx4j.wml.P  p = factory.createP();
				mdp.addObject(p);
			

				List<TextLineType> lines = r.getTextLine();
				for (int i=0; i<lines.size(); ++i) {
					TrpTextLineType trpL = (TrpTextLineType) lines.get(i);
					
					try {
						if (wordBased && trpL.getWord().size()>0){
							getFormattedTextForLineElement(trpL.getWord(), p, mdp);
						}
						else {
							getFormattedTextForShapeElement(trpL, p, mdp);
						}
	
	
					
	//				org.docx4j.wml.Text  t = factory.createText();
	//				//create for each textline a run and add the text to it and than to the paragraph
	//				t.setValue(trpL.getUnicodeText());
					//org.docx4j.wml.R  run = factory.createR();
					//run.getContent().add(t);		
					
	//					for (R run : runs){
	//						p.getContent().add(run);
	//					}
						
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					/*add line break after each text line
					 * or omit this if explicitely wished to have dense lines
					 * No line break at end of paragraph
					 */
					if (preserveLineBreaks && !(i+1==lines.size()) ){
						Br br = factory.createBr(); // this Br element is used break the current and go for next line
						p.getContent().add(br);
					}

	
				}
//								
//				linesTexts[i] = ((trpL.getUnicodeText().equals("") || wordBased) && trpL.getWord().size()>0) ? getRtfTextForLineFromWords(trpL) : getRtfTextForShapeElement(trpL);
//				linesTexts[i] = RtfText.text(linesTexts[i], "\n");
			}
			
			
			

			

			/*
			 * 
			 * add comment test
			 * 
			 */
//		      // Create a package
//		      WordprocessingMLPackage wmlPack = new WordprocessingMLPackage();
//		      // Create main document part content
//		      org.docx4j.wml.Body  body = factory.createBody();      
//		      org.docx4j.wml.Document wmlDocumentEl = factory.createDocument();
//		      
//		      wmlDocumentEl.setBody(body);
//		      mdp.setJaxbElement(wmlDocumentEl);
//		      wmlPack.addTargetPart(mdp);
//		      
//		      CommentsPart cp = new CommentsPart();
//		      // Part must have minimal contents
//		      Comments comments = factory.createComments();
//		      cp.setJaxbElement(comments);
//		      
//		      mdp.addTargetPart(cp);
						
			//read from right to left -> alignment is right
			if (rtl){
				//paras[j] = RtfPara.p(linesTexts).footnote("Test").alignRight();
			}
			else{
				String test = "test";
				//paras[j] = RtfPara.p(linesTexts);
				//paras[j] = RtfPara.p(linesTexts, RtfText.footnote("Test")).alignLeft();
			}
			
		}
				
	}
	
	private static void getFormattedTextForLineElement(List<WordType> lines, P p, MainDocumentPart mdp) throws Exception{
		
		int wordCount = 0;
		int nrWords = lines.size();
		
		for (WordType word : lines){
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
		
		String textStr = element.getUnicodeText();
		CustomTagList cl = element.getCustomTagList();
		
		//Todo: add lists for abbrev tag: abbrev(expansion) and for unclear: [Beispiel]
		
		//contains all gap offsets
		ArrayList<Integer> gapList = new ArrayList<Integer>();
		LinkedHashMap<Integer, String> commentList = new LinkedHashMap<Integer, String>();
		
		//unclear list contains unclear begin as key and unclear end as value
		HashMap<Integer, Integer> unclearList = new HashMap<Integer, Integer>();
		
		//Integer used for storing the offset: this is where the abbrev ends resp. (expansion) starts, and String contains the 'expansion' itself
		LinkedHashMap<Integer, String> expandAbbrevList = new LinkedHashMap<Integer, String>();
		LinkedHashMap<Integer, AbbrevTag> substituteAbbrevList = new LinkedHashMap<Integer, AbbrevTag>();
		
		
		
		if (textStr == null || cl == null)
			throw new IOException("Element has no text or custom tag list: "+element+", class: "+element.getClass().getName());
		
		
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
				gapList.add(nonIndexedTag.getOffset());
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
				gapList.add(indexedTag.getOffset());
			}
			
			if (indexedTag.getTagName().equals("comment")){
				logger.debug("indexed comment tag found at pos " + (indexedTag.getEnd()-1));
				CommentTag ct = (CommentTag) indexedTag;
				commentList.put(indexedTag.getEnd()-1, ct.getComment());
			}
			
			//if(exportTags){
			if(markUnclearWords && indexedTag.getTagName().equals("unclear")){
				logger.debug("unclear tag found ");
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
				logger.debug("abbrev tag found ");
				AbbrevTag at = (AbbrevTag) indexedTag;
				String expansion = at.getExpansion();
				//key is the start of the abbrev
				if (!expansion.equals("")){
					substituteAbbrevList.put(indexedTag.getOffset(), at);
				}
			}
			//}
			
			
		}
		
		
		List<TextStyleTag> textStylesTags = element.getTextStyleTags();
		
		//ArrayList<R> runs = new ArrayList<R>();
		
		boolean shapeEnded = false;
		int abbrevIdx = 0;
		
		for (int i=0; i<textStr.length(); ++i) {
			
			//use of abbrevIdx: this is necessary for the appearance at the end of a textline
			//otherwise the abbrev expansion would not appear at the end of a line because then the index i would be too small
			abbrevIdx = i;
			shapeEnded = (i+1 == textStr.length() ? true : false);
			
			/*
			 * is this case the abbrev gets totally replaced by its expansion
			 * so if the start of the abbrev was found the expansion is written and we can break the writing of the abbrev
			 */
			if(substituteAbbrevList.containsKey(i)){
				String exp = substituteAbbrevList.get(i).getExpansion();
				
				org.docx4j.wml.Text  abbrevText = factory.createText();
				abbrevText.setValue(exp);
				
				org.docx4j.wml.R  abbrevRun = factory.createR();
				p.getContent().add(abbrevRun);
				abbrevRun.getContent().add(abbrevText);
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
				
				org.docx4j.wml.Text  abbrevText = factory.createText();
				abbrevText.setValue("["+exp+"]");
				
				org.docx4j.wml.R  abbrevRun = factory.createR();
				p.getContent().add(abbrevRun);
				abbrevRun.getContent().add(abbrevText);
			}

			/*
			 * gap is at this position
			 * hence create extra run with [...] as value and then go on
			 */
			if (gapList.contains(i)){
				org.docx4j.wml.Text  t = factory.createText();
				t.setValue("[...] ");
				t.setSpace("preserve");
				
				org.docx4j.wml.R  run = factory.createR();
				p.getContent().add(run);
				run.getContent().add(t);
			}
			
			//begin of unclear word should be marked with [ and end with ]
			if(unclearList.containsKey(i)){
				org.docx4j.wml.Text  t = factory.createText();
				t.setValue("[");
				
				org.docx4j.wml.R  run = factory.createR();
				p.getContent().add(run);
				run.getContent().add(t);
			}
			
			String currText = "";
			if (i+1 <= textStr.length()){
				currText = textStr.substring(i, i+1);
			}
			
			//soft break
			if (currText.equals("¬") && !preserveLineBreaks){
				break;
			}		
			
			org.docx4j.wml.Text  t = factory.createText();
			t.setValue(currText);
			t.setSpace("preserve");
			
			org.docx4j.wml.R  run = factory.createR();
			p.getContent().add(run);
			run.getContent().add(t);
			
			//end of unclear tag
			if(unclearList.containsValue(i)){
				org.docx4j.wml.Text unclearEnd = factory.createText();
				unclearEnd.setValue("]");
				
				org.docx4j.wml.R  unclearRun = factory.createR();
				p.getContent().add(unclearRun);
				run.getContent().add(unclearEnd);
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
					
					//rpr.setHighlight(new Highlight());
						
				}
			}
			
			//at the run properties (= text styles) to the run
			run.setRPr(rpr);
			
			/*
			 * abbrev at end of shape (= line) -> means use (index + 1)
			 */
			if (shapeEnded){
				if(expandAbbrevList.containsKey(i+1)){
					logger.debug("abbrev is at end of shape!");
					String exp = expandAbbrevList.get(i+1);
					
					org.docx4j.wml.Text  abbrevText = factory.createText();
					abbrevText.setValue("["+exp+"]");
					
					org.docx4j.wml.R  abbrevRun = factory.createR();
					p.getContent().add(abbrevRun);
					abbrevRun.getContent().add(abbrevText);
				}
			}
			
			
			//find position of footnote/comment
			if (commentList.containsKey(i)){
//				logger.debug("position of comment: " + i);
//				logger.debug("value of comment: " + commentList.get(i));
				
				//creates the footnote at the end of the wished text - this position was found at the beginning of this method
				org.docx4j.wml.R  fnRun = factory.createR();
				p.getContent().add(fnRun);
				createFootnote(commentList.get(i), fnRun, mdp);
			}
			
			/*
			 * add space at end of line if line breaks are not preserved
			 */
			if (!preserveLineBreaks && shapeEnded){
				org.docx4j.wml.Text  space = factory.createText();
				space.setValue(" ");
				space.setSpace("preserve");
				
				org.docx4j.wml.R  runSpace = factory.createR();
				p.getContent().add(runSpace);
				runSpace.getContent().add(space);
			}
			
			


			//runs.add(run);
			
		}

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

}
