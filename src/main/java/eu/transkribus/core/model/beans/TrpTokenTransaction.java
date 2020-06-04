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
 * A transaction on a set of credits. May reference a job with the workload of
 * this transaction.
 */
@Entity
@Table(name = "TOKEN_TRANSACTIONS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpTokenTransaction {

	@Id
	@Column(name = "TRANSACTION_ID")
	private int transactionId;

	@Column(name = "JOBID")
	private Integer jobId;

	@Column(name = "CREDIT_ID")
	private int creditId;

	@Column(name = "TIME")
	private Date time;

	/**
	 * Positive or negative "token" value of this transaction.
	 */
	@Column(name = "TOKEN_VALUE")
	private double tokenValue;
	
	@Column(name = "COST_FACTOR")
	private Double costFactor;
	
	@Column(name = "NR_OF_PAGES")
	private Double nrOfPages;

	@Column(name = "CREDIT_BALANCE")
	private double creditBalance;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	public TrpTokenTransaction() {}

	public TrpTokenTransaction(TrpTokenTransaction other) {
		this();
		this.transactionId = other.transactionId;
		this.jobId = other.jobId;
		this.creditId = other.creditId;
		this.time = other.time;
		this.tokenValue = other.tokenValue;
		this.costFactor = other.costFactor;
		this.nrOfPages = other.nrOfPages;
		this.creditBalance = other.creditBalance;
		this.description = other.description;
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

	public int getCreditId() {
		return creditId;
	}

	public void setCreditId(int creditId) {
		this.creditId = creditId;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public double getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(double tokenValue) {
		this.tokenValue = tokenValue;
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
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(costFactor);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(creditBalance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + creditId;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
		result = prime * result + ((nrOfPages == null) ? 0 : nrOfPages.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		temp = Double.doubleToLongBits(tokenValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		TrpTokenTransaction other = (TrpTokenTransaction) obj;
		if (Double.doubleToLongBits(costFactor) != Double.doubleToLongBits(other.costFactor))
			return false;
		if (Double.doubleToLongBits(creditBalance) != Double.doubleToLongBits(other.creditBalance))
			return false;
		if (creditId != other.creditId)
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
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (Double.doubleToLongBits(tokenValue) != Double.doubleToLongBits(other.tokenValue))
			return false;
		if (transactionId != other.transactionId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TrpTokenTransaction [transactionId=" + transactionId + ", jobId=" + jobId + ", creditId=" + creditId
				+ ", time=" + time + ", tokenValue=" + tokenValue + ", costFactor=" + costFactor + ", nrOfPages=" + nrOfPages + ", creditBalance="
				+ creditBalance + ", description=" + description + "]";
	}
}
