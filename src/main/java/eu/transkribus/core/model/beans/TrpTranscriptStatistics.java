package eu.transkribus.core.model.beans;

public class TrpTranscriptStatistics extends ATranscriptStatistics {
	private int nrOfRegions = 0;
	private int nrOfTranscribedRegions = 0;
	private int nrOfWordsInRegions = 0;
	private int nrOfLines = 0;
	private int nrOfTranscribedLines = 0;
	private int nrOfWordsInLines = 0;
	private int nrOfWords = 0;
	private int nrOfTranscribedWords = 0;
	
	public int getNrOfRegions() {
		return nrOfRegions;
	}
	public void setNrOfRegions(int nrOfRegions) {
		this.nrOfRegions = nrOfRegions;
	}
	public int getNrOfTranscribedRegions() {
		return nrOfTranscribedRegions;
	}
	public void setNrOfTranscribedRegions(int nrOfTranscribedRegions) {
		this.nrOfTranscribedRegions = nrOfTranscribedRegions;
	}
	public int getNrOfWordsInRegions() {
		return nrOfWordsInRegions;
	}
	public void setNrOfWordsInRegions(int nrOfWordsInRegions) {
		this.nrOfWordsInRegions = nrOfWordsInRegions;
	}
	public int getNrOfLines() {
		return nrOfLines;
	}
	public void setNrOfLines(int nrOfLines) {
		this.nrOfLines = nrOfLines;
	}
	public int getNrOfTranscribedLines() {
		return nrOfTranscribedLines;
	}
	public void setNrOfTranscribedLines(int nrOfTranscribedLines) {
		this.nrOfTranscribedLines = nrOfTranscribedLines;
	}
	public int getNrOfWordsInLines() {
		return nrOfWordsInLines;
	}
	public void setNrOfWordsInLines(int nrOfWordsInLines) {
		this.nrOfWordsInLines = nrOfWordsInLines;
	}
	public int getNrOfWords() {
		return nrOfWords;
	}
	public void setNrOfWords(int nrOfWords) {
		this.nrOfWords = nrOfWords;
	}
	public int getNrOfTranscribedWords() {
		return nrOfTranscribedWords;
	}
	public void setNrOfTranscribedWords(int nrOfTranscribedWords) {
		this.nrOfTranscribedWords = nrOfTranscribedWords;
	}
}