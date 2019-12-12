package eu.transkribus.core.model.beans;

import java.lang.reflect.Field;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = TrpP2PaLA.TABLE_NAME)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@XmlType(name="") // needed to suppress Moxy's default 'type' attribute which collides with type attribute of the ATrpModel class
public class TrpP2PaLA extends ATrpModel {
	private static final Logger logger = LoggerFactory.getLogger(TrpP2PaLA.class);
	
	public static final String TABLE_NAME = "P2PALA_MODEL_COLS"; 
	public static final String TYPE = "P2PaLA";
	
	public static final String STRUCT_TYPES_COL = "struct_types";
	public static final String MERGED_STRUCT_TYPES_COL = "merged_struct_types";
	public static final String OUT_MODE_COL = "out_mode";
	
	public final static String[] P2PALA_COLS = new String[] { STRUCT_TYPES_COL, MERGED_STRUCT_TYPES_COL, OUT_MODE_COL };
	
	public static final String OUT_MODE_LINES_ONLY = "L";
	public static final String OUT_MODE_REGIONS_ONLY = "R";
	public static final String OUT_MODE_LINES_AND_REGIONS = "LR";		
	
	@Column(name=STRUCT_TYPES_COL) private String structTypes;
	@Column(name=MERGED_STRUCT_TYPES_COL) private String mergedStructTypes;
	@Column(name=OUT_MODE_COL) private String outMode;
	
	public TrpP2PaLA() {
		super();
	}
	
	public TrpP2PaLA(Integer modelId, String name, String description, String path, Timestamp created,
			Integer parenId, Integer isActive, Integer releaseLevel, String params, String custom, Timestamp delTime, Integer jobId, Integer userId, String userName, Double minError,
			
			String structTypes, String mergedStructTypes, String outMode) {
		super(modelId, name, description, path, created, parenId, isActive, releaseLevel, params, custom, delTime, jobId, userId, userName, minError);
		
		this.structTypes = structTypes;
		this.mergedStructTypes = mergedStructTypes;
		this.outMode = outMode;
	}

	public TrpP2PaLA(TrpP2PaLA otherModel) {
		super(otherModel);
		this.structTypes = otherModel.structTypes;
		this.mergedStructTypes = otherModel.mergedStructTypes;
		this.outMode = otherModel.outMode;
	}
	
	public String getStructTypes() {
		return structTypes;
	}

	public void setStructTypes(String structTypes) {
		this.structTypes = structTypes;
	}

	public String getMergedStructTypes() {
		return mergedStructTypes;
	}

	public void setMergedStructTypes(String mergedStructTypes) {
		this.mergedStructTypes = mergedStructTypes;
	}

	public String getOutMode() {
		return outMode;
	}

	public void setOutMode(String outMode) {
		this.outMode = outMode;
	}

	@Override
	public String toString() {
		return "TrpP2PaLA [modelId="+modelId+", name=" + name + ", structTypes=" + structTypes + ", mergedStructTypes=" + mergedStructTypes + ", outMode="
				+ outMode + ", type=" + type + ", description="
				+ description + ", path=" + path + ", created=" + created + ", parentId=" + parentId + ", isActive="
				+ isActive + ", releaseLevel=" + releaseLevel + ", params=" + params + ", custom=" + custom
				+ ", delTime=" + delTime + ", jobId=" + jobId + ", userId=" + userId + ", userName=" + userName+", minError=" + minError
				+ "]";
	}

	@Override
	protected String getModelType() {
		return TYPE;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((mergedStructTypes == null) ? 0 : mergedStructTypes.hashCode());
		result = prime * result + ((outMode == null) ? 0 : outMode.hashCode());
		result = prime * result + ((structTypes == null) ? 0 : structTypes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrpP2PaLA other = (TrpP2PaLA) obj;
		if (mergedStructTypes == null) {
			if (other.mergedStructTypes != null)
				return false;
		} else if (!mergedStructTypes.equals(other.mergedStructTypes))
			return false;
		if (outMode == null) {
			if (other.outMode != null)
				return false;
		} else if (!outMode.equals(other.outMode))
			return false;
		if (structTypes == null) {
			if (other.structTypes != null)
				return false;
		} else if (!structTypes.equals(other.structTypes))
			return false;
		return true;
	}

	public static void main(String[] args) throws Exception {
		
		for (Field f : TrpP2PaLA.class.getDeclaredFields()) {
			logger.info("f = "+f.getName());
		}
		
		
	}
}
