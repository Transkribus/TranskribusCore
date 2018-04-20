package eu.transkribus.core.model.builder;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.dea.fimgstoreclient.beans.ImgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.ExportFilePatternUtils;
import eu.transkribus.core.io.LocalDocConst;
import eu.transkribus.core.model.builder.tei.TeiExportPars;
import eu.transkribus.core.util.CoreUtils;

/**
 * A general set of export parameters. Can and shall be subclassed for special exports as e.g. in {@link TeiExportPars}
 */
public class CommonExportPars {
	private static final Logger logger = LoggerFactory.getLogger(CommonExportPars.class);
	
	public static final String PARAMETER_KEY = "commonPars";
	
	String pages = null;
//	Set<Integer> pageIndices = null;
	
	boolean doWriteMets=true;
	boolean doWriteImages=true;
	boolean doExportPageXml=true; 
	boolean doExportAltoXml=true;
	boolean doWritePdf=false;
	boolean doWriteTei=false;
	boolean doWriteDocx=false;
	boolean doWriteTxt=false;
	boolean doWriteTagsXlsx = false;
	boolean doWriteTablesXlsx = false;
	
	boolean doCreateTitle=false;
	String useVersionStatus="Latest";
	
	boolean writeTextOnWordLevel = false;
	boolean doBlackening = false;
	Set<String> selectedTags = null;
	
	// from ExportOptions:
	String dir=null;
	String font = "FreeSerif";
	
	boolean splitIntoWordsInAltoXml=false;
	
	String pageDirName = LocalDocConst.PAGE_FILE_SUB_FOLDER;
	String fileNamePattern = ExportFilePatternUtils.PAGENR_FILENAME_PATTERN;
	boolean useHttps=true;
	ImgType remoteImgQuality = ImgType.orig;
	boolean doOverwrite=true;
	boolean useOcrMasterDir=true;
	boolean exportTranscriptMetadata = true;
			
	public CommonExportPars() {
	}
	
	//TODO: for backward compatibility - old export document job, delete when replaced
	public CommonExportPars(String pages, boolean doWriteMets, boolean doWriteImages, boolean doExportPageXml,
			boolean doExportAltoXml, boolean doWritePdf, boolean doWriteTei, boolean doWriteDocx, 
			boolean doWriteTxt, boolean doWriteTagsXlsx, boolean doWriteTablesXlsx,
			boolean doCreateTitle, String useVersionStatus, boolean writeTextOnWordLevel, 
			boolean doBlackening, Set<String> selectedTags) {
		
		this(pages, doWriteMets, doWriteImages, doExportPageXml, doExportAltoXml, doWritePdf, doWriteTei, doWriteDocx, 
				doWriteTxt, doWriteTagsXlsx, doWriteTablesXlsx, doCreateTitle, useVersionStatus, writeTextOnWordLevel, doBlackening, null, null);
	}

	public CommonExportPars(String pages, boolean doWriteMets, boolean doWriteImages, boolean doExportPageXml,
			boolean doExportAltoXml, boolean doWritePdf, boolean doWriteTei, boolean doWriteDocx, 
			boolean doWriteTxt, boolean doWriteTagsXlsx, boolean doWriteTablesXlsx,
			boolean doCreateTitle, String useVersionStatus, boolean writeTextOnWordLevel, 
			boolean doBlackening, Set<String> selectedTags, String font) {
		super();
		this.pages = pages;
		this.doWriteMets = doWriteMets;
		this.doWriteImages = doWriteImages;
		this.doExportPageXml = doExportPageXml;
		this.doExportAltoXml = doExportAltoXml;
		this.doWritePdf = doWritePdf;
		this.doWriteTei = doWriteTei;
		this.doWriteDocx = doWriteDocx;
		this.doWriteTxt = doWriteTxt;
		this.doWriteTagsXlsx = doWriteTagsXlsx;
		this.doWriteTablesXlsx = doWriteTablesXlsx;
		this.doCreateTitle = doCreateTitle;
		this.useVersionStatus = useVersionStatus;
		this.writeTextOnWordLevel = writeTextOnWordLevel;
		this.doBlackening = doBlackening;
		this.selectedTags = selectedTags;
		this.font = font;
	}
	
	

	public boolean isDoWriteTagsXlsx() {
		return doWriteTagsXlsx;
	}

