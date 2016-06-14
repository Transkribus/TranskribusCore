package eu.transkribus.core.model.builder.rtf;

//import ch.qos.logback.classic.Logger;

//import static com.tutego.jrtf.Rtf.rtf;
//import static com.tutego.jrtf.RtfDocfmt.*;
import static com.tutego.jrtf.RtfHeader.color;

//import static com.tutego.jrtf.Rtf.rtf;
//import static com.tutego.jrtf.RtfPara.p;
//import static com.tutego.jrtf.RtfText.italic;
//import static com.tutego.jrtf.RtfText.tab;
//import static com.tutego.jrtf.RtfText.text;
//import static com.tutego.jrtf.RtfText.underline;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tutego.jrtf.Rtf;
import com.tutego.jrtf.RtfPara;
import com.tutego.jrtf.RtfText;

import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.RegionTypeUtil;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpWordType;
import eu.transkribus.core.model.builder.ExportUtils;
import eu.transkribus.core.util.CoreUtils;

public class TrpRtfBuilder {
	private final static Logger logger = LoggerFactory.getLogger(TrpRtfBuilder.class);
	
	TrpDoc doc;
	static boolean exportTags = true;
	static boolean doBlackening = true;
	
	//static Map<CustomTag, String> tags = new HashMap<CustomTag, String>();
	static Set<String> tagnames = new HashSet<String>();
	
	static List<String> persons = new ArrayList<String>();
	static List<String> places = new ArrayList<String>();

	public TrpRtfBuilder(TrpDoc doc) {
		this.doc = doc;
	}
	
	private static RtfText formatRtfText(RtfText text, TextStyleType ts) {
		if (ts == null)
			return text;
		
		if (CoreUtils.val(ts.isBold())) {
			text = RtfText.bold(text);
		}			
		if (CoreUtils.val(ts.isItalic())) {
			text = RtfText.italic(text);
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
			RtfText.smallCapitals(text);
		}
		if (CoreUtils.val(ts.isStrikethrough())) {
			text = RtfText.strikethru(text);
		}
		if (CoreUtils.val(ts.isSubscript())) {
			text = RtfText.subscript(text);
		}
		if (CoreUtils.val(ts.isSuperscript())) {
			text = RtfText.superscript(text);
		}
		if (CoreUtils.val(ts.isUnderlined())) {
			text = RtfText.underline(text);
		}
			
		return text;
	}
	
