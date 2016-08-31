package eu.transkribus.core.rest;

public final class RESTConst {
	
	//Header keys
	public static final String GUI_VERSION_HEADER_KEY = "trpGuiVersion";
	public static final String CLIENT_ID_HEADER_KEY = "clientId";
	
	//base path is set as ApplicationPath either in TrpServer's web.xml or TrpServerApp.java
	public static final String BASE_PATH = "rest";
	
	/* Resource Paths */
	/* FIRST TIER: Resources */
	public static final String DOC_PATH = "docs";
	public static final String SYSTEM_PATH = "system";
	public static final String AUTH_PATH = "auth";
	public static final String LAYOUT_PATH = "LA";
	public static final String JOBS_PATH = "jobs";
	public static final String USER_PATH = "user";
	public static final String COLLECTION_PATH = "collections";
	public static final String STRUCTURE_PATH = "structure";
	public static final String FILES_PATH = "files";
	public static final String ADMIN_PATH = "admin";
	public static final String SEARCH_PATH = "search";
	public static final String ACTIONS_PATH = "actions";
	
	/* SECOND TIER: Methods */
	public static final String DB_PATH = "db";
	public static final String CURR_MD_PATH = "curr";
	public static final String MD_PATH = "list";
	public static final String COUNT_PATH = "count";
	
	public static final String LOGIN_PATH = "login";
	public static final String LOGOUT_PATH = "logout";
	public static final String ALLOW_PATH = "allow";
	public static final String DISALLOW_PATH = "disallow";
	public static final String REFRESH_PATH = "refresh";
	public static final String LIST_PATH = "list";
	public static final String LIST_BY_NAME_PATH = "listByName";
	public static final String LIST_MY_DOCS_PATH = "listMyDocs";
	public static final String COUNT_MY_DOCS_PATH = "countMyDocs";
	public static final String TRANSCRIPT_PATH = "text";
	public static final String FULLDOC_PATH = "fulldoc";
	public static final String METS_PATH = "mets";
	public static final String UPLOAD_PATH = "upload";
	public static final String UPLOAD_PATH_MULTIPART = "uploadMultipart";
	public static final String UPLOAD_PATH_FTP = "uploadFromFtp";
	public static final String DELETE_PATH = "delete";
	public static final String ANALYZE_LAYOUT_BATCH_PATH = "batch";
	public static final String ANALYZE_LAYOUT_PATH = "blocks";
	public static final String ANALYZE_LINES_PATH = "lines";
	public static final String ANALYZE_WORDS_PATH = "words";
	public static final String ANALYZE_BASELINES_PATH = "baselines";
	public static final String WORDGRAPHS_PATH = "wordgraphs";
	public static final String LIST_USERS_PATH = "userlist";
	public static final String LIST_LOGINS_PATH = "loginlist";
	public static final String REGISTER_PATH = "register";
	public static final String BUG_REPORT_PATH = "bugReport";
	public static final String SERVER_VERSION_INFO_PATH = "serverVersion";
	public static final String ANALYZE_STRUCTURE_PATH = "analyze";
	public static final String EVENTS_PATH = "events";
	public static final String DUPLICATE_PATH = "duplicate";
	
	public static final String DETECT_PAGE_NUMBERS_PARAM = "pageNumbers";
	public static final String DETECT_RUNNING_TITLES_PARAM = "runningTitles";
	public static final String DETECT_FOOTNOTES_PARAM = "footnotes";

	public static final String RECOGNITION_PATH = "recognition";
	public static final String OCR_PATH = "ocr";	
	
	public static final String ADD_OR_MODIFY_USER_IN_COLLECTION = "addOrModifyUserInCollection";
	public static final String REMOVE_USER_FROM_COLLECTION = "removeUserFromCollection";
	public static final String CREATE_COLLECTION_PATH = "createCollection";
	public static final String MODIFY_COLLECTION_PATH = "modifyCollection";
	public static final String DELETE_EMPTY_COLLECTION = "deleteEmptyCollection";
	
	public static final String ADD_DOC_TO_COLLECTION = "addDocToCollection";
	public static final String REMOVE_DOC_FROM_COLLECTION = "removeDocFromCollection";
	
