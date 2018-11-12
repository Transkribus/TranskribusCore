package eu.transkribus.core.model.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.builder.CommonExportPars;
import eu.transkribus.core.model.builder.alto.AltoExportPars;
import eu.transkribus.core.model.builder.docx.DocxExportPars;
import eu.transkribus.core.model.builder.pdf.PdfExportPars;
import eu.transkribus.core.model.builder.tei.TeiExportPars;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ExportParameters {

	private CommonExportPars commonPars;
	private AltoExportPars altoPars;
	private PdfExportPars pdfPars;
	private TeiExportPars teiPars;
	private DocxExportPars docxPars;
	
	@XmlElementWrapper(name = "docs")
	@XmlElement(name = "doc")
	private List<DocumentSelectionDescriptor> docDescriptorList;
	
	public ExportParameters() {
		commonPars = null;
		altoPars = null;
		pdfPars = null;
		teiPars = null;
		docxPars = null;
		docDescriptorList = null;
	}

	public CommonExportPars getCommonPars() {
		return commonPars;
	}

	public void setCommonPars(CommonExportPars commonPars) {
		this.commonPars = commonPars;
	}

	public AltoExportPars getAltoPars() {
		return altoPars;
	}

	public void setAltoPars(AltoExportPars altoPars) {
		this.altoPars = altoPars;
	}

	public PdfExportPars getPdfPars() {
		return pdfPars;
	}

	public void setPdfPars(PdfExportPars pdfPars) {
		this.pdfPars = pdfPars;
	}

	public TeiExportPars getTeiPars() {
		return teiPars;
	}

	public void setTeiPars(TeiExportPars teiPars) {
		this.teiPars = teiPars;
	}

	public DocxExportPars getDocxPars() {
		return docxPars;
	}

	public void setDocxPars(DocxExportPars docxPars) {
		this.docxPars = docxPars;
	}

	public List<DocumentSelectionDescriptor> getDocDescriptorList() {
		return docDescriptorList;
	}

	public void setDocDescriptorList(List<DocumentSelectionDescriptor> docDescriptorList) {
		this.docDescriptorList = docDescriptorList;
	}

	@Override
	public String toString() {
		return "ExportParameters [commonPars=" + commonPars + ", altoPars=" + altoPars + ", pdfPars=" + pdfPars
				+ ", teiPars=" + teiPars + ", docxPars=" + docxPars + "]";
	}
}
