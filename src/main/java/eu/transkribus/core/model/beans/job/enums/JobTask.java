package eu.transkribus.core.model.beans.job.enums;

public enum JobTask {
	CreateDocument(JobType.utility, "Create Document", "uibk"),
	DeleteDocument(JobType.utility, "Delete Document", "uibk"),
	DuplicateDocument(JobType.utility, "Duplicate Document", "uibk"),
	CreateSample(JobType.utility, "Create Sample", "uibk"),
	RebuildSolrIndex(JobType.utility, "Rebuild Solr Index", "uibk"),	
	IndexDocument(JobType.utility, "Build search index", "uibk"),
	Export(JobType.utility, "Export Document", "uibk"),
	
	DetectBaselines(JobType.layoutAnalysis, "Detect Baselines", "ncsr"),
	DetectBlocks(JobType.layoutAnalysis, "Block Segmentation", "ncsr"),
	DetectLines(JobType.layoutAnalysis, "Line Segmentation", "ncsr"),
	DetectWords(JobType.layoutAnalysis, "Word Segmentation", "ncsr"),
	Polygon2Baseline(JobType.layoutAnalysis, "Polygon to Baseline", "ncsr"),
	Baseline2Polygon(JobType.layoutAnalysis, "Baseline to Polygon", "upvlc"),
	
	LayoutAnalysis(JobType.layoutAnalysis, "Layout Analysis", "ncsr"),
	
	Htr(JobType.recognition, "Handwritten Text Recognition", "uro"),
	HtrTraining(JobType.recognition, "HTR Training", "uro"),
	Ocr(JobType.recognition, "Optical Character Recognition", "uibk"),
	UpvlcHtr(JobType.upvlc, "Handwritten Text Recognition", "upvlc"),
	UpvlcHtrTraining(JobType.upvlc, "HTR Training", "upvlc"),
	P2PaLATraining(JobType.upvlc, "P2PaLA Training", "upvlc"),
	
	Kws(JobType.kws, "Keyword Spotting", "uro");
	
	private JobType type;
	private String label;
	private String defaultProvider;
	
	JobTask(JobType type, String name, String defaultProvider){
		this.type = type;
		this.label = name;
		this.defaultProvider = defaultProvider;
	}
	
	public String getLabel(){
		return label;
	}
	
	public JobType getJobType(){
		return type;
	}
	
	public String getDefaultProvider() {
		return defaultProvider;
	}
}
