package eu.transkribus.core.model.beans.pagecontent_extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.io.GetBufferedRandomAccessSource;

import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.CustomTagUtil;
import eu.transkribus.core.model.beans.customtags.StructureTag;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.CoordsType;
import eu.transkribus.core.model.beans.pagecontent.TextEquivType;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.model.beans.pagecontent.TextTypeSimpleType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObservable;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpConstructedWithParentEvent;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpCoordsChangedEvent;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpReadingOrderChangedEvent;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpReinsertIntoParentEvent;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpRemovedEvent;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpStructureChangedEvent;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpTextChangedEvent;
import eu.transkribus.core.util.BeanCopyUtils;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.PrimaUtils;
import eu.transkribus.core.util.TextStyleTypeUtils;

public class TrpWordType extends WordType implements ITrpShapeType {
	private final static Logger logger = LoggerFactory.getLogger(TrpWordType.class);
	
	public final static String EMPTY_WORD_FILL = " ";
	
	TrpObservable observable = new TrpObservable(this);
	TrpTextLineType line;
	CustomTagList customTagList;
	
	Object data;

	public TrpWordType() {
		super();
		customTagList = new CustomTagList(this);	
	}
	
	public TrpWordType(TrpTextLineType line) {
		this();
		setLine(line);
		observable.setChangedAndNotifyObservers(new TrpConstructedWithParentEvent(this));
	}
	
	/** Copy constructor - NOTE: contained glyphs are <emph>not</emph> deep copied, only their references! */ 
	public TrpWordType(TrpWordType src) {
		super();
		
		copyFields(src);
	}
	
	@Override
	public TrpWordType copy() { return new TrpWordType(this); }	
	
	@Override public void setCustom(String custom) {
		// TODO: catch exceptions (index out of bounds!)
		this.custom = custom;
		customTagList = new CustomTagList(this);
	}
	
	@Override public void copyFields(ITrpShapeType srcShape) {
		if (!(srcShape instanceof TrpWordType))
			return;
		
		TrpWordType src = (TrpWordType) srcShape;
		
		// set new id:
//	    id = getName()+"_"+System.currentTimeMillis();
	    id = TrpPageType.getUniqueId("word");

		// copy base fields:
		if (src.coords != null)
			coords = BeanCopyUtils.copyCoordsType(src.coords);
		if (src.glyph!=null)
			glyph = new ArrayList<>(src.glyph);
	    textEquiv = BeanCopyUtils.copyTextEquivType(src.textEquiv);
	    textStyle = BeanCopyUtils.copyTextStyleType(src.textStyle);
	    language = src.language;
		production = src.production;
		
		src.getCustomTagList().writeToCustomTag();
	    if (src.custom != null)
	    	custom = new String(src.custom);
	    if (src.comments != null)
	    	comments = new String(src.comments);
		
		// copy new fields:
		line = src.line;
		data = src.data;
		customTagList = new CustomTagList(this);
	}	
	
	@Override public void setId(String id) { this.id = id; }
	
	@Override
	public Object getParent() { return getLine(); }
	
	@Override
	public void setParent(Object parent) {
		this.setLine((TrpTextLineType)parent);
	}
	
	@Override
	public String getName() { return "Word"; }
	
	@Override
	public int getLevel() { return 3; }
	
	public void setLine(TrpTextLineType line) {
		this.line = line;
	}
	public TrpTextLineType getLine() { return line; }	
	public int getIndex() { return line.getWordIndex(this); }
	
	public void applyTextToLine(boolean applyToRegion) {
		if (line != null) {
			line.applyTextFromWords();
			if (applyToRegion && line.getRegion()!=null) {
				line.getRegion().applyTextFromLines();
			}
		}
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
		if (!getLine().getWord().contains(this)) {
			CoreUtils.addOrAppend(getLine().getWord(), this, index);
			getLine().sortWords();
			observable.setChangedAndNotifyObservers(new TrpReinsertIntoParentEvent(this));
		}
	}
	
	@Override
	public void removeFromParent() {
		getLine().getWord().remove(this);
		observable.setChangedAndNotifyObservers(new TrpRemovedEvent(this));
	}
	
	@Override public void removeChildren() {}
	
	@Override
	public List<ITrpShapeType> getChildren(boolean recursive) {
		return new ArrayList<>();
	}
	
	@Override public boolean hasChildren() { return false; }
	
	@Override public void setUnicodeText(String unicode, Object who) {
		if (EMPTY_WORD_FILL.equals(unicode))
			unicode = "";
		
		TrpShapeTypeUtils.setUnicodeText(this, unicode, who);
		
//		int lBefore = getUnicodeText().length();
//		TextEquivType te = new TextEquivType();
//		te.setUnicode(unicode);
//	    this.setTextEquiv(te);
//	    
//	    customTagList.onTextEdited(0, lBefore, unicode);
//	    observable.setChangedAndNotifyObservers(new TrpTextChangedEvent(who, 0, lBefore, unicode));
	}
	
