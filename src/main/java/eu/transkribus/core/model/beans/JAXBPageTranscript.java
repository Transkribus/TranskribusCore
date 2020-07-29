package eu.transkribus.core.model.beans;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.util.PageXmlUtils;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JAXBPageTranscript extends AbstractPageTranscript<PcGtsType> {
	private static final Logger logger = LoggerFactory.getLogger(JAXBPageTranscript.class);
	
	public JAXBPageTranscript(){}
	
	public JAXBPageTranscript(TrpTranscriptMetadata md) {
		this.setMd(md);
	}
	
	public JAXBPageTranscript(TrpTranscriptMetadata md, PcGtsType pageData) {
		this(md);
		
//		if (pageData == null) {
//			throw new IllegalArgumentException("pageData is null");
//		}
		this.setPageData(pageData);
	}
	
	public void clear() {
		super.setMd(null);
		super.setPageData(null);
	}
	
//	public void setData(TrpTranscriptMetadata md, PcGtsType page) {
//		super.setMd(md);
//		super.setPageData(page);
//		
//		updateMdOnTrpPageType();
//	}
	
	@Override
	public void setMd(TrpTranscriptMetadata md) {
		logger.debug("Setting transcript md: {}", md);
		super.setMd(md);
	}
	
	@Override public void setPageData(PcGtsType page) {
		logger.debug("setting page data!");
		super.setPageData(page);
		logger.debug("Included page md: {}", getPage().getMd());
		updateMdOnTrpPageType();
	}
	
	void updateMdOnTrpPageType() {
		if (md!=null && getPage()!=null) {
			logger.debug("Overriding page data's transcript md with: {}", md);
			getPage().setMd(md);
		}
	}
	
//	public BorderType getBorder() {
//		return this.pageData.getPage().getBorder();
//	}
	
	public TrpPageType getPage() {
		if (pageData != null)
			return (TrpPageType) this.pageData.getPage();
		else 
			return null;
	}
		
//	public TrpPrintSpaceType getPrintSpace() {
//		if (pageData != null)
//			return (TrpPrintSpaceType) pageData.getPage().getPrintSpace();
//		else
//			return null;
//	}
	
//	public void build() throws JAXBException, IOException {
//		if (md != null) {
//			JAXBPageTranscript tr = TrpPageTranscriptBuilder.build(md);
//			setPageData(tr.getPageData());
//		}
//	}
	
	public void build() throws IOException {
		try {
			PcGtsType pageData = PageXmlUtils.unmarshal(md, true);
			setPageData(pageData);
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}
	
	public void write(File fileOut) throws IOException {
		if (pageData == null) {
			throw new IOException("PageData not set - use build() method to unmarshall object!");
		}

		try {
			PageXmlUtils.marshalToFile(pageData, fileOut);
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}
}
