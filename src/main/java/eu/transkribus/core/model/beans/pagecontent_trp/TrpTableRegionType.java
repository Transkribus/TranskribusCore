package eu.transkribus.core.model.beans.pagecontent_trp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.TableCellType;
import eu.transkribus.core.model.beans.pagecontent.TableRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpChildrenClearedEvent;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpConstructedWithParentEvent;

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
	
	@Override
	public String getName() { return "TableRegion"; }
	
	public void sortTableCells() {
		// sort table cells first by row then col index
		Collections.sort(tableCell, new Comparator<TableCellType>() {
			@Override public int compare(TableCellType o1, TableCellType o2) {
				int rc = Integer.compare(o1.getRow(), o2.getRow());
				if (rc != 0) 
					return rc;
				
				return Integer.compare(o1.getCol(), o2.getCol());
			}
		});
	}
	
	@Override public void sortChildren(boolean recursive) {
		sortTableCells();
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
	
	public Pair<Integer, Integer> getMaxRowCol() {
		int maxR = -1;
		int maxC = -1;
		
		for (TableCellType c : tableCell) {
			if (c.getRow() > maxR)
				maxR = c.getRow();
			
			if (c.getCol() > maxC)
				maxC = c.getCol();	
		}
		
		return Pair.of(maxR, maxC);
	}
	
	public int getNRows() {
		return getMaxRowCol().getLeft()+1;
	}
	
	public int getNCols() {
		return getMaxRowCol().getRight()+1;
	}
		
	public List<TrpTableCellType> getCells(boolean rowCells, boolean startIndex, int index) {
		if (rowCells)
			return startIndex ? getRowCells(index) : getRowEndCells(index);
		else
			return startIndex ? getColCells(index) : getColEndCells(index);
	}
	
	public List<TrpTableCellType> getRowEndCells(int rowEnd) {
		List<TrpTableCellType> rowCells = new ArrayList<>();
		for (TableCellType c : tableCell) {
			if (((TrpTableCellType)c).getRowEnd() == rowEnd)
				rowCells.add((TrpTableCellType) c);
		}
		return rowCells;
	}
	
	public List<TrpTableCellType> getColEndCells(int colEnd) {
		List<TrpTableCellType> rowCells = new ArrayList<>();
		for (TableCellType c : tableCell) {
			if (((TrpTableCellType)c).getColEnd() == colEnd)
				rowCells.add((TrpTableCellType) c);
		}
		return rowCells;
	}
	
	public List<TrpTableCellType> getRowCells(int row) {
		List<TrpTableCellType> rowCells = new ArrayList<>();
		for (TableCellType c : tableCell) {
			if (c.getRow() == row)
				rowCells.add((TrpTableCellType) c);
		}
		return rowCells;
	}
	
	public List<TrpTableCellType> getColCells(int col) {
		List<TrpTableCellType> colCells = new ArrayList<>();
		for (TableCellType c : tableCell) {
			if (c.getCol() == col)
				colCells.add((TrpTableCellType) c);
		}
		return colCells;
	}
	
	public boolean checkForMissingCells() {
		sortChildren(false);
		
		
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
