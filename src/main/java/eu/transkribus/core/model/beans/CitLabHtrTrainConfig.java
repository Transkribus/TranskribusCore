package eu.transkribus.core.model.beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import eu.transkribus.core.io.util.TrpProperties;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CitLabHtrTrainConfig extends HtrTrainConfig implements Serializable {
	
	public final static int DEFAULT_TRAIN_SIZE_PER_EPOCH = 10000;
	public final static String DEFAULT_NOISE = "both";
	public final static String DEFAULT_LEARNING_RATE = "1e-3";
	public final static int DEFAULT_NUM_EPOCHS = 20;

	private static final long serialVersionUID = 6826017343706433307L;
	protected Integer numEpochs = DEFAULT_NUM_EPOCHS;
	protected String learningRate = DEFAULT_LEARNING_RATE;
	protected String noise = DEFAULT_NOISE;
	protected Integer trainSizePerEpoch = DEFAULT_TRAIN_SIZE_PER_EPOCH;
	protected Integer baseModelId;
	
	public final static String NUM_EPOCHS_KEY = "Nr. of Epochs";
	public final static String LEARNING_RATE_KEY ="Learning Rate";
	public final static String NOISE_KEY = "Noise";
	public final static String TRAIN_SIZE_KEY = "Train Size per Epoch";
	public final static String BASE_MODEL_ID_KEY = "HTR Base Model ID";
	public final static String BASE_MODEL_NAME_KEY = "HTR Base Model Name";
	
//	private int threads=4;
//	private int subSampling = 4; // how many subsets are the pages divided into to reduce overfitting 
//								// max is the number of input pages, elsewise a RuntimeException is thrown during training!
	
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
