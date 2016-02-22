package eu.transkribus.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.validation.Schema;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.saxon.dom.DOMNodeList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.primaresearch.dla.page.layout.physical.shared.RegionType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eu.transkribus.core.io.formats.XmlFormat;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.customtags.StructureTag;
import eu.transkribus.core.model.beans.mets.AreaType;
import eu.transkribus.core.model.beans.mets.DivType;
import eu.transkribus.core.model.beans.mets.FileType;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.beans.mets.ParType;
import eu.transkribus.core.model.beans.mets.StructMapType;
import eu.transkribus.core.model.beans.mets.MetsType.FileSec.FileGrp;
import eu.transkribus.core.model.beans.pagecontent.TextTypeSimpleType;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.ProgressUtils;
import eu.transkribus.core.util.XmlUtils;

public class FEPLocalDocReader {
	private final static Logger logger = LoggerFactory.getLogger(FEPLocalDocReader.class);
	
	public final static String ALTO_GRP = "ALTO_GRP";
	public final static String ALTO_CROPPED_GRP = "ALTO_CROPPED_GRP";
	public final static String CROPPED_GRP = "CROPPED_GRP";
	public final static String IMG_GRP = "IMG_GRP";
	public final static String OCR_GRP = "OCR_GRP";
	
	public final static String PHYSICAL_STRUCT_MAP_LABEL = "Physical Structure";
	public final static String LOGICAL_STRUCT_MAP_LABEL = "Logical Structure";
	public final static String TOC_MAP_LABEL = "Table of Content";
	
	public final static String REGION_TYPE_ATTRIBUTE = "type";
	public final static String CUSTOM_TAG_ATTRIBUTE = "custom";
	
	/**
	 * Maps struct types from FEP to PAGE region elements
	 */
	static HashMap<String, String> STRUCT_NAME_REGION_CLASS_MAP = new HashMap<String, String>();
	
	static {
		STRUCT_NAME_REGION_CLASS_MAP.put("table", RegionType.TableRegion.getName());
		STRUCT_NAME_REGION_CLASS_MAP.put("picture", RegionType.ImageRegion.getName());
		STRUCT_NAME_REGION_CLASS_MAP.put("separator", RegionType.SeparatorRegion.getName());
	}
	
	static File findMetsFile(File dir) throws IOException {
		if (!dir.exists())
			throw new IOException("Directory does not exist: "+dir.getAbsolutePath());
		if (!dir.isDirectory())
			throw new IOException("Path is not a directory: "+dir.getAbsolutePath());

		// try mets.xml as filename:
		logger.debug("trying to find mets file in dir: "+dir.getAbsolutePath());
		File metsFile = new File(dir.getAbsolutePath()+"/mets.xml");
		if (metsFile.exists()) {
			logger.debug("found mets file: "+metsFile.getName());
			return metsFile;
		}
		
		// try find mets file with same name as folder:
		metsFile = new File(dir.getAbsolutePath()+"/"+dir.getName()+".xml");
		if (metsFile.exists()) {
			logger.debug("found mets file: "+metsFile.getName());
			return metsFile;
		}
		
		throw new IOException("Cannot find mets file in: "+dir.getAbsolutePath());
	}
	
	static Mets unmarshalMets(File metsFile, boolean validate, Class<?>... nestedClassed) throws IOException, JAXBException, SAXException {
		Mets mets;
//		try {
			Unmarshaller u = JaxbUtils.createUnmarshaller(Mets.class);
			
			long t = System.currentTimeMillis();
			if (validate) {
				Schema schema = XmlFormat.METS.getOrCompileSchema();
				u.setSchema(schema);
			}
			Object o = u.unmarshal(metsFile);
			mets = (Mets) o;
			logger.debug("time for unmarshalling: "+(System.currentTimeMillis()-t)+", validated: "+validate);
//			mets = JaxbUtils.unmarshal(metsFile, Mets.class, nestedClassed);
//			mets = JaxbUtils.unmarshal2(new FileInputStream(metsFile), Mets.class, true, false);
//		} catch (Exception e) {
//			throw new IOException("Could not unmarshal METS file!", e);
//		}
		logger.debug("unmarshalled mets file");
		
		return mets;
	}
	