	private static RtfText getRtfTextForShapeElement(ITrpShapeType element) throws IOException {

		String textStr = element.getUnicodeText();
		CustomTagList cl = element.getCustomTagList();
		if (textStr == null || cl == null)
			throw new IOException("Element has no text or custom tag list: "+element+", class: "+element.getClass().getName());
		
		if (doBlackening){
			// format according to tags:CustomTagList
			for (CustomTag nonIndexedTag : cl.getNonIndexedTags()) {
				
				if (nonIndexedTag.getTagName().equals(RegionTypeUtil.BLACKENING_REGION.toLowerCase())){
					//logger.debug("nonindexed tag found ");
					textStr = ExportUtils.blackenString(nonIndexedTag, textStr);
				}
	
			}
			for (CustomTag indexedTag : cl.getIndexedTags()) {
				if (indexedTag.getTagName().equals(RegionTypeUtil.BLACKENING_REGION.toLowerCase())){
					//logger.debug("nonindexed tag found ");
					textStr = ExportUtils.blackenString(indexedTag, textStr);
				}
			}
		}
		
		List<TextStyleTag> textStylesTags = element.getTextStyleTags();
		
		
		
//		if (exportTags){
//			getTagsForShapeElement(element);
//		}
		
		RtfText[] chars = new RtfText[textStr.length()];
		
		for (int i=0; i<textStr.length(); ++i) {

			chars[i] = RtfText.text(textStr.substring(i, i+1));

			// format according to "global" text style
			chars[i] = formatRtfText(chars[i], element.getTextStyle());
			
			/*
			 * format according to custom style tag - check for each char in the text if a special style should be set
			 */
			for (TextStyleTag styleTag : textStylesTags){
				if (i >= styleTag.getOffset() && i < (styleTag.getOffset()+styleTag.getLength())){
					chars[i] = formatRtfText(chars[i], styleTag.getTextStyle());
				}
			}
			
//			// format according to tags:
//			for (String nonIndexedTag : cl.getNonIndexedTagNames()) {
//				charText = formatRtfText(charText, element);
//				// TODO
//			}
//			for (String indexedTag : cl.getIndexedTagNames()) {
//				// TODO
//			}
			
			

			
			// TODO: include structure types!! (also possible in custom tags!!)
			
			
			// TODO: include reading order!!!
		}
		
		
		
		RtfText totalText = RtfText.text(false, (Object[]) chars);
		
//		if (exportTags && element.getClass().getSimpleName().equals("TrpWordType")){
//			
//			//logger.debug("element.getClass().getName().equals(TrpWordType) " + element.getClass().getSimpleName());
//			
//			if (persons.contains(element.getUnicodeText())){
//				//logger.debug("person contained " + element.getUnicodeText());
//				totalText = RtfText.color(0, textStr);
//			}
//			else if (places.contains(element.getUnicodeText())){
//				
//				//logger.debug("place contained " + element.getUnicodeText());
//				totalText = RtfText.color(1, textStr);
//			}
//
//		}
		
		//RtfText totalText = RtfText.text(false, chars);
		
		return totalText;
	}
	
//	private static void getTagsForShapeElement(ITrpShapeType element) throws IOException{
//
//		String textStr = element.getUnicodeText();
//		CustomTagList cl = element.getCustomTagList();
//		if (textStr == null || cl == null)
//			throw new IOException("Element has no text or custom tag list: "+element+", class: "+element.getClass().getName());
//			
//		for (CustomTag nonIndexedTag : cl.getNonIndexedTags()) {
//			
//			logger.debug("nonindexed tag found ");
//			storeCustomTag(nonIndexedTag, textStr);
//
//		}
//		for (CustomTag indexedTag : cl.getIndexedTags()) {
//			
//			logger.debug("indexed tag found ");
//			storeCustomTag(indexedTag, textStr);
//
//		}
//
//	}
//	
//	private static void storeCustomTag(CustomTag currTag, String textStr) {
//		if (!currTag.getTagName().equals("textStyle")){
//			
//			if (currTag.getOffset() != -1 && currTag.getLength() != -1 && (currTag.getOffset()+currTag.getLength() <= textStr.length())){
//				tags.put(currTag, textStr.substring(currTag.getOffset(), currTag.getOffset()+currTag.getLength()));
//			}
//			else{
//				tags.put(currTag, textStr);
//			}
//			logger.debug("++tag name is " + currTag.getTagName());
//			logger.debug("text " + tags.get(currTag));
//		}
//		
//		if (currTag.getTagName().equals("Person")){
//			if (currTag.getOffset() != -1 && currTag.getLength() != -1 && (currTag.getOffset()+currTag.getLength() <= textStr.length())){
//				persons.add(textStr.substring(currTag.getOffset(), currTag.getOffset()+currTag.getLength()));
//			}
//			else{
//				logger.debug("with index is something wrong: offset " + currTag.getOffset() + " length " + currTag.getLength()) ;
//				//throw new Exception("Something wrong with indexed tag for text: " + textStr);
//			}
//		}
//		else if (currTag.getTagName().equals("Place")){
//			if (currTag.getOffset() != -1 && currTag.getLength() != -1 && (currTag.getOffset()+currTag.getLength() <= textStr.length())){
//				places.add(textStr.substring(currTag.getOffset(), currTag.getOffset()+currTag.getLength()));
//			}
//		}
//		
//	}

