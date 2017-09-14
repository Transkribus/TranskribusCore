package eu.transkribus.core.model.beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import eu.transkribus.core.io.util.TrpProperties;
import eu.transkribus.core.model.beans.DocumentSelectionDescriptor.PageDescriptor;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CitLabHtrTrainConfig extends HtrTrainConfig implements Serializable {

	private static final long serialVersionUID = 6826017343706433307L;
	protected Integer numEpochs;
	protected String learningRate;
	protected String noise;
	protected Integer trainSizePerEpoch;
	protected Integer baseModelId;
	
	public final static String NUM_EPOCHS_KEY = "Nr. of Epochs";
	public final static String LEARNING_RATE_KEY ="Learning Rate";
	public final static String NOISE_KEY = "Noise";
	public final static String TRAIN_SIZE_KEY = "Train Size per Epoch";
	public final static String BASE_MODEL_ID_KEY = "HTR Base Model ID";
	public final static String BASE_MODEL_NAME_KEY = "HTR Base Model Name";
	
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
	@Override
	public String toString() {
		return "CitLabHtrTrainConfig [numEpochs=" + numEpochs + ", learningRate=" + learningRate + ", noise=" + noise
				+ ", trainSizePerEpoch=" + trainSizePerEpoch + ", baseModelId=" + baseModelId + ", modelName="
				+ modelName + ", description=" + description + ", language=" + language + ", colId=" + colId
				+ ", train=" + "["+StringUtils.join(train, ", ")+"]" + ", test=" + "["+StringUtils.join(test, ", ")+"]"+ "]";
	}
	
	public TrpProperties getParamProps() {
		TrpProperties p = new TrpProperties();
		p.setProperty(NUM_EPOCHS_KEY, ""+numEpochs);
		p.setProperty(LEARNING_RATE_KEY, learningRate);
		p.setProperty(NOISE_KEY, noise);
		p.setProperty(TRAIN_SIZE_KEY, ""+trainSizePerEpoch);
		if(baseModelId != null) {
			p.setProperty(BASE_MODEL_ID_KEY, ""+baseModelId);
		}
		return p;
	}
	
	
	
}
