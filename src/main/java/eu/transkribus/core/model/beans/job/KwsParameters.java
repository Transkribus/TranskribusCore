package eu.transkribus.core.model.beans.job;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.rest.JobConst;
import eu.transkribus.core.util.ParameterMap;

@XmlRootElement
public class KwsParameters extends ParameterMap {
	
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
		super();
		//case sensitivity is on as default
		setCaseSensitive(true);
	}

	public Double getThreshold() {
		return super.getDoubleParam(THRESHOLD);
	}
	
	public void setThreshold(Double threshold) {
		super.addDoubleParam(THRESHOLD, threshold, 0, 1);
	}

	public boolean isExpert() {
		return super.getBoolParam(IS_EXPERT);
	}

	public void setExpert(boolean isExpert) {
		super.addBoolParam(IS_EXPERT, isExpert);
	}

	public Integer getMaxNrOfHits() {
		return super.getIntParam(MAX_NR_OF_HITS);
	}

	public void setMaxNrOfHits(Integer maxNrOfHits) {
		super.addIntParam(MAX_NR_OF_HITS, maxNrOfHits);
	}

	public boolean isPartialMatching() {
		return super.getBoolParam(DO_PARTIAL_MATCHING);
	}

	public void setPartialMatching(boolean doPartialMatching) {
		super.addBoolParam(DO_PARTIAL_MATCHING, doPartialMatching);
	}
	
	public boolean isCaseSensitive() {
		return super.getBoolParam(IS_CASE_SENSITIVE);
	}

	public void setCaseSensitive(boolean isCaseSensitive) {
		super.addBoolParam(IS_CASE_SENSITIVE, isCaseSensitive);
	}
	
	public void setCustomParameter(final String name, final Object value) {
		super.addParameter(name, value);
	}
	
	public String getCustomParameter(final String name) {
		return super.getParameterValue(name);
	}
	
	public void setCustomParams(Map<String, String> stringMap) {
		super.getParamMap().putAll(stringMap);
	}
	
	public ParameterMap getCustomParams() {
		ParameterMap map = new ParameterMap();
		for(Entry<String, String> e : super.getParamMap().entrySet()) {
			if(!knownParams.contains(e.getKey())) {
				map.addParameter(e.getKey(), e.getValue());
			}
		}
		return map;
	}
}