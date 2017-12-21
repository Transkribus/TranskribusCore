package eu.transkribus.core.model.beans.kws;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpKeyWord {
	private static final Logger logger = LoggerFactory.getLogger(TrpKeyWord.class);
	private String keyWord;
	private Integer nrOfHits;
	
	@XmlElementWrapper(name="hitList")
	@XmlElement
	private List<TrpKwsHit> hits = new ArrayList<>();
	
	public TrpKeyWord() {}
	
	public TrpKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	
	public TrpKeyWord(String keyWord, Integer nrOfHits) {
		this(keyWord);
		this.nrOfHits = nrOfHits;
	}
	
	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public Integer getNrOfHits() {
		return nrOfHits;
	}

	public void setNrOfHits(Integer nrOfHits) {
		this.nrOfHits = nrOfHits;
	}
	
	public List<TrpKwsHit> getHits() {
		return hits;
	}

	public void setHits(List<TrpKwsHit> hits) {
		this.hits = hits;
	}
	
	public void beforeMarshal(Marshaller m) {
		logger.trace("running JAXB event callback method");
		if(hits.isEmpty()) {
			logger.trace("Hiding empty hitList");
			//empty list is removed in order to omit an empty XML wrapper element
			hits = null;
		}
	}
}