package eu.transkribus.core.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.LocalDocConst;
import eu.transkribus.core.io.util.ImgFileFilter;
import eu.transkribus.core.util.NaturalOrderFileComparator;

public class BuildTranskribusDocs {
	private static final Logger logger = LoggerFactory.getLogger(BuildTranskribusDocs.class);

	/**
	 * index of file in list will be prefix. If false, then buildDocs() will fail on duplicates.
	 */
	private static final boolean RENAME_FILES = true;
	
	private final ImgFileFilter imgFilter;
	private final NaturalOrderFileComparator comp;
	public BuildTranskribusDocs() {
		imgFilter = new ImgFileFilter();
		comp = new NaturalOrderFileComparator();
	}
	
	public void buildDocs(File outDir, File... inDirs) throws IOException {
		if(inDirs == null || inDirs.length == 0) {
			return;
		}
		if(outDir == null || (!outDir.isDirectory() && !outDir.mkdirs())) {
			throw new IOException("Could not create output dir: " + outDir);
		}
		
		for(File f : inDirs) {
			final String docName = f.getName();
			File docDir = new File(outDir.getAbsolutePath() + File.separator + docName);
			if(!docDir.isDirectory() && !docDir.mkdir()) {
				throw new IOException("Could not create document directory.");
			}
			// Pair<img, PAGE XML>
			List<Pair<File, File>> files = findFiles(f);
			
			boolean hasDuplicates = checkForDuplicates(files);
			if(!RENAME_FILES && hasDuplicates) {
				throw new IllegalStateException("There are duplicate image names in the path: " + f.getAbsolutePath());
			}
			
			copy(files, docDir);
		}
	}

	private void copy(List<Pair<File, File>> files, File outDir) throws IOException {
		final File pageDir = new File(outDir.getAbsolutePath() + File.separator + LocalDocConst.PAGE_FILE_SUB_FOLDER);
		if(!pageDir.mkdir()) {
			throw new IOException("Could not create page dir at: " + pageDir);
		}
		logger.info("Copying files for doc: " + outDir.getName());
		int i = 0;
		for(Pair<File, File> p : files) {
			final String prefix = RENAME_FILES ? (i++) + "_" : ""; 
			File img = p.getLeft();
			File outImg = new File(outDir.getAbsolutePath() + File.separator + prefix + img.getName());
			File xml = p.getRight();
			Files.copy(img.toPath(), outImg.toPath());
			if(xml != null) {
				File outXml = new File(pageDir.getAbsolutePath() + File.separator + prefix + xml.getName());
				Files.copy(xml.toPath(), outXml.toPath());
			}
		}
	}

	private boolean checkForDuplicates(List<Pair<File, File>> files) {
		boolean hasDuplicates = false;
		Map<String, File> set = new HashMap<>();
		for(Pair<File,File> p : files) {
			String baseName = FilenameUtils.getBaseName(p.getLeft().getName());
			if(set.containsKey(baseName)) {
				logger.warn("Found duplicate img name: " + p.getLeft().getAbsolutePath());
				logger.warn("Duplicate of: " + set.get(baseName));
				hasDuplicates = true;
			} else {
				set.put(baseName, p.getLeft());
			}
		}
		return hasDuplicates;
	}

	private List<Pair<File, File>> findFiles(File searchPath) {
		List<Pair<File, File>> files = new LinkedList<>();
		List<File> imgList = Arrays.asList(searchPath.listFiles(imgFilter));
		Collections.sort(imgList, comp);
		File pageDir = new File(searchPath.getAbsolutePath() 
				+ File.separator + LocalDocConst.PAGE_FILE_SUB_FOLDER);
		for(File img : imgList) {
			final String baseName = FilenameUtils.getBaseName(img.getName());
			File[] xmlCandidates = {
					new File(searchPath.getAbsolutePath() + File.separator + baseName + ".xml"),
					new File(pageDir.getAbsolutePath() + File.separator + baseName + ".xml"),
					new File(searchPath.getAbsolutePath() + File.separator + baseName + ".XML"),
					new File(pageDir.getAbsolutePath() + File.separator + baseName + ".XML")
			};
			File xml = null;
			for(File x : xmlCandidates) {
				if(x.isFile()) {
					xml = x;
					break;
				}
			}
			files.add(Pair.of(img, xml));
		}
		List<File> subDirs = Arrays.asList(searchPath.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		}));
		Collections.sort(subDirs, comp);
		for(File subDir : subDirs) {
			files.addAll(findFiles(subDir));
		}
		return files;
	}
	
	public static void main(String[] args) {
		File in1 = new File("/home/philip/kws_demo/kws_collections/speed");
		File in2 = new File("/home/philip/kws_demo/kws_collections/val_b2p");
		
		File out = new File("/home/philip/kws_demo/TrpDocs");
		
		BuildTranskribusDocs b = new BuildTranskribusDocs();
		try {
			b.buildDocs(out, in1, in2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
