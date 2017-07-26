package eu.transkribus.core.model.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="CROWD_PROJECT_MESSAGE")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpCrowdProjectMessage implements Serializable {
	
	public TrpCrowdProjectMessage() {
		super();
	}
	
	public TrpCrowdProjectMessage(String subject, String message, Integer mstId, String date) {
		super();
		this.subject = subject;
		this.message = message;
		this.milestoneId = mstId;
		this.date = date;
	}
	
	public TrpCrowdProjectMessage(String subject, String message, String date) {
		super();
		this.subject = subject;
		this.message = message;
		this.date = date;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 9108121773775394004L;
	@Id
	@Column(name="ID")
	private Integer messageId;
	@Column(name="MILESTONE_ID")
	private Integer milestoneId;
	@Column(name="PROJECT_ID")
	private Integer projectId;
	@Column(name="SUBJECT")
	private String subject;
	@Column(name="MESSAGE")
	private String message;
	@Column(name="DATE_CREATED")
	private String date;
	@Column(name="EMAIL_SENT")
	private boolean emailSent = false;
	public Integer getMessageId() {
		return messageId;
	}
	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}
	public Integer getMilestoneId() {
		return milestoneId;
	}
	public void setMilestoneId(Integer milestoneId) {
		this.milestoneId = milestoneId;
	}
	public Integer getProjectId() {
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public boolean isEmailSent() {
		return emailSent;
	}
	public void setEmailSent(boolean emailSent) {
		this.emailSent = emailSent;
	}
	public void update(String subject, String message, Integer mstId, String date) {
		this.subject = subject;
		this.message = message;
		this.milestoneId = mstId;
		this.date = date;
	}

	@Override
	public String toString() {
		return "TrpCrowdProjectMessage [messageId=" + messageId + ", milestoneId=" + milestoneId + ", projectId="
				+ projectId + ", subject=" + subject + ", message=" + message + ", date=" + date + ", emailSent="
				+ emailSent + "]";
	}

	
	
}
