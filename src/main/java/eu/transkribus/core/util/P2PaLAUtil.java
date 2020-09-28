package eu.transkribus.core.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class P2PaLAUtil {
	private static final Logger logger = LoggerFactory.getLogger(P2PaLAUtil.class);
	
	public static Set<String> getUniqueStructTypes(String structTypes) {
		return StringUtils.isEmpty(structTypes) ? new TreeSet<>() : new TreeSet<>(Arrays.asList(structTypes.split("\\s+")).stream().distinct().sorted().collect(Collectors.toList()));
	}
	
	public static String getUniqueStructTypesString(String structTypes) {
		return StringUtils.join(getUniqueStructTypes(structTypes), " ");
	}
	
	public static Map<String, Set<String>> getStructTypesMap(String structTypesMapStr) throws IllegalArgumentException {
		Map<String, Set<String>> m = new TreeMap<>();
		if (StringUtils.isEmpty(structTypesMapStr)) {
			return new HashMap<>();
		}
		
		for (String s : structTypesMapStr.trim().split("\\s+")) {
			s = s.trim();
			String[] s12 = s.split(":");
			if (s12.length != 2) {
				throw new IllegalArgumentException("Not a valid merged structure type specificiation: "+s);
			}
			String struct = s12[0].trim();
			String merge = s12[1].trim();
			
			if (StringUtils.equals(struct, merge)) {
				continue;
//				throw new IllegalArgumentException("Cannot merge equal structures: '"+s+"'");
			}
			
			Set<String> v = m.get(struct);
			if (v==null) {
				v = new TreeSet<>();
			}
			v.addAll(CoreUtils.parseStringList(merge, true));
			v.remove(struct); // prevent entries equal to the struct key
			
			if (!v.isEmpty()) {
				m.put(struct, v);	
			}
			
//			for (int i=0; i<2; ++i) {
//				if (!isStruct(s12[i])) {
//					logger.debug("Invalid merged structure types - struct not specified: "+s12[i]);
//					throw new Exception("Invalid merged structure types - struct not specified: "+s12[i]);
//				}
//			}
		}
		return m;
	}
	
	public static Set<String> getValuesFromStructTypeMap(String structTypesMapStr) {
		return getValuesFromStructTypeMap(getStructTypesMap(structTypesMapStr));
	}
	
	public static Set<String> getValuesFromStructTypeMap(Map<String, Set<String>> structTypesMap) {
		Set<String> v = new TreeSet<>();
		for (String k : structTypesMap.keySet()) {
			v.addAll(structTypesMap.get(k));
		}
		return v;
	}
	
	public static String getUniqueStructTypesMapStr(String structTypesMapStr) {
		Map<String, Set<String>> m = getStructTypesMap(structTypesMapStr);
		String s="";
		for (String k : m.keySet()) {
			s+=k+":"+CoreUtils.join(m.get(k))+" ";
		}
		return s.trim();
	}
	
	public static Pair<String, String> getUniqueStructsAndMergedStructs(String structStr, String mergedStructsStr) {
		Set<String> structs = getUniqueStructTypes(structStr);
		structs.removeAll(getValuesFromStructTypeMap(mergedStructsStr));
		String structsStr2 = StringUtils.join(structs, " ");
		String mergedStructsStr2 = getUniqueStructTypesMapStr(mergedStructsStr);
		return Pair.of(structsStr2, mergedStructsStr2);
	}
	
	public static void main(String[] args) {
		String structsStr = "s1 s10 s11 s11 s3 s4";
		String structTypesMapStr = "s1:s1 s1:a,b,c s2:s4 s5:s7,s8 s9:s9 s1:s2,s3,s1 s1:s10";
		String structTypesMapStr2 = getUniqueStructTypesMapStr(structTypesMapStr);
		System.out.println(structTypesMapStr2);
		System.out.println(getValuesFromStructTypeMap(structTypesMapStr));
		System.out.println(getValuesFromStructTypeMap(structTypesMapStr2));
		System.out.println("------");
		System.out.println(getUniqueStructsAndMergedStructs(structsStr, structTypesMapStr).getLeft());
		System.out.println(getUniqueStructsAndMergedStructs(structsStr, structTypesMapStr).getRight());
		
	}

}
