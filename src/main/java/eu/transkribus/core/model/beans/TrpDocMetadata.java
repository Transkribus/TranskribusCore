package eu.transkribus.core.model.beans;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;

import eu.transkribus.core.io.RemoteDocConst;
import eu.transkribus.core.model.beans.adapters.DocTypeAdapter;
import eu.transkribus.core.model.beans.adapters.ScriptTypeAdapter;
import eu.transkribus.core.model.beans.enums.DocType;
import eu.transkribus.core.model.beans.enums.ScriptType;
import io.swagger.v3.oas.annotations.Hidden;

@Entity
@Table(name="DOC_MD")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpDocMetadata extends ATotalTranscriptStatistics implements Serializable, Comparable<TrpDocMetadata> {
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	private int docId=-1;
	@Column
	private String title;
	@Column
	private String author;
//	@XmlJavaTypeAdapter(DateAdapter.class)
//	@Column(name="ultime")
//	private Date uploadTime;
	@Column(name="ultimestamp")
	private long uploadTimestamp;
	@Column
	private String genre;
	@Column
	private String writer;

	@Column
//	@XmlJavaTypeAdapter(EnumAdapter.class)
	@XmlJavaTypeAdapter(ScriptTypeAdapter.class)
	private ScriptType scriptType;
	
	//Owner is defined via docPermissions
	//TODO rename column to creator
	@Column
	private String uploader;
	
	@Column
	private int uploaderId;
	
	//actually nrOfPages is no real column. Transient annotated fields are ignored on inserts
	@Column
	@Transient
	private int nrOfPages;
	
	@Column(name="PAGE_ID")
	private Integer pageId;
	
	@Column(name="DEL_TIME")
	private Date deletedOnDate;

	@Column
	@Transient
	private URL url;
	@Column
	@Transient
	private URL thumbUrl;
	
	@Column(name="EXTID")
	private String externalId;
	
	@Column
	private String authority;
	
	@Column
	private String hierarchy;
	
	@Column
	private String backlink;
	
	@Column(name="DESCRIPTION")
	private String desc;
	
	@Column(name="DOCTYPE")
	@XmlJavaTypeAdapter(DocTypeAdapter.class)
	private DocType type;

	@Column(name="LANGUAGE")
	private String language;
	
	@Column(name="STATUS")
	private Integer status;
	
	@Column
	private String fimgStoreColl = null; // == null if local document
	private File localFolder = null; // != null when local document

	@Column(name="CREATEDFROM")
	private Long createdFromTimestamp;
	
	@Column(name="CREATEDTO")
	private Long createdToTimestamp;
	
	@Column
	private Integer origDocId = null;
	
	@XmlElementWrapper(name="collectionList")
	@XmlElement
	protected List<TrpCollection> colList = new ArrayList<>();
	
	public TrpDocMetadata() {}
	
	public TrpDocMetadata(TrpDocMetadata md) {
		this();
		docId = md.getDocId();
		title = md.getTitle();
		author = md.getAuthor();
		authority = md.getAuthority();
		uploadTimestamp = md.getUploadTimestamp();
		genre = md.getGenre();
		writer = md.getWriter();
		scriptType = md.getScriptType();
		uploader = md.getUploader();
		uploaderId = md.getUploaderId();
		nrOfPages = md.getNrOfPages();
		externalId = md.getExternalId();
		hierarchy = md.getHierarchy();
		backlink = md.getBacklink();
		desc = md.getDesc();
		type = md.getType();
		language = md.getLanguage();
		status = md.getStatus();
		fimgStoreColl = md.getFimgStoreColl();
		localFolder = md.getLocalFolder();
		createdFromTimestamp = md.getCreatedFromTimestamp();
		createdToTimestamp = md.getCreatedToTimestamp();
		origDocId = md.getOrigDocId();
		pageId = md.getPageId();
		deletedOnDate = md.getDeletedOnDate();
		url = md.getUrl();
		thumbUrl = md.getThumbUrl();
		for(TrpCollection c : md.getColList()) {
			colList.add(new TrpCollection(c));
		}
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public long getUploadTimestamp() {
		return uploadTimestamp;
	}
	
	/**
	 * A helper that converts the timestamp to a Date
	 * 
	 * @return
	 */
	public Date getUploadTime() {
		return new Date(this.uploadTimestamp);
	}
	
	/**
	 * Helper to set the timestamp via a Date object
	 * @param uploadTime
	 */
	public void setUploadTime(Date uploadTime) {
		this.uploadTimestamp = uploadTime.getTime();
	}

	public void setUploadTimestamp(long uploadTimestamp) {
		this.uploadTimestamp = uploadTimestamp;
	}
	
	public String getGenre() {// set null
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}
	
	public int getNrOfPages() {
		return this.nrOfPages;
	}
	
	public void setNrOfPages(int nrOfPages) {
		this.nrOfPages = nrOfPages;
	}

	public ScriptType getScriptType() {
		return scriptType;
	}

	public void setScriptType(ScriptType scriptType) {
		this.scriptType = scriptType;
	}

	public String getUploader() {
		return uploader;
	}

	public boolean isDeleted() {
		return deletedOnDate != null;
	}
	
	public void setDeleted(boolean deleted) {
		if(deleted && getDeletedOnDate() != null) {
			return;
		}
		setDeletedOnDate(deleted ? new Date() : null);
	}
	
	public void setDeletedOnDate(Date deletedOnDate) {
		this.deletedOnDate = deletedOnDate;
	}
	
	public Date getDeletedOnDate() {
		return deletedOnDate;
	}
	
	public void setUploader(String uploader) {
		this.uploader = uploader;
	}
	
	public int getUploaderId() {
		return uploaderId;
	}

	public void setUploaderId(int uploaderId) {
		this.uploaderId = uploaderId;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}

	public String getBacklink() {
		return backlink;
	}

	public void setBacklink(String backlink) {
		this.backlink = backlink;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public DocType getType() {
		return type;
	}

	public void setType(DocType type) {
		this.type = type;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getFimgStoreColl() {
		return fimgStoreColl;
	}

	public void setFimgStoreColl(String fimgStoreColl) {
		this.fimgStoreColl = fimgStoreColl;
	}

	public File getLocalFolder() {
		return localFolder;
	}
	
	public void setLocalFolder(File localFolder) {
		this.localFolder = localFolder;
	}

	public Long getCreatedFromTimestamp() {
		return createdFromTimestamp;
	}
	
	public Date getCreatedFromDate(){
		return (createdFromTimestamp == null) ? null : new Date(createdFromTimestamp);
	}

	public void setCreatedFromTimestamp(Long createdFromTimestamp) {
		this.createdFromTimestamp = createdFromTimestamp;
	}
	
	public void setCreatedFromDate(Date createdFromDate) {
		this.createdFromTimestamp = createdFromDate == null ? null : createdFromDate.getTime();
	}

	public Long getCreatedToTimestamp() {
		return createdToTimestamp;
	}
	
	public Date getCreatedToDate(){
		return (createdToTimestamp == null) ? null : new Date(createdToTimestamp);
	}

	public void setCreatedToTimestamp(Long createdToTimestamp) {
		this.createdToTimestamp = createdToTimestamp;
	}
	
	public void setCreatedToDate(Date createdToDate) {
		this.createdToTimestamp = createdToDate == null ? null : createdToDate.getTime();
	}
	
	public TrpCollection getCollection(int collId) {
		for (TrpCollection c : getColList()) {
			if (c.getColId() == collId)
				return c;
		}
		return null;
	}
	
	public int getFirstCollectionId() {
		if (colList!=null && !colList.isEmpty()) {
			return colList.get(0).getColId();
		} else {
			return -1;
		}
	}

	public List<TrpCollection> getColList() {
		return colList;
	}

	public void setColList(List<TrpCollection> colList) {
		this.colList = colList;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Hidden
	public boolean isSampleDoc() {
		return status == RemoteDocConst.STATUS_SAMPLE_DOC;
	}
	
	@Hidden 
	public boolean isGtDoc() {
		return status == RemoteDocConst.STATUS_GROUND_TRUTH_DOC;
	}
	
	public Integer getOrigDocId(){
		return origDocId;
	}
	
	public void setOrigDocId(Integer origDocId){
		this.origDocId = origDocId;
	}

	public Integer getPageId() {
		return pageId;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}

	public URL getUrl() {
		return url;
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

	public String getColString() {
		String colsStr = "";
		if (getColList() != null) {
			for (TrpCollection c : getColList())
				colsStr += "("+c.getColName()+","+c.getColId()+") ";
		}
		colsStr = colsStr.trim();
		return colsStr;
	}

	/**
	 * Uses the docid for comparison by default.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TrpDocMetadata md) {
		if (this.getDocId() > md.getDocId()) {
			return 1;
		}
		if (this.getDocId() < md.getDocId()) {
			return -1;
		}
		return 0;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName() + " {");
		sb.append(this.getTitle() + " - ");
		sb.append(this.getAuthor() + " - ");
		sb.append(this.getUploader() + " - ");
		sb.append("deleted on " +(getDeletedOnDate() == null ? null : getDeletedOnDate().toString()) + " - ");
		sb.append(this.getGenre() + " - ");
		sb.append(this.getDocId() + " - ");
		sb.append(this.getWriter() + " - ");
		sb.append(this.getScriptType() + " - ");
		sb.append((new Date(uploadTimestamp)).toString() + " - ");
		sb.append(this.getNrOfPages() + " - ");
		sb.append(this.language + " - ");
		sb.append("pageID = " + this.getPageId() + " - ");
		sb.append((getCreatedFromDate() == null ? null : getCreatedFromDate().toString()) + " - ");
		sb.append((getCreatedToDate() == null ? null : getCreatedToDate().toString()) + " - ");
		if (this.getLocalFolder()!=null) {
			sb.append(" - Folder: "+this.getLocalFolder());
		}
		// print collection list:
		String clist = "";
		clist = "collections { ";
		if (colList != null) {
			for (TrpCollection c : colList) {
				clist += c.toShortString()+"; ";
			}
			clist = StringUtils.removeEnd(clist, "; ");
		} else
			clist += " null ";
		clist += " }";
		sb.append(clist);
		
		sb.append(this.getNrOfLines() + " - ");
		sb.append(this.getNrOfTranscribedLines() + " - ");
		sb.append(this.getNrOfNew() + " - ");
		sb.append(this.getNrOfInProgress() + " - ");
		sb.append(this.getNrOfDone() + " - ");
		sb.append(this.getNrOfFinal() + " - ");
		sb.append(this.getNrOfGT() + " - ");
		
		sb.append("}");
		return sb.toString();
	}
	
	@Override
	public TrpDocMetadata clone(){
		try {
			return (TrpDocMetadata) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(); //can't happen
		}
	}

	/**
	 * Only compares field values that are allowed to be changed in user interfaces and are also in the doc_md DB table.
	 * This is used on TrpServer's REST API to check if a DB update and Solr index update is needed 
	 * 
	 * @param obj
	 * @return
	 */
	public boolean equalsMutableFields(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrpDocMetadata other = (TrpDocMetadata) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (createdFromTimestamp == null) {
			if (other.createdFromTimestamp != null)
				return false;
		} else if (!createdFromTimestamp.equals(other.createdFromTimestamp))
			return false;
		if (createdToTimestamp == null) {
			if (other.createdToTimestamp != null)
				return false;
		} else if (!createdToTimestamp.equals(other.createdToTimestamp))
			return false;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (docId != other.docId)
			return false;
		if (externalId == null) {
			if (other.externalId != null)
				return false;
		} else if (!externalId.equals(other.externalId))
			return false;
		if (genre == null) {
			if (other.genre != null)
				return false;
		} else if (!genre.equals(other.genre))
			return false;
		if (pageId == null) {
			if (other.pageId != null)
				return false;
		} else if (!pageId.equals(other.pageId))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (scriptType != other.scriptType)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type != other.type)
			return false;
		if (writer == null) {
			if (other.writer != null)
				return false;
		} else if (!writer.equals(other.writer))
			return false;
		if (thumbUrl == null) {
			if (other.thumbUrl != null)
				return false;
		} else if (!thumbUrl.equals(other.thumbUrl))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (deletedOnDate == null) {
			if (other.deletedOnDate != null)
				return false;
		} else if (!deletedOnDate.equals(other.deletedOnDate))
			return false;
		return true;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((authority == null) ? 0 : authority.hashCode());
		result = prime * result + ((backlink == null) ? 0 : backlink.hashCode());
		result = prime * result + ((colList == null) ? 0 : colList.hashCode());
		result = prime * result + ((createdFromTimestamp == null) ? 0 : createdFromTimestamp.hashCode());
		result = prime * result + ((createdToTimestamp == null) ? 0 : createdToTimestamp.hashCode());
		result = prime * result + ((deletedOnDate == null) ? 0 : deletedOnDate.hashCode());
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + docId;
		result = prime * result + ((externalId == null) ? 0 : externalId.hashCode());
		result = prime * result + ((fimgStoreColl == null) ? 0 : fimgStoreColl.hashCode());
		result = prime * result + ((genre == null) ? 0 : genre.hashCode());
		result = prime * result + ((hierarchy == null) ? 0 : hierarchy.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((localFolder == null) ? 0 : localFolder.hashCode());
		result = prime * result + nrOfPages;
		result = prime * result + ((origDocId == null) ? 0 : origDocId.hashCode());
		result = prime * result + ((pageId == null) ? 0 : pageId.hashCode());
		result = prime * result + ((scriptType == null) ? 0 : scriptType.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((thumbUrl == null) ? 0 : thumbUrl.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + (int) (uploadTimestamp ^ (uploadTimestamp >>> 32));
		result = prime * result + ((uploader == null) ? 0 : uploader.hashCode());
		result = prime * result + uploaderId;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((writer == null) ? 0 : writer.hashCode());
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
		TrpDocMetadata other = (TrpDocMetadata) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (authority == null) {
			if (other.authority != null)
				return false;
		} else if (!authority.equals(other.authority))
			return false;
		if (backlink == null) {
			if (other.backlink != null)
				return false;
		} else if (!backlink.equals(other.backlink))
			return false;
		if (colList == null) {
			if (other.colList != null)
				return false;
		} else if (!colList.equals(other.colList))
			return false;
		if (createdFromTimestamp == null) {
			if (other.createdFromTimestamp != null)
				return false;
		} else if (!createdFromTimestamp.equals(other.createdFromTimestamp))
			return false;
		if (createdToTimestamp == null) {
			if (other.createdToTimestamp != null)
				return false;
		} else if (!createdToTimestamp.equals(other.createdToTimestamp))
			return false;
		if (deletedOnDate == null) {
			if (other.deletedOnDate != null)
				return false;
		} else if (!deletedOnDate.equals(other.deletedOnDate))
			return false;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (docId != other.docId)
			return false;
		if (externalId == null) {
			if (other.externalId != null)
				return false;
		} else if (!externalId.equals(other.externalId))
			return false;
		if (fimgStoreColl == null) {
			if (other.fimgStoreColl != null)
				return false;
		} else if (!fimgStoreColl.equals(other.fimgStoreColl))
			return false;
		if (genre == null) {
			if (other.genre != null)
				return false;
		} else if (!genre.equals(other.genre))
			return false;
		if (hierarchy == null) {
			if (other.hierarchy != null)
				return false;
		} else if (!hierarchy.equals(other.hierarchy))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (localFolder == null) {
			if (other.localFolder != null)
				return false;
		} else if (!localFolder.equals(other.localFolder))
			return false;
		if (nrOfPages != other.nrOfPages)
			return false;
		if (origDocId == null) {
			if (other.origDocId != null)
				return false;
		} else if (!origDocId.equals(other.origDocId))
			return false;
		if (pageId == null) {
			if (other.pageId != null)
				return false;
		} else if (!pageId.equals(other.pageId))
			return false;
		if (scriptType != other.scriptType)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (thumbUrl == null) {
			if (other.thumbUrl != null)
				return false;
		} else if (!thumbUrl.equals(other.thumbUrl))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type != other.type)
			return false;
		if (uploadTimestamp != other.uploadTimestamp)
			return false;
		if (uploader == null) {
			if (other.uploader != null)
				return false;
		} else if (!uploader.equals(other.uploader))
			return false;
		if (uploaderId != other.uploaderId)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (writer == null) {
			if (other.writer != null)
				return false;
		} else if (!writer.equals(other.writer))
			return false;
		return true;
	}

	/**
	 * Just for testing new Dao that includes the symbolic image. Remove when done.
	 * @param obj
	 * @return
	 */
	public boolean equalsWoUrl(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrpDocMetadata other = (TrpDocMetadata) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (colList == null) {
			if (other.colList != null)
				return false;
		} else if (!colList.equals(other.colList))
			return false;
		if (createdFromTimestamp == null) {
			if (other.createdFromTimestamp != null)
				return false;
		} else if (!createdFromTimestamp.equals(other.createdFromTimestamp))
			return false;
		if (createdToTimestamp == null) {
			if (other.createdToTimestamp != null)
				return false;
		} else if (!createdToTimestamp.equals(other.createdToTimestamp))
			return false;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (docId != other.docId)
			return false;
		if (externalId == null) {
			if (other.externalId != null)
				return false;
		} else if (!externalId.equals(other.externalId))
			return false;
		if (fimgStoreColl == null) {
			if (other.fimgStoreColl != null)
				return false;
		} else if (!fimgStoreColl.equals(other.fimgStoreColl))
			return false;
		if (genre == null) {
			if (other.genre != null)
				return false;
		} else if (!genre.equals(other.genre))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (localFolder == null) {
			if (other.localFolder != null)
				return false;
		} else if (!localFolder.equals(other.localFolder))
			return false;
		if (nrOfPages != other.nrOfPages)
			return false;
		if (origDocId == null) {
			if (other.origDocId != null)
				return false;
		} else if (!origDocId.equals(other.origDocId))
			return false;
		if (scriptType != other.scriptType)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (type != other.type)
			return false;
		if (uploadTimestamp != other.uploadTimestamp)
			return false;
		if (uploader == null) {
			if (other.uploader != null)
				return false;
		} else if (!uploader.equals(other.uploader))
			return false;
		if (uploaderId != other.uploaderId)
			return false;
		if (writer == null) {
			if (other.writer != null)
				return false;
		} else if (!writer.equals(other.writer))
			return false;
		return true;
	}

}
