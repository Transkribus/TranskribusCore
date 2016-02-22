package eu.transkribus.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.customtags.TextStyleTag;
import eu.transkribus.core.model.beans.pagecontent.TextStyleType;
import eu.transkribus.core.model.beans.pagecontent_extension.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_extension.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_extension.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_extension.TrpWordType;
import eu.transkribus.core.model.beans.pagecontent_extension.observable.TrpObserveEvent.TrpTextStyleChangedEvent;

/**
 * Some utility functions for the {@link TextStyleType} class.
 */
public class TextStyleTypeUtils {
	private final static Logger logger = LoggerFactory.getLogger(TextStyleTypeUtils.class);

	/**
	 * Cleans the fields of the given TextStyleType object, i.e. it sets its values to null if they are equal to their default values
	 * (false for Boolean; 0 for Double, Float, Integer; empty string for String).
	 * @return Returns the cleaned TextStyleType object or null if a null object was given.
	 */
	public static TextStyleType cleanTextStyleTypeFields(TextStyleType src) {
		if (src==null) return null;
		
		// font family is set to null if empty:
		if (src.getFontFamily()!=null && src.getFontFamily().isEmpty()) src.setFontFamily(null);
				
		// font size and kerning are set to null if 0:
		if (src.getFontSize()!=null&&src.getFontSize()==0.0f) src.setFontSize(null);
		if (src.getKerning()!=null&&src.getKerning()==0) src.setKerning(null);
				
		// boolean values are set to null if false:
		if (src.isSerif()!=null && !src.isSerif()) src.setSerif(null);
		if (src.isMonospace()!=null && !src.isMonospace()) src.setMonospace(null);
		if (src.isReverseVideo()!=null && !src.isReverseVideo()) src.setReverseVideo(null);
		if (src.isBold()!=null && !src.isBold()) src.setBold(null);
		if (src.isItalic()!=null && !src.isItalic()) src.setItalic(null);
		if (src.isUnderlined()!=null && !src.isUnderlined()) src.setUnderlined(null);
		if (src.isSubscript()!=null && !src.isSubscript()) src.setSubscript(null);
		if (src.isSuperscript()!=null && !src.isSuperscript()) src.setSuperscript(null);
		if (src.isStrikethrough()!=null && !src.isStrikethrough()) src.setStrikethrough(null);
		if (src.isSmallCaps()!=null && !src.isSmallCaps()) src.setSmallCaps(null);
		if (src.isLetterSpaced()!=null && !src.isLetterSpaced()) src.setLetterSpaced(null);
		
		return src;
	}
	
	/** Returns true if the fields of two TextStyleType object equal in effective values, i.e. it extracts default values for null valued fields.
	 * The default value of a Boolean is false. The default value of a Double, Float or Integer is 0.0d, 0.0f and 0 respectively.
	 * The default value of a String is the empty string. Other objects are compared using the == operator.
	 * If either src or dst are null, false is returned.
	 * */
	public static boolean equalsInEffectiveValues(TextStyleType src, TextStyleType dst) {
		if (src==null || dst==null)
			return false;

    	boolean e = 
    		CoreUtils.val(src.getFontFamily()).equals(CoreUtils.val(dst.getFontFamily())) &&
			
			CoreUtils.val(src.getFontSize()) == CoreUtils.val(dst.getFontSize()) &&
			CoreUtils.val(src.getKerning()) == CoreUtils.val(dst.getKerning()) &&
			
			src.getTextColour( )== dst.getTextColour() &&
			src.getBgColour() == dst.getBgColour() &&			
			
			CoreUtils.val(src.isSerif()) == CoreUtils.val(dst.isSerif()) &&
			CoreUtils.val(src.isMonospace()) == CoreUtils.val(dst.isMonospace()) &&
			CoreUtils.val(src.isReverseVideo()) == CoreUtils.val(dst.isReverseVideo()) &&
			CoreUtils.val(src.isBold()) == CoreUtils.val(dst.isBold()) &&
			CoreUtils.val(src.isItalic()) == CoreUtils.val(dst.isItalic()) &&
			CoreUtils.val(src.isUnderlined()) == CoreUtils.val(dst.isUnderlined()) &&
			CoreUtils.val(src.isSubscript()) == CoreUtils.val(dst.isSubscript()) &&
			CoreUtils.val(src.isSuperscript()) == CoreUtils.val(dst.isSuperscript()) &&
			CoreUtils.val(src.isStrikethrough()) == CoreUtils.val(dst.isStrikethrough()) &&
			CoreUtils.val(src.isSmallCaps()) == CoreUtils.val(dst.isSmallCaps()) &&
			CoreUtils.val(src.isLetterSpaced()) == CoreUtils.val(dst.isLetterSpaced());
    	return e;
	}
	
