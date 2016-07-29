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
import eu.transkribus.core.model.beans.pagecontent.TextEquivType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObservable;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpChildrenClearedEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpConstructedWithParentEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpCoordsChangedEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpReadingOrderChangedEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpReinsertIntoParentEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpRemovedEvent;
import eu.transkribus.core.util.BeanCopyUtils;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.PointStrUtils;
import eu.transkribus.core.util.TextStyleTypeUtils;

/**
 * Custom TextLineType object that adds Observable behaviour when changing significant values such as text or coordinates.
 */
public class TrpTextLineType  extends TextLineType implements ITrpShapeType {
	private final static Logger logger = LoggerFactory.getLogger(TrpTextLineType.class);
	
	TrpObservable observable = new TrpObservable(this);
	TrpTextRegionType region;
	Object data;
	List<TaggedWord> taggedWords = new ArrayList<>();
	CustomTagList customTagList;
	
	public TrpTextLineType() {
		super();
		customTagList = new CustomTagList(this);
		updateTaggedWords();
	}
	
	public TrpTextLineType(TrpTextRegionType region) {
		this();
		setRegion(region);
		observable.setChangedAndNotifyObservers(new TrpConstructedWithParentEvent(this));
	}
	
	/** Copy constructor - NOTE: the contained words are <b>not</b> deep copied, only their references! */
	public TrpTextLineType(TrpTextLineType src) {
		copyFields(src);
	}
	
	@Override
	public TrpTextLineType copy() { return new TrpTextLineType(this); }	
	
	@Override public void setCustom(String custom) {
		// TODO: catch exceptions (index out of bounds!)
//		logger.debug("setting custom tag to: "+custom);
		this.custom = custom;
		customTagList = new CustomTagList(this);
	}
	
	@Override public void copyFields(ITrpShapeType srcShape) {
		if (!(srcShape instanceof TrpTextLineType))
			return;
		
		TrpTextLineType src = (TrpTextLineType) srcShape;
		
		// set new id:
//	    id = getName()+"_"+System.currentTimeMillis();
	    id = TrpPageType.getUniqueId("line");
		
		// copy base fields:
		if (src.coords!=null)
			coords = BeanCopyUtils.copyCellCoordsType(src.coords);
	    if (src.baseline!=null)
	    	baseline = new TrpBaselineType((TrpBaselineType) src.baseline);
	    if (src.word!=null)
	    	word = new ArrayList<>(src.word);
	    textEquiv = BeanCopyUtils.copyTextEquivType(src.textEquiv);
	    textStyle = BeanCopyUtils.copyTextStyleType(src.textStyle);
	    primaryLanguage = src.primaryLanguage;
	    production = src.production;
	    
	    src.getCustomTagList().writeToCustomTag();
	    if (src.custom != null)
	    	custom = new String(src.custom);
	    if (src.comments != null)
	    	comments = new String(src.comments);
	    
	    // copy new fields:
		region = src.region;
		data = src.data;
		taggedWords = new ArrayList<>(src.taggedWords);
		
		customTagList = new CustomTagList(this);
	}	
	
	@Override public void setId(String id) { this.id = id; }
	
	@Override
	public Object getParent() { return getRegion(); }
	
	@Override
	public void setParent(Object parent) {
		this.setRegion((TrpTextRegionType)parent);
	}
	
	@Override public String getName() { return "Line"; }
	
//	@Override public int getLevel() { return 1; }
	@Override public int getLevel() { return ITrpShapeType.LINE_BASE_LEVEL; }

	public void setRegion(TrpTextRegionType region) {
		this.region = region;
	}
	
	public TrpTextRegionType getRegion() { return region; }
	
	public int getIndex() { return region.getLineIndex(this); }
		
	@Deprecated
	public List<TaggedWord> getTaggedWords() {
		return taggedWords;
	}
	
	public void clearTextForAllWords(Object who) {
		for (WordType w : getWord()) {
			TrpWordType tw = (TrpWordType) w;
			tw.setUnicodeText("", who);
		}
//		applyTextFromWords();
	}
		
