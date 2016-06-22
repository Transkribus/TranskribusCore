package eu.transkribus.core.model.beans.pagecontent_trp;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.CustomTagUtil;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.CellCoordsType;
import eu.transkribus.core.model.beans.pagecontent.TableCellType;
import eu.transkribus.core.model.beans.pagecontent.TextEquivType;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObservable;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpChildrenClearedEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpCoordsChangedEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpReinsertIntoParentEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpRemovedEvent;
import eu.transkribus.core.util.BeanCopyUtils;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.PointStrUtils;

public class TrpTableCellType extends TableCellType implements ITrpShapeType {
	
	private final static Logger logger = LoggerFactory.getLogger(TrpTableCellType.class);
	
 	@XmlTransient
 	protected TrpObservable observable = new TrpObservable(this);
 	
	@XmlTransient private TrpTableRegionType table;
	
 	@XmlTransient
	protected CustomTagList customTagList;
 	
 	@XmlTransient Object data;
 		
	public TrpTableCellType() {
		logger.debug("created TrpTableCellType!");	
		customTagList = new CustomTagList(this);
	}
	
	public TrpTableCellType(TrpTableRegionType parent) {
		this();
		
		setParent(parent);
	}
	
	public TrpTableCellType(TrpTableCellType src) {
		super();
				
		copyFields(src);
	}
	
	@Override public ITrpShapeType copy() {
		return new TrpTableCellType(this);
	}

	@Override public void copyFields(ITrpShapeType src) {
		if (!(src instanceof TrpTableCellType))
			throw new RuntimeException("copyFields - not a TrpTableCellType!");
		
		TrpTableCellType srcCell = (TrpTableCellType) src;
		
		this.id = TrpPageType.getUniqueId(getName());
		this.coords = BeanCopyUtils.copyCellCoordsType(srcCell.coords);
		
		if (srcCell.textLine!=null)
			textLine = new ArrayList<>(srcCell.textLine);
		
		this.row = srcCell.row;
		this.col = srcCell.col;
		this.rowSpan = srcCell.rowSpan;
		this.colSpan = srcCell.colSpan;
		
	    if (srcCell.getCustomTagList() != null)
	    	srcCell.getCustomTagList().writeToCustomTag();
	    if (srcCell.custom != null)
	    	custom = new String(srcCell.custom);
	    
	    if (srcCell.comments != null)
	    	comments = new String(srcCell.comments);
			
		// copy new fields:
		table = srcCell.table;
		data = srcCell.data;
		
		customTagList = new CustomTagList(this);
	}

	@Override public String getName() {
		return "TableCell";
	}

	@Override public int getLevel() {
		return 0; // to be 0 or 1 that is the question
	}

	@Override public TrpPageType getPage() {
		return table==null ? null : table.getPage();
	}
	
	public void setTable(TrpTableRegionType table) {
		this.table = table;
	}
	
	public TrpTableRegionType getTable() {
		return this.table;
	}

	@Override public ITrpShapeType getParentShape() {
		return table;
	}

	@Override public void setParent(Object parent) {
		if (parent instanceof TrpTableRegionType)
			setTable((TrpTableRegionType) parent);
	}

	@Override public Object getParent() {
		return table;
	}

	@Override public Object getData() {
		return data;
	}

	@Override public void setData(Object data) {
		this.data = data;
	}

	@Override public void setStructure(String structureType, boolean recursive, Object who) {
		CustomTagUtil.setStructure(this, structureType, recursive, who);
	}
	
	@Override public String getStructure() {
		return CustomTagUtil.getStructure(this);
	}

//	@Override public void setStructure(String structureType, boolean recursive, Object who) {
//	}
//
//	@Override public String getStructure() {
//		return null;
//	}

	@Override public void setCoordinates(String value, Object who) {
		CellCoordsType coords = new CellCoordsType();
		coords.setPoints(value);
		setCoords(coords);
		
		observable.setChangedAndNotifyObservers(new TrpCoordsChangedEvent(who));
	}
	
