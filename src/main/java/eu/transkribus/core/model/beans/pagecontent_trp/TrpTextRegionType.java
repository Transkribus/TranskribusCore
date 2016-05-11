package eu.transkribus.core.model.beans.pagecontent_trp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.CustomTagUtil;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.CoordsType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObservable;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpChildrenClearedEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpConstructedWithParentEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpCoordsChangedEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpReinsertIntoParentEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpRemovedEvent;
import eu.transkribus.core.util.BeanCopyUtils;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.PointStrUtils;
import eu.transkribus.core.util.TextStyleTypeUtils;

public class TrpTextRegionType extends TextRegionType implements ITrpShapeType {
	final static Logger logger = LoggerFactory.getLogger(TrpTextRegionType.class);
	
	TrpObservable observable = new TrpObservable(this);
//	TrpPageType page=null;
//	Object data;
	
	public TrpTextRegionType() {
		super();
	}
	public TrpTextRegionType(TrpPageType page) {
		this();
		setParent(page);
		observable.setChangedAndNotifyObservers(new TrpConstructedWithParentEvent(this));
	}
	
	/** Copy constructor - NOTE: the contained lines are <b>not</b> deep copied, only their references! */
	public TrpTextRegionType(TrpTextRegionType src) {
		super();
		
		copyFields(src);
	}
	
	@Override
	public TrpTextRegionType copy() { return new TrpTextRegionType(this); }
	
	@Override public void copyFields(ITrpShapeType srcShape) {
		if (!(srcShape instanceof TrpTextRegionType))
			return;
		
		TrpTextRegionType src = (TrpTextRegionType) srcShape;
		
		// copy text first, s.t. customtaglist is inited properly inside RegionType.copyFields!
		textEquiv = BeanCopyUtils.copyTextEquivType(src.textEquiv);
		
		super.copyFields(srcShape);
//	    id = TrpPageType.getUniqueId("region");
//
//		// copy base fields:
//		coords = BeanCopyUtils.copyCoordsType(src.coords);
//		// FIXME: should be null always --> error in JAXB generator??
//	    textRegionOrImageRegionOrLineDrawingRegion = null;
	    
//	    if (src.getCustomTagList() != null)
//	    	src.getCustomTagList().writeToCustomTag();
//	    if (src.custom != null)
//	    	custom = new String(src.custom);
//	    
//	    if (src.comments != null)
//	    	comments = new String(src.comments);
		
		// specific to text regions:
		if (src.textLine!=null)
			textLine = new ArrayList<>(src.textLine);
		
		textStyle = BeanCopyUtils.copyTextStyleType(src.textStyle);
		orientation = src.orientation;
		type = src.type;
		leading = src.leading;
		readingDirection = src.readingDirection;
		readingOrientation = src.readingOrientation;
		indented = src.indented;
		align = src.align;
		primaryLanguage = src.primaryLanguage;
		secondaryLanguage = src.secondaryLanguage;
		primaryScript = src.primaryScript;
		secondaryScript = src.secondaryScript;
		production = src.production;
		
		
		
		// copy new fields:
//		page = src.page;
//		data = src.data;
//		customTagList = new CustomTagList(this);
	}	
	
//	@Override public void setCustom(String custom) {
//		this.custom = custom;
//		customTagList = new CustomTagList(this);
//	}
	
//	@Override public void setId(String id) {
//		this.id = id; 
//	}
	
//	@Override
//	public Object getParent() { return getPage(); }
	
//	@Override
//	public void setParent(Object parent) {
//		this.setPage((TrpPageType)parent);
//	}	
	
	@Override
	public String getName() { return "TextRegion"; }
	
//	@Override
//	public int getLevel() { return 0; }

//	public void setPage(TrpPageType page) {
//		this.page = page;
//	}
//	public TrpPageType getPage() { return page; }
	
	public int getIndex() {
		return (getPage() != null) ? getPage().getTextRegionIndex(this) : -1;
	}
	
	public TrpTextRegionType getNeighborTextRegion(boolean previous) {
		if (getPage()==null)
			return this;
		
		int nRegions = getPage().getTextRegions(true).size();
		
		int index = (previous) ? getIndex()-1 : getIndex()+1;
		
		if (index < 0) {
			index = nRegions-1;
		}
		else if (index >= nRegions) {
			index = 0;
		}
		
		return getPage().getTextRegions(true).get(index);	
	}
	
	
	public void applyTextFromWords() {
		// first apply from words to all lines
		for (TextLineType tl : getTextLine()) {
			((TrpTextLineType)tl).applyTextFromWords();
			
		}
		// then apply from lines to region:
		applyTextFromLines();
	}
		
	public boolean isLinesInSyncWithWordsText() {
//		logger.debug("diff word/line text = "+StringUtils.difference(getTextFromWords(false), getTextFromLines()));
		
		return getTextFromWords(false).equals(getRegionTextFromLines());
	}
	
