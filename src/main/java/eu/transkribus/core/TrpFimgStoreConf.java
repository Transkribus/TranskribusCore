package eu.transkribus.core;

import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.TrpFImagestore;

public class TrpFimgStoreConf {
		private static final Logger logger = LoggerFactory.getLogger(TrpFimgStoreConf.class);
		
		protected static Properties props = loadProps("fimgstore.properties");
		protected static String dbConfig = "trpProd";
		protected static TrpFImagestore fImagestore = null; 
		
		static{
			//initialize fimagestore client settings
			init();
		}

		public static void loadConfig(String newDbConfig) {
			if(newDbConfig == null) {
				logger.warn("Ignoring null value argument and keeping config: " + dbConfig);
				return;
			}
			if(newDbConfig.equals(dbConfig)) {
				logger.debug("Config already loaded: " + newDbConfig);
				return;
			}
			dbConfig = newDbConfig;
			try {
				init();
			} catch (IllegalStateException e) {
				throw new IllegalArgumentException("Not a valid config name: " + dbConfig, e);
			}
		}
		
		private static void init() {
			fImagestore = new TrpFImagestore();
			fImagestore.setHostName(TrpFimgStoreConf.getString(dbConfig + ".store_hostname"));
			fImagestore.setContext(TrpFimgStoreConf.getString(dbConfig + ".store_context"));		
			fImagestore.setPort(TrpFimgStoreConf.getInt(dbConfig + ".store_port"));
			if(StringUtils.isEmpty(fImagestore.getHostName())) {
				 throw new IllegalStateException("No filestore hostname found in config!");
			}
			if(StringUtils.isEmpty(fImagestore.getContext())) {
				fImagestore.setContext("/");
			}
			logger.debug("Initiated FImagestore config: " + fImagestore);
		}

		public static String getString(String name){
			return props.getProperty(name);
		}
		
		public static Integer getInt(String name){
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

		protected static Properties loadProps(String filename){
			logger.debug("Load properties file: " + filename);
			Properties props = new Properties();
			try (InputStream is = TrpFimgStoreConf.class.getClassLoader().getResourceAsStream(filename);) {
				props.load(is);
			} catch (Exception e) {
				logger.debug("Could not find properties file: " + filename);
			}
			return props;
		}

		public static TrpFImagestore getFImagestore() {
			return fImagestore;
		}

		public static TrpFImagestore getFImagestore(String dbConfigName) {
			loadConfig(dbConfigName);
			return fImagestore;
		}
}
