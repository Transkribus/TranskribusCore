package eu.transkribus.core.model.beans.customtags;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.exceptions.InitializationFailedException;
import eu.transkribus.core.model.beans.pagecontent.ColourSimpleType;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.IntRange;
import eu.transkribus.core.util.OverlapType;
import eu.transkribus.core.util.RegexPattern;

/**
 * Base class for custom tags. Includes the tag name and optional offset/length
 * attributes for indexed tags. All special custom tags with further attributes,
 * as e.g. {@link TextStyleTag} or {@link StructureTag} have to inherit from
 * this class.
 */
public class CustomTag implements Comparable<CustomTag> {
	final static Logger logger = LoggerFactory.getLogger(CustomTag.class);

	protected String tagName = null;

	CustomTagList customTagList=null; // a ref to the CustomTagList it is contained, can be null!
	
	public List<CustomTag> continuations=new ArrayList<>();
	
	protected int offset = -1;
	protected int length = -1;
	protected boolean continued = false;
	
//	protected String tagColor = null;
//	public static String TAG_COLOR_PROPERTY_NAME = "tagColor";
//	public static CustomTagAttribute TAG_COLOR_PROPERTY = new CustomTagAttribute(TAG_COLOR_PROPERTY_NAME, false, null, "The display color of the tag");
//	public String getTagColor() {
//		return tagColor;
//	}
//
//	public void setTagColor(String tagColor) {
//		this.tagColor = tagColor;
//	}	
	
//	protected String displayName;

	protected static HashSet<CustomTagAttribute> ATTRIBUTES = new HashSet<CustomTagAttribute>();
	
	public static String OFFSET_PROPERTY_NAME = "offset";
	public static CustomTagAttribute OFFSET_PROPERTY = new CustomTagAttribute(OFFSET_PROPERTY_NAME, false, null, "The character offset of the tag, relative to the line or word it is set");
	
	public static String LENGTH_PROPERTY_NAME = "length";
	public static CustomTagAttribute LENGTH_PROPETY = new CustomTagAttribute(LENGTH_PROPERTY_NAME, false, null, "The length in characters of the tag");
	
	public static String CONTINUED_PROPERTY_NAME = "continued";
	public static CustomTagAttribute CONTINUED_PROPERTY = new CustomTagAttribute(CONTINUED_PROPERTY_NAME, false, null,
			"Determines if this tag is a continuation from a tag of the previous line or word");
		
//	public static 
	// TODO: insert color tag

	// Map<String, Object> attributes = new HashMap<>(); /** a set of additional
	// attributes */
	
	Map<CustomTagAttribute, Object> attributes = new HashMap<>();
	
	private static BeanUtilsBean BEAN_UTILS_BEAN = new BeanUtilsBean();
	static {		
		// register custom converters:
		BEAN_UTILS_BEAN.getConvertUtils().register(new ColourSimpleTypeConverter(), ColourSimpleType.class);	
	}

	/** a set of additional attributes */

	/* public */CustomTag(String tagName) throws InitializationFailedException {
		// Assert.assertTrue("tagName null or empty - not allowed!",
		// tagName!=null && !tagName.isEmpty());
		if (!RegexPattern.TAG_NAME_PATTERN.matches(tagName)) {
			throw new InitializationFailedException("Tag name not valid: " + tagName + ", syntax: " + RegexPattern.TAG_NAME_PATTERN.getDescription());
		}

		this.tagName = tagName;

		this.attributes = new HashMap<CustomTagAttribute, Object>();		
	}

	/* public */CustomTag(String tagName, int offset, int length) throws InitializationFailedException {
		this(tagName);
		setOffset(offset);
		setLength(length);
//		this.offset = offset;
//		this.length = length;
	}

	public CustomTag(CustomTag customTag) {
		this(customTag.tagName, customTag.offset, customTag.length);
//		this.tagName = customTag.tagName;
//		this.offset = customTag.offset;
//		this.length = customTag.length;
		this.continued = customTag.continued;
		
//		this.displayName = customTag.displayName;

		// copy attributes
		for (CustomTagAttribute k : customTag.attributes.keySet()) {
			this.attributes.put(k, customTag.attributes.get(k));
		}
//		this.attributes = new HashMap<CustomTagAttribute, Object>(customTag.attributes);
	}
		
