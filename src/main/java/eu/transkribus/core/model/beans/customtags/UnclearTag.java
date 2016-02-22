package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

public class UnclearTag extends CustomTag {
	public static final String TAG_NAME = "unclear";
	
	public static String DEFAULT_COLOR = "#FFCC66";
	
	public final CustomTagAttribute[] ATTRIBUTES = {
			new CustomTagAttribute("alternative", true, "Alternative", ""),
			new CustomTagAttribute("reason", true, "Reason", ""),
	};
	
	String alternative;
	String reason;

	public UnclearTag() {
		super(TAG_NAME);
	}
	
	public UnclearTag(UnclearTag other) {
		super(other);
		
		this.alternative = other.alternative;
	}
	
	@Override public String getDefaultColor() { return DEFAULT_COLOR; }
	
	public String getAlternative() {
		return alternative;
	}

	public void setAlternative(String alternative) {
		this.alternative = alternative;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override protected void reset(boolean withIndices) {
		super.reset(withIndices);
		
		this.alternative = "";
	}

	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public UnclearTag copy() {
		return new UnclearTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}	
	
}
