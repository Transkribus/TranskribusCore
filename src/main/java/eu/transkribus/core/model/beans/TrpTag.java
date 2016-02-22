package eu.transkribus.core.model.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "tags")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpTag {
	@Id
	@GeneratedValue
	int tagId;
	@Column(name="xmlkey")
	private String key;
	@Column(name="region_id")
	private String parentRegId;
	@Column
	private String value;

	public TrpTag() {}
	public TrpTag(String key, String parentRegId, String value){
		this.key = key;
		this.parentRegId = parentRegId;
		this.value = value;
	}

	public int getTagId() {
		return tagId;
	}
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getParentRegId() {
		return parentRegId;
	}

	public void setParentRegId(String parentRegId) {
		this.parentRegId = parentRegId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName() + "{" + key + " - " + parentRegId + " - " + value + "}";
	}
}
