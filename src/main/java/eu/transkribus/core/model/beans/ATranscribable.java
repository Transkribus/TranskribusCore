package eu.transkribus.core.model.beans;

/**
 * Abstract class that defines getters and setters for text content objects such as collections, documents or transcripts
 * 
 * @author philip
 *
 */
public abstract class ATranscribable {
	
	public TrpTranscriptStatistics getStats() {
		TrpTranscriptStatistics s = new TrpTranscriptStatistics();
		s.setNrOfLines(this.getNrOfLines());
		s.setNrOfRegions(this.getNrOfRegions());
		s.setNrOfTranscribedLines(this.getNrOfTranscribedLines());
		s.setNrOfTranscribedRegions(this.getNrOfTranscribedRegions());
		s.setNrOfTranscribedWords(this.getNrOfTranscribedWords());
		s.setNrOfWords(this.getNrOfWords());
		s.setNrOfWordsInLines(this.getNrOfWordsInLines());
		s.setNrOfWordsInRegions(this.getNrOfWordsInRegions());
		return s;
	}
	
	public void setStats(TrpTranscriptStatistics s) {
		this.setNrOfLines(s.getNrOfLines());
		this.setNrOfRegions(s.getNrOfRegions());
		this.setNrOfTranscribedLines(s.getNrOfTranscribedLines());
		this.setNrOfTranscribedRegions(s.getNrOfTranscribedRegions());
		this.setNrOfTranscribedWords(s.getNrOfTranscribedWords());
		this.setNrOfWords(s.getNrOfWords());
		this.setNrOfWordsInLines(s.getNrOfWordsInLines());
		this.setNrOfWordsInRegions(s.getNrOfWordsInRegions());
	}

	public abstract int getNrOfRegions();
	public abstract void setNrOfRegions(int nrOfRegions);
	public abstract int getNrOfTranscribedRegions();
	public abstract void setNrOfTranscribedRegions(int nrOfTranscribedRegions);
	public abstract int getNrOfWordsInRegions();
	public abstract void setNrOfWordsInRegions(int nrOfWordsInRegions);
	public abstract int getNrOfLines();
	public abstract void setNrOfLines(int nrOfLines);
	public abstract int getNrOfTranscribedLines();
	public abstract void setNrOfTranscribedLines(int nrOfTranscribedLines);
	public abstract int getNrOfWordsInLines();
	public abstract void setNrOfWordsInLines(int nrOfWordsInLines);
	public abstract int getNrOfWords();
	public abstract void setNrOfWords(int nrOfWords);
	public abstract int getNrOfTranscribedWords();
	public abstract void setNrOfTranscribedWords(int nrOfTranscribedWords);
}
