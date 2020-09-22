package eu.transkribus.core.model.beans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A transaction on a credit package. May reference a job with the workload of
 * this transaction.
 */
@Entity
@Table(name = "CREDIT_TRANSACTIONS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpCreditTransaction {

	@Id
	@Column(name = "TRANSACTION_ID")
	private int transactionId;

	@Column(name = "JOBID")
	private Integer jobId;

	@Column(name = "PACKAGE_ID")
	private int packageId;

	@Column(name = "TIME")
	private Date time;

	/**
	 * Positive or negative nr. of credits accounted in this transaction.
	 */
	@Column(name = "CREDIT_VALUE")
	private double creditValue;
	
	/**
	 * Optional link to CREDIT_COSTS entry applied regarding costFactor
	 */
	@Column(name = "COSTS_ID")
	private Integer costsId;
	
	@Column(name = "COST_FACTOR")
	private Double costFactor;
	
	@Column(name = "NR_OF_PAGES")
	private Double nrOfPages;

	@Column(name = "CREDIT_BALANCE")
	private double creditBalance;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "PARENT_ID")
	private Integer parentId;
	
	public TrpCreditTransaction() {}

	public TrpCreditTransaction(TrpCreditTransaction other) {
		this();
		this.transactionId = other.transactionId;
		this.jobId = other.jobId;
		this.packageId = other.packageId;
		this.time = other.time;
		this.creditValue = other.creditValue;
		this.costFactor = other.costFactor;
		this.nrOfPages = other.nrOfPages;
		this.creditBalance = other.creditBalance;
		this.description = other.description;
		this.parentId = other.parentId;
		this.costsId = other.costsId;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public int getPackageId() {
		return packageId;
	}

	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public double getCreditValue() {
		return creditValue;
	}

	public void setCreditValue(double creditValue) {
		this.creditValue = creditValue;
	}
	
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
	
	public Double getNrOfPages() {
		return nrOfPages;
	}
	
	public void setNrOfPages(Double nrOfPages) {
		this.nrOfPages = nrOfPages;
	}

	public double getCreditBalance() {
		return creditBalance;
	}

	public void setCreditBalance(double creditBalance) {
		this.creditBalance = creditBalance;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((costFactor == null) ? 0 : costFactor.hashCode());
		result = prime * result + ((costsId == null) ? 0 : costsId.hashCode());
		long temp;
		temp = Double.doubleToLongBits(creditBalance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(creditValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
		result = prime * result + ((nrOfPages == null) ? 0 : nrOfPages.hashCode());
		result = prime * result + packageId;
		result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + transactionId;
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
		TrpCreditTransaction other = (TrpCreditTransaction) obj;
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
		if (Double.doubleToLongBits(creditBalance) != Double.doubleToLongBits(other.creditBalance))
			return false;
		if (Double.doubleToLongBits(creditValue) != Double.doubleToLongBits(other.creditValue))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (jobId == null) {
			if (other.jobId != null)
				return false;
		} else if (!jobId.equals(other.jobId))
			return false;
		if (nrOfPages == null) {
			if (other.nrOfPages != null)
				return false;
		} else if (!nrOfPages.equals(other.nrOfPages))
			return false;
		if (packageId != other.packageId)
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (transactionId != other.transactionId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TrpCreditTransaction [transactionId=" + transactionId + ", jobId=" + jobId + ", packageId=" + packageId
				+ ", time=" + time + ", creditValue=" + creditValue + ", costsId=" + costsId + ", costFactor="
				+ costFactor + ", nrOfPages=" + nrOfPages + ", creditBalance=" + creditBalance + ", description="
				+ description + ", parentId=" + parentId + "]";
	}
}