	public boolean isPredefined() {
		return !getClass().equals(CustomTag.class);
	}

	public CustomTagList getCustomTagList() {
		return customTagList;
	}
	
	public String getDefaultColor() {
		return null;
	}
	
	public String getTextOfShape() {
		if (customTagList == null)
			return "";
		
		return customTagList.getShape().getUnicodeText();
	}
	
	public String getContainedText() {
		if (customTagList == null)
			return "";
		
		String txt = customTagList.getShape().getUnicodeText();
		
		int o = CoreUtils.bound(offset, 0, txt.length());
		int e = CoreUtils.bound(offset+length, 0, txt.length());
		
		/**
		 * FIXME Prod Server doc 331 on page 22 has illegal tags (outside of string bounds with no length)
		 * Then o = 21 and e = 20 which leads to a StringIndexOutOfBoundsException. Code below fixes that for now.
		 * philip
		 */
		
		final String containedText;
		if(o > e) {
			containedText = "";
		} else {
			containedText = txt.substring(o, e);
		}
		return containedText;
	}
	
	public boolean isDeleteable() {
		return true;
	}
	
	protected void reset(boolean withIndices) {
		if (withIndices) {
			offset = -1;
			length = -1;
		}
		continued = false;
		deleteCustomAttributes();
	}

	public boolean showInTagWidget() {
		return true;
	}

//	public boolean canAddAttribute() {
//		return true;
//	}
	
	public boolean equalsTagName(CustomTag t) {
		return (t != null && tagName.equals(t.tagName));
	}

//	public void addAttribute(CustomTagAttribute attribute, boolean overwriteExisting) throws IOException {
//		if (!canAddAttribute()) {
//			throw new IOException("Cannot add attribute to "+tagName+" - adding attributes is not allowed for this tag");
//		}
//
//		if (isPredefinedAttribute(attribute.getName())) {
//			throw new IOException("Cannot add attribute to "+tagName+" tag - it is a predefined attribute: " + attribute.getName());
//		}
//
//		// delete attribute first, s.t. key gets replaced!!!
//		if (attributes.containsKey(attribute)) {
//			if (!overwriteExisting)
//				throw new IOException("Cannot add attribute to "+tagName+" tag - it is already defined: " + attribute.getName());
//			else
//				attributes.remove(attribute);
//		}
//			
//		attributes.put(attribute, null);
//	}
	
	public boolean setAttributes(CustomTag ct, boolean withIndices) {
		return setAttributes(ct, withIndices, true);
	}
	
	public boolean setAttributes(CustomTag ct, boolean withIndices, boolean withValues) {
		logger.trace("setting attributes from tag: "+tagName);
		if (!getTagName().equals(ct.getTagName())) { // do nothing if tag names are not equal
			logger.warn("trying to add attributes from different tags - skipping: "+getTagName()+ "/"+ct.getTagName());
			return false;
		}
		
		if (!getClass().equals(ct.getClass())) {
			logger.warn("trying to add attributes from different classes - skipping: "+getClass().getSimpleName()+ "/"+ct.getClass().getSimpleName());
			return false;			
		}
		
//		for (CustomTagAttribute a : ct.attributes.keySet()) {
		boolean addedAttributes = false;
		for (String an : ct.getAttributeNames()) {
			if (!withIndices && isOffsetOrLengthProperty(an))
				continue;
			
			try {
				if (setAttribute(an, withValues ? ct.getAttributeValue(an) : null, true))
					addedAttributes = true;
			} catch (IOException e) {
				logger.error("Error setting attribute '"+an+"' : "+e.getMessage(), e);
			}
		}
		
		return addedAttributes;
	}

	public void deleteCustomAttribute(String attributeName) throws IOException {
//		if (!canAddAttribute()) {
//			throw new IOException("Cannot delete attributes from this tag type: " + getTagName());
//		}

		if (isPredefinedAttribute(attributeName)) {
			throw new IOException("Cannot delete attribute: it is a predefined attribute: " + attributeName);
		}

		CustomTagAttribute cta = new CustomTagAttribute(attributeName);
		attributes.remove(cta);
	}
	
