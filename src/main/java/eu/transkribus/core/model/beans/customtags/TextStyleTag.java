package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.ColourSimpleType;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.util.BeanCopyUtils;
import eu.transkribus.core.util.TextStyleTypeUtils;
import net.sf.saxon.om.SelectedElementsSpaceStrippingRule;

/**
 * A custom tag that wraps the fields of a {@link TextSyleType} object and is used for indexed styles.
 */
public class TextStyleTag extends CustomTag {
	private final static Logger logger = LoggerFactory.getLogger(TextStyleTag.class);
	
	public static final String TAG_NAME = "textStyle";
	
	public final CustomTagAttribute[] ATTRIBUTES = { 
		new CustomTagAttribute("fontFamily", true, "Font family", "Font family"),
		new CustomTagAttribute("serif", true, "Serif", "Is this a serif font?", Boolean.class),
		new CustomTagAttribute("monospace",true, "Monospace", "Is this a monospace (i.e. equals width characters) font?", Boolean.class),
		new CustomTagAttribute("fontSize", true, "Font size", "The size of the font in points"),
		new CustomTagAttribute("kerning", true, "Kerning", "The kerning of the font, see: http://en.wikipedia.org/wiki/Kerning"),
		new CustomTagAttribute("textColour", true, "Text colour", "The foreground colour of the text"),
		new CustomTagAttribute("bgColour", true, "Background colour", "The background colour of the text"),
		new CustomTagAttribute("reverseVideo", true, "Reverse video", "http://en.wikipedia.org/wiki/Reverse_video", Boolean.class),
		new CustomTagAttribute("bold", true, "Bold", "Bold font", Boolean.class),
		new CustomTagAttribute("italic", true, "Italic", "Italic font", Boolean.class),
		new CustomTagAttribute("underlined", true, "Underlined", "Underlined", Boolean.class),
		new CustomTagAttribute("subscript", true, "Subscript", "Subscript", Boolean.class),
		new CustomTagAttribute("superscript", true, "Superscript", "Superscript", Boolean.class),
		new CustomTagAttribute("strikethrough", true, "Strikethrough", "Strikethrough", Boolean.class),
		new CustomTagAttribute("smallCaps", true, "Small caps", "Small capital letters at the height as lowercase letters, see: http://en.wikipedia.org/wiki/Small_caps", Boolean.class),
		new CustomTagAttribute("letterSpaced", true, "Letter spaced", "Equals distance between characters, see: http://en.wikipedia.org/wiki/Letter-spacing", Boolean.class),
	};
	
//	public static String DEFAULT_COLOR = "#000000";
	public static String DEFAULT_COLOR = "#808080";
	
	TextStyleType ts;
	
	public TextStyleTag() {
		super(TAG_NAME);
		ts = new TextStyleType();
	}
	
	@Override public boolean isDeleteable() {
		return false;
	}
	
	@Override protected void reset(boolean withIndices) {
		super.reset(withIndices);
		ts = new TextStyleType();
	}
	
	public TextStyleTag(int offset, int length) {
		this();
		setOffset(offset);
		setLength(length);
	}
	
	public TextStyleTag(TextStyleType ts, int offset, int length) {
		this();
		this.ts = ts;
		Assert.assertNotNull("TextStyleType is null!", ts);
		this.offset = offset;
		this.length = length;
		
		Assert.assertTrue("offset must be greater 0: o="+offset+" l="+length, offset>=0 && length>=0);
	}
    
	public TextStyleTag(TextStyleTag styleTag) {
//		super(TAG_NAME, styleTag.offset, styleTag.length);
		super(styleTag);
		
		ts = BeanCopyUtils.copyTextStyleType(styleTag.ts);
	}
	
	@Override public String getDefaultColor() { return DEFAULT_COLOR; }
	
	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	public TextStyleType getTextStyle() { return ts; }
	public void setTextStyle(TextStyleType ts) { this.ts = ts; }
                   
	public String getFontFamily() {
		return ts.getFontFamily();
	}

	public void setFontFamily(String fontFamily) {
		ts.setFontFamily(fontFamily);
	}


	public Boolean getSerif() {
		return ts.isSerif();
	}


	public void setSerif(Boolean serif) {
		ts.setSerif(serif);
	}


	public Boolean getMonospace() {
		return ts.isMonospace();
	}


	public void setMonospace(Boolean monospace) {
		ts.setMonospace(monospace);
	}


	public Float getFontSize() {
		return ts.getFontSize();
	}


	public void setFontSize(Float fontSize) {
		ts.setFontSize(fontSize);
	}


	public Integer getKerning() {
		return ts.getKerning();
	}


	public void setKerning(Integer kerning) {
		ts.setKerning(kerning);
	}


	public ColourSimpleType getTextColour() {
		return ts.getTextColour();
	}


