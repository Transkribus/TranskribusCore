package eu.transkribus.core.model.beans.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import eu.transkribus.core.model.beans.kws.TrpKeyWord;
import eu.transkribus.core.model.beans.kws.TrpKwsHit;
import eu.transkribus.core.model.beans.kws.TrpKwsQuery;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({TrpKwsQuery.class, TrpKeyWord.class, TrpKwsHit.class})
public class TrpKwsQueryList extends JaxbPaginatedList<TrpKwsQuery> {

	public TrpKwsQueryList() {
		super();
	}

	public TrpKwsQueryList(List<TrpKwsQuery> list, int total, int index, int nValues, String sortColumnField,
			String sortDirection) {
		super(list, total, index, nValues, sortColumnField, sortDirection);
	}
}
