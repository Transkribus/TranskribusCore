package eu.transkribus.core.model.beans.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import eu.transkribus.core.model.beans.TrpTokenCredit;
import eu.transkribus.core.model.beans.TrpTokenCreditProduct;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({TrpTokenCredit.class, TrpTokenCreditProduct.class})
public class TrpTokenCreditList extends JaxbPaginatedList<TrpTokenCredit> {

	public TrpTokenCreditList() {
		super();
	}

	public TrpTokenCreditList(List<TrpTokenCredit> list, int total, int index, int nValues, String sortColumnField,
			String sortDirection) {
		super(list, total, index, nValues, sortColumnField, sortDirection);
	}
}
