package eu.transkribus.core.model.builder;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocStructure;
import eu.transkribus.core.model.beans.TrpDocStructure.TrpDocStructureImage;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;

public class TrpDocStructureBuilder {
	public static TrpDocStructure build(TrpDoc doc) {
		if(doc == null) {
			throw new IllegalArgumentException("doc is null.");
		}
		if (doc.getMd().getLocalFolder() == null) {
			throw new IllegalArgumentException("doc is no localdocument.");
		}
		if(doc.getNPages() < 1) {
			throw new IllegalArgumentException("doc has no pages.");
		}
		
		TrpDocStructure s = new TrpDocStructure();
		s.setMd(doc.getMd());
		
		for(TrpPage p : doc.getPages()) {
			s.getImages().add(buildStructureImage(p));
		}
		return s;
	}
	
	private static TrpDocStructureImage buildStructureImage(TrpPage p) {
		TrpDocStructureImage i = new TrpDocStructureImage();
		i.setFileName(p.getImgFileName());
		i.setIndex(p.getPageNr()-1);
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
