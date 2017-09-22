package eu.transkribus.core.model.beans.kws;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpKeyWord {
	
	private String keyWord;
	@XmlElementWrapper(name="hitList")
	@XmlElement
	private List<TrpKwsHit> hits = new ArrayList<>();
	
	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public List<TrpKwsHit> getHits() {
		return hits;
	}

	public void setHits(List<TrpKwsHit> hits) {
		this.hits = hits;
	}
}