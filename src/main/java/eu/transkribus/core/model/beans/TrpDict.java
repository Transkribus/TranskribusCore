package eu.transkribus.core.model.beans;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "DICT")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpDict {
	@Id
	@Column(name="DICT_ID")
	private int dictId;
	
	@Column
	private String name;
	
	@Column
	private String description;
	
	@Column(name="LANGUAGE")
	private String language;
	
	@XmlTransient
	@Column
	private String path;
	
	@Column
	private Timestamp created;

	public int getDictId() {
		return dictId;
	}

	public void setDictId(int dictId) {
		this.dictId = dictId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	@Override
	public String toString() {
		return "TrpDict [dictId=" + dictId + ", name=" + name + ", description=" + description + ", language="
				+ language + ", path=" + path + ", created=" + created + "]";
	}
	
}