	public void setTextColour(ColourSimpleType textColour) {
		ts.setTextColour(textColour);
	}


	public ColourSimpleType getBgColour() {
		return ts.getBgColour();
	}


	public void setBgColour(ColourSimpleType bgColour) {
		ts.setBgColour(bgColour);
	}


	public Boolean getReverseVideo() {
		return ts.isReverseVideo();
	}


	public void setReverseVideo(Boolean reverseVideo) {
		ts.setReverseVideo(reverseVideo);
	}


	public Boolean getBold() {
		return ts.isBold();
	}


	public void setBold(Boolean bold) {
		ts.setBold(bold);
	}


	public Boolean getItalic() {
		return ts.isItalic();
	}


	public void setItalic(Boolean italic) {
		ts.setItalic(italic);
	}


	public Boolean getUnderlined() {
		return ts.isUnderlined();
	}


	public void setUnderlined(Boolean underlined) {
		ts.setUnderlined(underlined);
	}


	public Boolean getSubscript() {
		return ts.isSubscript();
	}


	public void setSubscript(Boolean subscript) {
		ts.setSubscript(subscript);
		// set superscript to false if subscript was set:
		if (subscript!=null && subscript) {
			ts.setSuperscript(false);
		}		
	}


	public Boolean getSuperscript() {
		return ts.isSuperscript();
	}


	public void setSuperscript(Boolean superscript) {
		ts.setSuperscript(superscript);
		// set subscript to false if superscript was set:
		if (superscript!=null && superscript) {
			ts.setSubscript(false);	
		}
	}


	public Boolean getStrikethrough() {
		return ts.isStrikethrough();
	}


	public void setStrikethrough(Boolean strikethrough) {
		ts.setStrikethrough(strikethrough);
	}


	public Boolean getSmallCaps() {
		return ts.isSmallCaps();
	}


	public void setSmallCaps(Boolean smallCaps) {
		ts.setSmallCaps(smallCaps);
	}


	public Boolean getLetterSpaced() {
		return ts.isLetterSpaced();
	}


	public void setLetterSpaced(Boolean letterSpaced) {
		ts.setLetterSpaced(letterSpaced);
	}
	
	@Override
	public TextStyleTag copy() {
		return new TextStyleTag(this);
	}
	
    /**
     * Adds fields from src to this custom tag, i.e. fields in this tag are overwritten with fields in the src tag
     * if both are set or the field is set in the src tag. 
     * Fields that are set in this tag and null in the src tag are <emph>not</emph> overwritten though!
     * In this implementation, this method is used for merging: {@link TextStyleTypeUtils#addTextStyleTypeFieldsConservative(TextStyleType, TextStyleType)}
     */
    @Override public boolean setAttributes(CustomTag src, boolean withIndices) {
    	boolean addedAttributes = super.setAttributes(src, withIndices);
    	// set attributes from text style type seperately:
    	if (src instanceof TextStyleTag) {
    		TextStyleTag tst = (TextStyleTag) src;
    		TextStyleTypeUtils.addTextStyleTypeFields(tst.getTextStyle(), ts, true);
    	}
    	return addedAttributes;
    }
    
    @Override public void mergeEqualAttributes(CustomTag src, boolean withIndices) {
    	super.mergeEqualAttributes(src, withIndices);
    	
    	if (src instanceof TextStyleTag) {
    		TextStyleTag tst = (TextStyleTag) src;
    		TextStyleType mergedTs = TextStyleTypeUtils.mergeEqualTextStyleTypeFields(this.ts, tst.ts);
    		
    		this.ts = mergedTs;
    	}
    }
    
    /** Returns false -> a TextStyleTag never continues! */
    @Override public boolean isContinued() { return false; }
    /** Does nothing -> a TextStyleTag never continues! */
    @Override public void setContinued(boolean continued) { }
	
//	@Override
//	public String getCssStr() {
//	    final String TAB = "; ", TAB1 = ";";
//	    String retValue = getTagName()+" {";
//	    
//	    retValue += "offset:" + this.offset;
//		retValue += TAB + "length:" + this.length;
//		
//		if (getFontFamily()!=null && !getFontFamily().isEmpty())
//			retValue += TAB + "fontFamily:" + getFontFamily();
//		if (getSerif()!=null && getSerif())
//			retValue += TAB + "serif:" + getSerif();
//		if (getMonospace()!=null && getMonospace())
//			retValue += TAB + "monospace:" + getMonospace();
//		if (getFontSize()!=null)
//			retValue += TAB + "fontSize:" + getFontSize();
//		if (getKerning()!=null)
//			retValue += TAB + "kerning:" + getKerning();
//		if (getTextColour()!=null)
//			retValue += TAB + "textColour:" + getTextColour();
//		if (getBgColour()!=null)
//			retValue += TAB + "bgColour:" + getBgColour();
//		if (getReverseVideo()!=null && getReverseVideo())
//			retValue += TAB + "reverseVideo:" + getReverseVideo();
//		if (getBold()!=null && getBold())
//			retValue += TAB + "bold:" + getBold();
//		if (getItalic()!=null && getItalic())
//			retValue += TAB + "italic:" + getItalic();
//		if (getUnderlined()!=null && getUnderlined())
//			retValue += TAB + "underlined:" + getUnderlined();
//		if (getSubscript()!=null && getSubscript())
//			retValue += TAB + "subscript:" + getSubscript();
//		if (getSuperscript()!=null && getSuperscript())
//			retValue += TAB + "superscript:" + getSuperscript();
//		if (getStrikethrough()!=null && getStrikethrough())
//			retValue += TAB + "strikethrough:" + getStrikethrough();
//		if (getSmallCaps()!=null && getSmallCaps())
//			retValue += TAB + "smallCaps:" + getSmallCaps();
//		if (getLetterSpaced()!=null && getLetterSpaced())
//			retValue += TAB + "letterSpaced:" + getLetterSpaced();
//		
//		retValue += TAB1 + "}";
//	    return retValue;
//	}
	
