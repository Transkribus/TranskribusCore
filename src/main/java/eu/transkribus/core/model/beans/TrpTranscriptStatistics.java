package eu.transkribus.core.model.beans;

import javax.persistence.Column;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Statistics object that can be extracted from all classes extending ATranscriptStatistics
 * 
 * @author philip
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpTranscriptStatistics extends ATranscriptStatistics {
	
	@Column(name=N_REGIONS_COL_NAME)
	protected Integer nrOfRegions;

	@Column(name=N_TRANSCRIBED_REGIONS_COL_NAME)
	protected Integer nrOfTranscribedRegions;

	@Column(name=N_WORDS_IN_REGIONS_COL_NAME)
	protected Integer nrOfWordsInRegions;
	
	@Column(name=N_LINES_COL_NAME)
	protected Integer nrOfLines;

	@Column(name=N_TRANSCRIBED_LINES_COL_NAME)
	protected Integer nrOfTranscribedLines;

	@Column(name=N_WORDS_IN_LINES_COL_NAME)
	protected Integer nrOfWordsInLines;

	@Column(name=N_WORDS_COL_NAME)
	protected Integer nrOfWords;

	@Column(name=N_TRANSCRIBED_WORDS_COL_NAME)
	protected Integer nrOfTranscribedWords;
	
	public Integer getNrOfRegions() {
		return nrOfRegions;
	}
	public void setNrOfRegions(Integer nrOfRegions) {
		this.nrOfRegions = nrOfRegions;
	}
	public Integer getNrOfTranscribedRegions() {
		return nrOfTranscribedRegions;
	}
	public void setNrOfTranscribedRegions(Integer nrOfTranscribedRegions) {
		this.nrOfTranscribedRegions = nrOfTranscribedRegions;
	}
	public Integer getNrOfWordsInRegions() {
		return nrOfWordsInRegions;
	}
	public void setNrOfWordsInRegions(Integer nrOfWordsInRegions) {
		this.nrOfWordsInRegions = nrOfWordsInRegions;
	}
	public Integer getNrOfLines() {
		return nrOfLines;
	}
	public void setNrOfLines(Integer nrOfLines) {
		this.nrOfLines = nrOfLines;
	}
	public Integer getNrOfTranscribedLines() {
		return nrOfTranscribedLines;
	}
	public void setNrOfTranscribedLines(Integer nrOfTranscribedLines) {
		this.nrOfTranscribedLines = nrOfTranscribedLines;
	}
	public Integer getNrOfWordsInLines() {
		return nrOfWordsInLines;
	}
	public void setNrOfWordsInLines(Integer nrOfWordsInLines) {
		this.nrOfWordsInLines = nrOfWordsInLines;
	}
	public Integer getNrOfWords() {
		return nrOfWords;
	}
	public void setNrOfWords(Integer nrOfWords) {
		this.nrOfWords = nrOfWords;
	}
	public Integer getNrOfTranscribedWords() {
		return nrOfTranscribedWords;
	}
	public void setNrOfTranscribedWords(Integer nrOfTranscribedWords) {
		this.nrOfTranscribedWords = nrOfTranscribedWords;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nrOfLines == null) ? 0 : nrOfLines.hashCode());
		result = prime * result + ((nrOfRegions == null) ? 0 : nrOfRegions.hashCode());
		result = prime * result + ((nrOfTranscribedLines == null) ? 0 : nrOfTranscribedLines.hashCode());
		result = prime * result + ((nrOfTranscribedRegions == null) ? 0 : nrOfTranscribedRegions.hashCode());
		result = prime * result + ((nrOfTranscribedWords == null) ? 0 : nrOfTranscribedWords.hashCode());
		result = prime * result + ((nrOfWords == null) ? 0 : nrOfWords.hashCode());
		result = prime * result + ((nrOfWordsInLines == null) ? 0 : nrOfWordsInLines.hashCode());
		result = prime * result + ((nrOfWordsInRegions == null) ? 0 : nrOfWordsInRegions.hashCode());
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
		TrpTranscriptStatistics other = (TrpTranscriptStatistics) obj;
		if (nrOfLines == null) {
			if (other.nrOfLines != null)
				return false;
		} else if (!nrOfLines.equals(other.nrOfLines))
			return false;
		if (nrOfRegions == null) {
			if (other.nrOfRegions != null)
				return false;
		} else if (!nrOfRegions.equals(other.nrOfRegions))
			return false;
		if (nrOfTranscribedLines == null) {
			if (other.nrOfTranscribedLines != null)
				return false;
		} else if (!nrOfTranscribedLines.equals(other.nrOfTranscribedLines))
			return false;
		if (nrOfTranscribedRegions == null) {
			if (other.nrOfTranscribedRegions != null)
				return false;
		} else if (!nrOfTranscribedRegions.equals(other.nrOfTranscribedRegions))
			return false;
		if (nrOfTranscribedWords == null) {
			if (other.nrOfTranscribedWords != null)
				return false;
		} else if (!nrOfTranscribedWords.equals(other.nrOfTranscribedWords))
			return false;
		if (nrOfWords == null) {
			if (other.nrOfWords != null)
				return false;
		} else if (!nrOfWords.equals(other.nrOfWords))
			return false;
		if (nrOfWordsInLines == null) {
			if (other.nrOfWordsInLines != null)
				return false;
		} else if (!nrOfWordsInLines.equals(other.nrOfWordsInLines))
			return false;
		if (nrOfWordsInRegions == null) {
			if (other.nrOfWordsInRegions != null)
				return false;
		} else if (!nrOfWordsInRegions.equals(other.nrOfWordsInRegions))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TrpTranscriptStatistics [nrOfRegions=" + nrOfRegions + ", nrOfTranscribedRegions="
				+ nrOfTranscribedRegions + ", nrOfWordsInRegions=" + nrOfWordsInRegions + ", nrOfLines=" + nrOfLines
				+ ", nrOfTranscribedLines=" + nrOfTranscribedLines + ", nrOfWordsInLines=" + nrOfWordsInLines
				+ ", nrOfWords=" + nrOfWords + ", nrOfTranscribedWords=" + nrOfTranscribedWords + "]";
	}
}