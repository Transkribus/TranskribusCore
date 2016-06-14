package eu.transkribus.core.model.beans.pagecontent_trp;

import java.util.ArrayList;

import eu.transkribus.core.model.beans.pagecontent.TableRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.observable.TrpObserveEvent.TrpConstructedWithParentEvent;

public class TrpTableRegionType extends TableRegionType implements ITrpShapeType {
	
	
	public TrpTableRegionType() {
		super();
			
		System.out.println("TRP TABLE REGION TYPE!");
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
	
	
	
	
	

}
