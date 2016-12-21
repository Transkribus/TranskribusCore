package eu.transkribus.core.model.beans;

import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "HTR_OUTPUT")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpHtrOutput {
	
	@Id
	@Column(name="HTR_OUTPUT_ID")
	private int htrOutputId;
	@Column
	private int pageId;
	@Column
	private String lineId;
	@Column
	private String provider;
	@Column
	private String key;
	@Column
	@Transient
	private URL url; 
	@Column(name="HTR_ID")
	private int htrId;
	
	public TrpHtrOutput(){}
	
	public TrpHtrOutput(int pageId, String lineId, String provider, String key, int htrId) {
		this.pageId = pageId;
		this.lineId = lineId;
		this.provider = provider;
		this.key = key;
		this.htrId = htrId;
	}

	public int getHtrOutputId() {
		return htrOutputId;
	}

	public void setHtrOutputId(int htrOutputId) {
		this.htrOutputId = htrOutputId;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getHtrId() {
		return htrId;
	}

	public void setHtrId(int htrId) {
		this.htrId = htrId;
	}

}
