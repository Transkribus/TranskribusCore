package eu.transkribus.core.model.beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentUploadDescriptor implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(DocumentUploadDescriptor.class);
	private static final long serialVersionUID = 2778247824554250512L;

	@XmlElement
	protected TrpDocMetadata md;
	
	@XmlElement
	protected Integer relatedUploadId;
	
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
	
	public Integer getRelatedUploadId() {
		return relatedUploadId;
	}

	public void setRelatedUploadId(Integer relatedUploadId) {
		this.relatedUploadId = relatedUploadId;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof DocumentUploadDescriptor)) {
			logger.debug("Type is different");
			return false;
		}
		DocumentUploadDescriptor d = (DocumentUploadDescriptor)obj;
		if(this.pages.size() != d.getPages().size()) {
			logger.debug("Nr. of pages is different");
			return false;
		}
		for(int i = 0; i < this.pages.size(); i++) {
			if(!this.pages.get(i).equals(d.getPages().get(i))) {
				logger.debug("Page " + this.pages.get(i).getPageNr() + " is different");
				return false;
			}
		}
		return true;
	}
	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class PageUploadDescriptor implements Comparable<PageUploadDescriptor> {
		@XmlElement
		String fileName = null;
		@XmlElement
		String pageXmlName = null;
		@XmlElement
		boolean pageUploaded = false;
		@XmlElement
		int pageNr = -1;
		@XmlElement
		String imgChecksum = null;
		@XmlElement
		String pageXmlChecksum = null;
		public PageUploadDescriptor() {
			fileName = null;
			pageXmlName = null;
			pageUploaded = false;
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
		public boolean isPageUploaded() {
			return pageUploaded;
		}
		public void setPageUploaded(boolean pageUploaded) {
			this.pageUploaded = pageUploaded;
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
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof PageUploadDescriptor)) {
				logger.debug("Type is different");
				return false;
			}
			PageUploadDescriptor p = (PageUploadDescriptor)obj;
			if(!StringUtils.equals(this.fileName, p.getFileName())){
				logger.debug("IMG filename is different");
				return false;
			}
			if(!StringUtils.equals(this.pageXmlName, p.getPageXmlName())){
				logger.debug("XML filename is different");
				return false;
			}
			if(this.pageNr != p.getPageNr()) {
				return false;
			}
			if(!StringUtils.equals(this.imgChecksum, p.getImgChecksum())){
				logger.debug("IMG checksum is different");
				return false;
			}
			if(!StringUtils.equals(this.pageXmlChecksum, p.getPageXmlChecksum())){
				logger.debug("XML checksum is different");
				return false;
			}
			return true;
		}	
	}
}
