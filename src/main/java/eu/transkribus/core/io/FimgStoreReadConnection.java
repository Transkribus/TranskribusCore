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
		return new FimgStoreUriBuilder(
				Scheme.https.toString(), TrpFimgStoreConf.STORE_HOST, 
				TrpFimgStoreConf.STORE_PORT, TrpFimgStoreConf.STORE_CONTEXT);
	}
}
