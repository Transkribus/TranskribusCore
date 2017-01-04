package eu.transkribus.core.model.beans.customtags;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomTagAttribute {
	private final static Logger logger = LoggerFactory.getLogger(CustomTagAttribute.class);
	
	String name;
	String displayName;
	String description="";
	boolean isEditable=false;
	Class<?> type = Object.class;
	
	public static final boolean CASE_INSENSITVE_ATTRIBUTE_NAME = false;
	
	public CustomTagAttribute(String name) {
		this(name, true, null, null);
	}
	
	public CustomTagAttribute(String name, boolean isEditable, String displayName, String description) {
		Assert.assertFalse("The tag name cannot be empty!", StringUtils.isEmpty(name));
		
		this.name = name;
		this.isEditable = isEditable;
		this.displayName = displayName == null ? name : displayName;
		this.description = description == null ? "" : description;
		this.type = Object.class;
	}
	
	public CustomTagAttribute(String name, boolean isEditable, String displayName, String description, Class<?> type) {
		this(name, isEditable, displayName, description);
		this.type = type == null ? Object.class : type;
	}
	
	public CustomTagAttribute(CustomTagAttribute customTagAttribute) {
	    this.name = customTagAttribute.name;
	    this.displayName = customTagAttribute.displayName;
	    this.description = customTagAttribute.description;
	    this.isEditable = customTagAttribute.isEditable;
	    this.type = customTagAttribute.type;
	}
	
	
	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}
	
	private String getNameToCompare() {
		return CASE_INSENSITVE_ATTRIBUTE_NAME ? name.toLowerCase() : name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isBoolean() {
		return type!=null && (type.equals(Boolean.class) || type.equals(boolean.class));
	}
	
	public boolean isInteger() {
		return type!=null && (type.equals(Integer.class) || type.equals(int.class));
	}
	
	public boolean isString() {
		return type!=null && type.equals(String.class);
	}
	
	public boolean isDouble() {
		return type!=null && (type.equals(Double.class) || type.equals(double.class));
	}
	
	public boolean isFloat() {
		return type!=null && (type.equals(Float.class) || type.equals(float.class));
	}
	
	public boolean isFloatOrDouble() {
		return isFloat() || isDouble();
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}
	
	@Override public int hashCode() {
		return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
	            // if deriving: appendSuper(super.hashCode()).
	            append(getNameToCompare()).toHashCode();
	}
	
	@Override public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null || !(other instanceof CustomTagAttribute))
			return false;
		
		return this.getNameToCompare().equals( ((CustomTagAttribute)other).getName() );
	}

	@Override public String toString() {
	    final String TAB = ", ";
	    String retValue = "CustomTagAttribute ( "+super.toString();
		retValue += TAB + "name = " + this.name;
		retValue += TAB + "displayName = " + this.displayName;
		retValue += TAB + "description = " + this.description;
		retValue += TAB + "type = " + this.type;
		retValue += " )";
	    return retValue;
	}
	
	public static void main(String[] args) {
		Map<CustomTagAttribute, Object> map = new HashMap<>();
		
		CustomTagAttribute a1 = new CustomTagAttribute("asdf", true, null, null, String.class);
		CustomTagAttribute a2 = new CustomTagAttribute("asdf", true,  null, null);
		CustomTagAttribute a3 = new CustomTagAttribute("asdfaa", true, null, null);
		
		logger.info("a1 = "+a1);
		logger.info("a2 = "+a2);
		logger.info("a1 = "+a3);
		assertTrue("a1 == a2 ", a1.equals(a2));
		assertTrue("a2 == a1 ", a2.equals(a1));
		
		assertFalse("a2 != a1 ", a2.equals(a3));
		assertFalse("a2 != a1 ", a1.equals(a3));
		
		map.put(a1, null);
		
		assertTrue("map contains a2", map.containsKey(a2));
		assertFalse("map not contains a3", map.containsKey(a3));
				
		map.put(a2, null);
		map.put(a3, null);
		
		assertTrue("map size == 2: ", map.size()==2);
		
		logger.info("a1 hashcode "+a1.hashCode());
		logger.info("a2 hashcode "+a2.hashCode());
		logger.info("a3 hashcode "+a3.hashCode());
		
		assertTrue("hash a1 == a2: ", a1.hashCode() == a2.hashCode());
		assertTrue("hash a1 != a3: ", a1.hashCode() != a3.hashCode());
	}
	
}
