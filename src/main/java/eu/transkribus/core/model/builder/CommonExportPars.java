package eu.transkribus.core.model.builder;

import java.io.IOException;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CommonExportPars {
	private static final Logger logger = LoggerFactory.getLogger(CommonExportPars.class);
	
	public static final String PARAMETER_KEY = "commonPars";
	
	String pages = null;
//	Set<Integer> pageIndices = null;
	
	boolean doExportDocMetadata=true;
	boolean doWriteMets=true;
	boolean doWriteImages=true;
	boolean doExportPageXml=true; 
	boolean doExportAltoXml=true;
	boolean doExportSingleTxtFiles=false;
	boolean doWritePdf=false;
	boolean doWriteTei=false;
	boolean doWriteDocx=false;
	boolean doWriteOneTxt=false;
	boolean doWriteTagsXlsx = false;
	boolean doWriteTagsIob = false;
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
	/**
	 * defines which prepared image type should be exported.
	 * <br>
	 * Set to private as the setter will enforce that this is never null
	 */
	private ImgType remoteImgQuality = ImgType.orig;
	boolean doOverwrite=true;
	boolean useOcrMasterDir=true;
	boolean exportTranscriptMetadata = true;
	boolean updatePageXmlImageDimensions = false;
			
	public CommonExportPars() {
	}
	
	//TODO: for backward compatibility - old export document job, delete when replaced
	public CommonExportPars(String pages, boolean doWriteMets, boolean doWriteImages, boolean doExportPageXml,
			boolean doExportAltoXml, boolean doWritePdf, boolean doWriteTei, boolean doWriteDocx, 
			boolean doWriteTxt, boolean doWriteTagsXlsx, boolean doWriteTagsIob, boolean doWriteTablesXlsx,
			boolean doCreateTitle, String useVersionStatus, boolean writeTextOnWordLevel, 
			boolean doBlackening, Set<String> selectedTags) {
		
		this(pages, doWriteMets, doWriteImages, doExportPageXml, doExportAltoXml, false, doWritePdf, doWriteTei, doWriteDocx, 
				doWriteTxt, doWriteTagsXlsx, doWriteTablesXlsx, doWriteTagsIob, doCreateTitle, useVersionStatus, writeTextOnWordLevel, doBlackening, null, null);
	}

	public CommonExportPars(String pages, boolean doWriteMets, boolean doWriteImages, boolean doExportPageXml,
			boolean doExportAltoXml, boolean doExportSingleTxtFiles, boolean doWritePdf, boolean doWriteTei, boolean doWriteDocx, 
			boolean doWriteTxt, boolean doWriteTagsXlsx,boolean doWriteTagsIob, boolean doWriteTablesXlsx,
			boolean doCreateTitle, String useVersionStatus, boolean writeTextOnWordLevel, 
			boolean doBlackening, Set<String> selectedTags, String font) {
		this();
		this.pages = pages;
		this.doWriteMets = doWriteMets;
		this.doWriteImages = doWriteImages;
		this.doExportPageXml = doExportPageXml;
		this.doExportAltoXml = doExportAltoXml;
		this.doExportSingleTxtFiles = doExportSingleTxtFiles;
		this.doWritePdf = doWritePdf;
		this.doWriteTei = doWriteTei;
		this.doWriteDocx = doWriteDocx;
		this.doWriteOneTxt = doWriteTxt;
		this.doWriteTagsXlsx = doWriteTagsXlsx;
		this.doWriteTagsIob = doWriteTagsIob;
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
	
	public boolean isDoWriteTagsIob() {
		return doWriteTagsIob;
	}

	public void setDoWriteTagsIob(boolean doWriteTagsIob) {
		this.doWriteTagsIob = doWriteTagsIob;
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

	public boolean isDoExportDocMetadata() {
		return doExportDocMetadata;
	}
	
	public void setDoExportDocMetadata(boolean doExportDocMetadata) {
		this.doExportDocMetadata = doExportDocMetadata;
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

	public boolean isDoExportSingleTxtFiles() {
		return doExportSingleTxtFiles;
	}

	public void setDoExportSingleTxtFiles(boolean doExportSingleTxtFiles) {
		this.doExportSingleTxtFiles = doExportSingleTxtFiles;
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
		return doWriteOneTxt;
	}

	public void setDoWriteTxt(boolean doWriteTxt) {
		this.doWriteOneTxt = doWriteTxt;
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
		if(remoteImgQuality == null) {
			//remoteImgQuality shall never be null
			this.remoteImgQuality = ImgType.orig;
		} else {
			this.remoteImgQuality = remoteImgQuality;
		}
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
	
	public boolean isUpdatePageXmlImageDimensions() {
		return updatePageXmlImageDimensions;
	}

	public void setUpdatePageXmlImageDimensions(boolean updatePageXmlImageDimensions) {
		this.updatePageXmlImageDimensions = updatePageXmlImageDimensions;
	}

	// --- utility methods ---

	public boolean isTagSelected(String tagName) {
		return selectedTags == null || selectedTags.contains(tagName);
	}
	
	public boolean isTaggableExport() {
		return (isDoWritePdf() || isDoWriteDocx() || isDoWriteTagsXlsx() || isDoWriteTei() || isDoWriteTagsIob());			
//		return (isDoWritePdf() || isDoWriteDocx() || isDoWriteTagsXlsx() || isDoWriteTei())
//				&& (isHighlightTags() || isDocxTagExport() || isTagXlsxExport());
	}
	
	/*
	 * export only images - that means we do not load the transcripts during export and save time
	 */
	public boolean exportImagesOnly(){
		return (isDoWriteMets() && doWriteImages && !doExportAltoXml && !doExportPageXml && !doExportSingleTxtFiles &&
				!(isDoWritePdf() || isDoWriteDocx() || isDoWriteTxt() || isDoWriteTagsXlsx() || isDoWriteTei() || isDoWriteTablesXlsx()));
		
	}

	@Override
	public String toString() {
		return "CommonExportPars [pages=" + pages + ", doWriteMets=" + doWriteMets + ", doWriteImages=" + doWriteImages
				+ ", doExportPageXml=" + doExportPageXml + ", doExportAltoXml=" + doExportAltoXml + ", doExportSingleTxtFiles=" + doExportSingleTxtFiles
				+ ", doWritePdf=" + doWritePdf + ", doWriteTei=" + doWriteTei + ", doWriteDocx=" + doWriteDocx + ", doWriteTagsXlsx="
				+ doWriteTagsXlsx + ", doWriteTagsIob="+doWriteTagsIob+", doWriteTablesXlsx=" + doWriteTablesXlsx + ", doCreateTitle=" + doCreateTitle
				+ ", useVersionStatus=" + useVersionStatus + ", writeTextOnWordLevel=" + writeTextOnWordLevel
				+ ", doBlackening=" + doBlackening + ", selectedTags=" + selectedTags + ", dir=" + dir
				+ ", splitIntoWordsInAltoXml=" + splitIntoWordsInAltoXml + ", pageDirName=" + pageDirName
				+ ", fileNamePattern=" + fileNamePattern + ", useHttps=" + useHttps + ", remoteImgQuality="
				+ remoteImgQuality + ", doOverwrite=" + doOverwrite + ", useOcrMasterDir=" + useOcrMasterDir
				+ ", exportTranscriptMetadata=" + exportTranscriptMetadata + ", font=" + font + ", updatePageXmlImageDimensions = "+updatePageXmlImageDimensions+"]";
	}

	public static CommonExportPars getDefaultParSetForHtrTraining() {
		CommonExportPars opts = new CommonExportPars();
		//don't write a metadata XML
		opts.setDoExportDocMetadata(false);
		opts.setDoWriteImages(true);
		opts.setDoExportAltoXml(false);
		opts.setDoExportPageXml(true);
		//export pageXml to same dir as images
		opts.setPageDirName("");
		opts.setUseOcrMasterDir(false);
		opts.setDoWriteMets(false);
		//use page ID as filename to not run into problems with overlaps. Consider e.g. "0000001.jpg"
		opts.setFileNamePattern(ExportFilePatternUtils.PAGEID_PATTERN);
		return opts;
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
		opts.setFileNamePattern(ExportFilePatternUtils.PAGEID_PATTERN);
		return opts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
		result = prime * result + (doBlackening ? 1231 : 1237);
		result = prime * result + (doCreateTitle ? 1231 : 1237);
		result = prime * result + (doExportAltoXml ? 1231 : 1237);
		result = prime * result + (doExportSingleTxtFiles ? 1231 : 1237);
		result = prime * result + (doExportPageXml ? 1231 : 1237);
		result = prime * result + (doOverwrite ? 1231 : 1237);
		result = prime * result + (doWriteDocx ? 1231 : 1237);
		result = prime * result + (doWriteImages ? 1231 : 1237);
		result = prime * result + (doWriteMets ? 1231 : 1237);
		result = prime * result + (doWritePdf ? 1231 : 1237);
		result = prime * result + (doWriteTablesXlsx ? 1231 : 1237);
		result = prime * result + (doWriteTagsXlsx ? 1231 : 1237);
		result = prime * result + (doWriteTei ? 1231 : 1237);
		result = prime * result + (doWriteOneTxt ? 1231 : 1237);
		result = prime * result + (exportTranscriptMetadata ? 1231 : 1237);
		result = prime * result + ((fileNamePattern == null) ? 0 : fileNamePattern.hashCode());
		result = prime * result + ((font == null) ? 0 : font.hashCode());
		result = prime * result + ((pageDirName == null) ? 0 : pageDirName.hashCode());
		result = prime * result + ((pages == null) ? 0 : pages.hashCode());
		result = prime * result + ((remoteImgQuality == null) ? 0 : remoteImgQuality.hashCode());
		result = prime * result + ((selectedTags == null) ? 0 : selectedTags.hashCode());
		result = prime * result + (splitIntoWordsInAltoXml ? 1231 : 1237);
		result = prime * result + (useHttps ? 1231 : 1237);
		result = prime * result + (useOcrMasterDir ? 1231 : 1237);
		result = prime * result + ((useVersionStatus == null) ? 0 : useVersionStatus.hashCode());
		result = prime * result + (writeTextOnWordLevel ? 1231 : 1237);
		result = prime * result + (updatePageXmlImageDimensions ? 1231 : 1237);
		return result;
	}
	
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CommonExportPars other = (CommonExportPars) obj;
		if (dir == null) {
			if (other.dir != null)
				return false;
		} else if (!dir.equals(other.dir))
			return false;
		if (doBlackening != other.doBlackening)
			return false;
		if (doCreateTitle != other.doCreateTitle)
			return false;
		if (doExportAltoXml != other.doExportAltoXml)
			return false;
		if (doExportPageXml != other.doExportPageXml)
			return false;
		if (doExportSingleTxtFiles != other.doExportSingleTxtFiles)
			return false;
		if (doOverwrite != other.doOverwrite)
			return false;
		if (doWriteDocx != other.doWriteDocx)
			return false;
		if (doWriteImages != other.doWriteImages)
			return false;
		if (doWriteMets != other.doWriteMets)
			return false;
		if (doWritePdf != other.doWritePdf)
			return false;
		if (doWriteTablesXlsx != other.doWriteTablesXlsx)
			return false;
		if (doWriteTagsXlsx != other.doWriteTagsXlsx)
			return false;
		if(doWriteTagsIob != other.doWriteTagsIob)
			return false;
		if (doWriteTei != other.doWriteTei)
			return false;
		if (doWriteOneTxt != other.doWriteOneTxt)
			return false;
		if (exportTranscriptMetadata != other.exportTranscriptMetadata)
			return false;
		if (fileNamePattern == null) {
			if (other.fileNamePattern != null)
				return false;
		} else if (!fileNamePattern.equals(other.fileNamePattern))
			return false;
		if (font == null) {
			if (other.font != null)
				return false;
		} else if (!font.equals(other.font))
			return false;
		if (pageDirName == null) {
			if (other.pageDirName != null)
				return false;
		} else if (!pageDirName.equals(other.pageDirName))
			return false;
		if (pages == null) {
			if (other.pages != null)
				return false;
		} else if (!pages.equals(other.pages))
			return false;
		if (remoteImgQuality != other.remoteImgQuality)
			return false;
		if (selectedTags == null) {
			if (other.selectedTags != null)
				return false;
		} else if (!selectedTags.equals(other.selectedTags))
			return false;
		if (splitIntoWordsInAltoXml != other.splitIntoWordsInAltoXml)
			return false;
		if (useHttps != other.useHttps)
			return false;
		if (useOcrMasterDir != other.useOcrMasterDir)
			return false;
		if (useVersionStatus == null) {
			if (other.useVersionStatus != null)
				return false;
		} else if (!useVersionStatus.equals(other.useVersionStatus))
			return false;
		if (writeTextOnWordLevel != other.writeTextOnWordLevel)
			return false;
		if (updatePageXmlImageDimensions != other.updatePageXmlImageDimensions)
			return false;
		return true;
	}
}
