package eu.transkribus.core.model.beans.customtags;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
//import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.MyObservable;
import eu.transkribus.core.util.RegexPattern;

/**
 * Factory class to create CustomTag objects.<br>
 * Contains a registry that stores a list of derived CustomTag types (e.g. {@link TextStyleTag})
 */
public class CustomTagFactory {
	
	public static class EnumConvertUtilsBean extends ConvertUtilsBean {
        @Override
        public Object convert(String value, Class clazz) {
              if (clazz.isEnum()){
                   return Enum.valueOf(clazz, value);
              }else {
                   return super.convert(value, clazz);
              }
       }
	};
	
	public static final String IS_EMPTY_TAG_PROPERTY_NAME = "_isEmptyTag";
	
	/**
	 * Contains the data for each CustomTag registry entry: a CustomTag prototype object,
	 * i.e. the CustomTag 'prototype' with all non-set properties,
	 * its class object, color, isEmptyTag, ...
	 */
	private static class CustomTagWrapper {
		public CustomTag customTag=null;
		public Class<? extends CustomTag> clazz=null;
		
		public String color=null;
		public boolean isEmptyTag=false;
		
		public CustomTagWrapper(CustomTag customTagToRegister, String color, boolean isEmptyTag) {
			if (customTagToRegister == null) {
				throw new IllegalArgumentException("customTag cannot be null!");
			}
			this.customTag = customTagToRegister.copy();
			this.customTag.setAttributes(customTagToRegister, true, false); // clear all attribute values
			this.clazz = customTagToRegister.getClass();
			this.color = color;
			this.isEmptyTag = isEmptyTag;
		}
	}
	
	
	private final static BeanUtilsBean beanUtilsBean = new BeanUtilsBean(new EnumConvertUtilsBean());

	private final static Logger logger = LoggerFactory.getLogger(CustomTagFactory.class);
	
//	private static final Map<CustomTag, Constructor<? extends CustomTag> > registry = new HashMap<>();
//	private static final Map<String, Constructor<? extends CustomTag> > registry = new HashMap<>();
	
	// case insensitve maps:
//	private static final Map<String, Class<? extends CustomTag> > registry = new CaseInsensitiveMap<>();
//	private static final ConcurrentMap<String, CustomTag > objectRegistry = new CaseInsensitiveMap<>();
//	private static final Map<String, String > colorRegistry = new CaseInsensitiveMap<>();
	
//	private static final Map<String, Class<? extends CustomTag> > registry = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);
//	private static final Map<String, CustomTag > objectRegistry = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);
	private static final Map<String, CustomTagWrapper > objectRegistry = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);
//	private static final Map<String, String > colorRegistry = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);
	
	// case sensitive maps:
//	private static final Map<String, Class<? extends CustomTag> > registry = new HashMap<>();
//	private static final Map<String, CustomTag > objectRegistry = new HashMap<>();
//	private static final Map<String, String > colorRegistry = new HashMap<>();
	
	private static final MyObservable registryObserver = new MyObservable();
	
	public static class TagRegistryChangeEvent {
		public static final String ADDED_TAG = "ADDED_TAG";
		public static final String REMOVED_TAG = "REMOVED_TAG";
		public static final String ADDED_TAG_ATTRIBUTES = "ADDED_TAG_ATTRIBUTES";
		public static final String CHANGED_TAG_COLOR="CHANGED_TAG_COLOR";
		
		public TagRegistryChangeEvent(String type, CustomTag tag) {
			super();
			this.type = type;
			this.tag = tag;
		}
		public String type;
		public CustomTag tag;
		
		public String toString() {
		    final String TAB = ", ";
		    String retValue = "TagRegistryChangeEvent ( "+super.toString();
			retValue += TAB + "type = " + this.type;
			retValue += TAB + "tag = " + this.tag;
			retValue += " )";
		    return retValue;
		}
	}
	
