package eu.transkribus.core.model.beans.searchresult;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.pagecontent_trp.TrpWordType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PageHit {
	private long docId;
	private long pageNr;
	private String docTitle;
	private String pageUrl;	
	@XmlElementWrapper(name="collectionIds")
	@XmlElement(name="collectionId")
	private ArrayList<Integer> collectionIds = new ArrayList<>();
	private ArrayList<String> highlights = new ArrayList<>();
	private ArrayList<String> wordCoords = new ArrayList<>();
	
	public ArrayList<Integer> getCollectionIds() {
		return collectionIds;
	}

	public void setCollectionIds(ArrayList<Integer> collectionIds) {
		this.collectionIds = collectionIds;
	}	
	
	public PageHit(){}
	
	public ArrayList<String> getHighlights() {
		return highlights;
	}

	public void setHighlights(ArrayList<String> highlights) {
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

	public ArrayList<String> getWordCoords() {
		return wordCoords;
	}

	public void setWordCoords(ArrayList<String> wordCoords) {
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
