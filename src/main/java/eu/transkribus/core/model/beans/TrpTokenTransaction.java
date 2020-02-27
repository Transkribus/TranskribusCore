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
	private int tokenValue;
	
	@Column(name = "NR_OF_PAGES")
	private Integer nrOfPages;

	@Column(name = "CREDIT_BALANCE")
	private int creditBalance;

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

	public int getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(int tokenValue) {
		this.tokenValue = tokenValue;
	}
	
	public Integer getNrOfPages() {
		return nrOfPages;
	}
	
	public void setNrOfPages(Integer nrOfPages) {
		this.nrOfPages = nrOfPages;
	}

	public int getCreditBalance() {
		return creditBalance;
	}

	public void setCreditBalance(int creditBalance) {
		this.creditBalance = creditBalance;
	}

	@Override
	public String toString() {
		return "TrpTransaction [transactionId=" + transactionId + ", jobId=" + jobId + ", creditId=" + creditId
				+ ", time=" + time + ", tokenValue=" + tokenValue + ", nrOfPages=" + nrOfPages + ", creditBalance="
				+ creditBalance + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + creditBalance;
		result = prime * result + creditId;
		result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
		result = prime * result + ((nrOfPages == null) ? 0 : nrOfPages.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + tokenValue;
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
		if (creditBalance != other.creditBalance)
			return false;
		if (creditId != other.creditId)
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
		if (tokenValue != other.tokenValue)
			return false;
		if (transactionId != other.transactionId)
			return false;
		return true;
	}

	
}
