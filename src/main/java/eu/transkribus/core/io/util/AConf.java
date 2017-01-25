package eu.transkribus.core.io.util;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.util.CoreUtils;

/**
 * Abstract utility class containing static methods for reading property files and mapping values to various data types
 */
public abstract class AConf {
		private static final Logger logger = LoggerFactory.getLogger(AConf.class);
		
		protected String filename;
		protected Properties props;
		
		protected AConf(String filename) throws IOException {
			this.filename = filename;
			loadProps();
		}
		
		public String getString(String name){
			return props.getProperty(name);
		}
		
		public Integer getInt(String name){
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
		
		public Pattern getPattern(String name){
			return Pattern.compile(props.getProperty(name));
		}
		
		public boolean getBool(String name){
			final String value = props.getProperty(name);
			boolean bool = false;
			if(value.equals("1") || value.equalsIgnoreCase("true")){
				bool = true;
			}
			return bool;
		}

		protected Properties loadProps() throws IOException {
			props = CoreUtils.loadProperties(filename);
			return props;
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
