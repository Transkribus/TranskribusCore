package eu.transkribus.core.model.beans.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import eu.transkribus.core.model.beans.TrpCreditTransaction;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({TrpCreditTransaction.class})
public class TrpCreditTransactionList extends JaxbPaginatedList<TrpCreditTransaction> {

	public TrpCreditTransactionList() {
		super();
	}

	public TrpCreditTransactionList(List<TrpCreditTransaction> list, int total, int index, int nValues, String sortColumnField,
			String sortDirection) {
		super(list, total, index, nValues, sortColumnField, sortDirection);
	}
}