	/**
	 * Returns true if all fields of src are equal (using the equals method) to the corresponding fields in dst.
	 * Returns false if either src or dst is null.
	 */
	public static boolean equals(TextStyleType src, TextStyleType dst) {
		if (src==null || dst==null)
			return false;

    	return
			CoreUtils.equalsObjects(src.getFontFamily(), (dst.getFontFamily())) &&
			CoreUtils.equalsObjects(src.getFontSize(), (dst.getFontSize())) &&
			CoreUtils.equalsObjects(src.getKerning(), (dst.getKerning())) &&
			CoreUtils.equalsObjects(src.getTextColour(), (dst.getTextColour())) &&
			CoreUtils.equalsObjects(src.getBgColour(), (dst.getBgColour())) &&			
			
			CoreUtils.equalsObjects(src.isSerif(), (dst.isSerif())) &&
			CoreUtils.equalsObjects(src.isMonospace(), (dst.isMonospace())) &&
			CoreUtils.equalsObjects(src.isReverseVideo(), (dst.isReverseVideo())) &&
			CoreUtils.equalsObjects(src.isBold(), (dst.isBold())) &&
			CoreUtils.equalsObjects(src.isItalic(), (dst.isItalic())) &&
			CoreUtils.equalsObjects(src.isUnderlined(), (dst.isUnderlined())) &&
			CoreUtils.equalsObjects(src.isSubscript(), (dst.isSubscript())) &&
			CoreUtils.equalsObjects(src.isSuperscript(), (dst.isSuperscript())) &&
			CoreUtils.equalsObjects(src.isStrikethrough(), (dst.isStrikethrough())) &&
			CoreUtils.equalsObjects(src.isSmallCaps(), (dst.isSmallCaps())) &&
			CoreUtils.equalsObjects(src.isLetterSpaced(), (dst.isLetterSpaced()));
//    	return e;
	}
	
//	/**
//	 * Warpper method for {@link #addTextStyleTypeFields(TextStyleType, TextStyleType, boolean)} with conservative=true.
//	 */
//	public static TextStyleType addTextStyleTypeFieldsConservative(TextStyleType src, TextStyleType dst) {
//		return addTextStyleTypeFields(src, dst, true);
//	}

