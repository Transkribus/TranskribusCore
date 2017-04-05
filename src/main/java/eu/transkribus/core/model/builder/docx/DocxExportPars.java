package eu.transkribus.core.model.builder.docx;

public class DocxExportPars {
	public static final String PARAMETER_KEY = "docxPars";
	
	boolean doDocxWithTags=false;
	boolean doDocxPreserveLineBreaks=false;
	boolean doDocxMarkUnclear=false;
	boolean doDocxKeepAbbrevs=true;
	boolean doDocxExpandAbbrevs=false;
	boolean doDocxSubstituteAbbrevs=false;
	
	public DocxExportPars() {
	}

	public DocxExportPars(boolean doDocxWithTags, boolean doDocxPreserveLineBreaks, boolean doDocxMarkUnclear,
			boolean doDocxKeepAbbrevs, boolean doDocxExpandAbbrevs, boolean doDocxSubstituteAbbrevs) {
		super();
		this.doDocxWithTags = doDocxWithTags;
		this.doDocxPreserveLineBreaks = doDocxPreserveLineBreaks;
		this.doDocxMarkUnclear = doDocxMarkUnclear;
		this.doDocxKeepAbbrevs = doDocxKeepAbbrevs;
		this.doDocxExpandAbbrevs = doDocxExpandAbbrevs;
		this.doDocxSubstituteAbbrevs = doDocxSubstituteAbbrevs;
	}

	public boolean isDoDocxWithTags() {
		return doDocxWithTags;
	}

	public void setDoDocxWithTags(boolean doDocxWithTags) {
		this.doDocxWithTags = doDocxWithTags;
	}

	public boolean isDoDocxPreserveLineBreaks() {
		return doDocxPreserveLineBreaks;
	}

	public void setDoDocxPreserveLineBreaks(boolean doDocxPreserveLineBreaks) {
		this.doDocxPreserveLineBreaks = doDocxPreserveLineBreaks;
	}

	public boolean isDoDocxMarkUnclear() {
		return doDocxMarkUnclear;
	}

	public void setDoDocxMarkUnclear(boolean doDocxMarkUnclear) {
		this.doDocxMarkUnclear = doDocxMarkUnclear;
	}

	public boolean isDoDocxKeepAbbrevs() {
		return doDocxKeepAbbrevs;
	}

	public void setDoDocxKeepAbbrevs(boolean doDocxKeepAbbrevs) {
		this.doDocxKeepAbbrevs = doDocxKeepAbbrevs;
	}

	public boolean isDoDocxExpandAbbrevs() {
		return doDocxExpandAbbrevs;
	}

	public void setDoDocxExpandAbbrevs(boolean doDocxExpandAbbrevs) {
		this.doDocxExpandAbbrevs = doDocxExpandAbbrevs;
	}

	public boolean isDoDocxSubstituteAbbrevs() {
		return doDocxSubstituteAbbrevs;
	}

	public void setDoDocxSubstituteAbbrevs(boolean doDocxSubstituteAbbrevs) {
		this.doDocxSubstituteAbbrevs = doDocxSubstituteAbbrevs;
	}

	@Override
	public String toString() {
		return "DocxExportPars [doDocxWithTags=" + doDocxWithTags + ", doDocxPreserveLineBreaks="
				+ doDocxPreserveLineBreaks + ", doDocxMarkUnclear=" + doDocxMarkUnclear + ", doDocxKeepAbbrevs="
				+ doDocxKeepAbbrevs + ", doDocxExpandAbbrevs=" + doDocxExpandAbbrevs + ", doDocxSubstituteAbbrevs="
				+ doDocxSubstituteAbbrevs + "]";
	}
	
	

}
