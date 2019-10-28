package eu.transkribus.core.model.beans;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.GsonUtil;
import eu.transkribus.core.util.JaxbUtils;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentSelectionDescriptor implements Serializable {
	private static final long serialVersionUID = -4923573285902913207L;

	private int docId;
	
	@XmlElementWrapper(name="pageList")
	@XmlElement
	private List<PageDescriptor> pages = new LinkedList<>();
	
//	protected String pagesStr="hello world";
	
	public DocumentSelectionDescriptor() {}
	
	public DocumentSelectionDescriptor(int docId) {
		this();
		this.docId = docId;
	}
	
	public DocumentSelectionDescriptor(int docId, int pageId) {
		this(docId);
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
	
	public void addPages(List<Integer> pageIds) {
		pageIds.stream().forEach(pid -> addPage(pid));
	}
	
	public static List<DocumentSelectionDescriptor> fromDocIds(int... docIds) throws IOException {
		List<DocumentSelectionDescriptor> dsds = new ArrayList<>();
		for (int docId : docIds) {
			dsds.add(new DocumentSelectionDescriptor(docId));
		}
		return dsds;
	}
	
	public static DocumentSelectionDescriptor fromDocAndPagesStr(TrpDoc doc, String pagesStr) throws IOException {
		DocumentSelectionDescriptor dd = new DocumentSelectionDescriptor(doc.getId());
		if (!StringUtils.isEmpty(pagesStr)) {
			for (int pageIndex : CoreUtils.parseRangeListStrToList(pagesStr, doc.getNPages())) {
				for (TrpPage page : doc.getPages()) {
					if (page.getPageNr() == pageIndex+1) {
						dd.addPage(page.getPageId());
						break;
					}
				}
			}
		}
		
		return dd;
	}
	
	public static List<Integer> extractPageIndices(DocumentSelectionDescriptor dsd, TrpDoc doc) {
		if (dsd.getPages() == null || dsd.getPages().isEmpty())
			return null;
		
		List<Integer> indices = new ArrayList<>();
		for (PageDescriptor pd : dsd.getPages()) {
			try {
				TrpPage p = doc.getPages().stream().filter(page -> { return page.getPageId()==pd.getPageId(); }).findFirst().get();
				indices.add(p.getPageNr()-1);
			} catch (NoSuchElementException e) {
				continue;
			}
		}
		
		return indices;
	}
		
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class PageDescriptor implements Serializable  {
		private static final long serialVersionUID = -6203294525504243123L;
		
		private int pageId;
		private Integer tsId;
		
		private Set<String> regionIds = new HashSet<>();
		
		public PageDescriptor() {
		}
		
		public PageDescriptor(int pageId) {
			this.pageId = pageId;
		}
		
		public PageDescriptor(int pageId, Integer tsId) {
			this.pageId = pageId;
			this.tsId = tsId;
		}
		
		public int getPageId() {
			return pageId;
		}
		public void setPageId(int pageId) {
			this.pageId = pageId;
		}
		public Integer getTsId() {
			return tsId;
		}
		public void setTsId(Integer tsId) {
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
		
		@Override
		public boolean equals(Object other) {
			if (!(other instanceof PageDescriptor)) {
				return false;
			}
			
			PageDescriptor pd = (PageDescriptor) other;
			if (pd.pageId != pageId) {
				return false;
			}
			
			if (pd.tsId != tsId) {
				return false;
			}
			
			if (pd.regionIds.size() != regionIds.size()) {
				return false;
			}
			
			for (String rid : pd.regionIds) {
				if (!regionIds.contains(rid))
					return false;
			}

			return true;
		}
	}

	@Override
	public String toString() {
		return "DocumentSelectionDescriptor [docId=" + docId + ", pages=" + "["+StringUtils.join(pages, ", ")+"]" + "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof DocumentSelectionDescriptor)) {
			return false;
		}
		
		DocumentSelectionDescriptor dd = (DocumentSelectionDescriptor) other;
		if (dd.docId != docId) {
			return false;
		}
		
		if (dd.getPages().size() != getPages().size()) {
			return false;
		}
		
		for (int i=0; i<dd.getPages().size(); ++i) {
			if (!dd.getPages().get(i).equals(getPages().get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		DocumentSelectionDescriptor d = new DocumentSelectionDescriptor(10);
		d.addPage(10).setTsId(102);
		d.addPage(14).setPageId(300);
		d.addPage(15).setPageId(301);
//		pd.setTsId(102);
		
		List<DocumentSelectionDescriptor> dsds = new ArrayList<>();
		dsds.add(d);
		dsds.add(d);
		
//		System.out.println("d = "+JaxbUtils.marshalToString(d));
		System.out.println("d = "+JaxbUtils.marshalToJsonString(dsds, true));
//		System.out.println("d = "+GsonUtil.toJson(d));
	}
	
}
