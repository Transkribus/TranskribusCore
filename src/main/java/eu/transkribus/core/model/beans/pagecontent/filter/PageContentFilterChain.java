package eu.transkribus.core.model.beans.pagecontent.filter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.PcGtsType;

/**
 * IPageContentFilter that applies a ordered list of filters in doFilter()
 */
public class PageContentFilterChain implements IPageContentFilter {
	Logger logger = LoggerFactory.getLogger(PageContentFilterChain.class);
	private final List<IPageContentFilter> filterChain;
	
	public PageContentFilterChain() {
		filterChain = new ArrayList<>();
	}
	
	public void addFilter(IPageContentFilter filter) {
		filterChain.add(filter);
	}

	/**
	 * Factory method for a filter chain with the given filters
	 * 
	 * @param contentFilters the filters to be added to the new chain. Order of arguments defines the order of application
	 * @return a filter chain with the given filters
	 */
	public static PageContentFilterChain of(IPageContentFilter...contentFilters) {
		PageContentFilterChain chain = new PageContentFilterChain();
		for(IPageContentFilter filter : contentFilters) {
			chain.addFilter(filter);
		}
		return chain;
	}

	@Override
	public void doFilter(PcGtsType pc) {
		if(filterChain.isEmpty()) {
			logger.debug("No filter set. Doing nothing.");
			return;
		}
		
		for(IPageContentFilter filter : filterChain) {
			logger.debug("Applying {}", filter.getClass().getSimpleName());
			filter.doFilter(pc);
		}
	}
}
