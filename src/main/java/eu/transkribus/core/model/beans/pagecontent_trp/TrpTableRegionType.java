package eu.transkribus.core.model.beans.pagecontent_trp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.TableCellType;
import eu.transkribus.core.model.beans.pagecontent.TableRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpChildrenClearedEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpConstructedWithParentEvent;
import eu.transkribus.core.util.IntRange;
import eu.transkribus.core.util.OverlapType;

public class TrpTableRegionType extends TableRegionType implements ITrpShapeType {
	private final static Logger logger = LoggerFactory.getLogger(TrpTableRegionType.class);
	
	
	public TrpTableRegionType() {
		super();
			
		logger.debug("created trp table region type!");
	}
	
	public TrpTableRegionType(TrpPageType page) {
		this();
		setParent(page);
		observable.setChangedAndNotifyObservers(new TrpConstructedWithParentEvent(this));
	}
	
	/** Copy constructor - NOTE: the contained lines are <b>not</b> deep copied, only their references! */
	public TrpTableRegionType(TrpTableRegionType src) {
		super();
		
		copyFields(src);
	}
	
	@Override public TrpTableRegionType copy() { return new TrpTableRegionType(this); }
	
	@Override public void copyFields(ITrpShapeType srcShape) {
		if (!(srcShape instanceof TrpTableRegionType))
			throw new RuntimeException("copyFields: not a TrpTableRegionType: "+srcShape);
		
		TrpTableRegionType src = (TrpTableRegionType) srcShape;
				
		super.copyFields(srcShape);
		
		// specific to table regions:
		if (src.tableCell != null)
			this.tableCell = new ArrayList<>(src.tableCell);
		
		this.orientation = src.orientation;
		this.rows = src.rows;
		this.columns = src.columns;
		
	    this.lineColour = src.lineColour;
	    this.bgColour = src.bgColour;
	}
	
	@Override public String getName() { return RegionTypeUtil.TABLE; }
		
	public void sortCells() {
		// sort table cells first by row then col index
		Collections.sort(tableCell, TrpTableCellType.TABLE_CELL_COMPARATOR);
	}
	
	@Override public void sortChildren(boolean recursive) {
		sortCells();
		if (recursive) {
			// TODO: sort lines and regions in table cells...
			
			for (TableCellType tc : getTableCell()) {
				((TrpTableCellType) tc).sortChildren(recursive);
			}
		}
	}
	
	
	@Override public void removeChildren() {
		super.removeChildren();
		
		getTableCell().clear();
		observable.setChangedAndNotifyObservers(new TrpChildrenClearedEvent(this));
	}
	
	@Override
	public List<ITrpShapeType> getChildren(boolean recursive) {
		ArrayList<ITrpShapeType> c = new ArrayList<ITrpShapeType>();
		for (TableCellType tc : this.getTableCell()) {
			c.add((TrpTableCellType) tc);
			
			if (recursive) {
				for (ITrpShapeType o : ((TrpTableCellType) tc).getChildren(recursive)) {
					c.add(o);
				}
			}
		}
		
		return c;
	}
	
	@Override public boolean hasChildren() { return !getTableCell().isEmpty(); }
	
	public Pair<Integer, Integer> getDimensions() {
		int maxR = -1;
		int maxC = -1;
		
		for (TableCellType c : tableCell) {
			TrpTableCellType tc = (TrpTableCellType) c;
			
			if (tc.getRowEnd() > maxR)
				maxR = tc.getRowEnd();
			
			if (tc.getColEnd() > maxC)
				maxC = tc.getColEnd();	
		}
		
		return Pair.of(maxR, maxC);
	}
	
	public int getNRows() {
		return getDimensions().getLeft();
	}
	
	public int getNCols() {
		return getDimensions().getRight();
	}
	
	public enum GetCellsType {
		START_INDEX,
		END_INDEX,
		OVERLAP;
	}
	
