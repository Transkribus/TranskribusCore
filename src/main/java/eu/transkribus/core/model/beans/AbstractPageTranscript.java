package eu.transkribus.core.model.beans;

import java.io.IOException;


public abstract class AbstractPageTranscript<T> implements Comparable<AbstractPageTranscript<T>> {
	protected TrpTranscriptMetadata md;
	protected T pageData;
	
	public TrpTranscriptMetadata getMd() {
		return md;
	}
	
	protected void setMd(TrpTranscriptMetadata md) {
//		if (md == null) {
//			throw new IllegalArgumentException("Metadata is null!");
//		}
		this.md = md;
	}
	
	public T getPageData(){
		return this.pageData;
	}
	
	protected void setPageData(T page) {
		this.pageData = page;
	}
	
	/**
	 * Uses the timestamp in the metadata object for comparison
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AbstractPageTranscript<T> pt){
		return this.getMd().compareTo(pt.getMd());
	}
	
	public void build() throws IOException {}
}
