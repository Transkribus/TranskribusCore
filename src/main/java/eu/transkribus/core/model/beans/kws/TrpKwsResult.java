package eu.transkribus.core.model.beans.kws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpKwsResult extends TrpKwsQuery {
	
	/*benchmark data*/
	@XmlAttribute
	private Long initTime;
	@XmlAttribute
	private Long searchTime;
	@XmlAttribute
	private Long enrichmentTime;

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

	public int getTotalNrOfHits() {
		int n = 0;
		for(TrpKeyWord k : getKeyWords()) {
			n += k.getHits().size();
		}
		return n;
	}
}
