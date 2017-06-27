package eu.transkribus.core.model.beans.job;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.util.TrpProperties;
import eu.transkribus.core.util.CoreUtils;

@Entity
@Table(name = "JOBS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpJobStatus implements Serializable {
	private final static Logger logger = LoggerFactory.getLogger(TrpJobStatus.class);
	
	private static final long serialVersionUID = -2312854543251222064L;
	
	static DateFormat timeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	//TODO how to handle states and types?
	public static final String CREATED = "CREATED";
	public static final String FAILED = "FAILED";
	public static final String FINISHED = "FINISHED";
	public static final String WAITING = "WAITING";
	public static final String RUNNING = "RUNNING";
	public static final String CANCELED = "CANCELED";
	public static final String UNFINISHED = "UNFINISHED"; // meta-status -> all but FINISHED

	@Id
	@Column
	private String jobId = "-1";

	@Column
	private int docId;
	
	@Column
	@Deprecated
	private int pageNr = -1; 
	
	@Column
	private String pages;
	
	@Column
	private String type;

	@Column
	private String state = CREATED;

	@Column
	private boolean success = false;

	@Column
	private String description = "";

	@Column(name="userId")
	private String userName;
	@Column(name="user_id")
	private int userId;
	
	@Column
	private long createTime;

	@Column
	private long startTime;

	@Column
	private long endTime;
	
	@Column(name="JOBDATA_CLOB")
	private String jobData;
	
	@Column
	private boolean resumable = false;
	
	@Column 
	private String className;
	
	@Column
	private String result;
	
	@XmlTransient
	@Column
	private Integer session_history_id;
	
//	@Column(name="JOB_IMPL")
//	private JobImpl jobImpl;
	
	@Column(name="JOB_IMPL")
	private String jobImpl;	
	
	// NEW cols
//	@XmlTransient
	@Column(name="MODULE_URL")
	private String moduleUrl;
	
//	@XmlTransient
	@Column(name="MODULE_NAME")
	private String moduleName;
	
//	@XmlTransient
	@Column(name="MODULE_VERSION")
	private String moduleVersion;
	
	@Column(name="STARTED")
	private java.util.Date started;
	@Column(name="ENDED")
	private java.util.Date ended;
	@Column(name="CREATED")
	private java.util.Date created;
	
//	@XmlTransient
	@Column(name="PID")
	private String pid;
	
	@Column
	private Integer batchId;
	
	@Column
	private Integer pageid;
	
	@Column
	private Integer tsid;
	
	@Column
	private String regionids;
	
	@Column
	private Integer parent_jobid;
	
	@Column
	private Integer parent_batchid;
	
//	@XmlTransient
	@Column(name="STACKTRACE")
	private String stackTrace;
	
	@Column
	private Integer colId;
	
	@Column
	private Integer progress;
	
	@Column(name="TOTAL_WORK")
	private Integer totalWork;

	/**
	 * Empty, public constructor for Jaxb and DbUtils
	 */
	public TrpJobStatus() {
	}

//	public TrpJobStatus(String jobId, int docId, String pages, int userId, String userName, String type, String jobData, Integer session_history_id, JobImpl impl) {
//		this(jobId, docId, pages, userId, userName, type, jobData, session_history_id, impl==null ? null : impl.toString());
//	}
	
	public TrpJobStatus(String jobId, Integer batchId, Integer docId, Integer pageid, Integer tsid, String regionids, int userId, String userName, String type, String jobData, Integer session_history_id, String jobImpl) {
		this.jobId = jobId;
		this.batchId = batchId;
		
		this.docId = docId;
		this.pageid = pageid;
		this.tsid = tsid;
		this.regionids = regionids;
				
		this.userId = userId;
		this.userName = userName;
		this.type = type;
		
		this.jobData = jobData;
		this.session_history_id = session_history_id;
		this.jobImpl = jobImpl;
		
		setCreatedNow();
	}
	
	public TrpJobStatus(String jobId, Integer batchId, int docId, String pages, int userId, String userName, String type, String jobData, Integer session_history_id, String jobImpl) {
		this.jobId = jobId;
		this.batchId = batchId;
		this.docId = docId;
		this.userId = userId;
		this.userName = userName;
		this.type = type;
		this.pages = pages;
		this.jobData = jobData;
		this.session_history_id = session_history_id;
		this.jobImpl = jobImpl;
		
		setCreatedNow();
	}
	
	public void copy(TrpJobStatus other) {
	    this.jobId = other.jobId;
	    this.colId = other.colId;
	    this.docId = other.docId;
	    this.pages = other.pages;
	    this.type = other.type;
	    this.state = other.state;
	    this.success = other.success;
	    this.description = other.description;
	    this.userName = other.userName;
	    this.userId = other.userId;
	    this.createTime = other.createTime;
	    this.startTime = other.startTime;
	    this.endTime = other.endTime;
	    this.jobData = other.jobData;
	    this.resumable = other.resumable;
	    this.className = other.className;
	    this.session_history_id = other.session_history_id;
	    this.jobImpl = other.jobImpl;
	    this.result = other.result;
	    
	    this.moduleName = other.moduleName;
	    this.moduleUrl = other.moduleUrl;
	    this.moduleVersion = other.moduleVersion;
	    
	    this.started = other.started;
	    this.ended = other.ended;
	    this.created = other.created;
	    
	    this.pid = other.pid;
	    this.batchId = other.batchId;
	    this.pageid = other.pageid;
	    this.tsid = other.tsid;
	    this.regionids = other.regionids;
	    this.parent_jobid = other.parent_jobid;
	    this.parent_batchid = other.parent_batchid;
	    this.stackTrace = other.stackTrace;
	}
	
	public int getJobIdAsInt() {
		try {
			return Integer.parseInt(jobId);
		} catch (Exception e) {
			return -1;
		}
	}
	
	public Integer getColId() {
		return colId;
	}

	public void setColId(Integer colId) {
		this.colId = colId;
	}

	public String getJobId() {
		return jobId;
	}
	
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}
	
	public int getPageNr() {
		return pageNr;
	}

	public void setPageNr(int pageNr) {
		this.pageNr = pageNr;
	}

	public String getPages() {
		return pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public long getStartTime() {
		return startTime;
	}
	
	public String getStartTimeFormatted() {
		return getStartTime() > 0 ? timeFormatter.format(new Date(getStartTime())) : "";
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public String getCreateTimeFormatted() {
		return getCreateTime() > 0 ? timeFormatter.format(new Date(getCreateTime())) : "";
	}
	
	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getEndTime() {
		return endTime;
	}
	
	public String getEndTimeFormatted() {
		return getEndTime() > 0 ? timeFormatter.format(new Date(getEndTime())) : "";
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	public String getJobData() {
		return jobData;
	}
	
	public TrpProperties getJobDataProps() {
		try {
//			return CoreUtils.readPropertiesFromString(jobData);
			return new TrpProperties(jobData, false);
		} catch (Exception e) {
			return new TrpProperties();
		}
	}
	
	public void setJobData(String jobData) {
		this.jobData = jobData;
	}
	
	public void setJobData(Properties jobData) {
		this.jobData = CoreUtils.propertiesToString(jobData);
	}

	public boolean isResumable() {
		return resumable;
	}

	public void setResumable(boolean isResumable) {
		this.resumable = isResumable;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public boolean isFinished() {
		return state.equals(FINISHED) || state.equals(FAILED) || state.equals(CANCELED);
	}

	public Integer getSession_history_id() {
		return session_history_id;
	}

	public void setSession_history_id(Integer session_history_id) {
		this.session_history_id = session_history_id;
	}

	public String getJobImpl(){
		return this.jobImpl;
	}
	
	public void setJobImpl(String jobImpl){
		this.jobImpl = jobImpl;
	}
	
	public boolean isActiveOnPage(int pageNr) {
		if (this.pageNr == -1) {
			if (jobData == null)
				return true;
			
			try {	
				Properties props = CoreUtils.readPropertiesFromString(jobData);
				String pagesStr = props.getProperty("pages");
				logger.debug("pagesStr = "+pagesStr);
				if (pagesStr != null) {
					return CoreUtils.parseRangeListStr(pagesStr, 10000).contains(pageNr);
				} else
					return true;
			} catch (Exception e) {
				logger.warn("Could not parse jobData: "+e.getMessage());
				return false;
			}
		}
		else
			return this.pageNr == pageNr;
	}
	
	public boolean isCancelled() {
		return this.getState().equals(TrpJobStatus.CANCELED);
	}
	
	public boolean isRunning() {
		return this.getState().equals(TrpJobStatus.RUNNING);
	}
	
	public boolean isFailed() {
		return this.getState().equals(TrpJobStatus.FAILED);
	}

	public String getModuleUrl() {
		return moduleUrl;
	}

	public void setModuleUrl(String moduleUrl) {
		this.moduleUrl = moduleUrl;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleVersion() {
		return moduleVersion;
	}

	public void setModuleVersion(String moduleVersion) {
		this.moduleVersion = moduleVersion;
	}

//	public java.sql.Timestamp getStarted() {
//		return started;
//	}
//
//	public void setStarted(java.sql.Timestamp started) {
//		this.started = started;
//	}
//
//	public java.sql.Timestamp getEnded() {
//		return ended;
//	}
//	
//	public void setEnded(java.sql.Timestamp ended) {
//		this.ended = ended;
//	}
//
//	public java.sql.Timestamp getCreated() {
//		return created;
//	}
//
//	public void setCreated(java.sql.Timestamp created) {
//		this.created = created;
//	}
		
	public java.util.Date getStarted() {
		return started;
	}

	public void setStarted(java.util.Date started) {
		this.started = started;
	}

	public java.util.Date getEnded() {
		return ended;
	}

	public void setEnded(java.util.Date ended) {
		this.ended = ended;
	}

	public java.util.Date getCreated() {
		return created;
	}

	public void setCreated(java.util.Date created) {
		this.created = created;
	}
	
	public void setEndedNow() {
		this.endTime = System.currentTimeMillis();
		this.ended = new Date(endTime);
	}

	public void setStartedNow() {
		this.startTime = System.currentTimeMillis();
		this.started = new Date(startTime);
	}	
	
	public void setCreatedNow() {
		this.createTime = System.currentTimeMillis();
		this.created = new Date(this.createTime);
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}

	public Integer getPageid() {
		return pageid;
	}

	public void setPageid(Integer pageid) {
		this.pageid = pageid;
	}

	public Integer getTsid() {
		return tsid;
	}

	public void setTsid(Integer tsid) {
		this.tsid = tsid;
	}

	public String getRegionids() {
		return regionids;
	}

	public void setRegionids(String regionids) {
		this.regionids = regionids;
	}

	public Integer getParent_jobid() {
		return parent_jobid;
	}

	public void setParent_jobid(Integer parent_jobid) {
		this.parent_jobid = parent_jobid;
	}

	public Integer getParent_batchid() {
		return parent_batchid;
	}

	public void setParent_batchid(Integer parent_batchid) {
		this.parent_batchid = parent_batchid;
	}
	
	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public Integer getProgress() {
		return progress;
	}

	public void setProgress(Integer progress) {
		this.progress = progress;
	}

	public Integer getTotalWork() {
		return totalWork;
	}

	public void setTotalWork(Integer totalWork) {
		this.totalWork = totalWork;
	}

	@Override
	public String toString() {
		return "TrpJobStatus [jobId=" + jobId + ", colId=" + colId+", docId=" + docId + ", pageNr=" + pageNr + ", pages=" + pages
				+ ", type=" + type + ", state=" + state + ", success=" + success + ", description=" + description
				+ ", userName=" + userName + ", userId=" + userId + ", createTime=" + createTime + ", startTime="
				+ startTime + ", endTime=" + endTime + ", jobData=" + jobData + ", resumable=" + resumable
				+ ", className=" + className + ", result=" + result + ", session_history_id=" + session_history_id
				+ ", jobImpl=" + jobImpl + ", moduleUrl=" + moduleUrl + ", moduleName=" + moduleName
				+ ", moduleVersion=" + moduleVersion + ", started=" + started + ", ended=" + ended + ", created="
				+ created + ", pid=" + pid + ", batchId=" + batchId + ", pageid=" + pageid + ", tsid=" + tsid
				+ ", regionids=" + regionids + ", parent_jobid=" + parent_jobid + ", parent_batchid=" + parent_batchid
				+ ", stackTrace="+stackTrace+", progress="+progress+", totalWork="+totalWork+"]";
	}

	

}