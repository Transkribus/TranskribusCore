package eu.transkribus.core.model.beans;

import java.net.URL;

public interface ITrpFile {
	public URL getUrl();
	public void setUrl(URL url);
	public String getKey();
	public void setKey(String key);
	public String getMd5Sum();
	public void setMd5Sum(String md5Sum);
}
