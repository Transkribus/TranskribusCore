package eu.transkribus.core.model.beans.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import eu.transkribus.core.model.beans.TrpCreditPackage;
import eu.transkribus.core.model.beans.TrpCreditProduct;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({TrpCreditPackage.class, TrpCreditProduct.class})
public class TrpCreditPackageList extends JaxbPaginatedList<TrpCreditPackage> {

	private Double overallBalance;

	public TrpCreditPackageList() {
		super();
	}

	public TrpCreditPackageList(List<TrpCreditPackage> list, Double overallBalance, int total, int index, int nValues, String sortColumnField,
			String sortDirection) {
		super(list, total, index, nValues, sortColumnField, sortDirection);
		this.overallBalance = overallBalance;
	}
	
	public Double getOverallBalance() {
		return overallBalance;
	}

	public void setOverallBalance(Double overallBalance) {
		this.overallBalance = overallBalance;
	}

	@Override
	public String toString() {
		return "TrpCreditPackageList [overallBalance=" + overallBalance + ", total=" + total + ", index=" + index
				+ ", nValues=" + nValues + ", sortColumnField=" + sortColumnField + ", sortDirection=" + sortDirection
				+ ", list=" + list + "]";
	}
}
