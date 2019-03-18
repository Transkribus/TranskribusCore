package eu.transkribus.core.model.beans.job.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum JobImpl {	
	// --- utility jobs ---
	DeleteDocumentJob(JobTask.DeleteDocument, JobTask.DeleteDocument.getLabel(), "DeleteDocJob", null),
	MetsImportJob(JobTask.CreateDocument, JobTask.CreateDocument.getLabel(), "MetsImportJob", null),
	DocImportJob(JobTask.CreateDocument, JobTask.CreateDocument.getLabel(), "DocImportJob", null),
	UploadImportJob(JobTask.CreateDocument, JobTask.CreateDocument.getLabel(), "UploadImportJob", null),
	ZipDocImportJob(JobTask.CreateDocument, JobTask.CreateDocument.getLabel(), "ZipDocImportJob", null),
	GoobiMetsImportJob(JobTask.CreateDocument, JobTask.CreateDocument.getLabel(), "GoobiMetsImportJob", null),
	DuplicateDocumentJob(JobTask.DuplicateDocument, JobTask.DuplicateDocument.getLabel(), "DuplicateDocJob", null),
	CreateSampleDocJob(JobTask.CreateSample, JobTask.CreateSample.getLabel(), "CreateSampleDocJob", null),
	ComputeSampleJob(JobTask.Htr,"Sample Computation","ComputeSampleJob",null),
	DeleteDocJob(JobTask.DeleteDocument, JobTask.DeleteDocument.getLabel(), "DeleteDocJob", null),
	RebuildSolrIndexJob(JobTask.RebuildSolrIndex, JobTask.RebuildSolrIndex.getLabel(), "RebuildSolrIndexJob", null),	
	IndexDocumentJob(JobTask.IndexDocument, JobTask.IndexDocument.getLabel(), "IndexDocumentJob", null),
	DocExportJob(JobTask.Export, JobTask.Export.getLabel(), "ExportDocumentJob", null),
	// ---------------------

	// --- LA jobs ---
	NcsrDetectLinesJob(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "NcsrDetectLinesJob", null),
	NcsrDetectBlocksJob(JobTask.DetectBlocks, JobTask.DetectBlocks.getLabel(), "NcsrDetectBlocksJob", null),
	NcsrBatchLaJob(JobTask.DetectBlocks, "Block/Line Segmentation", "NcsrBatchLaJob", null),
	
	NcsrOldLaJob(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "LaJob", null),
	NcsrLaJob(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "LaJob", "libNCSR_TextLineSegmentation.so"),
	CITlabLaJob(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "LaJob", null),
	CITlabAdvancedLaJob(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "LaJob", null),
	CvlLaJob(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "LaJob", null),
	CvlTableJob(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "LaJob", null),
	UpvlcLaJob(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "LaJob", null),
	P2PaLAJob(JobTask.DetectLines, "Layout and structure analysis", "P2PaLAJob", null),
	P2PaLAJobMultiThread(JobTask.DetectLines, "Layout and structure analysis", "P2PaLAJobMultiThread", null),
	
	//FIXME this could be reduced by stating one impl while the wrapper is merely a job-parameter
	NcsrOldLaJobMultiThread(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "MultiThreadLaJob", null),
	NcsrLaJobMultiThread(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "MultiThreadLaJob", "libNCSR_TextLineSegmentation.so"),
	CITlabLaJobMultiThread(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "MultiThreadLaJob", null),
	CITlabAdvancedLaJobMultiThread(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "MultiThreadLaJob", null),
	CvlLaJobMultiThread(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "MultiThreadLaJob", null),
	CvlTableJobMultiThread(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "MultiThreadLaJob", null),
	UpvlcLaJobMultiThread(JobTask.DetectLines, JobTask.DetectLines.getLabel(), "MultiThreadLaJob", null),
	// ---------------------
	
	// --- Text recognition jobs ---
	CITlabHtrTrainingJob(JobTask.HtrTraining, "CITlab " + JobTask.HtrTraining.getLabel(), "CITlabHtrTrainingJob", null),
	CITlabHtrPlusTrainingJob(JobTask.HtrTraining, "CITlab HTR+ Training", "CITlabHtrPlusTrainingJob", null),
	CITlabSemiSupervisedHtrTrainingJob(JobTask.HtrTraining, "CITlab Text2Image", "CITlabSemiSupervisedHtrTrainingJob", null),
	CITlabHtrJob(JobTask.Htr, "CITlab " + JobTask.Htr.getLabel(), "CITlabHtrJob", null),
	CITlabLocalHtrJob(JobTask.Htr, "Local CITlab " + JobTask.Htr.getLabel(), "CITlabLocalHtrJob", null),
	FinereaderOcrJob(JobTask.Ocr, JobTask.Ocr.getLabel(), "OcrJob", null),
	
	CITlabKwsJob(JobTask.Kws, "CITlab " + JobTask.Kws.getLabel(), "CITlabKwsJob", null),
	
	CITlabClusterRecognitionJob(JobTask.Htr, "CITlab Cluster Recognition", "CITlabClusterRecognition", null),
	
	//for testing
	DummyJob(JobTask.CreateDocument, "Dummy Job", "DummyJob", null),
	DummyMailJob(JobTask.CreateDocument, "Dummy Mail Job", "DummyMailJob", null), 
	ErrorRateJob(JobTask.Htr, "Error Rate Computation", "ErrorRateJob", null);
	
	private final static Logger logger = LoggerFactory.getLogger(JobImpl.class);
	public final static String MULTI_THREAD_LA_JOB_SUFFIX = "MultiThread";
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
	
	/**
	 * Same as {@link #valueOf} but will return null if invalid value is passed!
	 * 
	 * @param str
	 * @return
	 */
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

	public static JobImpl getBaseLaJob(JobImpl impl) {
		if(impl == null || !JobType.layoutAnalysis.equals(impl.getTask().getJobType())) {
			throw new IllegalArgumentException("No Layout Analysis job type: " + impl);
		}
		//Normalize to base job name (MultiThread suffix should only occur on server)
		final String baseImplStr = impl.toString().endsWith("MultiThread") ? impl.toString().replace("MultiThread", "") : impl.toString();
		return JobImpl.valueOf(baseImplStr);
	}
}
