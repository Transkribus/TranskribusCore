package eu.transkribus.core.model.beans.pagecontent_trp;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observer;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.customtags.CustomTagUtil;
import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.CoordsType;
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
import eu.transkribus.core.util.IntRange;
import eu.transkribus.core.util.OverlapType;
import eu.transkribus.core.util.PointStrUtils;
import eu.transkribus.core.util.PointStrUtils.PointParseException;

public class TrpTableCellType extends TableCellType implements ITrpShapeType {
	
	private final static Logger logger = LoggerFactory.getLogger(TrpTableCellType.class);
	
 	@XmlTransient
 	protected TrpObservable observable = new TrpObservable(this);
 	
	@XmlTransient private TrpTableRegionType table;
	
// 	@XmlTransient
//	protected CustomTagList customTagList;
 	
// 	@XmlTransient Object data;
 	
 	public static final Comparator<TableCellType> TABLE_CELL_COMPARATOR = new Comparator<TableCellType>() {
		@Override public int compare(TableCellType o1, TableCellType o2) {
			int rc = Integer.compare(o1.getRow(), o2.getRow());
			if (rc != 0) 
				return rc;
			
			return Integer.compare(o1.getCol(), o2.getCol());
		}
	};
	
 	public static final Comparator<TableCellType> TABLE_CELL_ROW_COMPARATOR = new Comparator<TableCellType>() {
		@Override public int compare(TableCellType o1, TableCellType o2) {
			return Integer.compare(o1.getRow(), o2.getRow());
		}
	};
	
 	public static final Comparator<TableCellType> TABLE_CELL_COLUMN_COMPARATOR = new Comparator<TableCellType>() {
		@Override public int compare(TableCellType o1, TableCellType o2) {
			return Integer.compare(o1.getCol(), o2.getCol());
		}
	};	
 		
	public TrpTableCellType() {	
//		customTagList = new CustomTagList(this);
	}
	
	public TrpTableCellType(TrpTableRegionType parent) {
		this();
		
		setParent(parent);
	}
	
	public TrpTableCellType(TrpTableCellType src) {
		super();
				
		copyFields(src);
	}
	
	@Override public TrpTableCellType copy() {
		return new TrpTableCellType(this);
	}

	@Override public void copyFields(ITrpShapeType src) {
		if (!(src instanceof TrpTableCellType))
			throw new RuntimeException("copyFields - not a TrpTableCellType!");
		
		super.copyFields(src);
		
		TrpTableCellType srcCell = (TrpTableCellType) src;
		
		this.id = TrpPageType.getUniqueId(getName());
//		this.coords = BeanCopyUtils.copyCoordsType(srcCell.coords);
		
//		if (srcCell.textLine!=null)
//			textLine = new ArrayList<>(srcCell.textLine);
		
		this.row = srcCell.row;
		this.col = srcCell.col;
		this.rowSpan = srcCell.rowSpan;
		this.colSpan = srcCell.colSpan;
		
//	    if (srcCell.getCustomTagList() != null)
//	    	srcCell.getCustomTagList().writeToCustomTag();
//	    if (srcCell.custom != null)
//	    	custom = new String(srcCell.custom);
//	    
//	    if (srcCell.comments != null)
//	    	comments = new String(srcCell.comments);
			
		// copy new fields:
		table = srcCell.table;
//		data = srcCell.data;
		
//		customTagList = new CustomTagList(this);
	}

	@Override public String getName() {
		return "TableCell";
	}

