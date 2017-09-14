package eu.transkribus.core.model.beans;

import javax.persistence.Column;
import javax.persistence.Transient;

/**
 * Extension of ATranscribable that defines Transient fields that are filled upon selects from the DB
 * 
 * @author philip
 *
 */
public abstract class ATransientTranscribable extends ATranscribable {
	
	@Transient
	@Column(name="NR_OF_REGIONS")
	protected int nrOfRegions;

	@Transient
	@Column(name="NR_OF_TRANSCRIBED_REGIONS")
	protected int nrOfTranscribedRegions;

	@Transient
	@Column(name="NR_OF_WORDS_IN_REGIONS")
	protected int nrOfWordsInRegions;
	
	@Transient
	@Column(name="NR_OF_LINES")
	protected int nrOfLines;

	@Transient
	@Column(name="NR_OF_TRANSCRIBED_LINES")
	protected int nrOfTranscribedLines;

	@Transient
	@Column(name="NR_OF_WORDS_IN_LINES")
	protected int nrOfWordsInLines;

	@Transient
	@Column(name="NR_OF_WORDS")
	protected int nrOfWords;

	@Transient
	@Column(name="NR_OF_TRANSCRIBED_WORDS")
	protected int nrOfTranscribedWords;

	@Override
	public int getNrOfRegions() {
		return nrOfRegions;
	}

	@Override
	public void setNrOfRegions(int nrOfRegions) {
		this.nrOfRegions = nrOfRegions;
	}

	@Override
	public int getNrOfTranscribedRegions() {
		return nrOfTranscribedRegions;
	}

	@Override
	public void setNrOfTranscribedRegions(int nrOfTranscribedRegions) {
		this.nrOfTranscribedRegions = nrOfTranscribedRegions;
	}

	@Override
	public int getNrOfWordsInRegions() {
		return nrOfWordsInRegions;
	}

	@Override
	public void setNrOfWordsInRegions(int nrOfWordsInRegions) {
		this.nrOfWordsInRegions = nrOfWordsInRegions;
	}

	@Override
	public int getNrOfLines() {
		return nrOfLines;
	}

	@Override
	public void setNrOfLines(int nrOfLines) {
		this.nrOfLines = nrOfLines;
	}

	@Override
	public int getNrOfTranscribedLines() {
		return nrOfTranscribedLines;
	}

	@Override
	public void setNrOfTranscribedLines(int nrOfTranscribedLines) {
		this.nrOfTranscribedLines = nrOfTranscribedLines;
	}

	@Override
	public int getNrOfWordsInLines() {
		return nrOfWordsInLines;
	}

	@Override
	public void setNrOfWordsInLines(int nrOfWordsInLines) {
		this.nrOfWordsInLines = nrOfWordsInLines;
	}

	@Override
	public int getNrOfWords() {
		return nrOfWords;
	}

	@Override
	public void setNrOfWords(int nrOfWords) {
		this.nrOfWords = nrOfWords;
	}

	@Override
	public int getNrOfTranscribedWords() {
		return nrOfTranscribedWords;
	}

	@Override
	public void setNrOfTranscribedWords(int nrOfTranscribedWords) {
		this.nrOfTranscribedWords = nrOfTranscribedWords;
	}

	

}
