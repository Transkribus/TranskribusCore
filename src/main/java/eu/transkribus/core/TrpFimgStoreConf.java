package eu.transkribus.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.regex.Pattern;

import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrpFimgStoreConf {
		private static final Logger logger = LoggerFactory.getLogger(TrpFimgStoreConf.class);
		
		protected static Properties props = new Properties();
		protected static String fimgStoreUrl = null;
		public static String STORE_HOST;
		public static String STORE_CONTEXT;		
		public static Integer STORE_PORT;
		
		static{
			props = loadProps("fimgstore.properties");
			//initialize fimagestore client vars
			STORE_HOST = TrpFimgStoreConf.getString("store_hostname");
			STORE_CONTEXT = TrpFimgStoreConf.getString("store_context");		
			STORE_PORT = TrpFimgStoreConf.getInt("store_port");
		}
		
		public static String getFimgStoreUrl(){
			if(fimgStoreUrl == null){
				FimgStoreUriBuilder uriBuilder = new FimgStoreUriBuilder("https", STORE_HOST, STORE_PORT, STORE_CONTEXT);
				try {
					fimgStoreUrl = uriBuilder.getBaseUri().toString();
				} catch(URISyntaxException e){
					logger.error("fimagstore settings in trp.properties are not correct! TRP will not function correctly!!");
					e.printStackTrace();
				}
			}
			return fimgStoreUrl;
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
		
		public static Pattern getPattern(String name){
			return Pattern.compile(props.getProperty(name));
		}
		
		public static boolean getBool(String name){
			final String value = props.getProperty(name);
			boolean bool = false;
			if(value.equals("1") || value.equalsIgnoreCase("true")){
				bool = true;
			}
			return bool;
		}

		protected static Properties loadProps(String filename){
			logger.debug("Load properties file: " + filename);
//			if(props == null){
			props = new Properties();
//			}
			InputStream is = null;
			try{
				is = TrpFimgStoreConf.class.getClassLoader().getResourceAsStream(filename);
				props.load(is);
			} catch (Exception e) {
				logger.debug("Could not find properties file: " + filename);
			}
			finally{
				try {
					is.close();
				} catch (IOException e) {
					logger.warn("Could not close resource stream");
					//ignore
				}
			}
			return props;
		}
}
