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
public abstract class ATotalTranscriptStatistics extends ATranscriptStatistics {
		
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
	
	@Transient
	@Column(name="NUMBER_STATUS_NEW")
	protected Integer nrOfNew;
	
	@Transient
	@Column(name="NUMBER_STATUS_INPROGRESS")
	protected Integer nrOfInProgress;
	
	@Transient
	@Column(name="NUMBER_STATUS_DONE")
	protected Integer nrOfDone;
	
	@Transient
	@Column(name="NUMBER_STATUS_FINAL")
	protected Integer nrOfFinal;
	
	@Transient
	@Column(name="NUMBER_STATUS_GT")
	protected Integer nrOfGT;

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

	public Integer getNrOfNew() {
		return nrOfNew;
	}

	public void setNrOfNew(Integer nrOfNew) {
		this.nrOfNew = nrOfNew;
	}

	public Integer getNrOfInProgress() {
		return nrOfInProgress;
	}

	public void setNrOfInProgress(Integer nrOfInProgress) {
		this.nrOfInProgress = nrOfInProgress;
	}

	public Integer getNrOfDone() {
		return nrOfDone;
	}

	public void setNrOfDone(Integer nrOfDone) {
		this.nrOfDone = nrOfDone;
	}

	public Integer getNrOfFinal() {
		return nrOfFinal;
	}

	public void setNrOfFinal(Integer nrOfFinal) {
		this.nrOfFinal = nrOfFinal;
	}

	public Integer getNrOfGT() {
		return nrOfGT;
	}

	public void setNrOfGT(Integer nrOfGT) {
		this.nrOfGT = nrOfGT;
	}
	
	public TrpTotalTranscriptStatistics getTotalStats() {
		TrpTotalTranscriptStatistics s = new TrpTotalTranscriptStatistics();
		s.setStats(this.getStats());
		s.setNrOfNew(this.getNrOfNew());
		s.setNrOfInProgress(this.getNrOfInProgress());
		s.setNrOfDone(this.getNrOfDone());
		s.setNrOfFinal(this.getNrOfFinal());
		s.setNrOfGT(this.getNrOfGT());
		return s;
	}
	
	public void setTotalStats(TrpTotalTranscriptStatistics s) {
		
		this.setStats(s.getStats());
		this.setNrOfNew(s.getNrOfNew());
		this.setNrOfInProgress(s.getNrOfInProgress());
		this.setNrOfDone(s.getNrOfDone());
		this.setNrOfFinal(s.getNrOfFinal());
		this.setNrOfGT(s.getNrOfGT());
	}

	

}
