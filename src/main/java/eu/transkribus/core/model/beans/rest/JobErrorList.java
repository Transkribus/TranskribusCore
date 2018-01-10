package eu.transkribus.core.model.beans.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import eu.transkribus.core.model.beans.job.JobError;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({JobError.class})
public class JobErrorList extends JaxbPaginatedList<JobError> {
	
	public JobErrorList() {
		super();
	}

	public JobErrorList(List<JobError> list, int total, int index, int nValues, String sortColumnField,
			String sortDirection) {
		super(list, total, index, nValues, sortColumnField, sortDirection);
	}
}
