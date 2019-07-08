package eu.transkribus.core.model.beans.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.util.TrpProperties;

/**
 * Implements {@link AJaxbMap} and adds parameter-related helper methods.
 * @author philip
 *
 */
@XmlRootElement(name="parameters")
public class ParameterMap extends AJaxbMap {
	private static final Logger logger = LoggerFactory.getLogger(ParameterMap.class);
	
	//keep that for Jaxb
	public ParameterMap() {
		super();
	}
	
	public ParameterMap(Map<String, Object> paramMap) {
		super();
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
		} else if (value == null) {
			if(map.containsKey(name)) {
				logger.debug("Removing existing parameter value as null value was passed: " + name + " -> old = " + value + " | new = null");
				map.remove(name);
			} else {
				logger.debug("Ommiting null value to be set in parameter map: " + name + " -> " + value);
			}
		} else {
			this.getParamMap().put(name, convertToString(value));
		}
	}

	public String getParameterValue(final String name) {
		return map.get(name);
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
	
	public String remove(final String key) {
		return map.remove(key);
	}
	
	public Map<String, String> getParamMap() {
		return map;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.map = paramMap;
	}
	
	public boolean containsKey(final String name) {
		return map.containsKey(name);
	}
	
	protected String convertToString(Object o) {
		if(o == null || o instanceof String) {
			return (String)o;
		} else if (o instanceof Number || o instanceof Boolean || o instanceof Enum) {
			return ""+o;
		} else {
			logger.warn("Type " + o.getClass() + " not yet supported here.");
			return null;
		}
	}
	
	public void addDoubleParam(String key, Double value) {
		addDoubleParam(key, value, -Double.MAX_VALUE, Double.MAX_VALUE);
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
		List<String> list = new ArrayList<>(map.size() * 2);
		for(Entry<String, String> e : map.entrySet()) {
			list.add(e.getKey());
			list.add(e.getValue());
		}
		return list;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(this.getClass().getName() + " {\n");
		for(Entry<String, String> e : map.entrySet()) {
			sb.append("\t" + e.getKey() + " = " + e.getValue() + "\n");
		}
		return sb.toString() + "}";
	}
	
	/**
	 * Get a Planet-style property array
	 * 
	 * @return
	 */
	public String[] toArray() {
		return this.toList().toArray(new String[map.size() * 2]);
	}
	
	public Properties toProperties() {
		Properties props = new Properties();
		props.putAll(map);
		return props;
	}
	
	public TrpProperties toTrpProperties() {
		return new TrpProperties(toProperties());
	}
	
	public static void main(String[] args) {
		System.out.println(-Double.MIN_VALUE);
	}
}
