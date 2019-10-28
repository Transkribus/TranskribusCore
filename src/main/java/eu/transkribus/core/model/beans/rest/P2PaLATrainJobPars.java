package eu.transkribus.core.model.beans.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.DocSelection;
import eu.transkribus.core.model.beans.DocumentSelectionDescriptor;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class P2PaLATrainJobPars {
	
//	@XmlElementWrapper(name="trainDocs")
//	private List<DocumentSelectionDescriptor> trainDocs = new ArrayList<>();
//	@XmlElementWrapper(name="valDocs")
//	private List<DocumentSelectionDescriptor> valDocs = new ArrayList<>();
//	@XmlElementWrapper(name="testDocs")
//	private List<DocumentSelectionDescriptor> testDocs = new ArrayList<>();
	
	@XmlElementWrapper(name="trainDocs")
	private List<DocSelection> trainDocs = new ArrayList<>();
	@XmlElementWrapper(name="valDocs")
	private List<DocSelection> valDocs = new ArrayList<>();
	@XmlElementWrapper(name="testDocs")
	private List<DocSelection> testDocs = new ArrayList<>();	
	
	private ParameterMap params = new ParameterMap();

	public List<DocSelection> getTrainDocs() {
		return trainDocs;
	}

	public void setTrainDocs(List<DocSelection> trainDocs) {
		this.trainDocs = trainDocs;
	}

	public List<DocSelection> getValDocs() {
		return valDocs;
	}

	public void setValDocs(List<DocSelection> valDocs) {
		this.valDocs = valDocs;
	}

	public List<DocSelection> getTestDocs() {
		return testDocs;
	}

	public void setTestDocs(List<DocSelection> testDocs) {
		this.testDocs = testDocs;
	}

	public ParameterMap getParams() {
		return params;
	}

	public void setParams(ParameterMap params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "P2PaLATrainJobPars [trainDocs=" + trainDocs + ", valDocs=" + valDocs + ", testDocs=" + testDocs
				+ ", params=" + params + "]";
	}
	
	

}
