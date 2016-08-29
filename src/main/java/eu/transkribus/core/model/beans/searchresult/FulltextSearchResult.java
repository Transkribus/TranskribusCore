package eu.transkribus.core.model.beans.searchresult;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FulltextSearchResult {
	
	protected String params;	
	protected long numResults;
	@XmlElementWrapper(name="pageHits")
	@XmlElement(name="PageHit")
	protected List<PageHit> pageHits = new ArrayList<>();
	
	public FulltextSearchResult(){}
	
	public List<PageHit> getPageHits() {
		return pageHits;
	}
	
	public void setPageHits(List<PageHit> pageHits) {
		this.pageHits = pageHits;
	}
	
	public long getNumResults() {
		return numResults;
	}
	
	public void setNumResults(long numResults) {
		this.numResults = numResults;
	}	
	
	public String getParams() {
		return params;
	}
	
	public void setParams(String params) {
		this.params = params;
	}

}
