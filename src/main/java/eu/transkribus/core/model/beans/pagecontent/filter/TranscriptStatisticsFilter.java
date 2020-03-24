package eu.transkribus.core.model.beans.pagecontent.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.TrpTranscriptStatistics;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.util.PageXmlUtils;

/**
 * IPageContentFilter that accumulates a {@link TrpTranscriptStatistics} over all PcGtsTypes filtered.
 * This filter does not alter the content in any way.
 */
public class TranscriptStatisticsFilter implements IPageContentFilter {
	private static final Logger logger = LoggerFactory.getLogger(TranscriptStatisticsFilter.class);
	
	TrpTranscriptStatistics stats;
	
	public TranscriptStatisticsFilter() {
		stats = new TrpTranscriptStatistics();
	}
	
	@Override
	public void doFilter(PcGtsType pc) {
		TrpTranscriptStatistics pcStats = PageXmlUtils.extractStats(pc);
		logger.debug("Extracted stats from PcGtsType: {}", pcStats);
		this.stats.add(pcStats);
		logger.debug("New overall stats: {}", stats);
	}

	public TrpTranscriptStatistics getStats() {
		return stats;
	}
}
