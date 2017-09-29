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
	private Integer htrOutputId;
	@Column
	private int pageId;
	@Column
	private Integer tsId;
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
	
	/*
	 * for KWS
	 * 
	 * colId is a String so it can hold a CSV list of several groups when searching in several collections
	 */
	@Transient
	@Column
	private String colId;
	@Transient
	@Column
	private int docId;
	@Transient
	@Column
	private int pageNr;
	
	public TrpHtrOutput(){}
	
	public TrpHtrOutput(int pageId, Integer tsId, String lineId, String provider, String key, int htrId) {
		this.pageId = pageId;
		this.tsId = tsId;
		this.lineId = lineId;
		this.provider = provider;
		this.key = key;
		this.htrId = htrId;
	}

	public Integer getHtrOutputId() {
		return htrOutputId;
	}

	public void setHtrOutputId(Integer htrOutputId) {
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

	public Integer getTsId() {
		return tsId;
	}

	public void setTsId(Integer tsId) {
		this.tsId = tsId;
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

	public String getColId() {
		return colId;
	}

	public void setColId(String colId) {
		this.colId = colId;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public int getPageNr() {
		return pageNr;
	}

	public void setPageNr(int pageNr) {
		this.pageNr = pageNr;
	}

	@Override
	public String toString() {
		return "TrpHtrOutput [htrOutputId=" + htrOutputId + ", pageId=" + pageId + ", tsId=" + tsId 
				+ ", lineId=" + lineId + ", provider="
				+ provider + ", key=" + key + ", url=" + url + ", htrId=" + htrId + ", colId=" + colId + ", docId="
				+ docId + ", pageNr=" + pageNr + "]";
	}

}
