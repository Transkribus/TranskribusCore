package eu.transkribus.core.model.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UroHtrTrainConfig extends HtrTrainConfig {
	protected String numEpochs;
	protected String learningRate;
	protected String noise;
	protected Integer trainSizePerEpoch;
	protected Integer baseModelId;
	
	public String getNumEpochs() {
		return numEpochs;
	}
	public void setNumEpochs(String numEpochs) {
		this.numEpochs = numEpochs;
	}
	public String getLearningRate() {
		return learningRate;
	}
	public void setLearningRate(String learningRate) {
		this.learningRate = learningRate;
	}
	public String getNoise() {
		return noise;
	}
	public void setNoise(String noise) {
		this.noise = noise;
	}
	public Integer getTrainSizePerEpoch() {
		return trainSizePerEpoch;
	}
	public void setTrainSizePerEpoch(Integer trainSizePerEpoch) {
		this.trainSizePerEpoch = trainSizePerEpoch;
	}
	public Integer getBaseModelId() {
		return baseModelId;
	}
	public void setBaseModelId(Integer baseModelId) {
		this.baseModelId = baseModelId;
	}
}
