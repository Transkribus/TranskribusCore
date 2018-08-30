package eu.transkribus.core.model.beans.searchresult;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KeywordSearchResult {
	
	private String params;
	private long numResults;
	private List<KeywordPageHit> keywordHits = new ArrayList<>();
	
	
	public KeywordSearchResult(){}


	public long getNumResults() {
		return numResults;
	}


	public void setNumResults(long numResults) {
		this.numResults = numResults;
	}


	public List<KeywordPageHit> getKeywordHits() {
		return keywordHits;
	}


	public void setKeywordHits(List<KeywordPageHit> keywordHits) {
		this.keywordHits = keywordHits;
	}


	public String getParams() {
		return params;
	}


	public void setParams(String params) {
		this.params = params;
	}
	
	

}
