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
public class P2PaLATrainJobPars {
	
	@XmlElementWrapper(name="trainDocs")
	private List<DocumentSelectionDescriptor> trainDocs = new ArrayList<>();
	@XmlElementWrapper(name="valDocs")
	private List<DocumentSelectionDescriptor> valDocs = new ArrayList<>();
	@XmlElementWrapper(name="testDocs")
	private List<DocumentSelectionDescriptor> testDocs = new ArrayList<>();
	
	private ParameterMap params = new ParameterMap();

	public List<DocumentSelectionDescriptor> getTrainDocs() {
		return trainDocs;
	}

	public void setTrainDocs(List<DocumentSelectionDescriptor> trainDocs) {
		this.trainDocs = trainDocs;
	}

	public List<DocumentSelectionDescriptor> getValDocs() {
		return valDocs;
	}

	public void setValDocs(List<DocumentSelectionDescriptor> valDocs) {
		this.valDocs = valDocs;
	}

	public List<DocumentSelectionDescriptor> getTestDocs() {
		return testDocs;
	}

	public void setTestDocs(List<DocumentSelectionDescriptor> testDocs) {
		this.testDocs = testDocs;
	}

	public ParameterMap getParams() {
		return params;
	}

	public void setParams(ParameterMap params) {
		this.params = params;
	}
	
	

}
