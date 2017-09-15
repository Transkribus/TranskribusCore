package eu.transkribus.core.model.beans;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Extension of ATranscriptStatistics that defines Transient fields which are filled upon selects from the DB
 * 
 * @author philip
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ATransientTranscriptStatistics extends ATranscriptStatistics {
	
	@Transient
	@Column(name=N_REGIONS_COL_NAME)
	protected Integer nrOfRegions;

	@Transient
	@Column(name=N_TRANSCRIBED_REGIONS_COL_NAME)
	protected Integer nrOfTranscribedRegions;

	@Transient
	@Column(name=N_WORDS_IN_REGIONS_COL_NAME)
	protected Integer nrOfWordsInRegions;
	
	@Transient
	@Column(name=N_LINES_COL_NAME)
	protected Integer nrOfLines;

	@Transient
	@Column(name=N_TRANSCRIBED_LINES_COL_NAME)
	protected Integer nrOfTranscribedLines;

	@Transient
	@Column(name=N_WORDS_IN_LINES_COL_NAME)
	protected Integer nrOfWordsInLines;

	@Transient
	@Column(name=N_WORDS_COL_NAME)
	protected Integer nrOfWords;

	@Transient
	@Column(name=N_TRANSCRIBED_WORDS_COL_NAME)
	protected Integer nrOfTranscribedWords;

	@Override
	public Integer getNrOfRegions() {
		return nrOfRegions;
	}

	@Override
	public void setNrOfRegions(Integer nrOfRegions) {
		this.nrOfRegions = nrOfRegions;
	}

	@Override
	public Integer getNrOfTranscribedRegions() {
		return nrOfTranscribedRegions;
	}

	@Override
	public void setNrOfTranscribedRegions(Integer nrOfTranscribedRegions) {
		this.nrOfTranscribedRegions = nrOfTranscribedRegions;
	}

	@Override
	public Integer getNrOfWordsInRegions() {
		return nrOfWordsInRegions;
	}

	@Override
	public void setNrOfWordsInRegions(Integer nrOfWordsInRegions) {
		this.nrOfWordsInRegions = nrOfWordsInRegions;
	}

	@Override
	public Integer getNrOfLines() {
		return nrOfLines;
	}

	@Override
	public void setNrOfLines(Integer nrOfLines) {
		this.nrOfLines = nrOfLines;
	}

	@Override
	public Integer getNrOfTranscribedLines() {
		return nrOfTranscribedLines;
	}

	@Override
	public void setNrOfTranscribedLines(Integer nrOfTranscribedLines) {
		this.nrOfTranscribedLines = nrOfTranscribedLines;
	}

	@Override
	public Integer getNrOfWordsInLines() {
		return nrOfWordsInLines;
	}

	@Override
	public void setNrOfWordsInLines(Integer nrOfWordsInLines) {
		this.nrOfWordsInLines = nrOfWordsInLines;
	}

	@Override
	public Integer getNrOfWords() {
		return nrOfWords;
	}

	@Override
	public void setNrOfWords(Integer nrOfWords) {
		this.nrOfWords = nrOfWords;
	}

	@Override
	public Integer getNrOfTranscribedWords() {
		return nrOfTranscribedWords;
	}

	@Override
	public void setNrOfTranscribedWords(Integer nrOfTranscribedWords) {
		this.nrOfTranscribedWords = nrOfTranscribedWords;
	}

	

}
