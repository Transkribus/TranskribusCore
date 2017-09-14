package eu.transkribus.core.model.beans;

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
	
	public static final String SERVICE_TYPE_WEB = "web";
	public static final String SERVICE_TYPE_CONSOLE = "console";
	
	@Id
	@Column(name = "URL")
	String url;
	
	@Column(name = "NAME")
	String name;
	
	@Column(name = "TASKS")
	String tasks;
	
	@Column(name = "VERSION")
	String version;
	
	@Column(name = "REGISTERED_TIME")
	java.util.Date registeredTime;
	
	@Column(name = "UNREGISTERED_TIME")
	java.util.Date unregisteredTime;
	
	@Column(name = "ISACTIVE")
	Integer isActive;
	
	@Column(name = "SERVICE_TYPE")
	String serviceType;

	public JobModule(String url, String name, String tasks, String version, String serviceType) {
		super();
		this.url = url;
		this.name = name;
		this.tasks = tasks;
		this.version = version;
		this.registeredTime = new java.sql.Timestamp(System.currentTimeMillis());
		this.isActive = 1;
		this.serviceType = serviceType;
	}

	public JobModule() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTasks() {
		return tasks;
	}

	public void setTasks(String tasks) {
		this.tasks = tasks;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public java.util.Date getRegisteredTime() {
		return registeredTime;
	}

	public void setRegisteredTime(java.util.Date registeredTime) {
		this.registeredTime = registeredTime;
	}

	public java.util.Date getUnregisteredTime() {
		return unregisteredTime;
	}

	public void setUnregisteredTime(java.util.Date unregisteredTime) {
		this.unregisteredTime = unregisteredTime;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	@Override
	public String toString() {
		return "JobModule [url=" + url + ", name=" + name + ", tasks=" + tasks + ", version=" + version
				+ ", registeredTime=" + registeredTime + ", unregisteredTime=" + unregisteredTime + ", isActive="
				+ isActive + ", serviceType=" + serviceType + "]";
	}

}
