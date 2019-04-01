package eu.transkribus.core.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import eu.transkribus.core.model.beans.DocumentSelectionDescriptor;
import eu.transkribus.core.model.beans.DocumentSelectionDescriptor.PageDescriptor;
import eu.transkribus.core.model.beans.GroundTruthSelectionDescriptor;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpGroundTruthPage;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.enums.DataSetType;
import eu.transkribus.core.model.beans.enums.EditStatus;

public class DescriptorUtils {
	
	/**
	 * Old method for building a descriptor list in the basis of document map as it is given in the HtrTrainingDialog of TranskribusSwtGui
	 * 
	 * @param map
	 * @param useGt choose GT versions if available
	 * @param useInitial choose most recent NEW versions (overridden by useGt=true) 
	 * @return
	 */
	@Deprecated
	public static List<DocumentSelectionDescriptor> buildCompleteSelectionDescriptorListOld(Map<TrpDocMetadata, List<TrpPage>> map, boolean useGt, boolean useInitial) {
		List<DocumentSelectionDescriptor> list = new LinkedList<>();

		for (Entry<TrpDocMetadata, List<TrpPage>> e : map.entrySet()) {
			DocumentSelectionDescriptor dsd = new DocumentSelectionDescriptor();
			dsd.setDocId(e.getKey().getDocId());
			for (TrpPage p : e.getValue()) {
				PageDescriptor pd = new PageDescriptor();
				pd.setPageId(p.getPageId());
				pd.setTsId(p.getCurrentTranscript().getTsId());
				if (useGt || useInitial) {
					for (TrpTranscriptMetadata t : p.getTranscripts()) {
						if (useGt && t.getStatus().equals(EditStatus.GT)) {
							pd.setTsId(t.getTsId());
							break;
						}
						if (useInitial && t.getStatus().equals(EditStatus.NEW)){
							pd.setTsId(t.getTsId());
							break;
						}
					}
				}
				dsd.addPage(pd);
			}
			list.add(dsd);
		}

		return list;
	}
	
	/** 
	 * Method for building a descriptor list on the basis of document map as it is given in the HtrTrainingDialog of TranskribusSwtGui.
	 * @param map the document map including the selected pages
	 * @param status if not null, then the most recent version with this status is chosen
	 * @return
	 */
	public static List<DocumentSelectionDescriptor> buildCompleteSelectionDescriptorList(Map<TrpDocMetadata, List<TrpPage>> map, EditStatus status) {
		List<DocumentSelectionDescriptor> list = new LinkedList<>();
		
		for (Entry<TrpDocMetadata, List<TrpPage>> e : map.entrySet()) {
			DocumentSelectionDescriptor dsd = new DocumentSelectionDescriptor();
			dsd.setDocId(e.getKey().getDocId());
			for (TrpPage p : e.getValue()) {
				PageDescriptor pd = new PageDescriptor();
				pd.setPageId(p.getPageId());
				TrpTranscriptMetadata tmd = p.getTranscriptWithStatus(status);
				if(tmd != null) {
					pd.setTsId(tmd.getTsId());
				}
				dsd.addPage(pd);
			}
			list.add(dsd);
		}

		return list;
	}
	
	/** 
	 * Method for building a descriptor list on the basis of document map as it is given in the HtrTrainingDialog of TranskribusSwtGui.
	 * <br><br>
	 * If no status is specified all pages of the document are selected, then the descriptor for the specific document 
	 * will not specify any pages, reducing the payload when submitting it to the server. 
	 * 
	 * @param map the document map including the selected pages
	 * @param status if not null, then the most recent version with this status is chosen
	 * @return
	 */
	public static List<DocumentSelectionDescriptor> buildSelectionDescriptorList(Map<TrpDocMetadata, List<TrpPage>> map, EditStatus status) {
		List<DocumentSelectionDescriptor> list = new LinkedList<>();

		for (Entry<TrpDocMetadata, List<TrpPage>> e : map.entrySet()) {
			DocumentSelectionDescriptor dsd = new DocumentSelectionDescriptor();
			TrpDocMetadata md = e.getKey();
			dsd.setDocId(md.getDocId());
			List<TrpPage> selectedPages = e.getValue();
			if(status != null || selectedPages.size() != md.getNrOfPages()) {
				//only specify pages in detail if selection does not contain the whole doc. Payload of subsequent POST may get too large
				List<PageDescriptor> pdList = buildPageSelectionDescriptor(selectedPages, status);
				dsd.setPages(pdList);
			}
			list.add(dsd);
		}

		return list;
	}
	
