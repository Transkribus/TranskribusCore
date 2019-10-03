package eu.transkribus.core.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.DocumentSelectionDescriptor;
import eu.transkribus.core.model.beans.DocumentSelectionDescriptor.PageDescriptor;
import eu.transkribus.core.model.beans.GroundTruthSelectionDescriptor;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpGroundTruthPage;
import eu.transkribus.core.model.beans.TrpHtr;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.enums.DataSetType;
import eu.transkribus.core.model.beans.enums.EditStatus;

public class DescriptorUtils {
	private static final Logger logger = LoggerFactory.getLogger(DescriptorUtils.class);
	
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
	 * @param map the document map including the selected pages each with a list of transcripts
	 * @param status if not null, then the most recent version with this status is chosen
	 * @return
	 */
	public static List<DocumentSelectionDescriptor> buildSelectionDescriptorList(Map<TrpDocMetadata, List<TrpPage>> map, PageTranscriptSelector selector, EditStatus status) {
		final PageTranscriptSelector internalSelector; 
		if(selector == null) {
			//use base implementation
			logger.debug("No TrainDataSelector given! Falling back to base implementation.");
			internalSelector = new PageTranscriptSelector();
		} else {
			internalSelector = selector;
		}
		List<DocumentSelectionDescriptor> list = new LinkedList<>();

		for (Entry<TrpDocMetadata, List<TrpPage>> e : map.entrySet()) {
			DocumentSelectionDescriptor dsd = new DocumentSelectionDescriptor();
			TrpDocMetadata md = e.getKey();
			dsd.setDocId(md.getDocId());
			List<TrpPage> selectedPages = e.getValue();
			
			if(status != null || selectedPages.size() != md.getNrOfPages() 
					|| !selectedPages.stream().allMatch(p -> internalSelector.isQualifiedForTraining(p, status))) {
				//build detailed descriptor
				List<PageDescriptor> pdList = buildPageSelectionDescriptor(selectedPages, internalSelector, status);
				
				if(pdList.isEmpty()) {
					logger.debug("All pages discarded due to EditStatus selection. Ignoring document {}", dsd.getDocId());
					continue;
				}
				
				dsd.setPages(pdList);
			} else {
				//if all pages of the document are selected and qualify for training => set docId only. Payload of subsequent POST to server may get too large otherwise
				logger.debug("All pages selected and qualified. Setting docId only.");
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
	public static List<GroundTruthSelectionDescriptor> buildGtSelectionDescriptorList(Map<AGtDataSet<?>, List<TrpGroundTruthPage>> map) {
		List<GroundTruthSelectionDescriptor> list = new LinkedList<>();

		for (Entry<AGtDataSet<?>, List<TrpGroundTruthPage>> e : map.entrySet()) {
			GroundTruthSelectionDescriptor dsd = new GroundTruthSelectionDescriptor();
			AGtDataSet<?> md = e.getKey();
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
	
	private static List<PageDescriptor> buildPageSelectionDescriptor(List<TrpPage> selectedPages, PageTranscriptSelector selector, EditStatus status) {
		//selector must not be null here
		if(selector == null) {
			throw new IllegalArgumentException("TrainDataSelector is null!");
		}
		List<PageDescriptor> pdList = new ArrayList<>(selectedPages.size());
		for (TrpPage p : selectedPages) {
			//delegate the selection and data check to the given TrainDataSelector implementation
			TrpTranscriptMetadata selection = selector.selectTranscript(p, status);
			logger.debug("Page {}: selected transcript {}", p.getPageNr(), selection == null ? null : selection.getTsId());
			if(selection == null) {
				//no transcript with this status
				continue;
			}
			if(!selector.isQualifiedForTraining(selection)) {
				//data is not usable
				continue;
			}
			PageDescriptor pd = new PageDescriptor();
			pd.setPageId(p.getPageId());
			//always set tsId! someone might update the page in the meantime
			pd.setTsId(selection.getTsId());
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
	 * Extract the pageIds that are contained in the descriptor
	 * 
	 * @param descriptor that defines the input for the job
	 * @return a set containing the pageIds
	 */
	public static Set<Integer> extractPageIdsFromDescriptor(DocumentSelectionDescriptor descriptor) {
		if(descriptor == null) {
			return new HashSet<>();
		}
		return descriptor.getPages().stream()
				.map(p -> p.getPageId())
				.collect(Collectors.toSet());
	}

	/**
	 * Type for capturing properties of a ground truth data set, 
	 * needed for building a descriptor with {@link DescriptorUtils#buildGtSelectionDescriptorList(Map)}.
	 * @param <T>
	 *
	 */
	public abstract static class AGtDataSet<T> implements Comparable<AGtDataSet<?>> {
		private final DataSetType dataSetType;
		protected int size;
		protected T delegate;
		public AGtDataSet(final T model, final DataSetType dataSetType, final int size) {
			if(model == null) {
				throw new IllegalArgumentException("Model argument must not be null!");
			}
			this.delegate = model;
			this.dataSetType = dataSetType;
			this.size = size;
		}
		public AGtDataSet(final T model, final DataSetType dataSetType) {
			this(model, dataSetType, -1);
		}
		public abstract int getId();
		public abstract String getName();
		public DataSetType getDataSetType() {
			return dataSetType;
		}
		public int getSize() {
			return size;
		}
		public T getModel() {
			return delegate;
		}
		
		public String getTypeLabel() {
			if(delegate instanceof TrpHtr) {
				return "HTR";
			} else {
				//the type is not known yet.
				return "Unknown model type";
			}
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((delegate == null) ? 0 : delegate.hashCode());
			result = prime * result + ((getDataSetType() == null) ? 0 : getDataSetType().hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AGtDataSet<?> other = (AGtDataSet<?>) obj;
			if (delegate == null) {
				if (other.delegate != null)
					return false;
				
			//TODO the delegate has to be checked on equivalence too!
				
			} else if (getId() != other.getId())
				return false;
			if (!getDataSetType().equals(other.getDataSetType()))
				return false;
			return true;
		}
		@Override
		public int compareTo(AGtDataSet<?> groundTruthDataSetDescriptor) {
			if (this.getId() > groundTruthDataSetDescriptor.getId()) {
				return 1;
			}
			if (this.getId() < groundTruthDataSetDescriptor.getId()) {
				return -1;
			}
			if (DataSetType.TRAIN.equals(this.getDataSetType()) 
					&& DataSetType.VALIDATION.equals(groundTruthDataSetDescriptor.getDataSetType())) {
				return 1;
			}
			if (DataSetType.VALIDATION.equals(this.getDataSetType()) 
					&& DataSetType.TRAIN.equals(groundTruthDataSetDescriptor.getDataSetType())) {
				return -1;
			}
			return 0;			
		}
	}
}
