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
public class SearchResult {
protected String queryString;

@XmlElementWrapper(name="hitList")
@XmlElement
protected List<Hit> hits = new ArrayList<>();

public SearchResult(){}

public List<Hit> getHits() {
	return hits;
}

public void setHits(List<Hit> hits) {
	this.hits = hits;
}

public String getQueryString() {
	return queryString;
}

public void setQueryString(String queryString) {
	this.queryString = queryString;
}
}
