package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

public class AdditionTag extends CustomTag {
	public static final String TAG_NAME = "add";
	
	public static String DEFAULT_COLOR = "#33FFCC";
	
	public final CustomTagAttribute[] ATTRIBUTES = {
			new CustomTagAttribute("place", true, "Place", ""),
	};	
	
	String place;

	public AdditionTag() {
		super(TAG_NAME);
	}
	
	public AdditionTag(AdditionTag other) {
		super(other);
		this.place = other.place;
	}
	
	@Override public String getDefaultColor() { return DEFAULT_COLOR; }
	
	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	@Override protected void reset(boolean withIndices) {
		super.reset(withIndices);
		
		this.place = "";
	}

	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public AdditionTag copy() {
		return new AdditionTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}	
	
}
