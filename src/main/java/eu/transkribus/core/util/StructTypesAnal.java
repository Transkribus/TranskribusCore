package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.customtags.CssSyntaxTag;

/**
 * Class for analyzing structure types of documents in either a local path, a collection-id or document-id-list.<br/>
 * Can also be used to split files into train/val/test sets and copy the corresponding image and PAGE-XML files into dedicated folders.
 */
public class StructTypesAnal {
	private static final Logger logger = LoggerFactory.getLogger(StructTypesAnal.class);

	private List<ImgAndPageXml> imgXmlPairs = new ArrayList<>();
	private Map<String, Integer> counts = new HashMap<>(); // a counter for all unique struct types
	private Map<ImgAndPageXml, Map<String, String>> occs = new HashMap<>();
	
//	private String path = null;
//	private Integer collId = null;
//	private List<Integer> docIdExceptions;
	
//	private List<Integer> docIds = new ArrayList<>();
	
//	private CollectionManager cMan;
//	private DocManager docMan;
	
	public static final class ImgAndPageXml {
		private TrpPage page=null;
		private Pair<File, File> imgAndXml=null;
		
		public ImgAndPageXml(Pair<File, File> imgAndXml) {
			this.imgAndXml = imgAndXml;
		}		
		
		public ImgAndPageXml(TrpPage page) {
			this.page = page;
		}
		
		public boolean isRemote() {
			return page!=null;
		}
		
		@Override public int hashCode() {
			return page != null ? page.hashCode() :imgAndXml.hashCode(); 
		}
		
		@Override public boolean equals(Object obj) {
			ImgAndPageXml o = (ImgAndPageXml) obj;
			return page != null ? page.equals(o.page) : imgAndXml.equals(o.imgAndXml); 
		}
		
		public String getBaseName() {
			return FilenameUtils.getBaseName(getImageFileName());
		}
		
		public String getImgUrlAndFilename() {
			return getImageFileName()+" - "+getImgUrl();
		}
		
		public String getPageXmlUrlAndFilename() {
			return getPageXmlFileName()+" - "+getPageXmlUrl();
		}
		
