package eu.transkribus.core.model.beans;

import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.rest.ParameterMap;

@XmlRootElement(name="trainCtcPars")
public class PyLaiaTrainCtcPars extends ParameterMap {
//    [--logging_also_to_stderr LOGGING_ALSO_TO_STDERR]
//    [--logging_config LOGGING_CONFIG]
//    [--logging_file LOGGING_FILE]
//    [--logging_level LOGGING_LEVEL]
//    [--logging_overwrite [LOGGING_OVERWRITE]]
//    [--print_args [PRINT_ARGS]]
//    [--batch_size BATCH_SIZE]
//    [--learning_rate LEARNING_RATE]
//    [--momentum MOMENTUM] [--gpu GPU]
//    [--max_epochs MAX_EPOCHS] [--seed SEED]
//    [--show_progress_bar [SHOW_PROGRESS_BAR]]
//    [--train_path TRAIN_PATH]
//    [--train_samples_per_epoch TRAIN_SAMPLES_PER_EPOCH]
//    [--valid_samples_per_epoch VALID_SAMPLES_PER_EPOCH]
//    [--iterations_per_update N]
//    [--save_checkpoint_interval N]
//    [--num_rolling_checkpoints N]
//    [--use_distortions [USE_DISTORTIONS]]
//    [--delimiters DELIMITERS [DELIMITERS ...]]
//    [--max_nondecreasing_epochs MAX_NONDECREASING_EPOCHS]
//    [--model_filename MODEL_FILENAME]
//    [--checkpoint CHECKPOINT]
//    [--use_baidu_ctc [USE_BAIDU_CTC]]
//    [--add_logsoftmax_to_loss [ADD_LOGSOFTMAX_TO_LOSS]]
	
	public final static int DEFAULT_MAX_EPOCHS = 250;
	public final static int DEFAULT_MAX_NONDECREASING_EPOCHS = 20;
	public final static double DEFAULT_LEARNING_RATE = 0.0003d;
	public final static int DEFAULT_BATCH_SIZE = 24;
	
	public PyLaiaTrainCtcPars() {
	}
	
	public static PyLaiaTrainCtcPars getDefault() {
		PyLaiaTrainCtcPars pars = new PyLaiaTrainCtcPars();
		pars.addParameter("--max_nondecreasing_epochs", ""+DEFAULT_MAX_NONDECREASING_EPOCHS);
		pars.addParameter("--max_epochs", ""+DEFAULT_MAX_EPOCHS);		
		pars.addParameter("--batch_size", ""+DEFAULT_BATCH_SIZE);
		pars.addParameter("--learning_rate", ""+DEFAULT_LEARNING_RATE);
		pars.addParameter("--print_args", "True");
		pars.addParameter("--delimiters", "<SPACE>"); // note: this argument cannot be on the last position for some reason!
		pars.addParameter("--use_baidu_ctc", "True");
		pars.addParameter("--add_logsoftmax_to_loss", "False");
		pars.addParameter("--train_path", "./model");
		pars.addParameter("--logging_level", "info");
		pars.addParameter("--logging_also_to_stderr", "info");
		pars.addParameter("--logging_file", "train-crnn.log");
		pars.addParameter("--show_progress_bar", "False");
		pars.addParameter("--use_distortions", "True");
		
		return pars;
	}
	
	public Integer getMaxEpochs() {
		return getIntParam("--max_epochs");
	}

	public void setMaxEpochs(Integer maxEpochs) {
		addParameter("--max_epochs", ""+maxEpochs);
	}	
	
	public Integer getMaxNondecreasingEpochs() {
		return getIntParam("--max_nondecreasing_epochs");
	}

	public void setMaxNondecreasingEpochs(int maxNondecreasingEpochs) {
		addParameter("--max_nondecreasing_epochs", ""+maxNondecreasingEpochs);
	}

	public Double getLearningRate() {
		return getDoubleParam("--learning_rate");
	}

	public void setLearningRate(double learningRate) {
		addParameter("--learning_rate", ""+learningRate);
	}

	public Integer getBatchSize() {
		return getIntParam("--batch_size");
	}

	public void setBatchSize(int batchSize) {
		addParameter("--batch_size", ""+batchSize);
	}	
	
	@Override
	public String toString() {
		return toSingleLineString();
	}	
	
	public static void main(String[] args) throws Exception {
		PyLaiaTrainCtcPars p = PyLaiaTrainCtcPars.getDefault();
		System.out.println(p.toSingleLineString());
	}	
	
	
}
