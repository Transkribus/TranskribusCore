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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionId == null) ? 0 : actionId.hashCode());
		result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result + ((clientName == null) ? 0 : clientName.hashCode());
		result = prime * result + ((clientVersion == null) ? 0 : clientVersion.hashCode());
		result = prime * result + ((colDesc == null) ? 0 : colDesc.hashCode());
		result = prime * result + ((colId == null) ? 0 : colId.hashCode());
		result = prime * result + ((colName == null) ? 0 : colName.hashCode());
		result = prime * result + ((docId == null) ? 0 : docId.hashCode());
		result = prime * result + ((docName == null) ? 0 : docName.hashCode());
		result = prime * result + ((pageId == null) ? 0 : pageId.hashCode());
		result = prime * result + ((pageNr == null) ? 0 : pageNr.hashCode());
		result = prime * result + ((sessionHistoryId == null) ? 0 : sessionHistoryId.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + typeId;
		result = prime * result + userId;
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		result = prime * result + ((userRole == null) ? 0 : userRole.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrpAction other = (TrpAction) obj;
		if (actionId == null) {
			if (other.actionId != null)
				return false;
		} else if (!actionId.equals(other.actionId))
			return false;
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (clientName == null) {
			if (other.clientName != null)
				return false;
		} else if (!clientName.equals(other.clientName))
			return false;
		if (clientVersion == null) {
			if (other.clientVersion != null)
				return false;
		} else if (!clientVersion.equals(other.clientVersion))
			return false;
		if (colDesc == null) {
			if (other.colDesc != null)
				return false;
		} else if (!colDesc.equals(other.colDesc))
			return false;
		if (colId == null) {
			if (other.colId != null)
				return false;
		} else if (!colId.equals(other.colId))
			return false;
		if (colName == null) {
			if (other.colName != null)
				return false;
		} else if (!colName.equals(other.colName))
			return false;
		if (docId == null) {
			if (other.docId != null)
				return false;
		} else if (!docId.equals(other.docId))
			return false;
		if (docName == null) {
			if (other.docName != null)
				return false;
		} else if (!docName.equals(other.docName))
			return false;
		if (pageId == null) {
			if (other.pageId != null)
				return false;
		} else if (!pageId.equals(other.pageId))
			return false;
		if (pageNr == null) {
			if (other.pageNr != null)
				return false;
		} else if (!pageNr.equals(other.pageNr))
			return false;
		if (sessionHistoryId == null) {
			if (other.sessionHistoryId != null)
				return false;
		} else if (!sessionHistoryId.equals(other.sessionHistoryId))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (typeId != other.typeId)
			return false;
		if (userId != other.userId)
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		if (userRole == null) {
			if (other.userRole != null)
				return false;
		} else if (!userRole.equals(other.userRole))
			return false;
		return true;
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