	/**
	 * Adds the values of the fields of {@link TextStyleType} src into TextStyleType dst and returns the result. <br>
	 * 
	 * If src is null, null is returned, if dst
	 * is null, a new TextStyleType is created using the default constructor.
	 * @param conservative If true, merging is done 'conservatively' for strings and numbers: the font-family string is only overwritten,
	 * if the src font-family is not null and not the empty string. The font-size and kerning fields are only overwritten, is 
	 * their corresponding src fields are not null and greater than 0!
	 */
	public static TextStyleType addTextStyleTypeFields(TextStyleType src, TextStyleType dst, boolean conservative) {
		// TODO add flag to only update a specific property!! ( really here or rather in customtaglist class ?????) 
		if (src==null)
			return null;
		
		if (dst==null)
			dst = new TextStyleType();
		
		if (conservative) {
			// font family is set if not null and not empty
//			 FIXME???
//			if (src.getFontFamily()!=null&&!src.getFontFamily().isEmpty()) dst.setFontFamily(src.getFontFamily());
			dst.setFontFamily(src.getFontFamily());
			// font size and kerning are set if not null and greater 0
			if (src.getFontSize()!=null&&src.getFontSize()>0.0f) dst.setFontSize(src.getFontSize());
			if (src.getKerning()!=null&&src.getKerning()>0) dst.setKerning(src.getKerning());			
		}
		else {
			dst.setFontFamily(src.getFontFamily());
			dst.setFontSize(src.getFontSize());
			dst.setKerning(src.getKerning());
		}
				
		// color values are set no matter what
		dst.setTextColour(src.getTextColour());
		dst.setBgColour(src.getBgColour());
		
		// boolean values are set no matter what:
		/* if (src.isSerif()!=null) */ dst.setSerif(src.isSerif());
		/* if (src.isMonospace()!=null) */ dst.setMonospace(src.isMonospace());
		/* if (src.isReverseVideo()!=null) */ dst.setReverseVideo(src.isReverseVideo());
		/* if (src.isBold()!=null) */ dst.setBold(src.isBold());
		/* if (src.isItalic()!=null) */ dst.setItalic(src.isItalic());
		/* if (src.isUnderlined()!=null) */ dst.setUnderlined(src.isUnderlined());
		/* if (src.isSubscript()!=null) */ dst.setSubscript(src.isSubscript());
		/* if (src.isSuperscript()!=null) */ dst.setSuperscript(src.isSuperscript());
		/* if (src.isStrikethrough()!=null) */ dst.setStrikethrough(src.isStrikethrough());
		/* if (src.isSmallCaps()!=null) */ dst.setSmallCaps(src.isSmallCaps());
		/* if (src.isLetterSpaced()!=null) */ dst.setLetterSpaced(src.isLetterSpaced());
		
		return dst;
	}
	
