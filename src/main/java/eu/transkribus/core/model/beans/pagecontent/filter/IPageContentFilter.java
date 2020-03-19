package eu.transkribus.core.model.beans.pagecontent.filter;

import eu.transkribus.core.model.beans.pagecontent.PcGtsType;

/**
 * Interface for a filter, manipulating a PcGtsType object, e.g. after loading or before storing it.
 */
public interface IPageContentFilter {
	public void doFilter(PcGtsType pc);
}
