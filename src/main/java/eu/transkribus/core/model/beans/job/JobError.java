package eu.transkribus.core.model.beans.job;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * This class records errors on specific pages of multi-page jobs
 * 
 * @author philip
 *
 */
@Entity
@Table(name="JOB_ERRORS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JobError {
	@Id
	@Column(name="JOB_ERR_ID")
	private Integer jobErrorId;
	@Column
	private int jobId;
	@Column
	private Integer docId;
	@Column
	private Integer pageId;
	@Column
	private Integer pageNr;
	@Column
	private Integer tsId;
	@Column(name="MSG")
	private String message;
	@Column(name="EX_CLASS")
	private String exceptionClass;
	@XmlTransient
	@Column
	private String stacktrace;
	@Column
	private String path;
	
	public JobError() {}
	
	public JobError(int jobId, Integer docId, Integer pageId, Integer pageNr, Integer tsId,
			String message, String exceptionClass, String stacktrace, String path) {
		super();
		this.jobId = jobId;
		this.docId = docId;
		this.pageId = pageId;
		this.pageNr = pageNr;
		this.tsId = tsId;
		this.message = message;
		this.exceptionClass = exceptionClass;
		this.stacktrace = stacktrace;
		this.path = path;
	}

	public JobError(int jobId, Integer docId, Integer pageId, Integer pageNr, Integer tsId, Throwable throwable) {
		this.jobId = jobId;
		this.docId = docId;
		this.pageId = pageId;
		this.pageNr = pageNr;
		this.tsId = tsId;
		this.setThrowable(throwable);
	}
	
	public Integer getJobErrorId() {
		return jobErrorId;
	}

	public void setJobErrorId(Integer jobErrorId) {
		this.jobErrorId = jobErrorId;
	}

	public Integer getJobId() {
		return jobId;
	}
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	public Integer getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public Integer getPageId() {
		return pageId;
	}
	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}
	public Integer getPageNr() {
		return pageNr;
	}
	public void setPageNr(Integer pageNr) {
		this.pageNr = pageNr;
	}
	public Integer getTsId() {
		return tsId;
	}
	public void setTsId(Integer tsId) {
		this.tsId = tsId;
	}
	public int getPageIndex() {
		return pageNr - 1;
	}
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getExceptionClass() {
		return exceptionClass;
	}

	public void setExceptionClass(String exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	public String getStacktrace() {
		return stacktrace;
	}

	public void setStacktrace(String stacktrace) {
		this.stacktrace = stacktrace;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setThrowable(Throwable t) {
		final String errorMsg;
		if (t instanceof NullPointerException) {
			errorMsg = "NullPointerException";
		} else {
			errorMsg = t.getMessage();
		}
		if(StringUtils.isEmpty(this.getMessage())) {
			this.setMessage(errorMsg);
		}
		this.setExceptionClass(t.getClass().getName());
		final String stacktrace = ExceptionUtils.getStackTrace(t);
		this.setStacktrace(stacktrace);
	}

	@Override
	public String toString() {
		return "JobError [jobErrorId=" + jobErrorId + ", jobId=" + jobId + ", docId=" + docId + ", pageId=" + pageId
				+ ", pageNr=" + pageNr + ", tsId=" + tsId + ", message=" + message + ", exceptionClass="
				+ exceptionClass + ", stacktrace=" + stacktrace + ", path=" + path + "]";
	}
}