	@Override public void editUnicodeText(int start, int end, String replacement, Object who) {
		TrpShapeTypeUtils.editUnicodeText(this, start, end, replacement, who);
		
//		logger.debug("editing unicode text in word: "+start+"/"+end+" text = '"+replacement+"' id = "+getId());
//		StringBuilder sb = new StringBuilder(getUnicodeText());
//		sb.replace(start, end, replacement);
//		String unicode = sb.toString();
//		if (unicode.equals(EMPTY_WORD_FILL)) {
//			unicode = "";
//			replacement = "";
//		}
//		
//		TextEquivType te = new TextEquivType();
//		te.setUnicode(unicode);
//	    setTextEquiv(te);
//	    
//	    customTagList.onTextEdited(start, end, replacement);
////	    customTagList.onTextEdited(0, unicode.length(), unicode);
//		observable.setChangedAndNotifyObservers(new TrpTextChangedEvent(who, start, end, replacement));
	}
	
	public String getUnicodeText() {
		return (getTextEquiv()!=null&&getTextEquiv().getUnicode()!=null) ? getTextEquiv().getUnicode() : "";
	}	
	
	@Override
	public Object getData() { return data; }
	@Override
	public void setData(Object data) { this.data = data; }
	
	@Override
	public TrpPageType getPage() { return getLine().getPage(); }
	
	@Override 
	public ITrpShapeType getParentShape() { return getLine(); }
	
	@Override public ITrpShapeType getSiblingShape(boolean previous) {
		if (line==null)
			return null;
		if (previous && getIndex()==0)
			return null;
		if (!previous && getIndex()==(line.getWord().size()-1) )
			return null;
		else
			return getNeighborWord(previous, false, false);
	}	
	
	public TrpWordType getNeighborWord(boolean previous, boolean overAllLines, boolean overAllRegions) {		
		int nWords = line.getWordCount();
		
		int index = (previous) ? getIndex()-1 : getIndex()+1;
		// find sibling line:
		TrpTextLineType neighborLine = line;
		if (overAllLines && (index<0 || index>=nWords) ) {
			do {
				neighborLine = neighborLine.getNeighborLine(previous, overAllRegions);
			} while (neighborLine.getWord().size() == 0 /*|| neighborLine == line*/);
		}
		
		if (index < 0) {
			if (overAllLines) {
				return neighborLine.getWord(neighborLine.getWordCount()-1);
			}
			else
				index = nWords-1;
		}
		else if (index >= nWords) {
			if (overAllLines) {
				return neighborLine.getWord(0);
			}
			else
				index = 0;
		}
		
		return (TrpWordType) line.getWord().get(index);
	}
	
//	public TrpWordType getPreviousWord(boolean jumpToPreviousLine) {
//		int nWords = getLine().getWord().size();
//		int i = getLine().getWord().indexOf(this);
//
//		return (TrpWordType) getLine().getWord().get((i + 1) % nWords);
//	}
	
	@Override public void addTextStyleTag(TextStyleTag s, String addOnlyThisProperty, /*boolean recursive,*/ Object who) {
		TextStyleTypeUtils.addTextStyleTag(this, s, addOnlyThisProperty, /*recursive,*/ who);
	}
	
	@Override public List<TextStyleTag> getTextStyleTags() {
		return customTagList.getIndexedTags(TextStyleTag.TAG_NAME);
	}
	
	@Override public void setTextStyle(TextStyleType s, boolean recursive, Object who) {
		TextStyleTypeUtils.setTextStyleTag(this, s, recursive, who);
	}
	
	@Override public TextStyleType getTextStyle() { return textStyle; }
	
	@Override public void setStructure(String structureType, boolean recursive, Object who) {
		CustomTagUtil.setStructure(this, structureType, recursive, who);
	}
	
	@Override public String getStructure() {
		return CustomTagUtil.getStructure(this);
	}
	
	@Override public void translate(int x, int y) throws Exception { 
		setCoordinates(PrimaUtils.translatePoints(getCoordinates(), x, y), this);
	}
	
	@Override public void rotate(double degrees) throws Exception {
		setCoordinates(PrimaUtils.rotatePoints(getCoordinates(), Math.toRadians(degrees)), this);
	}
	
	@Override public CustomTagList getCustomTagList() { return customTagList; }
	
	@Override public void setReadingOrder(Integer readingOrder, Object who) {
		CustomTagUtil.setReadingOrder(this, readingOrder, who);
	}
	@Override public Integer getReadingOrder() {
		return CustomTagUtil.getReadingOrder(this);
	}
	@Override public void sortChildren(boolean recursive) {}

	// OBSERVABLE STUFF:
	public TrpObservable getObservable() { return observable; }
    public void addObserver(Observer o) { observable.addObserver(o); }
    public void deleteObserver(Observer o) { observable.deleteObserver(o); }
    public void deleteObservers() { observable.deleteObservers(); }
    
	public String print() {
		return "TrpWordType: id = " + getId() + ", text = " + getUnicodeText() + ", level = " + getLevel() + ", parent = " + getParent() + ", nChildren = "
				+ getChildren(false).size();
	}

	@Override
	public void swap(int direction) {
		int i = this.getIndex();
		
		if (direction == 0){
			
			if (i>0){
				Collections.swap(getLine().getWord(), i, i-1);
				setReadingOrder(getReadingOrder()-1, TrpWordType.class);
				line.sortWords();
				observable.setChangedAndNotifyObservers(new TrpReadingOrderChangedEvent(this));
				
			}
		}
		
		else if (direction == 1){
			
			if (i<(getLine().getWord().size()-1)){
				Collections.swap(getLine().getWord(), i, i+1);
				setReadingOrder(getReadingOrder()+1, TrpWordType.class);
				line.sortWords();
				observable.setChangedAndNotifyObservers(new TrpReadingOrderChangedEvent(this));
				
			}
		}
		
		
		
	}


}