	/** *
	 * Merges equals fields of src1 and src2 into a new TextStlyeType object that is returned.
	 * If either src1 or src2 are null, null is returned. 
	 */
	public static TextStyleType mergeEqualTextStyleTypeFields(TextStyleType src1, TextStyleType src2) {
		if (src1==null || src2==null)
			return null;
		
		TextStyleType result = new TextStyleType();
		
		if (CoreUtils.val(src1.getFontFamily()).equals(CoreUtils.val(src2.getFontFamily())))
			result.setFontFamily(CoreUtils.val(src1.getFontFamily()));
		
		if (CoreUtils.val(src1.getFontSize()) == CoreUtils.val(src2.getFontSize()))
			result.setFontSize(CoreUtils.val(src1.getFontSize()));
		
		if (CoreUtils.val(src1.getKerning()) == CoreUtils.val(src2.getKerning()))
			result.setKerning(CoreUtils.val(src1.getKerning()));
		
		if (src1.getTextColour()==src2.getTextColour())
			result.setTextColour(src1.getTextColour());
		if (src1.getBgColour()==src2.getBgColour())
			result.setBgColour(src1.getBgColour());
		
		// boolean values are set no matter what:
		if (CoreUtils.val(src1.isSerif()) == CoreUtils.val(src2.isSerif())) result.setSerif(src1.isSerif());
		if (CoreUtils.val(src1.isMonospace()) == CoreUtils.val(src2.isMonospace())) result.setMonospace(src1.isMonospace());
		if (CoreUtils.val(src1.isReverseVideo()) == CoreUtils.val(src2.isReverseVideo())) result.setReverseVideo(src1.isReverseVideo());
		if (CoreUtils.val(src1.isBold()) == CoreUtils.val(src2.isBold())) result.setBold(src1.isBold());
		if (CoreUtils.val(src1.isItalic()) == CoreUtils.val(src2.isItalic())) result.setItalic(src1.isItalic());
		if (CoreUtils.val(src1.isUnderlined()) == CoreUtils.val(src2.isUnderlined())) result.setUnderlined(src1.isUnderlined());
		if (CoreUtils.val(src1.isSubscript()) == CoreUtils.val(src2.isSubscript())) result.setSubscript(src1.isSubscript());
		if (CoreUtils.val(src1.isSuperscript()) == CoreUtils.val(src2.isSuperscript())) result.setSuperscript(src1.isSuperscript());
		if (CoreUtils.val(src1.isStrikethrough()) == CoreUtils.val(src2.isStrikethrough())) result.setStrikethrough(src1.isStrikethrough());
		if (CoreUtils.val(src1.isSmallCaps()) == CoreUtils.val(src2.isSmallCaps())) result.setSmallCaps(src1.isSmallCaps());
		if (CoreUtils.val(src1.isLetterSpaced()) == CoreUtils.val(src2.isLetterSpaced())) result.setLetterSpaced(src1.isLetterSpaced());
		
		return result;
	}	
	
//	public static TextStyleType mergeTextStyleTypeFields(TextStyleType src, TextStyleType dst, boolean preserveFields) {
//	TextStyleType merged = new TextStyleType();
//	
//	merged.setFontFamily(CoreUtils.mergeObjects(src.getFontFamily(), dst.getFontFamily(), preserveFields));
//	merged.setSerif(CoreUtils.mergeBoolean(src.isSerif(), dst.isSerif()));
//	merged.setMonospace(CoreUtils.mergeBoolean(src.getMonospace(), dst.getMonospace()));
//	merged.setFontSize(CoreUtils.mergeObjects(src.getFontSize(), dst.getFontSize(), preserveFields));
//	merged.setKerning(CoreUtils.mergeObjects(src.getKerning(), dst.getKerning(), preserveFields));
//	merged.setTextColour(CoreUtils.mergeObjects(src.getTextColour(), dst.getTextColour(), preserveFields));
//	merged.setBgColour(CoreUtils.mergeObjects(src.getBgColour(), dst.getBgColour(), preserveFields));
//	merged.setReverseVideo(CoreUtils.mergeBoolean(src.isReverseVideo(), dst.isReverseVideo()));
//	merged.setBold(CoreUtils.mergeBoolean(src.isBold(), dst.isBold()));
//	merged.setItalic(CoreUtils.mergeBoolean(src.isItalic(), dst.isItalic()));
//	merged.setUnderlined(CoreUtils.mergeBoolean(src.isUnderlined(), dst.isUnderlined()));
//	merged.setSubscript(CoreUtils.mergeBoolean(src.isSubscript(), dst.isSubscript()));
//	merged.setSuperscript(CoreUtils.mergeBoolean(src.isSuperscript(), dst.isSuperscript()));
//	merged.setStrikethrough(CoreUtils.mergeBoolean(src.isStrikethrough(), dst.isStrikethrough()));
//	merged.setSmallCaps(CoreUtils.mergeBoolean(src.isSmallCaps(), dst.isSmallCaps()));
//	merged.setLetterSpaced(CoreUtils.mergeBoolean(src.isLetterSpaced(), dst.isLetterSpaced()));
//	
//	return merged;
//}

	/**
	 * Copies all fields from src to dst. If either src or dst are null, nothing is done.
	 */
	public static void copyTextStyleTypeFields(TextStyleType src, TextStyleType dst) {
		if (src == null || dst == null)
			return;
		
		dst.setBgColour(src.getBgColour());
		dst.setBold(src.isBold());
		dst.setFontFamily(src.getFontFamily());
		dst.setFontSize(src.getFontSize());
		dst.setItalic(src.isItalic());
		dst.setKerning(src.getKerning());
		dst.setLetterSpaced(src.isLetterSpaced());
		dst.setMonospace(src.isMonospace());
		dst.setReverseVideo(src.isReverseVideo());
		dst.setSerif(src.isSerif());
		dst.setSmallCaps(src.isSmallCaps());
		dst.setStrikethrough(src.isStrikethrough());
		dst.setSubscript(src.isSubscript());
		dst.setSuperscript(src.isSuperscript());
		dst.setTextColour(src.getTextColour());
		dst.setUnderlined(src.isUnderlined());	
	}
	
