package eu.transkribus.core.model.beans.kws;

import java.util.ArrayList;
import java.util.List;

public class TrpKwsResult {
	
	private List<Integer> colIds = new ArrayList<>();
	private List<Integer> docIds = new ArrayList<>();
	private List<TrpKeyWord> keyWords = new ArrayList<>();
	
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
}
