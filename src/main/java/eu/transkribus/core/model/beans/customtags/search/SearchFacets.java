package eu.transkribus.core.model.beans.customtags.search;

public abstract class SearchFacets {
	
	public enum SearchType {
		Text,
		Tags;
	}
	
	public SearchFacets(boolean wholeWord, boolean caseSensitive) {
		super();
		this.wholeWord = wholeWord;
		this.caseSensitive = caseSensitive;
	}

	private boolean wholeWord=false;
	private boolean caseSensitive=false;
	
	public abstract SearchType getType();
	
	public boolean isWholeWord() {
		return wholeWord;
	}

	public void setWholeWord(boolean wholeWord) {
		this.wholeWord = wholeWord;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

}
