package eu.transkribus.core.model.builder.pdf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.dea.fimgstoreclient.beans.ImgType;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PdfExportPars {
	public static final String PARAMETER_KEY = "pdfPars";
	
	boolean doPdfImagesOnly=false;
	boolean doPdfImagesPlusText=true;
	boolean doPdfWithTextPages=false;
	boolean doPdfWithTags=false; 
	boolean doPdfWithArticles=false;
	
	ImgType pdfImgQuality = ImgType.view;
	
	public PdfExportPars() {
		
	}

	public PdfExportPars(boolean doPdfImagesOnly, boolean doPdfImagesPlusText, boolean doPdfWithTextPages,
			boolean doPdfWithTags, boolean doPdfWithArticles) {
		super();
		this.doPdfImagesOnly = doPdfImagesOnly;
		this.doPdfImagesPlusText = doPdfImagesPlusText;
		this.doPdfWithTextPages = doPdfWithTextPages;
		this.doPdfWithTags = doPdfWithTags;
		this.doPdfWithArticles = doPdfWithArticles;
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
	
	public boolean isDoPdfWithArticles() {
		return doPdfWithArticles;
	}

	public void setDoPdfWithArticles(boolean doPdfWithArticles) {
		this.doPdfWithArticles = doPdfWithArticles;
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
				+ ", doPdfWithTextPages=" + doPdfWithTextPages + ", doPdfWithTags=" + doPdfWithTags + ", doPdfWithArticles=" + doPdfWithArticles + ", pdfImgQuality="
				+ pdfImgQuality + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (doPdfImagesOnly ? 1231 : 1237);
		result = prime * result + (doPdfImagesPlusText ? 1231 : 1237);
		result = prime * result + (doPdfWithTags ? 1231 : 1237);
		result = prime * result + (doPdfWithArticles ? 1231 : 1237);
		result = prime * result + (doPdfWithTextPages ? 1231 : 1237);
		result = prime * result + ((pdfImgQuality == null) ? 0 : pdfImgQuality.hashCode());
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
		PdfExportPars other = (PdfExportPars) obj;
		if (doPdfImagesOnly != other.doPdfImagesOnly)
			return false;
		if (doPdfImagesPlusText != other.doPdfImagesPlusText)
			return false;
		if (doPdfWithTags != other.doPdfWithTags)
			return false;
		if (isDoPdfWithArticles() != other.doPdfWithArticles)
			return false;
		if (doPdfWithTextPages != other.doPdfWithTextPages)
			return false;
		if (pdfImgQuality != other.pdfImgQuality)
			return false;
		return true;
	}
}
