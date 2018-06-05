package eu.transkribus.core.io;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.dea.fimgstoreclient.AbstractHttpClient.Scheme;
import org.dea.fimgstoreclient.FimgStoreGetClient;
import org.dea.fimgstoreclient.beans.FimgStoreFileMd;
import org.dea.fimgstoreclient.beans.FimgStoreImgMd;
import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;
import org.dea.fimgstoreclient.utils.FimgStoreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.TrpFimgStoreConf;
import eu.transkribus.core.model.beans.TrpFImagestore;

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
			getter = new FimgStoreGetClient(store.getHostName(), 
					store.getPort(), store.getContext());
		}
		return getter;
	}
	
	public static void logCreds(){
		logger.debug(TrpFimgStoreConf.getFImagestore().toString());
	}
	
	public static FimgStoreFileMd getFileMd(URL url) throws IOException {
		FimgStoreGetClient getter = new FimgStoreGetClient(url);
		final String key;
		try {
			key = FimgStoreUtils.extractKey(url);
		} catch (URISyntaxException e) {
			throw new IOException("Could not extract key from url: " + url.toString(), e);
		}

		return getter.getFileMd(key);
	}
	
	public static FimgStoreImgMd getImgMd(URL url) throws IOException {
		FimgStoreFileMd md = getFileMd(url);

		if (!(md instanceof FimgStoreImgMd)) {
			throw new IOException("File with key " + md.getKey() + " is not an image!");
		}
		FimgStoreImgMd imgMd = (FimgStoreImgMd) md;
		return imgMd;
	}
	
	public static FimgStoreUriBuilder getUriBuilder() {
		TrpFImagestore store = FimgStoreReadConnection.getInstance().getFImagestore();
		return new FimgStoreUriBuilder(
				Scheme.https.toString(), store.getHostName(), 
				store.getPort(), store.getContext());
	}
	
	public static String getFimgStoreUrl(){
		FimgStoreUriBuilder uriBuilder = getUriBuilder();
		String fimgStoreUrl;
		try {
			fimgStoreUrl = uriBuilder.getBaseUri().toString();
		} catch(URISyntaxException e){
			throw new IllegalStateException("fimagstore settings in properties are not correct! TRP will not function correctly!!", e);
		}
		return fimgStoreUrl;
	}
}
