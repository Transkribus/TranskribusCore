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
 * A "Product" is the entity as shown in the web shop and defines the base attributes of a credit package. 
 * Upon purchase an instance of a TrpCreditPackage is created and stored for the user, linking to the respective product.
 */
@Entity
@Table(name = "CREDIT_PRODUCTS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpCreditProduct {
	
	@Id
	@Column(name = "PRODUCT_ID")
	private Integer productId;
	
	@Column(name = "CREDIT_TYPE")
	private String creditType;
	
	@Column(name = "NR_OF_CREDITS")
	private int nrOfCredits;
	
	@Column(name = "LABEL")
	private String label;

	@Column(name= "SHAREABLE")
	private boolean shareable;
	
	@Column(name = "SUBSCRIPTION")
	private String subscription;
	
	@Column(name = "STATUS")
	private Integer status;
	
	@Column(name = "CREATED")
	private Date created;
	
	@Column(name = "EXTERNAL_PRODUCT_ID")
	private Integer externalProductId;

	public TrpCreditProduct() {}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getCreditType() {
		return creditType;
	}
	
	public void setCreditType(String creditType) {
		this.creditType = creditType;
	}
	
	public int getNrOfCredits() {
		return nrOfCredits;
	}

	public void setNrOfCredits(int nrOfCredits) {
		this.nrOfCredits = nrOfCredits;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public boolean getShareable() {
		return shareable;
	}

	public void setShareable(boolean shareable) {
		this.shareable = shareable;
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Integer getExternalProductId() {
		return externalProductId;
	}

	public void setExternalProductId(Integer externalProductId) {
		this.externalProductId = externalProductId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((creditType == null) ? 0 : creditType.hashCode());
		result = prime * result + ((externalProductId == null) ? 0 : externalProductId.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + nrOfCredits;
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
		result = prime * result + (shareable ? 1231 : 1237);
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((subscription == null) ? 0 : subscription.hashCode());
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
		TrpCreditProduct other = (TrpCreditProduct) obj;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (creditType == null) {
			if (other.creditType != null)
				return false;
		} else if (!creditType.equals(other.creditType))
			return false;
		if (externalProductId == null) {
			if (other.externalProductId != null)
				return false;
		} else if (!externalProductId.equals(other.externalProductId))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (nrOfCredits != other.nrOfCredits)
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		if (shareable != other.shareable)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (subscription == null) {
			if (other.subscription != null)
				return false;
		} else if (!subscription.equals(other.subscription))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TrpCreditProduct [productId=" + productId + ", creditType=" + creditType + ", nrOfCredits="
				+ nrOfCredits + ", label=" + label + ", shareable=" + shareable + ", subscription=" + subscription
				+ ", status=" + status + ", created=" + created + ", externalProductId=" + externalProductId + "]";
	}
}
