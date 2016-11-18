package eu.transkribus.core.model.beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentSelectionDescriptor implements Serializable {

	private static final long serialVersionUID = -4923573285902913207L;
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
	public static class PageDescriptor implements Serializable  {
		private static final long serialVersionUID = -6203294525504243123L;
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
