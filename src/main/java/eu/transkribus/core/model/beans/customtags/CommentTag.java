package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

public class CommentTag extends CustomTag {
	public static final String TAG_NAME = "comment";
	
//	public static String DEFAULT_COLOR = "#FFEB00";
	
	public static final String COMMENT_PROPERTY_NAME = "comment";
	
	public final CustomTagAttribute[] ATTRIBUTES = {
			new CustomTagAttribute(COMMENT_PROPERTY_NAME, true, "Comment", ""),
	};
	
	String comment="";

	public CommentTag() {
		super(TAG_NAME);
	}
	
	public CommentTag(CommentTag other) {
		super(other);
		
		this.comment = other.comment;
	}

	

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
	
	@Override public boolean showInTagWidget() { return false; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public CommentTag copy() {
		return new CommentTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}
	
}