	private static RtfText getRtfTextForLineFromWords(TrpTextLineType line) throws IOException {
		List<WordType> words = line.getWord();
		
		RtfText[] wordTexts = new RtfText[words.size()];
		for (int i=0; i<wordTexts.length; ++i) {
			TrpWordType w = (TrpWordType) words.get(i);
			wordTexts[i] = getRtfTextForShapeElement(w);
		}
		RtfText totalText = RtfText.text(true, wordTexts);
		
		return totalText;
	}
	
//	public static void writeRtfForElement(Rtf rtf, ITrpShapeType element, boolean wordBased, File file, boolean append) throws IOException, JAXBException {
//		element.getUnicodeText();
//		CustomTagList cl = element.getCustomTagList();
//		
//		RtfText text = RtfText.text(element.getUnicodeText());
//		text = formatRtfText(text, element.getTextStyle());
//		
//		
//		
//		if (element instanceof TextLineType || element instanceof TextRegionType) {// TODO words vs lines and regions
//			rtf.p(text);
//		} else if (element instanceof TrpWordType) {
////			rtf.p(texts);
//		}
//		
//		
////		cl.getCustomTagAndContinuations(tag)
//		
//		
//		
//	}
	
	public static RtfPara[] getRtfParagraphsForTranscript(TrpPageType trpPage, boolean wordBased) throws IOException, JAXBException {
		
		boolean rtl = false;
		List<TrpTextRegionType> textRegions = trpPage.getTextRegions(true);
		//List<TrpTextRegionType> textRegions = trpPage.getTextRegionsAndTextRegionsFromTableRegions(true);
		RtfPara[] paras = new RtfPara[textRegions.size()];
		for (int j=0; j<textRegions.size(); ++j) {
			TrpTextRegionType r = textRegions.get(j);
			
//			if (exportTags){
//				getTagsForShapeElement(r);
//			}
			
			List<TextLineType> lines = r.getTextLine();
			RtfText[] linesTexts = new RtfText[lines.size()];
			for (int i=0; i<lines.size(); ++i) {
				TrpTextLineType trpL = (TrpTextLineType) lines.get(i);
								
				linesTexts[i] = (wordBased && trpL.getWord().size()>0) ? getRtfTextForLineFromWords(trpL) : getRtfTextForShapeElement(trpL);
				linesTexts[i] = RtfText.text(linesTexts[i], "\n");
			}
			
			
			//read from right to left -> alignment is right
			if (rtl){
				//paras[j] = RtfPara.p(linesTexts).footnote("Test").alignRight();
			}
			else{
				String test = "test";
				paras[j] = RtfPara.p(linesTexts);
				//paras[j] = RtfPara.p(linesTexts, RtfText.footnote("Test")).alignLeft();
			}
			
		}
		
		return paras;
		
//		Rtf rtf = Rtf.rtf().section(paras);
//		return rtf;
		
//		for (RegionType r : trpPage.getTextRegionOrImageRegionOrLineDrawingRegion()) {
//			if (r instanceof GraphicRegionType) {
//				GraphicRegionType gr = (GraphicRegionType) r;
//				// TODO: how to export images in pdf??
//				r.getTextRegions(recursive);		
//			}
//		}
	
		
//		tr.getPage().getTextRegions(recursive);
				
//		Rtf.rtf();
//		RtfWriter;
		
		
	}
	

