package eu.transkribus.core.model.beans.job;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.transkribus.core.io.util.TrpProperties;
import eu.transkribus.core.rest.JobConst;
import eu.transkribus.core.util.JobDataUtils;

public class KwsParameters {

	private ParameterMap params;
	
	private final static String THRESHOLD = JobConst.PROP_THRESHOLD;
	private final static String IS_CASE_SENSITIVE = JobConst.PROP_IS_CASE_SENSITIVE;
	private final static String IS_EXPERT = JobConst.PROP_IS_EXPERT;
	private final static String MAX_NR_OF_HITS = JobConst.PROP_MAX_NR_OF_HITS;
	private final static String DO_PARTIAL_MATCHING = JobConst.PROP_DO_PARTIAL_MATCHING;
	private final static String CUSTOM = JobConst.PROP_CUSTOM_PROP_MAP;
	
	private List<String> knownParams = Arrays.asList(
			THRESHOLD,
			IS_CASE_SENSITIVE,
			IS_EXPERT,
			MAX_NR_OF_HITS,
			DO_PARTIAL_MATCHING,
			CUSTOM
			);
	
	public KwsParameters() {
		params = new ParameterMap();
	}
	
	public KwsParameters(TrpProperties jobProps) {
		this();
		this.setThreshold(jobProps.getDoubleProperty(THRESHOLD));
		this.setCaseSensitive(jobProps.getBoolProperty(IS_CASE_SENSITIVE));
		this.setExpert(jobProps.getBoolProperty(IS_EXPERT));
		this.setMaxNrOfHits(jobProps.getIntProperty(MAX_NR_OF_HITS));
		this.setPartialMatching(jobProps.getBoolProperty(DO_PARTIAL_MATCHING));
		this.setCustomParams(JobDataUtils.getStringMap(jobProps.getProperties(), CUSTOM));
	}

	public Double getThreshold() {
		return params.getDoubleParam(THRESHOLD);
	}
	
	public void setThreshold(Double threshold) {
		params.addDoubleParam(THRESHOLD, threshold, 0, 1);
	}

	public boolean isExpert() {
		return params.getBoolParam(IS_EXPERT);
	}

	public void setExpert(boolean isExpert) {
		params.addBoolParam(IS_EXPERT, isExpert);
	}

	public Integer getMaxNrOfHits() {
		return params.getIntParam(MAX_NR_OF_HITS);
	}

	public void setMaxNrOfHits(Integer maxNrOfHits) {
		params.addIntParam(MAX_NR_OF_HITS, maxNrOfHits);
	}

	public boolean isPartialMatching() {
		return params.getBoolParam(DO_PARTIAL_MATCHING);
	}

	public void setPartialMatching(boolean doPartialMatching) {
		params.addBoolParam(DO_PARTIAL_MATCHING, doPartialMatching);
	}
	
	public boolean isCaseSensitive() {
		return params.getBoolParam(IS_CASE_SENSITIVE);
	}

	public void setCaseSensitive(boolean isCaseSensitive) {
		params.addBoolParam(IS_CASE_SENSITIVE, isCaseSensitive);
	}
	
	public void setCustomParameter(final String name, final Object value) {
		params.addParameter(name, value);
	}
	
	public String getCustomParameter(final String name) {
		return params.getParameterValue(name);
	}
	
	public void setCustomParams(Map<String, String> stringMap) {
		params.getParamMap().putAll(stringMap);
	}
	
	public ParameterMap getCustomParams() {
		ParameterMap map = new ParameterMap();
		for(Entry<String, String> e : params.getParamMap().entrySet()) {
			if(!knownParams.contains(e.getKey())) {
				map.addParameter(e.getKey(), e.getValue());
			}
		}
		return map;
	}
}