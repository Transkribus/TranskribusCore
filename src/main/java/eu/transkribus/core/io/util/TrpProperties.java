package eu.transkribus.core.io.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.exceptions.ParsePropertiesException;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.GsonUtil;

/**
 * Utility class containing for reading property files and mapping values to various data types<br/>
 * FIXME this does not extend Properties and thus doesn't work interchangeably. E.g. the object can't be used with JobManager::createJob whereas Jobs internally use it.
 */
public class TrpProperties {
		private static final Logger logger = LoggerFactory.getLogger(TrpProperties.class);
		
		protected String filename;
		protected String string;
		
		protected Properties props;
		
		public static TrpProperties fromJsonMapStr(String jsonMapStr) {
			TrpProperties trpProps = new TrpProperties();
			
			Map<String, Object> props = GsonUtil.toMap2(jsonMapStr);
			if (props != null) {
				for (String key : props.keySet()) {
					trpProps.props.put(key, props.get(key));
				}
			}
			
			return trpProps;
		}
		
		public static TrpProperties fromFile(String filename) {
			return new TrpProperties(filename, true);
		}
		
		public static TrpProperties fromProperties(Properties props) {
			return new TrpProperties(props);
		}
		
		public TrpProperties() {
			props = new Properties();
		}
		
		public TrpProperties(Properties props) {
			this.props = CoreUtils.copyProperties(props);
		}
		
		public TrpProperties(String filename) throws ParsePropertiesException {
			this(filename, true);
		}
		
		public TrpProperties(String filenameOrString, boolean isFilename) throws ParsePropertiesException {
			if (isFilename) {
				this.filename = filenameOrString;
				loadPropsFromFilename();
			} else {
				this.string = filenameOrString;
				loadPropsFromString();
			}
		}
				
		public Object get(Object key) {
			return props.get(key);
		}
		
		public String getProperty(String key) {
			return props.getProperty(key);
		}
		
		public String getString(String name){
			return props.getProperty(name);
		}
				
		public Pattern getPattern(String name){
			return Pattern.compile(props.getProperty(name));
		}
		
		public String getOrDefault(String key, String defaultValue) {
			return (String) props.getOrDefault(key, defaultValue);
		}
		
		public boolean getOrDefault(String string, boolean defaultValue) {
			String v = (String) props.get(string);
			
			try {
				return Boolean.valueOf(v);
			} catch (Exception e) {
				return defaultValue;
			}
		}
		
		public int getOrDefault(String string, int defaultValue) {
			String v = (String) props.get(string);
			
			try {
				return Integer.valueOf(v);
			} catch (Exception e) {
				return defaultValue;
			}
		}
		
		public long getOrDefault(String string, long defaultValue) {
			String v = (String) props.get(string);
			
			try {
				return Long.valueOf(v);
			} catch (Exception e) {
				return defaultValue;
			}
		}
		
		/**
		 * Duplicate of getIntProperty()
		 * 
		 * @param name
		 * @return
		 */
		@Deprecated 
		public Integer getInt(String name) {
			final String prop = props.getProperty(name);
			Integer value = null;
			if(prop != null){
				try{
					value = Integer.parseInt(props.getProperty(name));
				} catch (NumberFormatException nfe){
					nfe.printStackTrace();
				}
			}
			return value;
		}
		
		/**
		 * Duplicate of getBoolProperty()
		 * @param name
		 * @return
		 */
		@Deprecated
		public boolean getBool(String name){
			final String value = props.getProperty(name);
			boolean bool = false;
			if(value.equals("1") || value.equalsIgnoreCase("true")){
				bool = true;
			}
			return bool;
		}
				
		public Integer getIntProperty(String key) {
			String propStr = getProperty(key);
			if(propStr == null) {
				return null;
			}
			Integer retVal = null;
			try {
				retVal = Integer.parseInt(propStr);
			} catch (NumberFormatException nfe) {}
			return retVal;
		}
		
		public Double getDoubleProperty(String key) {
			String propStr = getProperty(key);
			if(propStr == null) {
				return null;
			}
			Double retVal = null;
			try {
				retVal = Double.parseDouble(propStr);
			} catch (NumberFormatException nfe) {}
			return retVal;
		}
		
		/**
		 * Get a Boolean value from the Properties.<br>
		 * Evaluation result is based on {@link Boolean#parseBoolean(String)}. Additionally, "1" will be evaluated as "true".<br>
		 * A missing (null) value will be interpreted as false!  
		 * 
		 * @param key
		 * @return
		 */
		public Boolean getBoolProperty(String key) {
			final String propStr = getProperty(key);
			if("1".equals(propStr)) {
				return true;
			}
			return Boolean.parseBoolean(propStr);
		}
		
		public List<String> getCsvStringListProperty(String key, boolean trimEntries) {
			return CoreUtils.parseStringList(getProperty(key), trimEntries);
		}
		
		public List<Integer> getCsvIntListProperty(String key) {
			return CoreUtils.parseIntList(getProperty(key));
			
//			List<Integer> result = new LinkedList<>();
//			String str = getProperty(key);
//			if(str != null && !str.isEmpty()) {
//				String[] arr = str.split(",");
//				for(String s : arr) {
//					result.add(Integer.parseInt(s));
//				}
//			}
//			return result;
		}

		protected Properties loadPropsFromFilename() throws ParsePropertiesException {
			try {
				props = CoreUtils.loadProperties(filename);
				return props;
			} catch (IOException e) {
				throw new ParsePropertiesException(e);
			}
		}
		
		protected Properties loadPropsFromString() throws ParsePropertiesException {
			try {
				props = CoreUtils.readPropertiesFromString(string);
				return props;
			} catch (IOException e) {
				throw new ParsePropertiesException(e);
			}
		}

		public Properties getProperties() {
			return props;
		}

		public <T> T getJsonBean(String key, Class<T> clazz) {
			return GsonUtil.fromJson2(props.getProperty(key), clazz);
		}

		public String writeToString() {
			return CoreUtils.propertiesToString(props);
		}
		
		public Object setProperty(String key, String value) {
			if(key == null) {
				return null;
			}
			return props.setProperty(key, value);
		}
		
//		public void parseAndSetString(String propertyName) throws IOException, IllegalAccessException, InvocationTargetException {
//			String value = props.getProperty(propertyName);
//			
//			logger.info(propertyName+" = "+value);
//			
//			if (StringUtils.isEmpty(value))
//				throw new IOException(propertyName+" must be provided!");
//			
//			BeanUtils.setProperty(this, propertyName, value);
//		}
}
