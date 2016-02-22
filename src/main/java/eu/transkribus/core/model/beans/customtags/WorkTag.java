package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

public class WorkTag extends CustomTag {
	public static final String TAG_NAME = "work";
	
	public static String DEFAULT_COLOR = "#008000";
	
	public final CustomTagAttribute[] ATTRIBUTES = {
			new CustomTagAttribute("creator", true, "Creator", ""),
			new CustomTagAttribute("title", true, "Title", ""),
			new CustomTagAttribute("year", true, "Year", ""),
	};
	
	String creator, title;
	String year;

	public WorkTag() {
		super(TAG_NAME);
	}
	
	public WorkTag(WorkTag other) {
		super(other);
		
		this.year = other.year;
		this.creator = other.creator;
		this.title = other.title;
	}
	
	@Override public String getDefaultColor() { return DEFAULT_COLOR; }
	


	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@Override protected void reset(boolean withIndices) {
		super.reset(withIndices);
		
		this.year = "";
		this.creator = "";
		this.title = "";
	}

	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public WorkTag copy() {
		return new WorkTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}	
	
}
