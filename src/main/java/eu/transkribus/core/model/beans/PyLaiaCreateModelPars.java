package eu.transkribus.core.model.beans;

import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.rest.ParameterMap;

@XmlRootElement(name="createModelPars")
public class PyLaiaCreateModelPars extends ParameterMap {
	private static final Logger logger = LoggerFactory.getLogger(PyLaiaCreateModelPars.class);
	
	// Possible parameter
//    [--logging_also_to_stderr LOGGING_ALSO_TO_STDERR]
//    [--logging_config LOGGING_CONFIG]
//    [--logging_file LOGGING_FILE]
//    [--logging_level LOGGING_LEVEL]
//    [--logging_overwrite [LOGGING_OVERWRITE]]
//    [--print_args [PRINT_ARGS]]
//    [--train_path TRAIN_PATH] [--seed SEED]
//    [--fixed_input_height FIXED_INPUT_HEIGHT]
//    [--adaptive_pooling ADAPTIVE_POOLING]
//    [--model_filename MODEL_FILENAME]
//    [--cnn_num_features CNN_NUM_FEATURES [CNN_NUM_FEATURES ...]]
//    [--cnn_kernel_size CNN_KERNEL_SIZE [CNN_KERNEL_SIZE ...]]
//    [--cnn_stride CNN_STRIDE [CNN_STRIDE ...]]
//    [--cnn_dilation CNN_DILATION [CNN_DILATION ...]]
//    [--cnn_activations {ReLU,Tanh,LeakyReLU} [{ReLU,Tanh,LeakyReLU} ...]]
//    [--cnn_poolsize CNN_POOLSIZE [CNN_POOLSIZE ...]]
//    [--cnn_dropout CNN_DROPOUT [CNN_DROPOUT ...]]
//    [--cnn_batchnorm CNN_BATCHNORM [CNN_BATCHNORM ...]]
//    [--rnn_units RNN_UNITS]
//    [--rnn_layers RNN_LAYERS]
//    [--rnn_dropout RNN_DROPOUT]
//    [--lin_dropout LIN_DROPOUT]
//    [--rnn_type {LSTM,GRU}]
//    [--vertical_text [VERTICAL_TEXT]]
//    [--use_masked_conv [USE_MASKED_CONV]]
	
	public PyLaiaCreateModelPars() {
	}
	
	public static PyLaiaCreateModelPars getDefault() {
		PyLaiaCreateModelPars pars = new PyLaiaCreateModelPars();
		pars.addParameter("--print_args", "True");
		pars.addParameter("--train_path", "./model");
		pars.addParameter("--model_filename", "model");
		pars.addParameter("--logging_level", "info");
//		pars.addParameter("--fixed_input_height", "128");
		pars.addParameter("--cnn_kernel_size", "3 3 3 3");
		pars.addParameter("--cnn_dilation", "1 1 1 1");
		pars.addParameter("--cnn_num_features", "12 24 48 48");
		pars.addParameter("--cnn_batchnorm", "True True True True");
		pars.addParameter("--cnn_activations", "LeakyReLU LeakyReLU LeakyReLU LeakyReLU");
		pars.addParameter("--cnn_poolsize", "2 2 0 2");
		pars.addParameter("--use_masked_conv", "True");
		pars.addParameter("--rnn_type", "LSTM");
		pars.addParameter("--rnn_layers", "3");
		pars.addParameter("--rnn_units", "256");
		pars.addParameter("--rnn_dropout", "0.5");
		pars.addParameter("--lin_dropout", "0.5");
		
		return pars;
	}
	
//	public void setFixedInputHeight(int fixedInputHeight) {
//		if (fixedInputHeight > 0) {
//			addParameter("--fixed_input_height", fixedInputHeight);	
//		}
//		else {
//			remove("--fixed_input_height");
//		}
//	}
	
	public Integer getFixedInputHeight() {
		return getIntParam("--fixed_input_height");
	}
	
	@Override
	public String toString() {
		return toSingleLineString();
	}
	
	public static PyLaiaCreateModelPars fromSingleLineString(String str) {
		ParameterMap m = ParameterMap.fromSingleLineString(str, "--", " ");
		PyLaiaCreateModelPars p = new PyLaiaCreateModelPars();
		p.setParamMap(m.getParamMap());
		return p;
	}
	
	public static PyLaiaCreateModelPars fromSingleLineString2(String str) {
		try {
			return fromSingleLineString(str);
		} catch (Exception e) {
			return null;
		}
	}	
	
	public static void main(String[] args) throws Exception {
//		PyLaiaCreateModelPars p = PyLaiaCreateModelPars.getDefault();
//		System.out.println(p.toSimpleString("\n"));
//		System.out.println("-------");
//		System.out.println(p.toSingleLineString());
		
		String str = "--print_args True --train_path ./model --model_filename model --logging_level info --cnn_kernel_size 3 3 3 3 --cnn_dilation 1 1 1 1 --cnn_num_features 12 24 48 48 --cnn_batchnorm True True True True --cnn_activations LeakyReLU LeakyReLU LeakyReLU LeakyReLU --cnn_poolsize 2 2 0 2 --use_masked_conv True --rnn_type LSTM --rnn_layers 3 --rnn_units 256 --rnn_dropout 0.5 --lin_dropout 0.5 --logging_also_to_stderr info --logging_file train-crnn.log --empty_par_inside --logging_overwrite False --empty_last_par";
		PyLaiaCreateModelPars p = PyLaiaCreateModelPars.fromSingleLineString(str);
		logger.info("str1 = "+str);
		logger.info("pars = "+p.toSingleLineString());
		
		if (str.equals(p.toSingleLineString())) {
			logger.info("SUCCESS!");
		}
		else {
			logger.info("ERROR!");
		}
	}

}
