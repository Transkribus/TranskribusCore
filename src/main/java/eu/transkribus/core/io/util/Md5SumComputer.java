package eu.transkribus.core.io.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import eu.transkribus.core.misc.APassthroughObservable;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.util.ChecksumUtils;

/**
 * An observable class that deals with MD5 checksums in TrpDoc objects.
 * 
 * @author philip
 *
 */
public class Md5SumComputer extends APassthroughObservable {
	
	public Md5SumComputer() {
		super();
	}
	
	public TrpDoc computeAndSetMd5Sums(TrpDoc doc) throws IOException {
		if(doc == null) {
			throw new IllegalArgumentException("doc is null.");
		}
		File localFolder = doc.getMd().getLocalFolder();
		
		if(localFolder == null){
			throw new IllegalArgumentException("Not a local Document!");
		}
		
		updateStatus("Computing checksums...");
		for (TrpPage p : doc.getPages()) {
			updateStatus("Computing checksum: " + getFileNameFromUrl(p.getUrl()));
			p.setMd5Sum(ChecksumUtils.getMd5SumHex(p.getUrl()));
			for(TrpTranscriptMetadata t : p.getTranscripts()){
				updateStatus("Computing checksum: " + getFileNameFromUrl(t.getUrl()));
				t.setMd5Sum(ChecksumUtils.getMd5SumHex(t.getUrl()));
			}
		}
		return doc;
	}
	
	private String getFileNameFromUrl(URL url) {
		if(url == null) return "";
		return FileUtils.toFile(url).getName();
	}
	
}