	public TrpTableCellType getCell(int row, int col) {
		for (TrpTableCellType c : getTrpTableCell()) {
			if (c.getRowRange().isInside(row) && c.getColRange().isInside(col)) {
				return c;
			}
		}
		return null;
	}
	
	public List<TrpTableCellType> getCells(boolean rowCells, GetCellsType type, int index) {
		return getCells(rowCells, type, index, 1);
	}
	
	public List<TrpTableCellType> getCells(boolean rowCells, GetCellsType type, int index, int length) {
		if (rowCells) {
			switch (type) {
			case START_INDEX:
				return getRowCells(index);
			case END_INDEX:
				return getRowEndCells(index);
			case OVERLAP:
				return getRowOverlapCells(index, length);
			}
		}
		else {
			switch (type) {
			case START_INDEX:
				return getColCells(index);
			case END_INDEX:
				return getColEndCells(index);
			case OVERLAP:
				return getColOverlapCells(index, length);
			}
		}
		
		return new ArrayList<>();
	}
	
	public List<TrpTableCellType> getRowOverlapCells(int index, int length) {
		List<TrpTableCellType> rowCells = new ArrayList<>();
		for (TrpTableCellType c : getTrpTableCell()) {
			
			if (c.getRowRange().getOverlapType(new IntRange(index, length))!=OverlapType.NONE)
				rowCells.add(c);
		}
		return rowCells;
	}
	
	public List<TrpTableCellType> getColOverlapCells(int index, int length) {
		List<TrpTableCellType> rowCells = new ArrayList<>();
		for (TrpTableCellType c : getTrpTableCell()) {
			if (c.getColRange().getOverlapType(new IntRange(index, length))!=OverlapType.NONE)
				rowCells.add(c);
		}
		return rowCells;
	}	
	
	public List<TrpTableCellType> getRowEndCells(int rowEnd) {
		List<TrpTableCellType> rowCells = new ArrayList<>();
		for (TrpTableCellType c : getTrpTableCell()) {
			if (c.getRowEnd() == rowEnd)
				rowCells.add(c);
		}
//		Collections.sort(rowCells, TrpTableCellType.TABLE_CELL_ROW_COMPARATOR);
		return rowCells;
	}
	
	public List<TrpTableCellType> getColEndCells(int colEnd) {
		List<TrpTableCellType> rowCells = new ArrayList<>();
		for (TrpTableCellType c : getTrpTableCell()) {
			if (c.getColEnd() == colEnd)
				rowCells.add(c);
		}
//		Collections.sort(rowCells, TrpTableCellType.TABLE_CELL_COL_COMPARATOR);
		return rowCells;
	}
	
	public List<TrpTableCellType> getRowCells(int row) {
		List<TrpTableCellType> rowCells = new ArrayList<>();
		for (TrpTableCellType c : getTrpTableCell()) {
			if (c.getRow() == row)
				rowCells.add(c);
		}
		return rowCells;
	}
	
	public List<TrpTableCellType> getColCells(int col) {
		List<TrpTableCellType> colCells = new ArrayList<>();
		for (TrpTableCellType c : getTrpTableCell()) {
			if (c.getCol() == col)
				colCells.add( c);
		}
		return colCells;
	}
	
    @SuppressWarnings("unchecked")
	public List<TrpTableCellType> getTrpTableCell() {
    	return (List<TrpTableCellType>) (Object) getTableCell();
    }
	
//	public void adjustCellIndexesOnRowOrColInsert(int insertIndex, boolean isRowInserted) {
//		logger.debug("adjustCellIndexesOnRowOrColInsert, insertIndex: "+insertIndex+" isRowInserted = "+isRowInserted);
//		
//		// adjust row/cell indices according to split:
//		for (TableCellType c : getTableCell()) {
//			if (!isRowInserted) { // adjust columns
//				if (c.getCol() >== i) {
//					c.setCol(c.getCol()+1);
//					
//				}
//				
//				
//			} else {
//				
//				
//			}
//			
//			
//		}
//		
//		
//	}
}
