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
public class DocumentUploadDescriptor implements Serializable {
	private static final long serialVersionUID = 2778247824554250512L;

	@XmlElement
	protected TrpDocMetadata md;
	
	@XmlElementWrapper(name="pageList")
	@XmlElement
	protected List<PageUploadDescriptor> pages;
	
	public DocumentUploadDescriptor() {
		md = null;
		pages = new LinkedList<>();
	}
	
	public TrpDocMetadata getMd() {
		return md;
	}

	public void setMd(TrpDocMetadata md) {
		this.md = md;
	}

	public List<PageUploadDescriptor> getPages() {
		return pages;
	}

	public void setPages(List<PageUploadDescriptor> pages) {
		this.pages = pages;
	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class PageUploadDescriptor implements Comparable<PageUploadDescriptor> {
		@XmlElement
		String fileName = null;
		@XmlElement
		String pageXmlName = null;
		@XmlElement
		boolean imgUploaded = false;
		@XmlElement
		boolean pageXmlUploaded = false;
		@XmlElement
		int pageNr = -1;
		@XmlElement
		String imgChecksum = null;
		@XmlElement
		String pageXmlChecksum = null;
		public PageUploadDescriptor() {
			fileName = null;
			pageXmlName = null;
			imgUploaded = false;
			pageXmlUploaded = false;
			pageNr = -1;
			imgChecksum = null;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getPageXmlName() {
			return pageXmlName;
		}
		public void setPageXmlName(String pageXmlName) {
			this.pageXmlName = pageXmlName;
		}
		public boolean isImgUploaded() {
			return imgUploaded;
		}
		public void setImgUploaded(boolean imgUploaded) {
			this.imgUploaded = imgUploaded;
		}
		public boolean isPageXmlUploaded() {
			return pageXmlUploaded;
		}
		public void setPageXmlUploaded(boolean pageXmlUploaded) {
			this.pageXmlUploaded = pageXmlUploaded;
		}
		public int getPageNr() {
			return pageNr;
		}
		public void setPageNr(int pageNr) {
			this.pageNr = pageNr;
		}
		public String getImgChecksum() {
			return imgChecksum;
		}
		public void setImgChecksum(String imgChecksum) {
			this.imgChecksum = imgChecksum;
		}
		public String getPageXmlChecksum() {
			return pageXmlChecksum;
		}
		public void setPageXmlChecksum(String pageXmlChecksum) {
			this.pageXmlChecksum = pageXmlChecksum;
		}
		/**
		 * Uses the page index for comparison
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(PageUploadDescriptor img) {
			if (this.getPageNr() > img.getPageNr()) {
				return 1;
			}
			if (this.getPageNr() < img.getPageNr()) {
				return -1;
			}
			return 0;
		}
	}
}