	public String getRegionTextFromLines() {
		String textAll = "";
		
		for (TextLineType l : getTextLine()) {
			TrpTextLineType trp_l = (TrpTextLineType) l;
			textAll +=  trp_l.getUnicodeText()+"\n";
		}
		textAll = StringUtils.removeEnd(textAll, "\n");
		
		return textAll;
	}
	
	public List<String> getLinesTextList() {
		List<String> linesText = new ArrayList<>();
		for (TextLineType l : getTextLine()) {
			linesText.add(((TrpTextLineType)l).getUnicodeText());
		}
		return linesText;
	}
	
	public String getTextFromWords(boolean fillEmptyWords) {
		String textAll = "";
		
		for (TextLineType l : getTextLine()) {
			TrpTextLineType trp_l = (TrpTextLineType)l;
			textAll += trp_l.getTextFromWords(fillEmptyWords)+"\n";
		}
		textAll = StringUtils.removeEnd(textAll, "\n");
		
		return textAll;
	}	
	
	public void clearTextForAllLines(Object who) {
		for (TextLineType tl : getTextLine()) {
			TrpTextLineType trpTl = (TrpTextLineType) tl;
			trpTl.setUnicodeText("", who);
		}
	}
	
	public void clearTextForAllWordsinLines(Object who) {
		for (TextLineType tl : getTextLine()) {
			TrpTextLineType trpTl = (TrpTextLineType) tl;
			trpTl.clearTextForAllWords(who);
		}
	}
	
	public void applyTextFromLines() {
		setUnicodeText(getUnicodeTextFromLines(), this);
	}
	
	public String getUnicodeTextFromLines() {
		String text="";
		for (TextLineType tl : getTextLine()) {
			text += ((TrpTextLineType)tl).getUnicodeText() + "\n";
		}
		if (text.endsWith("\n")) text = text.substring(0, text.length()-1);
		
		return text;
	}
	
	@Override public void sortChildren(boolean recursive) {
		sortLines();
		if (recursive) {
			for (TextLineType tl : getTextLine()) {
				((TrpTextLineType)tl).sortChildren(recursive);
			}
		}
	}
	
	public void sortLines() {
//		Collections.sort(getTextLine(), new TrpElementCoordinatesComparator<TextLineType>());
//		for (int i = 0; i<getTextLine().size(); i++){
//		logger.debug("i line in text " + getTextLine().get(i).getId());	
//		}
		
		boolean doSort = false;
		List<TextLineType> textLines = getTextLine();
		
		//sort only if reading order is not set actually
		for (int i=0; i<textLines.size(); i++) {
			ITrpShapeType o = (ITrpShapeType) textLines.get(i);
			if (o.getReadingOrder() == null){
				doSort = true;
				break;
			}
		}
		
		if (doSort){
			Collections.sort(textLines, new TrpElementReadingOrderComparator<TextLineType>(true));
		}
		
		for (int i=0; i<textLines.size(); i++) {
			TrpTextLineType line = (TrpTextLineType) textLines.get(i);
			//logger.debug("line " + i + " line.getid " + line.getId() + " + line.getReadingOrder " + line.getReadingOrder());
			if (line.getReadingOrder() == null || !line.getReadingOrder().equals(i)){
				//logger.debug("line " + i + " + line.getReadingOrder " + line.getReadingOrder());
				line.setReadingOrder(i, this);
				//logger.debug("line " + i + " with id " + line.getId() + " + line.setReadingOrder " + i);
			}
		}
		
	}
	
	public int getIndexAccordingToCoordinates(ITrpShapeType o1){
		
		for (int i=0; i<textLine.size(); i++) {
			ITrpShapeType o2 = (ITrpShapeType) getTextLine().get(i);
			
//			logger.debug("o1 coords " + o1.getCoordinates());
//			logger.debug("o2 coords " + o2.getCoordinates());
			
			int ordering = new TrpElementCoordinatesComparator().compare(o1, o2);
			
			//o1 is smaller the second shape according to their coordinates
			if (ordering < 0){
				//logger.debug(" o1 smaller o2 ");
				//i is than the index in the parent shape to insert the child shape
				return i;
			}
						
		}
		//logger.debug(" no smaller shape found: return last index " + (textLine.size()-1));
		//int idx = (textLine.size()>0? (textLine.size()):0);

		return textLine.size();
	}
	
	
	
	public int getLineIndex(TrpTextLineType line) {
		int i=0;
		for (TextLineType tl : getTextLine()) {
			if (tl == line)
				return i;
			++i;
		}
		return -1;
	}
		
	@Override
	public void setCoordinates(String value, Object who) {
		CoordsType coords = new CoordsType();
		coords.setPoints(value);
		setCoords(coords);
		
		observable.setChangedAndNotifyObservers(new TrpCoordsChangedEvent(who));
	}
		
