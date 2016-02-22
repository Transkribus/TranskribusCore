package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

public class PlaceTag extends CustomTag {
	public static final String TAG_NAME = "place";
	
	public static String DEFAULT_COLOR = "#8A2BE2";
	
	public final CustomTagAttribute[] ATTRIBUTES = {
			new CustomTagAttribute("country", true, "Country", ""),
	};	
	
	String country;

	public PlaceTag() {
		super(TAG_NAME);
	}
	
	public PlaceTag(PlaceTag other) {
		super(other);
		this.country = other.country;
	}
	
	@Override public String getDefaultColor() { return DEFAULT_COLOR; }
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override protected void reset(boolean withIndices) {
		super.reset(withIndices);
		
		this.country = "";
	}

	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public PlaceTag copy() {
		return new PlaceTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}	
	
}
