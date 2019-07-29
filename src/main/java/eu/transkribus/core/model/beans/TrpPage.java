package eu.transkribus.core.model.beans;

import java.awt.Dimension;
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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.exceptions.NullValueException;
import eu.transkribus.core.model.beans.adapters.SqlTimestampAdapter;
import eu.transkribus.core.model.beans.enums.EditStatus;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;

@Entity
@Table(name = "pages")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpPage implements ITrpFile, Serializable, Comparable<TrpPage> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(TrpPage.class);
	//objid for parentDoc is ID in DB
	@Id
	@Column
	private int pageId = -1;
	@Column
	private int docId;
	@Column
	private int pageNr;
	@Column(name = "imagekey")
	@Transient
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
	
	/**
	 * This field is used to store information on problems with the given image for this page
	 * LocalDocReader: message on corrupt image (dimension can not be read)
	 * GoobiMetsImporter: message upon brokenUrl, no or corrupt image
	 */
	@Column(name="IMG_PROBLEM")
	private String imgFileProblem = null;

	//TODO SortedList from DB. comparator?
	@XmlElementWrapper(name="tsList")
	@XmlElement
	private List<TrpTranscriptMetadata> transcripts = new LinkedList<>();
	
	@Column
	@Transient
	private int width;
	@Column
	@Transient
	private int height;
	
	@XmlJavaTypeAdapter(SqlTimestampAdapter.class)
	@Column
	@Transient
	private java.sql.Timestamp created;
	
	@Column(name="IS_INDEXED")
	private boolean indexed = false;
	
	@XmlElementWrapper(name="imageVersions")
	@XmlElement
	private List<TrpPageImageVersion> imageVersions = new LinkedList<>();
	
	@XmlJavaTypeAdapter(SqlTimestampAdapter.class)
	@Column(name="TAGS_STORED")
	private java.sql.Timestamp tagsStored;

	public TrpPage() {}
	
	public TrpPage(TrpPage p) {
		this();
		pageId = p.getPageId();
		docId = p.getDocId();
		pageNr = p.getPageNr();
		key = p.getKey();
		imageId = p.getImageId();
		url = p.getUrl();
		thumbUrl = p.getThumbUrl();
		md5Sum = p.getMd5Sum();
		imgFileName = p.getImgFileName();
		width = p.getWidth();
		height = p.getHeight();
		created = p.getCreated();
		indexed = p.isIndexed();
		tagsStored = p.tagsStored;
		
		for(TrpPageImageVersion v : p.getImageVersions()) {
			imageVersions.add(new TrpPageImageVersion(v));
		}
		for(TrpTranscriptMetadata m : p.getTranscripts()) {
			transcripts.add(new TrpTranscriptMetadata(m, this));
		}
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

	public String getImgFileProblem() {
		return imgFileProblem;
	}

	public void setImgFileProblem(String imgFileProblem) {
		this.imgFileProblem = imgFileProblem;
	}
	
	public Dimension getImgDimension() {
		return new Dimension(width, height);
	}

	/**
	 * Due to corrupt or missing input image during import
	 * @return true if dummy image is linked to this page
	 */
	public boolean isImgMissing() {
		return !StringUtils.isEmpty(imgFileProblem);
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

	public java.sql.Timestamp getTagsStored() {
		return tagsStored;
	}

	public void setTagsStored(java.sql.Timestamp tagsStored) {
		this.tagsStored = tagsStored;
	}

	public TrpTranscriptMetadata getCurrentTranscript() {
		List<TrpTranscriptMetadata> tList = getTranscripts();
		if(tList.isEmpty()) {
			//might happen if image file is broken and no PAGE XML can be generated
			return new TrpTranscriptMetadata();
		}
		Collections.sort(tList, Collections.reverseOrder());
		return tList.get(0);
	}
	
	/**
	 * Return the most recent transcript with status. If status is not found return current.
	 * 
	 * @param status
	 * @return
	 */
	public TrpTranscriptMetadata getTranscriptWithStatus(EditStatus status) {
		return getTranscriptWithStatus(status == null ? null : status.getStr());
	}
	
	/**
	 * Return the most recent transcript with status. If status is not found return current.
	 * 
	 * @param status
	 * @return
	 */
	public TrpTranscriptMetadata getTranscriptWithStatus(String status) {
		TrpTranscriptMetadata tmd = getTranscriptWithStatusOrNull(status);

		//if no transcript with this status was found return the latest one
		return tmd == null ? getTranscripts().get(0) : tmd;
	}
	/**
	 * Return the most recent transcript with status. If status is not found return null.
	 * 
	 * @param status
	 * @return
	 */
	public TrpTranscriptMetadata getTranscriptWithStatusOrNull(EditStatus status) {
		return getTranscriptWithStatusOrNull(status == null ? null : status.getStr());
	}
	
	/**
	 * Return the most recent transcript with status. If status is not found return null.
	 * 
	 * @param status
	 * @return
	 */
	public TrpTranscriptMetadata getTranscriptWithStatusOrNull(String status) {
		List<TrpTranscriptMetadata> tList = getTranscripts();
		if(status == null) {
			return null;
		}
		
		Collections.sort(tList, Collections.reverseOrder());
		for (TrpTranscriptMetadata md : tList){
			if (md.getStatus().getStr().equals(status) || (md.getToolName() != null && md.getToolName().equals(status))){
				return md;
			}
				
		}
		
		//if no transcript with this status was found return null
		return null;
	}
	
	public PcGtsType unmarshallCurrentTranscript() throws NullValueException, JAXBException {
		if (getCurrentTranscript()==null)
			throw new NullValueException("Current transcript is null!");
		
		return getCurrentTranscript().unmarshallTranscript();	
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
		i.setImageId(imageId);
		i.setImgFileName(imgFileName);
		i.setKey(key);
		i.setCreated(created);
		i.setWidth(width);
		i.setHeight(height);
		i.setUrl(url);
		i.setThumbUrl(thumbUrl);
		return i;
	}
	
	public void setImage(TrpImage i) {
		this.imageId = i.getImageId();
		this.imgFileName = i.getImgFileName();
		this.key = i.getKey();
		this.created = i.getCreated();
		this.width = i.getWidth();
		this.height = i.getHeight();
		this.url = i.getUrl();
		this.thumbUrl = i.getThumbUrl();
	}

	public boolean isIndexed() {
		return indexed;
	}

	public void setIndexed(boolean isIndexed) {
		this.indexed = isIndexed;
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
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrpPage other = (TrpPage) obj;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (docId != other.docId)
			return false;
		if (height != other.height)
			return false;
		if (imageId != other.imageId)
			return false;
		if (imageVersions == null) {
			if (other.imageVersions != null)
				return false;
		} else if (!imageVersions.equals(other.imageVersions))
			return false;
		if (imgFileName == null) {
			if (other.imgFileName != null)
				return false;
		} else if (!imgFileName.equals(other.imgFileName))
			return false;
		if (indexed != other.indexed)
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
		if (pageId != other.pageId)
			return false;
		if (pageNr != other.pageNr)
			return false;
		if (tagsStored == null) {
			if (other.tagsStored != null)
				return false;
		} else if (!tagsStored.equals(other.tagsStored))
			return false;
		if (thumbUrl == null) {
			if (other.thumbUrl != null)
				return false;
		} else if (!thumbUrl.equals(other.thumbUrl))
			return false;
		if (transcripts == null) {
			if (other.transcripts != null)
				return false;
		} else if (!transcripts.equals(other.transcripts))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (width != other.width)
			return false;
		return true;
	}
	

	/**
	 * This method is just for testing equivalence of documents selected via different DocManager methods
	 * Same as normal equals, but iterates transcripts and checks equivalence
	 * @param obj
	 * @return
	 */
	public boolean testEquals(TrpPage obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrpPage other = (TrpPage) obj;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (docId != other.docId)
			return false;
		if (height != other.height)
			return false;
		if (imageId != other.imageId)
			return false;
		if (imageVersions == null) {
			if (other.imageVersions != null)
				return false;
		} else if (!imageVersions.equals(other.imageVersions))
			return false;
		if (imgFileName == null) {
			if (other.imgFileName != null)
				return false;
		} else if (!imgFileName.equals(other.imgFileName))
			return false;
		if (indexed != other.indexed)
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
		if (pageId != other.pageId)
			return false;
		if (pageNr != other.pageNr)
			return false;
		if (tagsStored == null) {
			if (other.tagsStored != null)
				return false;
		} else if (!tagsStored.equals(other.tagsStored))
			return false;
		if (thumbUrl == null) {
			if (other.thumbUrl != null)
				return false;
		} else if (!thumbUrl.equals(other.thumbUrl))
			return false;
		if (transcripts == null) {
			if (other.transcripts != null)
				return false;
		}
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (width != other.width)
			return false;
		if(transcripts != null) {
			if(other.transcripts == null) {
				return false;
			}
			if(transcripts.size() != other.transcripts.size()) {
				logger.info("Transcript list size is unequal on page nr. " + this.pageNr + ": " 
						+ transcripts.size() + " != " + other.transcripts.size());
				return false;
			}
			for(int i = 0; i < transcripts.size(); i++) {
				if(!transcripts.get(i).testEquals(other.transcripts.get(i))) {
					logger.info("Unequal transcript on page nr. " + this.pageNr
							+ "\n" + transcripts.get(i).toString() 
							+ "\n" + other.transcripts.get(i).toString());
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		String str = "TrpPage [pageId=" + pageId + ", docId=" + docId + ", pageNr=" + pageNr + ", key=" + key + ", imageId="
				+ imageId + ", url=" + url + ", thumbUrl=" + thumbUrl + ", md5Sum=" + md5Sum + ", imgFileName="
				+ imgFileName + ", transcripts=" + transcripts + ", width=" + width + ", height=" + height
				+ ", created=" + created + ", indexed=" + indexed + ", imageVersions=" + imageVersions + ", tagsStored="+tagsStored+"]";
		for(TrpTranscriptMetadata t : this.transcripts) {
			str += "\n\t" + t.toString();
		}
		
		return str;
	}

	public boolean hasImgError() {
		return !StringUtils.isEmpty(imgFileProblem);
	}

	/**
	 * returns the transcript with the given ID from the transcript list or null if not found
	 * 
	 * @param tsId
	 * @return
	 */
	public TrpTranscriptMetadata getTranscriptById(int tsId) {
		TrpTranscriptMetadata tmd = null;
		if(!getTranscripts().isEmpty()) {
			for(TrpTranscriptMetadata t : getTranscripts()) {
				if(t.getTsId() == tsId) {
					tmd = t;
					break;
				}
			}
		}
		return tmd;
	}
	
}