	public static void writeRtfForDoc(TrpDoc doc, boolean wordBased, boolean writeTags, boolean doBlackening, File file, Set<Integer> pageIndices, IProgressMonitor monitor, Set<String> selectedTags) throws JAXBException, IOException {
		
		exportTags = writeTags;
		tagnames = selectedTags;
		TrpRtfBuilder.doBlackening = doBlackening;
		
		/*
		 * get all names of tags
		 */
		//tagnames = CustomTagFactory.getRegisteredTagNames();
		
		Rtf rtf = Rtf.rtf();	
		List<TrpPage> pages = doc.getPages();
		
		int totalPages = pageIndices==null ? pages.size() : pageIndices.size();
		if (monitor!=null) {
			monitor.beginTask("Exporting to RTF", totalPages);
		}
		
		int c=0;
		for (int i=0; i<pages.size(); ++i) {
			if (pageIndices!=null && !pageIndices.contains(i))
				continue;
			
			if (monitor!=null) {
				if (monitor.isCanceled()) {
					logger.debug("RTF export cancelled!");
					return;
				}
				monitor.subTask("Processing page "+(c+1));
			}
			TrpPage page = pages.get(i);
			TrpTranscriptMetadata md = page.getCurrentTranscript();
			JAXBPageTranscript tr = new JAXBPageTranscript(md);
			tr.build();			
			
			TrpPageType trpPage = tr.getPage();
			
			logger.debug("writing rtf for page "+(i+1)+"/"+doc.getNPages());
//			rtf().header(color( 204, 0, 0 ).at( 0 ),
//				    color( 0, 0xff, 0 ).at( 1 ),
//				    color( 0, 0, 0xff ).at( 2 ),
//				    font( "Calibri" ).at( 0 ) );
			
			//RtfHeaderColor color = RtfHeaderColor.color(0xff, 0, 0);
				    
			rtf.header(color( 204, 0, 0 ).at( 0 ),color( 0, 0xff, 0 ).at( 1 )).section(getRtfParagraphsForTranscript(trpPage, wordBased));
			

			
			++c;
			if (monitor!=null) {
				monitor.worked(c);
			}
		}
		
		//write tags at end of last page
		if (exportTags){
			//RtfText headline = RtfText.text("Person names in this document (amount of found persons: " + persons.size() + ")", "\n");
			
			
			/*
			 * for all different tagnames:
			 * find all custom tags in doc
			 * create list and 
			 */
			
			ArrayList<RtfPara> tagParas = new ArrayList<RtfPara>();
			//tagnames = all user choosen tags via export dialog
			for (String currTagname : tagnames){
				//logger.debug("curr tagname " + currTagname);
				//get all custom tags with currTagname and text
				HashMap<CustomTag, String> allTagsOfThisTagname = ExportUtils.getTags(currTagname);
				if(allTagsOfThisTagname.size()>0){
					tagParas.add(RtfPara.p(RtfText.text(RtfText.underline(currTagname + " tags in this document: " + allTagsOfThisTagname.size()))));
					//ArrayList<RtfText> tagTexts = new ArrayList<RtfText>(); 
					Collection<String> valueSet = allTagsOfThisTagname.values();
					RtfText[] tagTexts = new RtfText[valueSet.size()];
					int l = 0;
					for (String currEntry : valueSet){
						tagTexts[l++] = RtfText.text(currEntry.concat("\n"));
						//logger.debug("tag value is " + currEntry);
					}
					tagParas.add(RtfPara.p(tagTexts));
				}
				
			}

	
//			int parSize = getParsNumber();
//			int k = 0;
//
//			if (persons.size() > 0){
//				logger.debug("k is " + k);
//				List<String> newPersonList = new ArrayList<String>(new HashSet<String>(persons));
//				tagParas[k++]=RtfPara.p(RtfText.text("Person names in this document (amount of found persons: " + newPersonList.size() + ")", "\n"));
//				logger.debug("k is " + k);
//				//rtf.p("Person names in this document (amount of found persons: " + persons.size() + ")", "\n");
//				//to make the list contain only unique values
//				
//				RtfText[] personTexts = new RtfText[newPersonList.size()];
//				for (int j=0; j<newPersonList.size(); ++j) {
//					personTexts[j] = RtfText.text(newPersonList.get(j), "\n");
//					logger.debug("person is " + newPersonList.get(j));
//				}
//				tagParas[k++] = RtfPara.p(personTexts);
//			}
//			
//			if (places.size() > 0){
//				List<String> newPlaceList = new ArrayList<String>(new HashSet<String>(places));
//				tagParas[k++]=RtfPara.p(RtfText.text("Places in this document (amount of found places " + newPlaceList.size() + ")", "\n"));
//				
//				RtfText[] placeTexts = new RtfText[newPlaceList.size()];
//				for (int j=0; j<newPlaceList.size(); ++j) {
//					//RtfText.color(0, "red");
//					placeTexts[j] = RtfText.color(0, newPlaceList.get(j).concat("\n"));
//					logger.debug("place is " + newPlaceList.get(j));
//				}
//				RtfPara par2 = RtfPara.p(placeTexts);
//				tagParas[k++] = par2;
//			}
//			
//			if(addresses.size() > 0){
//				List<String> newAddressList = new ArrayList<String>(new HashSet<String>(addresses));
//				tagParas[k++]=RtfPara.p(RtfText.text("Addresses in this document (amount of found addresses " + newAddressList.size() + ")", "\n"));
//				
//				RtfText[] addresseTexts = new RtfText[newAddressList.size()];
//				for (int j=0; j<newAddressList.size(); ++j) {
//					addresseTexts[j] = RtfText.text(newAddressList.get(j), "\n");
//					logger.debug("addresse is " + newAddressList.get(j));
//				}
//				RtfPara par3 = RtfPara.p(addresseTexts);
//				tagParas[k++] = par3;
//			}
			//rtf.section(par3);
			rtf.header(color( 204, 0, 0 ).at( 0 )).section(tagParas);
		}
		
		rtf.out( new FileWriter(file) );
		logger.info("wrote rtf to: "+file.getAbsolutePath());
	}
	
//	private static HashMap<CustomTag, String> getTags(String currTagname) {
//		
//		HashMap<CustomTag, String> resultTags = new HashMap<CustomTag, String>();
//		for (Map.Entry<CustomTag, String> currEntry : tags.entrySet()){
//			//logger.debug("current tag name "+currEntry.getKey().getTagName());
//			if(currEntry.getKey().getTagName().equals(currTagname)){
//				logger.debug("current tag name found "+currEntry.getKey().getTagName());
//				resultTags.put(currEntry.getKey(), currEntry.getValue());
//			}
//		}
//		return resultTags;
//	}

