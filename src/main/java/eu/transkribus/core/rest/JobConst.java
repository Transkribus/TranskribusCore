package eu.transkribus.core.rest;

public class JobConst {
	public static final String SEP = ",";
	
	public static final String JOBS_PACKAGE = "eu.transkribus.persistence.jobs.";
	
	public static final String STATE_CREATE = "create";
	public static final String STATE_HTR = "htr";
	public static final String STATE_FINISH = "finish";
	public static final String STATE_DOWNLOAD = "download";
	public static final String STATE_OCR = "ocr";
	public static final String STATE_INGEST = "ingest";
	
	public static final String PROP_DOC_ID = "docId";
	public static final String PROP_JOB_ID = "jobId";
	public static final String PROP_USER_ID = "userId";
	public static final String PROP_USER_NAME = "userName";
	public static final String PROP_SESSION_ID = "sessionId";
	public static final String PROP_COLLECTION_ID = "colId";
	public static final String PROP_TITLE = "title";
	
	public static final String PROP_STATE = "state";
	public static final String PROP_MODELNAME = "modelName";
	public static final String PROP_PAGES = "pages";
	public static final String PROP_DOC_IDS = "docIds";
	public static final String PROP_PATH = "path";
	public static final String PROP_DEALOG_DOC_ID = "dealogDocId";
	
	public static final String TYPE_HTR_TRAINING = "HTR Training";
	public static final String TYPE_HTR = "HTR";
	
}
