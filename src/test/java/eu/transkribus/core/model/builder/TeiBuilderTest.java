package eu.transkribus.core.model.builder;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.builder.tei.TeiExportPars;
import eu.transkribus.core.model.builder.tei.TrpTeiStringBuilder;
import eu.transkribus.core.util.SebisStopWatch;

public class TeiBuilderTest {
	private static final Logger logger = LoggerFactory.getLogger(TeiBuilderTest.class);
	
//	final static String docPath = "/mnt/dea_scratch/TRP/test/bsb00089816_textRegion_from_par";
	final static String docPath = "/mnt/dea_scratch/TRP/TrpTestDoc_20140508"; // has words!
//	final static String docPath = "/home/sebastianc/Dokumente/Bentham_box_035";
	
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
	
	public static void main(String[] args){
		SebisStopWatch sw = new SebisStopWatch();

		TrpDoc doc;
		try {
//			doc = LocalDocReader.load(LocalDocReaderTest.TEST_DOC1);
			doc = LocalDocReader.load(docPath);
			
//			test1(doc);
			test2(doc);
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
