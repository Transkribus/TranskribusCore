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
public class TrpDocStructure implements Serializable {
	private static final long serialVersionUID = 2778247824554250512L;

	@XmlElement
	protected TrpDocMetadata md;
	
	@XmlElementWrapper(name="imageList")
	@XmlElement
	protected List<TrpDocStructureImage> images;
	
	public TrpDocStructure() {
		md = null;
		images = new LinkedList<>();
	}
	
	public TrpDocMetadata getMd() {
		return md;
	}

	public void setMd(TrpDocMetadata md) {
		this.md = md;
	}

	public List<TrpDocStructureImage> getImages() {
		return images;
	}

	public void setImages(List<TrpDocStructureImage> images) {
		this.images = images;
	}

	public static class TrpDocStructureImage implements Comparable<TrpDocStructureImage> {
		@XmlElement
		String fileName = null;
		@XmlElement
		String pageXmlName = null;
		@XmlElement
		boolean imgUploaded = false;
		@XmlElement
		boolean pageXmlUploaded = false;
		@XmlElement
		int index = -1;
		@XmlElement
		String imgChecksum = null;
		@XmlElement
		String pageXmlChecksum = null;
		public TrpDocStructureImage() {
			fileName = null;
			pageXmlName = null;
			imgUploaded = false;
			pageXmlUploaded = false;
			index = -1;
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
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
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
		public int compareTo(TrpDocStructureImage img) {
			if (this.getIndex() > img.getIndex()) {
				return 1;
			}
			if (this.getIndex() < img.getIndex()) {
				return -1;
			}
			return 0;
		}
	}
}
