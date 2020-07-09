package eu.transkribus.core.model.beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.dea.fimgstoreclient.beans.ImgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.DocumentSelectionDescriptor.PageDescriptor;
import eu.transkribus.core.model.beans.rest.ParameterMap;
import eu.transkribus.core.rest.JobConst;
import eu.transkribus.core.util.HtrCITlabUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HtrTrainConfig implements Serializable {
	private static final Logger logger = LoggerFactory.getLogger(HtrTrainConfig.class);

	private static final long serialVersionUID = 1434111712220564100L;
	@Schema(description = "the name of the new HTR model", required=true)
	protected String modelName;
	@Schema(description = "textual description", required=true)
	protected String description;
	@Schema(description = "specifies the language of the training data's text content", required=true)
	protected String language;
	protected int colId;
	@Schema(description = "the HTR techology provider", allowableValues =  {HtrCITlabUtils.PROVIDER_CITLAB, HtrCITlabUtils.PROVIDER_CITLAB_PLUS})
	protected String provider;
	@Schema(description = "map with custom parameters", implementation=ParameterMap.class)
	protected ParameterMap customParams;
	
	public HtrTrainConfig() {
		provider = null;
		customParams = new ParameterMap();
	}
	
	@XmlElementWrapper(name="trainList")
	@XmlElement
	protected List<DocumentSelectionDescriptor> train = new LinkedList<>();

	@XmlElementWrapper(name="testList")
	@XmlElement
	protected List<DocumentSelectionDescriptor> test = new LinkedList<>();
	
	@XmlElementWrapper(name="trainGtList")
	@XmlElement
	protected List<GroundTruthSelectionDescriptor> trainGt;
	
	@XmlElementWrapper(name="testGtList")
	@XmlElement
	protected List<GroundTruthSelectionDescriptor> testGt;
	
	public String getModelName() {
		return modelName;
	}
	
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getColId() {
		return colId;
	}
	
	public void setColId(int colId) {
		this.colId = colId;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public ParameterMap getCustomParams() {
		if (customParams == null) {
			customParams = new ParameterMap();
		}
		
		return customParams;
	}

	public void setCustomParams(ParameterMap customParams) {
		this.customParams = customParams;
	}

	public void setCustomParam(String key, Object value) {
		getCustomParams().addParameter(key, value);
	}
	
	public void removeCustomParam(String key) {
		getCustomParams().remove(key);
	}
	
	public void setImgType(ImgType imgType) {
		setCustomParam(JobConst.PROP_IMG_TYPE, imgType);
	}
	
	public ImgType getImgType() {
		String imgTypeStr = getCustomParams().getParameterValue(JobConst.PROP_IMG_TYPE);
		if (imgTypeStr == null) {
			return ImgType.orig;
		}
		try {	
			return ImgType.valueOf(imgTypeStr);
		} catch (Exception e) {
			logger.warn("Could not parse image type: "+imgTypeStr+" - using imgType "+ImgType.orig);
			return ImgType.orig;
		}
	}
	
	public List<DocumentSelectionDescriptor> getTrain() {
		return train;
	}

	public void setTrain(List<DocumentSelectionDescriptor> train) {
		if (train == null) {
			train = new LinkedList<>();
		} else {
			this.train = train;	
		}
	}
	
	public List<DocumentSelectionDescriptor> getTest() {
		return test;
	}

	public void setTest(List<DocumentSelectionDescriptor> test) {
		if (test == null) {
			test = new LinkedList<>();
		} else {
			this.test = test;	
		}
	}
	
	public List<GroundTruthSelectionDescriptor> getTrainGt() {
		return trainGt;
	}

	public void setTrainGt(List<GroundTruthSelectionDescriptor> trainGt) {
		this.trainGt = trainGt;	
	}
	
	public List<GroundTruthSelectionDescriptor> getTestGt() {
		return testGt;
	}

	public void setTestGt(List<GroundTruthSelectionDescriptor> testGt) {
		this.testGt = testGt;
	}
	
	@Hidden
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
