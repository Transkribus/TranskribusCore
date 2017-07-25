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
@Table(name="CROWD_PROJECT_MILESTONE")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpCrowdProjectMilestone implements Serializable {	
	
	public TrpCrowdProjectMilestone() {
		super();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -8542381996102454011L;
	@Id
	@Column(name="ID")
	private Integer milestoneId;
	@Column(name="PROJECT_ID")
	private Integer projectId;
	@Column(name="TITLE")
	private String title;
	@Column(name="DESCRIPTION")
	private String description;
	@Column(name="DUE_DATE")
	private String dueDate;
	@Column(name="DATE_CREATED")
	private String date;
	
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDueDate() {
		return dueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String toShortString() {
		return "MilestoneId=" + milestoneId + ", Title=" + title + ", Description=" + description;
	}
	@Override
	public String toString() {
		return "TrpCrowdProjectMilestone [milestoneId=" + milestoneId + ", projectId=" + projectId + ", title=" + title
				+ ", description=" + description + ", dueDate=" + dueDate + ", date=" + date + "]";
	}
	
	
}
