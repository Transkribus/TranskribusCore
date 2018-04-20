package eu.transkribus.core.model.builder;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.io.UnsupportedFormatException;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.mets.Mets;
import eu.transkribus.core.model.builder.mets.TrpMetsBuilder;
import eu.transkribus.core.util.JaxbUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrpMetsBuilderTest {
	private static final Logger logger = LoggerFactory.getLogger(TrpMetsBuilderTest.class);
//	private final static String docPath = "/mnt/dea_scratch/TRP/sample_buch_done";
	private final static String docPath = "/media/iza_retro/DIG_Auftraege/D_2014_0022_BSB_Klassikerausgaben/sample_buch_done/";
	
	
	public static void createMets(File folder, boolean printResultOnSysOut) throws UnsupportedFormatException, IOException, JAXBException {
		if (folder==null || !folder.isDirectory())
			throw new IOException("Folder null or no directory!");
		
		TrpDoc doc = LocalDocReader.load(folder.getAbsolutePath());
//		System.out.println(doc.toString());
		//2nd arg: export page files (add to mets filesec), 3rd arg: export alto files, 4th arg: export images
		TrpMetsBuilder metsBuilder = new TrpMetsBuilder();
		Mets mets = metsBuilder.buildMets(doc, true, false, true, null);
		String outFile = folder.getAbsolutePath()+"/mets.xml";
		JaxbUtils.marshalToFile(mets, new File(outFile), TrpDocMetadata.class);
		if (printResultOnSysOut)
			JaxbUtils.marshalToSysOut(mets, TrpDocMetadata.class);
	}
	
	public static void batchCreateMets(String path) {
		File folder = new File(path);
		for (File f : folder.listFiles()) {
			if (!f.isDirectory())
				continue;
			
			try {
				logger.info("Creating mets for folder: "+f.getAbsolutePath());
				createMets(f, false);
			} catch (IOException | JAXBException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}
	
	
	public static void main(String[] args){
		try {
//			batchCreateMets("/media/iza_retro/DIG_Auftraege/D_2014_0022_BSB_Klassikerausgaben/abgeschlossen/");
			createMets(new File(docPath), true);
		} catch (Exception e) {
			logger.error("Caught: " + e.getMessage(), e);
		}
	}
}
