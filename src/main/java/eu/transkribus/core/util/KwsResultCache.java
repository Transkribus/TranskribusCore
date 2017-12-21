package eu.transkribus.core.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.kws.TrpKwsResult;

public enum KwsResultCache {
	INSTANCE;
	private static final Logger logger = LoggerFactory.getLogger(KwsResultCache.class);
	private final Map<Integer, TrpKwsResult> kwsCache;
	private KwsResultCache() {
		kwsCache = Collections.synchronizedMap(new HashMap<>());
	}
	public TrpKwsResult get(int jobId) {
		return kwsCache.get(jobId);
	}
	public void put(TrpKwsResult res) {
		kwsCache.put(res.getJobId(), res);
	}
	public boolean contains(int jobId) {
		return kwsCache.containsKey(jobId);
	}
	public void putAll(List<TrpKwsResult> results) {
		logger.debug("OLD KWS cache size: " + kwsCache.size());
		synchronized(kwsCache) {
			results.stream()
				.filter(r -> !this.contains(r.getJobId()))
				.forEach(r -> this.put(r));
		}
		logger.debug("NEW KWS cache size: " + kwsCache.size());
	}
	public void clear() {
		kwsCache.clear();
	}
}
