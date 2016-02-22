package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

public class SicTag extends CustomTag {
	public static final String TAG_NAME = "sic";
	
	public static String DEFAULT_COLOR = "#FFEB00";
	
	public final CustomTagAttribute[] ATTRIBUTES = {
			new CustomTagAttribute("correction", true, "Correction", ""),
	};
	
	String correction;

	public SicTag() {
		super(TAG_NAME);
	}
	
	public SicTag(SicTag other) {
		super(other);
		
		this.correction = other.correction;
	}

	@Override public String getDefaultColor() { return DEFAULT_COLOR; }

	public String getCorrection() {
		return correction;
	}

	public void setCorrection(String correction) {
		this.correction = correction;
	}

	@Override protected void reset(boolean withIndices) {
		super.reset(withIndices);
		
		this.correction = "";
	}

	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public SicTag copy() {
		return new SicTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}	
	
}
