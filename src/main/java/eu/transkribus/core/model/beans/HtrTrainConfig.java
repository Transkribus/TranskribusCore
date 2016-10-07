package eu.transkribus.core.model.beans;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HtrTrainConfig {
	
	protected String modelName;
	
	@XmlElementWrapper(name="TrainList")
	@XmlElement
	protected List<DocumentDuplicationDescriptor> trainList = new LinkedList<>();

	public String getModelName() {
		return modelName;
	}
	
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	public List<DocumentDuplicationDescriptor> getTrainList() {
		return trainList;
	}

	public void setTrainList(List<DocumentDuplicationDescriptor> dddList) {
		this.trainList = dddList;
	}
}
