package eu.transkribus.core.model.beans;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "CREDIT_PACKAGES")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpCreditPackage {
	
	@Id
	@Column(name = "PACKAGE_ID")
	private int packageId;

	@Column(name = "PRODUCT_ID")
	private Integer productId;

	@Column(name = "PURCHASE_DATE")
	private Date purchaseDate;

	@Column(name = "EXPIRATION_DATE")
	private Date expirationDate;
	
	@Column(name = "IS_ACTIVE")
	private boolean active;
	
	@Column(name = "USER_ID")
	private int userId;
	
	/**
	 * The product instance as in organized in webshop. Defines e.g. nrOfCredits.
	 */
	@Transient
	private TrpCreditProduct product;
	
	@Column(name = "CREDIT_BALANCE")
	@Transient
	private Double balance;
	

	public TrpCreditPackage() {}
	
	public int getPackageId() {
		return packageId;
	}

	public void setPackageId(int packageId) {
		this.packageId = packageId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean isActive) {
		this.active = isActive;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public TrpCreditProduct getProduct() {
		return product;
	}
	
	public void setProduct(TrpCreditProduct product) {
		this.product = product;
	}
	
	public Double getBalance() {
		return balance;
	}
	
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	
	public int getNrOfTokens() {
		//delegate to product
		return product.getNrOfCredits();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + ((balance == null) ? 0 : balance.hashCode());
		result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
		result = prime * result + packageId;
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
		result = prime * result + ((purchaseDate == null) ? 0 : purchaseDate.hashCode());
		result = prime * result + userId;
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
		TrpCreditPackage other = (TrpCreditPackage) obj;
		if (active != other.active)
			return false;
		if (balance == null) {
			if (other.balance != null)
				return false;
		} else if (!balance.equals(other.balance))
			return false;
		if (expirationDate == null) {
			if (other.expirationDate != null)
				return false;
		} else if (!expirationDate.equals(other.expirationDate))
			return false;
		if (packageId != other.packageId)
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		if (purchaseDate == null) {
			if (other.purchaseDate != null)
				return false;
		} else if (!purchaseDate.equals(other.purchaseDate))
			return false;
		if (userId != other.userId)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TrpCreditPackage [packageId=" + packageId + ", productId=" + productId + ", purchaseDate="
				+ purchaseDate + ", expirationDate=" + expirationDate + ", active=" + active + ", userId=" + userId
				+ ", product=" + product + ", balance=" + balance + "]";
	}
}
