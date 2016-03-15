package eu.transkribus.core.model.beans.customtags;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
//import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cedarsoftware.util.CaseInsensitiveMap;

import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.MyObservable;
import eu.transkribus.core.util.RegexPattern;

/**
 * Factory class to create CustomTag objects.<br>
 * Contains a registry that stores a list of derived CustomTag types (e.g. {@link TextStyleTag})
 */
public class CustomTagFactory {
	
//	public static class Property implements Map.Entry<String, Object> {
//		String key;
//		Object value;
//		
//		private Property(String key, Object value) {
//	        this.key = key;
//	        this.value = value;
//	    }
//		
//		public static Property create(String key, Object value) {
//			return new Property(key, value);
//		}
//
//		@Override public String getKey() {
//			return key;
//		}
//
//		@Override public Object getValue() {
//			return value;
//		}
//
//		@Override public Object setValue(Object value) {
//			return null;
//		}
//	};
	
		
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
	
	private final static BeanUtilsBean beanUtilsBean = new BeanUtilsBean(new EnumConvertUtilsBean());

	private final static Logger logger = LoggerFactory.getLogger(CustomTagFactory.class);
	
//	private static final Map<CustomTag, Constructor<? extends CustomTag> > registry = new HashMap<>();
//	private static final Map<String, Constructor<? extends CustomTag> > registry = new HashMap<>();
	
	// case insensitve maps:
	private static final Map<String, Class<? extends CustomTag> > registry = new CaseInsensitiveMap<>();
	private static final Map<String, CustomTag > objectRegistry = new CaseInsensitiveMap<>();
	private static final Map<String, String > colorRegistry = new CaseInsensitiveMap<>();
	
	// case sensitive maps:
//	private static final Map<String, Class<? extends CustomTag> > registry = new HashMap<>();
//	private static final Map<String, CustomTag > objectRegistry = new HashMap<>();
//	private static final Map<String, String > colorRegistry = new HashMap<>();
	
	public static final MyObservable registryObserver = new MyObservable();
	
	
	public static class TagRegistryChangeEvent {
		public static final String ADDED_TAG = "ADDED_TAG";
		public static final String REMOVED_TAG = "REMOVED_TAG";
		public static final String ADDED_TAG_ATTRIBUTES = "ADDED_TAG_ATTRIBUTES";
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
			CustomTagFactory.addToRegistry(new StructureTag());
			CustomTagFactory.addToRegistry(new ReadingOrderTag());
			CustomTagFactory.addToRegistry(new RegionTypeTag());
			
			// indexed:
			CustomTagFactory.addToRegistry(new TextStyleTag());
			CustomTagFactory.addToRegistry(new AbbrevTag());
			CustomTagFactory.addToRegistry(new PersonTag());
			CustomTagFactory.addToRegistry(new OrganizationTag());
			CustomTagFactory.addToRegistry(new PlaceTag());
			CustomTagFactory.addToRegistry(new SpeechTag());
			CustomTagFactory.addToRegistry(new DateTag());
			CustomTagFactory.addToRegistry(new WorkTag());
			CustomTagFactory.addToRegistry(new SicTag());
			CustomTagFactory.addToRegistry(new GapTag());
			CustomTagFactory.addToRegistry(new UnclearTag());
			CustomTagFactory.addToRegistry(new BlackeningTag());
			CustomTagFactory.addToRegistry(new SuppliedTag());
			
			CustomTagFactory.addToRegistry(new CommentTag()); // no color needed since extra rendering is done!
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
	
