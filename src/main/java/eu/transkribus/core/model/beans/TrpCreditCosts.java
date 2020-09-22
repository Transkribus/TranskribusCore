package eu.transkribus.core.model.beans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="CREDIT_COSTS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpCreditCosts {
	
	@Id
	@GeneratedValue
	@Column(name="COSTS_ID")
	private Integer costsId;
	
	@Column(name="COST_FACTOR")
	@Transient
	private Double costFactor;
	
	@Column(name="CREATED")
	private Date costDate;
	
	@Column(name="JOB_IMPL_REGISTRY_ID")
	private int jobImplRegistryId;
	
	@Column
	@Transient
	private String jobImpl;
	
	@Column(name="DOC_TYPE")
	private String docType;
	
	public Integer getCostsId() {
		return costsId;
	}

	public void setCostsId(Integer costsId) {
		this.costsId = costsId;
	}
	
	public Double getCostFactor() {
		return costFactor;
	}

	public void setCostFactor(Double costFactor) {
		this.costFactor = costFactor;
	}

	public Date getCostDate() {
		return costDate;
	}

	public void setCostDate(Date costDate) {
		this.costDate = costDate;
	}

	public int getJobImplRegistryId() {
		return jobImplRegistryId;
	}

	public void setJobImplRegistryId(int jobImplRegistryId) {
		this.jobImplRegistryId = jobImplRegistryId;
	}

	public String getJobImpl() {
		return jobImpl;
	}

	public void setJobImpl(String jobImpl) {
		this.jobImpl = jobImpl;
	}
	
	public String getDocType() {
		return docType;
	}
	
	public void setDocType(String docType) {
		this.docType = docType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((costDate == null) ? 0 : costDate.hashCode());
		result = prime * result + ((costFactor == null) ? 0 : costFactor.hashCode());
		result = prime * result + ((costsId == null) ? 0 : costsId.hashCode());
		result = prime * result + ((docType == null) ? 0 : docType.hashCode());
		result = prime * result + ((jobImpl == null) ? 0 : jobImpl.hashCode());
		result = prime * result + jobImplRegistryId;
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
		TrpCreditCosts other = (TrpCreditCosts) obj;
		if (costDate == null) {
			if (other.costDate != null)
				return false;
		} else if (!costDate.equals(other.costDate))
			return false;
		if (costFactor == null) {
			if (other.costFactor != null)
				return false;
		} else if (!costFactor.equals(other.costFactor))
			return false;
		if (costsId == null) {
			if (other.costsId != null)
				return false;
		} else if (!costsId.equals(other.costsId))
			return false;
		if (docType == null) {
			if (other.docType != null)
				return false;
		} else if (!docType.equals(other.docType))
			return false;
		if (jobImpl == null) {
			if (other.jobImpl != null)
				return false;
		} else if (!jobImpl.equals(other.jobImpl))
			return false;
		if (jobImplRegistryId != other.jobImplRegistryId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TrpCreditCosts [costsId=" + costsId + ", costFactor=" + costFactor + ", costDate=" + costDate
				+ ", jobImplRegistryId=" + jobImplRegistryId + ", jobImpl=" + jobImpl + ", docType=" + docType + "]";
	}
}