	static Map<String, FileGrp> getFileGrpMap(Mets mets) {
		Map<String, FileGrp> fileGroups = new HashMap<String, FileGrp>();
		for (FileGrp grp : mets.getFileSec().getFileGrp()) {
			fileGroups.put(grp.getID(), grp);
		}
		return fileGroups;
	}
	
	static Map<String, StructMapType> getStructMapMap(Mets mets) {
		Map<String, StructMapType> structMaps = new HashMap<String, StructMapType>();
		for (StructMapType sm : mets.getStructMap()) {
			structMaps.put(sm.getLABEL(), sm);
		}
		return structMaps;
	}
	
	static File getFileFromFileType(File inputDir, FileType fileType) throws IOException {
		if (fileType.getFLocat().size() != 1)
			throw new IOException("Error finding file: nr of Flocat elements != 1: "+fileType.getID());
		
		String ref = fileType.getFLocat().get(0).getHref();
		if (ref == null)
			throw new IOException("Error finding file: ref is null: "+fileType.getID());
		
		logger.trace("ref = "+ref);
		if (ref.startsWith("file://")) {
			ref = ref.replaceFirst("file://", "");
		}
		
		File file = new File(inputDir.getAbsolutePath()+"/"+ref);
		if (!file.exists()) {
//			logger.debug(file.getAbsolutePath());
			throw new IOException("Error finding file: file does not exist: "+fileType.getID()+" ref: "+ref+" path: "+file.getAbsolutePath());
		}
		
		return file;		
	}
	
	static Pair<FileGrp, File> findFile(File inputDir, Mets mets, String id) throws IOException {		
		for (FileGrp grp : mets.getFileSec().getFileGrp()) {
			for (FileType f : grp.getFile()) {
				if (f.getID().equals(id)) {			
					return Pair.of(grp, getFileFromFileType(inputDir, f));
				}
			}
		}
		
		throw new FileNotFoundException("Error finding file: file not found: "+id);
	}
	
	static StructMapType findStructMap(Mets mets, String label) throws IOException {
		for (StructMapType sm : mets.getStructMap()) {
			if (sm.getLABEL().equals(label)) {
				return sm;
			}
		}
		throw new IOException("Cannot find struct map with label: "+label);
	}
	
	static void setTitle(TrpDoc doc, Mets mets) throws IOException {
		StructMapType physSm = findStructMap(mets, PHYSICAL_STRUCT_MAP_LABEL);
		DivType rootDiv = physSm.getDiv();
		String title = rootDiv.getLABEL();
		doc.getMd().setTitle(title);
	}
	
	static List<HashMap<String, File>> parsePhysicalStructure(File inputDir, Mets mets) throws IOException {
		StructMapType physSm = findStructMap(mets, PHYSICAL_STRUCT_MAP_LABEL);		
		DivType rootDiv = physSm.getDiv();

		// sort divs by order:
		Collections.sort(rootDiv.getDiv(), new Comparator<DivType>() {
			@Override public int compare(DivType o1, DivType o2) {
				return o1.getORDER().compareTo(o2.getORDER());
			}
		});
		
		List<HashMap<String, File>> fepFileGrps = new ArrayList<>();
		// parse them bloody divs:
		for (DivType div : rootDiv.getDiv()) {
			if (div.getFptr().size() != 1)
				throw new IOException("Error parsing physical structure: nr of fptr elements is not 1 in div: "+div.getFptr().size()+", id: "+div.getID());
			
			ParType par = div.getFptr().get(0).getPar();
			if (par == null)
				throw new IOException("Error parsing physical structure: could not parse par element in fptr of div: "+div.getID());
			
			HashMap<String, File> files = new HashMap<>();
			for (Serializable o : par.getAreaOrSeq()) {
				if (o instanceof AreaType) {
					AreaType area = (AreaType) o;
					FileType fileType = (FileType) area.getFILEID();					
					Pair<FileGrp, File> filePair = findFile(inputDir, mets, fileType.getID());
					logger.debug("found file with id: "+fileType.getID()+", path: "+filePair.getRight().getAbsolutePath());
					files.put(filePair.getLeft().getID(), filePair.getRight());
				}
			}
			
			fepFileGrps.add(files);
		}
		
		return fepFileGrps;
	}
	
