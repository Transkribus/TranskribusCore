package eu.transkribus.core.model.beans;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.util.PageXmlUtils;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class JAXBPageTranscript extends AbstractPageTranscript<PcGtsType> {
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
		super.setMd(md);
	}
		
	@Override public void setPageData(PcGtsType page) {
		super.setPageData(page);
		updateMdOnTrpPageType();
	}
	
	void updateMdOnTrpPageType() {
		if (md!=null && getPage()!=null)
			getPage().setMd(md);
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
		if (md == null)
			throw new IOException("Metadata is null!");
		
		PcGtsType pageData;
		URL url = md.getUrl();
		if (url != null) {
			try {
				if(url.getProtocol().startsWith("file")){
					final String path = url.toExternalForm().replaceFirst("file:", "");
					pageData = PageXmlUtils.unmarshal(new File(path));
				} else {
					pageData = PageXmlUtils.unmarshal(url);
				}
			} catch (JAXBException e) {
				throw new IOException(e);
			}
		}
		else
			pageData = PageXmlUtils.createEmptyPcGtsType(md.getPagePageReferenceForLocalDocs().getUrl());
		
		setPageData(pageData);
	}
}
