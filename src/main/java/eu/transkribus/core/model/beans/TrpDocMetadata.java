package eu.transkribus.core.model.beans;

import java.io.File;
import java.io.Serializable;
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

import eu.transkribus.core.model.beans.adapters.DocTypeAdapter;
import eu.transkribus.core.model.beans.adapters.ScriptTypeAdapter;
import eu.transkribus.core.model.beans.enums.DocType;
import eu.transkribus.core.model.beans.enums.ScriptType;

@Entity
@Table(name="DOC_MD")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpDocMetadata implements Serializable, Comparable<TrpDocMetadata> {
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
	
	@Column(name="EXTID")
	private String externalId;
	
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
		uploadTimestamp = md.getUploadTimestamp();
		genre = md.getGenre();
		writer = md.getWriter();
		scriptType = md.getScriptType();
		uploader = md.getUploader();
		uploaderId = md.getUploaderId();
		nrOfPages = md.getNrOfPages();
		externalId = md.getExternalId();
		desc = md.getDesc();
		type = md.getType();
		language = md.getLanguage();
		status = md.getStatus();
		fimgStoreColl = md.getFimgStoreColl();
		localFolder = md.getLocalFolder();
		createdFromTimestamp = md.getCreatedFromTimestamp();
		createdToTimestamp = md.getCreatedToTimestamp();
		origDocId = md.getOrigDocId();
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
	
	public Date getUploadTime() {
		return new Date(this.uploadTimestamp);
	}
	
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
	
	public Integer getOrigDocId(){
		return origDocId;
	}
	
	public void setOrigDocId(Integer origDocId){
		this.origDocId = origDocId;
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
		sb.append(this.getGenre() + " - ");
		sb.append(this.getDocId() + " - ");
		sb.append(this.getWriter() + " - ");
		sb.append(this.getScriptType() + " - ");
		sb.append((new Date(uploadTimestamp)).toString() + " - ");
		sb.append(this.getNrOfPages() + " - ");
		sb.append(this.language + " - ");
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((colList == null) ? 0 : colList.hashCode());
		result = prime * result + ((createdFromTimestamp == null) ? 0 : createdFromTimestamp.hashCode());
		result = prime * result + ((createdToTimestamp == null) ? 0 : createdToTimestamp.hashCode());
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + docId;
		result = prime * result + ((externalId == null) ? 0 : externalId.hashCode());
		result = prime * result + ((fimgStoreColl == null) ? 0 : fimgStoreColl.hashCode());
		result = prime * result + ((genre == null) ? 0 : genre.hashCode());
		result = prime * result + ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((localFolder == null) ? 0 : localFolder.hashCode());
		result = prime * result + nrOfPages;
		result = prime * result + ((origDocId == null) ? 0 : origDocId.hashCode());
		result = prime * result + ((scriptType == null) ? 0 : scriptType.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + (int) (uploadTimestamp ^ (uploadTimestamp >>> 32));
		result = prime * result + ((uploader == null) ? 0 : uploader.hashCode());
		result = prime * result + uploaderId;
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