	static HashMap<String, String> getLogicalStructuresForPage(Mets mets, int pageNr) throws IOException {
		HashMap<String, String> structs = new HashMap<>();
		
		StructMapType physSm = findStructMap(mets, LOGICAL_STRUCT_MAP_LABEL);	
		DivType rootDiv = physSm.getDiv();
		
		String regex = "^STRUCTURE_PAGE_(\\d+)_(BLOCK_\\d+)$";
		Pattern p = Pattern.compile(regex);
		
		for (DivType d : rootDiv.getDiv()) {
			String id = d.getID();
			logger.trace("found div with id = "+id);
			Matcher m = p.matcher(id);
			if (m.matches()) {
				logger.trace("id matches regex: "+id);
				logger.trace("g1 = "+m.group(1));
				logger.trace("g2 = "+m.group(2));
				logger.trace("TYPE = "+d.getTYPE());
				
				if (Integer.parseInt(m.group(1)) == pageNr)
					structs.put(m.group(2), d.getTYPE());
			} else {
				logger.warn("div with ID = "+id+" cannot be parsed as a logical structure!");
			}
		}
		
		return structs;
	}
	
	static void applyLogicalStructFromMetsToPageFile(Mets mets, int pageNr, File pageFile) throws TransformerException, IOException, SAXException, ParserConfigurationException, XPathExpressionException {
		HashMap<String, String> structs4Page = getLogicalStructuresForPage(mets, pageNr);
		long t = System.currentTimeMillis();
		Document pageDom = XmlUtils.parseDomFromFile(pageFile, true);
		logger.debug("time to read dom: "+(System.currentTimeMillis()-t));
		
		for (String blockId : structs4Page.keySet()) {
			String type = structs4Page.get(blockId);
			String typeLc = type.toLowerCase();
			logger.debug("block: "+blockId+" type: "+type);
			
//			final XPathExpression expr = xpath.compile("//TextRegion[@id='"+block+"']");
			final XPathExpression expr = XPathFactory.newInstance().newXPath().compile("//*[@id='"+blockId+"']");
			DOMNodeList result = (DOMNodeList) expr.evaluate(pageDom, XPathConstants.NODESET);
			if (result.getLength() != 1) {
				throw new IOException("Could not find region with ID '"+blockId+"' in page file "+pageNr);
			}
			Element regionElement  = (Element) result.item(0);
			
			// convert a logical struct region type to corresponding PAGE region element if not same:
			if (STRUCT_NAME_REGION_CLASS_MAP.containsKey(typeLc)) {
				String newElementName = STRUCT_NAME_REGION_CLASS_MAP.get(typeLc);
				if (!newElementName.equals(regionElement.getNodeName())) {
					logger.debug("Converting a region of type "+regionElement.getNodeName()+" to type "+newElementName);
					
					Element newElement = pageDom.createElementNS(XmlFormat.PAGE_2013.namespace, newElementName);
					NodeList coordsList = regionElement.getElementsByTagName("Coords");
					Node coordsCopy = null;
					for (int i=0; i<coordsList.getLength(); ++i) {
						if (coordsList.item(i).getParentNode().equals(regionElement)) {
							coordsCopy = coordsList.item(i).cloneNode(false);
							break;
						}
					}
					
					if (coordsCopy == null)
						throw new IOException("Could not find coordinates for region: "+blockId+", pageNr: "+pageNr);
					
					newElement.appendChild(coordsCopy);
					newElement.setAttribute("id", blockId);
					
					regionElement.getParentNode().insertBefore(newElement, regionElement);
					regionElement.getParentNode().removeChild(regionElement);
					regionElement = newElement;
				}
			}
			
			// if this is a text region -> set logical struct from mets as type attribute and custom tag
			if (regionElement.getTagName().equals(RegionType.TextRegion.getName())) {
				logger.debug("Text region");
				
				TextTypeSimpleType ts = StructureTag.parseTextType(typeLc);
				if (ts != null) {
					logger.debug("successfully parsed text type: "+typeLc);
					regionElement.setAttribute(REGION_TYPE_ATTRIBUTE, ts.value());
					regionElement.setAttribute(CUSTOM_TAG_ATTRIBUTE, "structure {type:"+ts.value()+"};");
				} else {
					regionElement.setAttribute(CUSTOM_TAG_ATTRIBUTE, "structure {type:"+typeLc+"};");
				}
			}
		}
		XmlUtils.writeDomToFile(pageFile, pageDom);
		logger.debug("time to apply logical structs: "+(System.currentTimeMillis()-t));
	}

