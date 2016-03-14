package eu.transkribus.core.model.beans.job.enums;

public enum JobImpl {
	DeleteDocumentJob(JobTask.DeleteDocument, JobTask.DeleteDocument.getName(), "DeleteDocJob"),
	MetsImportJob(JobTask.CreateDocument, JobTask.CreateDocument.getName(), "MetsImportJob"),
	DocImportJob(JobTask.CreateDocument, JobTask.CreateDocument.getName(), "DocImportJob"),
	ZipDocImportJob(JobTask.CreateDocument, JobTask.CreateDocument.getName(), "ZipDocImportJob"),
	GoobiMetsImportJob(JobTask.CreateDocument, JobTask.CreateDocument.getName(), "GoobiMetsImportJob"),
	DuplicateDocumentJob(JobTask.DuplicateDocument, JobTask.DuplicateDocument.getName(), "DuplicateDocJob"),
	DeleteDocJob(JobTask.DeleteDocument, JobTask.DeleteDocument.getName(), "DeleteDocJob"),
	
	NcsrDetectBaselinesJob(JobTask.DetectBaselines, JobTask.DetectBaselines.getName(), "DetectBaselinesJob"),
	NcsrDetectLinesJob(JobTask.DetectLines, JobTask.DetectLines.getName(), "LineSegmentationJob"),
	NcsrDetectBlocksJob(JobTask.DetectBlocks, JobTask.DetectBlocks.getName(), "BlockSegmentationJob"),
	NcsrDetectWordsJob(JobTask.DetectWords, JobTask.DetectWords.getName(), "WordSegmentationJob"),
	
	HmmHtrJob(JobTask.Htr, "HMM " + JobTask.Htr.getName(), "HtrJob"),
	HmmHtrTrainingJob(JobTask.HtrTraining, "HMM " + JobTask.HtrTraining.getName(), "HtrTrainingJob"),
	FinereaderOcrJob(JobTask.Ocr, JobTask.Ocr.getName(), "OcrJob");
	
	
	private JobTask task;
	private String label;
	private String className;
	JobImpl(JobTask task, String label, String className){
		this.task = task;
		this.label = label;
		this.className = className;
	}
	
	public JobTask getTask(){
		return task;
	}
	public String getClassName(){
		return className;
	}

	public String getLabel() {
		return label;
	}
}