	@Override
	public String getCoordinates() {
		return getCoords().getPoints();
	}	
	
//	@Override
//	public void reInsertIntoParent() {
//		reInsertIntoParent(-1);
//	}	
//	
//	@Override
//	public void reInsertIntoParent(int index) {
//		if (!getPage().getTextRegionOrImageRegionOrLineDrawingRegion().contains(this)) {
//			CoreUtils.addOrAppend(getPage().getTextRegionOrImageRegionOrLineDrawingRegion(), this, index);
//			getPage().sortRegions();
//			observable.setChangedAndNotifyObservers(new TrpReinsertIntoParentEvent(this));
//		}
//		
//	}
	
	@Override
	public void removeFromParent() {
//		System.out.println("removing from parent before: "+getPage().getTextRegionOrImageRegionOrLineDrawingRegion().size());
		getPage().getTextRegionOrImageRegionOrLineDrawingRegion().remove(this);
//		System.out.println("removing from parent after: "+getPage().getTextRegionOrImageRegionOrLineDrawingRegion().size());
		observable.setChangedAndNotifyObservers(new TrpRemovedEvent(this));
	}
	
	@Override public void removeChildren() {
		super.removeChildren();
		
		getTextLine().clear();
		observable.setChangedAndNotifyObservers(new TrpChildrenClearedEvent(this));
	}
	
	@Override
	public List<ITrpShapeType> getChildren(boolean recursive) {
		ArrayList<ITrpShapeType> c = new ArrayList<ITrpShapeType>();
		for (TextLineType tl : this.getTextLine()) {
			c.add((TrpTextLineType)tl);
			if (recursive) {
				for (ITrpShapeType o : ((TrpTextLineType)tl).getChildren(recursive)) {
					c.add(o);
				}
			}
		}
		
		return c;
	}
	
	@Override public boolean hasChildren() { return !getTextLine().isEmpty(); }
	
	@Override public String getUnicodeText() {
		return (getTextEquiv()!=null&&getTextEquiv().getUnicode()!=null) ? getTextEquiv().getUnicode() : "";
	}
			
	@Override public void setUnicodeText(String unicode, Object who) {		
		TrpShapeTypeUtils.setUnicodeText(this, unicode, who);	    
	}
	
	@Override public void editUnicodeText(int start, int end, String replacement, Object who) {
		TrpShapeTypeUtils.editUnicodeText(this, start, end, replacement, who);
	}
	
//	@Override
//	public Object getData() { return data; }
//	@Override
//	public void setData(Object data) { this.data = data; }
	
//	@Override 
//	public ITrpShapeType getParentShape() { return null; }
	
	@Override public ITrpShapeType getSiblingShape(boolean previous) {
		if (getPage()==null)
			return null;
		if (previous && getIndex()==0)
			return null;
		if (!previous && getIndex()==(getPage().getTextRegions(true).size()-1) )
			return null;
		else
			return getNeighborTextRegion(previous);
	}
	
	@Override public void addTextStyleTag(TextStyleTag s, String addOnlyThisProperty, /*boolean recursive,*/ Object who) {
		TextStyleTypeUtils.addTextStyleTag(this, s, addOnlyThisProperty, /*recursive,*/ who);
	}

	@Override public List<TextStyleTag> getTextStyleTags() {
		return customTagList.getIndexedTags(TextStyleTag.TAG_NAME);
	}
	
	@Override
	public void setTextStyle(TextStyleType s, boolean recursive, Object who) {
		TextStyleTypeUtils.setTextStyleTag(this, s, recursive, who);
	}
	
	@Override public TextStyleType getTextStyle() { return textStyle; }
	
//	@Override public void setStructure(String structureType, boolean recursive, Object who) {
//		CustomTagUtil.setStructure(this, structureType, recursive, who);
//	}
//	
//	@Override public String getStructure() {
//		return CustomTagUtil.getStructure(this);
//	}
	
//	@Override public void translate(int x, int y) throws Exception { 
//		setCoordinates(PrimaUtils.translatePoints(getCoordinates(), x, y), this);
//	}
//	
//	@Override public void rotate(double degrees) throws Exception {
//		setCoordinates(PrimaUtils.rotatePoints(getCoordinates(), Math.toRadians(degrees)), this);
//	}
	
//	@Override public CustomTagList getCustomTagList() { return customTagList; }
	
//	@Override public void setReadingOrder(Integer readingOrder, Object who) {
//		CustomTagUtil.setReadingOrder(this, readingOrder, who);
//	}
//	@Override public Integer getReadingOrder() {
//		return CustomTagUtil.getReadingOrder(this);
//	}
	
	// OBSERVABLE STUFF:
//	@Override public TrpObservable getObservable() { return observable; }
//  @Override public void addObserver(Observer o) { observable.addObserver(o); }
//  @Override public void deleteObserver(Observer o) { observable.deleteObserver(o); }
//  @Override public void deleteObservers() { observable.deleteObservers(); }
    
	@Override public String print() {
	    return "TrpTextRegionType: id = "+getId()+", text = "+getUnicodeText()
	    		+", level = "+getLevel()+", parent = "+getParent()+", nChildren = "+getChildren(false).size();
	}
    


}