	public static TrpDoc loadFEPDoc(final String path, 
			boolean validateMets, boolean preserveOcrTxtStyles, boolean preserveOcrFontFamily, boolean replaceBadChars,
			IProgressMonitor monitor) throws Exception {
		final File inputDir = new File(path);
		logger.info("importing FEP document from path: "+path);
		
		ProgressUtils.beginTask(monitor, "Importing a FEP document", -1);
		ProgressUtils.subTask(monitor, "Parsing mets");
		
		// find mets file:
		File metsFile = findMetsFile(inputDir);
		
		// unmarshall mets:
		Mets mets = unmarshalMets(metsFile, validateMets);
				
		// create trp-document and set metadata:
		TrpDoc trpDoc = new TrpDoc();
		setTitle(trpDoc, mets);
		trpDoc.getMd().setDesc("Imported from FEP export");
		trpDoc.getMd().setLocalFolder(inputDir);
		
		File pageDir = new File(inputDir.getAbsolutePath()+"/"+LocalDocConst.PAGE_FILE_SUB_FOLDER);
		File thumbDir = new File(inputDir.getAbsolutePath() + "/" + LocalDocConst.THUMBS_FILE_SUB_FOLDER);
		
		// parse physical structure:
		List<HashMap<String, File>> physStruct = parsePhysicalStructure(inputDir, mets);
		final int nPages = physStruct.size();
		
		ProgressUtils.beginTask(monitor, "Importing a FEP document", nPages);
		
		// create PAGEs:
		List<TrpPage> pages = new ArrayList<TrpPage>(nPages);
		
		int pageNr = 0;
		for (HashMap<String, File> files : physStruct) {
			ProgressUtils.subTask(monitor, "Importing page "+pageNr);
			++pageNr;
			logger.debug("page: "+pageNr+", nr of files: "+files.size());
			
			// first, check if image file is there and set some variables:
			if (!files.containsKey(IMG_GRP))
				throw new IOException("Image file for page "+pageNr+" could not be found!");			
			File imgFile = files.get(IMG_GRP);; 
			String imgFileBn = FilenameUtils.getBaseName(imgFile.getName());
			File thumbFile = LocalDocReader.getThumbFile(thumbDir, imgFileBn);
			File pageOutFile = new File(pageDir.getAbsolutePath() + "/" + imgFileBn + ".xml");
						
			FileUtils.forceMkdir(pageOutFile.getParentFile());
			
			if (files.containsKey(ALTO_GRP)) {
				File altoFile = files.get(ALTO_GRP);
				pageOutFile = LocalDocReader.createPageFromAlto2(imgFile, altoFile, pageOutFile, 
						preserveOcrTxtStyles, preserveOcrFontFamily, replaceBadChars);
			} else {
				throw new IOException("ALTO file for image "+pageNr+" could not be found!");
				// TODO: create empty page file -> NO!
			}
						
			TrpPage page = LocalDocReader.buildPage(inputDir, pageNr, imgFile, pageOutFile, thumbFile);
			
			// exract logical structs for this page from mets and apply them to the page:
			applyLogicalStructFromMetsToPageFile(mets, pageNr, pageOutFile);
			
			pages.add(page);
			ProgressUtils.worked(monitor, pageNr);
		}

		trpDoc.setPages(pages);
		return trpDoc;
	}

}
