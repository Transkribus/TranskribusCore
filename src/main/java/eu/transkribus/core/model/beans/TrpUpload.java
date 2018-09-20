package eu.transkribus.core.model.beans;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.transkribus.core.model.builder.TrpDocUploadBuilder;
import eu.transkribus.core.util.JaxbUtils;

@Entity
@Table(name = "UPLOADS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpUpload extends DocumentUploadDescriptor implements Serializable {
	private static final long serialVersionUID = -7706054520211169041L;
	/**
	 * uploadId equals docId
	 */
	@Id
	@Column(name="UPLOAD_ID")
	private int uploadId; 
	
	@Column
	private Date created;
	
	@Column
	private Date finished;
	
	@Column(name="USER_ID")
	private int userId;
	
	@Column
	private String userName;
	
	@Column(name="NR_OF_PAGES")
	private Integer nrOfPagesTotal;
	
	@Column(name="TYPE")
	private UploadType uploadType;
	
	@Column(name="JOB_ID")
	private Integer jobId;
	
	@Column(name="COLLECTION_ID")
	private int colId;
	
	/**
	 * isComplete caches the evaluation result of isComplete()
	 */
	@XmlTransient
	private boolean isComplete = false;
	
	@XmlTransient
	private File uploadTmpDir = null;
	@XmlTransient
	private File uploadPageTmpDir = null;
	
	public TrpUpload(){
		super();
	}
	
	public TrpUpload(DocumentUploadDescriptor struct) {
		super();
		this.md = struct.getMd();
		this.relatedUploadId = struct.getRelatedUploadId();
		this.pages = struct.getPages();
		this.created = new Date();
		if(this.pages == null || this.pages.isEmpty()) {
			this.setUploadType(UploadType.VIRTUAL);
		} else {
			//sort by page index!
			Collections.sort(this.pages);
			TrpDocUploadBuilder.validateAndNormalize(this.pages);
			this.setUploadType(UploadType.JSON);
		}
		
	}

	public int getUploadId() {
		return uploadId;
	}

	public void setUploadId(int uploadId) {
		this.uploadId = uploadId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getFinished() {
		return finished;
	}

	public void setFinished(Date finished) {
		this.finished = finished;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getNrOfPagesTotal() {
		return nrOfPagesTotal;
	}

	public void setNrOfPagesTotal(Integer nrOfPagesTotal) {
		this.nrOfPagesTotal = nrOfPagesTotal;
	}

	public UploadType getUploadType() {
		return uploadType;
	}

	public void setUploadType(UploadType uploadType) {
		this.uploadType = uploadType;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public int getColId() {
		return colId;
	}

	public void setColId(int colId) {
		this.colId = colId;
	}
	
	/**
	 * Iterates the list of images to be uploaded and checks if the "uploaded" flag is set true.
	 * Once this method has returned true, the state is stored and subsequent calls won't iterate the list.
	 * @return false if an incomplete page is found, true otherwise
	 */
	public boolean isUploadComplete() {
		if(isComplete) {
			return true;
		}
		for(PageUploadDescriptor img : this.getPages()) {
			if (!img.isPageUploaded()) {
				return false;
			}
		}
		isComplete = true;
		return isComplete;
	}

	public File getUploadTmpDir() {
		return uploadTmpDir;
	}

	public void setUploadTmpDir(File uploadTmpDir) {
		this.uploadTmpDir = uploadTmpDir;
	}

	public File getUploadPageTmpDir() {
		return uploadPageTmpDir;
	}

	public void setUploadPageTmpDir(File uploadPageTmpDir) {
		this.uploadPageTmpDir = uploadPageTmpDir;
	}

	public boolean canReadDirectories() {
		if(uploadPageTmpDir == null || uploadTmpDir == null) {
			return false;
		}
		return uploadTmpDir.canRead() && uploadPageTmpDir.canRead();
	}
	
	public enum UploadType {
		NoStructure,
		METS,
		JSON,
		//archives will be create a "virtual" upload, where another user will send the files within a another upload process 
		VIRTUAL;
	}
	
	public String toXmlStr() throws JAXBException {
		return JaxbUtils.marshalToString(this, TrpDocMetadata.class, PageUploadDescriptor.class);
	}

	public boolean hasChecksumsSet() {
		for(PageUploadDescriptor p : this.pages) {
			if(p.getImgChecksum() == null || p.getPageXmlChecksum() == null) {
				return false;
			}
		}
		return true;
	}
}
