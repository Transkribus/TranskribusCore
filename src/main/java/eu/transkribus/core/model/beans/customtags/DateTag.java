package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

public class DateTag extends CustomTag {
	public static final String TAG_NAME = "date";
	
	public static String DEFAULT_COLOR = "#0000FF";
	
	public final CustomTagAttribute[] ATTRIBUTES = {
			new CustomTagAttribute("year", true, "Year", ""),
			new CustomTagAttribute("month", true, "Month", ""),
			new CustomTagAttribute("day", true, "Day", ""),
	};
	
	Integer year;
	Integer month;
	Integer day;

	public DateTag() {
		super(TAG_NAME);
	}
	
	public DateTag(DateTag other) {
		super(other);
		
		this.year = other.year;
		this.month = other.month;
		this.day = other.day;
	}
	
	@Override public String getDefaultColor() { return DEFAULT_COLOR; }

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	@Override protected void reset(boolean withIndices) {
		super.reset(withIndices);
		
		this.year = null;
		this.month = null;
		this.day = null;
	}

	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public DateTag copy() {
		return new DateTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}	
	
}
