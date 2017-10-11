package eu.transkribus.core.model.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Abstract class that defines getters and setters for text content objects such as collections, documents or transcripts
 * 
 * @author philip
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class ATranscriptStatistics {
	
	@XmlTransient
	public static final String N_REGIONS_COL_NAME = "NR_OF_REGIONS";
	@XmlTransient
	public static final String N_TRANSCRIBED_REGIONS_COL_NAME = "NR_OF_TRANSCRIBED_REGIONS";
	@XmlTransient
	public static final String N_WORDS_IN_REGIONS_COL_NAME = "NR_OF_WORDS_IN_REGIONS";
	@XmlTransient
	public static final String N_LINES_COL_NAME = "NR_OF_LINES";
	@XmlTransient
	public static final String N_TRANSCRIBED_LINES_COL_NAME = "NR_OF_TRANSCRIBED_LINES";
	@XmlTransient
	public static final String N_WORDS_IN_LINES_COL_NAME = "NR_OF_WORDS_IN_LINES";
	@XmlTransient
	public static final String N_WORDS_COL_NAME = "NR_OF_WORDS";
	@XmlTransient
	public static final String N_TRANSCRIBED_WORDS_COL_NAME = "NR_OF_TRANSCRIBED_WORDS";

	@XmlTransient
	public static final String[] COL_NAMES = {
			N_REGIONS_COL_NAME, 
			N_TRANSCRIBED_REGIONS_COL_NAME,
			N_WORDS_IN_REGIONS_COL_NAME,
			N_LINES_COL_NAME,
			N_TRANSCRIBED_LINES_COL_NAME,
			N_WORDS_IN_LINES_COL_NAME,
			N_WORDS_COL_NAME,
			N_TRANSCRIBED_WORDS_COL_NAME
	};
	
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
	
	public void add(TrpTranscriptStatistics s) {
		this.setNrOfLines(sum(this.getNrOfLines(), s.getNrOfLines()));
		this.setNrOfRegions(sum(this.getNrOfRegions(), s.getNrOfRegions()));
		this.setNrOfTranscribedLines(sum(this.getNrOfTranscribedLines(), s.getNrOfTranscribedLines()));
		this.setNrOfTranscribedRegions(sum(this.getNrOfTranscribedRegions(), s.getNrOfTranscribedRegions()));
		this.setNrOfTranscribedWords(sum(this.getNrOfTranscribedWords(), s.getNrOfTranscribedWords()));
		this.setNrOfWords(sum(this.getNrOfWords(), s.getNrOfWords()));
		this.setNrOfWordsInLines(sum(this.getNrOfWordsInLines(), s.getNrOfWordsInLines()));
		this.setNrOfWordsInRegions(sum(this.getNrOfWordsInRegions(), s.getNrOfWordsInRegions()));
	}
	
	private Integer sum(Integer a, Integer b){
		if(a == null && b == null) {
			return null;
		}
		if(a == null) {
			return b;
		}
		if(b == null) {
			return a;
		}
		return a + b;
	}
	

	public abstract Integer getNrOfRegions();
	public abstract void setNrOfRegions(Integer nrOfRegions);
	public abstract Integer getNrOfTranscribedRegions();
	public abstract void setNrOfTranscribedRegions(Integer nrOfTranscribedRegions);
	public abstract Integer getNrOfWordsInRegions();
	public abstract void setNrOfWordsInRegions(Integer nrOfWordsInRegions);
	public abstract Integer getNrOfLines();
	public abstract void setNrOfLines(Integer nrOfLines);
	public abstract Integer getNrOfTranscribedLines();
	public abstract void setNrOfTranscribedLines(Integer nrOfTranscribedLines);
	public abstract Integer getNrOfWordsInLines();
	public abstract void setNrOfWordsInLines(Integer nrOfWordsInLines);
	public abstract Integer getNrOfWords();
	public abstract void setNrOfWords(Integer nrOfWords);
	public abstract Integer getNrOfTranscribedWords();
	public abstract void setNrOfTranscribedWords(Integer nrOfTranscribedWords);	
}
