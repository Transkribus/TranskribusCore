package eu.transkribus.core.model.beans.customtags.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.transkribus.core.model.beans.customtags.search.SearchFacets.SearchType;
import eu.transkribus.core.util.CoreUtils;

public class CustomTagSearchFacets extends SearchFacets {
	private String tagNameRegex;
	private String tagValueRegex;
	private Map<String, Object> props;
	
	public CustomTagSearchFacets(String tagNameRegex, String tagValueRegex, Map<String, Object> props
			, boolean wholeWord, boolean caseSensitive) {
		super(wholeWord, caseSensitive);
		
		this.tagNameRegex = tagNameRegex == null || tagNameRegex.isEmpty() ? "" : tagNameRegex;
		this.tagValueRegex = tagValueRegex == null || tagValueRegex.isEmpty() ? "" : tagValueRegex;
		this.props = props==null ? new HashMap<String, Object>() : props;
	}
	
	public String getTagName(boolean withRegex) {
		return withRegex ? CoreUtils.createRegexFromSearchString(tagNameRegex, true, false, isCaseSensitive()) :tagNameRegex; 
	}
	
	public String getTagValue(boolean withRegex) {
		return withRegex ? CoreUtils.createRegexFromSearchString(tagValueRegex, true, false, isCaseSensitive()) :tagValueRegex;
	}
	
	public Set<String> getProperties(boolean withRegex) {
		if (props == null)
			new HashSet<String>();
		if (!withRegex)
			return props.keySet();
		
		Set<String> propsWithRegex = new HashSet<String>();
		for (String p : props.keySet()) {
			propsWithRegex.add(CoreUtils.createRegexFromSearchString(p, true, false, isCaseSensitive()));
		}

		return propsWithRegex;
	}
	
	public Object getPropValue(String property, boolean withRegex) {
		Object v = props.get(property);
		if (v != null) {
			return withRegex ? CoreUtils.createRegexFromSearchString(v.toString(), true, false, isCaseSensitive()) : v;
		}
		else
			return null;
	}	
	
	public SearchType getType() { return SearchType.Tags; }
	
}
