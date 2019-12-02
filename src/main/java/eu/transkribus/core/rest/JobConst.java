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
	public static final String PROP_NEW_DOC_ID = "newDocId";
	public static final String PROP_DOC_DESCS = "docDescs"; // property for list of DocumentSelectionDescriptor's
	public static final String PROP_PAGE_NR = "pageNr";
	public static final String PROP_JOB_ID = "jobId";
	public static final String PROP_USER_ID = "userId";
	public static final String PROP_USER_NAME = "userName";
	public static final String PROP_SESSION_ID = "sessionId";
	public static final String PROP_COLLECTION_ID = "colId";
	public static final String PROP_TITLE = "title";
	public static final String PROP_TRANSCRIPT_ID = "transcriptId";
	public static final String PROP_REG_IDS = "regIds";
	public static final String PROP_TRANSCRIPTS = "transcripts";
	
	public static final String PROP_TRAIN_DESCS = "trainDescs"; // property for list of DocumentSelectionDescriptor's for training
	public static final String PROP_VAL_DESCS = "valDescs"; // property for list of DocumentSelectionDescriptor's for validation
	public static final String PROP_TEST_DESCS = "testDescs"; // property for list of DocumentSelectionDescriptor's for test
	
	public static final String PROP_TSIDS = "tsIds"; // property for transcription ids
//	public static final String PROP_TRAIN_TSIDS = "trainTsIds";
//	public static final String PROP_VAL_TSIDS = "valTsIds";
//	public static final String PROP_TEST_TSDIS = "testTsIds";
	
	public static final String PROP_STATE = "state";
	public static final String PROP_MODELNAME = "modelName";
	public static final String PROP_MODEL_ID = "modelId";
	public static final String PROP_DESCRIPTION = "description";
	public static final String PROP_PAGES = "pages";
	public static final String PROP_DOC_IDS = "docIds";
	public static final String PROP_PATH = "path";
	public static final String PROP_DEALOG_DOC_ID = "dealogDocId";
	public static final String PROP_METS_PATH = "metsPath";
	public static final String PROP_IIIF_PATH = "iiifPath";
	
	public static final String PROP_DICTNAME = "dictName";
	public static final String PROP_TEMP_DICTNAME = "tempDictName";
	public static final String PROP_NUM_EPOCHS = "numEpochs";
	public static final String PROP_LEARNING_RATE = "learningRate";
	public static final String PROP_NOISE = "noise";
	public static final String PROP_NR_OF_THREADS = "nrOfThreads";
	public static final String PROP_TRAIN_SIZE_PER_EPOCH = "trainSizePerEpoch";
	public static final String PROP_BASE_MODEL = "baseModel";
	public static final String PROP_EARLY_STOPPING = "earlyStopping";
	
	/**
	 * Use {@link #PROP_PARAMETERS} for consistency
	 */
	@Deprecated
	public static final String PROP_CONFIG = "config";
	
	public static final String PROP_DO_BLOCK_SEG = "doBlockSeg";
	public static final String PROP_DO_LINE_SEG = "doLineSeg";
	public static final String PROP_DO_WORD_SEG = "doWordSeg";
	public static final String PROP_DO_POLYGON_TO_BASELINE = "doPolygonToBaseline";
	public static final String PROP_DO_BASELINE_TO_POLYGON = "doBaselineToPolygon";
	public static final String PROP_STRUCTURES = "doStructures";
	
	public static final String PROP_REMOVE_LINE_BREAKS = "removeLineBreaks";
	public static final String PROP_PERFORM_LAYOUT_ANALYSIS = "performLayoutAnalysis";
	public static final String PROP_T2I_SKIP_WORD = "skip_word";
	public static final String PROP_T2I_SKIP_BASELINE = "skip_bl";
	public static final String PROP_T2I_JUMP_BASELINE = "jump_bl";
	
	public static final String PROP_KEEP_ORIGINAL_LINE_POLYGONS = "keepOriginalLinePolygons";
	public static final String PROP_DO_LINE_POLYGON_SIMPLIFICATION = "doLinePolygonSimplification";
	public static final String PROP_DO_STORE_CONFMATS = "doStoreConfMats";
	
	public static final String PROP_TABLE_TEMPLATE_ID = "templateId";

	public static final String PROP_SOLR_URL = "solrUrl";

	public static final String PROP_ADDITIONAL_COL_IDS = "additionalColIds";

	public static final String PROP_EXPORT_OPTIONS = "options";

	public static final String PROP_PARAMETERS = "parameters";
	
	//kws
	public static final String PROP_QUERY = "query";
	/**
	 * kws result is too big for the result-column. So this goes into  the job data clob
	 */
	public static final String PROP_RESULT = "result";
	public static final String PROP_THRESHOLD = "threshold";
	public static final String PROP_EDIT_STATUS = "editStatus";
	public static final String PROP_IS_CASE_SENSITIVE = "caseSensitive";
	public static final String PROP_IS_EXPERT = "expert";
	public static final String PROP_MAX_NR_OF_HITS = "maxNrOfHits";
	public static final String PROP_DO_PARTIAL_MATCHING = "partialMatching";
	public static final String PROP_CUSTOM_PROP_MAP = "customPropMap";
	
	public static final String PROP_KEY = "key";
	public static final String PROP_IMG_FILE = "imgKey";

	public static final String PROP_TITLE_PREFIX = "titlePrefix";

	public static final String PROP_AUTHORITY = "authority";
	public static final String PROP_HIERARCHY = "hierarchy";
	public static final String PROP_BACKLINK = "backlink";
	public static final String PROP_EXTID = "extId";
	public static final String PROP_FROMDATE = "fromDate";
	public static final String PROP_TODATE = "toDate";
	
	public static final String PROP_NUM_LINESAMPLES = "numLineSamples";

	public static final String PROP_GPU_DEVICE_ENV_VAR = "GPU_DEVICE";

	public static final String PROP_SKIP_PAGES_WITH_MISSING_STATUS = "skipPagesWithMissingStatus";
	
}
