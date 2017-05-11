package eu.transkribus.core.model.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import eu.transkribus.core.model.beans.adapters.TrpRoleAdapter;
import eu.transkribus.core.model.beans.auth.TrpRole;

@Entity
@Table(name="COLLECTION")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpCollection implements Serializable {
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
	private boolean eLearning = false;
	
	@Column
	@Transient
	private String label;
	
	@Column
	@Transient
	@XmlJavaTypeAdapter(TrpRoleAdapter.class)
	private TrpRole role = null;
	
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
	
	public boolean isELearning() {
		return eLearning;
	}

	public void setELearning(boolean isELearning) {
		this.eLearning = isELearning;
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
	public String toString() {
		return "TrpCollection [colId=" + colId + ", colName=" + colName + ", description=" + description
				+ ", defaultForApp=" + defaultForApp + ", crowdsourcing=" + crowdsourcing + ", eLearning=" + eLearning
				+ ", label=" + label + ", role=" + role + "]";
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
