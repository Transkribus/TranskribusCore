package eu.transkribus.core.util;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PagesStrUtil {
	
	public static List<Integer> getPageIndices(String pagesStr, int nPages, boolean sorted) throws IOException {
		List<Integer> pageIndices = CoreUtils.parseRangeListStrToList(pagesStr, nPages);
		if (sorted) {
			Collections.sort(pageIndices);
		}
		return pageIndices;
	}
	
	public static List<Integer> getPageNrs(String pagesStr, int nPages, boolean sorted) throws IOException {
		return getPageIndices(pagesStr, nPages, sorted).stream().map(i -> i+1).collect(Collectors.toList());
	}

}
