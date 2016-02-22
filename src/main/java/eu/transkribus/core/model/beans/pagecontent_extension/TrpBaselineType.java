package eu.transkribus.core.model.beans.pagecontent_extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.CustomTagUtil;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.BaselineType;
import eu.transkribus.core.model.beans.pagecontent.TextEquivType;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.model.beans.pagecontent.TextTypeSimpleType;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObservable;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpConstructedWithParentEvent;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpCoordsChangedEvent;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpReinsertIntoParentEvent;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpRemovedEvent;
import eu.transkribus.core.util.PrimaUtils;

public class TrpBaselineType extends BaselineType implements ITrpShapeType {	
	TrpObservable observable = new TrpObservable(this);
	TrpTextLineType line;
	
	Object data;

	public TrpBaselineType() {
		super();
	}
	public TrpBaselineType(TrpTextLineType line) {
		super();
		setLine(line);
		observable.setChangedAndNotifyObservers(new TrpConstructedWithParentEvent(this));
	}
	
	public TrpBaselineType(TrpBaselineType src) {
		super();
		
		copyFields(src);
	}
	
	@Override public void setId(String id) {}
	
	@Override
	public TrpBaselineType copy() { return new TrpBaselineType(this); }
	
	@Override public void copyFields(ITrpShapeType srcShape) {
		if (!(srcShape instanceof TrpBaselineType))
			return;
		
		TrpBaselineType src = (TrpBaselineType) srcShape;
		
		if (src.points != null)
			points = new String(src.points);
		
		line = src.line;
		data = src.data;
	}
		
	@Override
	public String getName() { return "Baseline"; }
	@Override
	public int getLevel() { return 2; }	
	
	@Override
	public String getId() { return ""; }
	
	@Override
	public void setCoordinates(String value, Object who) {
		setPoints(value);
		observable.setChangedAndNotifyObservers(new TrpCoordsChangedEvent(who));
	}
	
	@Override
	public String getCoordinates() {
		return getPoints();
	}
	
	public void setLine(TrpTextLineType line) {
		this.line = line;
	}
	public TrpTextLineType getLine() { return line; }
		
//	@Override
//    public void setPoints(String pts) {
//		super.setPoints(pts);
//		observable.setChangedAndNotifyObservers(new TrpObserveEvent(TrpObserveEvent.COORDS_CHANGED, null));
//    }
	
	@Override
	public List<ITrpShapeType> getChildren(boolean recursive) {
		return new ArrayList<>();
	}
	
	@Override public boolean hasChildren() { return false; }
	
	@Override
	public void reInsertIntoParent() {
		if (getLine().getBaseline() != this) {
			getLine().setBaseline(this);
			observable.setChangedAndNotifyObservers(new TrpReinsertIntoParentEvent(this));
		}
	}
	
	@Override
	public void reInsertIntoParent(int index) {
		reInsertIntoParent();
	}
	
	@Override
	public void removeFromParent() {
		getLine().setBaseline(null);
		observable.setChangedAndNotifyObservers(new TrpRemovedEvent(this));
	}
	
	@Override public void removeChildren() {}
	
	@Override
	public Object getData() { return data; }
	@Override
	public void setData(Object data) { this.data = data; }
	
	@Override public void setUnicodeText(String unicode, Object who) {}
	@Override public void editUnicodeText(int start, int end, String text, Object who) {}
	
	@Override
	public String getUnicodeText() {
		return null;
	}
	
	@Override
	public TrpPageType getPage() { return getLine().getPage(); }
	
	@Override 
	public ITrpShapeType getParentShape() { return getLine(); }
	
	@Override public ITrpShapeType getSiblingShape(boolean previous) { return null; }
	
	@Override
	public Object getParent() { return getLine(); }
	
	@Override
	public void setParent(Object parent) {
		this.setLine((TrpTextLineType)parent);
	}
	
	@Override public TextStyleType getTextStyle() { return null; }
	@Override public void setTextStyle(TextStyleType s, boolean recursive, Object who) {}
	@Override public void setTextStyle(TextStyleType s) {}
	@Override public void addTextStyleTag(TextStyleTag s, String addOnlyThisProperty, /*boolean recursive,*/ Object who) {}
	@Override public List<TextStyleTag> getTextStyleTags() { return new ArrayList<>(); }	
	
	@Override public void setStructure(String s, boolean recursive, Object who) {}
	@Override public String getStructure() { return ""; }
	
	@Override public String getCustom() { return null; }
	@Override public void setCustom(String custom) {}

	@Override public void translate(int x, int y) throws Exception { 
		setCoordinates(PrimaUtils.translatePoints(getCoordinates(), x, y), this);
	}
	
	@Override public void rotate(double degrees) throws Exception {
		setCoordinates(PrimaUtils.rotatePoints(getCoordinates(), Math.toRadians(degrees)), this);
	}
	
	@Override public CustomTagList getCustomTagList() { return null; }
	
	// OBSERVABLE STUFF:
	public TrpObservable getObservable() { return observable; }
    public void addObserver(Observer o) { observable.addObserver(o); }
    public void deleteObserver(Observer o) { observable.deleteObserver(o); }
    public void deleteObservers() { observable.deleteObservers(); }

    
	public String print() {
		return "TrpBaselineType: id = " + getId() + ", text = " + getUnicodeText() + ", level = " + getLevel() + ", parent = " + getParent() + ", nChildren = "
				+ getChildren(false).size();
	}
	
	@Override public void setReadingOrder(Integer readingOrder, Object who) {
	}
	@Override public Integer getReadingOrder() {
		return null;
	}
	@Override public void setTextEquiv(TextEquivType te) {}
	@Override public void sortChildren(boolean recursive) {}
	@Override
	public void swap(int i) {
		// TODO Auto-generated method stub
		
	}

}
