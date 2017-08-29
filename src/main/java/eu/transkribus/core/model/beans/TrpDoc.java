package eu.transkribus.core.model.beans;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.DocumentSelectionDescriptor.PageDescriptor;
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
	
	public int getPageIndex(TrpPage p) {
		return pages.indexOf(p);
	}
	
	public boolean isLocalDoc() {
		return md.getLocalFolder()!=null;
	}
	
	public boolean isRemoteDoc() {
		return !isLocalDoc();
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

}
