package eu.transkribus.core.model.builder.docx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DocxExportPars {
	public static final String PARAMETER_KEY = "docxPars";
	
	boolean doDocxWithTags=false;
	boolean doDocxPreserveLineBreaks=false;
	boolean doDocxForcePageBreaks=false;
	boolean doDocxMarkUnclear=false;
	boolean doDocxKeepAbbrevs=true;
	boolean doDocxExpandAbbrevs=false;
	boolean doDocxSubstituteAbbrevs=false;
	
	public DocxExportPars() {
	}

	public DocxExportPars(boolean doDocxWithTags, boolean doDocxPreserveLineBreaks, boolean doDocxForcePageBreaks, boolean doDocxMarkUnclear,
			boolean doDocxKeepAbbrevs, boolean doDocxExpandAbbrevs, boolean doDocxSubstituteAbbrevs) {
		super();
		this.doDocxWithTags = doDocxWithTags;
		this.doDocxPreserveLineBreaks = doDocxPreserveLineBreaks;
		this.doDocxForcePageBreaks = doDocxForcePageBreaks;
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

	public boolean isDoDocxForcePageBreaks() {
		return doDocxForcePageBreaks;
	}

	public void setDoDocxForcePageBreaks(boolean doDocxForcePageBreaks) {
		this.doDocxForcePageBreaks = doDocxForcePageBreaks;
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
				+ doDocxPreserveLineBreaks + ", doDocxForcePageBreaks=" + doDocxForcePageBreaks + ", doDocxMarkUnclear="
				+ doDocxMarkUnclear + ", doDocxKeepAbbrevs=" + doDocxKeepAbbrevs + ", doDocxExpandAbbrevs="
				+ doDocxExpandAbbrevs + ", doDocxSubstituteAbbrevs=" + doDocxSubstituteAbbrevs + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (doDocxExpandAbbrevs ? 1231 : 1237);
		result = prime * result + (doDocxForcePageBreaks ? 1231 : 1237);
		result = prime * result + (doDocxKeepAbbrevs ? 1231 : 1237);
		result = prime * result + (doDocxMarkUnclear ? 1231 : 1237);
		result = prime * result + (doDocxPreserveLineBreaks ? 1231 : 1237);
		result = prime * result + (doDocxSubstituteAbbrevs ? 1231 : 1237);
		result = prime * result + (doDocxWithTags ? 1231 : 1237);
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
		DocxExportPars other = (DocxExportPars) obj;
		if (doDocxExpandAbbrevs != other.doDocxExpandAbbrevs)
			return false;
		if (doDocxForcePageBreaks != other.doDocxForcePageBreaks)
			return false;
		if (doDocxKeepAbbrevs != other.doDocxKeepAbbrevs)
			return false;
		if (doDocxMarkUnclear != other.doDocxMarkUnclear)
			return false;
		if (doDocxPreserveLineBreaks != other.doDocxPreserveLineBreaks)
			return false;
		if (doDocxSubstituteAbbrevs != other.doDocxSubstituteAbbrevs)
			return false;
		if (doDocxWithTags != other.doDocxWithTags)
			return false;
		return true;
	}
}