	public void deleteCustomAttributes() {
		attributes.clear();
	}
	
	public boolean setAttribute(String name, Object value, boolean forceAdd) throws IOException {
		return setAttribute(name, value, null, forceAdd);
	}

	public boolean setAttribute(String name, Object value, Class<?> type, boolean forceAdd) throws IOException {
		boolean addedAttribute = false;
		// TODO: check if this is a predefined property:
		if (isPredefinedAttribute(name)) {
			try {
				logger.trace("setting property: "+name+" value: "+value+" type: "+(value==null ? "" : value.getClass().getSimpleName()));
				
//				BeanUtilsBean b = new BeanUtilsBean();
//				b.getConvertUtils().register(new ColourSimpleTypeConverter(), ColourSimpleType.class);

				BEAN_UTILS_BEAN.setProperty(this, name, value);
			} catch (Exception e) {
				logger.error("Error setting property '" + name + "': " + e.getMessage(), e);
			}
		} else {
			CustomTagAttribute att = getAttribute(name);
			if (att == null) {
				if (!forceAdd)
					throw new IOException("An attribute with this name does not exist: " + name);
				else {
					att = new CustomTagAttribute(name);
					if (type != null)
						att.setType(type);
					addedAttribute = true;
				}
				
			}

			//logger.debug("putting value for attribute: " + name + ": " + value);
			attributes.put(att, value);
		}
		
		return addedAttribute;
	}

	public Object getAttributeValue(String name) {
		if (isPredefinedAttribute(name)) {
			try {
				return PropertyUtils.getProperty(this, name);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				logger.error("Error getting property '" + name + "': " + e.getMessage(), e);
				return null;
			}
		} else {
			return attributes.get(new CustomTagAttribute(name, true, null, null));
		}

	}

	public CustomTagAttribute getAttribute(String name) {
		for (CustomTagAttribute a : getAttributes()) {
			if (a.getName().equals(name))
				return a;
		}
		return null;
	}

	public Class<?> getAttributeType(String name) {
		if (!hasAttribute(name))
			return null;

		if (isPredefinedAttribute(name)) { // get type via reflection for
											// predefined attributes
			try {
				return PropertyUtils.getPropertyType(this, name);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				logger.error(e.getMessage(), e);
				return null;
			}
		} else {
			CustomTagAttribute att = getAttribute(name);
			return att.getType();
		}
	}

	protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = new HashSet<>();
		atts.add(OFFSET_PROPERTY);
		atts.add(LENGTH_PROPETY);
		atts.add(CONTINUED_PROPERTY);
		
//		atts.add(TAG_COLOR_PROPERTY);

		return atts;
	}

	public boolean isPredefinedAttribute(String attrName) {
		for (CustomTagAttribute a : getPredefinedAttributes())
			if (a.getName().equals(attrName))
				return true;

		return false;
	}

	public boolean hasAttribute(String attrName) {
		return getAttributeNames().contains(attrName);
	}
	
	public boolean hasSameAttributes(CustomTag t) {
		Set<String> a1 = getAttributeNames();
		Set<String> a2 = t.getAttributeNames();
		return (a1.size() == a2.size() && a1.containsAll(a2));
	}
	
	public Set<String> getAttributeNames() {
		Set<String> attNames = new HashSet<>();
		for (CustomTagAttribute a : attributes.keySet()) {
			attNames.add(a.getName());
		}
		for (CustomTagAttribute a : getPredefinedAttributes()) {
			attNames.add(a.getName());
		}

		// Set<String> attNames = new HashSet<String>(attributes.keySet());
		// for (CustomTagAttribute an : getPredefinedAttributes())
		// attNames.add(an.getName());

		return attNames;
	}

	public Set<CustomTagAttribute> getAttributes() {
		Set<CustomTagAttribute> atts = new HashSet<>(getPredefinedAttributes());
		atts.addAll(attributes.keySet());

		// for (CustomTagAttribute an : attributes.keySet()) {
		// atts.add(new CustomTagAttribute(an, "", "custom attribute"));
		// }

		return atts;
	}
	
	public Map<CustomTagAttribute, Object> getAttributesValuesMap() {
		Map<CustomTagAttribute, Object> atts = new HashMap<>();
		for (CustomTagAttribute c : getAttributes()) {
			atts.put(c, getAttributeValue(c.getName()));
		}
		return atts;
	}
	
	public Map<String, Object> getAttributeNamesValuesMap() {
		Map<String, Object> atts = new HashMap<>();
		for (CustomTagAttribute c : getAttributes()) {
			atts.put(c.getName(), getAttributeValue(c.getName()));
		}
		return atts;
	}

	public boolean hasRange(int offset, int length) {
		return this.length == length && this.offset == offset;
	}

	public IntRange getRange() {
		return new IntRange(offset, length);
	}

	/** @see CustomTag#getOverlapType(int, int) */
	public OverlapType getOverlapType(CustomTag st) {
		return getOverlapType(st.offset, st.length);
	}

	/** @see IntRange#getOverlapType(int, int) */
	public OverlapType getOverlapType(int o, int l) {
		return getRange().getOverlapType(o, l);
	}

	public boolean isInside(int offset) {
		return getRange().isInside(offset);
	}

	public boolean isEmptyValued() {
		return false;
	}

