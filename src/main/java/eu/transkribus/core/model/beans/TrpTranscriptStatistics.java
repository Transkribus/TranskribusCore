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
	public String toString() {
		return "TrpTranscriptStatistics [nrOfRegions=" + nrOfRegions + ", nrOfTranscribedRegions="
				+ nrOfTranscribedRegions + ", nrOfWordsInRegions=" + nrOfWordsInRegions + ", nrOfLines=" + nrOfLines
				+ ", nrOfTranscribedLines=" + nrOfTranscribedLines + ", nrOfWordsInLines=" + nrOfWordsInLines
				+ ", nrOfWords=" + nrOfWords + ", nrOfTranscribedWords=" + nrOfTranscribedWords + "]";
	}
}