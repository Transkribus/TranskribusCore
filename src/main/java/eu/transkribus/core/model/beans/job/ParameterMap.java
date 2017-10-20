package eu.transkribus.core.model.beans.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps a HashMap and can be transmitted as entity in REST API calls both as JSON and XML
 * 
 * TODO write adapter to make this work as expected: https://stackoverflow.com/questions/8413608/sending-list-map-as-post-parameter-jersey
 * 
 * @author philip
 *
 */
@XmlRootElement(name="parameters")
@XmlAccessorType(XmlAccessType.FIELD)
public class ParameterMap {
	private static final Logger logger = LoggerFactory.getLogger(ParameterMap.class);	
//	@XmlElement(name="param")
	protected Map<String, String> paramMap = new HashMap<>();
	
	//keep that for Jaxb
	public ParameterMap() {}
	
	public ParameterMap(Map<String, Object> paramMap) {
		if(paramMap == null || paramMap.isEmpty()) {
			return;
		}
		for(Entry<String, Object> e : paramMap.entrySet()) {
			this.addParameter(e.getKey(), convertToString(e.getValue()));
		}
	}
	
	public void addParameter(final String name, Object value) {
		if(StringUtils.isEmpty(name)) {
			logger.warn("Ignoring noname parameter with value: " + value);
		} else {
			this.getParamMap().put(name, convertToString(value));
		}
	}

	public String getParameterValue(final String name) {
		return paramMap.get(name);
	}
	
	public Integer getIntParam(String key) {
		String propStr = getParameterValue(key);
		if(propStr == null) {
			return null;
		}
		Integer retVal = null;
		try {
			retVal = Integer.parseInt(propStr);
		} catch (NumberFormatException nfe) {}
		return retVal;
	}
	
	public Double getDoubleParam(String key) {
		String propStr = getParameterValue(key);
		if(propStr == null) {
			return null;
		}
		Double retVal = null;
		try {
			retVal = Double.parseDouble(propStr);
		} catch (NumberFormatException nfe) {}
		return retVal;
	}
	
	public Boolean getBoolParam(String key) {
		final String propStr = getParameterValue(key);
		if("1".equals(propStr)) {
			return true;
		}
		return Boolean.parseBoolean(propStr);
	}
	
	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	
	public boolean containsKey(final String name) {
		return paramMap.containsKey(name);
	}
	
	protected String convertToString(Object o) {
		if(o == null || o instanceof String) {
			return (String)o;
		} else if (o instanceof Number || o instanceof Boolean) {
			return ""+o;
		} else {
			logger.warn("Type " + o.getClass() + " not yet supported here.");
			return null;
		}
	}
	
	public void addDoubleParam(String key, Double value) {
		addDoubleParam(key, value, Double.MIN_VALUE, Double.MAX_VALUE);
	}

	public void addDoubleParam(String key, Double value, double min, double max) {
		if(value != null && value >= min && value <= max) {
			this.addParameter(key, value);
		} else {
			logger.warn("Ignoring illegal value: " + key + " -> " + value);
		}
	}
	
	public void addIntParam(String key, Integer value) {
		addIntParam(key, value, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	public void addIntParam(String key, Integer value, int min, int max) {
		if(value != null && value >= min && value <= max) {
			this.addParameter(key, value);
		} else {
			logger.warn("Ignoring illegal value: " + key + " -> " + value);
		}
	}

	public void addBoolParam(String key, boolean value) {
		addParameter(key, ""+value);
	}

	public boolean isEmpty() {
		return this.getParamMap().isEmpty();
	}

	/** flatten this map into a list to be compatible with Planet-style property arrays
	 * @return
	 */
	public List<String> toList() {
		List<String> list = new ArrayList<>(paramMap.size() * 2);
		for(Entry<String, String> e : paramMap.entrySet()) {
			list.add(e.getKey());
			list.add(e.getValue());
		}
		return list;
	}
}
