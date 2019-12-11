package eu.transkribus.core.model.beans;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.exceptions.ParsePropertiesException;
import eu.transkribus.core.io.util.TrpProperties;

@Entity
@Table(name = ATrpModel.TABLE_NAME)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class ATrpModel {
	private static final Logger logger = LoggerFactory.getLogger(ATrpModel.class);
	
	public static final String TABLE_NAME = "MODEL";
	
	public final static String MODEL_ID_COL = "model_id";
	public final static String NAME_COL = "name";
	public final static String TYPE_COL = "type";
	public final static String DESCRIPTION_COL = "description";
	public final static String PATH_COL = "path";
	public final static String CREATED_COL = "created";
	public final static String PARENT_ID_COL = "parent_id";
	public final static String IS_ACTIVE_COL = "is_active";
	public final static String RELEASE_LEVEL_COL = "release_level";
	public final static String PARAMS_COL = "params";
	public final static String CUSTOM_COL = "custom";
	public final static String DEL_TIME_COL = "del_time";
	public final static String JOBID_COL = "jobid";
	public final static String USERID_COL = "userid";
	public final static String USERNAME_COL = "username";
	public final static String MIN_ERROR_COL = "min_error";
	
	public final static String MODEL_ID_VARIABLE_NAME = "modelId";
	public final static String NAME_VARIABLE_NAME = "name";
	public final static String DESCRIPTION_VARIABLE_NAME = "description";
	
	public final static String NR_OF_TRAIN_GT_PAGES_COL = "NR_OF_TRAIN_GT_PAGES";	
	public final static String NR_OF_VALIDATION_GT_PAGES_COL = "NR_OF_VALIDATION_GT_PAGES";	
	public final static String COLLECTION_ID_LINK_COL = "COLLECTION_ID_LINK";
	
	public final static String[] COLS = { MODEL_ID_COL, NAME_COL, TYPE_COL, DESCRIPTION_COL,
			PATH_COL, CREATED_COL, PARENT_ID_COL, IS_ACTIVE_COL, RELEASE_LEVEL_COL,
			PARAMS_COL, CUSTOM_COL, DEL_TIME_COL, JOBID_COL, USERID_COL, USERNAME_COL, MIN_ERROR_COL,
			NR_OF_TRAIN_GT_PAGES_COL, NR_OF_VALIDATION_GT_PAGES_COL, COLLECTION_ID_LINK_COL };
	
	public final static String[] EDITABLE_VARIABLES = { NAME_VARIABLE_NAME, DESCRIPTION_VARIABLE_NAME };

	@Id @Column(name = MODEL_ID_COL) protected Integer modelId;
	@Column(name = NAME_COL) protected String name;
	@Column(name = TYPE_COL) protected String type;
	@Column(name = DESCRIPTION_COL) protected String description;
	@Column(name = PATH_COL) protected String path;
	@Column(name = CREATED_COL) protected Timestamp created;
	@Column(name = PARENT_ID_COL) protected Integer parentId;
	@Column(name = IS_ACTIVE_COL) protected Integer isActive;
	@Column(name = RELEASE_LEVEL_COL) protected Integer releaseLevel;
	@Column(name = PARAMS_COL) protected String params;
	@Column(name = CUSTOM_COL) protected String custom;
	@Column(name = DEL_TIME_COL) protected Timestamp delTime;
	@Column(name = JOBID_COL) protected Integer jobId;
	@Column(name = USERID_COL) protected Integer userId;
	@Column(name = USERNAME_COL) protected String userName;
	@Column(name = MIN_ERROR_COL) protected Double minError;
	@Transient @Column(name = NR_OF_TRAIN_GT_PAGES_COL) protected Integer nrOfTrainGtPages;
	@Transient @Column(name = NR_OF_VALIDATION_GT_PAGES_COL) protected Integer nrOfValidationGtPages;
	@Transient @Column(name = COLLECTION_ID_LINK_COL) protected Integer collectionIdLink;		
	
	public ATrpModel() {
		this.type = getModelType();
	}
	
	/**
	 * @deprecated This constructor's argument list does not reflect all available fields anymore! 
	 * Use {@link #ATrpModel(Integer, String, String, String, Timestamp, Integer, Integer, Integer, String, String, Timestamp, Integer, Integer, String, Double, Integer, Integer, Integer)}
	 */
	public ATrpModel(Integer modelId, String name, /*String type,*/ String description, String path, Timestamp created,
			Integer parent_id, Integer isActive, Integer releaseLevel,
			String params, String custom, Timestamp delTime, Integer jobId, Integer userId, String userName, Double minError) {
		this(modelId, name, description, path, created, parent_id, isActive, releaseLevel, params, custom, delTime, jobId, 
				userId, userName, minError, null, null, null);
	}
	
	public ATrpModel(Integer modelId, String name, String description, String path, Timestamp created,
			Integer parent_id, Integer isActive, Integer releaseLevel,
			String params, String custom, Timestamp delTime, Integer jobId, Integer userId, String userName, Double minError, 
			Integer nrOfTrainGtPages, Integer nrOfValidationGtPages, Integer collectionIdLink) {
		this();
		this.modelId = modelId;
		this.name = name;
		this.type = getType();
		this.description = description;
		this.path = path;
		this.created = created;
		this.parentId = parent_id;
		this.isActive = isActive;
		this.releaseLevel = releaseLevel;
		this.params = params;
		this.custom = custom;
		this.delTime = delTime;
		this.jobId = jobId;
		this.userId = userId;
		this.userName = userName;
		this.minError = minError;
		this.nrOfTrainGtPages = nrOfTrainGtPages;
		this.nrOfValidationGtPages = nrOfValidationGtPages;
		this.collectionIdLink = collectionIdLink;
	}
	
	public ATrpModel(ATrpModel otherModel) {
		this(otherModel.modelId, otherModel.name, otherModel.description, otherModel.path, otherModel.created, otherModel.parentId, 
				otherModel.isActive, otherModel.releaseLevel, otherModel.params, otherModel.custom, otherModel.delTime, otherModel.jobId, 
				otherModel.userId, otherModel.userName, otherModel.minError, otherModel.nrOfTrainGtPages, otherModel.nrOfValidationGtPages,
				otherModel.collectionIdLink);
	}

	public Integer getModelId() {
		return modelId;
	}

	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public Integer getReleaseLevel() {
		return releaseLevel;
	}

	public void setReleaseLevel(Integer releaseLevel) {
		this.releaseLevel = releaseLevel;
	}
	
	public ReleaseLevel getReleaseLevelInternal() {
		return ReleaseLevel.fromValue(releaseLevel);
	}

	public void setReleaseLevel(ReleaseLevel level) {
		this.releaseLevel = level.getValue();
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public Timestamp getDelTime() {
		return delTime;
	}

	public void setDelTime(Timestamp delTime) {
		this.delTime = delTime;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Double getMinError() {
		return minError;
	}

	public void setMinError(Double minError) {
		this.minError = minError;
	}
	
	public Integer getCollectionIdLink() {
		return collectionIdLink;
	}

	public void setCollectionIdLink(Integer collectionIdLink) {
		this.collectionIdLink = collectionIdLink;
	}

	public Integer getNrOfTrainGtPages() {
		return nrOfTrainGtPages;
	}
	
	public void setNrOfTrainGtPages(Integer nrOfTrainGtPages) {
		this.nrOfTrainGtPages = nrOfTrainGtPages;
	}
	
	public Integer getNrOfValidationGtPages() {
		return nrOfValidationGtPages;
	}
	
	public void setNrOfValidationGtPages(Integer nrOfValidationGtPages) {
		this.nrOfValidationGtPages = nrOfValidationGtPages;
	}
	
	public boolean isDeleted() {
		return this.delTime != null;
	}
	
	public boolean hasTrainGt() {
		return getNrOfTrainGtPages() != null && getNrOfTrainGtPages() > 0;
	}
	
	public boolean hasValidationGt() {
		return getNrOfValidationGtPages() != null && getNrOfValidationGtPages() > 0;
	}
	
	protected abstract String getModelType();
	
	/**
	 * Reads custom field value into a TrpProperties object. Override this method if your model type uses another format.
	 * 
	 * @return TrpProperties containing the key value pairs stored in the custom field
	 */
	public TrpProperties parseCustomProperties() {
		if (!StringUtils.isEmpty(this.custom)) {
			try {
				return new TrpProperties(custom, false);
			} catch (ParsePropertiesException e) {
				logger.warn("Could not parse non-empty custom properties from TrpHtrModel: " + custom, e);
				return new TrpProperties();
			}
		} else {
			return new TrpProperties();
		}
	}
	
}
