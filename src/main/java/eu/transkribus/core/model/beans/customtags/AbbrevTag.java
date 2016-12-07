package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "ABBREV_TAG")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AbbrevTag extends CustomTag {
	private final static Logger logger = LoggerFactory.getLogger(AbbrevTag.class);
	
	public static final String TAG_NAME = "abbrev";
	public final CustomTagAttribute[] ATTRIBUTES = { 
			new CustomTagAttribute("expansion", true, "Expansion", "The expansion of this abbreviation")
	};
	
	public static String DEFAULT_COLOR = "#FF0000";
			
	String expansion = "";
	
	public AbbrevTag() {
		super(TAG_NAME);
	}
		
	public AbbrevTag(String expansion) {
		this();
		setExpansion(expansion);
	}
	
	public AbbrevTag(AbbrevTag other) {
		super(other);
		setExpansion(other.expansion);
	}
	
	public AbbrevTag(String expansion, int offset, int length) {
		this();
		setExpansion(expansion);
		setOffset(offset);
		setLength(length);
	}
	
	@Override public String getDefaultColor() { return DEFAULT_COLOR; }
		
	@Override protected void reset(boolean withIndices) {
		super.reset(withIndices);
		expansion = "";
	}

	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public AbbrevTag copy() {
		return new AbbrevTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}

	public String getExpansion() {
		return expansion;
	}

	public void setExpansion(String expansion) {
		this.expansion = expansion;
	}

}
