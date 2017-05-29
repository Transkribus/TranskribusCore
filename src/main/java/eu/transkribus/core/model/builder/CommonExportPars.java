package eu.transkribus.core.model.builder;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.dea.fimgstoreclient.beans.ImgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	boolean doWriteTagsXlsx = false;
	boolean doWriteTablesXlsx = false;
	
	boolean doCreateTitle=false;
	String useVersionStatus="Latest";
	
	boolean writeTextOnWordLevel = false;
	boolean doBlackening = false;
	Set<String> selectedTags = null;
	
	// from ExportOptions:
	public String dir=null;
	public Set<Integer> pageIndices=null; // can be set to null to include all pages!

	public String pageDirName = LocalDocConst.PAGE_FILE_SUB_FOLDER;
	public boolean splitIntoWordsInAltoXml=false;
	public String fileNamePattern = "${filename}";
	public boolean useHttps=true;
	public ImgType remoteImgQuality = ImgType.orig;
	@Deprecated //this will be in the Page element's custom attribute
	public boolean exportTranscriptMetadata = false;
	
	public boolean doOverwrite=true;
	public boolean useOcrMasterDir=true;
			
	public CommonExportPars() {
	}

	public CommonExportPars(String pages, boolean doWriteMets, boolean doWriteImages, boolean doExportPageXml,
			boolean doExportAltoXml, boolean doWritePdf, boolean doWriteTei, boolean doWriteDocx, 
			boolean doWriteTagsXlsx, boolean doWriteTablesXlsx,
			boolean doCreateTitle, String useVersionStatus, boolean writeTextOnWordLevel, boolean doBlackening, Set<String> selectedTags) {
		super();
		this.pages = pages;
		this.doWriteMets = doWriteMets;
		this.doWriteImages = doWriteImages;
		this.doExportPageXml = doExportPageXml;
		this.doExportAltoXml = doExportAltoXml;
		this.doWritePdf = doWritePdf;
		this.doWriteTei = doWriteTei;
		this.doWriteDocx = doWriteDocx;
		this.doWriteTagsXlsx = doWriteTagsXlsx;
		this.doWriteTablesXlsx = doWriteTablesXlsx;
		this.doCreateTitle = doCreateTitle;
		this.useVersionStatus = useVersionStatus;
		this.writeTextOnWordLevel = writeTextOnWordLevel;
		this.doBlackening = doBlackening;
		this.selectedTags = selectedTags;
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

	// utility method:
	public boolean isTagSelected(String tagName) {
		return selectedTags == null || selectedTags.contains(tagName);
	}
	
	public boolean isTaggableExport() {
		return (isDoWritePdf() || isDoWriteDocx() || isDoWriteTagsXlsx() || isDoWriteTei() || isDoWriteTagsXlsx());			
//		return (isDoWritePdf() || isDoWriteDocx() || isDoWriteTagsXlsx() || isDoWriteTei())
//				&& (isHighlightTags() || isDocxTagExport() || isTagXlsxExport());
	}

	@Override
	public String toString() {
		return "CommonExportPars [pages=" + pages + ", doWriteMets=" + doWriteMets + ", doWriteImages=" + doWriteImages
				+ ", doExportPageXml=" + doExportPageXml + ", doExportAltoXml=" + doExportAltoXml + ", doWritePdf="
				+ doWritePdf + ", doWriteTei=" + doWriteTei + ", doWriteDocx=" + doWriteDocx + ", doWriteTagsXlsx="
				+ doWriteTagsXlsx + ", doWriteTablesXlsx=" + doWriteTablesXlsx + ", doCreateTitle=" + doCreateTitle
				+ ", useVersionStatus=" + useVersionStatus + ", writeTextOnWordLevel=" + writeTextOnWordLevel
				+ ", doBlackening=" + doBlackening + ", selectedTags=" + selectedTags + "]";
	}

	

	

	
}
