package eu.transkribus.core.model.beans.job;

import java.io.IOException;
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

import eu.transkribus.core.model.beans.job.enums.JobImpl;
import eu.transkribus.core.model.beans.job.enums.JobType;
import eu.transkribus.core.util.CoreUtils;

//public class TrpJob extends ATrpJob {
//	private static final long serialVersionUID = -2312854543251222064L;
//	
//	public TrpJob(){
//		super();
//	}
//	
//	public TrpJob(final String jobId, final int docId, final String userId, final long startTime) {
//		super(jobId, docId, userId, startTime);
//	}
//	
//}

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
	
	@Column
	private String jobData;
	
	@Column
	private boolean resumable = false;
	
	@Column 
	private String className;
	
	//isPersistent specifies whether the job is to be stored in the DB. 
	//This is always true if not stated otherwise in constructor call.
//	private boolean isPersistent;
	
	@XmlTransient
	@Column
	private Integer session_history_id;
	
	@Column(name="JOB_IMPL")
	private JobImpl jobImpl;

//	private Future<?> future = null;

	/**
	 * Empty, public constructor for Jaxb and DbUtils
	 */
	public TrpJobStatus() {
//		this.isPersistent = true;
	}

//	public TrpJobStatus(String jobId, int docId, int userId, String userName, String type) {
//		this(jobId, docId, -1, userId, userName, type);
//	}
	
//	public TrpJobStatus(String jobId, int docId, int pageNr, int userId, String userName, String type) {
//		this(jobId, docId, pageNr, userId, userName, type, null, null);
//	}
	
	public TrpJobStatus(String jobId, int docId, String pages, int userId, String userName, String type, Integer session_history_id, JobImpl impl) {
		this(jobId, docId, pages, userId, userName, type, null, session_history_id, impl);
	}
	
	public TrpJobStatus(String jobId, int docId, String pages, int userId, String userName, String type, String jobData, Integer session_history_id, JobImpl impl) {
		this.jobId = jobId;
		this.docId = docId;
		this.userId = userId;
		this.userName = userName;
		this.type = type;
		this.pages = pages;
//		this.isPersistent = isPersistent;
		this.createTime = System.currentTimeMillis();
		this.jobData = jobData;
		this.session_history_id = session_history_id;
		this.jobImpl = impl;
	}
	
	public void copy(TrpJobStatus other) {
	    this.jobId = other.jobId;
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
//	    this.future = trpJobStatus.future;
	}
	
//	public boolean hasState(String state) {
//		if (state == null)
//			return true;
//		
//		if (state.equals(UNFINISHED))
//			return !isFinished();
//		
//		return this.state.equals(state);
//	}

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
	
	public Properties getJobDataProps(){
		try {
			return CoreUtils.readPropertiesFromString(jobData);
		} catch (IOException e) {
			return new Properties();
		}
	}
	
	public void setJobData(String jobData) {
		this.jobData = jobData;
	}
	
	public void setJobData(Properties jobData) {
		this.jobData = CoreUtils.writePropertiesToString(jobData);
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

	public boolean isFinished() {
		return state.equals(FINISHED) || state.equals(FAILED) || state.equals(CANCELED);
	}

	public Integer getSession_history_id() {
		return session_history_id;
	}

	public void setSession_history_id(Integer session_history_id) {
		this.session_history_id = session_history_id;
	}

	public JobImpl getJobImpl(){
		return this.jobImpl;
	}
	
	public void setJobImpl(JobImpl jobImpl){
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
	
	@Override
	public String toString() {
		return "TrpJobStatus [jobId=" + jobId + ", docId=" + docId + ", pageNr=" + pageNr + ", pages=" + pages
				+ ", type=" + type + ", state=" + state + ", success=" + success + ", description=" + description
				+ ", userName=" + userName + ", userId=" + userId + ", createTime=" + createTime + ", startTime="
				+ startTime + ", endTime=" + endTime + ", jobData=" + jobData + ", resumable=" + resumable
				+ ", className=" + className + ", session_history_id=" + session_history_id + ", jobImpl=" + jobImpl
				+ "]";
	}
}