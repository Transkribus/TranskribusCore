package eu.transkribus.core.model.beans.kws;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpKwsResult {
	
	/*benchmark data*/
	@XmlAttribute
	private Long initTime;
	@XmlAttribute
	private Long searchTime;
	@XmlAttribute
	private Long enrichmentTime;
	
	@XmlElementWrapper(name="collectionList")
	@XmlElement
	private List<Integer> colIds = new ArrayList<>();
	@XmlElementWrapper(name="docList")
	@XmlElement
	private List<Integer> docIds = new ArrayList<>();
	@XmlElementWrapper(name="keyWordList")
	@XmlElement
	private List<TrpKeyWord> keyWords = new ArrayList<>();
	
	public Long getInitTime() {
		return initTime;
	}

	public void setInitTime(Long initTime) {
		this.initTime = initTime;
	}

	public Long getSearchTime() {
		return searchTime;
	}

	public void setSearchTime(Long searchTime) {
		this.searchTime = searchTime;
	}

	public Long getEnrichmentTime() {
		return enrichmentTime;
	}

	public void setEnrichmentTime(Long enrichmentTime) {
		this.enrichmentTime = enrichmentTime;
	}

	public List<Integer> getColIds() {
		return colIds;
	}

	public void setColIds(List<Integer> colIds) {
		this.colIds = colIds;
	}

	public List<Integer> getDocIds() {
		return docIds;
	}

	public void setDocIds(List<Integer> docIds) {
		this.docIds = docIds;
	}

	public List<TrpKeyWord> getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(List<TrpKeyWord> keyWords) {
		this.keyWords = keyWords;
	}

	public int getTotalNrOfHits() {
		int n = 0;
		for(TrpKeyWord k : getKeyWords()) {
			n += k.getHits().size();
		}
		return n;
	}
}
