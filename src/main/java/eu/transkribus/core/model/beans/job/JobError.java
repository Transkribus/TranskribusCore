package eu.transkribus.core.model.beans.job;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

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
	private int docId;
	@Column
	private int pageId;
	@Column
	private int pageNr;
	@Column
	private int tsId;
	@Column(name="MSG")
	private String message;
	@Column(name="EX_CLASS")
	private String exceptionClass;
	@XmlTransient
	@Column
	private String stacktrace;
	
	public JobError() {}
	
	public JobError(int jobId, int docId, int pageId, int pageNr, int tsId, Throwable throwable) {
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

	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public int getPageId() {
		return pageId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	public int getPageNr() {
		return pageNr;
	}
	public void setPageNr(int pageNr) {
		this.pageNr = pageNr;
	}
	public int getTsId() {
		return tsId;
	}
	public void setTsId(int tsId) {
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

	public void setThrowable(Throwable t) {
		final String errorMsg;
		if (t instanceof NullPointerException) {
			errorMsg = "NullPointerException";
		} else {
			errorMsg = t.getMessage();
		}
		this.setMessage(errorMsg);
		this.setExceptionClass(t.getClass().getName());
		final String stacktrace = ExceptionUtils.getStackTrace(t);
		this.setStacktrace(stacktrace);
	}

	@Override
	public String toString() {
		return "JobError [jobErrorId=" + jobErrorId + ", jobId=" + jobId + ", docId=" + docId + ", pageId=" + pageId
				+ ", pageNr=" + pageNr + ", tsId=" + tsId + ", message=" + message + ", exceptionClass="
				+ exceptionClass + ", stacktrace=" + stacktrace + "]";
	}
}
