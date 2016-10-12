package eu.transkribus.core.model.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="ACTIONS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpAction {
	public static final int SAVE = 1;
	public static final int LOGIN = 2;
	public static final int STATUS_CHANGE = 3;
	public static final int ACCESS_DOCUMENT = 4;

	public TrpAction() {}
	@Id
	@Column(name="ACTION_ID")
	private Integer actionId;
	@Column(name="TYPE_ID")
	private int typeId;
	@Transient
	private String type;
	@Column(name="USER_ID")
	private int userId;
	@Column(name="USER_NAME")
	private String userName;
	@Column
	private java.sql.Timestamp time;
	@Column(name="COL_ID")
	private Integer colId;
	@Transient
	private String colName;
	@Transient
	private String colDesc;
	@Column(name="DOC_ID")
	private Integer docId;
	@Transient
	private String docName;
	@Column(name="PAGE_ID")
	private Integer pageId;
	@Transient
	private Integer pageNr;
	@Column(name="CLIENT_ID")
	private Integer clientId;
	@Transient
	private String clientName;
	@Transient
	private String clientVersion;
	@Column(name="SESSION_HISTORY_ID")
	private Integer sessionHistoryId;
	@Column(name="USER_ROLE")
	private String userRole;

	public Integer getActionId() {
		return actionId;
	}


	public void setActionId(Integer actionId) {
		this.actionId = actionId;
	}


	public int getTypeId() {
		return typeId;
	}


	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
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


	public java.sql.Timestamp getTime() {
		return time;
	}


	public void setTime(java.sql.Timestamp time) {
		this.time = time;
	}


	public Integer getColId() {
		return colId;
	}


	public void setColId(Integer colId) {
		this.colId = colId;
	}


	public String getColName() {
		return colName;
	}


	public void setColName(String colName) {
		this.colName = colName;
	}

	public String getColDesc() {
		return colDesc;
	}
	
	public void setColDesc(String colDesc) {
		this.colDesc = colDesc;
	}

	public Integer getDocId() {
		return docId;
	}


	public void setDocId(Integer docId) {
		this.docId = docId;
	}


	public String getDocName() {
		return docName;
	}


	public void setDocName(String docName) {
		this.docName = docName;
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


	public Integer getClientId() {
		return clientId;
	}


	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}


	public String getClientName() {
		return clientName;
	}


	public void setClientName(String clientName) {
		this.clientName = clientName;
	}


	public String getClientVersion() {
		return clientVersion;
	}


	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}


	public Integer getSessionHistoryId() {
		return sessionHistoryId;
	}


	public void setSessionHistoryId(Integer sessionHistoryId) {
		this.sessionHistoryId = sessionHistoryId;
	}


	public String getUserRole() {
		return userRole;
	}


	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}


	@Override
	public String toString() {
		return "TrpAction [actionId=" + actionId + ", typeId=" + typeId + ", type=" + type + ", userId=" + userId
				+ ", userName=" + userName + ", time=" + time + ", colId=" + colId + ", colName=" + colName
				+ ", colDesc=" + colDesc + ", docId=" + docId + ", docName=" + docName + ", pageId=" + pageId
				+ ", pageNr=" + pageNr + ", clientId=" + clientId + ", clientName=" + clientName + ", clientVersion="
				+ clientVersion + ", sessionHistoryId=" + sessionHistoryId + ", userRole=" + userRole + "]";
	}
	
}
