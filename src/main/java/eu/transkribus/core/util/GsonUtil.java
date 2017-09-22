package eu.transkribus.core.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.transkribus.core.model.beans.DocumentSelectionDescriptor;

public class GsonUtil {
	
	public static final Gson GSON = new Gson();
	
	public static final Type STRING_LIST_TYPE = new TypeToken<List<String>>(){}.getType();
	public static final Type DOCUMENT_SELECTION_DESCRIPTOR_LIST_TYPE = new TypeToken<List<DocumentSelectionDescriptor>>(){}.getType();
	
	public static final Type MAP_TYPE = new TypeToken<Map<String, Object>>(){}.getType();
		
	public static String toJson(Object o) {	
		return GSON.toJson(o);
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
