package eu.transkribus.core.model.beans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.fop.afp.modca.PageDescriptor;

import eu.transkribus.core.util.CoreUtils;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentSelectionDescriptor implements Serializable {

	private static final long serialVersionUID = -4923573285902913207L;
	private int docId;
	@XmlElementWrapper(name="pageList")
	@XmlElement
	private List<PageDescriptor> pages = new LinkedList<>();
	
	public DocumentSelectionDescriptor() {
	}
	
	public DocumentSelectionDescriptor(int docId) {
		this.docId = docId;
	}
	
	public DocumentSelectionDescriptor(int docId, int pageId) {
		this.docId = docId;
		addPage(pageId);
	}

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
	
	public void addPage(PageDescriptor page) {
		pages.add(page);
	}
	
	public PageDescriptor addPage(int pageid) {
		PageDescriptor pd = new PageDescriptor(pageid);
		pages.add(pd);
		return pd;
	}
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class PageDescriptor implements Serializable  {
		private static final long serialVersionUID = -6203294525504243123L;
		
		private int pageId;
		private int tsId;
		
		private Set<String> regionIds = new HashSet<>();
		
		public PageDescriptor() {
		}
		
		public PageDescriptor(int pageId) {
			this.pageId = pageId;
		}
		
		public PageDescriptor(int pageId, int tsId) {
			this.pageId = pageId;
			this.tsId = tsId;
		}
		
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

		public Set<String> getRegionIds() {
			return regionIds;
		}
		
		public void setRegionIds(Set<String> regionIds) {
			this.regionIds = regionIds;
		}
		
		@Override
		public String toString() {
			return "PageDescriptor [pageId=" + pageId + ", tsId=" + tsId + ", regionIds="+CoreUtils.join(regionIds, ",", "(",")")+"]";
		}
	}

	@Override
	public String toString() {
		return "DocumentSelectionDescriptor [docId=" + docId + ", pages=" + "["+StringUtils.join(pages, ", ")+"]" + "]";
	}
	
}
