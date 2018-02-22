package eu.transkribus.core.model.beans.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.DocumentSelectionDescriptor;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
//@XmlSeeAlso({DocumentSelectionDescriptor.class, ParameterMap.class})
public class JobParameters {
	
	private Integer colId;
	private String jobImpl;
	
	@XmlElementWrapper(name="docList")
	private List<DocumentSelectionDescriptor> docs;
	private ParameterMap params;
	
	public JobParameters() {
		docs = new ArrayList<>(0);
		params = new ParameterMap();
	}
	
	public Integer getColId() {
		return colId;
	}

	public void setColId(Integer colId) {
		this.colId = colId;
	}

	public String getJobImpl() {
		return jobImpl;
	}

	public void setJobImpl(String jobImpl) {
		this.jobImpl = jobImpl;
	}

	public List<DocumentSelectionDescriptor> getDocs() {
		return docs;
	}

	public void setDocs(List<DocumentSelectionDescriptor> docs) {
		this.docs = docs;
	}

	public ParameterMap getParams() {
		return params;
	}

	public void setParams(ParameterMap params) {
		this.params = params;
	}
}
