package eu.transkribus.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;

import eu.transkribus.core.io.util.TrpProperties;

public class JobDataUtils {
	private static final String LIST_SEP = ".";
	
	/**
	 * Set a list of strings to the properties.<br/>
	 * This method uses a separate key for each entry in the list and won't break on commas in the value.
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static Properties setStringList(Properties props, final String key, List<String> values) {
		if(props == null) {
			props = new Properties();
		}
		for(int i = 0; i < values.size(); i++) {
			final String value = values.get(i);
			if(value != null && !value.trim().isEmpty()) {
				props.setProperty(buildKey(key, i), value);
			}
		}
		return props;
	}
	/**
	 * get a list of strings from the properties, that have been set with {@link #setStringList(String, List)}. 
	 * In contrast to {@link #getCsvStringListProperty(String, boolean)}
	 * those methods use a separate key for each entry in the list and won't break on commas in the value.
	 * 
	 * @param key
	 * @param values
	 * @return
	 */
	public static List<String> getStringList(Properties props, final String key) {
		List<String> values = new LinkedList<>();
		if(StringUtils.isEmpty(key)) {
			return values;
		}
		int i = 0;
		String val;
		while((val = props.getProperty(buildKey(key, i))) != null) {
			values.add(i++, val);
		}
		return values;
	}
	
	public static <T> T getObject(TrpProperties jobProps, String key,
			Class<T> targetClazz, Class<?>... nestedClazzes) throws JAXBException {
		final String objectStr = jobProps.getString(key);
		T object = null;
		if(!StringUtils.isEmpty(objectStr)) {
			object = JaxbUtils.unmarshal(objectStr, targetClazz, 
					nestedClazzes);
		}
		return object;
	}
	
	public static <T> List<T> getObjectList(TrpProperties props, String key,
			Class<T> targetClazz, Class<?>... nestedClazzes) throws JAXBException {
		List<T> values = new LinkedList<>();
		if(StringUtils.isEmpty(key)) {
			return values;
		}
		int i = 0;
		String strVal;
		while((strVal = props.getProperty(buildKey(key, i))) != null) {
			final T object = JaxbUtils.unmarshal(strVal, targetClazz, 
					nestedClazzes);
			values.add(i++, object);
		}
		return values;
	}
	
	public static Map<String, String> getStringMap(Properties props, final String propKey) {
		Map<String, String> map = new HashMap<>();
		
		if(StringUtils.isEmpty(propKey)) {
			return map;
		}
		int i = 0;
		String key;
		while((key = props.getProperty(buildKeyOfKey(propKey, i))) != null) {
			final String value = props.getProperty(buildKeyOfValue(propKey, i));
			if(!StringUtils.isEmpty(value)) {
				map.put(key, value);
			}
			i++;
		}
		return map;
	}
	
	public static <T extends ParameterMap> T getParameterObject(Properties props, final String key, Class<T> targetClass) {
		Constructor<T> constructor;
		ParameterMap params = getParameterMap(props, key);
		try {
			constructor = targetClass.getConstructor();
			T object = constructor.newInstance();
			object.setParamMap(params.getParamMap());
			return object;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalArgumentException("Target class is invalid: no no-args constructor!");
		}
	}
	
	public static ParameterMap getParameterMap(Properties props, final String key) {
		Map <String, String> map = getStringMap(props, key);
		ParameterMap params = new ParameterMap();
		params.setParamMap(map);
		return params;
	}
	
	public static <T extends ParameterMap> Properties setParameterObject(Properties props, final String key, T object) {
		return setParameterMap(props, key, (ParameterMap)object);
	}
	
	public static Properties setParameterMap(Properties props, final String key, ParameterMap map) {
		return JobDataUtils.setStringMap(props, key, map.getParamMap());
	}
	
	public static Properties setStringMap(Properties props, final String key, Map<String, String> map) {
		if(props == null) {
			props = new Properties();
		}
		int i = 0;
		for(Entry<String, String> e : map.entrySet()) {
			if(e.getValue() != null && !e.getValue().trim().isEmpty()) {
				props.setProperty(
					buildKeyOfKey(key, i),
					e.getKey()
				);
				props.setProperty(
					buildKeyOfValue(key, i),
					e.getValue()
				);
				i++;
			}
		}
		return props;
	}
	
	/**
	 * Flatten a parameter map into a list, which is then compatible with Planet-style property arrays.
	 * 
	 * @param map
	 * @return
	 */
	public static List<String> convertToPropList(Map<String, String> map) {
		if(map == null || map.isEmpty()) {
			return new ArrayList<>(0);
		}
		List<String> list = new ArrayList<>(map.size() * 2);
		for(Entry<String, String> e : map.entrySet()) {
			if(!StringUtils.isEmpty(e.getValue())) {
				list.add(e.getKey());
				list.add(e.getValue());
			}
		}
		return list;
	}
	
	/**
	 * For map keys
	 * @param key
	 * @param i
	 * @return
	 */
	private static String buildKeyOfKey(String key, int i) {
		return buildKeyWithSuffix(key, i, "key");
	}
	
	/**
	 * For map values
	 * @param key
	 * @param i
	 * @return
	 */
	private static String buildKeyOfValue(String key, int i) {
		return buildKeyWithSuffix(key, i, "value");
	}
	
	private static String buildKey(String key, int i) {
		if(StringUtils.isEmpty(key)) {
			throw new IllegalArgumentException("key must not be empty.");
		}
		return key + LIST_SEP + i;
	}
	
	/** For storing a map
	 * @param key
	 * @param i
	 * @return
	 */
	private static String buildKeyWithSuffix(String key, int i, final String suffix) {
		if(StringUtils.isEmpty(suffix)) {
			throw new IllegalArgumentException("Suffix must not be empty.");
		}
		return buildKey(key, i) + LIST_SEP + suffix;
	}
}