//	private static final Map<String, Pair<Class, CustomTag> > registry = new HashMap<>();
	
	static {
		try {
			// add some custom tags to the registry:
			
			// non-indexed:
			CustomTagFactory.addToRegistry(new StructureTag(), null, true, true);
			CustomTagFactory.addToRegistry(new ReadingOrderTag(), null, true, true);
			CustomTagFactory.addToRegistry(new RegionTypeTag(), null, true, true);
			
			// indexed:
			CustomTagFactory.addToRegistry(new TextStyleTag(), null, false, true);
			CustomTagFactory.addToRegistry(new AbbrevTag(), null, false, true);
			CustomTagFactory.addToRegistry(new PersonTag(), null, false, true);
			CustomTagFactory.addToRegistry(new OrganizationTag(), null, false, true);
			CustomTagFactory.addToRegistry(new PlaceTag(), null, false, true);
			CustomTagFactory.addToRegistry(new SpeechTag(), null, false, true);
			CustomTagFactory.addToRegistry(new DateTag(), null, false, true);
			CustomTagFactory.addToRegistry(new WorkTag(), null, false, true);
			CustomTagFactory.addToRegistry(new SicTag(), null, false, true);
			CustomTagFactory.addToRegistry(new GapTag(), null, true, true);
			CustomTagFactory.addToRegistry(new DivTag(), null, false, true);
			CustomTagFactory.addToRegistry(new UnclearTag(), null, false, true);
			CustomTagFactory.addToRegistry(new BlackeningTag(), null, false, true);
			CustomTagFactory.addToRegistry(new SuppliedTag(), null, false, true);
			CustomTagFactory.addToRegistry(new AdditionTag(), null, false, true);
			
			CustomTagFactory.addToRegistry(new CommentTag(), null, false, true); // no color needed since extra rendering is done!
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
//	public static void addPredifinedTagsToRegistry(String tagNamesProp) {
////		String tagNamesProp = TrpConfig.getTrpSettings().getTagNames();
//		logger.debug("tagNames = "+tagNamesProp);
//
//		Matcher wordMatcher = Pattern.compile("(\\w|_|-)+").matcher(tagNamesProp);
//		while (wordMatcher.find()) {
//			String tn = tagNamesProp.substring(wordMatcher.start(), wordMatcher.end());
//			logger.debug("adding tag: '"+tn+"'");
//			try {
//				addToRegistry(CustomTagFactory.create(tn));
//			} catch (Exception e1) {
//				logger.warn(e1.getMessage());
//			}
//		}
//	}
	
	public static void addObserver(Observer o) {
		registryObserver.addObserver(o);
	}
	
	public static void deleteObserver(Observer o) {
		registryObserver.deleteObserver(o);
	}
	
	/**
	 * Constructs a String for the config.properties file that stores non-predefined tags and additionals properties from predefined tags
	 * also stores if predfined color has changed
	 * 
	 * New: We store these values to the DB as user specific tag definitions
	 */
	public static String createTagDefPropertyForConfigFile() {
		String p = "";
		
		for (CustomTag t : getRegisteredCustomTags()) {
			if (!t.showInTagWidget())
				continue;
			
			Class<? extends CustomTag> clazz = t.getClass();
			
			boolean addToProp = true;
//			if (!clazz.equals(CustomTag.class)) { // predifined tag
			if (t.isPredefined()) {
//				logger.info("ss = "+t.getClass().getSimpleName()+" - "+t.getClass().isAssignableFrom(CustomTag.class));
				CustomTag tProto;
				try {
					tProto = clazz.newInstance();

					boolean hasSameColor = StringUtils.equalsIgnoreCase(tProto.getDefaultColor(), getTagColor(t.getTagName()));
					
					if (tProto.hasSameAttributes(t) && hasSameColor) {
						logger.debug(clazz.getSimpleName()+" has not changed properties - not adding to properties string!");
						addToProp = false;
					} else {
						logger.info(clazz.getSimpleName()+" has changed properties - adding to properties string!");
					}
				} catch (InstantiationException | IllegalAccessException e) {
					addToProp = false;
					logger.error(e.getMessage(), e);
				}
			}
			
			if (addToProp) {
				p += t.getTagName()+"{";
				String color = getTagColor(t.getTagName());
				if (color != null)
					p += color+",";
				boolean isEmptyTag = isEmptyTag(t.getTagName());
				if (isEmptyTag && !t.isPredefined()) {
					p += IS_EMPTY_TAG_PROPERTY_NAME+",";
				}
				
				for (String pn : t.getAttributeNames()) {
					if (!CustomTag.isOffsetOrLengthOrContinuedProperty(pn) && !t.isPredefinedAttribute(pn))
						p += pn+",";
				}
				
				p=StringUtils.removeEnd(p, ",");
				p+="} ";
			}
		}
		p = p.trim();
		
		return p;
	}
			
	public static void addLocalUserDefinedTagsToRegistry(String tagNamesProp) {		
		logger.info("adding local tags to registry, tagNamesProp = "+tagNamesProp);
		
		Matcher m = RegexPattern.TAG_DEFINITIONS_PATTERN.matcher(tagNamesProp);
		while (m.find()) {
			String tag = tagNamesProp.substring(m.start(), m.end());
			logger.debug("found tag: '"+tag+"'");
			
			String tagName = m.group(1);
			logger.debug("tagname = "+tagName);
		
			Map<String, Object> attributes = new HashMap<>();
			String atts = m.group(3);
			
			String color = null;
			boolean isEmptyTag=false;
			if (atts != null)
				for (String a : m.group(3).split(",")) {
					a = a.trim();
					logger.debug("attribute = "+a);
					if (a.startsWith("#")) { // color attribute!
						logger.debug("setting color for tag "+tagName+" to: "+color);
						color = a;
					}
					else if (a.equals(IS_EMPTY_TAG_PROPERTY_NAME)) {
						isEmptyTag=true;
					}
					else {
						attributes.put(a, null);	
					}
				}
			
			try {
				addToRegistry(CustomTagFactory.create(tagName, attributes), color, isEmptyTag, true);
			} catch (Exception e1) {
				logger.warn(e1.getMessage());
			}
		}
	}
	
//	/**
//	 * @deprecated does this even work? never used anyway...
//	 */
//	public static List<CustomTag> getCustomTagListFromProperties(String tagNamesProp) {		
//		List<CustomTag> cts = new ArrayList<CustomTag>();
//		
//		Matcher m = RegexPattern.TAG_DEFINITIONS_PATTERN.matcher(tagNamesProp);
//		while (m.find()) {
//			String tag = tagNamesProp.substring(m.start(), m.end());
//			logger.debug("found tag: '"+tag+"'");
//			
//			String tagName = m.group(1);
//			logger.debug("tagname = "+tagName);
//			
//			CustomTag ct = objectRegistry.get(tagName);
//		    cts.add(ct);
//		}
//		return cts;
//	}
	
	public static boolean removeFromRegistry(String tagName) throws IOException {
		CustomTag t = getCustomTag(tagName);
		if (t != null) {
			if (t.isDeleteable()) {
				logger.debug("deleting tag '"+tagName+"'");
				objectRegistry.remove(tagName);
				
				TagRegistryChangeEvent e = new TagRegistryChangeEvent(TagRegistryChangeEvent.REMOVED_TAG, t);
				registryObserver.setChangedAndNotifyObservers(e);
				return true;
			} else {
				throw new IOException("Cannot delete tag "+tagName+"'");
			}
		}
		return false;
	}
	
	/**
	 * Register the given tag in the tag registry. If it is already present, attributes are merged.
	 * @param ct The tag to register
	 * @param color The color for the tag. Set to <code>null</code> to use default color (CustomTag::getDefaultColor).<br>
	 * @param mergeAttributesIfAlreadyRegistered If the tag is already registered, attributes from ct will be merged into the existing registered tag.
	 * If no default color is set a new one is generated automatically.
	 */
	public static boolean addToRegistry(CustomTag ct, String color, boolean isEmptyTag, boolean mergeAttributesIfAlreadyRegistered) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
		CustomTag t = getCustomTag(ct.getTagName());
		if (t == null) { // not registered yet
			logger.debug("registering new tag: "+ct);
			if (color == null) { // color not given -> get default color
				color = ct.getDefaultColor();
			}
			if (!CoreUtils.isValidColorCode(color)) { // still no valid color --> create a new one
				color = getNewTagColor();
			}
			
			CustomTagWrapper cw = new CustomTagWrapper(ct, color, isEmptyTag);
			objectRegistry.put(ct.getTagName(), cw);
			
			TagRegistryChangeEvent e = new TagRegistryChangeEvent(TagRegistryChangeEvent.ADDED_TAG, cw.customTag);
			registryObserver.setChangedAndNotifyObservers(e);
			return true;
		} else if (mergeAttributesIfAlreadyRegistered) {
			boolean addedAttributes = t.setAttributes(ct, false, false); // add attributes (without values!!!)
			if (addedAttributes) {
				TagRegistryChangeEvent e = new TagRegistryChangeEvent(TagRegistryChangeEvent.ADDED_TAG_ATTRIBUTES, t);
				registryObserver.setChangedAndNotifyObservers(e);
			}
			// set new color (if it's a new one)
			if (CoreUtils.isValidColorCode(color) && !color.equals(getTagColor(ct.getTagName()))) {
				setTagColor(ct.getTagName(), color);	
			}				
//			logger.warn("A tag with this name is already defined: "+ct.getTagName()+" - not adding to registry!");
			return false;
		}
		return false;
	}
	
//	public static void addToRegistry(CustomTag ct, String color, boolean mergeAttributesIfAlreadyRegistered) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
//		addToRegistry(ct, color, false, mergeAttributesIfAlreadyRegistered);
//	}
	
	public static String getNewTagColor() {
		return ColorTable.getNewColor(getRegisteredColors());
	}

	public static String getTagColor(String tagName) {
		return objectRegistry.get(tagName)==null ? null : objectRegistry.get(tagName).color;
	}
	
	public static boolean setTagColor(String tagName, String color) {
		CustomTagWrapper cw = objectRegistry.get(tagName);
		
		if (CoreUtils.isValidColorCode(color) && cw != null) {
//			colorRegistry.put(tagName, color.toUpperCase());
			cw.color = color.toUpperCase();
			
			TagRegistryChangeEvent e = new TagRegistryChangeEvent(TagRegistryChangeEvent.CHANGED_TAG_COLOR, cw.customTag);
			registryObserver.setChangedAndNotifyObservers(e);
			return true;
		} else {
//			logger.warn("no valid color specified: "+color+" tag: "+tagName);
			return false;
		}
	}
	
	public static boolean isEmptyTag(String tagName) {
		CustomTagWrapper cw = objectRegistry.get(tagName);
		return cw != null ? cw.isEmptyTag : false;
	}
	
	public static void setIsEmptyTag(String tagName, boolean isEmptyTag) {
		CustomTagWrapper cw = objectRegistry.get(tagName);
		if (cw != null) {
			cw.isEmptyTag = isEmptyTag;
		}
	}
	
	public static List<CustomTag> getRegisteredCustomTags() {
		return objectRegistry.values().stream().map(tw -> tw.customTag).collect(Collectors.toList());
	}

	public static List<String> getRegisteredColors() {
		return objectRegistry.values().stream().map(tw -> tw.color).collect(Collectors.toList());
	}
	
	public static Class<? extends CustomTag> getTagClassFromRegistry(String tagName) {
		CustomTagWrapper cw = objectRegistry.get(tagName);
		if (tagName == null || cw == null) {
			return null;
		}
		return cw.clazz;
	}
	
	public static CustomTag getCustomTag(String tagName) {
		CustomTagWrapper cw = objectRegistry.get(tagName);
		if (cw != null) {
			return cw.customTag;
		}
		else {
			return null;
		}
	}
	
	public static CustomTag getTagObjectFromRegistry(String tagName) {
		return getCustomTag(tagName);
	}
	
	public static Set<String> getRegisteredTagNames() { return objectRegistry.keySet(); }
	
	public static List<String> getRegisteredTagNamesSorted() {
		List<String> tags = new ArrayList<>();
		tags.addAll(getRegisteredTagNames());
		Collections.sort(tags);
		return tags;
	}
	
//	public static Collection<CustomTag> getRegisteredTagObjects() { return objectRegistry.values(); }
//	public static Map<String, CustomTag> getRegisteredObjects() { return objectRegistry; } 
	
	public static List<CustomTag> getRegisteredTagObjectsSortedByName(boolean caseInsensitve) {
		List<CustomTag> registeredTagsSorted = new ArrayList<>(CustomTagFactory.getRegisteredCustomTags());
		Collections.sort(registeredTagsSorted, new Comparator<CustomTag>() {
			@Override
			public int compare(CustomTag t1, CustomTag t2) {
				if (caseInsensitve) {
					return t1.getTagName().toLowerCase().compareTo(t2.getTagName().toLowerCase());	
				}
				else {
					return t1.getTagName().compareTo(t2.getTagName());
				}
			}
		});
		
		return registeredTagsSorted;
	}
	
	public static Set<CustomTagAttribute> getTagAttributes(String tagName) {
		CustomTag t = getTagObjectFromRegistry(tagName);
		if (t == null)
			return null;
		
		return t.getAttributes();
	}
	
	public static CustomTagAttribute getAttribute(String tagName, String attributeName) {
		Set<CustomTagAttribute> atts = getTagAttributes(tagName);
		if (atts == null)
			return null;
		
		for (CustomTagAttribute a : atts) {
			if (a.getName().equals(attributeName)) {
				return a;
			}
		}
	
		return null;
	}
	
	public static CustomTag create(String tagName) throws Exception {
		return create(tagName, new HashMap<String, Object>());
	}
	
//	public static CustomTag create(String tagName, Property ...entries) throws Exception {
//		Map<String, Object> attributes = new HashMap<>();
//		for (Map.Entry<String, Object> e : entries) {
//			attributes.put(e.getKey(), e.getValue());
//		}
//		return create(tagName, attributes);
//	}
	
//	public static CustomTag create(String tagName, int offset, int length) throws Exception {
//		return create(tagName, Property.create(CustomTag.OFFSET_PROPERTY_NAME, offset), Property.create(CustomTag.LENGTH_PROPERTY_NAME, length));
//	}
	
	public static CustomTag create(String tagName, int offset, int length) throws Exception {
		return create(tagName, offset, length, null);
	}
	
	public static CustomTag create(String tagName, int offset, int length, Map<String, Object> attributes) throws Exception {
		CustomTag t = create(tagName, attributes);
		
		// add those attributes later to make sure they are set correclty according to the given parameters:
		t.setOffset(offset);
		t.setLength(length);
		
		return t;
	}
	
	/**
	 * Creates a CustomTag with the given name and attributes. 
	 * If the tagName is found in the tag registry the constructor of this derived tag is called, elsewise the default CustomTag constructor will be used.
	 * @param tagName
	 * @param attributes
	 * @return The created CustomTag object
	 */
	public static CustomTag create(String tagName, Map<String, Object> attributes) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IOException {
		Class<? extends CustomTag> tagClazz = getTagClassFromRegistry(tagName);
//		if (t==null)
//			throw new ClassNotFoundException("Class for tagName = "+tagName+" not found in registry - use addToRegistry method to register a CustomTag!");
		
		CustomTag ct = null;
		if (tagClazz != null && !tagClazz.equals(CustomTag.class)) { // found in registry for custom tags (i.e. not of class CustomTag!)
			ct = tagClazz.newInstance();
		} else if (objectRegistry.containsKey(tagName)) { // found in object registry for all custom tags
//			ct = new CustomTag(tagName);
			ct = new CustomTag(getCustomTag(tagName));
		} else // not found in registry --> use constructor with tagName in CustomTag class
			ct = new CustomTag(tagName);

		// set attributes:
		final boolean FORCE_ADDING_OF_ATTRIBUTES = true;
		if (attributes != null) {
			for (String an : attributes.keySet()) {
				logger.trace("setting attribute: "+an);
				ct.setAttribute(an, attributes.get(an), FORCE_ADDING_OF_ATTRIBUTES);
			}
			
			// FIXME: is this call necessary ?????
			beanUtilsBean.populate(ct, attributes);
		}
			
//		for (String attribute : attributes.keySet()) {
//			PropertyUtils.setProperty(ct, attribute, attributes.get(attribute));			
//		}
		
		return ct;
	}
	
	public static void main(String [] args) throws Exception {
//    	TextStyleTag ts = (TextStyleTag) create(TextStyleTag.TAG_NAME, 1, 3, null);	
//		logger.info(ts.toString());
		
		addLocalUserDefinedTagsToRegistry("[Person{prop1, prop2, prop3,prop4  } Location Speech Address]");
	}

}