	public static final String CLIENT_VERSION_INFO_PATH = "clientVersion";
	public static final String DOWNLOAD_LATEST_CLIENT = "downloadLatestGui";
	public static final String CLIENT_AVAILABLE_FILES = "availableClientFiles";
	public static final String CLIENT_AVAILABLE_FILES_ASYNC = "availableClientFilesAsync";
	public static final String DOWNLOAD_CLIENT_FILE = "downloadClientFile";
	public static final String DOWNLOAD_CLIENT_FILE_NEW = "downloadClientFileNew";
	public static final String INGEST_PATH = "ingest";
	public static final String INFO_PATH = "info";
	
	/* Parameters */
	public static final String DOC_ID_PARAM = "id";
	public static final String PAGE_NR_PARAM = "page";
	public static final String TRANSCRIPT_ID_PARAM = "transcriptId";
	public static final String PAGES_PARAM = "pages";
	public static final String NR_OF_TRANSCRIPTS_PARAM = "nrOfTranscripts";
	public static final String STATUS_PARAM = "status";
	public static final String USER_PARAM = "user";
	public static final String USER_ID_PARAM = "userid";
	public static final String ROLE_PARAM = "role";
	public static final String PW_PARAM = "pw";
	public static final String OVERWRITE_PARAM = "overwrite";	
	public static final String USE_PRINTSPACE_PARAM = "usePs";
	public static final String ID_PARAM = "id";
	public static final String EMAIL_PARAM = "email";
	public static final String KEY_PARAM = "key";
	public static final String REF_KEY_PARAM = "ref";
	public static final String TEI_TAGS_PARAM = "teiTags";
	
	public static final String TITLE_PARAM = "title";
	public static final String DESCRIPTION_PARAM = "descr";
	public static final String TEXT_PARAM = "text";
	public static final String AUTHOR_PARAM = "author";
	public static final String WRITER_PARAM = "writer";
	
	public static final String HAS_AFFILIATION = "hasAffiliation";
	public static final String AFFILIATION_NAME = "affName";
	public static final String AFFILIATION_ACRONYM = "affAcr";
	public static final String TECH_EMAIL_PARAM = "techMail";
	public static final String REPORT_EMAIL_PARAM = "reportMail";
	public static final String PHONE = "phone";
	public static final String ADDRESS = "address";	
	
	public static final String SUBJECT_PARAM = "subject";
	public static final String MESSAGE_PARAM = "message";
	public static final String IS_BUG_PARAM = "isBug";
	public static final String LOG_FILE_PARAM = "logFile";
	
	public static final String IS_RELEASE_PARAM = "isRelease";
	public static final String FILE_NAME_PARAM = "fileName";
	public static final String LIBS_MAP_PARAM = "libsMap";
	
	//Layout analysis
	public static final String REG_ID_PARAM = "regId";
	public static final String DO_BLOCK_SEG_PARAM = "doBlockSeg";
	public static final String DO_LINE_SEG_PARAM = "doLineSeg";
	@Deprecated
	public static final String IMG_KEY_PARAM = "imgKey";

	public static final String INVALIDATE_PATH = "invalidate";

	public static final String FILTER_BY_USER_PARAM = "filterByUser";

	public static final String TIMESTAMP = "time";

	public static final String TEST_LOCK_PATH = "isLocked";
	public static final String LOCK_PATH = "lock";
	public static final String UNLOCK_PATH = "unlock";
	public static final String LIST_LOCKS_PATH = "listLocks";	

	public static final String PAGING_INDEX_PARAM = "index";
	public static final String PAGING_NVALUES_PARAM = "nValues";

	public static final String KILL_JOB_PATH = "kill";

	public static final String TOKEN_PARAM = "token";
	public static final String APPLICATION_PARAM = "application";
//	public static final String HASH = "hash";
	public static final String ACTIVATE_USER_PATH = "activateUser";
	public static final String LIST_AFFILIATectionIONS = "affList";
	public static final String IS_USER_AVAILABLE_PATH = "isUserAvailable";
//	public static final String IS_EMAIL_AVAILABLE = "isEmailAvailable";
	public static final String IS_EMAIL_BLACKLISTED = "isEmailBlacklisted";

	public static final String FIRST_NAME_PARAM = "firstName";
	public static final String LAST_NAME_PARAM = "lastName";
	public static final String GENDER_PARAM = "gender";
	public static final String ORCID_PARAM = "orcid";
	
	public static final String RESET_PW_PATH = "resetPw";
	public static final String FORGOT_PW_PATH = "forgotPw";
	public static final String MODIFY_USER_PATH = "modify";

