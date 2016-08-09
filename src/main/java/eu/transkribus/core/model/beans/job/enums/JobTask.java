package eu.transkribus.core.model.beans.job.enums;

public enum JobTask {
	CreateDocument(JobType.utility, "Create Document"),
	DeleteDocument(JobType.utility, "Delete Document"),
	DuplicateDocument(JobType.utility, "Duplicate Document"),
	RebuildSolrIndex(JobType.utility, "Rebuild Solr Index"),	
	IndexDocument(JobType.utility, "Build search index"),
	
	DetectBaselines(JobType.layoutAnalysis, "Detect Baselines"),
	DetectBlocks(JobType.layoutAnalysis, "Block Segmentation"),
	DetectLines(JobType.layoutAnalysis, "Line Segmentation"),
	DetectWords(JobType.layoutAnalysis, "Word Segmentation"),
	
	Htr(JobType.recognition, "Handwritten Text Recognition"),
	HtrTraining(JobType.recognition, "HTR Training"),
	Ocr(JobType.recognition, "Optical Character Recognition");
	
	private JobType type;
	private String label;
	JobTask(JobType type, String name){
		this.type = type;
		this.label = name;
	}
	
	public String getLabel(){
		return label;
	}
	
	public JobType getJobType(){
		return type;
	}
}
