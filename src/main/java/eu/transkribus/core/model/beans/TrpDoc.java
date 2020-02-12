package eu.transkribus.core.model.beans;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.RemoteDocConst;
import eu.transkribus.core.model.beans.DocumentSelectionDescriptor.PageDescriptor;
import eu.transkribus.core.model.beans.enums.EditStatus;
import eu.transkribus.core.util.CoreUtils;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpDoc implements Serializable, Comparable<TrpDoc> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(TrpDoc.class);
	
//	@XmlElement
	private TrpDocMetadata md;
	
	//TODO SortedList from DB. comparator?
	@XmlElementWrapper(name="pageList")
	@XmlElement
	private List<TrpPage> pages = new ArrayList<TrpPage>();
	
	@XmlElement
	private TrpCollection collection = null;
	
	//FIXME add element wrapper
//	@XmlElementWrapper(name="editDeclList")
	@XmlElement
	private List<EdFeature> edDeclList = new ArrayList<>();
	
	public TrpDoc() {
		md = new TrpDocMetadata();
	}
	
	/**
	 * Copy constructor
	 * @param doc
	 */
	public TrpDoc(TrpDoc doc) {
		this();
		md = new TrpDocMetadata(doc.getMd());

		collection =  doc.getCollection() == null ? null : new TrpCollection(doc.getCollection());
		
		for(TrpPage p : doc.getPages()) {
			pages.add(new TrpPage(p));
		}
		
		for(EdFeature f : doc.getEdDeclList()) {
			edDeclList.add(new EdFeature(f));
		}
	}
	
	public int getId() { return md.getDocId(); }
	
	public void addPage(TrpPage p) {
		if(p == null)
			throw new IllegalArgumentException("Page to be added is null!");
		
		pages.add(p);
	}
	
	public void clearPages() {
		pages.clear();
	}
	
	public List<TrpPage> getPages() {
		return pages;
	}
	
	/**
	 * Get count of pages
	 * @return count of pages in document
	 */
	public int getNPages() {
		return pages.size();
	}

	public void setPages(List<TrpPage> pages) {
		if(pages == null){
			throw new IllegalArgumentException("pageList is null!");
		}
		
		this.pages = pages;
	}
	
	/**
	 * This method will check occurrence of the object in the list using the {@link TrpPage#equals(Object)} method and won't work if the number of transcripts differs!
	 * <br>
	 * Equivalent to {@link TrpDoc#getPages()} and {@link List#indexOf(TrpPage)}.
	 * 
	 * @param p a page object
	 * @return the index of the equal object in the list
	 */
	public int getPageIndexByEquality(TrpPage p) {
		if(p == null) {
			logger.warn("getPageIndex(): given page is null!");
			return -1;
		}
		logger.debug("Finding page in pageList:\n" + p.toString());
		final int index = pages.indexOf(p);
		logger.debug("Correct page would be:\n" + getPageWithId(p.getPageId()).toString());
		logger.debug("Index in pagelist of doc using equals(): " + index);
		return index;
	}
	
	/**
	 * Returns the index of a page in this doc's pagelist or -1 if it is not found.<br>
	 * Occurence is checked by equality of page nr. and image URL for local docs (filestore key == null).<br>
	 * Only the pageId is compared on remote docs.
	 * 
	 * @param p
	 * @return
	 */
	public int getPageIndex(TrpPage p) {
		if(p == null) {
			logger.warn("getPageIndex(): given page is null!");
			return -1;
		}
		if(StringUtils.isEmpty(p.getKey())) {
			//local doc: search by equivalence of pageNr and image URL
			for(int i = 0; i < pages.size(); i++) {
				if(p.getPageNr() == pages.get(i).getPageNr() && p.getUrl().equals(pages.get(i).getUrl())) {
					return i;
				}
			}
		} else {
			//remote doc: search by equivalence of pageId
			for(int i = 0; i < pages.size(); i++) {
				if(p.getPageId() == pages.get(i).getPageId()) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public boolean isLocalDoc() {
		return md.getLocalFolder()!=null;
	}
	
	public boolean isRemoteDoc() {
		return !isLocalDoc();
	}
	
	public boolean isGtDoc() {
		return md==null || md.getStatus()==null ? false : getMd().getStatus() == RemoteDocConst.STATUS_GROUND_TRUTH_DOC;
	}

	public TrpDocMetadata getMd() {
		return md;
	}

	public void setMd(TrpDocMetadata md) {
		this.md = md;
	}

	public List<EdFeature> getEdDeclList() {
		return edDeclList;
	}

	public void setEdDeclList(List<EdFeature> edDeclList) {
		this.edDeclList = edDeclList;
	}

	public List<URL> getThumbUrls() /*throws Exception*/ {
		List<URL> thumbUrls = new LinkedList<>();
		
		for (TrpPage p : getPages()) {
			thumbUrls.add(p.getThumbUrl());
		}
		return thumbUrls;
	}
	
	public List<TrpTranscriptMetadata> getTranscripts() /*throws Exception*/ {
		List<TrpTranscriptMetadata> transcriptMd = new LinkedList<>();
		
		for (TrpPage p : getPages()) {
			transcriptMd.add(p.getCurrentTranscript());
		}
		return transcriptMd;
	}
	
	public List<TrpImage> getImages() {
		List<TrpImage> images = new LinkedList();
		
		for (TrpPage p : getPages()) {
			images.add(p.getImage());
		}
		return images;
	}
	
	public List<String> getPageImgNames() {
		List<String> names = new ArrayList<>(getPages().size());
		for (TrpPage p : getPages()) {
			names.add(p.getImgFileName());
		}
		return names;
	}

	public TrpCollection getCollection() {
		return collection;
	}

	public void setCollection(TrpCollection collection) {
		this.collection = collection;
	}
	
	public DocumentSelectionDescriptor getDocSelectionDescriptorForPagesString(String pagesStr) throws IOException {
		DocumentSelectionDescriptor dd = new DocumentSelectionDescriptor(md.getDocId());
		for (int pageIndex : CoreUtils.parseRangeListStr(pagesStr, getNPages())) {
			try {
				TrpPage p = getPages().get(pageIndex);
				dd.addPage(new PageDescriptor(p.getPageId()));
			} catch (IndexOutOfBoundsException e) {
				throw new IOException(e);
			}
		}
		
		return dd;
	}

	@Override
	public String toString(){
		// FIXME do null checks
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName() + "\n");
		if(this.getMd() != null) {
			sb.append(this.getMd().toString() + "\n");
		} else {
			sb.append("null\n");
		}
		for(TrpPage p : this.getPages()){
			sb.append("\t" + p.toString() + "\n");
		}
		sb.append("\t collection: "+collection);
		
		if (edDeclList != null) {
			for (EdFeature f : this.getEdDeclList()) {
				sb.append("\t ed-feature: "+f);
			}
		}
		
		return sb.toString();
				
	}

	/**
	 * Uses the docid in the TrpDocMetadata Object for comparison
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TrpDoc doc) {
		return this.getMd().compareTo(doc.getMd());
	}
	
	/**
	 * This method is just for testing equivalence of documents selected via different DocManager methods
	 * @param doc
	 * @return
	 */
	public boolean testEquals(TrpDoc doc) {
		//TrpDocMetadata has overwritten equals method
		if(!this.md.equals(doc.getMd())) {
			return false;
		}
		if(this.collection == null && doc.collection != null) {
			return false;
		}
		if(this.collection != null && doc.collection == null) {
			return false;
		}
		if(this.collection != null && doc.collection != null && !this.collection.equals(doc.getCollection())) {
			return false;
		}
		if(this.pages.size() != doc.pages.size()) {
			return false;
		}
		logger.info("Doc fields are equal. Now checking pageList...");
		for(int i = 0; i < pages.size(); i++) {
			if(!pages.get(i).testEquals(doc.pages.get(i))) {
				logger.info("Page nr. " + pages.get(i).getPageNr() + " is not equal in both docs!");
				return false;
			}
		}
		
		//TODO compare Editorial Declaration (edDeclList)
		return true;
	}

	public boolean hasChecksumsSet() {
		for(TrpPage p : this.getPages()) {
			if(p.getMd5Sum() == null) {
				return false;
			}
			for(TrpTranscriptMetadata t : p.getTranscripts()) {
				if(t.getMd5Sum() == null) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Iterates all pages and checks for known image file errors.
	 * The returned String contains one line per faulty page with the respective error.
	 * @return
	 */
	public String getImageErrors() {
		StringBuffer sb = new StringBuffer();
		for(TrpPage p : this.pages) {
			if(p.isImgMissing()) {
				sb.append("Page " + p.getPageNr() + ": " + p.getImgFileProblem() + "\n");
			}
		}
		return sb.toString().trim();
	}
	
	public TrpPage getPageWithId(int pageid) {
		for (TrpPage page : pages) {
			if (page.getPageId() == pageid) {
				return page;
			}
		}
		return null;
	}
	
	public void filterPagesByPageDescriptors(List<PageDescriptor> pds) {
		if (pds==null || pds.isEmpty()) {
			return;
		}
		List<Integer> pids = pds.stream().map(pd -> pd.getPageId()).collect(Collectors.toList());
		this.pages = this.pages.stream().filter(p -> pids.contains(p.getPageId())).collect(Collectors.toList());
		if (this.md!=null) {
			this.md.setNrOfPages(this.pages.size());	
		}
	}
	
	public void filterPagesByPagesStr(String pagesStr) throws IOException {
		if (StringUtils.isEmpty(pagesStr)) {
			return;
		}
		
		List<Integer> pageList = CoreUtils.parseRangeListStrToList(pagesStr, getNPages());
		List<TrpPage> newPages = pages.stream().filter(p -> pageList.contains(p.getPageNr()-1)).collect(Collectors.toList());
		logger.debug("N-newPages = "+newPages.size());
		setPages(newPages);
		if (this.md!=null) {
			this.md.setNrOfPages(this.pages.size());	
		}
	}
	
	public void filterPagesByPagesStrAndEditStatus(String pagesStr, EditStatus editStatus, boolean skipPagesWithMissingStatus) throws IOException {
		List<TrpPage> newPages = new ArrayList<>();
		
		// filter by pagesStr
		if (StringUtils.isEmpty(pagesStr)) {
			newPages = new ArrayList<>(pages);
		}
		else {
			List<Integer> pageList = CoreUtils.parseRangeListStrToList(pagesStr, getNPages());
			newPages = pages.stream().filter(p -> pageList.contains(p.getPageNr()-1)).collect(Collectors.toList());			
		}
		
		// filter transcripts of pages by editStatus, remove pages with no transcript
		Iterator<TrpPage> it = newPages.iterator();
		while (it.hasNext()) {
			TrpPage p = it.next();
			TrpTranscriptMetadata md = editStatus==null ? p.getCurrentTranscript() : p.getTranscriptWithStatusOrNull(editStatus);
			if (!skipPagesWithMissingStatus) {
				md = p.getCurrentTranscript();
			}
			if (md != null) {
				p.getTranscripts().clear();
				p.addTranscript(md);
			}
			else {
				it.remove();
			}
		}
		logger.debug("N-newPages = "+newPages.size());
		
		setPages(newPages);
		if (this.md!=null) {
			this.md.setNrOfPages(this.pages.size());	
		}
	}	

}
