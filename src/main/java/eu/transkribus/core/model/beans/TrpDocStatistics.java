package eu.transkribus.core.model.beans;


public class TrpDocStatistics {
	private int nrOfPages;
	private int nrOfTextRegions;
	private int nrOfLines;
	private int nrOfWords;
	public TrpDocStatistics(){
		nrOfPages = 0;
		nrOfLines = 0;
		nrOfWords = 0;
		nrOfTextRegions = 0;
	}
	public int getNrOfPages() {
		return nrOfPages;
	}
	public void setNrOfPages(int nrOfPages) {
		this.nrOfPages = nrOfPages;
	}
	public int getNrOfTextRegions() {
		return nrOfTextRegions;
	}
	public void setNrOfTextRegions(int nrOfTextRegions) {
		this.nrOfTextRegions = nrOfTextRegions;
	}
	public int getNrOfLines() {
		return nrOfLines;
	}
	public void setNrOfLines(int nrOfLines) {
		this.nrOfLines = nrOfLines;
	}
	public int getNrOfWords() {
		return nrOfWords;
	}
	public void setNrOfWords(int nrOfWords) {
		this.nrOfWords = nrOfWords;
	}
	
	
}
