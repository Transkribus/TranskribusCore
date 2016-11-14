package eu.transkribus.core.model.beans;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentSelectionDescriptor {
	private int docId;
	@XmlElementWrapper(name="pageList")
	@XmlElement
	private List<PageDescriptor> pages = new LinkedList<>();

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public List<PageDescriptor> getPages() {
		return pages;
	}

	public void setPages(List<PageDescriptor> pages) {
		this.pages = pages;
	}
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class PageDescriptor {
		private int pageId;
		private int tsId;
		public int getPageId() {
			return pageId;
		}
		public void setPageId(int pageId) {
			this.pageId = pageId;
		}
		public int getTsId() {
			return tsId;
		}
		public void setTsId(int tsId) {
			this.tsId = tsId;
		}
	}
}