	public void setDoWriteTagsXlsx(boolean doWriteTagsXlsx) {
		this.doWriteTagsXlsx = doWriteTagsXlsx;
	}

	public boolean isDoWriteTablesXlsx() {
		return doWriteTablesXlsx;
	}

	public void setDoWriteTablesXlsx(boolean doWriteTablesXlsx) {
		this.doWriteTablesXlsx = doWriteTablesXlsx;
	}

	public boolean isWriteTextOnWordLevel() {
		return writeTextOnWordLevel;
	}

	public void setWriteTextOnWordLevel(boolean writeTextOnWordLevel) {
		this.writeTextOnWordLevel = writeTextOnWordLevel;
	}

	public boolean isDoBlackening() {
		return doBlackening;
	}

	public void setDoBlackening(boolean doBlackening) {
		this.doBlackening = doBlackening;
	}

	/**
	 * Helper method that parses the pages string (this.pages) with a given number of pages (nPages) into a set of page indices (starting from 0!)
	 */
	public Set<Integer> getPageIndices(int nPages) {
		if (StringUtils.isEmpty(this.pages))
			return null;
		
		try {
			return CoreUtils.parseRangeListStr(this.pages, nPages); 
		} catch (IOException e) {
			logger.warn("Could not pares pages string '"+pages+"' - "+e.getMessage());
			return null;
		}
	}

//	public void setPageIndices(Set<Integer> pageIndices) {
//		this.pageIndices = pageIndices;
//		
//		if (this.pageIndices != null && this.pageIndices.isEmpty()) // if no pages -> set to null -> means all pages in tei export
//			this.pageIndices = null;
//	}
	
	

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public Set<String> getSelectedTags() {
		return selectedTags;
	}

