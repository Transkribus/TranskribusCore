package eu.transkribus.core.model.beans.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import eu.transkribus.core.model.beans.TrpCreditProduct;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({TrpCreditProduct.class})
public class TrpCreditProductList extends JaxbPaginatedList<TrpCreditProduct> {

	public TrpCreditProductList() {
		super();
	}

	public TrpCreditProductList(List<TrpCreditProduct> list, int total, int index, int nValues, String sortColumnField,
			String sortDirection) {
		super(list, total, index, nValues, sortColumnField, sortDirection);
	}

	@Override
	public String toString() {
		return "TrpCreditProductList [total=" + total + ", index=" + index
				+ ", nValues=" + nValues + ", sortColumnField=" + sortColumnField + ", sortDirection=" + sortDirection
				+ ", list=" + list + "]";
	}
}
