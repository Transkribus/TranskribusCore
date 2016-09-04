package eu.transkribus.core.model.beans;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="HISTORY")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpHistoryCall {
	@Id
	@Column(name="HISTORY_ID")
	private int id;
	
	@Column
	private int session_history_id;
	
	@Column
	private Timestamp time;
	
	@Column
	private String call;
	
	@Column
	private String parameter;
	
	@Column
	private String custom;
	
	@Column
	private Integer collId;
	
	@Column
	private Integer docId;
	
	@Column
	private Integer pageNr;
	
	@Column(name="ACTION_ID")
	private Integer actionId;
	
	@Transient
	private Integer pageId;
	
	@Transient
	private Integer userId;
	
	@Transient
	private String gui_version;
	
	public TrpHistoryCall() {}

	public TrpHistoryCall(int id, int session_history_id, Timestamp time, String call, String parameter, String custom) {
		super();
		this.id = id;
		this.session_history_id = session_history_id;
		this.time = time;
		this.call = call;
		this.parameter = parameter;
		this.custom = custom;
	}

	public TrpHistoryCall(TrpHistoryCall other) {
		super();
		this.id = other.id;
		this.session_history_id = other.session_history_id;
		this.time = other.time;
		this.call = other.call;
		this.parameter = other.parameter;
		this.custom = other.custom;
	}

	// bean getters & setters:
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSession_history_id() {
		return session_history_id;
	}

	public void setSession_history_id(int session_history_id) {
		this.session_history_id = session_history_id;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String getCall() {
		return call;
	}

	public void setCall(String call) {
		this.call = call;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}

	public Integer getCollId() {
		return collId;
	}

	public void setCollId(Integer collId) {
		this.collId = collId;
	}

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

	public Integer getPageNr() {
		return pageNr;
	}

	public void setPageNr(Integer pageNr) {
		this.pageNr = pageNr;
	}

	public Integer getActionId(){
		return actionId;
	}
	
	public void setActionId(Integer actionId){
		this.actionId = actionId;
	}
	
	public Integer getPageId() {
		return pageId;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getGui_version() {
		return gui_version;
	}

	public void setGui_version(String gui_version) {
		this.gui_version = gui_version;
	}

	@Override
	public String toString() {
		return "TrpHistoryCall [id=" + id + ", session_history_id=" + session_history_id + ", time=" + time + ", call="
				+ call + ", parameter=" + parameter + ", custom=" + custom + ", collId=" + collId + ", docId=" + docId
				+ ", pageNr=" + pageNr + ", actionId=" + actionId + ", pageId=" + pageId + ", userId=" + userId
				+ ", gui_version=" + gui_version + "]";
	}

}
