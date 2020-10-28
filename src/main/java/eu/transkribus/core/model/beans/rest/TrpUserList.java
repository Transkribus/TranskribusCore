package eu.transkribus.core.model.beans.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import eu.transkribus.core.model.beans.auth.TrpUser;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ TrpUser.class })
public class TrpUserList extends JaxbPaginatedList<TrpUser> {

	public TrpUserList() {
		super();
	}

	public TrpUserList(List<TrpUser> list, int total, int index, int nValues, String sortColumnField,
			String sortDirection) {
		super(list, total, index, nValues, sortColumnField, sortDirection);
	}
}
