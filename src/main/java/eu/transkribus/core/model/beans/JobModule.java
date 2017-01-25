package eu.transkribus.core.model.beans;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "JOB_MODULE")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JobModule {
	@Id
	@Column(name = "URL")
	String url;
	
	@Column(name = "JOB_TYPE")
	String jobType;
	
	@Column(name = "JOB_TASK")
	String jobTask;
	
	@Column(name = "TOOL_PROVIDER")
	String toolProvider;	
	
	@Column(name = "TOOL_VERSION")
	String toolVersion;
	
	@Column(name = "REGISTERED_TIME")
	java.sql.Timestamp registeredTime;

	public JobModule(String url, String jobType, String jobTask, String toolProvider, String toolVersion) {
		super();
		this.url = url;
		this.jobType = jobType;
		this.jobTask = jobTask;
		this.toolProvider = toolProvider;
		this.toolVersion = toolVersion;
		this.registeredTime = new java.sql.Timestamp(System.currentTimeMillis());
	}

	public JobModule() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getJobTask() {
		return jobTask;
	}

	public void setJobTask(String jobTask) {
		this.jobTask = jobTask;
	}

	public String getToolProvider() {
		return toolProvider;
	}

	public void setToolProvider(String toolProvider) {
		this.toolProvider = toolProvider;
	}

	public String getToolVersion() {
		return toolVersion;
	}

	public void setToolVersion(String toolVersion) {
		this.toolVersion = toolVersion;
	}

	public java.sql.Timestamp getRegisteredTime() {
		return registeredTime;
	}

	public void setRegisteredTime(java.sql.Timestamp registeredTime) {
		this.registeredTime = registeredTime;
	}

	@Override
	public String toString() {
		return "JobModule [url=" + url + ", jobType=" + jobType + ", jobTask=" + jobTask + ", toolProvider="
				+ toolProvider + ", toolVersion=" + toolVersion + ", registeredTime=" + registeredTime + "]";
	}

}
