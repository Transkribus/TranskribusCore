package eu.transkribus.core.model.beans.job.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum JobImpl {
	DeleteDocumentJob(JobTask.DeleteDocument, JobTask.DeleteDocument.getLabel(), "DeleteDocJob", null),
	MetsImportJob(JobTask.CreateDocument, JobTask.CreateDocument.getLabel(), "MetsImportJob", null),
	DocImportJob(JobTask.CreateDocument, JobTask.CreateDocument.getLabel(), "DocImportJob", null),
	ZipDocImportJob(JobTask.CreateDocument, JobTask.CreateDocument.getLabel(), "ZipDocImportJob", null),
	GoobiMetsImportJob(JobTask.CreateDocument, JobTask.CreateDocument.getLabel(), "GoobiMetsImportJob", null),
	DuplicateDocumentJob(JobTask.DuplicateDocument, JobTask.DuplicateDocument.getLabel(), "DuplicateDocJob", null),
	DeleteDocJob(JobTask.DeleteDocument, JobTask.DeleteDocument.getLabel(), "DeleteDocJob", null),
	RebuildSolrIndexJob(JobTask.RebuildSolrIndex, JobTask.RebuildSolrIndex.getLabel(), "RebuildSolrIndexJob", null),	
	IndexDocumentJob(JobTask.IndexDocument, JobTask.IndexDocument.getLabel(), "IndexDocumentJob", null),
	DocExportJob(JobTask.Export, JobTask.Export.getLabel(), "ExportDocumentJob", null),
	
	NcsrDetectBaselinesJob(JobTask.DetectBaselines, JobTask.DetectBaselines.getLabel(), "NcsrDetectBaselinesJob", null),
	NcsrDetectLinesJob(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "NcsrDetectLinesJob", null),
	NcsrDetectBlocksJob(JobTask.DetectBlocks, JobTask.DetectBlocks.getLabel(), "NcsrDetectBlocksJob", null),
	NcsrDetectWordsJob(JobTask.DetectWords, JobTask.DetectWords.getLabel(), "NcsrDetectWordsJob", null),
	NcsrBatchLaJob(JobTask.DetectBlocks, "Block/Line Segmentation", "NcsrBatchLaJob", null),
	
	NcsrSinglePageLineSegmentationJob(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "SinglePageLaJob", "libNCSR_TextLineSegmentation.so"),
	NcsrSinglePageWordSegmentationJob(JobTask.DetectWords, JobTask.DetectWords.getLabel(), "SinglePageLaJob", "libNCSR_WordSegmentation.so"),
	
	HmmHtrJob(JobTask.UpvlcHtr, "PRHLT " + JobTask.Htr.getLabel(), "HmmHtrJob", null),
	HmmHtrTrainingJob(JobTask.UpvlcHtrTraining, "PRHLT " + JobTask.HtrTraining.getLabel(), "HmmHtrTrainingJob", null),
	
	RnnHtrJob(JobTask.Htr, "CITlab " + JobTask.Htr.getLabel(), "RnnHtrJob", null),
	RnnHtrTrainingJob(JobTask.HtrTraining, "CITlab " + JobTask.HtrTraining.getLabel(), "RnnHtrTrainingJob", null),
	
	CITlabHtrTrainingJob(JobTask.HtrTraining, "CITlab " + JobTask.HtrTraining.getLabel(), "CITlabHtrTrainingJob", null),
//	CITlabHtrTrainingJob2(JobTask.HtrTraining, "CITlab " + JobTask.HtrTraining.getLabel(), "CITlabHtrTrainingJob", null), // duplicate for new module!
	
	CITlabHtrJob(JobTask.Htr, "CITlab " + JobTask.Htr.getLabel(), "CITlabHtrJob", null),
//	CITlabHtrJob2(JobTask.Htr, "CITlab " + JobTask.Htr.getLabel(), "CITlabHtrJob", null), // duplicate for new module!
	
	FinereaderOcrJob(JobTask.Ocr, JobTask.Ocr.getLabel(), "OcrJob", null),
//	FinereaderOcrJob2(JobTask.Ocr, JobTask.Ocr.getLabel(), "OcrJob", null), // duplicate for new module!
	
	//for testing
	DummyJob(JobTask.CreateDocument, "Dummy Job", "DummyJob", null),
	DummyJob2(JobTask.CreateDocument, "Dummy Job 2", "DummyJob2", null);
	
	private final static Logger logger = LoggerFactory.getLogger(JobImpl.class);
	
	private JobTask task;
	private String label;
	private String className;
	private String libName;
	
	JobImpl(JobTask task, String label, String className, String libName){
		this.task = task;
		this.label = label;
		this.className = className;
		this.libName = libName;
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
	public String getLibName() {
		return libName;
	}
	
	public static JobImpl fromStr(String str) {
		try {
			return JobImpl.valueOf(str); 
		}
		catch (Exception e) {
			logger.warn("Could not parse JobImpl from string: "+str+" - returning null value!");
			return null;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(JobImpl.valueOf(null)); 
	}
}
