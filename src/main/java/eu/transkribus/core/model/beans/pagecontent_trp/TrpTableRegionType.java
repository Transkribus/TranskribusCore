package eu.transkribus.core.model.beans.pagecontent_trp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	
	
	

}
