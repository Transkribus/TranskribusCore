package eu.transkribus.core.model.beans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A transaction on a set of credits. May reference a job with the workload of
 * this transaction.
 */
@Entity
@Table(name = "TRANSACTIONS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpTransaction {

	@Column(name = "TRANSACTION_ID")
	int transactionId;

	@Column(name = "JOBID")
	Integer jobId;

	@Column(name = "CREDIT_ID")
	int creditId;

	@Column(name = "time")
	Date time;

	/**
	 * Positive or negative "token" value of this transaction.
	 */
	@Column(name = "TOKEN_VALUE")
	int value;

	@Column(name = "CREDIT_BALANCE")
	int creditBalance;

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

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
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
				+ ", time=" + time + ", value=" + value + ", creditBalance=" + creditBalance + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + creditBalance;
		result = prime * result + creditId;
		result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + transactionId;
		result = prime * result + value;
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
		TrpTransaction other = (TrpTransaction) obj;
		if (creditBalance != other.creditBalance)
			return false;
		if (creditId != other.creditId)
			return false;
		if (jobId == null) {
			if (other.jobId != null)
				return false;
		} else if (!jobId.equals(other.jobId))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (transactionId != other.transactionId)
			return false;
		if (value != other.value)
			return false;
		return true;
	}
}
