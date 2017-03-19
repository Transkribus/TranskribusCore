package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

public class DivTag extends CustomTag {
	private static final long serialVersionUID = 4860867382781002910L;

	public static final String TAG_NAME = "div";
	
	public static String DEFAULT_COLOR = "#006FA6";
	
<<<<<<< HEAD
	String n;
	String type;
	
	public final CustomTagAttribute[] ATTRIBUTES = {
//			new CustomTagAttribute("whatever", true, "Whatever", ""),
			new CustomTagAttribute("n", true, "n", "The hierarchy level of this division, e.g. '1.1'"),
			new CustomTagAttribute("type", true, "type", "The type of this division, e.g. 'chapter'"),
	};

	public DivTag() {
		super(TAG_NAME);
	}
	
	public DivTag(DivTag other) {
		super(other);
		this.n = other.n;
		this.type = other.type;
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

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

=======
	public final CustomTagAttribute[] ATTRIBUTES = {
//			new CustomTagAttribute("whatever", true, "Whatever", ""),
			new CustomTagAttribute("n", true, "n", "The hierarchy level of this division, e.g. '1.1'"),
			new CustomTagAttribute("type", true, "type", "The type of this division, e.g. 'chapter'"),
	};

	public DivTag() {
		super(TAG_NAME);
	}
	
	public DivTag(DivTag other) {
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
	
>>>>>>> branch 'master' of https://github.com/Transkribus/TranskribusCore
	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public DivTag copy() {
		return new DivTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}	
	
}