	public void updateTaggedWords() {
		taggedWords = LineTags.getTaggedWords(this);
	}
		
	public TrpPageType getPage() {
		return getRegion().getPage();
	}
	
	public int getWordCount() { return getWord().size(); }
	
	public TrpWordType getWord(int index) {
		return (TrpWordType) getWord().get(index);
	}
	
	public TrpTextLineType getNeighborLine(boolean previous, boolean overAllRegions) {		
		int nLines = region.getTextLine().size();
		
		int index = (previous) ? getIndex()-1 : getIndex()+1;
		// find sibling region:
		TrpTextRegionType neighborRegion = region;
		if (overAllRegions && (index<0 || index>=nLines) ) {
			do {
				neighborRegion = neighborRegion.getNeighborTextRegion(previous);
			} while (neighborRegion.getTextLine().size() == 0 /*|| neighborRegion == region*/);
		}
		
		if (index < 0) {
			if (overAllRegions) {
				return (TrpTextLineType) neighborRegion.getTextLine().get(neighborRegion.getTextLine().size()-1);
			}
			else
				index = nLines-1;
		}
		else if (index >= nLines) {
			if (overAllRegions) {
				return (TrpTextLineType) neighborRegion.getTextLine().get(0);
			}
			else
				index = 0;
		}
		
		return (TrpTextLineType) region.getTextLine().get(index);
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
	
	@Override
	public void reInsertIntoParent() {
		reInsertIntoParent(-1);
	}
	
	@Override
	public void reInsertIntoParent(int index) {
		if (!getRegion().getTextLine().contains(this)) {
			CoreUtils.addOrAppend(getRegion().getTextLine(), this, index);
			getRegion().sortLines();
			getRegion().applyTextFromLines();
			observable.setChangedAndNotifyObservers(new TrpReinsertIntoParentEvent(this));
			
		}
	}
	
//	@Override
//	public void reInsertIntoParent(Integer index) {
//		if (!getRegion().getTextLine().contains(this)) {
//			if (index==null || index<0 || index >= getRegion().getTextLine().size()) {
//				getRegion().getTextLine().add(this);
//			} else {
//				getRegion().getTextLine().add(index, this);
//			}
//			
//			getRegion().sortLines();
//			getRegion().applyTextFromLines();
//			observable.setChangedAndNotifyObservers(new TrpReinsertIntoParentEvent(this));
//		}
//	}
	
	@Override
	public void swap(int direction){
		int i = this.getIndex();
		
		if (direction == 0){
			if (i>0){
					
				Collections.swap(getRegion().getTextLine(), i, i-1);
				
				setReadingOrder(getReadingOrder()-1, TrpTextLineType.class);
				//getRegion().getChildren(false).get(i-1).setReadingOrder(getReadingOrder()+1, TrpTextLineType.class);
				//getRegion().sortLines();
				observable.setChangedAndNotifyObservers(new TrpReadingOrderChangedEvent(this));
				
			}
		
		}
		else if (direction == 1){
			
			if (i<(getRegion().getTextLine().size()-1)){
				
				Collections.swap(getRegion().getTextLine(), i, i+1);
				//getRegion().getChildren(false).get(i+1).setReadingOrder(getReadingOrder()+1, TrpTextLineType.class);
				setReadingOrder(getReadingOrder()+1, TrpTextLineType.class);
//				getRegion().sortLines();
				observable.setChangedAndNotifyObservers(new TrpReadingOrderChangedEvent(this));
				
			}
		}
	}
	
	@Override
	public List<ITrpShapeType> getChildren(boolean recursive) {
		ArrayList<ITrpShapeType> c = new ArrayList<ITrpShapeType>();
		// add baseline:
		if (getBaseline() != null)
			c.add((TrpBaselineType)getBaseline());
		// add words:
		for (WordType w : getWord()) {
			c.add((TrpWordType)w);
		}

		return c;
	}
	
	@Override public boolean hasChildren() { 
		return getBaseline()!=null || !getWord().isEmpty();
	}
	
	@Override public void setUnicodeText(String unicode, Object who) {
		TrpShapeTypeUtils.setUnicodeText(this, unicode, who);
		
//		int lBefore = getUnicodeText().length();
//		TextEquivType te = new TextEquivType();
//		te.setUnicode(unicode);
//	    setTextEquiv(te);
//	    customTagList.onTextEdited(0, lBefore, unicode);
//		observable.setChangedAndNotifyObservers(new TrpTextChangedEvent(who, 0, lBefore, unicode));
							    
		// apply changes to region:
		if (region!=null) region.applyTextFromLines();	    
	}
	
	@Override public void editUnicodeText(int start, int end, String replacement, Object who) {
		TrpShapeTypeUtils.editUnicodeText(this, start, end, replacement, who);
		
//		logger.trace("editing unicode text in line: "+start+"/"+end+" text = "+replacement+" id = "+getId());
//		StringBuilder sb = new StringBuilder(getUnicodeText());
//		sb.replace(start, end, replacement);
//		
//		TextEquivType te = new TextEquivType();
//		te.setUnicode(sb.toString());
//	    setTextEquiv(te);
//	    
//	    customTagList.onTextEdited(start, end, replacement);
//		observable.setChangedAndNotifyObservers(new TrpTextChangedEvent(who, start, end, replacement));
	    
		// apply changes to region:		
		if (region!=null) region.applyTextFromLines();
	}
	
	/**
	 * Returns the  embedded Unicode text of this line or an empty string if nothings there
	 */
	public String getUnicodeText() {
		return (getTextEquiv()!=null&&getTextEquiv().getUnicode()!=null) ? getTextEquiv().getUnicode() : "";
	}	
	

	@Override public void sortChildren(boolean recursive) {
		sortWords();
	}
	
	public void sortWords() {
//		Collections.sort(getWord(), new TrpElementCoordinatesComparator<WordType>());
		boolean doSort = false;
		List<WordType> words = getWord();
		
		//sort only if reading order is not set actually
		for (int i=0; i<words.size(); i++) {
			ITrpShapeType o = (ITrpShapeType) words.get(i);
			if (o.getReadingOrder() == null){
				doSort = true;
				break;
			}
		}
		
		if (doSort){
				Collections.sort(words, new TrpElementReadingOrderComparator<WordType>(true));
		}

		for (int i=0; i<word.size(); ++i) {
			TrpWordType w = (TrpWordType) word.get(i);
			if (w.getReadingOrder() == null || !w.getReadingOrder().equals(i)){
				w.setReadingOrder(i, this);
			}
		}
	}
	
	public int getWordIndex(TrpWordType word) {
		int i=0;
		for (WordType w : getWord()) {
			if (w == word)
				return i;
			++i;
		}
		return -1;
	}	
	

	
//	@Override
    public void setTextEquiv(TextEquivType value) {
		super.setTextEquiv(value);
		

    }
	
	@Override
	public void removeFromParent() {
		getRegion().getTextLine().remove(this);
		getRegion().applyTextFromLines();
		observable.setChangedAndNotifyObservers(new TrpRemovedEvent(this));
	}
	
	@Override public void removeChildren() {
		baseline = null;
		word.clear();
		observable.setChangedAndNotifyObservers(new TrpChildrenClearedEvent(this));
	}
	
	@Override
	public Object getData() { return data; }
	@Override
	public void setData(Object data) { this.data = data; }
	
    
	public TrpWordType getWordWithId(String wordRegionId) {
		for (WordType w : getWord()) {
			if (w.getId().equals(wordRegionId))
				return (TrpWordType) w;
		}
		return null;
	}
	
	public boolean isInSyncWithWordsText() {
		return getTextFromWords(false).equals(getUnicodeText());
	}
	
	public void applyTextFromWords() {
		setUnicodeText(getTextFromWords(false), this);
	}
	
	public String getTextFromWords(boolean fillEmptyWords) {
		String text="";
		for (WordType w : getWord()) {
			String wt = ((TrpWordType)w).getUnicodeText();
			if (fillEmptyWords && wt.isEmpty())
				wt = TrpWordType.EMPTY_WORD_FILL;
			
			text += wt + " ";
		}
		text = StringUtils.removeEnd(text, " ");
				
		return text;
	}	
	
	@Override 
	public ITrpShapeType getParentShape() { return getRegion(); }
	
	@Override public ITrpShapeType getSiblingShape(boolean previous) { 
		logger.trace("getting sibling shape, index = "+getIndex());
		if (region==null)
			return null;
		if (previous && getIndex()==0)
			return null;
		if (!previous && getIndex()==(region.getTextLine().size()-1) )
			return null;
		else {
			return getNeighborLine(previous, false);
		}
	}
	
	@Override public void translate(int x, int y) throws Exception { 
		setCoordinates(PointStrUtils.translatePoints(getCoordinates(), x, y), this);
	}
	
	@Override public void rotate(double degrees) throws Exception {
		setCoordinates(PointStrUtils.rotatePoints(getCoordinates(), Math.toRadians(degrees)), this);
	}
	
//	// FIXME: custom tags
//	public List<StyleTag> getStyleTags() {		
//		List<StyleTag> sts = new ArrayList<>();
//		for (CustomTag c : getCustomTags()) {
//			if (c instanceof StyleTag)
//				sts.add((StyleTag)c);
//		}
//		return sts;
//	}
//	
//	// FIXME: custom tags
//	public CustomTagList getStyleList() {
//		CustomTagList sl = new CustomTagList();
//		for (StyleTag st : getStyleTags()) {
//			sl.addOrMergeTag(st);
//		}
//		return sl;
//	}
//	
//	// FIXME: custom tags 
//	public void addStyle(StyleTag st) {
//	}
	
	@Override public void addTextStyleTag(TextStyleTag s, String addOnlyThisProperty, /*boolean recursive,*/ Object who) {
		TextStyleTypeUtils.addTextStyleTag(this, s, addOnlyThisProperty, /*recursive,*/ who);
	}
	
	@Override public List<TextStyleTag> getTextStyleTags() {
//		return TextStyleTypeUtils.getTextStyleTagList(this);
		return customTagList.getIndexedTags(TextStyleTag.TAG_NAME);
	}
	
	@Override public void setTextStyle(TextStyleType s, boolean recursive, Object who) {
		TextStyleTypeUtils.setTextStyleTag(this, s, recursive, who);
	}
	
	@Override public TextStyleType getTextStyle() { 
		return (TextStyleType) textStyle;
	}
	
	@Override public void setStructure(String structureType, boolean recursive, Object who) {
		CustomTagUtil.setStructure(this, structureType, recursive, who);
		if (!recursive)
			CustomTagUtil.setStructure((TrpBaselineType) getBaseline(), structureType, recursive, who);
	}
	
	@Override public String getStructure() {
		return CustomTagUtil.getStructure(this);
	}
	
	@Override public CustomTagList getCustomTagList() { return customTagList; }
	
	@Override public void setReadingOrder(Integer readingOrder, Object who) {
		CustomTagUtil.setReadingOrder(this, readingOrder, who);
	}
	@Override public Integer getReadingOrder() {
		return CustomTagUtil.getReadingOrder(this);
	}

	// OBSERVABLE STUFF:
	public TrpObservable getObservable() { return observable; }
    public void addObserver(Observer o) { observable.addObserver(o); }
    public void deleteObserver(Observer o) { observable.deleteObserver(o); }
    public void deleteObservers() { observable.deleteObservers(); }

	public String print() {
		return "TrpTextLineType: id = " + getId() + ", text = " + getUnicodeText() + ", level = " + getLevel() + ", parent = " + getParent() + ", nChildren = "
				+ getChildren(false).size();
	}

	public ArrayList<ITrpShapeType> getChildrenWithoutBaseline() {

		ArrayList<ITrpShapeType> c = new ArrayList<ITrpShapeType>();
		// add words:
		for (WordType w : getWord()) {
			c.add((TrpWordType)w);
		}
		return c;
		
	}

}
