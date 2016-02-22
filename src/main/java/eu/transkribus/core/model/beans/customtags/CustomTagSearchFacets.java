package eu.transkribus.core.model.beans.customtags;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.transkribus.core.util.CoreUtils;

public class CustomTagSearchFacets {
	
//	public CustomTagSearchFacets() {
//	}

	public CustomTagSearchFacets(String tagNameRegex, String tagValueRegex, Map<String, Object> props
			, boolean searchText, boolean wholeWord, boolean caseSensitive) {
		super();
		this.tagNameRegex = tagNameRegex == null || tagNameRegex.isEmpty() ? "" : tagNameRegex;
		this.tagValueRegex = tagValueRegex == null || tagValueRegex.isEmpty() ? "" : tagValueRegex;
		this.props = props==null ? new HashMap<String, Object>() : props;
		this.searchText = searchText;
		this.wholeWord = wholeWord;
		this.caseSensitive = caseSensitive;
	}
	
	private String tagNameRegex;
	private String tagValueRegex;
	private Map<String, Object> props;
	private boolean searchText=false;
	private boolean wholeWord;
	private boolean caseSensitive;	
	
	public String getTagName(boolean withRegex) {
		return withRegex ? CoreUtils.createRegexFromSearchString(tagNameRegex, true) :tagNameRegex; 
	}
	
	public String getTagValue(boolean withRegex) {
		return withRegex ? CoreUtils.createRegexFromSearchString(tagValueRegex, true) :tagValueRegex;
	}
	
	public Set<String> getProperties(boolean withRegex) {
		if (props == null)
			new HashSet<String>();
		if (!withRegex)
			return props.keySet();
		
		Set<String> propsWithRegex = new HashSet<String>();
		for (String p : props.keySet()) {
			propsWithRegex.add(CoreUtils.createRegexFromSearchString(p, true));
		}

		return propsWithRegex;
	}
	
	public Object getPropValue(String property, boolean withRegex) {
		Object v = props.get(property);
		if (v != null) {
			return withRegex ? CoreUtils.createRegexFromSearchString(v.toString(), true) : v;
		}
		else
			return null;
	}

	public boolean isSearchText() {
		return searchText;
	}

	public void setSearchText(boolean searchText) {
		this.searchText = searchText;
	}

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
