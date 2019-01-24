package eu.transkribus.core.model.beans;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public interface ITrpFile {
	public URL getUrl();
	public void setUrl(URL url);
	public String getKey();
	public void setKey(String key);
	public String getMd5Sum();
	public void setMd5Sum(String md5Sum);
	
	/**
	 * Check key and URL protocol.<br/>
	 * This method returns false, if there is a filekey set.<br/>
	 * It returns true, if there is no filekey and the URL points to an existing file.<br/>
	 * 
	 * @return
	 * @throws IllegalStateException in case there are inconsistencies:
	 * <ul>
	 * <li>key of transcript is null, but the URL protocol is not "file://"</li>
	 * <li>key of transcript is null, but the file URL does not point to an existing file</li>
	 * </ul>
	 */
	default boolean isLocalFile() {
		if(getKey() == null) {
			if(!this.getFile().isFile()) {
				throw new IllegalStateException("Key of transcript is null, but file URL does not exist: " + getUrl().getFile());			
			}
			return true;
		} else {
			return false;
		}
	}
	
	default File getFile() {
		final String prot = getUrl().getProtocol();
		if(!"file".equals(prot)) {
			throw new IllegalStateException("Key of transcript is null, but URL protocol is not \"file\"!");
		}
		return FileUtils.toFile(getUrl());
	}
}
