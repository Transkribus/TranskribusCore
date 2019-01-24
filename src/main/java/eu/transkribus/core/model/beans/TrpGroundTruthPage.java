package eu.transkribus.core.model.beans;

import java.net.URL;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.enums.EditStatus;

@Entity
@Table(name = "GROUND_TRUTH")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpGroundTruthPage extends TrpTranscriptStatistics implements ITrpFile, Comparable<TrpGroundTruthPage> {

	/**
	 * GT pages are labeled according the content of the XML that is considered as
	 * ground truth
	 */
	public final static String FULLTEXT_GT = "text";
	
	@Id
	@Column(name = "ID")
	private int gtId;
	
	@Column(name = "XMLKEY")
	private String key;
	
	@Column
	@Transient
	private URL url;
	
	/**
	 * md5Sum getter/setter is defined in ITrpFile. Unclear if needed here
	 */
	private String md5Sum;
	
	@Column(name="IMAGE_ID")
	private Integer imageId;
	
	private TrpImage image;
	
	/**
	 * The type field is for storing a label like {@link #FULLTEXT_GT}, layout or struct(-ure).
	 * TODO check if a String label is ok or if an Integer should be used for faster filtering
	 * Maybe there is another field needed to mark if this was used for training or validation, but I'd rather put that into the relation table linking
	 * the GT with the trained model.
	 */
	@Column(name="GT_TYPE")
	private String gtType;
	
	/**
	 * The pageNr is joined in from a relation table containing this value
	 */
	@Transient
	@Column
	private Integer pageNr;
	
	/**
	 * Backlink to the page and thereby the document the image is coming from
	 */
	@Column(name = "ORIGIN_PAGEID")
	private Integer originPageId;
	
	/**
	 * Backlink to the transcript and thereby all related metadata
	 */
	@Column(name = "ORIGIN_TSID")
	private Integer originTsId;
	
	/**
	 * Backlink to the job that created this GT entity. A training result, such as an HTR, has the same backlink.
	 * By linking via the jobId we do not need to link to different tables in case there training results besides HTR later.
	 */
	@Column(name = "ORIGIN_JOBID")
	private Integer originJobId;
	
	/*
	 * Some fields copied from the TrpTranscriptMetadata in case the original transcript/document is deleted at a later point
	 */
	@Column
	private Timestamp created;
	
	@Column
	private String userName;
	
	@Column(name = "USER_ID")
	private int userId;
	
	public TrpGroundTruthPage() {
		gtId = -1;
	}

	public int getGtId() {
		return gtId;
	}
	
	public void setGtId(int gtId) {
		this.gtId = gtId;
	}
	
	@Override
	public URL getUrl() {
		return url;
	}

	@Override
	public void setUrl(URL url) {
		this.url = url;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getMd5Sum() {
		return md5Sum;
	}

	@Override
	public void setMd5Sum(String md5Sum) {
		this.md5Sum = md5Sum;
	}
	
	public Integer getImageId() {
		return imageId;
	}
	
	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public TrpImage getImage() {
		return image;
	}

	public void setImage(TrpImage image) {
		this.image = image;
		if(image != null) {
			setImageId(image.getImageId());
		}
	}

	public String getGtType() {
		return gtType;
	}

	public void setGtType(String gtType) {
		this.gtType = gtType;
	}
	
	public Integer getPageNr() {
		return pageNr;
	}
	
	public void setPageNr(Integer pageNr) {
		this.pageNr = pageNr;
	}

	public Integer getOriginPageId() {
		return originPageId;
	}

	public void setOriginPageId(Integer originPageId) {
		this.originPageId = originPageId;
	}

	public Integer getOriginTsId() {
		return originTsId;
	}

	public void setOriginTsId(Integer originTsId) {
		this.originTsId = originTsId;
	}

	public Integer getOriginJobId() {
		return originJobId;
	}

	public void setOriginJobId(Integer originJobId) {
		this.originJobId = originJobId;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public TrpPage toTrpPage() {
		TrpPage p = new TrpPage();
		p.setImage(this.getImage());
		p.setPageNr(this.pageNr != null ? this.pageNr : -1);
		p.setPageId(this.originPageId);
		
		TrpTranscriptMetadata t = new TrpTranscriptMetadata();
		t.setKey(this.key);
		t.setUserName(this.userName);
		t.setUserId(this.userId);
		t.setTime(this.created);
		t.setStatus(EditStatus.GT);
		t.setUrl(this.url);
		t.setTsId(this.originTsId);
		t.setStats(this.getStats());
		
		p.getTranscripts().add(t);
		return p;
	}

	/**
	 * Uses the page number for comparison
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TrpGroundTruthPage p) {
		if(this.getPageNr() == null && p.getPageNr() == null) {
			return 0;
		}
		if(this.getPageNr() != null && p.getPageNr() == null) {
			return 1;
		}
		if(this.getPageNr() == null && p.getPageNr() != null) {
			return -1;
		}
		if (this.getPageNr() > p.getPageNr()) {
			return 1;
		}
		if (this.getPageNr() < p.getPageNr()) {
			return -1;
		}
		return 0;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + gtId;
		result = prime * result + ((gtType == null) ? 0 : gtType.hashCode());
		//image does not yet implement equals(). imageId is sufficient for now
		//result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((md5Sum == null) ? 0 : md5Sum.hashCode());
		result = prime * result + ((originJobId == null) ? 0 : originJobId.hashCode());
		result = prime * result + ((originPageId == null) ? 0 : originPageId.hashCode());
		result = prime * result + ((originTsId == null) ? 0 : originTsId.hashCode());
		result = prime * result + ((pageNr == null) ? 0 : pageNr.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + userId;
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrpGroundTruthPage other = (TrpGroundTruthPage) obj;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (gtId != other.gtId)
			return false;
		if (gtType == null) {
			if (other.gtType != null)
				return false;
		} else if (!gtType.equals(other.gtType))
			return false;
		if (image == null) {
			if (other.image != null)
				return false;
		} 
		//image does not yet implement equals(). imageId is sufficient for now
//		else if (!image.equals(other.image))
//			return false;
		if (imageId == null) {
			if (other.imageId != null)
				return false;
		} else if (!imageId.equals(other.imageId))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (md5Sum == null) {
			if (other.md5Sum != null)
				return false;
		} else if (!md5Sum.equals(other.md5Sum))
			return false;
		if (originJobId == null) {
			if (other.originJobId != null)
				return false;
		} else if (!originJobId.equals(other.originJobId))
			return false;
		if (originPageId == null) {
			if (other.originPageId != null)
				return false;
		} else if (!originPageId.equals(other.originPageId))
			return false;
		if (originTsId == null) {
			if (other.originTsId != null)
				return false;
		} else if (!originTsId.equals(other.originTsId))
			return false;
		if (pageNr == null) {
			if (other.pageNr != null)
				return false;
		} else if (!pageNr.equals(other.pageNr))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (userId != other.userId)
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TrpGroundTruthPage [gtId=" + gtId + ", key=" + key + ", url=" + url + ", md5Sum=" + md5Sum
				+ ", imageId=" + imageId + ", image= [ " + image + " ], type=" + gtType + ", pageNr=" + pageNr
				+ ", originPageId=" + originPageId + ", originTsId=" + originTsId + ", originJobId=" + originJobId
				+ ", created=" + created + ", username=" + userName + ", userId=" + userId + "]";
	}
}
