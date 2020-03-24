package eu.transkribus.core.model.beans.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.util.TrpProperties;
import eu.transkribus.core.util.JobDataUtils;

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
	
	public ParameterMap(Map<String, ? extends Object> paramMap) {
		super();
		if(paramMap == null || paramMap.isEmpty()) {
			return;
		}
		for(Entry<String, ? extends Object> e : paramMap.entrySet()) {
			this.addParameter(e.getKey(), convertToString(e.getValue()));
		}
	}
	
	public void addParameterFromSingleLine(String parameterLine, String separator) {
		parameterLine = parameterLine.trim();
		int i = parameterLine.indexOf(separator);
		if (i >= 0 && i+1<parameterLine.length()) {
			addParameter(parameterLine.substring(0, i), parameterLine.substring(i+1));
		}
		else { // separator not found -> add parameter with empty value!
			addParameter(parameterLine, "");
		}
	}
	
	public void addAll(ParameterMap map) {
		addAll(map.getParamMap());
	}
	
	public void addAll(Map<String, ? extends Object> map) {
		for (String key : map.keySet()) {
			addParameter(key, map.get(key));
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

	/**
	 * Add a list of string values to the map, associated with name.
	 * No escaping of values needed, which would be the case when using csv format.
	 * @see {@link JobDataUtils#setStringListToMap(Map, String, List)}
	 * 
	 * @param name the parameter key
	 * @param valueList list of string values
	 */
	public void addStringListParameter(final String name, List<String> valueList) {
		if(CollectionUtils.isEmpty(valueList)) {
			logger.debug("Parameter value list is null or empty. Doing nothing.");
			return;
		}
		Map<String, String> props = JobDataUtils.setStringListToMap(null, name, valueList);
		this.addAll(props);
	}
	
	public String getParameterValue(final String name) {
		return map.get(name);
	}
	
	public String getParameterValue(String name, String defaultValue) {
		String value = map.get(name);
		return value != null ? value : defaultValue;
	}
	
	public Integer getIntParam(String key, Integer defaultValue) {
		Integer val = getIntParam(key);
		return val!=null ? val : defaultValue;
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
	
	public Boolean getBoolParam(String key, boolean defaultValue) {
		String propStr = getParameterValue(key);
		if (propStr == null) {
			return defaultValue;
		}
		return getBoolParam(key);
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
	
	public String toSimpleString(String separator) {
		String str="";
		for (String key : map.keySet()) {
			str+=key;
			String val = map.get(key);
			if (!StringUtils.isEmpty(val)) {
				str+=" "+val+separator;
			}
			else {
				str+=separator;
			}
		}
		str = StringUtils.removeEnd(str, separator);
		str = str.trim();
		
		return str;
	}
	
	public static ParameterMap fromSingleLineString(String str, String argumentPrefix, String separator) {
		ParameterMap pm = new ParameterMap();
		
		String currentArg=null, currentVal="";
		for (String s : str.split(separator)) {
			s = s.trim();
			if (StringUtils.isEmpty(s)) {
				continue;
			}
			
			if (s.startsWith(argumentPrefix)) {
				if (currentArg!=null) {
					logger.debug("adding par: '"+currentArg+"', val = '"+currentVal+"'");
					pm.addParameter(currentArg, currentVal);
					currentVal="";
				}
				currentArg=s;
			}
			else {
				currentVal = StringUtils.isEmpty(currentVal) ? s : currentVal+separator+s;
			}
		}
		if (currentArg!=null) { // there was an empty value argument at the last place
			pm.addParameter(currentArg, currentVal);
		}
		
		return pm;
	}	
	
	public String toSingleLineString() {
		return toSimpleString(" ");
	}
	
	public String toSimpleStringLineByLine() {
		return toSimpleString("\n");
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
	
//	public static void main(String[] args) {
//		System.out.println(-Double.MIN_VALUE);
//		
//		ParameterMap m = new ParameterMap();
//		m.addBoolParam("key1", true);
//		m.addParameter("asdf", "");
////		m.addParameter("null_valu", null);
//		m.addDoubleParam("res", 1.3);
//		m.addDoubleParam("adsf", 1.3);
//		
//		for (String k: m.keySet()) {
//			System.out.println("k = "+k);
//		}
//		
//	}
}