		public URL getImgUrl() {
			try {
//				return page!=null ? page.getUrl() : imgAndXml.getLeft().toURI().toURL();
				return page!=null ? getOrigImgUrlFromPage() : imgAndXml.getLeft().toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		
		private URL getOrigImgUrlFromPage() throws MalformedURLException {
			if (page==null) {
				return null;
			}
			String urlStr = page.getUrl().toString();
			return new URL(CoreUtils.removeFileTypeFromUrl(urlStr));
		}
		
		public URL getPageXmlUrl() {
			try {
				return page!=null ? page.getCurrentTranscript().getUrl() : imgAndXml.getRight().toURI().toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}			
		}
		
		public String getImageFileName() {
			return page!=null ? page.getImgFileName() : imgAndXml.getLeft().getName();
		}
		
		public String getPageXmlFileName() {
			return page!=null ? page.getCurrentTranscript().getXmlFileName() : imgAndXml.getRight().getName();
		}		
	}
	
	public StructTypesAnal() {
	}
	
	public StructTypesAnal(List<ImgAndPageXml> imgXmlPairs) {
		this.imgXmlPairs = imgXmlPairs;
		logger.info("StructTypesAnal, got "+imgXmlPairs.size()+" files");
	}	
	
	public void setImgXmlPairs(List<ImgAndPageXml> imgXmlPairs) {
		this.imgXmlPairs = imgXmlPairs;
	}
	
	public void setPages(List<TrpPage> pages) {
		setImgXmlPairs(pages.stream().map(p -> new ImgAndPageXml(p)).collect(Collectors.toList()));
	}
	
	public static StructTypesAnal fromPages(List<TrpPage> pages) {
		return new StructTypesAnal(pages.stream().map(p -> new ImgAndPageXml(p)).collect(Collectors.toList()));
	}
	
//	private void initDbObjects() {
//		if (cMan == null) {
//			cMan = new CollectionManager();
//		}
//		if (docMan == null) {
//			docMan = new DocManager();
//		}
//	}
	
//	private void parseDocIds() throws ReflectiveOperationException, SQLException {
//		if (this.collId != null) {
//			this.docIds = docMan.getDocList(collId).stream().filter(md -> {
//					return docIdExceptions==null || !docIdExceptions.contains(md.getDocId());
//				}).map(md -> md.getDocId()).collect(Collectors.toList());
//		}
//		
//		logger.debug("docIds, N = "+docIds.size()+": "+CoreUtils.join(docIds));
//	}
	
//	private void parseImgPageXmlPairs() throws IOException, EntityNotFoundException, ReflectiveOperationException, SQLException {
//		if (path != null) {
//			try {
//				imgXmlPairs = PageXmlUtils.listAllImgPageXmlPairsInFolderRecursively(path).stream().map(
//							p -> { 
//								return new ImgAndPageXml(p);
//							}
//						).collect(Collectors.toList());
//			} catch (IOException e) {
//				throw new RuntimeException(e);
//			}
//		}
//		else { // TODO: parse from collId, or docId-list
//			parseDocIds();
//			
//			imgXmlPairs = new ArrayList<>();
//			for (Integer docId : docIds) {
//				TrpDoc doc = docMan.getDocById(docId, 1);
//				for (TrpPage p : doc.getPages()) {
//					imgXmlPairs.add(new ImgAndPageXml(p));
////					imgXmlPairs.add(Pair.of(p.getUrl(), p.getCurrentTranscript().getUrl()));
//				}
//			}
//		}
//		logger.info("Parsed "+imgXmlPairs.size()+" files");
//	}
	
	public void analyzeStructureTypes() throws Exception {
		analyzeStructureTypes(null);
	}

	public void analyzeStructureTypes(IProgressMonitor monitor) throws Exception {
//		parseImgPageXmlPairs();
//		if (true) return;
		
		logger.info("analyzing structure types for "+imgXmlPairs.size()+" pages");
		MonitorUtil.beginTask(monitor, "Analyzing structure types for "+imgXmlPairs.size()+" pages", imgXmlPairs.size());

		int i = 0;
		for (ImgAndPageXml p : imgXmlPairs) {
			if (MonitorUtil.isCanceled(monitor)) {
				break;
			}
			
			MonitorUtil.subTask(monitor, (i+1)+"/"+imgXmlPairs.size());
			
			logger.info("analyzeStructureTypes, Parsing page " + (i + 1) + "/" + imgXmlPairs.size());
			URL pageXml = p.getPageXmlUrl();

			PageXmlFileProcessor fp = new PageXmlFileProcessor(pageXml.toString());
			Document d = fp.getDocument();

			NodeList trs = fp.getTextRegions(d);
			for (int j = 0; j < trs.getLength(); ++j) {
				Node tr = trs.item(j);
				String custom = tr.getAttributes().getNamedItem("custom").getNodeValue();
				CssSyntaxTag structTag = CssSyntaxTag.parseTags(custom).stream()
						.filter(t -> t.getTagName().equals("structure")).findAny().orElse(null);
				if (structTag != null) {
					String structType = (String) structTag.getAttributeValue("type");
					String rid = tr.getAttributes().getNamedItem("id").getNodeValue();
					logger.trace("Found struct type = " + structType + ", rid = " + rid);

//						sts.put(rid, structType);
//						Integer count = anal.counts.get(structType);
//						count = count==null ? 1 : count+1;
//						anal.counts.put(structType, count);

					addStructType(p, rid, structType);
				}
			}

//				anal.occs.put(pageXml.getAbsolutePath(), sts);

			++i;
			MonitorUtil.worked(monitor, i);
		}
	}

	private void addStructType(ImgAndPageXml imgAndXml, String rid, String structType) {
		Map<String, String> sts = occs.get(imgAndXml);
		if (sts == null) {
			sts = new HashMap<>();
		}
		sts.put(rid, structType);
		occs.put(imgAndXml, sts);

		Integer count = counts.get(structType);
		count = count == null ? 1 : count + 1;
		counts.put(structType, count);
	}

	public List<ImgAndPageXml> getFilesWithStruct(String structType) {
		return occs.keySet().stream().filter(fn -> occs.get(fn).values().contains(structType))
				.collect(Collectors.toList());
	}

	public Map<String, List<ImgAndPageXml>> getStructAndOccuringFilesMap(List<String> structs) {
		Map<String, List<ImgAndPageXml>> res = new HashMap<>();
		for (String st : counts.keySet()) {
			res.put(st, getFilesWithStruct(st));
		}
		if (structs != null) {
			res.entrySet().removeIf(e -> !structs.contains(e.getKey()));
		}

		return res;
	}

	public List<Set<ImgAndPageXml>> splitIntoSets(boolean includeAllForLastSet, boolean allowDuplicateFilesInDifferentSets,
			List<String> structs, double... fracs) {
		List<Set<ImgAndPageXml>> res = new ArrayList<>();
		for (int i = 0; i < fracs.length; ++i) {
			res.add(new HashSet<>());
		}

		Map<String, List<ImgAndPageXml>> m = getStructAndOccuringFilesMap(structs);
		for (String st : m.keySet()) {
			List<ImgAndPageXml> filesForStruct = new ArrayList<>(m.get(st));
			if (filesForStruct.isEmpty()) {
				continue;
			}

			Collections.shuffle(filesForStruct);

			logger.info("files for struct '" + st + "', N = " + filesForStruct.size());
			filesForStruct.stream().forEach(f -> logger.info("img: "+f.getImgUrlAndFilename()));

			int start = 0, end = 0;
			for (int i = 0; i < fracs.length; ++i) {
				Set<ImgAndPageXml> filesForFrac = res.get(i);

//					int N = Math.round(filesForStruct.size() * fracs[i]);
				int N = (int) (filesForStruct.size() * fracs[i]);
				if (N == 0) {
					logger.warn("warning: nr of files for struct '" + st + "' too low for frac " + i
							+ " - setting to 1 at least!");
					N = 1;
				}

				end = CoreUtils.bound(start + N, 0, filesForStruct.size());
				if (includeAllForLastSet && i == fracs.length - 1) {
					end = filesForStruct.size();
				}

				if (filesForStruct.size() == 1) { // special case: if GT occurs on only 1 page -> always take this page
													// for every set
					logger.warn("GT for this st has only 1 page -> will try to include that page for every set!");
					start = 0;
					end = 1;
				}

				int countNew = 0;
				logger.info("frac = " + i + ", st = " + st + ", N = " + N + ", N-filesForStruct = "
						+ filesForStruct.size() + ", start = " + start + ", end = " + end);
				for (int j = start; j < end; ++j) {
					ImgAndPageXml imgAndXml = filesForStruct.get(j);

					boolean addToFilesForFrac = false;
					if (allowDuplicateFilesInDifferentSets) {
						addToFilesForFrac = !filesForFrac.contains(imgAndXml);
					} else { // check if file is not contained in *any* set
						addToFilesForFrac = res.stream().filter(files -> files.contains(imgAndXml)).findFirst()
								.orElse(null) == null;
					}
					if (filesForStruct.size() == 1) { // special case: if GT occurs on only 1 page -> always add it to
														// any split
						addToFilesForFrac = true;
					}
					if (addToFilesForFrac) { // if file is not contained in *any* set
						filesForFrac.add(imgAndXml);
						++countNew;
					}
				}
				logger.info("countNew = " + countNew + ", N-filesForFrac = " + filesForFrac.size());

				start += N;
			}
		}
		
		if (true) { // debug output
			for (int i=0; i<res.size(); ++i) {
				logger.info("set "+(i+1)+"/"+res.size()+", N = "+res.get(i).size());
				for (ImgAndPageXml f : res.get(i)) {
					logger.info(""+f.getImgUrlAndFilename());
				}
			}
			logger.info("N-Split = "+res.stream().collect(Collectors.summarizingInt(s -> s.size())).getSum());
			logger.info("N-Total = "+getOccs().size());
		}

		return res;
	}
	
	public void createP2PaLAGT(List<Set<ImgAndPageXml>> fileSets, boolean usePrefix, String outPath, boolean overwrite) throws IOException {
		String[] subDirs = new String[] { "train", "val", "test" };
		
		for (int i=0; i<fileSets.size(); ++i) {
			Set<ImgAndPageXml> files = fileSets.get(i);
			String subDir = subDirs[i];
			if (files.isEmpty()) {
				continue;
			}
			
			String outDir = outPath+"/"+subDir;
			CoreUtils.createDirectoriesSilent(outDir, overwrite);
			
			int j=1;
			for (ImgAndPageXml file : files) {
				String fnPrefix="";
				if (usePrefix) {
					fnPrefix = StringUtils.leftPad(j+"", (""+files.size()).length(), "0")+"_";	
				}
				
				URL imgUrl = file.getImgUrl();
				URL xmlUrl = file.getPageXmlUrl();
				
				File imgFileTo = new File(outDir+"/"+fnPrefix+file.getImageFileName());
				logger.info(j+"/"+files.size()+" Copy '"+imgUrl.toString()+"' --> '"+imgFileTo.getAbsolutePath()+"'");
				FileUtils.copyURLToFile(imgUrl, imgFileTo); // copy img
				
				File xmlFileTo = new File(outDir+"/page/"+fnPrefix+file.getBaseName()+".xml");
				logger.info(j+"/"+files.size()+" Copy '"+xmlUrl.toString()+"' --> '"+xmlFileTo.getAbsolutePath()+"'");
				FileUtils.copyURLToFile(xmlUrl, xmlFileTo); // copy xml
				
				++j;
			}
		}
	}

	public List<ImgAndPageXml> getImgXmlPairs() {
		return imgXmlPairs;
	}

	public Map<String, Integer> getCounts() {
		return counts;
	}

	public Map<ImgAndPageXml, Map<String, String>> getOccs() {
		return occs;
	}
	
	public String getStructTypesStrForP2PaLA() {
		return counts.keySet().stream().collect(Collectors.joining(" "));
	}
	
	

}