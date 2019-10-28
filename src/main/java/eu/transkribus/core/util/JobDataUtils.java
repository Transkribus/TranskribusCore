package eu.transkribus.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.util.TrpProperties;
import eu.transkribus.core.model.beans.job.JobError;
import eu.transkribus.core.model.beans.rest.ParameterMap;

public class JobDataUtils {
	private static final Logger logger = LoggerFactory.getLogger(JobDataUtils.class);
	
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
	
	
	/**
	 * Sets the parameter map object to the properties using prefixes for the acutal keys,
	 * so that it can be extracted later via {@link #getParameterMap(Properties, String)} using the specified key argument
	 * 
	 * @param props
	 * @param key the key to be the prefix for the actual new keys
	 * @param map
	 * @return
	 */
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
		if(key.contains(LIST_SEP)) {
			throw new IllegalArgumentException("The key must not include the seperator char: '" + LIST_SEP + "'");
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
	
	/**
	 * Merges parameter map entries into the Properties object's map. 
	 * In contrast to {@link #setParameterMap(Properties, String, ParameterMap)} the original ParameterMap can't be retrieved from the properties later on.
	 * 
	 * @param props
	 * @param params
	 * @param doOverwrite if true, then entries will be overwritten in props in case the same key occurs in props and params!
	 * @return
	 */
	public static Properties putEntriesFromMap(Properties props, ParameterMap params, final boolean doOverwrite) {
		if(props == null) {
			props = new Properties();
		}
		if(params == null || params.isEmpty()) {
			return props;
		}
		for(Entry<String, String> e : params.getParamMap().entrySet()) {
			if(!doOverwrite && props.containsKey(e.getKey())) {
				logger.debug("Omitting existing key: " + e.getKey() + " -> " + e.getValue());
				continue;
			}
			if (e.getValue()==null) {
				logger.warn("Omitting null value for key = "+e.getKey());
			}
			else if (e.getKey()==null) {
				logger.warn("Null key occured in ParameterMap - should not happen - ommiting");
			}
			else {
				props.put(e.getKey(), e.getValue());
			}
		}
		return props;
	}
	
	/**
	 * Extract the pageIds that are affected by the JobError object in the map.
	 * 
	 * @param failedPages the map containing the JobErrors: ( docId -> { pageId -> report } )
	 * @return a set containing the pageIds
	 */
	public static Set<Integer> extractPageIdsFromFailedPagesMap(final Map<Integer, Map<Integer, JobError>> failedPages) {
		Set<Integer> jobErrorPageIds = new HashSet<>();
		if(failedPages == null) {
			return jobErrorPageIds;
		}
		for(Entry<Integer, Map<Integer, JobError>> e : failedPages.entrySet()) {
			//collect pageIds from all detected errors
			jobErrorPageIds.addAll(e.getValue().keySet());
		}
		return jobErrorPageIds;
	}
	
	/**
	 * Extract the pageNrs that are affected by the JobError object in the map.
	 * 
	 * @param failedPages the map containing the JobErrors for a single document: ( pageId -> report )
	 * @return a set containing the pageNrs
	 */
	public static Set<Integer> extractPageNrsFromFailedPagesMap(final Map<Integer, JobError> failedPages) {
		if(failedPages == null) {
			return new HashSet<>();
		}
		//collect pageIds from all detected errors
		return failedPages.entrySet()
				.stream()
				.map(e -> e.getValue().getPageNr())
				.collect(Collectors.toSet());
	}
}
