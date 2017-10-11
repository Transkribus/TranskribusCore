package eu.transkribus.core.model.beans;

@Deprecated
public class KwsHit implements Comparable<KwsHit> {
	private int colId;
	private int docId;
	private String docTitle;
	private int pageNr;
	private String lineId;
	private float score;
	private int frameStart;
	private int frameEnd;

	public KwsHit(){}

	public KwsHit(int colId, int docId, int pageNr, String docTitle, String lineId, float score,
			int frameStart, int frameEnd) {
		this.colId = colId;
		this.docId = docId;
		this.pageNr = pageNr;
		this.docTitle = docTitle;
		this.lineId = lineId;
		this.score = score;
		this.frameStart = frameStart;
		this.frameEnd = frameEnd;
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

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public int getFrameStart() {
		return frameStart;
	}

	public void setFrameStart(int frameStart) {
		this.frameStart = frameStart;
	}

	public int getFrameEnd() {
		return frameEnd;
	}

	public void setFrameEnd(int frameEnd) {
		this.frameEnd = frameEnd;
	}
	
	@Override
	public int compareTo(KwsHit h) {
		if (this.getScore() > h.getScore()) {
			return 1;
		}
		if (this.getScore() < h.getScore()) {
			return -1;
		}
		return 0;
	}
	
	@Override
	public String toString(){
		return "KwsHit { colId = " + colId
				+ " | docId = " + docId
				+ " | docTitle = " + docTitle
				+ " | pageNr = " + pageNr
				+ " | lineId = " + lineId
				+ " | score = " + score
				+ " | frameStart = " + frameStart
				+ " | frameEnd = " + frameEnd
				+ " }";
	}
}