	private static int getParsNumber() {
		int nr = 0;
		if (persons.size() > 0){
			nr += 2;
		}
		if (places.size() > 0){
			nr += 2;
		}
		return nr;
	}

	public static void writeRtfForDoc(TrpDoc doc, boolean wordBased, File file, Set<Integer> pageIndices, IProgressMonitor monitor) throws JAXBException, IOException {
		Rtf rtf = Rtf.rtf();	
		List<TrpPage> pages = doc.getPages();
		
		int totalPages = pageIndices==null ? pages.size() : pageIndices.size();
		if (monitor!=null) {
			monitor.beginTask("Exporting to RTF", totalPages);
		}
		
		int c=0;
		for (int i=0; i<pages.size(); ++i) {
			if (pageIndices!=null && !pageIndices.contains(i))
				continue;
			
			if (monitor!=null) {
				if (monitor.isCanceled()) {
					logger.debug("RTF export cancelled!");
					return;
				}
				monitor.subTask("Processing page "+(c+1));
			}
			TrpPage page = pages.get(i);
			TrpTranscriptMetadata md = page.getCurrentTranscript();
			JAXBPageTranscript tr = new JAXBPageTranscript(md);
			tr.build();			
			TrpPageType trpPage = tr.getPage();
			
			logger.debug("writing rtf for page "+(i+1)+"/"+doc.getNPages());
			rtf.section(getRtfParagraphsForTranscript(trpPage, wordBased));
					
			++c;
			if (monitor!=null) {
				monitor.worked(c);
			}
		}
		
		rtf.out( new FileWriter(file) );
		logger.info("wrote rtf to: "+file.getAbsolutePath());
	}

	public static void main(String[] args) throws IOException, JAXBException {
		
		
		TrpDoc doc = LocalDocReader.load("/media/dea_scratch/TRP/Schauplatz_small/");
//		TrpPage page = doc.getPages().get(0);
//		TrpTranscriptMetadata md = page.getCurrentTranscript();
		
		writeRtfForDoc(doc, false, new File("test_rtf.rtf"), null, null);
		
//		Rtf.rtf().p("helo");
//		italic( underline( "italic"+bold( "with something in bold" )+" underline" ) );
		
//		String str = "abcd";
//		for (int i=0; i<str.length(); ++i) {
//			System.out.println(str.substring(i, i+1));
//		}
		
//		rtf().section(
//				   p( "first paragraph" ),
//				   p("whatever"),
//				   p( tab(),
//				      " second par ",
//				      italic( underline( "italic") ).bold( underline("with somethinnng in bold") ),
//				      text( " and " ),
//				      italic( underline( "italic underline" ) )     
//				    )  
//				).out( new FileWriter("testout.rtf") );
		System.out.println("finished");
	}

}
