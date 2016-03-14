package eu.transkribus.core.model.beans.job.enums;

public enum JobTask {
	CreateDocument(JobType.utility, "Create Document"),
	DeleteDocument(JobType.utility, "Delete Document"),
	DuplicateDocument(JobType.utility, "Duplicate Document"),
	
	DetectBaselines(JobType.layoutAnalysis, "Detect Baselines"),
	DetectBlocks(JobType.layoutAnalysis, "Block Segmentation"),
	DetectLines(JobType.layoutAnalysis, "Line Segmentation"),
	DetectWords(JobType.layoutAnalysis, "Word Segmentation"),
	
	Htr(JobType.recognition, "Handwritten Text Recognition"),
	HtrTraining(JobType.recognition, "HTR Training"),
	Ocr(JobType.recognition, "Optical Character Recognition");
	
	private JobType type;
	private String name;
	JobTask(JobType type, String name){
		this.type = type;
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public JobType getJobType(){
		return type;
	}
}
