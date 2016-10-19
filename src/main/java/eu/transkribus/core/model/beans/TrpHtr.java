package eu.transkribus.core.model.beans;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "HTR")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpHtr {
	@Id
	@Column(name="HTR_ID")
	private int htrId;
	
	@Column
	private String name;
	
	@Column
	private String description;
	
	@Column
	private String provider;
	
	@Column
	private String path;
	
	@Column
	private Timestamp created;
	
	@Column(name="GT_DOCID")
	private int gtDocId;

	public int getHtrId() {
		return htrId;
	}

	public void setHtrId(int htrId) {
		this.htrId = htrId;
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

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
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

	public int getGtDocId() {
		return gtDocId;
	}

	public void setGtDocId(int gtDocId) {
		this.gtDocId = gtDocId;
	}

	@Override
	public String toString() {
		return "TrpHtr [rnnId=" + htrId + ", name=" + name + ", description=" + description + ", provider=" + provider
				+ ", path=" + path + ", created=" + created + ", gtDocId=" + gtDocId + "]";
	}
	
}
