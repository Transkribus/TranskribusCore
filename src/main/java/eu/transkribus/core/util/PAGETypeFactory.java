package eu.transkribus.core.util;

import eu.transkribus.core.model.beans.pagecontent.OrderedGroupIndexedType;
import eu.transkribus.core.model.beans.pagecontent.OrderedGroupType;
import eu.transkribus.core.model.beans.pagecontent.RegionRefIndexedType;

public class PAGETypeFactory {
	
	public static OrderedGroupIndexedType createOrderedGroupIndexed(int index, String id, String caption) {
		OrderedGroupIndexedType g = new OrderedGroupIndexedType();
		g.setIndex(index);
		g.setId(id);
		if (caption != null)
			g.setCaption(caption);

		return g;
	}
	
	public static OrderedGroupType createOrderedGroup(String id, String caption) {
		OrderedGroupType g = new OrderedGroupType();
		g.setId(id);
		if (caption != null)
			g.setCaption(caption);

		return g;
	}
	
	public static RegionRefIndexedType createRegionRefIndexed(int index, Object refObject) {
		RegionRefIndexedType r = new RegionRefIndexedType();
		r.setIndex(index);
		r.setRegionRef(refObject);
		
		return r;
	}

}
