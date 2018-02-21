package eu.transkribus.core.model.beans.searchresult;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KeywordHit {
	
	private String id;
	private String docTitle;
	private int pageNr;
	private ArrayList<Integer> colIds;
	private ArrayList<String> wordOptions;
	private String pageUrl;
	private String lineId;
	private String textCoords;
	private String word;
	private float probability;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDocTitle() {
		return docTitle;
	}
	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}
	public ArrayList<Integer> getColIds() {
		return colIds;
	}
	public void setColIds(ArrayList<Integer> colIds) {
		this.colIds = colIds;
	}
	public String getPageUrl() {
		return pageUrl;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	public int getPageNr() {
		return pageNr;
	}
	public void setPageNr(int pageNr) {
		this.pageNr = pageNr;
	}
	public String getTextCoords() {
		return textCoords;
	}
	public void setTextCoords(String textCoords) {
		this.textCoords = textCoords;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public float getProbability() {
		return probability;
	}
	public void setProbability(float probability) {
		this.probability = probability;
	}
	public String getLineId() {
		return lineId;
	}
	public void setLineId(String lineId) {
		this.lineId = lineId;
	}
	public ArrayList<String> getWordOptions() {
		return wordOptions;
	}
	public void setWordOptions(ArrayList<String> wordOptions) {
		this.wordOptions = wordOptions;
	}
	
	

}
