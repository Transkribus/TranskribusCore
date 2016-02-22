package eu.transkribus.core.model.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
// Not used anymore
public class TrpPageTranscriptBuilder {
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(TrpPageTranscriptBuilder.class);
	
//	public static JAXBPageTranscript build(JAXBPageTranscript transcript) throws JAXBException, IOException {
//		return build(transcript.getMd());
//	}
//	
//	public static JAXBPageTranscript build(TrpTranscriptMetadata md) throws JAXBException, IOException {
//		PcGtsType pageData;
//		if (md.getUrl()!=null)
//			pageData = PageXmlUtils.unmarshal(md.getUrl());
//		else
//			pageData = PageXmlUtils.createEmptyPcGtsType(md.getPage().getUrl());
//		
//		// set reference to metadata object:
//		if (pageData.getPage() instanceof TrpPageType) {
//			((TrpPageType) pageData.getPage()).setMd(md);
//		}
//		
//		JAXBPageTranscript ts = new JAXBPageTranscript(md, pageData);
//		return ts;
//	}
}
