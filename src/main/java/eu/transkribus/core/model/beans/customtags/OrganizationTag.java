package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

public class OrganizationTag extends CustomTag {
	public static final String TAG_NAME = "organization";
	
	public static String DEFAULT_COLOR = "#FF00FF";
	
	public final CustomTagAttribute[] ATTRIBUTES = {
//			new CustomTagAttribute("country", true, "Country", ""),
	};
	
	public OrganizationTag() {
		super(TAG_NAME);
	}
	
	public OrganizationTag(OrganizationTag other) {
		super(other);
	}	
	
	@Override public String getDefaultColor() { return DEFAULT_COLOR; }

	@Override protected void reset(boolean withIndices) {
		super.reset(withIndices);
	}

	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public OrganizationTag copy() {
		return new OrganizationTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}

}
