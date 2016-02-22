package eu.transkribus.core.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.ColourSimpleType;
import eu.transkribus.core.model.beans.pagecontent.PageTypeSimpleType;

public class EnumUtils {
	
	private final static Logger logger = LoggerFactory.getLogger(EnumUtils.class);
	
	/** Calls the value() method of the given enum. */
	public static <E extends Enum<E>> String value(E type) /*throws NoSuchMethodException, IllegalAccessException, InvocationTargetException*/ {
		try {
			return (String) MethodUtils.invokeExactMethod(type, "value", 
				new Object[]{});
		} catch (Exception e) {
			return null;
		}
	}
	
	public static <E extends Enum<E>> String getStr(E type) /*throws NoSuchMethodException, IllegalAccessException, InvocationTargetException*/ {
		try {
			return (String) MethodUtils.invokeExactMethod(type, "getStr", 
				new Object[]{});
		} catch (Exception e) {
			return null;
		}
	}
	
	/** Calls the fromValue(String v) method of the given enum. If the value was not found, null is returned instead of an IllegalArgumentException. */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E fromValue(Class<E> enumClass, String value) /*throws NoSuchMethodException, IllegalAccessException, InvocationTargetException*/ {
		try {
			return (E) MethodUtils.invokeExactStaticMethod(enumClass, "fromValue", 
					new Object[]{value});
		} catch (Exception e) {
			return null;
//			// if illegal argument given -> return null!
//			if (e.getCause() instanceof IllegalArgumentException) {
//				return null;
//			} else
//				throw e;
		}		
	}
	
	/** Calls the fromString(String v) method of the given enum. If the value was not found, null is returned instead of an IllegalArgumentException. */
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value) /*throws NoSuchMethodException, IllegalAccessException, InvocationTargetException*/ {
		try {
			return (E) MethodUtils.invokeExactStaticMethod(enumClass, "fromString", 
					new Object[]{value});
		} catch (Exception e) {
			return null;
		}		
	}	
	
	/** Returns all string values of the given enum as a string list. */
	public static <E extends Enum<E>> List<String> valuesList(Class<E> enumClass) {

		List<String> values = new ArrayList<String>();
		for (E en : EnumSet.allOf(enumClass)) {
			logger.trace("en = " + en + ", name = " + en.name());
			String value = value(en);
			if (value != null) {
				values.add(value);
			} else {
				logger.error("Could not parse value of enum type "+en+" - will not add to list!");	
			}
//			try {
//				String value = value(en);
////				values.add(value(en));
//			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//				logger.error("Could not parse value of enum type "+en+" - will not add to list!");
//			}
		}
		return values;
	}
	
	public static <E extends Enum<E>> List<String> stringsList(Class<E> enumClass) {

		List<String> values = new ArrayList<String>();
		for (E en : EnumSet.allOf(enumClass)) {
			logger.trace("en = " + en + ", name = " + en.name());
			String value = getStr(en);
			if (value != null) {
				values.add(value);
			} else {
				logger.error("Could not parse value of enum type "+en+" - will not add to list!");	
			}
//			try {
//				String value = value(en);
////				values.add(value(en));
//			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//				logger.error("Could not parse value of enum type "+en+" - will not add to list!");
//			}
		}
		return values;
	}
	
	/** Returns all string values of the given enum as a string array. */
	public static <E extends Enum<E>> String[] valuesArray(Class<E> enumClass) {
		return (String[]) valuesList(enumClass).toArray(new String[0]);
	}
	
	public static <E extends Enum<E>> String[] stringsArray(Class<E> enumClass) {
		return (String[]) stringsList(enumClass).toArray(new String[0]);
	}

	/** Returns all string values of the given enum as a string list. */
	public static <E extends Enum<E>> List<E> values(Class<E> enumClass) {

		List<E> values = new ArrayList<E>();
		for (E en : EnumSet.allOf(enumClass)) {
			values.add(en);
		}
		return values;
	}
	
	/** The index of the */
    public static <E extends Enum<E>> int indexOf(E type) {
    	if (type == null)
    		return -1;
    	
    	List<E> values = values(type.getClass());
    	for (int j=0; j<values.size(); j++) {
    		if (values.get(j) == type)
    			return j;
    		
    	}
    	return -1;
    }
	
	public static void main(String [] args) {
		valuesList(ColourSimpleType.class);
		
		logger.debug("fromValue = "+ fromValue(PageTypeSimpleType.class, "table-o-contents"));
		logger.debug("value = "+ value(PageTypeSimpleType.TABLE_OF_CONTENTS));

	}

}
