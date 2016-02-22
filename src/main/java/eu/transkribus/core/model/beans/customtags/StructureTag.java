package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.model.beans.pagecontent.TextTypeSimpleType;
import eu.transkribus.core.util.TextStyleTypeUtils;

/**
 * {@code CustomTag} that embeds a {@link TextTypeSimpleType} attribute to encode
 * structure information into any shape level (i.e. lines and words, not just text regions)
 */
public class StructureTag extends CustomTag {
	private final static Logger logger = LoggerFactory.getLogger(StructureTag.class);
	
	public static final String TAG_NAME = "structure";
	public final CustomTagAttribute[] ATTRIBUTES = { 
			new CustomTagAttribute("type", true, "Structure type", "The structure type of this element")
	};
		
//	TextTypeSimpleType type = null;
//	String type = "";
	String type = "";
	
	public StructureTag() {
		super(TAG_NAME);
	}
	
//	public StructureTag(TextTypeSimpleType type) {
//		this();
//		this.type = type;
//	}
	
	public StructureTag(String type) {
		this();
		setType(type);
	}
	
	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean showInTagWidget() { return false; }
	
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public StructureTag copy() {
		return new StructureTag(type);
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
		TextTypeSimpleType s = parseTextType(type);
		if (s != null)
			this.type = s.value();
		else
			this.type = type;
		
//		this.type = type;
		if (this.type == null)
			this.type = "";
	}
	
	public String getType() { return type; }
	
	public TextTypeSimpleType parseTextType() {
		return parseTextType(type);
	}
	
	public static TextTypeSimpleType parseTextType(String type) {
		if (type == null)
			return null;
		
		try {
			TextTypeSimpleType s = TextTypeSimpleType.fromValue(type);
			return s;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
	
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
