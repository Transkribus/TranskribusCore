package eu.transkribus.core.model.beans;

import java.io.File;
import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.util.HtrUtils;

@Entity
@Table(name = "HTR_MODELS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HtrModel {
	private static final Logger logger = LoggerFactory.getLogger(HtrModel.class);
	public static final String PROJ_MAT_NAME = "Proj-Mat.mat";
	public static final String HMM_DIR_NAME = "hmms";
	public static final String DICT_NAME = "dictionary.dic";
	public static final String SLF_FILE_NAME = "LM.slf";
	public static final String HMM_LIST_NAME = "HMMs.lst";
	public static final String TRAIN_INFO_NAME = "train-info.txt";
	public static final String LABELS_MLF_NAME = "labels.mlf";
	public static final String TRAIN_TEXT_NAME = "Train-text";
	
	
	
	@Id
	@Column(name = "MODEL_ID")
	private Integer modelId = null;
	
	@Column(name = "MODEL_NAME")
	protected String modelName = null;
	
	@XmlTransient
	@Column(name = "PATH")
	private String baseDirPath;
	
	@XmlTransient
	protected File baseDir;
	
	@Column
	private String label;
	
	@Column(name="IS_USABLE_IN_TRANSKRIBUS")
	private int isUsableInTranskribus = 1;
	
	@Column(name="LANGUAGE")
	private String language;
	@Column(name="NR_OF_TOKENS")
	private Integer nrOfTokens;
	@Column(name="NR_OF_LINES")
	private Integer nrOfLines;
	@Column(name="NR_OF_DICT_TOKENS")
	private Integer nrOfDictTokens;
	
	@XmlTransient
	protected File hmmDir;
	@XmlTransient
	protected File dict;
	@XmlTransient
	protected File netSlfFile;
	@XmlTransient
	protected File hmmList;
	@XmlTransient
	protected File projMat;
	@XmlTransient
	protected File trainInfo;
	@XmlTransient
	protected File labelsMlf;
	@XmlTransient
	protected File trainText;
	
	public HtrModel(){}
	
	public HtrModel(String modelName, File baseDir) throws IOException{
		this(baseDir);
		if(modelName == null || modelName.isEmpty()){
			throw new IllegalArgumentException("ModelName is null or empty!");
		}
		this.modelName = modelName;
	}
	
	public HtrModel(File baseDir) throws IOException{
		this.baseDir = baseDir;
		this.baseDirPath = baseDir.getAbsolutePath();
		if(modelName == null){
			modelName = baseDir.getName();
		}
		loadModel(baseDir);
	}

	public Integer getModelId() {
		return modelId;
	}

	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}

	public String getModelName(){
		return this.modelName;
	}
	
	public void setModelName(String modelName){
		this.modelName = modelName;
	}
	
	public String getBaseDirPath(){
		return this.baseDirPath;
	}
	
	public void setBaseDirPath(String baseDirPath){
		this.baseDirPath = baseDirPath;
		this.baseDir = new File(this.baseDirPath);
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getIsUsableInTranskribus() {
		return isUsableInTranskribus;
	}

	public void setIsUsableInTranskribus(int isUsableInTranskribus) {
		this.isUsableInTranskribus = isUsableInTranskribus;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getNrOfTokens() {
		return nrOfTokens;
	}

	public void setNrOfTokens(int nrOfTokens) {
		this.nrOfTokens = nrOfTokens;
	}

	public int getNrOfLines() {
		return nrOfLines;
	}

	public void setNrOfLines(int nrOfLines) {
		this.nrOfLines = nrOfLines;
	}

	public int getNrOfDictTokens() {
		return nrOfDictTokens;
	}

	public void setNrOfDictTokens(int nrOfDictTokens) {
		this.nrOfDictTokens = nrOfDictTokens;
	}

	public File getHmmDir() {
		return hmmDir;
	}
	public void setHmmDir(File hmmDir) {
		this.hmmDir = hmmDir;
	}
	public File getDict() {
		return dict;
	}
	public void setDict(File dict) {
		this.dict = dict;
	}
	public File getNetSlfFile() {
		return netSlfFile;
	}
	public void setNetSlfFile(File netSlfFile) {
		this.netSlfFile = netSlfFile;
	}
	public File getHmmList() {
		return hmmList;
	}
	public void setHmmList(File hmmList) {
		this.hmmList = hmmList;
	}
	public File getProjMat() {
		return projMat;
	}
	public void setProjMat(File projMat) {
		this.projMat = projMat;
	}
	public File getTrainInfo() {
		return trainInfo;
	}
	public void setTrainInfo(File trainInfo) {
		this.trainInfo = trainInfo;
	}
	
	public File getLabelsMlf() {
		return labelsMlf;
	}

	public void setLabelsMlf(File labelsMlf) {
		this.labelsMlf = labelsMlf;
	}

	public File getTrainText() {
		return trainText;
	}

	public void setTrainText(File trainText) {
		this.trainText = trainText;
	}

	public void loadModel() throws IOException{
		this.loadModel(this.baseDir);
	}
	
	protected void loadModel(File baseDir) throws IOException {
		hmmDir = new File(baseDir.getAbsolutePath() + File.separator + HMM_DIR_NAME);
		dict = new File(baseDir.getAbsolutePath() + File.separator + DICT_NAME);
		netSlfFile = new File(baseDir.getAbsolutePath() + File.separator + SLF_FILE_NAME);
		hmmList = new File(baseDir.getAbsolutePath() + File.separator + HMM_LIST_NAME);
		projMat = new File(baseDir.getAbsolutePath() + File.separator + PROJ_MAT_NAME);
		trainInfo = new File(baseDir.getAbsolutePath() + File.separator + TRAIN_INFO_NAME);
		labelsMlf = new File(baseDir.getAbsolutePath() + File.separator + LABELS_MLF_NAME);
		trainText = new File(baseDir.getAbsolutePath() + File.separator + TRAIN_TEXT_NAME);
		
		if(!hmmDir.isDirectory())
			throw new IOException("No hmm dir in: " + baseDir.getAbsolutePath());
		if(!dict.isFile())
			throw new IOException("No dictionary in: " + baseDir.getAbsolutePath());
		if(!netSlfFile.isFile())
			throw new IOException("No slf in: " + baseDir.getAbsolutePath());
		if(!hmmList.isFile())
			throw new IOException("No hmm list in: " + baseDir.getAbsolutePath());
		if(!projMat.isFile())
			throw new IOException("No Proj-Mat.mat in: " + baseDir.getAbsolutePath());
		if(!trainInfo.isFile())
			throw new IOException("No train-info.txt in: " + baseDir.getAbsolutePath());
		//no check is done for labelsMlf and traintext as those are not needed for recognition
	}
	
	public void persistFiles() throws IOException{
		final File modelDir = new File(HtrUtils.MODEL_PATH + modelName);
		if(baseDir.getAbsolutePath().equals(modelDir.getAbsolutePath())){
			logger.info("Model is already stored...");
			return;
		}
		logger.info("Storing model at: " + modelDir.getAbsolutePath());
		FileUtils.copyFileToDirectory(dict, modelDir);
		FileUtils.copyFileToDirectory(hmmList, modelDir);
		FileUtils.copyFileToDirectory(netSlfFile, modelDir);
		FileUtils.copyFileToDirectory(projMat, modelDir);
		FileUtils.copyFileToDirectory(trainInfo, modelDir);
		FileUtils.copyFileToDirectory(labelsMlf, modelDir);
		FileUtils.copyFileToDirectory(trainText, modelDir);
		FileUtils.copyDirectoryToDirectory(hmmDir, modelDir);
		this.baseDir = modelDir;
		this.baseDirPath = this.baseDir.getAbsolutePath();
		loadModel(modelDir);
	}
	
	@Override
	public String toString(){
		final String tanga = "HtrModel { modelName = " + modelName + " | baseDirPath = " + baseDirPath 
				+ " | language = " + language 
				+ " | nrOfTokens = " + nrOfTokens + " | nrOfLines = " + nrOfLines 
				+ " | nrOfDictTokens = " + nrOfDictTokens + " }";				
		return tanga;
	}
}
