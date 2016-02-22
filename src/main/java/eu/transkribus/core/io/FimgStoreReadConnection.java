package eu.transkribus.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.TrpFimgStoreConf;

import org.dea.fimgstoreclient.FimgStoreGetClient;

public class FimgStoreReadConnection {
	private static final Logger logger = LoggerFactory.getLogger(FimgStoreReadConnection.class);
	
	protected static FimgStoreGetClient getter = null;
	
	public static FimgStoreGetClient getGetClient(){
		if(getter == null){
			getter = new FimgStoreGetClient(TrpFimgStoreConf.STORE_HOST, 
					TrpFimgStoreConf.STORE_PORT, TrpFimgStoreConf.STORE_CONTEXT);
		}
		return getter;
	}
	
	public static void logCreds(){
		logger.debug(TrpFimgStoreConf.STORE_HOST + " - " +
				TrpFimgStoreConf.STORE_PORT + " - " + 
				TrpFimgStoreConf.STORE_CONTEXT);
	}
}
