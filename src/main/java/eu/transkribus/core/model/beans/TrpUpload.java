package eu.transkribus.core.model.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

import eu.transkribus.core.io.LocalDocConst;

@Entity
@Table(name = "UPLOADS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpUpload extends TrpDocStructure implements Serializable {
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
	
	@Column
	private UploadType type;
	
	@Column(name="JOB_ID")
	private Integer jobId;
	
	@Column(name="COLLECTION_ID")
	private int colId;
	
	/**
	 * isComplete stores the evaluation result of isComplete()
	 */
	private boolean isComplete = false;
	
	public TrpUpload(){
		super();
	}
	
	public TrpUpload(TrpDocStructure struct) {
		super();
		this.md = struct.getMd();
		this.images = struct.getImages();
		//sort by page index!
		Collections.sort(this.images);
		validateAndNormalize(this.images);
		created = new Date();
	}
	
	/**Ensures that all images have filenames assigned and page indices are iterated throughout the structure
	 * If page indices start from 0 they will be incremented by 1 in order to be compatible with METS-style counting.
	 * If XML filenames have the "page/" dir prefix, it will be removed.
	 * @param images
	 */
	private void validateAndNormalize(List<TrpDocStructureImage> images) {
		if(images.isEmpty()) {
			throw new IllegalArgumentException("Image list is empty!");
		}
		//check page indices
		int i = images.get(0).getIndex();
		//check if it starts with 1 or 0
		boolean pageCountFromZero = false;
		if(i == 0) {
			//increment all indexes by 1
			pageCountFromZero = true;
		} else if (i < 0 || i > 1) {
			throw new IllegalArgumentException("page indexes have to start with 1 or 0!");
		}
		for(TrpDocStructureImage img : this.images) {
			//check page indexes for continuity
			if(img.getIndex() != i) {
				throw new IllegalArgumentException("Page indexes are inconsistent!");
			} else {
				i++;
			}
			//correct index if counting starts from zero as METS also includes counts starting from 1
			if(pageCountFromZero) {
				img.setIndex(img.getIndex() + 1);
			}
			//ensure that at least the img filename is set
			if(StringUtils.isEmpty(img.getFileName())) {
				throw new IllegalArgumentException("Image filename is empty for page index: " + img.getIndex());
			}
			if(!StringUtils.isEmpty(img.getPageXmlName()) 
					&& img.getPageXmlName().startsWith(LocalDocConst.PAGE_FILE_SUB_FOLDER + "/")) {
				//remove the "page/" prefix in XML filename if existent
				img.setPageXmlName(img.getPageXmlName().replaceFirst(LocalDocConst.PAGE_FILE_SUB_FOLDER + "/", ""));
			}
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

	public UploadType getType() {
		return type;
	}

	public void setType(UploadType type) {
		this.type = type;
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
		for(TrpDocStructureImage img : this.getImages()) {
			boolean pageComplete = img.isImgUploaded() 
					&& (StringUtils.isEmpty(img.getPageXmlName()) || img.isPageXmlUploaded());
			if (!pageComplete) {
				return false;
			}
		}
		isComplete = true;
		return isComplete;
	}

	public enum UploadType {
		NoStructure,
		METS,
		TrpDocStructure;
	}
}