	@Deprecated
	public static final String COLLECTION_PARAM = "collection";	
	
	public static final String COLLECTION_ID_PARAM = "collId";
	public static final String COLLECTION_NAME_PARAM = "collName";
	
	public static final String ONLY_ACTIVE_PARAM = "onlyActive";

	public static final String FIND_USER_PATH = "findUser";

	public static final String EXACT_MATCH_PARAM = "exactMatch";
	public static final String CASE_SENSITIVE_PARAM = "caseSensitive";

	public static final String RECENT_PATH = "recent";

	public static final String CAN_MANAGE_PATH = "canManage";

	public static final String REPLACE_PAGE_PATH = "replacePage";

	public static final String URL_PARAM = "url";

	public static final String HTR_UPVLC_TRAIN_PATH = "htrTrainingUpvlc";
	public static final String HTR_URO_TRAIN_PATH = "htrTrainingUro";
	public static final String HTR_PATH = "htr";
	public static final String HTR_LIST_MODEL_PATH = "htrModels";
	public static final String HTR_MODEL_NAME_PARAM = "modelName";
	public static final String WER_PATH = "computeWER";
	
	public static final String KWS_SEARCH_PATH = "kwsSearch";
	
	//editorial declaration
	public static final String STORE_EDIT_DECL_FEATURE = "storeEditDeclFeat";
	public static final String UPDATE_EDIT_DECL_FEATURE = "updateEditDeclFeat";
	public static final String DELETE_EDIT_DECL_FEATURE = "deleteEditDeclFeat";	
	public static final String STORE_EDIT_DECL_OPTION = "storeEditDeclOption";
	public static final String UPDATE_EDIT_DECL_OPTION = "updateEditDeclOption";
	public static final String DELETE_EDIT_DECL_OPTION = "deleteEditDeclOption";	
	public static final String LIST_EDIT_DECL_FEATURES = "listEditDeclFeats";
	public static final String EDIT_DECL_PATH = "editorialDeclaration";

	public static final String FIND_DOCUMENTS_PATH = "findDocuments";
	public static final String COUNT_FIND_DOCUMENTS_PATH = "countFindDocuments";

	public static final String ONLY_FINISHED_PARAM = "onlyFinished";

	public static final String MOVE_TO_PARAM = "moveTo";

	public static final String SORT_COLUMN_PARAM = "sortColumn";
	public static final String SORT_DIRECTION_PARAM = "sortDirection";

	public static final String LIST_ACTIVE_SESSIONS_PATH = "listActiveSessions";
	public static final String LIST_DB_SESSIONS_PATH = "listDbSessions";

	public static final String TYPE_PARAM = "type";
	public static final String OPTS_PARAM = "opts";

	public static final String NOTE_PARAM = "note";
	public static final String PARENT_ID_PARAM = "parent";

	public static final String NAME_PARAM = "name";
	
	public static final String SET_COLLECTIONS_FIELD_PARAM = "setColls";
	
	public static final String GET_ALL_DOCS_IF_ADMIN_PARAM = "getAllDocsIfAdmin";

	public static final String CHECK_FOR_DUPLICATE_TITLE_PARAM = "checkForDuplicateTitle";
	public static final String CONFIDENCE_PARAM = "confidence";
	public static final String TEXT_PATH = "text";

	public static final String HTR_LIST_NETS_PATH = "nets";
	public static final String HTR_LIST_DICTS_PATH = "dicts";
	public static final String HTR_RNN_PATH = "rnn";

	public static final String HTR_DICT_NAME_PARAM = "dict";

	//OAuth stuff
	public static final String LOGIN_OAUTH_PATH = "oauth";
	public static final String CODE_PARAM = "code";
	public static final String STATE_PARAM = "state";
	public static final String PROVIDER_PARAM = "prov";
	public static final String REDIRECT_URI_PARAM = "redirect_uri";

	public static final String SEND_MAIL_PARAM = "sendMail";
	
	//Solr search
	public static final String FULLTEXT_PATH = "fulltext";
	public static final String QUERY_PARAM = "query";
	public static final String START_PARAM = "start";
	public static final String ROWS_PARAM = "rows";
	public static final String FILTER_PARAM = "filter";
	
	//Actions
	public static final String TYPE_ID_PARAM = "typeId";
	public static final String PAGE_ID_PARAM = "pageId";
	public static final String END_PARAM = "end";
	

	
}
