package eu.transkribus.core.model.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.HtrCITlabUtils;

@Entity
@Table(name = "HTR_MODEL_COLS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@XmlType(name="") // needed to suppress Moxy's default 'type' attribute which collides with type attribute of the ATrpModel class
public class TrpHtrModel extends ATrpModel {
	private static final Logger logger = LoggerFactory.getLogger(TrpP2PaLA.class);
	
	public static final String TABLE_NAME = "HTR_MODEL_COLS";
	public static final String TYPE = "TrpHtrModel";
	
	public TrpHtrModel() {
		super();
	}

	public TrpHtrModel(TrpHtrModel otherHtrModel) {
		super(otherHtrModel);
		this.provider = otherHtrModel.provider;
		this.created = otherHtrModel.created;
		this.language = otherHtrModel.language;
		this.cerTrainString = otherHtrModel.cerTrainString;
		this.cerTrainSeries = otherHtrModel.cerTrainSeries;
		this.cerValString = otherHtrModel.cerValString;
		this.cerValSeries = otherHtrModel.cerValSeries;
		this.charSetString = otherHtrModel.charSetString;
		this.bestNetStored = otherHtrModel.bestNetStored;
		this.languageModelExists = otherHtrModel.languageModelExists;
		this.trainNrOfLines = otherHtrModel.trainNrOfLines;
		this.trainNrOfWords = otherHtrModel.trainNrOfWords;
		this.valNrOfLines = otherHtrModel.valNrOfLines;
		this.valNrOfWords = otherHtrModel.valNrOfWords;
	}
	
	@Column
	private String provider;
	
	@Column(name="LANGUAGE")
	private String language;
	
	@XmlTransient
	@Column(name="TRAIN_CER_SERIES")
	private String cerTrainString;
	
	private double[] cerTrainSeries;	
	
	@XmlTransient
	@Column(name="VAL_CER_SERIES")
	private String cerValString;
	
	private double[] cerValSeries;
	
	@Column(name="CHARSET")
	private String charSetString;
	
	private List<String> charSetList;
	
	@Column(name="HAS_BEST_NET")
	private boolean bestNetStored = true;
	
	@Column(name="HAS_LANGUAGE_MODEL")
	private boolean languageModelExists = false;
	
	@Column(name="TRAIN_NR_OF_LINES")
	private int trainNrOfLines;
	
	@Column(name="TRAIN_NR_OF_WORDS")
	private int trainNrOfWords;
	
	@Column(name="VAL_NR_OF_LINES")
	private int valNrOfLines;
	
	@Column(name="VAL_NR_OF_WORDS")
	private int valNrOfWords;
	
	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getCerTrainString() {
		return cerTrainString;
	}

	public void setCerTrainString(String cerTrainString) {
		this.cerTrainString = cerTrainString;
		cerTrainSeries = HtrCITlabUtils.parseCitlabCerString(cerTrainString);
	}
	
	public double[] getCerTrainSeries() {
		return cerTrainSeries;
	}

	public void setCerTrainSeries(double[] cerTrainSeries) {
		this.cerTrainSeries = cerTrainSeries;
	}
	
	public String getCerValString() {
		return cerValString;
	}

	public void setCerValString(String cerValString) {
		this.cerValString = cerValString;
		cerValSeries = HtrCITlabUtils.parseCitlabCerString(cerValString);
	}
	
	public double[] getCerValSeries() {
		return cerValSeries;
	}

	public void setCerValSeries(double[] cerValSeries) {
		this.cerValSeries = cerValSeries;
	}
	
	public String getCharSetString() {
		return charSetString;
	}

	public void setCharSetString(String charSet) {
		this.charSetString = charSet;
		this.charSetList = createCharSetList(charSet);
	}
	
	public void setCharSetList(List<String> charSetList) {
		this.charSetList = charSetList;
	}
	
	public List<String> getCharSetList() {
		return this.charSetList;
	}
	
	private List<String> createCharSetList(String charSetString) {
		if(StringUtils.isEmpty(charSetString)) {
			return new ArrayList<>();
		}
		return Arrays.asList(charSetString.split("\n"));
	}

	public boolean isBestNetStored() {
		return bestNetStored;
	}

	public void setBestNetStored(boolean bestNetStored) {
		this.bestNetStored = bestNetStored;
	}

	public boolean isLanguageModelExists() {
		return languageModelExists;
	}

	public void setLanguageModelExists(boolean dictionaryExists) {
		this.languageModelExists = dictionaryExists;
	}

	public String getParams() {
		return params;
	}
	
	public Properties getParamsProps() {
		if(params == null || params.isEmpty()) {
			return new Properties();
		}
		try {
			return CoreUtils.readPropertiesFromString(params);
		} catch (IOException ioe) {
			logger.error("Could nor read Properties from String:\n" + params);
			return new Properties();
		}
	}
	
	public void setParams(String params) {
		this.params = params;
	}

	public int getTrainNrOfLines() {
		return trainNrOfLines;
	}

	public void setTrainNrOfLines(int trainNrOfLines) {
		this.trainNrOfLines = trainNrOfLines;
	}

	public int getTrainNrOfWords() {
		return trainNrOfWords;
	}

	public void setTrainNrOfWords(int trainNrOfWords) {
		this.trainNrOfWords = trainNrOfWords;
	}
	
	public int getValNrOfLines() {
		return valNrOfLines;
	}

	public void setValNrOfLines(int valNrOfLines) {
		this.valNrOfLines = valNrOfLines;
	}

	public int getValNrOfWords() {
		return valNrOfWords;
	}

	public void setValNrOfWords(int valNrOfWords) {
		this.valNrOfWords = valNrOfWords;
	}
	
	public double getFinalTrainCerVal() {
		if(!hasCerTrainSeries()) {
			return -1;
		}
		return getCerTrainSeries()[getCerTrainSeries().length-1];
	}
	
	public boolean hasCerTrainSeries() {
		return cerTrainSeries != null && cerTrainSeries.length > 0;
	}

	public boolean hasCerValSeriesLog() {
		return cerValSeries != null && cerValSeries.length > 0;
	}
	
	@Override
	public String toString() {
		return "TrpHtrModel [modelId=" + modelId + ", name="
				+ name + ", type=" + type + ", description=" + description + ", path=" + path + ", created=" + created
				+ ", parentId=" + parentId + ", isActive=" + isActive + ", releaseLevel=" + releaseLevel + ", params="
				+ params + ", custom=" + custom + ", delTime=" + delTime + ", jobId=" + jobId + ", userId=" + userId
				+ ", userName=" + userName + ", minError=" + minError + ", provider=" + provider + ", language="
				+ language + ", cerTrainString=" + cerTrainString
				+ ", cerTrainSeries=" + Arrays.toString(cerTrainSeries) + ", cerValString=" + cerValString
				+ ", cerValSeries=" + Arrays.toString(cerValSeries) + ", charSetString=" + charSetString
				+ ", charSetList=" + charSetList + ", bestNetStored=" + bestNetStored + ", languageModelExists="
				+ languageModelExists + ", trainNrOfLines=" + trainNrOfLines + ", trainNrOfWords=" + trainNrOfWords
				+ ", valNrOfLines=" + valNrOfLines + ", valNrOfWords=" + valNrOfWords + ", nrOfTrainGtPages="
				+ nrOfTrainGtPages + ", nrOfValidationGtPages=" + nrOfValidationGtPages + ", collectionIdLink=" + collectionIdLink + ", ]";
	}

	public String toShortString() {
		return "TrpHtrModel [modelId=" + modelId + ", name="
				+ name + ", type=" + type + ", description=" + description + ", path=" + path + ", created=" + created
				+ ", parentId=" + parentId + ", isActive=" + isActive + ", releaseLevel=" + releaseLevel + ", params="
				+ params + ", custom=" + custom + ", delTime=" + delTime + ", jobId=" + jobId + ", userId=" + userId
				+ ", userName=" + userName + ", minError=" + minError + ", provider=" + provider + ", language="
				+ language + ", bestNetStored=" + bestNetStored + ", languageModelExists="
				+ languageModelExists + ", trainNrOfLines=" + trainNrOfLines + ", trainNrOfWords=" + trainNrOfWords
				+ ", valNrOfLines=" + valNrOfLines + ", valNrOfWords=" + valNrOfWords + ", nrOfTrainGtPages="
				+ nrOfTrainGtPages + ", nrOfValidationGtPages=" + nrOfValidationGtPages + ", collectionIdLink=" + collectionIdLink + ", ]";
	}

	@Override
	protected String getModelType() {
		return TYPE;
	}	
}
