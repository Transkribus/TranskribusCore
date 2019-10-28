package eu.transkribus.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import eu.transkribus.core.model.beans.DocSelection;
import eu.transkribus.core.model.beans.DocumentSelectionDescriptor;
import eu.transkribus.core.model.beans.enums.EditStatus;

public class DocSelectionUtil {
	
	public static List<DocSelection> fromDocIds(List<Integer> docIds, Map<Integer, String> pages, EditStatus editStatus, Boolean doNotUseLatestTranscriptIfEditStatusNotFound) {
		List<DocSelection> ds = new ArrayList<>();
		for (int docId : docIds) {
			String pagesStr = pages!=null ? pages.get(docId) : null;
			ds.add(new DocSelection(docId, pagesStr, editStatus, doNotUseLatestTranscriptIfEditStatusNotFound));
		}
		return ds;
	}
	
	public static List<DocSelection> fromDocIds(int... docIds) {
		return fromDocIds(CoreUtils.asList(docIds), null, null, null);
	}
	
//	public static int getNumberOfPages(DocSelection ds, int nrOfPages) {
//		if (StringUtils.isEmpty(ds.getPages())) {
//			return nrOfPages;
//		}
//		else {
//			PagesStrUtil.getPageNrs(ds.getPages(), nrOfPages, false).size();
//		}
//		
//		
//	}
	
}
