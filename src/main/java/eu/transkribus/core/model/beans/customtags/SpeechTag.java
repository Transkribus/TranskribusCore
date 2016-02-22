package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

public class SpeechTag extends CustomTag {
	public static final String TAG_NAME = "speech";
	
	public static String DEFAULT_COLOR = "#A30059";
	
	public final CustomTagAttribute[] ATTRIBUTES = {
			new CustomTagAttribute("speaker", true, "Speaker", ""),
	};
	
	String speaker;
	
	public SpeechTag() {
		super(TAG_NAME);
	}
	
	public SpeechTag(SpeechTag other) {
		super(other);
		this.speaker = other.speaker;
	}
	
	@Override public String getDefaultColor() { return DEFAULT_COLOR; }
	
	public String getSpeaker() {
		return speaker;
	}

	public void setSpeaker(String speaker) {
		this.speaker = speaker;
	}

	@Override protected void reset(boolean withIndices) {
		super.reset(withIndices);
		this.speaker = "";
	}

	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public SpeechTag copy() {
		return new SpeechTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}
}
