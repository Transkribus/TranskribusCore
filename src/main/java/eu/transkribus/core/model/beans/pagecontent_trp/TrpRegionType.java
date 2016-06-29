package eu.transkribus.core.model.beans.pagecontent_trp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observer;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import eu.transkribus.core.exceptions.NotImplementedException;
import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.CustomTagUtil;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.RegionTypeUtil;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpElementReadingOrderComparator;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObservable;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpChildrenClearedEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpConstructedWithParentEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpCoordsChangedEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpReadingOrderChangedEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpReinsertIntoParentEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpRemovedEvent;
import eu.transkribus.core.model.beans.pagecontent.CoordsType;
import eu.transkribus.core.model.beans.pagecontent.TableRegionType;
import eu.transkribus.core.model.beans.pagecontent.TextEquivType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.util.BeanCopyUtils;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.PointStrUtils;

@XmlTransient
public class TrpRegionType extends RegionType implements ITrpShapeType {
	
	 // --- THE NEW SHIT ---
 	@XmlTransient
 	protected TrpObservable observable;
 	@XmlTransient
 	protected Object parent=null;
 	@XmlTransient
	protected CustomTagList customTagList;
 	
 	@XmlTransient
	Object data;
	
	public TrpRegionType() {
		super();
				
		observable = new TrpObservable(this);
		customTagList = new CustomTagList(this);
	}
	
	public TrpRegionType(TrpPageType page) {
		this();
		setParent(page);
		observable.setChangedAndNotifyObservers(new TrpConstructedWithParentEvent(this));
	}
	
	/** Copy constructor - NOTE: the contained lines are <b>not</b> deep copied, only their references! */
	public TrpRegionType(TrpRegionType src) {
		super();
		
		copyFields(src);
	}
	
	@Override public ITrpShapeType copy() {
		throw new NotImplementedException("copy method must be implemented for each type!");
	}
	
	@Override public void copyFields(ITrpShapeType srcShape) {
		if (!(srcShape instanceof TrpTextRegionType))
			return;
		
		TrpRegionType src = (TrpRegionType) srcShape;
		
		// set new id:
//	    id = getName()+"_"+System.currentTimeMillis();
	    id = TrpPageType.getUniqueId("region");

		// copy base fields:
		coords = BeanCopyUtils.copyCoordsType(src.coords);
		// copy child regions
		textRegionOrImageRegionOrLineDrawingRegion = 
				src.textRegionOrImageRegionOrLineDrawingRegion != null ? 
				new ArrayList<TrpRegionType>(src.textRegionOrImageRegionOrLineDrawingRegion) : null;
	    
	    if (src.getCustomTagList() != null)
	    	src.getCustomTagList().writeToCustomTag();
	    if (src.custom != null)
	    	custom = new String(src.custom);
	    
	    if (src.comments != null)
	    	comments = new String(src.comments);
			
		// copy new fields:
		parent = src.parent;
		data = src.data;
		
		customTagList = new CustomTagList(this);
	}
	
//	@Override public void setId(String id) {
//		this.id = id;
//	}
	
	public List<TrpTextRegionType> getTextRegions(boolean recursive) {
		return getTextRegions(getTextRegionOrImageRegionOrLineDrawingRegion(), recursive);
	}
		
	/*
	 * 
	 * added setion to get text regions for table region
	 * -> Is this a problem somewhere else???
	 */
	public static List<TrpTextRegionType> getTextRegions(List<TrpRegionType> textRegionOrImageRegionOrLineDrawingRegion, boolean recursive) {
		List<TrpTextRegionType> res = new ArrayList<TrpTextRegionType>();
		
		if(textRegionOrImageRegionOrLineDrawingRegion == null) {
			return res;
		}
		
		for (TrpRegionType region : textRegionOrImageRegionOrLineDrawingRegion) {
			if (region instanceof TextRegionType) {
				res.add((TrpTextRegionType) region);
				if (recursive) {
					res.addAll(region.getTextRegions(true));
				}
			}
			else if(region instanceof TableRegionType){
				for (ITrpShapeType textregionOfTable : region.getChildren(recursive)) {
					if (textregionOfTable instanceof TextRegionType) {
						res.add((TrpTextRegionType) textregionOfTable);
						if (recursive) {
							res.addAll( ((TrpTextRegionType)textregionOfTable).getTextRegions(true));
						}
					}
				}
			}
		}
		
		return res;
	}
	
