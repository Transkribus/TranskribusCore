package eu.transkribus.core.model.beans.rest;

import java.sql.Timestamp;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import eu.transkribus.core.model.beans.TrpHtr;

@XmlRootElement //(name="trpHtrList")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({TrpHtr.class, Timestamp.class})
public class TrpHtrList extends JaxbPaginatedList<TrpHtr> {

	public TrpHtrList() {
		super();
	}

	public TrpHtrList(List<TrpHtr> list, int total, int index, int nValues, String sortColumnField,
			String sortDirection) {
		super(list, total, index, nValues, sortColumnField, sortDirection);
	}
}
