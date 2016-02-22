package eu.transkribus.core.model.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import eu.transkribus.core.model.beans.adapters.TrpRoleAdapter;
import eu.transkribus.core.model.beans.auth.TrpRole;

@Entity
@Table(name="USER_COLLECTION")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpUserCollection {
	@Id
	@Column(name="USER_ID")
	private int userId;
	@Column(name="COLLECTION_ID")
	private int colId;
	@Column(name="is_default")
	private boolean isDefault;
	@Column
	@XmlJavaTypeAdapter(TrpRoleAdapter.class)
	private TrpRole role = null;
	
	public TrpUserCollection() { }

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getColId() {
		return colId;
	}

	public void setColId(int colId) {
		this.colId = colId;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public TrpRole getRole() {
		return role;
	}

	public void setRole(TrpRole role) {
		this.role = role;
	}

	public String toString() {
	    final String TAB = ", ";
	    String retValue = "TrpUserCollection ( "+super.toString();
		retValue += TAB + "userId = " + this.userId;
		retValue += TAB + "colId = " + this.colId;
		retValue += TAB + "isDefault = " + this.isDefault;
		retValue += TAB + "role = " + this.role;
		retValue += " )";
	    return retValue;
	}
	
	

}
