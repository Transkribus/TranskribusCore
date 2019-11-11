package eu.transkribus.core.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eu.transkribus.core.model.beans.DocumentSelectionDescriptor;

public class GsonUtil {
	
	public static final Gson GSON = new Gson();
	
	public static final Type STRING_LIST_TYPE = new TypeToken<List<String>>(){}.getType();
	public static final Type INTEGER_LIST_TYPE = new TypeToken<List<Integer>>(){}.getType();
	public static final Type LIST_OF_INT_LISTS_TYPE = new TypeToken<List<List<Integer>>>(){}.getType();
	public static final Type DOCUMENT_SELECTION_DESCRIPTOR_LIST_TYPE = new TypeToken<List<DocumentSelectionDescriptor>>(){}.getType();
	
	public static final Type MAP_TYPE = new TypeToken<Map<String, Object>>(){}.getType();
	public static final Type MAP_TYPE_STRING_VALUES = new TypeToken<Map<String, String>>(){}.getType();
	public static final Type PAIR_OF_INT_TO_INT_MAP_TYPE = new TypeToken<Map<Pair<Integer, Integer>, Integer>>(){}.getType();
	
//	public static final Type MAP_TYPE = new TypeToken<Map<String, Object>>(){}.getType();
	
	public static String toJson(Object o, boolean prettyPrint) {
		Gson gson = prettyPrint ? new GsonBuilder().setPrettyPrinting().create() : new GsonBuilder().create();
		return gson.toJson(o);
	}
		
	public static String toJson(Object o) {	
		return GSON.toJson(o);
	}
	
	public static String toJson2(Object o) {
		try {
			return GSON.toJson(o);
		} catch (Throwable e) {
			return null;
		}
	}	
	
	public static List<List<Integer>> toListOfIntLists(String json) {
		return GSON.fromJson(json, LIST_OF_INT_LISTS_TYPE);
	}
	
	public static List<Integer> toIntegerList(String json) {
		return GSON.fromJson(json, INTEGER_LIST_TYPE);
	}
	
	public static List<String> toStrList(String json) {
		return GSON.fromJson(json, STRING_LIST_TYPE);
	}
	
	public static List<DocumentSelectionDescriptor> toDocumentSelectionDescriptorList(String json) {
		return GSON.fromJson(json, DOCUMENT_SELECTION_DESCRIPTOR_LIST_TYPE);
	}
	
	/**
	 * Generic toList method - creates a TypeToken depending on the type of the given clazz object 
	 * @deprecated does not seem to work => create dedicated toXXXList methods instead, cf. e.g. {@link #toDocumentSelectionDescriptorList}
	 */
	public static <T> List<T> toList(String json, Class<T> clazz) {
		Type type = new TypeToken<List<T>>(){}.getType();
		
		return (List<T>) GSON.fromJson(json, type);
	}
	
	/**
	 * Returns an empty list, if any exception is thrown deserializing the json string
	 */
	public static List<String> toStrList2(String json) {
		if (StringUtils.isEmpty(json))
			return new ArrayList<>();
		
		try {
			return toStrList(json);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}
	
	public static Map<String, Object> toMap(String json) {
		return GSON.fromJson(json, MAP_TYPE);
	}
	
	public static Map<String, String> toMapWithStringValues(String json) {
		return GSON.fromJson(json, MAP_TYPE_STRING_VALUES);
	}	
	
//	public static Map<Pair<Integer,Integer>, Object> toMap(String json) {
//		return GSON.fromJson(json, MAP_TYPE);
//	}	
	
	public static Map<String, Object> toMap2(String json) {
		if (StringUtils.isEmpty(json))
			return new HashMap<>();
		
		try {
			return toMap(json); 
		} catch (Exception e) {
			return new HashMap<>();
		}
	}
	
	public static Properties toProperties(String json) {
		Properties p = new Properties();
		p.putAll(toMap2(json));
		return p;
	}
	
	public static <T> T fromJson(String json, Type type) {
		return GSON.fromJson(json, type);
	}
	
	public static <T> T fromJson2(String json, Type type) {
		try {
			return fromJson(json, type);
		} catch (Exception e) {
			return null;
		}
	}	

	public static <T> T fromJson(String json, Class<T> clazz) {
		return GSON.fromJson(json, clazz);
	}
	
	public static <T> T fromJson2(String json, Class<T> clazz) {
		try {
			return fromJson(json, clazz);
		} catch (Exception e) {
			return null;
		}
	}
	
	

}
