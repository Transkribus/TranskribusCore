package eu.transkribus.core.model.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="ACTION_TYPES")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpActionType {
	@Column(name="TYPE_ID")
	private int id;
	@Column(name="TYPE")
	private String type;
	public TrpActionType(){}
	public TrpActionType(int id, String type){
		this.id = id;
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}