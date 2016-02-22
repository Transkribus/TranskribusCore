package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegionTypeTag extends CustomTag {
	private final static Logger logger = LoggerFactory.getLogger(RegionTypeTag.class);
	
	public static final String TAG_NAME = "regionType";
	public final CustomTagAttribute[] ATTRIBUTES = { 
			new CustomTagAttribute("type", true, "Region type", "The region type")
	};
		
//	TextTypeSimpleType type = null;
//	String type = "";
	String type = "";
	
	public RegionTypeTag() {
		super(TAG_NAME);
	}
	
//	public StructureTag(TextTypeSimpleType type) {
//		this();
//		this.type = type;
//	}
	
	public RegionTypeTag(String type) {
		this();
		setType(type);
	}
	
	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean showInTagWidget() { return false; }
	
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public RegionTypeTag copy() {
		return new RegionTypeTag(type);
	}

	@Override
	public boolean isIndexed() {
		return false;
	}
	
	@Override public void setOffset(int offset) { }
	@Override public void setLength(int length) { }	
	
//    @Override public void addFieldsFrom(CustomTag toMerge) {
//    	super.setAttributes(toMerge);
//    	if (toMerge instanceof StructureTag) {
//    		this.type = ((StructureTag) toMerge).type;
//    	}
//    }
	
//	@Override
//	public String getCssStr() {
//		if (type!=null) {
//			return getTagName() + " {"+"type:"+type+";}";
//		}
//		else
//			return "";
//	}
	
	public void setType(String type) {
		this.type = type;
		if (this.type == null)
			this.type = "";
	}
	
	public String getType() { return type; }
			
	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}
		
//	@Override public String getDisplayName() {
//		return "Structure type";
//	}
//
//	@Override public void setDisplayName(String displayName) {
//	}
		
}
