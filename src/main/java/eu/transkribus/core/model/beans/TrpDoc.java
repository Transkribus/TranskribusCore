package eu.transkribus.core.model.beans;

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

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpDoc implements Serializable, Comparable<TrpDoc> {
	private static final long serialVersionUID = 1L;
	
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
		md = new TrpDocMetadata(md);
		
		collection =  new TrpCollection(doc.getCollection());
		
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

}
