package eu.transkribus.core.model.builder.pdf;

public class PdfExportPars {
	public static final String PARAMETER_KEY = "pdfPars";
	
	boolean doPdfImagesOnly=false;
	boolean doPdfImagesPlusText=true;
	boolean doPdfWithTextPages=false;
	boolean doPdfWithTags=false; 
	
	public PdfExportPars() {
		
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

	@Override
	public String toString() {
		return "PdfExportPars [doPdfImagesOnly=" + doPdfImagesOnly + ", doPdfImagesPlusText=" + doPdfImagesPlusText
				+ ", doPdfWithTextPages=" + doPdfWithTextPages + ", doPdfWithTags=" + doPdfWithTags + "]";
	}
	
	
	
	

}
