package eu.transkribus.core.model.beans;

import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.exceptions.NullValueException;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;

@Entity
@Table(name = "pages")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpPage implements ITrpFile, Serializable, Comparable<TrpPage> {
	private static final long serialVersionUID = 1L;
	//objid for parentDoc is ID in DB
	@Id
	@Column
	private int pageId = -1;
	@Column
	private int docId;
	@Column
	private int pageNr;
	@Column(name = "imagekey")
	private String key = null; //The fimagestore key for getting the image file
	//imageUrl represents a link to the local filesystem or the link to the fimagestore
	@Column(name="IMAGE_ID")
	private int imageId;
	@Column
	@Transient
	private URL url;
	@Column
	@Transient
	private URL thumbUrl;
	
	private String md5Sum;
	@Column(name="IMGFILENAME")
	private String imgFileName = "";

	//TODO SortedList from DB. comparator?
	@XmlElementWrapper(name="tsList")
	@XmlElement
	private List<TrpTranscriptMetadata> transcripts;
	
	@Transient
	private int width;
	@Transient
	private int height;
	@Transient
	private java.sql.Timestamp created;
	
	@XmlElementWrapper(name="imageVersions")
	@XmlElement
	private List<TrpPageImageVersion> imageVersions;

	public TrpPage() {
		transcripts = new LinkedList<>();
	}
	
	public int getPageId() {
		return this.pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	
	public int getDocId() {
		return this.docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public int getPageNr() {
		return pageNr;
	}

	public void setPageNr(int pageNr) {
		this.pageNr = pageNr;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String imageKey) {
		this.key = imageKey;
	}
	
	public int getNTranscripts() {
		return transcripts!=null ? transcripts.size() : 0;
	}

	public List<TrpTranscriptMetadata> getTranscripts() {
		return transcripts;
	}
	
	public void setTranscripts(List<TrpTranscriptMetadata> transcripts) {
		this.transcripts = transcripts;
	}

	public List<TrpPageImageVersion> getImageVersions() {
		return imageVersions;
	}

	public void setImageVersions(List<TrpPageImageVersion> imageVersions) {
		this.imageVersions = imageVersions;
	}

	public URL getUrl() {
		return this.url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public URL getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(URL thumbUrl) {
		this.thumbUrl = thumbUrl;
	}

	public String getMd5Sum() {
		return this.md5Sum;
	}

	public void setMd5Sum(String md5Sum) {
		this.md5Sum = md5Sum;
		
	}
	
	public String getImgFileName() {
		return imgFileName;
	}

	public void setImgFileName(String imgFileName) {
		this.imgFileName = imgFileName;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public java.sql.Timestamp getCreated() {
		return created;
	}

	public void setCreated(java.sql.Timestamp created) {
		this.created = created;
	}

	public TrpTranscriptMetadata getCurrentTranscript() {
		List<TrpTranscriptMetadata> tList = getTranscripts();
		
		Collections.sort(tList, Collections.reverseOrder());
		return tList.get(0);
	}
	
	public TrpTranscriptMetadata getTranscriptWithStatus(String status) {
		List<TrpTranscriptMetadata> tList = getTranscripts();
		
		Collections.sort(tList, Collections.reverseOrder());
		for (TrpTranscriptMetadata md : tList){
			if (md.getStatus().getStr().equals(status)){
				return md;
			}
				
		}
		
		//if no transcript with this status was found return the latest one
		return tList.get(0);
	}
	
	public PcGtsType unmarshallCurrentTranscript() throws NullValueException, JAXBException {
		if (getCurrentTranscript()==null)
			throw new NullValueException("Current transcript is null!");
		
		return getCurrentTranscript().unmarshallTranscript();	
	}

	/**
	 * Uses the page number for comparison
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TrpPage p) {
		if (this.getPageNr() > p.getPageNr()) {
			return 1;
		}
		if (this.getPageNr() < p.getPageNr()) {
			return -1;
		}
		return 0;
	}
	
	@Override public String toString() {
		return "TrpPage [pageId=" + pageId + ", docId=" + docId + ", pageNr=" + pageNr + ", key=" + key + ", url=" + url + ", thumbUrl=" + thumbUrl
				+ ", md5Sum=" + md5Sum + ", imgFileName=" + imgFileName+", nTranscripts="+getNTranscripts()+"]";
	}
	
	public String toString(boolean withTranscripts) {
		String str = toString();
		
		if (withTranscripts) {
			str += "Transcripts: \n"+getTranscriptsStr();
		}
		
		return str;
	}
	
	public String getTranscriptsStr() {
		String str="";
		for (TrpTranscriptMetadata md : this.getTranscripts()) {
			 str += md.toString()+"\n";
		}
		
		return str;
	}

	public TrpImage getImage() {
		TrpImage i = new TrpImage();
		i.setImgFileName(imgFileName);
		i.setKey(key);
		i.setCreated(created);
		i.setWidth(width);
		i.setHeight(height);
		i.setUrl(url);
		return i;
	}
	
	public void setImage(TrpImage i) {
		this.imgFileName = i.getImgFileName();
		this.key = i.getKey();
		this.created = i.getCreated();
		this.width = i.getWidth();
		this.height = i.getHeight();
		this.url = i.getUrl();
	}

//	@Override
//	public String toString() {
//		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName() + " {" 
//				+ this.getPageId() + " - "+ this.getDocId() + " - ");
//		sb.append(this.getImgFileName() + " - ");
//		sb.append(this.getKey() + " - ");
//		sb.append(this.getPageNr() + " - ");
//		sb.append(this.getTranscripts().size() + " - ");
//		sb.append(this.getUrl() + " - ");
//		sb.append(this.getThumbUrl() + "}");
//		for (TrpTranscriptMetadata md : this.getTranscripts()) {
//			sb.append("\n\t\t" + md.toString());
//		}
//		return sb.toString();
//	}
}
