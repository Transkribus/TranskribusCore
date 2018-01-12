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
public class PageHit {
	private long docId;
	private long pageNr;
	private String docTitle;
	private String pageUrl;	
	@XmlElementWrapper(name="collectionIds")
	@XmlElement(name="collectionId")
	private List<Integer> collectionIds = new ArrayList<>(0);
	private List<String> highlights = new ArrayList<>(0);
	private List<String> wordCoords = new ArrayList<>(0);
	
	public List<Integer> getCollectionIds() {
		return collectionIds;
	}

	public void setCollectionIds(List<Integer> collectionIds) {
		this.collectionIds = collectionIds;
	}	
	
	public PageHit(){}
	
	public List<String> getHighlights() {
		return highlights;
	}

	public void setHighlights(List<String> highlights) {
		this.highlights = highlights;
	}

	public long getPageNr() {
		return pageNr;
	}

	public void setPageNr(long pageNr) {
		this.pageNr = pageNr;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}

	public List<String> getWordCoords() {
		return wordCoords;
	}

	public void setWordCoords(List<String> wordCoords) {
		this.wordCoords = wordCoords;
	}

	@Override
	public String toString() {
		return "PageHit [docId=" + docId + ", pageNr=" + pageNr + ", pageUrl=" + pageUrl + ", highlights=" + highlights
				+ ", wordCoords=" + wordCoords + "]";
	}

	public String getDocTitle() {
		return docTitle;
	}

	public void setDocTitle(String docTitle) {
		this.docTitle = docTitle;
	}

}
