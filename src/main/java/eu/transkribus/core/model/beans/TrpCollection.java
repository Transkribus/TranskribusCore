package eu.transkribus.core.model.beans;

import java.io.Serializable;
import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import eu.transkribus.core.model.beans.adapters.TrpRoleAdapter;
import eu.transkribus.core.model.beans.auth.TrpRole;

@Entity
@Table(name="COLLECTION")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpCollection extends ATotalTranscriptStatistics implements Serializable {
	private static final long serialVersionUID = -6247876122034400418L;
	
	private static final String IS_CROWDSOURCING_COLUMN_NAME = "IS_CROWDSOURCING";
	private static final String IS_ELEARNING_COLUMN_NAME = "IS_ELEARNING";
	
	@Id
	@Column(name="COLLECTION_ID")
	private int colId;
	@Column(name="NAME")
	private String colName;
	@Column
	private String description;
	
	@Column(name="DEFAULT_FOR_APP")
//	@Transient
	private String defaultForApp = null;
	
	@Column(name=IS_CROWDSOURCING_COLUMN_NAME)
	private boolean crowdsourcing = false;
	
	@Column(name=IS_ELEARNING_COLUMN_NAME)
	private boolean elearning = false;
	
	//id of thee symbolic image
	@Column(name="PAGE_ID")
	private Integer pageId;
	
	@Column
	@Transient
	private URL url;
	@Column
	@Transient
	private URL thumbUrl;
	
	@Column
	@Transient
	private String label;
	
	//actually nrOfDocuments is no real column. Transient annotated fields are ignored on inserts
	@Column
	@Transient
	private int nrOfDocuments = 0;
	
	@Column
	@Transient
	@XmlJavaTypeAdapter(TrpRoleAdapter.class)
	private TrpRole role = null;
	
	@XmlElement
	private TrpCrowdProject crowdProject = null;
	
	@Column(name="ACCOUNTING_STATUS")
	private Integer accountingStatus;
	
	public TrpCollection(){}
	public TrpCollection(final int colId, final String name, final String description){
		this.colId = colId;
		this.colName = name;
		this.description = description;
	}
	public TrpCollection(TrpCollection c) {
		this();
		colId = c.getColId();
		colName = c.getColName();
		description = c.getDescription();
		defaultForApp = c.getDefaultForApp();
		label = c.getLabel();
		role = c.getRole();
		nrOfDocuments = c.getNrOfDocuments();
	}
	
	public int getColId() {
		return colId;
	}
	public void setColId(int colId) {
		this.colId = colId;
	}
	public String getColName() {
		return colName;
	}
	public void setColName(String colName) {
		this.colName = colName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public TrpRole getRole() {
		return role;
	}
	public void setRole(TrpRole role) {
		this.role = role;
	}
	
	public String getDefaultForApp() {
		return defaultForApp;
	}
	public void setDefaultForApp(String defaultForApp) {
		this.defaultForApp = defaultForApp;
	}
	
	public boolean isCrowdsourcing() {
		return crowdsourcing;
	}

	public void setCrowdsourcing(boolean isCrowdsourcing) {
		this.crowdsourcing = isCrowdsourcing;
	}
	
	public boolean isElearning() {
		return elearning;
	}

	public void setElearning(boolean isElearning) {
		this.elearning = isElearning;
	}
	
	public Integer getPageId() {
		return pageId;
	}
	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public URL getThumbUrl() {
		return thumbUrl;
	}
	public void setThumbUrl(URL thumbUrl) {
		this.thumbUrl = thumbUrl;
	}
	public String getSummary() {
		return getColName() +" ("+getColId()+", "+ (getRole() == null ? "Admin" : getRole())+")";
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public int getNrOfDocuments() {
		return nrOfDocuments;
	}
	public void setNrOfDocuments(int nrOfDocuments) {
		this.nrOfDocuments = nrOfDocuments;
	}
	
	public TrpCrowdProject getCrowdProject() {
		return crowdProject;
	}
	public void setCrowdProject(TrpCrowdProject crowdProject) {
		this.crowdProject = crowdProject;
	}
	public Integer getAccountingStatus() {
		return accountingStatus;
	}
	public void setAccountingStatus(Integer accountingStatus) {
		this.accountingStatus = accountingStatus;
	}
	public String toShortString() {
		return 	this.getColId() 
				+ " - " 
				+ this.getColName() 
				+ " - " 
				+ this.getDescription() 
				+ " - " 
				+ this.getRole();
	}
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountingStatus == null) ? 0 : accountingStatus.hashCode());
		result = prime * result + colId;
		result = prime * result + ((colName == null) ? 0 : colName.hashCode());
		result = prime * result + ((crowdProject == null) ? 0 : crowdProject.hashCode());
		result = prime * result + (crowdsourcing ? 1231 : 1237);
		result = prime * result + ((defaultForApp == null) ? 0 : defaultForApp.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + (elearning ? 1231 : 1237);
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + nrOfDocuments;
		result = prime * result + ((pageId == null) ? 0 : pageId.hashCode());
		result = prime * result + ((role == null) ? 0 : role.hashCode());
		result = prime * result + ((thumbUrl == null) ? 0 : thumbUrl.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		TrpCollection other = (TrpCollection) obj;
		if (accountingStatus == null) {
			if (other.accountingStatus != null)
				return false;
		} else if (!accountingStatus.equals(other.accountingStatus))
			return false;
		if (colId != other.colId)
			return false;
		if (colName == null) {
			if (other.colName != null)
				return false;
		} else if (!colName.equals(other.colName))
			return false;
		if (crowdProject == null) {
			if (other.crowdProject != null)
				return false;
		} else if (!crowdProject.equals(other.crowdProject))
			return false;
		if (crowdsourcing != other.crowdsourcing)
			return false;
		if (defaultForApp == null) {
			if (other.defaultForApp != null)
				return false;
		} else if (!defaultForApp.equals(other.defaultForApp))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (elearning != other.elearning)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (nrOfDocuments != other.nrOfDocuments)
			return false;
		if (pageId == null) {
			if (other.pageId != null)
				return false;
		} else if (!pageId.equals(other.pageId))
			return false;
		if (role != other.role)
			return false;
		if (thumbUrl == null) {
			if (other.thumbUrl != null)
				return false;
		} else if (!thumbUrl.equals(other.thumbUrl))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	public String equalsWoUrl(Object obj, boolean withNumDocs) {
		if (this == obj)
			return null;
		if (obj == null)
			return "is null";
		if (getClass() != obj.getClass())
			return "class";
		TrpCollection other = (TrpCollection) obj;
		if (accountingStatus == null) {
			if (other.accountingStatus != null)
				return "accountingStatus";
		} else if (!accountingStatus.equals(other.accountingStatus))
			return "accountingStatus";
		if (colId != other.colId)
			return "colId";
		if (colName == null) {
			if (other.colName != null)
				return "colName null";
		} else if (!colName.equals(other.colName))
			return "colName";
		if (crowdsourcing != other.crowdsourcing)
			return "crowdsourcing";
		if (defaultForApp == null) {
			if (other.defaultForApp != null)
				return "defaultForApp null";
		} else if (!defaultForApp.equals(other.defaultForApp))
			return "defaultForApp";
		if (description == null) {
			if (other.description != null)
				return "Description null";
		} else if (!description.equals(other.description))
			return "description";
		if (elearning != other.elearning)
			return "eLearning";
		if (pageId == null) {
			if (other.pageId != null)
				return "pageId null";
		} else if (!pageId.equals(other.pageId))
			return "pageId";
		if (label == null) {
			if (other.label != null)
				return "label null";
		} else if (!label.equals(other.label))
			return "label";
		if (role != other.role)
			return "role";
		if (withNumDocs && nrOfDocuments != other.nrOfDocuments)
			return "numDocs";
		return null;
	}
	
	
	
	@Override
	public String toString() {
		return "TrpCollection [colId=" + colId + ", colName=" + colName + ", description=" + description
				+ ", defaultForApp=" + defaultForApp + ", crowdsourcing=" + crowdsourcing + ", elearning=" + elearning
				+ ", pageId=" + pageId + ", label=" + label + ", nrOfDocuments=" + nrOfDocuments 
				+ ", role=" + role + ", crowdProject=" + crowdProject + ", url=" + url 
				+ ", thumbUrl=" + thumbUrl + "]";
	}



	public static enum TrpCollectionFlag {
		crowdsourcing(IS_CROWDSOURCING_COLUMN_NAME),
		eLearning(IS_ELEARNING_COLUMN_NAME);
		
		private final String colName;
		private TrpCollectionFlag(String colName) {
			this.colName = colName;
		}
		public final String getColumnName() {
			return this.colName;
		}
	}
}
