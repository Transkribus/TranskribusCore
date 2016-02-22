package eu.transkribus.core.model.dao;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.dea.fimgstoreclient.beans.ImgType;
import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;

import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.enums.EditStatus;
import eu.transkribus.core.model.beans.enums.ScriptType;

@Deprecated
public class FakeDocProvider {
	private static List<String[]> fileKeys = new ArrayList<>();
	private static List<URL[]> localFileUrls = new ArrayList<>();
	private static int nrOfPages;
	private static FimgStoreUriBuilder builder = new FimgStoreUriBuilder();

	//		/mnt/dea_scratch/TRP/TrpTestDoc/StAZ-Sign.2-1_001.jpg -> HCKQAWEMSBSCSQGDAYWCLEJI
	//		/mnt/dea_scratch/TRP/TrpTestDoc/StAZ-Sign.2-1_001.xml -> BYAJAWBKUGITXMGDDLSXKVWP
	//		/mnt/dea_scratch/TRP/TrpTestDoc/StAZ-Sign.2-1_002.jpg -> VQGVQUIAVTUTKZSKDPBBAVIO
	//		/mnt/dea_scratch/TRP/TrpTestDoc/StAZ-Sign.2-1_002.xml -> VJXGVXASSCMLPFJEVTOVQLYJ
	//		/mnt/dea_scratch/TRP/TrpTestDoc/StAZ-Sign.2-1_003.jpg -> RUFOJBDZHIOKFOLCVMZQYALH
	//		/mnt/dea_scratch/TRP/TrpTestDoc/StAZ-Sign.2-1_003.xml -> KOOAWPEMEEQUMNKJIHAPQTXB
	static {
		//List fileKeys contains fake doc images (first array field) and XMLs (second array field)
		//each list entry represents a page
		fileKeys.add(new String[] { "HCKQAWEMSBSCSQGDAYWCLEJI", "BYAJAWBKUGITXMGDDLSXKVWP" });
		fileKeys.add(new String[] { "VQGVQUIAVTUTKZSKDPBBAVIO", "VJXGVXASSCMLPFJEVTOVQLYJ" });
		fileKeys.add(new String[] { "RUFOJBDZHIOKFOLCVMZQYALH", "KOOAWPEMEEQUMNKJIHAPQTXB" });

		try {
			localFileUrls.add(new URL[] {
//					new URL("file:///mnt/dea_scratch/TRP/TrpTestDoc_20131209/StAZ-Sign.2-1_001.tif"),
//					new URL("file:///mnt/dea_scratch/tmp_raphael/TT/ex1/jpg/TT_1993_0036.jpg"),
					new URL("file:////mnt/dea_scratch/TRP/TrpTestDoc_20131209/StAZ-Sign.2-1_001.tif"),
					new URL("file:////mnt/dea_scratch/TRP/TrpTestDoc_20131209/test_out.xml") });
//			localFileUrls.add(new URL[] {
//					new URL("file:///mnt/dea_scratch/TRP/TrpTestDoc_20131209/StAZ-Sign.2-1_002.tif"),
//					new URL("file:///mnt/dea_scratch/TRP/TrpTestDoc_20131209/StAZ-Sign.2-1_002.xml") });
//			localFileUrls.add(new URL[] {
//					new URL("file:///mnt/dea_scratch/TRP/TrpTestDoc_20131209/StAZ-Sign.2-1_003.tif"),
//					new URL("file:///mnt/dea_scratch/TRP/TrpTestDoc_20131209/StAZ-Sign.2-1_003.xml") });
			localFileUrls.add(new URL[] {
					new URL("file:////mnt/dea_scratch/TRP/TrpTestDoc_20131209/StAZ-Sign.2-1_004.tif"),
					new URL("file:////mnt/dea_scratch/TRP/TrpTestDoc_20131209/StAZ-Sign.2-1_004.xml") });			
		} catch (MalformedURLException e) {
			//...
			e.printStackTrace();
		}

//		nrOfPages = fileKeys.size();
	}

	public static TrpDoc create(boolean useLocalFiles) {
		final int docid = 1;
		
		//initialize pageNum here so it can be set in docMd
		nrOfPages = useLocalFiles ? localFileUrls.size() : fileKeys.size();
		
		// set up a TrpDoc	
		TrpDoc doc = new TrpDoc();
		TrpDocMetadata md = createDocMd(docid);

		doc.setMd(md);
		
		// set up the pages
//		List<TrpPage> pages = new ArrayList<>(nrOfPages);
		TrpPage page;
		for (int i = 0; i < nrOfPages; i++) {
			page = createPage(docid, i, useLocalFiles);
			doc.addPage(page);
//			pages.add(page);
		}

//		doc.setPages(pages);

		return doc;
	}

	private static TrpPage createPage(int docid, int pageNum, boolean useLocalFiles) {
		TrpPage page = new TrpPage();
		page.setDocId(docid);
		page.setPageNr(pageNum);

		if (!useLocalFiles) {
			final String imgKey = fileKeys.get(pageNum)[0];
			page.setKey(imgKey);
			try {
				page.setUrl((new FimgStoreUriBuilder()).getImgUri(imgKey, ImgType.view).toURL());
			} catch (MalformedURLException e) {
				
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				
				e.printStackTrace();
			}

//			try {
//				page.setImageUrl(builder.getImgUri(imgKey, ImgType.view).toURL());
//			} catch (MalformedURLException | IllegalArgumentException e) {
//				//coffee++
//				e.printStackTrace();
//			}
		} else {
			page.setUrl(localFileUrls.get(pageNum)[0]);
		}

		TrpTranscriptMetadata ts = new TrpTranscriptMetadata();
		ts.setPageReferenceForLocalDocs(page);
		ts.setStatus(EditStatus.NEW);

		Calendar cal = Calendar.getInstance();
		cal.set(2013, 9, 30, 16, 43 + pageNum, 0);
		ts.setTimestamp(cal.getTime().getTime());

		ts.setUserName("Schorsch");
		
		if(!useLocalFiles){
			final String xmlKey = fileKeys.get(pageNum)[1];
			ts.setKey(xmlKey);
			
			try {
				ts.setUrl(builder.getFileUri(xmlKey).toURL());
			} catch (IllegalArgumentException | MalformedURLException e) {
				//coffee++
				e.printStackTrace();
			}
			
		} else {
			ts.setUrl(localFileUrls.get(pageNum)[1]);
		}
		
		page.getTranscripts().add(ts);

		return page;
	}

	public static TrpDocMetadata createDocMd(int docid) {
		TrpDocMetadata md = new TrpDocMetadata();
		md.setAuthor("The guy who made up that text");
		md.setGenre("Some genre");
		md.setDocId(docid);
		md.setTitle("Some Handwritten Text");
		md.setWriter("The guy who wrote this");
		md.setScriptType(ScriptType.NORMAL);
		Calendar cal = Calendar.getInstance();
		cal.set(1543, 1, 1, 16, 43, 0);
		md.setUploadTimestamp(cal.getTime().getTime());

		md.setNrOfPages(nrOfPages);
		return md;
	}

}
