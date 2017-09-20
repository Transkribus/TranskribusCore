package eu.transkribus.core.model.beans.kws;

import java.util.List;

public class TrpKeyWord {
	
	private String keyWord;
	private List<TrpKwsHit> hits;
	
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