	public String getPages() {
		return pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public void setSelectedTags(Set<String> selectedTags) {
		this.selectedTags = selectedTags;
	}

	public boolean isDoWriteMets() {
		return doWriteMets;
	}

	public void setDoWriteMets(boolean doWriteMets) {
		this.doWriteMets = doWriteMets;
	}

	public boolean isDoWriteImages() {
		return doWriteImages;
	}

	public void setDoWriteImages(boolean doWriteImages) {
		this.doWriteImages = doWriteImages;
	}

	public boolean isDoExportPageXml() {
		return doExportPageXml;
	}

	public void setDoExportPageXml(boolean doExportPageXml) {
		this.doExportPageXml = doExportPageXml;
	}

	public boolean isDoExportAltoXml() {
		return doExportAltoXml;
	}

	public void setDoExportAltoXml(boolean doExportAltoXml) {
		this.doExportAltoXml = doExportAltoXml;
	}

	public boolean isDoWritePdf() {
		return doWritePdf;
	}

	public void setDoWritePdf(boolean doWritePdf) {
		this.doWritePdf = doWritePdf;
	}

	public boolean isDoWriteTei() {
		return doWriteTei;
	}

	public void setDoWriteTei(boolean doWriteTei) {
		this.doWriteTei = doWriteTei;
	}

	public boolean isDoWriteDocx() {
		return doWriteDocx;
	}

	public void setDoWriteDocx(boolean doWriteDocx) {
		this.doWriteDocx = doWriteDocx;
	}

	public boolean isDoWriteTxt() {
		return doWriteTxt;
	}

	public void setDoWriteTxt(boolean doWriteTxt) {
		this.doWriteTxt = doWriteTxt;
	}

	public boolean isDoCreateTitle() {
		return doCreateTitle;
	}

	public void setDoCreateTitle(boolean doCreateTitle) {
		this.doCreateTitle = doCreateTitle;
	}

	public String getUseVersionStatus() {
		return useVersionStatus;
	}

	public void setUseVersionStatus(String useVersionStatus) {
		this.useVersionStatus = useVersionStatus;
	}
	
	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public boolean isSplitIntoWordsInAltoXml() {
		return splitIntoWordsInAltoXml;
	}

	public void setSplitIntoWordsInAltoXml(boolean splitIntoWordsInAltoXml) {
		this.splitIntoWordsInAltoXml = splitIntoWordsInAltoXml;
	}

	public String getPageDirName() {
		return pageDirName;
	}

	public void setPageDirName(String pageDirName) {
		this.pageDirName = pageDirName;
	}

	public String getFileNamePattern() {
		return fileNamePattern;
	}

	public void setFileNamePattern(String fileNamePattern) {
		this.fileNamePattern = fileNamePattern;
	}

	public boolean isUseHttps() {
		return useHttps;
	}

	public void setUseHttps(boolean useHttps) {
		this.useHttps = useHttps;
	}

	public ImgType getRemoteImgQuality() {
		return remoteImgQuality;
	}

	public void setRemoteImgQuality(ImgType remoteImgQuality) {
		this.remoteImgQuality = remoteImgQuality;
	}

	public boolean isDoOverwrite() {
		return doOverwrite;
	}

	public void setDoOverwrite(boolean doOverwrite) {
		this.doOverwrite = doOverwrite;
	}

	public boolean isUseOcrMasterDir() {
		return useOcrMasterDir;
	}

	public void setUseOcrMasterDir(boolean useOcrMasterDir) {
		this.useOcrMasterDir = useOcrMasterDir;
	}

	public boolean isExportTranscriptMetadata() {
		return exportTranscriptMetadata;
	}

	public void setExportTranscriptMetadata(boolean exportTranscriptMetadata) {
		this.exportTranscriptMetadata = exportTranscriptMetadata;
	}

	// --- utility methods ---

	public boolean isTagSelected(String tagName) {
		return selectedTags == null || selectedTags.contains(tagName);
	}
	
	public boolean isTaggableExport() {
		return (isDoWritePdf() || isDoWriteDocx() || isDoWriteTagsXlsx() || isDoWriteTei());			
//		return (isDoWritePdf() || isDoWriteDocx() || isDoWriteTagsXlsx() || isDoWriteTei())
//				&& (isHighlightTags() || isDocxTagExport() || isTagXlsxExport());
	}
	
	/*
	 * export only images - that means we do not load the transcripts during export and save time
	 */
	public boolean exportImagesOnly(){
		return (isDoWriteMets() && doWriteImages && !doExportAltoXml && !doExportPageXml && 
				!(isDoWritePdf() || isDoWriteDocx() || isDoWriteTxt() || isDoWriteTagsXlsx() || isDoWriteTei() || isDoWriteTablesXlsx()));
		
	}

	@Override
	public String toString() {
		return "CommonExportPars [pages=" + pages + ", doWriteMets=" + doWriteMets + ", doWriteImages=" + doWriteImages
				+ ", doExportPageXml=" + doExportPageXml + ", doExportAltoXml=" + doExportAltoXml + ", doWritePdf="
				+ doWritePdf + ", doWriteTei=" + doWriteTei + ", doWriteDocx=" + doWriteDocx + ", doWriteTagsXlsx="
				+ doWriteTagsXlsx + ", doWriteTablesXlsx=" + doWriteTablesXlsx + ", doCreateTitle=" + doCreateTitle
				+ ", useVersionStatus=" + useVersionStatus + ", writeTextOnWordLevel=" + writeTextOnWordLevel
				+ ", doBlackening=" + doBlackening + ", selectedTags=" + selectedTags + ", dir=" + dir
				+ ", splitIntoWordsInAltoXml=" + splitIntoWordsInAltoXml + ", pageDirName=" + pageDirName
				+ ", fileNamePattern=" + fileNamePattern + ", useHttps=" + useHttps + ", remoteImgQuality="
				+ remoteImgQuality + ", doOverwrite=" + doOverwrite + ", useOcrMasterDir=" + useOcrMasterDir
				+ ", exportTranscriptMetadata=" + exportTranscriptMetadata + ", font=" + font + "]";
	}

	public static CommonExportPars getDefaultParSetForHtrTraining() {
		CommonExportPars c = new CommonExportPars();
		c.setDoWriteImages(true);
		c.setDoExportAltoXml(false);
		c.setDoExportPageXml(true);
		c.setPageDirName("");
		c.setUseOcrMasterDir(false);
		c.setDoWriteMets(false);
		return c;
	}

	public static CommonExportPars getDefaultParSetForOcr() {
		CommonExportPars opts = new CommonExportPars();
		opts.setDoOverwrite(true);
		opts.setDoWriteMets(false);
		opts.setUseOcrMasterDir(true);
		opts.setDoWriteImages(true);
		opts.setDoExportPageXml(false);
		opts.setDoExportAltoXml(false);
		//store files with pageID as name. Important for matching result files to original document!
		opts.setFileNamePattern("${pageId}");
		return opts;
	}
	
	
}
