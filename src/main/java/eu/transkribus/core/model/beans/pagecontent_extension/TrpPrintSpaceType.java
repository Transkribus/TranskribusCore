package eu.transkribus.core.model.beans.pagecontent_extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.CustomTagUtil;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.CoordsType;
import eu.transkribus.core.model.beans.pagecontent.PrintSpaceType;
import eu.transkribus.core.model.beans.pagecontent.TextEquivType;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.model.beans.pagecontent.TextTypeSimpleType;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObservable;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpConstructedWithParentEvent;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpCoordsChangedEvent;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpReinsertIntoParentEvent;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpRemovedEvent;
import eu.transkribus.core.util.BeanCopyUtils;
import eu.transkribus.core.util.PrimaUtils;

public class TrpPrintSpaceType extends PrintSpaceType implements ITrpShapeType {	
	TrpObservable observable = new TrpObservable(this);
	TrpPageType page;
	Object data;

	public TrpPrintSpaceType() {
		super();
	}
	
	public TrpPrintSpaceType(TrpPrintSpaceType src) {
		super();
		
		copyFields(src);
	}
		
	public TrpPrintSpaceType(TrpPageType page) {
		super();
		setPage(page);
		observable.setChangedAndNotifyObservers(new TrpConstructedWithParentEvent(this));
	}
	
	@Override public void setId(String id) {}
	
	@Override
	public TrpPrintSpaceType copy() { return new TrpPrintSpaceType(this); }
	
	@Override public void copyFields(ITrpShapeType srcShape) {
		if (!(srcShape instanceof TrpPrintSpaceType))
			return;
		
		TrpPrintSpaceType src = (TrpPrintSpaceType) srcShape;
		
		if (src.coords != null)
			coords = BeanCopyUtils.copyCoordsType(src.coords);
		
		page = src.page;
		data = src.data;
	}	
	
	@Override
	public Object getParent() { return getPage(); }
	
	@Override
	public void setParent(Object parent) {
		this.setPage((TrpPageType)parent);
	}
	
	@Override
	public String getName() { return "Printspace"; }
	@Override
	public String getId() { return ""; }
	
	@Override
	public int getLevel() { return -1; }

	public void setPage(TrpPageType page) {
		this.page = page;
	}
	public TrpPageType getPage() { return page; }	
	
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
	public List<ITrpShapeType> getChildren(boolean recursive) {
		return new ArrayList<>();
	}
	
	@Override public boolean hasChildren() { return false; }
	
	@Override
	public void reInsertIntoParent() {
		if (getPage().getPrintSpace() != this) {
			getPage().setPrintSpace(this);
			observable.setChangedAndNotifyObservers(new TrpReinsertIntoParentEvent(this));
		}
	}
	
	@Override
	public void reInsertIntoParent(int index) {
		reInsertIntoParent();
	}
	
	@Override
	public void removeFromParent() {
		getPage().setPrintSpace(null);
		observable.setChangedAndNotifyObservers(new TrpRemovedEvent(this));
	}	
	
	@Override public void removeChildren() {}
	
	@Override
	public Object getData() { return data; }
	@Override
	public void setData(Object data) { this.data = data; }
	
	@Override public void setUnicodeText(String unicode, Object who) {}
	@Override public void editUnicodeText(int start, int end, String text, Object who) {}
	@Override public String getUnicodeText() { return null; }	
	
	@Override public ITrpShapeType getParentShape() { return null; }
	@Override public ITrpShapeType getSiblingShape(boolean previous) { return null; }
	
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
	
	@Override public void setReadingOrder(Integer readingOrder, Object who) {
	}
	@Override public Integer getReadingOrder() {
		return null;
	}
	@Override public void setTextEquiv(TextEquivType te) {}
	@Override public void sortChildren(boolean recursive) {}
	
	// OBSERVABLE STUFF:
	public TrpObservable getObservable() { return observable; }
    public void addObserver(Observer o) { observable.addObserver(o); }
    public void deleteObserver(Observer o) { observable.deleteObserver(o); }
    public void deleteObservers() { observable.deleteObservers(); }
    
	public String print() {
		return "TrpPrintSpaceType: id = " + getId() + ", text = " + getUnicodeText() + ", level = " + getLevel() + ", parent = " + getParent() + ", nChildren = "
				+ getChildren(false).size();
	}

	@Override
	public void swap(int i) {
		// TODO Auto-generated method stub
		
	}


}