	public void sortRegions() {
		sortRegions(getTextRegionOrImageRegionOrLineDrawingRegion());
	}
	
	public static void sortRegions(List<TrpRegionType> textRegionOrImageRegionOrLineDrawingRegion) {
		Collections.sort(textRegionOrImageRegionOrLineDrawingRegion, new TrpElementReadingOrderComparator<RegionType>(true));
	}

	@Override
	public Object getParent() { return parent; }
	
	@Override
	public void setParent(Object parent) {
		if (parent instanceof RegionType || parent instanceof TrpPageType || parent==null) {
			this.parent = parent;
		} else {
			throw new RuntimeException("Cannot set parent for region to object of type: "+parent.getClass().getSimpleName());
		}
	}
	
	@Override
	public String getName() {
		XmlType t = this.getClass().getAnnotation(XmlType.class);
				
		if (t != null) {
			if (t.name().endsWith("Type"))
				return t.name().substring(0, t.name().length()-4);
			else return t.name();
		} else
			return "UnknownRegionType";
	}
	
	@Override
	public int getLevel() {
		// regions are either on level 0, if they are directly attached to a page or on level 1 elsewise:
		if (RegionTypeUtil.isBlackening(this)) {
			return 4;
		}
		else if (getParent() instanceof TrpRegionType) {
			return ((TrpRegionType) getParent()).getLevel() + 1;
		} else {
			return 0;
		}
	}
	
	@Override public TrpPageType getPage() {
		if (parent instanceof TrpPageType)
			return (TrpPageType) parent;
		else if (parent instanceof TrpRegionType) {
			return ((TrpRegionType)parent).getPage();
		}
		else
			return null;
	}
	
	@Override public ITrpShapeType getSiblingShape(boolean previous) {
		// TODO: implement for RegionType's -> needed???
		return null;
	}

	@Override public void sortChildren(boolean recursive) {
		sortRegions();
		if (recursive) {
			for (TrpRegionType r : getTextRegionOrImageRegionOrLineDrawingRegion()) {
				r.sortRegions();
			}
		}
	}
	
	@Override public void removeChildren() {
		getTextRegionOrImageRegionOrLineDrawingRegion().clear();
		observable.setChangedAndNotifyObservers(new TrpChildrenClearedEvent(this));
	}
	
	@Override
	public List<ITrpShapeType> getChildren(boolean recursive) {
		ArrayList<ITrpShapeType> c = new ArrayList<ITrpShapeType>();
		for (TrpRegionType region : getTextRegionOrImageRegionOrLineDrawingRegion()) {
			c.add(region);
			if (recursive) {
				for (ITrpShapeType o : region.getChildren(recursive)) {
					c.add(o);
				}
			}
		}
				
		return c;
	}
	
	@Override public boolean hasChildren() { 
		return !getTextRegionOrImageRegionOrLineDrawingRegion().isEmpty();
	}
	
	@Override public void setCustom(String custom) {
		super.setCustom(custom);
		customTagList = new CustomTagList(this);
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
		if (!getPage().getTextRegionOrImageRegionOrLineDrawingRegion().contains(this)) {
			CoreUtils.addOrAppend(getPage().getTextRegionOrImageRegionOrLineDrawingRegion(), this, index);
			getPage().sortRegions();
			observable.setChangedAndNotifyObservers(new TrpReinsertIntoParentEvent(this));
		}
		
	}
	
