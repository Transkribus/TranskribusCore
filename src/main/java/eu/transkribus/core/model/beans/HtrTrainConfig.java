package eu.transkribus.core.model.beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class HtrTrainConfig implements Serializable {

	private static final long serialVersionUID = 1434111712220564100L;
	protected String modelName;
	protected String description;
	protected String language;
	protected int colId;
	
	@XmlElementWrapper(name="TrainList")
	@XmlElement
	protected List<DocumentSelectionDescriptor> train = new LinkedList<>();

	@XmlElementWrapper(name="TestList")
	@XmlElement
	protected List<DocumentSelectionDescriptor> test = new LinkedList<>();
	
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
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getColId() {
		return colId;
	}
	
	public void setColId(int colId) {
		this.colId = colId;
	}

	public List<DocumentSelectionDescriptor> getTrain() {
		return train;
	}

	public void setTrain(List<DocumentSelectionDescriptor> train) {
		this.train = train;
	}
	
	public List<DocumentSelectionDescriptor> getTest() {
		return test;
	}

	public void setTest(List<DocumentSelectionDescriptor> test) {
		this.test = test;
	}
}
