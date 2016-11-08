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
	protected String description;
	protected int colId;
	
	@XmlElementWrapper(name="TrainList")
	@XmlElement
	protected List<DocumentDuplicationDescriptor> trainList = new LinkedList<>();

	@XmlElementWrapper(name="TestList")
	@XmlElement
	protected List<DocumentDuplicationDescriptor> testList = new LinkedList<>();
	
	public String getModelName() {
		return modelName;
	}
	
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getColId() {
		return colId;
	}
	
	public void setColId(int colId) {
		this.colId = colId;
	}

	public List<DocumentDuplicationDescriptor> getTrainList() {
		return trainList;
	}

	public void setTrainList(List<DocumentDuplicationDescriptor> trainList) {
		this.trainList = trainList;
	}
	
	public List<DocumentDuplicationDescriptor> getTestList() {
		return testList;
	}

	public void setTestList(List<DocumentDuplicationDescriptor> testList) {
		this.testList = testList;
	}
}
