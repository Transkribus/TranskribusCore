package eu.transkribus.core.model.beans.customtags.search;

import eu.transkribus.core.util.CoreUtils;

public class TextSearchFacets extends SearchFacets {
	String text;
	
	public TextSearchFacets(String text, boolean wholeWord, boolean caseSensitive) {
		super(wholeWord, caseSensitive);
		
		this.text = text;
	}

	public String getText(boolean asRegex) {
		return asRegex ? CoreUtils.createRegexFromSearchString(text, true, isWholeWord(), isCaseSensitive()) : text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public SearchType getType() { return SearchType.Text; }

}