	/**
	 * Constructs a String for the config.properties file that stores non-predefined tags and additionals properties from predefined tags
	 */
	public static String constructTagDefPropertyForConfigFile() {
		String p = "";
		
		for (CustomTag t : objectRegistry.values()) {
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
			
	public static void addCustomDefinedTagsToRegistry(String tagNamesProp) {		
		logger.info("adding tags to registry, tagNamesProp = "+tagNamesProp);
		
		Matcher m = RegexPattern.TAG_DEFINITIONS_PATTERN.matcher(tagNamesProp);
		while (m.find()) {
			String tag = tagNamesProp.substring(m.start(), m.end());
			logger.debug("found tag: '"+tag+"'");
			
			String tagName = m.group(1);
			logger.debug("tagname = "+tagName);
		
			Map<String, Object> attributes = new HashMap<>();
			String atts = m.group(3);
			
			String color = null;
			if (atts != null)
				for (String a : m.group(3).split(",")) {
					a = a.trim();
					logger.debug("attribute = "+a);
					if (a.startsWith("#")) { // color attribute!
						logger.debug("setting color for tag "+tagName+" to: "+color);
						color = a;
					} else {
						attributes.put(a, null);	
					}
				}
			
			try {
				addToRegistry(CustomTagFactory.create(tagName, attributes), color);
			} catch (Exception e1) {
				logger.warn(e1.getMessage());
			}
		}
	}
	
	public static void removeFromRegistry(String tagName) throws IOException {
		if (registry.containsKey(tagName)) {
			CustomTag t = objectRegistry.get(tagName);
			if (t.isDeleteable()) {
				logger.debug("deleting tag '"+tagName+"'");
				
				colorRegistry.remove(tagName);
				objectRegistry.remove(tagName);
				registry.remove(tagName);
				
				TagRegistryChangeEvent e = new TagRegistryChangeEvent(TagRegistryChangeEvent.REMOVED_TAG, t);
				registryObserver.setChangedAndNotifyObservers(e);
			} else {
				throw new IOException("Cannot delete tag "+tagName+"'");
			}
		}
	}
	
	public static void addToRegistry(CustomTag ct) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
		addToRegistry(ct, null);
	}
	
	public static void addToRegistry(CustomTag ct, String color) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {		
//		Constructor<? extends CustomTag> c = ct.getClass().getConstructor();
		
		if (!registry.containsKey(ct.getTagName())) {
			CustomTag ctCopy = ct.copy();

			// register the class for this custom tag: 
			registry.put( ct.getTagName(), ctCopy.getClass() );
			// register the object for this custom tag:		
			logger.debug("adding prototype tag object to registry: "+ctCopy);
			objectRegistry.put(ct.getTagName(), ctCopy);
			
			if (color == null) // color not given -> get default color
				color = ct.getDefaultColor();
			
			if (!setTagColor(ct.getTagName(), color) && ct.showInTagWidget()) {
				color = getNewTagColor();
				setTagColor(ct.getTagName(), color);
			}
			
//			// check if given color is valid, if not, set it to null, s.t. it gets assigned in the next step:
//			if (color != null && !CoreUtils.isValidColorCode(color.toUpperCase())) {
//				logger.warn("no valid color specified: "+color+" tag: "+ct.getTagName());
//				color = null;
//			}
//			
//			// assign default color if not specified:
//			if (color == null && ct.showInTagWidget()) {
////				color = "#00000";
//				color = getNewTagColor();
//			}
//			
//			// set it in the registry:
//			if (color != null) {
//				color = color.toUpperCase();
//				colorRegistry.put(ct.getTagName(), color);
//			}
			
			TagRegistryChangeEvent e = new TagRegistryChangeEvent(TagRegistryChangeEvent.ADDED_TAG, ctCopy);
			registryObserver.setChangedAndNotifyObservers(e);
		} else {
			CustomTag t = objectRegistry.get(ct.getTagName());
			boolean addedAttributes = t.setAttributes(ct, false, false); // add attributes (without values!!!)
			
			if (addedAttributes) {
				TagRegistryChangeEvent e = new TagRegistryChangeEvent(TagRegistryChangeEvent.ADDED_TAG_ATTRIBUTES, t);
				registryObserver.setChangedAndNotifyObservers(e);
			}
			
			setTagColor(ct.getTagName(), color);
//			logger.warn("A tag with this name is already defined: "+ct.getTagName()+" - not adding to registry!");
		}
	}
	
	public static String getNewTagColor() {
		return ColorTable.getNewColor(colorRegistry.values());
	}

	public static String getTagColor(String tagName) {
		return colorRegistry.get(tagName);
	}
	
	public static boolean setTagColor(String tagName, String color) {
		if (CoreUtils.isValidColorCode(color)) {
			colorRegistry.put(tagName, color.toUpperCase());
			return true;
		} else {
//			logger.warn("no valid color specified: "+color+" tag: "+tagName);
			return false;
		}
	}
	
	public static Class<? extends CustomTag> getTagClassFromRegistry(String tagName) {
		if (tagName == null)
			return null;
		return registry.get(tagName);
	}
	
	public static CustomTag getTagObjectFromRegistry(String tagName) {
		if (tagName == null)
			return null;
		return objectRegistry.get(tagName);
	}
	
	public static Set<String> getRegisteredTagNames() { return registry.keySet(); }
	public static Collection<CustomTag> getRegisteredTagObjects() { return objectRegistry.values(); }
//	public static Map<String, CustomTag> getRegisteredObjects() { return objectRegistry; } 
	
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
			ct = new CustomTag(objectRegistry.get(tagName));
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
		
		addCustomDefinedTagsToRegistry("[Person{prop1, prop2, prop3,prop4  } Location Speech Address]");
	}

}
