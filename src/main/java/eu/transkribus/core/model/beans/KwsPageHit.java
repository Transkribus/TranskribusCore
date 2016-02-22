package eu.transkribus.core.model.beans;

import java.util.ArrayList;
import java.util.List;

public class KwsPageHit {
	private int colId;
	private int docId;
	private String docTitle;
	private int pageNr;
	private List<KwsHit> hitList = new ArrayList<>(0);
	private float score;

	public KwsPageHit(){}

	public KwsPageHit(int colId, int docId, String docTitle, int pageNr, float score, List<KwsHit> hitList) {
		this.colId = colId;
		this.docId = docId;
		this.docTitle = docTitle;
		this.pageNr = pageNr;
		this.score = score;
		this.hitList  = hitList;
	}

	public int getColId() {
		return colId;
	}

	public void setColId(int colId) {
		this.colId = colId;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public String getDocTitle() {
		return docTitle;
	}

	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}

	public int getPageNr() {
		return pageNr;
	}

	public void setPageNr(int pageNr) {
		this.pageNr = pageNr;
	}

	public List<KwsHit> getHitList() {
		return hitList;
	}

	public void setHitList(List<KwsHit> hitList) {
		this.hitList = hitList;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	@Override
	public String toString(){
		return  "KwsPageHit { colId = " + colId
				+ " | docId = " + docId
				+ " | docTitle = " + docTitle
				+ " | pageNr = " + pageNr
				+ " | pageHits = " + hitList.size()
				+ " | score = " + score
				+ " }";	
	}
}
