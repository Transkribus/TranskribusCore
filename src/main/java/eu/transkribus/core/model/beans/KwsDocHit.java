package eu.transkribus.core.model.beans;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class KwsDocHit {
	private int colId;
	private int docId;
	private String docTitle;
	private float score;
	private List<KwsPageHit> hitList = new ArrayList<>(0);

	public KwsDocHit(){}

	public KwsDocHit(int colId, int docId, String docTitle, float score, List<KwsPageHit> hitList) {
		this.colId = colId;
		this.docId = docId;
		this.docTitle = docTitle;
		this.score = score;
		this.hitList = hitList;
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

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public List<KwsPageHit> getHitList() {
		return hitList;
	}

	public void setHitList(List<KwsPageHit> hitList) {
		this.hitList = hitList;
	}
	
//	@Override
//	public int compareTo(KwsDocHit h) {
//		if (this.getScore() > h.getScore()) {
//			return 1;
//		}
//		if (this.getScore() < h.getScore()) {
//			return -1;
//		}
//		return 0;
//	}
	
	@Override
	public String toString(){
		return  "KwsDocHit { colId = " + colId
				+ " | docId = " + docId
				+ " | docTitle = " + docTitle
				+ " | pageHits = " + hitList.size()
				+ " | score = " + score
				+ " }";
		
		
	}
}