	/** If a global TextStyleType is set, apply it to the custom tag, if not leave custom tag as it is! */
	public static void applyTextStyleToCustomTag(ITrpShapeType shape) {
		TextStyleTag tst = getTextStyleAsCustomTag(shape);
		if (tst != null) { // there is a "global" text style type --> remove current text style tags and add new "global" text style tag
			shape.getCustomTagList().removeTags(TextStyleTag.TAG_NAME);
			logger.trace("global text style tst = "+tst);
			shape.getCustomTagList().addOrMergeTag(tst, null);
			logger.trace("synced custom tag: "+shape.getCustom());
		}
	}
	
	/** Returns the TextStyleTag object that covers the text of the whole shape if there is a TextStyleType set; null otherwise. */
	public static TextStyleTag getTextStyleAsCustomTag(ITrpShapeType shape) {
		if (shape == null || shape.getTextStyle()==null)
			return null;
		else {
			TextStyleTag tst = new TextStyleTag(shape.getTextStyle(), 0,  shape.getUnicodeText().length());
			logger.trace("covering text style: "+tst+" text = "+shape.getUnicodeText());
			return tst;
		}
	}
	
	/** Sets the <em>global</em> text style for the given shape. */
	public static void setTextStyleTag(ITrpShapeType shape, TextStyleType s, boolean recursive, Object who) {
		shape.setTextStyle(TextStyleTypeUtils.addTextStyleTypeFields(s, shape.getTextStyle(), true));
		TextStyleTypeUtils.applyTextStyleToCustomTag(shape);
		
		if (recursive) {
			for (ITrpShapeType c : shape.getChildren(recursive)) {
				c.getObservable().setActive(false);
				c.setTextStyle(s, recursive, who);
				c.getObservable().setActive(true);
			}
		}
		shape.getObservable().setChangedAndNotifyObservers(new TrpTextStyleChangedEvent(who));
	}
	
	/** Add this text style tag to the given shape. Also checks if the resulting text style tag is covering the whole area and then sets the global text style also if so. */
	public static void addTextStyleTag(ITrpShapeType shape, TextStyleTag s, String addOnlyThisProperty, /*boolean recursive,*/ Object who) {
		if (!(shape instanceof TrpTextRegionType || shape instanceof TrpTextLineType || shape instanceof TrpWordType))
			return;
		
		// add text style tag to custom list:
		shape.getCustomTagList().addOrMergeTag(s, addOnlyThisProperty);
		logger.debug("customtaglist="+shape.getCustomTagList());
		
		// apply text style tag to global text style if the text style tag is a single index tag over the whole range of the shape:
		boolean isS = shape.getCustomTagList().isSingleIndexedTagOverShapeRange(TextStyleTag.TAG_NAME);
		logger.debug("isSingleIndexedTagOverShapeRange: "+isS);
		
		boolean isActive = shape.getObservable().isActive(); // deactivate observer to avoid excessive events...
		shape.getObservable().setActive(false);
		
//		final boolean USE_GLOBAL_TEXT_STYLE = true; // using global text style has a bug... -> only stored in custom tag
//		if (USE_GLOBAL_TEXT_STYLE) {
			if (isS) {
//				logger.debug("HERE");
				shape.setTextStyle(s.getTextStyle(), false, shape);
			} else {
				shape.setTextStyle(null, false, shape);
			}
//		} 
//		else {
////			shape.setTextStyle(null);
//			shape.setTextStyle(null, false, shape); // erase global text style
//		}
		
		logger.debug("CUSTOM AFTER: "+shape.getCustom());
		
		shape.getObservable().setActive(isActive);
		
		// apply recursively:
//		if (recursive) {
//			for (ITrpShapeType c : shape.getChildren(recursive)) {
//				c.getObservable().setActive(false);
//				c.addTextStyleTag(s, addOnlyThisProperty, /*recursive,*/ who);
//				c.getObservable().setActive(true);
//			}	
//		}
		// send text style changed event:
		shape.getObservable().setChangedAndNotifyObservers(new TrpTextStyleChangedEvent(who));
	}

}
