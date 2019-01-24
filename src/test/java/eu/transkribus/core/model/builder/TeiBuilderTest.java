package eu.transkribus.core.model.builder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.DocExporter;
import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.builder.mets.util.MetsUtil;
import eu.transkribus.core.model.builder.tei.TeiExportPars;
import eu.transkribus.core.model.builder.tei.TrpTeiStringBuilder;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.SebisStopWatch;
import eu.transkribus.core.util.XslTransformer;

public class TeiBuilderTest {
	private static final Logger logger = LoggerFactory.getLogger(TeiBuilderTest.class);
	
//	final static String docPath = "/mnt/dea_scratch/TRP/test/bsb00089816_textRegion_from_par";
	//final static String docPath = "/mnt/dea_scratch/TRP/TrpTestDoc_20140508"; // has words!
	final static String docPath = "C:/Neuer Ordner/Grimmen/Grimmen/";
	final static String exportFilename = "C:/Neuer Ordner/Grimmen/Grimmen.xml";
	final static String metsPath = "C:/Neuer Ordner/Grimmen/Grimmen/mets.xml";
	private static final String PAGE_TO_TEI_XSLT = "xslt/page2tei-0.xsl";
	
	static SebisStopWatch sw = new SebisStopWatch();
	
	public static void test1(TrpDoc doc) throws Exception {
		Set<Integer> pageIndices = new HashSet<Integer>();
		pageIndices.add(0);
		Set<String> selectedTags = null; // all tags
//		Set<String> selectedTags = new HashSet<String>();
//		selectedTags.add("person");
//		selectedTags.add("place");
//		selectedTags.add("abbrev");
//		selectedTags.add("organization");
//		selectedTags.add("speech");
		
		boolean writeTextOnWordLevel = true;
		boolean doBlackening = true;
		TrpTeiStringBuilder tb = null;
		boolean boundingBoxCoords=false;
		
		TeiExportPars pars = new TeiExportPars(false, false, false, boundingBoxCoords, TeiExportPars.LINE_BREAK_TYPE_LINE_TAG);
		
		CommonExportPars commonPars = new CommonExportPars();
		commonPars.setWriteTextOnWordLevel(writeTextOnWordLevel);
		commonPars.setDoBlackening(doBlackening);
					
		sw.start();
		tb = new TrpTeiStringBuilder(doc, commonPars, pars, null);
		tb.buildTei();
		tb.writeTeiXml(new File(docPath + "/tei_simple_string_no_zones.xml"));
		sw.stop(true, "simple: ", null);
					
		sw.start();
		pars.setRegionZones(true);
		tb.buildTei();
		tb.writeTeiXml(new File(docPath + "/tei_simple_string.xml"));
		sw.stop(true, "zone per par: ", null);

		sw.start();
		pars.setLineZones(true);
		tb.buildTei();
		tb.writeTeiXml(new File(docPath + "/tei_simple_string_lines.xml"));
		sw.stop(true, "zone per line: ");
		
		sw.start();
		pars.setWordZones(true);
		tb.buildTei();
		tb.writeTeiXml(new File(docPath + "/tei_simple_string_words.xml"));
		sw.stop(true, "zone per word: ");			
		
		
//		TrpTeiDomBuilder tb = new TrpTeiDomBuilder(doc, TeiExportMode.SIMPLE, null);
//		tb.buildTei();
//		tb.writeTeiXml(new File(docPath + "/tei_simple.xml"));
//		
//		tb.setMode(TeiExportMode.ZONE_PER_LINE);
//		tb.buildTei();
//		tb.writeTeiXml(new File(docPath + "/tei_with_line_zones.xml"));
		
//		logger.debug(tb.getTeiAsString());
	}
	
	public static void test2(TrpDoc doc) throws Exception {
		Set<Integer> pageIndices = new HashSet<Integer>();
		pageIndices.add(0);
		Set<String> selectedTags = null; // all tags
		
		TrpTeiStringBuilder tb = null;
		
		CommonExportPars commonPars = new CommonExportPars();
		commonPars.setWriteTextOnWordLevel(false);
		commonPars.setDoBlackening(true);
		
		boolean boundingBoxCoords=true;
		boolean regionZones = false;
		boolean lineZones = true;
		boolean wordZones = false;
		
		TeiExportPars pars = new TeiExportPars(regionZones, lineZones, wordZones, boundingBoxCoords, TeiExportPars.LINE_BREAK_TYPE_LINE_BREAKS);

		sw.start();
		tb = new TrpTeiStringBuilder(doc, commonPars, pars, null);
		tb.buildTei();
		tb.writeTeiXml(new File(docPath + "/tei_4_schulthess_test.xml"));
		sw.stop(true, "tei_4_schulthess_test: ", null);
					
//		sw.start();
//		pars.setRegionZones(true);
//		tb.buildTei();
//		tb.writeTeiXml(new File(docPath + "/tei_simple_string.xml"));
//		sw.stop(true, "zone per par: ", null);
	}
	
	public static void page2Tei(TrpDoc doc) throws Exception {
		Mets mets;
		File metsFile = new File(metsPath);
		try {
			mets = JaxbUtils.unmarshal(metsFile, Mets.class, TrpDocMetadata.class);
			DocExporter docExp = new DocExporter();
			docExp.writeTEI(doc, exportFilename, new CommonExportPars("1-4", false, false, true, false, false, true, false, false, false, false, false, "Latest", false, false, null), null);
			//DocExporter.transformTei(mets, docPath);
			//transformTei(mets);
		} catch (JAXBException e) {
			throw new IOException("Could not unmarshal METS file!", e);
		}
		
		
		
	}
	
	/*
	 * first shot to get the TEI export as a transformation of the page XML with a predefined XSLT
	 * test and make it available via the rest API for the server export 
	 */
	public static File transformTei(Mets mets) throws JAXBException, FileNotFoundException, TransformerException {
		if(mets == null){
			throw new IllegalArgumentException("An argument is null!");
		}
				
		StreamSource mySrc = new StreamSource();
		mySrc.setInputStream(new ByteArrayInputStream(JaxbUtils.marshalToBytes(mets, TrpDocMetadata.class)));
		
		//necessary to use the relative paths in the xslt
		mySrc.setSystemId(docPath);
		
		InputStream is = XslTransformer.class.getClassLoader().getResourceAsStream(PAGE_TO_TEI_XSLT);
		
//		InputStream xslIS = new BufferedInputStream(new FileInputStream(xslID));
		InputStream xslIS = new BufferedInputStream(is);
		StreamSource xslSource = new StreamSource(xslIS);

        // das Factory-Pattern unterstützt verschiedene XSLT-Prozessoren
        TransformerFactory transFact =
                TransformerFactory.newInstance();
        Transformer trans;
//		try {
			trans = transFact.newTransformer(xslSource);
			
			File teiFile = new File(new File(docPath).getParentFile().getAbsolutePath()+"/gh_tei.xml");			
			trans.transform(mySrc, new StreamResult(new FileOutputStream(teiFile)));
			
			return teiFile;
//		} catch (TransformerConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (TransformerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	public static void main(String[] args){
		SebisStopWatch sw = new SebisStopWatch();

		TrpDoc doc;
		try {
//			doc = LocalDocReader.load(LocalDocReaderTest.TEST_DOC1);
			doc = LocalDocReader.load(docPath);
			
//			test1(doc);
			//test2(doc);
			page2Tei(doc);
			
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