	/** 
	 * Method for building a descriptor list on the basis of ground truth data map as it is given in the HtrTrainingDialog of TranskribusSwtGui.
	 * 
	 * @param map the document map including the selected pages
	 * @param status if not null, then the most recent version with this status is chosen
	 * @return
	 */
	public static List<GroundTruthSelectionDescriptor> buildGtSelectionDescriptorList(Map<GroundTruthDataSetDescriptor, List<TrpGroundTruthPage>> map) {
		List<GroundTruthSelectionDescriptor> list = new LinkedList<>();

		for (Entry<GroundTruthDataSetDescriptor, List<TrpGroundTruthPage>> e : map.entrySet()) {
			GroundTruthSelectionDescriptor dsd = new GroundTruthSelectionDescriptor();
			GroundTruthDataSetDescriptor md = e.getKey();
			dsd.setId(md.getId());
			dsd.setDataSetType(md.getDataSetType().toString());
			List<TrpGroundTruthPage> selectedPages = e.getValue();
			if(md.getSize() > 0 && selectedPages.size() < md.getSize()) {
				//only specify pages in detail if selection does not contain the whole set or size is unknown. 
				//Payload of subsequent POST may get too large if setting large number of pages here.
				List<Integer> pdList = selectedPages.stream().map(g -> g.getGtId()).collect(Collectors.toList());
				dsd.setPages(pdList);
			}
			list.add(dsd);
		}

		return list;
	}

	public static List<PageDescriptor> buildPageSelectionDescriptor(List<TrpPage> selectedPages, EditStatus status) {
		List<PageDescriptor> pdList = new ArrayList<>(selectedPages.size());
		for (TrpPage p : selectedPages) {
			PageDescriptor pd = new PageDescriptor();
			pd.setPageId(p.getPageId());
			TrpTranscriptMetadata tmd = p.getTranscriptWithStatus(status);
			if(tmd != null) {
				pd.setTsId(tmd.getTsId());
			}
			pdList.add(pd);
		}
		return pdList;
	}
	
	public static List<TrpPage> getPagesFromDocumentSelectionDescriptor(DocumentSelectionDescriptor dsd, TrpDoc doc) {
		if (dsd.getPages() == null || dsd.getPages().isEmpty())
			return null;
		
		if (CoreUtils.isEmpty(dsd.getPages())) { // return all pages from this document if list of pages is empty
			return doc.getPages();
		}
		else { // return pages from doc that are contained in this DocumentSelectionDescriptor
			return doc.getPages().stream().filter(p -> {
				for (PageDescriptor pd : dsd.getPages()) {
					if (pd.getPageId() == p.getPageId()) {
						return true;
					}
				}
				return false;
			}).collect(Collectors.toList());
		}
	}
	
	/**
	 * Type for capturing properties of a ground truth data set, 
	 * needed for building a descriptor with {@link DescriptorUtils#buildGtSelectionDescriptorList(Map)}.
	 *
	 */
	public static class GroundTruthDataSetDescriptor {
		private final int id;
		private final DataSetType dataSetType;
		protected int size;
		public GroundTruthDataSetDescriptor(final int id, final DataSetType dataSetType, final int size) {
			this.id = id;
			this.dataSetType = dataSetType;
			this.size = size;
		}
		public GroundTruthDataSetDescriptor(final int id, final DataSetType dataSetType) {
			this(id, dataSetType, -1);
		}
		public int getId() {
			return id;
		}
		public DataSetType getDataSetType() {
			return dataSetType;
		}
		public int getSize() {
			return size;
		}
	}
}
