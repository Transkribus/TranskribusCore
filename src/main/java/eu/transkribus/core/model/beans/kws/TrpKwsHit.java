package eu.transkribus.core.model.beans.kws;

import java.net.URL;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpKwsHit implements Comparable<TrpKwsHit> {
	
	/**
	 * keyword is necessary here for presenting the hit in a flat list
	 */
	private String keyword = null;
	private double confidence;
	private String bl;
	private String lineId;
	private URL imgUrl;
	private int pageId;
	private int pageNr;
	private String transcription;
	private int docId;
	private Integer colId;
	private Integer tsId;
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getBl() {
		return bl;
	}
	public void setBl(String bl) {
		this.bl = bl;
	}
	public String getLineId() {
		return lineId;
	}
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
	public URL getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(URL imgUrl) {
		this.imgUrl = imgUrl;
	}
	public int getPageId() {
		return pageId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	public int getPageNr() {
		return pageNr;
	}
	public void setPageNr(int pageNr) {
		this.pageNr = pageNr;
	}
	public String getTranscription() {
		return transcription;
	}
	public void setTranscription(String transcription) {
		this.transcription = transcription;
	}
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public Integer getColId() {
		return colId;
	}
	public void setColId(Integer colId) {
		this.colId = colId;
	}
	public double getConfidence() {
		return confidence;
	}
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	public Integer getTsId() {
		return tsId;
	}
	public void setTsId(Integer tsId) {
		this.tsId = tsId;
	}
	@Override
    public int compareTo(TrpKwsHit o) {
        return Double.compare(o.getConfidence(), this.confidence);
    }
}
