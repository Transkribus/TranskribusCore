package eu.transkribus.core.model.beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.dea.fimgstoreclient.beans.ImgType;

import eu.transkribus.core.io.util.TrpProperties;
import eu.transkribus.core.model.beans.rest.ParameterMap;
import eu.transkribus.core.rest.JobConst;
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
	
//	@Schema(description = "the number of epochs. A positive natural number.", required=true)
//	protected Integer numEpochs = DEFAULT_NUM_EPOCHS;
//	protected int earlyStopping=DEFAULT_EARLY_STOPPING;
//	protected double learningRate=DEFAULT_LEARNING_RATE;
//	protected int batchSize=DEFAULT_BATCH_SIZE;
	
	@Schema(description = "Optional. Can be used to specify an existing HTR to be used as starting point for the training. Provider string must match the one given.", required=false)
	protected Integer baseModelId;
	
	protected TextFeatsCfg textFeatsCfg;
	protected PyLaiaCreateModelPars createModelPars;
	protected PyLaiaTrainCtcPars trainCtcPars;
	
	public PyLaiaHtrTrainConfig() {
		super();
		
		customParams = new ParameterMap();
		textFeatsCfg = new TextFeatsCfg();
		createModelPars = PyLaiaCreateModelPars.getDefault();
		trainCtcPars = PyLaiaTrainCtcPars.getDefault();
	}

	public Integer getNumEpochs() {
		return trainCtcPars.getMaxEpochs();
	}

	public void setNumEpochs(Integer numEpochs) {
		trainCtcPars.setMaxEpochs(numEpochs);
	}

	public Integer getBaseModelId() {
		return baseModelId;
	}

	public void setBaseModelId(Integer baseModelId) {
		this.baseModelId = baseModelId;
	}

	public int getEarlyStopping() {
		return trainCtcPars.getMaxNondecreasingEpochs();
	}

	public void setEarlyStopping(int earlyStopping) {
		trainCtcPars.setMaxNondecreasingEpochs(earlyStopping);
	}

	public Double getLearningRate() {
		return trainCtcPars.getLearningRate();
	}

	public void setLearningRate(Double learningRate) {
		trainCtcPars.setLearningRate(learningRate);
	}

	public int getBatchSize() {
		return trainCtcPars.getBatchSize();
	}

	public void setBatchSize(int batchSize) {
		trainCtcPars.setBatchSize(batchSize);
	}
	
	public TextFeatsCfg getTextFeatsCfg() {
		return textFeatsCfg;
	}

	public void setTextFeatsCfg(TextFeatsCfg textFeatsCfg) {
		this.textFeatsCfg = textFeatsCfg;
	}
	
	public PyLaiaCreateModelPars getCreateModelPars() {
		return createModelPars;
	}

	public void setCreateModelPars(PyLaiaCreateModelPars createModelPars) {
		this.createModelPars = createModelPars;
	}

	public PyLaiaTrainCtcPars getTrainCtcPars() {
		return trainCtcPars;
	}

	public void setTrainCtcPars(PyLaiaTrainCtcPars trainCtcPars) {
		this.trainCtcPars = trainCtcPars;
	}
	
	@Hidden
	public TrpProperties getParamProps() {
		TrpProperties p = new TrpProperties();
		if (textFeatsCfg != null) { // shouldn't be null anymore...
			p.getProperties().put("textFeatsCfg", textFeatsCfg.toConfigString().replaceAll("\n", " "));
		}
		if (createModelPars != null) { // shouldn't be null anymore...
			p.getProperties().put("createModelPars", createModelPars.toSingleLineString());
		}
		if (trainCtcPars != null) { // shouldn't be null anymore...
			p.getProperties().put("trainCtcPars", trainCtcPars.toSingleLineString());
		}
		
		return p;
	}

	@Override
	public String toString() {
		return "PyLaiaHtrTrainConfig [baseModelId=" + baseModelId 
				+ ", modelName=" + modelName + ", description=" + description + ", language=" + language
				+ ", colId=" + colId + ", provider=" + provider + ", train=" + train
				+ ", test=" + test + ", trainGt=" + trainGt + ", testGt=" + testGt
				+ ", textFeatsCfg="+ textFeatsCfg+ ", createModelPars="+createModelPars+ ", trainCtcPars="+trainCtcPars
				+ ", imgType="+getImgType()
				+"]";
	}
	
	
	
}
