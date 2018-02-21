package eu.transkribus.core.model.beans.rest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.DocumentSelectionDescriptor;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
//@XmlSeeAlso({DocumentSelectionDescriptor.class, ParameterMap.class})
public class JobParameters {
	
	private List<DocumentSelectionDescriptor> descriptors;
	private ParameterMap params;
	
	public JobParameters() {
		descriptors = new ArrayList<>(0);
		params = new ParameterMap();
	}
	
	public List<DocumentSelectionDescriptor> getDescriptors() {
		return descriptors;
	}

	public void setDescriptors(List<DocumentSelectionDescriptor> descriptors) {
		this.descriptors = descriptors;
	}

	public ParameterMap getParams() {
		return params;
	}

	public void setParams(ParameterMap params) {
		this.params = params;
	}
}
