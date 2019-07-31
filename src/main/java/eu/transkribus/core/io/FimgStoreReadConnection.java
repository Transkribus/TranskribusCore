package eu.transkribus.core.io;

import org.dea.fimgstoreclient.FimgStoreGetClient;
import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.TrpFimgStoreConf;
import eu.transkribus.core.model.beans.TrpFImagestore;

/**
 * @deprecated use FImagestoreDao.newFImagestoreClient() or TrpServerConn.newFImagestoreGetClient() to get a configured client.
 */
public class FimgStoreReadConnection {
	private static final Logger logger = LoggerFactory.getLogger(FimgStoreReadConnection.class);
	
	private static FimgStoreReadConnection instance = null;
	
	protected TrpFImagestore store = null;
	
	protected static FimgStoreGetClient getter = null;
	
	protected FimgStoreReadConnection() {
		store = loadFImagestoreConfig();
	}
	
	public static FimgStoreReadConnection getInstance() {
		if(instance == null) {
			instance = new FimgStoreReadConnection();
		}
		return instance;
	}

	private TrpFImagestore loadFImagestoreConfig() {
		return TrpFimgStoreConf.getFImagestore();
	}
	
	public static void loadConfig(String dbConfig) {
		TrpFimgStoreConf.loadConfig(dbConfig);
		instance = null;
		getter = null;
		logger.debug("Reload FImagestore config: " + FimgStoreReadConnection.getInstance().loadFImagestoreConfig());
	}
	
	/**
	 * Returned config will never include username and password for storing files!
	 */
	public TrpFImagestore getFImagestore() {
		return store;
	}
	
	public static FimgStoreGetClient getGetClient(){
		if(getter == null){
			TrpFImagestore store = FimgStoreReadConnection.getInstance().getFImagestore();
			getter = new FimgStoreGetClient(store);
		}
		return getter;
	}
	
	public static void logCreds(){
		logger.debug(TrpFimgStoreConf.getFImagestore().toString());
	}
	
	public static FimgStoreUriBuilder getUriBuilder() {
		return getGetClient().getUriBuilder();
	}
	
	public static String getFimgStoreGetUrl(){
		return getUriBuilder().getBaseGetUri().toString();
	}
}
