package eu.transkribus.core.model.builder;

import org.apache.commons.lang3.StringUtils;

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
			s.getPages().add(buildStructureImage(p));
		}
		return s;
	}
	
	private static PageUploadDescriptor buildStructureImage(TrpPage p) {
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
}