//	/**
//	 * Adds fields from src tag to this tag. Default implementation does
//	 * nothing, as the default CustomTag has no fields. Overwrite this method in
//	 * derived classes with fields (as in {@link TextStyleTag})
//	 */
//	public void addFieldsFrom(CustomTag src) {
//	}

	/**
	 * Merges fields that are equal in this custom tag with the ones in the src
	 * tag. Default implementation does nothing.
	 * @param withIndices 
	 */
	public void mergeEqualAttributes(CustomTag src, boolean withIndices) {
		CustomTag t = CustomTagUtil.mergeEqualAttributes(this, src, withIndices);
		
		if (t != null) {
			this.reset(withIndices);
			this.setAttributes(t, withIndices);	
		}
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public boolean isContinued() {
		return continued;
	}

	public void setContinued(boolean continued) {
		this.continued = continued;
	}
	
	

//	public String getDisplayName() {
//		return displayName == null ? tagName : displayName;
//	}
//
//	public void setDisplayName(String displayName) {
//		this.displayName = displayName;
//	}
	
	/**
	 * Returns the <em>non-inclusive</em> end index, i.e
	 * <code>offset + length</code>
	 */
	public int getEnd() {
		return offset + length;
	}

	public String getTagName() {
		return tagName;
	}
	
	public static boolean isOffsetOrLengthProperty(String name) {
		return OFFSET_PROPERTY_NAME.equals(name) || LENGTH_PROPERTY_NAME.equals(name);
	}

	public static boolean isOffsetOrLengthOrContinuedProperty(String name) {
		return OFFSET_PROPERTY_NAME.equals(name) || LENGTH_PROPERTY_NAME.equals(name) || CONTINUED_PROPERTY_NAME.equals(name);
	}

	/**
	 * Converts this CustomTag object into a CSS-syntax style string including
	 * all its attributes
	 */
	public String getCssStr() {
		String css = getTagName() + " {";
		String S = "";
		if (isIndexed()) {
			css += OFFSET_PROPERTY_NAME + ":" + this.offset + "; " + LENGTH_PROPERTY_NAME + ":" + this.length + ";";
			S = " ";
		}
		if (isContinued()) {
			css += S + CONTINUED_PROPERTY_NAME + ":" + this.continued + ";";
			S = " ";
		}
		// add attributes:
		css += getAttributeCssStr();
//		for (CustomTagAttribute a : getAttributes()) {
//			if (!isOffsetOrLengthOrContinuedProperty(a.getName())) {
//				Object v = getAttributeValue(a.getName());
//				if (CoreUtils.isValueSet(v)) { // only if set
//					v = CssSyntaxTag.escapeCss(""+v); // escape attribute!
//					css += S + a.getName() + ":" + v + ";";
//					S = " ";
//				}
//			}
//		}

		css += "}";

		return css;
	}
	
	public String getAttributeCssStr() {
		String css = "";
		String S = "";
		for (CustomTagAttribute a : getAttributes()) {
			if (!isOffsetOrLengthOrContinuedProperty(a.getName())) {
				Object v = getAttributeValue(a.getName());
				if (CoreUtils.isValueSet(v)) { // only if set
					v = CssSyntaxTag.escapeCss(""+v); // escape attribute!
					css += S + a.getName() + ":" + v + ";";
					S = " ";
				}
			}
		}
		
		return css;
	}

	@Override public String toString() {
		return getCssStr();
	}

	public CustomTag copy() {
		return new CustomTag(this);
	}

	public boolean isIndexed() {
		return length >= 0 && offset >= 0;
	}

	public boolean isEmpty() {
		return isIndexed() && length == 0;
	}
	
	public boolean canBeEmpty() {
		return false;
	}

	/**
	 * Determines if a tag is resumed if the cursor sits at the end of this tag
	 * - e.g. when a user writes a text that is bold and wants to extend the
	 * bold text by placing the cursor at the end of the text currently written
	 * in bold. The same behaviour is not desired e.g. for a person name tag.
	 */
	public boolean sneakToLeft() {
		return false;
	}

	/**
	 * Compares two CustomTag object by their offset values if they are both
	 * indexed and have the same tag name. Otherwise, non-indexed tags are
	 * always smaller than indexed, if both are non-indexed or have non-equal
	 * tag-names, they are compared by their tag name.
	 */
	@Override public int compareTo(CustomTag arg0) {
		if (!isIndexed() && !arg0.isIndexed()) // both not indexed
			return tagName.compareTo(arg0.tagName);
		else if (!isIndexed() && arg0.isIndexed()) // this indexed, given not
			return -1;
		else if (isIndexed() && !arg0.isIndexed()) // this not indexed, given
													// indexed
			return 1;
//		else if (!tagName.equals(arg0.tagName)) // both indexed but not the same
//												 tag-name
//			return tagName.compareTo(arg0.tagName);
		
		
		// NEW version: both indexed and same tag-name -> compare by offset and length:		
		int offComp = new Integer(this.offset).compareTo(arg0.offset);
		if (offComp == 0) { // if offsets are equal compare by length: larger tag comes first!
			return -1 * new Integer(this.length).compareTo(arg0.length);
		} else
			return offComp;
		
		// OLD version: just compare by offset:
//		return new Integer(this.offset).compareTo(arg0.offset);
	}

//	public boolean equals(CustomTag compare) {
//		return equalsEffectiveValues(compare, true);
//	}

	public boolean equalsEffectiveValues(CustomTag compare, boolean withIndices) {
		if (!tagName.equals(compare.tagName))
			return false;
		if ( withIndices && (offset != compare.offset && length != compare.length) ) {
			return false;
		}
		
		if (!equalsCustomAttributes(compare))
			return false;
		
		return true;
	}
	
	protected boolean equalsCustomAttributes(CustomTag c) {
		// NEW:
		for (CustomTagAttribute a : getAttributes()) {
			if (isOffsetOrLengthOrContinuedProperty(a.getName()))
				continue;
			
			Object v1 = getAttributeValue(a.getName());
			Object v2 = c.getAttributeValue(a.getName());		
		
		// OLD: buggy most likely -> only considered attributes defined by user!!
//		for (CustomTagAttribute a : attributes.keySet()) {
//			Object v1 = attributes.get(a);
//			Object v2 = c.attributes.get(a);
			
			logger.trace("comparing values of attribute: "+a.getName()+" v1 = "+v1+" v2 = "+v2);
			if (!CoreUtils.equalsObjects(v1, v2))
				return false;
		}
		return true;
	}

	/**
	 * Determines if a this tag is merged with an adjacent neighbor tag when
	 * added in a {@link CustomTagList}
	 */
	public boolean mergeWithEqualValuedNeighbor() {
		return false;
	}

	public static void main(String[] args) {
		TextStyleTag t = new TextStyleTag();
		t.setBold(true);
		t.setFontSize(0.0f);
		t.setKerning(0);

		StructureTag st = new StructureTag("BLA");

		ReadingOrderTag ro = new ReadingOrderTag(10);

		logger.info(t.getCssStr());
		// logger.info(t.getCssStr2());

		logger.info(st.getCssStr());
		// logger.info(st.getCssStr2());

		logger.info(ro.getCssStr());
		// logger.info(ro.getCssStr2());

	}

}