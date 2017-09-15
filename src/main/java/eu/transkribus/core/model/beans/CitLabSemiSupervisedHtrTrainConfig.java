package eu.transkribus.core.model.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CitLabSemiSupervisedHtrTrainConfig extends CitLabHtrTrainConfig {
	private static final Logger logger = LoggerFactory.getLogger(CitLabSemiSupervisedHtrTrainConfig.class);
	
	private static final long serialVersionUID = 8243670144313403179L;
	
	// this parameter defines the number of training epochs per iteration of the semi-supervised training divided by semicolons
	public final static String DEFAULT_TRAINING_EPOCHS = "1;1;2;3;5;8;13;21;34;55";
	private String trainEpochs=DEFAULT_TRAINING_EPOCHS;
	public final static String TRAINING_EPOCHS_KEY = "Training epochs";

	// the subsampling parameter defines into how much subsets the training document is split to avoid overfitting.
	// Important: the parameter cannot exceed the number of input training pages (should be checked in TrpCITlabSemiSupervisedHtrTrainer)  
	public final static int DEFAULT_SUBSAMPLING = 4;
	private int subSampling = DEFAULT_SUBSAMPLING;
	public final static String SUBSAMPLING_KEY = "Subsampling";
	
	public final static boolean DEFAULT_REMOVE_LINEBREAKS = true;
	private boolean removeLineBreaks = true;
	public final static String REMOVE_LINEBREAKS_KEY = "Do not respect linebreaks";
	
	private int nThreads=4;
	
	/**
	 * A field for generic properties relevant to the T2I process
	 */
	private String jsonProps=null;
	

	public CitLabSemiSupervisedHtrTrainConfig() {
		setTrainEpochs(DEFAULT_TRAINING_EPOCHS);
	}
	
	public boolean isRemoveLineBreaks() {
		return removeLineBreaks;
	}

	public void setRemoveLineBreaks(boolean removeLineBreaks) {
		this.removeLineBreaks = removeLineBreaks;
	}

	public int getSubSampling() {
		return subSampling;
	}

	public void setSubSampling(int subSampling) {
		this.subSampling = subSampling;
	}

	public String getTrainEpochs() {
		return trainEpochs;
	}

	public boolean setTrainEpochs(String trainEpochs) {
		if (isValidTrainingEpochsString(trainEpochs)) {
			this.trainEpochs = trainEpochs;
			setNumEpochs(this.trainEpochs.split(";").length);
			return true;
		} else {
			logger.warn("Invalid training epochs string: "+trainEpochs+" leaving current value: "+trainEpochs);
			return false;
		}
	}
	
	public void setJsonProps(String jsonProps) {
		this.jsonProps = jsonProps;
	}
	
	public String getJsonProps() {
		return jsonProps;
	}

	public int getnThreads() {
		return nThreads;
	}

	public void setnThreads(int nThreads) {
		this.nThreads = nThreads;
	}

	@Override public void setNumEpochs(Integer numEpochs) {
		// does nothing as numEpochs is set fixed via trainEpochs parameter!
	}
	
	public boolean isValidTrainingEpochsString(String trainEpochs) {
		if (StringUtils.isEmpty(trainEpochs))
			return false;
		
		for (String epochStr : trainEpochs.split(";")) {
			try {
				Integer.parseInt(epochStr);
			}
			catch (NumberFormatException e) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public String toString() {
		return "CitLabSemiSupervisedHtrTrainConfig [trainEpochs=" + trainEpochs + ", subSampling=" + subSampling
				+ ", removeLineBreaks=" + removeLineBreaks + ", nThreads=" + nThreads + ", jsonProps=" + jsonProps
				+ ", learningRate=" + learningRate + ", noise=" + noise + ", trainSizePerEpoch=" + trainSizePerEpoch
				+ ", baseModelId=" + baseModelId + ", language=" + language + ", colId=" + colId + ", train=" + train
				+ ", test=" + test + "]";
	}

}