	@Override public String getCoordinates() {		
		return getCoords().getPoints();
	}
	
    public void setCornerPts(String value, Object who) {
        getCoords().setCornerPts(value);
        
        observable.setChangedAndNotifyObservers(new TrpCoordsChangedEvent(who));
    }
    
    public String getCornerPts() {
    	return getCoords().getCornerPts();
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

	@Override public boolean hasChildren() {
		return !getTextLine().isEmpty();
	}
	
	@Override
	public void reInsertIntoParent() {
		reInsertIntoParent(-1);
	}
	
	@Override
	public void reInsertIntoParent(int index) {
		if (!getTable().getTableCell().contains(this)) {
			CoreUtils.addOrAppend(getTable().getTableCell(), this, index);
			getTable().sortTableCells();
			observable.setChangedAndNotifyObservers(new TrpReinsertIntoParentEvent(this));
		}
	}	

	@Override public void removeFromParent() {
		getTable().getTableCell().remove(this);
		observable.setChangedAndNotifyObservers(new TrpRemovedEvent(this));		
	}

	@Override public void removeChildren() {
		getTextLine().clear();
		observable.setChangedAndNotifyObservers(new TrpChildrenClearedEvent(this));
	}

	public String print() {
		return "TrpTableCellType: id = " + getId() + ", text = " + getUnicodeText() + ", level = " + getLevel() + ", parent = " + getParent() + ", nChildren = "
				+ getChildren(false).size();
	}

	@Override public void translate(int x, int y) throws Exception { 
		setCoordinates(PointStrUtils.translatePoints(getCoordinates(), x, y), this);
	}
	
	@Override public void rotate(double degrees) throws Exception {
		setCoordinates(PointStrUtils.rotatePoints(getCoordinates(), Math.toRadians(degrees)), this);
	}

	@Override public CustomTagList getCustomTagList() {
		return customTagList;
	}

	@Override public void sortChildren(boolean recursive) {
		TrpTextRegionType.sortLines(getTextLine(), this);
	}


	
	// OBSERVABLE STUFF:
	@Override public TrpObservable getObservable() { return observable; }
	@Override public void addObserver(Observer o) { getObservable().addObserver(o); }
	@Override public void deleteObserver(Observer o) { getObservable().deleteObserver(o); }
	@Override public void deleteObservers() { getObservable().deleteObservers(); }
	
	
	// NO NEED TO IMPLEMENT THOSE:
	
	// related to text style
	@Override public TextStyleType getTextStyle() {
		return null;
	}

	@Override public void setTextStyle(TextStyleType s, boolean recursive, Object who) {
	}

	@Override public void setTextStyle(TextStyleType s) {
	}

	@Override public void addTextStyleTag(TextStyleTag s, String addOnlyThisProperty, Object who) {
	}
	
	@Override public void setTextEquiv(TextEquivType te) {
	}
	
	@Override public List<TextStyleTag> getTextStyleTags() {
		return new ArrayList<>();
	}
	
	// related to reading order
	@Override public void swap(int direction) {
	}
	@Override public void setReadingOrder(Integer readingOrder, Object who) {
	}
	@Override public Integer getReadingOrder() {
		return null;
	}
	
	// related to setting text
	@Override public void setUnicodeText(String unicode, Object who) {
	}

	@Override public void editUnicodeText(int start, int end, String replacement, Object who) {
	}

	@Override public String getUnicodeText() {
		return "";
	}
	
	// maybe needed??
	@Override public ITrpShapeType getSiblingShape(boolean previous) {
		return null;
	}

	@Override public String toString() {
		return "TrpTableCellType [coords=" + coords
				+ ", row=" + row + ", col=" + col + ", rowSpan=" + rowSpan + ", colSpan=" + colSpan + ", id=" + id + ", custom="
				+ custom+"]";
	}
	
	
	
//	public static void main(String[] args) {
//		TrpTableCellType tc = new TrpTableCellType();
//		TrpTableCellType tc2 = new TrpTableCellType(tc);
//	}

}
