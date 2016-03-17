package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SuppliedTag extends CustomTag {
	private final static Logger logger = LoggerFactory.getLogger(SuppliedTag.class);
	
	public static final String TAG_NAME = "supplied";
	
	public final CustomTagAttribute[] ATTRIBUTES = { 
			new CustomTagAttribute("reason", true, "Reason", "The reason why this text is supplied")
	};
	
	public static String DEFAULT_COLOR = "#CD5C5C";
			
	String reason = "";
	
	public SuppliedTag() {
		super(TAG_NAME);
	}
		
	public SuppliedTag(String expansion) {
		this();
		setReason(expansion);
	}
	
	public SuppliedTag(SuppliedTag other) {
		super(other);
		setReason(other.reason);
	}
	
	public SuppliedTag(String expansion, int offset, int length) {
		this();
		setReason(expansion);
		setOffset(offset);
		setLength(length);
	}
	
	@Override public String getDefaultColor() { return DEFAULT_COLOR; }
		
	@Override protected void reset(boolean withIndices) {
		super.reset(withIndices);
		reason = "";
	}

	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public SuppliedTag copy() {
		return new SuppliedTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