	@Override public String toString() {
		return this.getCssStr();
	}

	@Override
	public boolean isIndexed() {
		return true;
	}

	/**
	 * TextStyleTag is empty valued if containing TextStyleType equals empty TextStyleType 
	 */
	@Override public boolean isEmptyValued() {
		return length<=0 || TextStyleTypeUtils.equalsInEffectiveValues(ts, new TextStyleType());
	}
	
//	static Boolean nullVal = null;
//	
//	public static boolean getNull() {
//		return nullVal;
//	}
	
//	@Override public boolean equals(CustomTag compare) {
//		if (compare instanceof TextStyleTag) {
//			return super.equals(compare) && TextStyleTypeUtils.equals(ts, ((TextStyleTag)compare).ts);
//		} else return false;
//	}
	
	@Override public boolean equalsEffectiveValues(CustomTag compare, boolean withIndices) {		
		if (compare instanceof TextStyleTag) {
			if ( withIndices && (offset != compare.offset && length != compare.length) ) {
				return false;
			}
			return TextStyleTypeUtils.equalsInEffectiveValues(ts, ((TextStyleTag)compare).ts);
			
		} else
			return false;
	}
	
	@Override public boolean sneakToLeft() { return true; }
	
	@Override public boolean mergeWithEqualValuedNeighbor() { return true; }
	
	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}
	
	public static TextStyleTag getBoldTag() {
		TextStyleTag t = new TextStyleTag();
		t.setBold(true);
		return t;
	}
	
	public static TextStyleTag getItalicTag() {
		TextStyleTag t = new TextStyleTag();
		t.setItalic(true);
		return t;
	}
	
	public static TextStyleTag getSuperscriptTag() {
		TextStyleTag t = new TextStyleTag();
		t.setSuperscript(true);
		return t;
	}
	
	public static TextStyleTag getSubscriptTag() {
		TextStyleTag t = new TextStyleTag();
		t.setSubscript(true);
		return t;
	}
	
	public static TextStyleTag getUnderlinedTag() {
		TextStyleTag t = new TextStyleTag();
		t.setUnderlined(true);
		return t;
	}
	
	public static TextStyleTag getStrikethroughTag() {
		TextStyleTag t = new TextStyleTag();
		t.setStrikethrough(true);
		return t;
	}
	
	/**
	 * Helper function to apply all boolean fields from tag t to this tag if they are true
	 */
	public void applyTrueValues(TextStyleTag t) {
		// FIXME...
		
		
		if (t.ts.isBold())
			ts.setBold(true);
		
		if (t.ts.isItalic())
			ts.setItalic(true);
		
		if (t.ts.isLetterSpaced())
			ts.setLetterSpaced(true);
		
		if (t.ts.isMonospace())
			ts.setMonospace(true);
		
		if (t.ts.isReverseVideo())
			ts.setReverseVideo(true);
		
		if (t.ts.isSerif())
			ts.setSerif(true);
		
		if (t.ts.isSmallCaps())
			ts.setSmallCaps(true);
		
		if (t.ts.isStrikethrough())
			ts.setStrikethrough(true);
		
		if (t.ts.isSubscript())
			ts.setSubscript(true);
		
		if (t.ts.isSuperscript())
			ts.setSuperscript(true);
		
		if (t.ts.isUnderlined())
			ts.setUnderlined(true);
	}
	
//	@Override public String getDisplayName() {
//		return "Text style";
//	}
//
//	@Override public void setDisplayName(String displayName) {
//	}
	
	public static void main(String[] args) {
		TextStyleTag tst = new TextStyleTag();
		System.out.println(tst.getAttributeNames());
	}

}