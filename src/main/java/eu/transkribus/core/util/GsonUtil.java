package eu.transkribus.core.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GsonUtil {
	
	public static final Gson GSON = new Gson();
	
	public static final Type STRING_LIST_TYPE = new TypeToken<List<String>>(){}.getType();
	
	public static final Type MAP_TYPE = new TypeToken<Map<String, Object>>(){}.getType();
		
	public static String toJson(Object o) {	
		return GSON.toJson(o);
	}
	
	public static List<String> toStrList(String json) {
		return GSON.fromJson(json, STRING_LIST_TYPE);
	}
	
	/**
	 * Returns an empty list, if any exception is thrown deserializing the json string
	 */
	public static List<String> toStrList2(String json) {
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
		try {
			return GSON.fromJson(json, MAP_TYPE);
		} catch (Exception e) {
			return new HashMap<>();
		}
	}	
	
	

}
