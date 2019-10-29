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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = TrpP2PaLA.TABLE_NAME)
@XmlAccessorType(XmlAccessType.FIELD)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
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
			Integer parenId, Integer isActive, Integer releaseLevel, String params, String custom, Timestamp delTime, Integer jobId, Integer userId, String userName,
			
			String structTypes, String mergedStructTypes, String outMode) {
		super(modelId, name, description, path, created, parenId, isActive, releaseLevel, params, custom, delTime, jobId, userId, userName);
		
		this.structTypes = structTypes;
		this.mergedStructTypes = mergedStructTypes;
		this.outMode = outMode;
	}
	
	public String getType() {
		return TYPE;
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
				+ ", delTime=" + delTime + ", jobId=" + jobId + ", userId=" + userId + ", userName=" + userName
				+ "]";
	}
	
	public static void main(String[] args) throws Exception {
		
		for (Field f : TrpP2PaLA.class.getDeclaredFields()) {
			logger.info("f = "+f.getName());
		}
		
		
	}
	
}
