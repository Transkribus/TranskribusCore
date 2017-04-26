package eu.transkribus.core.model.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "transcripts")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpDocStatistic implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name="NR_OF_TRANSCRIBED_LINES")
	private int nrOfTranscribedLines;
	
	@Column(name="NR_OF_WORDS_IN_LINES")
	private int nrOfWords;
	
	public TrpDocStatistic(){
		nrOfTranscribedLines = 0;
		nrOfWords = 0;
	}

	public int getNrOfLines() {
		return nrOfTranscribedLines;
	}
	public void setNrOfLines(int nrOfLines) {
		this.nrOfTranscribedLines = nrOfLines;
	}
	
	public int getNrOfWords() {
		return nrOfWords;
	}
	public void setNrOfWords(int nrOfWords) {
		this.nrOfWords = nrOfWords;
	}
	
	
}
