package eu.transkribus.core.model.beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.io.util.TrpProperties;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PyLaiaHtrTrainConfig extends HtrTrainConfig implements Serializable {
	private static final long serialVersionUID = -7105887429420356434L;
	
	@Hidden
	public final static String NUM_EPOCHS_KEY = "Nr. of Epochs";
	@Hidden
	public final static String LEARNING_RATE_KEY ="Learning Rate";
	@Hidden
	public final static String BATCH_SIZE_KEY = "Train Size per Epoch";
	@Hidden
	public final static String EARLY_STOPPING_KEY = "Early Stopping";		
	
	public final static int DEFAULT_NUM_EPOCHS = 250;
	public final static int DEFAULT_EARLY_STOPPING = 20;
	public final static double DEFAULT_LEARNING_RATE = 0.0003d;
	public final static int DEFAULT_BATCH_SIZE = 20;
	
	@Schema(description = "the number of epochs. A positive natural number.", required=true)
	protected Integer numEpochs = DEFAULT_NUM_EPOCHS;
	protected int earlyStopping=DEFAULT_EARLY_STOPPING;
	protected double learningRate=DEFAULT_LEARNING_RATE;
	protected int batchSize=DEFAULT_BATCH_SIZE;
	
	@Schema(description = "Optional. Can be used to specify an existing HTR to be used as starting point for the training. Provider string must match the one given.", required=false)
	protected Integer baseModelId;
	
	protected TextFeatsCfg textFeatsCfg;
	
	public PyLaiaHtrTrainConfig() {
		super();
	}

	public Integer getNumEpochs() {
		return numEpochs;
	}

	public void setNumEpochs(Integer numEpochs) {
		this.numEpochs = numEpochs;
	}

	public Integer getBaseModelId() {
		return baseModelId;
	}

	public void setBaseModelId(Integer baseModelId) {
		this.baseModelId = baseModelId;
	}

	public int getEarlyStopping() {
		return earlyStopping;
	}

	public void setEarlyStopping(int earlyStopping) {
		this.earlyStopping = earlyStopping;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	
	public TextFeatsCfg getTextFeatsCfg() {
		return textFeatsCfg;
	}

	public void setTextFeatsCfg(TextFeatsCfg textFeatsCfg) {
		this.textFeatsCfg = textFeatsCfg;
	}

	@Hidden
	public TrpProperties getParamProps() {
		TrpProperties p = new TrpProperties();
		p.setProperty(NUM_EPOCHS_KEY, numEpochs);
		p.setProperty(LEARNING_RATE_KEY, ""+learningRate);
		p.setProperty(BATCH_SIZE_KEY, batchSize);
		p.setProperty(EARLY_STOPPING_KEY, earlyStopping);
		return p;
	}

	@Override
	public String toString() {
		return "PyLaiaHtrTrainConfig [numEpochs=" + numEpochs + ", earlyStopping=" + earlyStopping + ", learningRate="
				+ learningRate + ", batchSize=" + batchSize + ", baseModelId=" + baseModelId + ", textFeatsCfg="
				+ textFeatsCfg + ", modelName=" + modelName + ", description=" + description + ", language=" + language
				+ ", colId=" + colId + ", provider=" + provider + ", customParams=" + customParams + ", train=" + train
				+ ", test=" + test + ", trainGt=" + trainGt + ", testGt=" + testGt + "]";
	}
	
	
	
}
