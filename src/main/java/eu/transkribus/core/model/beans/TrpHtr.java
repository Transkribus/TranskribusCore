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
	
	@Column(name="TRAIN_GT_DOCID")
	private int gtDocId;
	
	@Column(name="TEST_GT_DOCID")
	private int testGtDocId;
	
	@Column(name="LANGUAGE")
	private String language;
	
	@Column(name="BASE_HTR_ID")
	private Integer baseHtrId;
	
	@Column(name="TRAIN_JOB_ID")
	private String trainJobId;

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
	
	public int getTestGtDocId() {
		return testGtDocId;
	}

	public void setTestGtDocId(int testGtDocId) {
		this.testGtDocId = testGtDocId;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Integer getBaseHtrId() {
		return baseHtrId;
	}

	public void setBaseHtrId(Integer baseHtrId) {
		this.baseHtrId = baseHtrId;
	}
	
	public String getTrainJobId() {
		return trainJobId;
	}

	public void setTrainJobId(String trainJobId) {
		this.trainJobId = trainJobId;
	}

	@Override
	public String toString() {
		return "TrpHtr [htrId=" + htrId + ", name=" + name + ", description=" + description + ", provider=" + provider
				+ ", path=" + path + ", created=" + created + ", gtDocId=" + gtDocId + ", testGtDocId=" + testGtDocId
				+ ", language=" + language + ", baseHtrId=" + baseHtrId + ", trainJobId=" + trainJobId + "]";
	}

}
