package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

public class BlackeningTag extends CustomTag {
	public static final String TAG_NAME = "blackening";
	
	public static String DEFAULT_COLOR = "#000000";
	
	public final CustomTagAttribute[] ATTRIBUTES = {
			new CustomTagAttribute("comment", true, "comment", ""),
	};
	
	String comment;

	public BlackeningTag() {
		super(TAG_NAME);
	}
	
	public BlackeningTag(BlackeningTag other) {
		super(other);
		
		this.comment = other.comment;
	}
	
	@Override public String getDefaultColor() { return DEFAULT_COLOR; }

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}


	@Override protected void reset(boolean withIndices) {
		super.reset(withIndices);
		
		this.comment = "";
	}

	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public BlackeningTag copy() {
		return new BlackeningTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}	
	
}
