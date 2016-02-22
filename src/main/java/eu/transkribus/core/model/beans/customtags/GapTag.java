package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

public class GapTag extends CustomTag {
	public static final String TAG_NAME = "gap";
	
	public static String DEFAULT_COLOR = "#1CE6FF";
	
	public final CustomTagAttribute[] ATTRIBUTES = {
//			new CustomTagAttribute("whatever", true, "Whatever", ""),
	};

	public GapTag() {
		super(TAG_NAME);
	}
	
	public GapTag(GapTag other) {
		super(other);
	}
	
	@Override public String getDefaultColor() { return DEFAULT_COLOR; }

	@Override protected void reset(boolean withIndices) {
		super.reset(withIndices);
	}

	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean canBeEmpty() {
		return true;
	}
	
//	@Override public int getLength() { 
//		return 0;
//	};
	
	@Override public void setLength(int length) {
		this.length = 0;
	}
	
	@Override public boolean isEmpty() { 
		return true;
	}
	
	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public GapTag copy() {
		return new GapTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}	
	
}