	@Override
	public void swap(int direction) {
		
		int i = -1;
		i = getPage().getTextRegionOrImageRegionOrLineDrawingRegion().indexOf(this);
				
		if (direction == 0){
		
			if (i>0){
				Collections.swap(getPage().getTextRegionOrImageRegionOrLineDrawingRegion(), i, i-1);
				setReadingOrder(getReadingOrder()-1, RegionType.class);
				observable.setChangedAndNotifyObservers(new TrpReadingOrderChangedEvent(this));
				
			}
		}
		else if (direction == 1){
			
			if (i >= 0 && i<(getPage().getTextRegionOrImageRegionOrLineDrawingRegion().size()-1)){
				Collections.swap(getPage().getTextRegionOrImageRegionOrLineDrawingRegion(), i, i+1);
				setReadingOrder(getReadingOrder()+1, RegionType.class);
				observable.setChangedAndNotifyObservers(new TrpReadingOrderChangedEvent(this));
				
			}
		}
		
	}
	

	@Override
	public void removeFromParent() {
//		System.out.println("removing from parent before: "+getPage().getTextRegionOrImageRegionOrLineDrawingRegion().size());
		getPage().getTextRegionOrImageRegionOrLineDrawingRegion().remove(this);
//		System.out.println("removing from parent after: "+getPage().getTextRegionOrImageRegionOrLineDrawingRegion().size());
		observable.setChangedAndNotifyObservers(new TrpRemovedEvent(this));
	}

	@Override public String getUnicodeText() {
		return null;
	}
	
	@Override public void setUnicodeText(String unicode, Object who) { }
	@Override public void editUnicodeText(int start, int end, String replacement, Object who) { }
	@Override public void setTextEquiv(TextEquivType te) { }	
	
	@Override
	public Object getData() { return data; }
	@Override
	public void setData(Object data) { this.data = data; }
	
	@Override 
	public ITrpShapeType getParentShape() {
		if (parent instanceof RegionType) {
			return (TrpRegionType) parent;
		} else
			return null;
	}
	
	@Override public void addTextStyleTag(TextStyleTag s, String addOnlyThisProperty, /*boolean recursive,*/ Object who) {}

	@Override public List<TextStyleTag> getTextStyleTags() { return new ArrayList<>(); }
	
	@Override public void setTextStyle(TextStyleType s) {
	}
	@Override
	public void setTextStyle(TextStyleType s, boolean recursive, Object who) { }
	@Override public TextStyleType getTextStyle() { return null; }
	
	@Override public void setStructure(String structureType, boolean recursive, Object who) {
		CustomTagUtil.setStructure(this, structureType, recursive, who);
	}
	
	@Override public String getStructure() {
		return CustomTagUtil.getStructure(this);
	}
	
	@Override public void translate(int x, int y) throws Exception { 
		setCoordinates(PointStrUtils.translatePoints(getCoordinates(), x, y), this);
	}
	
	@Override public void rotate(double degrees) throws Exception {
		setCoordinates(PointStrUtils.rotatePoints(getCoordinates(), Math.toRadians(degrees)), this);
	}
		
	@Override public void setReadingOrder(Integer readingOrder, Object who) {
		CustomTagUtil.setReadingOrder(this, readingOrder, who);
	}
	
	@Override public Integer getReadingOrder() {
		return CustomTagUtil.getReadingOrder(this);
	}
	
	@Override public String print() {
	    return getName()+": id = "+getId()+", text = "+getUnicodeText()
	    		+", level = "+getLevel()+", parent = "+getParent();
	}
	
	@Override public CustomTagList getCustomTagList() { return customTagList; }
	
	// OBSERVABLE STUFF:
	@Override public TrpObservable getObservable() { return observable; }
	@Override public void addObserver(Observer o) { observable.addObserver(o); }
	@Override public void deleteObserver(Observer o) { observable.deleteObserver(o); }
	@Override public void deleteObservers() { observable.deleteObservers(); }    
	

	 // --- END OF THE NEW SHIT ---

}
