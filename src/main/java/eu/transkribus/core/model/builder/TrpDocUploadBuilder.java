package eu.transkribus.core.model.builder;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import eu.transkribus.core.io.LocalDocConst;
import eu.transkribus.core.io.util.ImgFilenameFilter;
import eu.transkribus.core.model.beans.DocumentUploadDescriptor;
import eu.transkribus.core.model.beans.DocumentUploadDescriptor.PageUploadDescriptor;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;

public class TrpDocUploadBuilder {
	public static DocumentUploadDescriptor build(TrpDoc doc) {
		if(doc == null) {
			throw new IllegalArgumentException("doc is null.");
		}
		if (doc.getMd().getLocalFolder() == null) {
			throw new IllegalArgumentException("doc is no localdocument.");
		}
		if(doc.getNPages() < 1) {
			throw new IllegalArgumentException("doc has no pages.");
		}
		
		DocumentUploadDescriptor s = new DocumentUploadDescriptor();
		s.setMd(doc.getMd());
		
		for(TrpPage p : doc.getPages()) {
			s.getPages().add(buildPageUploadDescriptor(p));
		}
		return s;
	}
	
	private static PageUploadDescriptor buildPageUploadDescriptor(TrpPage p) {
		PageUploadDescriptor i = new PageUploadDescriptor();
		i.setFileName(p.getImgFileName());
		i.setPageNr(p.getPageNr());
		if(!StringUtils.isEmpty(p.getMd5Sum())) {
			i.setImgChecksum(p.getMd5Sum());
		}
		//add transcript if any
		if(!p.getTranscripts().isEmpty()) {
			TrpTranscriptMetadata t = p.getCurrentTranscript();
			i.setPageXmlName(t.getXmlFileName());
			
			if (!StringUtils.isEmpty(t.getMd5Sum())) {
				i.setPageXmlChecksum(t.getMd5Sum());
			}
		}
		return i;
	}

	/**Ensures that all images have filenames assigned and page indices are iterated throughout the structure
	 * If page indices start from 0 they will be incremented by 1 in order to be compatible with METS-style counting.
	 * If XML filenames have the "page/" dir prefix, it will be removed.
	 * @param pages
	 */
	public static void validateAndNormalize(List<PageUploadDescriptor> pages) {
		if(pages.isEmpty()) {
			throw new IllegalArgumentException("Image list is empty!");
		}
		ImgFilenameFilter imgNameFilter = new ImgFilenameFilter();
		//check page indices
		int i = pages.get(0).getPageNr();
		//check if it starts with 1 or 0
		boolean pageCountFromZero = false;
		if(i == 0) {
			//increment all indexes by 1
			pageCountFromZero = true;
		} else if (i < 0 || i > 1) {
			throw new IllegalArgumentException("page indexes have to start with 1 or 0!");
		}
		for(PageUploadDescriptor img : pages) {
			//check page indexes for continuity
			if(img.getPageNr() != i) {
				throw new IllegalArgumentException("Page indexes are inconsistent!");
			} else {
				i++;
			}
			//correct the index if counting starts from zero as METS also includes counts starting from 1
			if(pageCountFromZero) {
				img.setPageNr(img.getPageNr() + 1);
			}
			//ensure that at least the img filename is set
			if(StringUtils.isEmpty(img.getFileName())) {
				throw new IllegalArgumentException("Image filename is empty for page index: " + img.getPageNr());
			}
			if(!imgNameFilter.accept(null, img.getFileName())){
				throw new IllegalArgumentException("Image type is not supported: " + img.getFileName());
			}
			if(!StringUtils.isEmpty(img.getPageXmlName()) 
					&& img.getPageXmlName().startsWith(LocalDocConst.PAGE_FILE_SUB_FOLDER + "/")) {
				//remove the "page/" prefix in XML filename if existent
				img.setPageXmlName(img.getPageXmlName().replaceFirst(LocalDocConst.PAGE_FILE_SUB_FOLDER + "/", ""));
			}
		}
	}
}