	@Override public int getLevel() {
		if (getTable() != null) {
			return getTable().getLevel()+1;
		} else {
			return ITrpShapeType.REGION_BASE_LEVEL;
		}
		
//		return 0; // to be 0 or 1 that is the question
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

//	@Override public Object getData() {
//		return data;
//	}
//
//	@Override public void setData(Object data) {
//		this.data = data;
//	}

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
	
//	public Pair<Integer, Integer> getSpan() {
//		return Pair.of(getRowSpan(), getColSpan());
//	}
	
	public int[] getSpan() {
		return new int[] { getRowSpan(), getColSpan() };
	}
	
	public int[] getPos() {
		return new int[] { getRow(), getCol() };
	}
	
	public boolean isMergedCell() {
		return getRowSpan() > 1 || getColSpan() > 1;
	}
	
	public IntRange getRowRange() {
		return new IntRange(getRow(), getRowSpan());
	}
	
	public IntRange getColRange() {
		return new IntRange(getCol(), getColSpan());
	}	
	
//	public 
	
	public int getRowEnd() {
		return getRow() + getRowSpan();
	}
	
	public int getColEnd() {
		return getCol() + getColSpan();
	}
	
	public boolean isNeighborCell(TrpTableCellType tc, int position) {
		IntRange rr, cr;
		if (position == 0) { // left
			if (getCol() <= 0)
				return false;
			
			rr = new IntRange(getRow(), getRowSpan());
			cr = new IntRange(getCol()-1, 1);
		} else if (position == 1) { // bottom
			if (getRowEnd() >= table.getNRows())
				return false;
			
			rr = new IntRange(getRowEnd(), 1); // FIXME
			cr = new IntRange(getCol(), getColSpan());
		} else if (position == 2) { // right
			if (getColEnd() >= table.getNCols())
				return false;			
			
			rr = new IntRange(getRow(), getRowSpan());
			cr = new IntRange(getColEnd(), 1); // FIXME
		} else if (position == 3) { // top
			if (getRow() <= 0)
				return false;
			
			rr = new IntRange(getRow()-1, 1);
			cr = new IntRange(getCol(), getColSpan());
		}
		else { // no valid position specified -> all neighbors
			rr = new IntRange(getRow()-1, getRowSpan()+2);
			cr = new IntRange(getCol()-1, getColSpan()+2);
		}

		IntRange rr0 = new IntRange(tc.getRow(), tc.getRowSpan());
		IntRange cr0 = new IntRange(tc.getCol(), tc.getColSpan());
		
		boolean isNc = rr.getOverlapType(rr0) != OverlapType.NONE && cr.getOverlapType(cr0) != OverlapType.NONE;
		
		logger.trace(this.print()+" - "+tc.print()+" isNc: "+isNc);
		
		return isNc;
	}
	
//	public static boolean isCellsMergeable(List<TrpTableCellType> cells) {
//		
//		
//		
//		
//		
//	}
	
//	public boolean isNeihgborCell(TrpTableCellType tc, )
	
	public List<Pair<Integer, TrpTableCellType>> getCommonPointsOnNeighborCells(int x, int y) {
		List<Pair<Integer, TrpTableCellType>> res = new ArrayList<>();
		
		Point pt = new Point(x, y); 
//		for (int pos=0; pos<4; ++pos) {
//			for (TrpTableCellType nc : getNeighborCells(pos)) {
			for (TrpTableCellType nc : getNeighborCells()) {
				try {
					int i = PointStrUtils.parsePoints(nc.getCoordinates()).indexOf(pt);
					if (i != -1) {
						res.add(Pair.of(i, nc));
					}
				} catch (PointParseException e) {
					logger.warn("Could not parse points for cell "+nc.getId()+": "+nc.getCoordinates());
					continue;
				}
			}
//		}
		
		return res;
	}
	
	/**
	 * Get neighbor cell according to position =
	 * 0 -> left
	 * 1 -> bottom
	 * 2 -> right
	 * 3 -> top
	 */
	public List<TrpTableCellType> getNeighborCells(int position) {
		
		List<TrpTableCellType> neighbors = new ArrayList<>();
		
		if (table != null) {
			for (TableCellType c : table.getTableCell()) {
				if (c == this)
					continue;
				
				TrpTableCellType tc = (TrpTableCellType) c;
				if (isNeighborCell(tc, position)) {
					neighbors.add(tc);
				}
			}
		}
		Collections.sort(neighbors, TrpTableCellType.TABLE_CELL_COMPARATOR);
		
		return neighbors;
	}
	
	public List<TrpTableCellType> getNeighborCells() {
		List<TrpTableCellType> neighbors = new ArrayList<>();
		
		if (table != null) {
			for (TableCellType c : table.getTableCell()) {
				if (c == this)
					continue;
				
				TrpTableCellType tc = (TrpTableCellType) c;
				if (isNeighborCell(tc, -1)) {
					neighbors.add(tc);
				}
			}
		}
		
		return neighbors;
	}
	
	
	
	@Override public void setRow(int value) {
		super.setRow(value);
		observable.setChangedAndNotifyObservers(new TrpCoordsChangedEvent(this));
	}
	
	@Override public void setCol(int value) {
		super.setCol(value);
		observable.setChangedAndNotifyObservers(new TrpCoordsChangedEvent(this));
	}
	
	@Override public void setRowSpan(Integer value) {
		super.setRowSpan(value);
		observable.setChangedAndNotifyObservers(new TrpCoordsChangedEvent(this));
	}
	
	@Override public void setColSpan(Integer value) {
		super.setColSpan(value);
		observable.setChangedAndNotifyObservers(new TrpCoordsChangedEvent(this));
	}
	
	public void setSpan(int index, Integer value) {
		if (index == 0)
			setRowSpan(value);
		else
			setColSpan(value);
	}
	
	public void setPos(int index, int value) {
		if (index == 0)
			setRow(value);
		else
			setCol(value);
	}

	@Override public void setCoordinates(String value, Object who) {
		CoordsType coords = new CoordsType();
		coords.setPoints(value);
		setCoords(coords);
		
		observable.setChangedAndNotifyObservers(new TrpCoordsChangedEvent(who));
	}
	
	@Override public String getCoordinates() {		
		return getCoords().getPoints();
	}
	
    public void setCornerPts(String value, Object who) {
//        getCoords().setCornerPts(value);
        setCornerPts(value);
        
        observable.setChangedAndNotifyObservers(new TrpCoordsChangedEvent(who));
    }
    
//    public String getCornerPts() {
//    	return getCoords().getCornerPts();
//    }

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
			getTable().sortCells();
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
		return "TrpTableCellType: id = " + getId()+", row = "+getRow()+", col = "+getCol()+" rowSpan = "+getRowSpan()+" colSpan = "+getColSpan();
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
		return "TrpTableCellType [cornerPts=" + cornerPts + ", row=" + row + ", col=" + col + ", rowSpan=" + rowSpan + ", colSpan=" + colSpan
				+ ", leftBorderVisible=" + leftBorderVisible + ", rightBorderVisible=" + rightBorderVisible + ", topBorderVisible=" + topBorderVisible
				+ ", bottomBorderVisible=" + bottomBorderVisible + ", label=" + label + "]";
	}

//	@Override public String toString() {
//		return "TrpTableCellType [coords=" + coords
//				+ ", row=" + row + ", col=" + col + ", rowSpan=" + rowSpan + ", colSpan=" + colSpan + ", id=" + id + ", custom="
//				+ custom+"]";
//	}
	
	
	
//	public static void main(String[] args) {
//		TrpTableCellType tc = new TrpTableCellType();
//		TrpTableCellType tc2 = new TrpTableCellType(tc);
//	}

}
