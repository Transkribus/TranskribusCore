package eu.transkribus.core.model.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="CROWD_PROJECT")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpCrowdProject implements Serializable {	
	private static final long serialVersionUID = -7113252844094661623L;
	
	public TrpCrowdProject (){
	}
	
	public TrpCrowdProject (Integer colId){
		this.colId = colId;
	}
	
	@Id
	@Column(name="PROJ_ID")
	private Integer projId;
	@Column(name="AIM")
	private String aim;
	@Column(name="COLLECTION_ID")
	private Integer colId;
	
	@XmlElementWrapper(name="milestoneList")
	@XmlElement
	private ArrayList<TrpCrowdProjectMilestone> crowdProjectMilestones = new ArrayList<TrpCrowdProjectMilestone>();
	
	@XmlElementWrapper(name="messageList")
	@XmlElement
	private ArrayList<TrpCrowdProjectMessage> crowdProjectMessages = new ArrayList<TrpCrowdProjectMessage>();
		
	public Integer getProjId() {
		return projId;
	}
	public void setProjId(Integer projId) {
		this.projId = projId;
	}
	public String getAim() {
		return aim;
	}
	public void setAim(String aim) {
		this.aim = aim;
	}
	public Integer getColId() {
		return colId;
	}
	public void setColId(Integer colId) {
		this.colId = colId;
	}
	public ArrayList<TrpCrowdProjectMilestone> getCrowdProjectMilestones() {
		return crowdProjectMilestones;
	}
	public TrpCrowdProjectMilestone getCrowdProjectMilestone(Integer id) {
		for (TrpCrowdProjectMilestone milestone : crowdProjectMilestones){
			if (milestone.getMilestoneId() == id){
				return milestone;
			}
		}
		return null;
	}
	public void setCrowdProjectMilestones(ArrayList<TrpCrowdProjectMilestone> crowdProjectMilestones) {
		this.crowdProjectMilestones = crowdProjectMilestones;
	}
	public ArrayList<TrpCrowdProjectMessage> getCrowdProjectMessages() {
		return crowdProjectMessages;
	}
	public TrpCrowdProjectMessage getCrowdProjectMessage(Integer id) {
		for (TrpCrowdProjectMessage message : crowdProjectMessages){
			if (message.getMessageId() == id){
				return message;
			}
		}
		return null;
	}
	public void setCrowdProjectMessages(ArrayList<TrpCrowdProjectMessage> crowdProjectMessages) {
		this.crowdProjectMessages = crowdProjectMessages;
	}
	@Override
	public String toString() {
		return "TrpCrowdProject [projId=" + projId + ", aim=" + aim + ", colId=" + colId + ", crowdProjectMilestones="
				+ crowdProjectMilestones + ", crowdProjectMessages=" + crowdProjectMessages + "]";
	}

	


}
