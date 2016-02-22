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
@Table(name = "WORDGRAPHS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpWordgraph { // TODO implements ITrpFile {
	@Id
	@Column(name="PAGEID")
	private int pageId;
	@Column(name="wordgraphkey")
	private String wgKey;
	@Column(name="nBestKey")
	private String nBestKey;
	@Id
	@Column(name="lineid")
	private String lineId;
	@Column
	private String text;
	@Column
	@Transient
	private URL wordgraphUrl;
	@Column
	@Transient
	private URL nBestUrl;
	
	@Column(name="model_id")
	private Integer model_id; 

	public TrpWordgraph() {}
	public TrpWordgraph(final int pageId, final String wgKey, final String nBestKey, final String lineId, final String text, final Integer model_id){
		this.pageId = pageId;
		this.wgKey = wgKey;
		this.nBestKey = nBestKey;
		this.lineId = lineId;
		this.text = text;
		this.model_id = model_id;
	}
	
	public int getPageId() {
		return pageId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	public String getWgKey() {
		return wgKey;
	}
	public void setWgKey(String wgKey) {
		this.wgKey = wgKey;
	}
	public String getnBestKey() {
		return nBestKey;
	}
	public void setnBestKey(String nBestKey) {
		this.nBestKey = nBestKey;
	}
	public String getLineId() {
		return lineId;
	}
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public URL getWordgraphUrl() {
		return wordgraphUrl;
	}
	public void setWordgraphUrl(URL wordgraphUrl) {
		this.wordgraphUrl = wordgraphUrl;
	}
	public URL getnBestUrl() {
		return nBestUrl;
	}
	public void setnBestUrl(URL nBestUrl) {
		this.nBestUrl = nBestUrl;
	}
	public Integer getModel_id() {
		return model_id;
	}
	public void setModel_id(Integer model_id) {
		this.model_id = model_id;
	}
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName() + "{" +pageId + " - wordgraph=" + wgKey + " - " + lineId + " - " + text + " - model_id=" + model_id+"}";
	}
}
