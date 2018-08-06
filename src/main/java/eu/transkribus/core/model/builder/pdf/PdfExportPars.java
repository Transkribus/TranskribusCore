package eu.transkribus.core.model.builder.pdf;

import org.dea.fimgstoreclient.beans.ImgType;

public class PdfExportPars {
	public static final String PARAMETER_KEY = "pdfPars";
	
	boolean doPdfImagesOnly=false;
	boolean doPdfImagesPlusText=true;
	boolean doPdfWithTextPages=false;
	boolean doPdfWithTags=false; 
	
	ImgType pdfImgQuality = ImgType.view;
	
	public PdfExportPars() {
		
	}

	public PdfExportPars(boolean doPdfImagesOnly, boolean doPdfImagesPlusText, boolean doPdfWithTextPages,
			boolean doPdfWithTags) {
		super();
		this.doPdfImagesOnly = doPdfImagesOnly;
		this.doPdfImagesPlusText = doPdfImagesPlusText;
		this.doPdfWithTextPages = doPdfWithTextPages;
		this.doPdfWithTags = doPdfWithTags;
	}



	public boolean isDoPdfImagesOnly() {
		return doPdfImagesOnly;
	}

	public void setDoPdfImagesOnly(boolean doPdfImagesOnly) {
		this.doPdfImagesOnly = doPdfImagesOnly;
	}

	public boolean isDoPdfImagesPlusText() {
		return doPdfImagesPlusText;
	}

	public void setDoPdfImagesPlusText(boolean doPdfImagesPlusText) {
		this.doPdfImagesPlusText = doPdfImagesPlusText;
	}

	public boolean isDoPdfWithTextPages() {
		return doPdfWithTextPages;
	}

	public void setDoPdfWithTextPages(boolean doPdfWithTextPages) {
		this.doPdfWithTextPages = doPdfWithTextPages;
	}

	public boolean isDoPdfWithTags() {
		return doPdfWithTags;
	}

	public void setDoPdfWithTags(boolean doPdfWithTags) {
		this.doPdfWithTags = doPdfWithTags;
	}
	
	public ImgType getPdfImgQuality() {
		return pdfImgQuality;
	}

	public void setPdfImgQuality(ImgType pdfImgQuality) {
		this.pdfImgQuality = pdfImgQuality;
	}

	@Override
	public String toString() {
		return "PdfExportPars [doPdfImagesOnly=" + doPdfImagesOnly + ", doPdfImagesPlusText=" + doPdfImagesPlusText
				+ ", doPdfWithTextPages=" + doPdfWithTextPages + ", doPdfWithTags=" + doPdfWithTags + ", pdfImgQuality="
				+ pdfImgQuality + "]";
	}
	
	
	
	

}
