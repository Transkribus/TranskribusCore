package eu.transkribus.core.model.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.DocumentSelectionDescriptor.PageDescriptor;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UroHtrTrainConfig extends HtrTrainConfig {
	protected Integer numEpochs;
	protected String learningRate;
	protected String noise;
	protected Integer trainSizePerEpoch;
	protected Integer baseModelId;
	
	public Integer getNumEpochs() {
		return numEpochs;
	}
	public void setNumEpochs(Integer numEpochs) {
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
	
	public boolean isTestAndTrainOverlapping() {
		for(DocumentSelectionDescriptor trainDsd : train) {
			for(DocumentSelectionDescriptor testDsd : test) {
				if(trainDsd.getDocId() == testDsd.getDocId()) {
					//found overlap in doc selection; compare each of the pages
					for(PageDescriptor trainP : trainDsd.getPages()) {
						for(PageDescriptor testP : testDsd.getPages()) {
							if(trainP.getPageId() == testP.getPageId()) {
								//same page was selected for test and train
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
}
