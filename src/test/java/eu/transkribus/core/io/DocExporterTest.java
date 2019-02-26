package eu.transkribus.core.io;

import org.dea.fimgstoreclient.FimgStoreGetClient;

import eu.transkribus.core.TrpFimgStoreConf;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpFImagestore;
import eu.transkribus.core.model.builder.CommonExportPars;

public class DocExporterTest {
	public static void main(String[] args) throws Exception {
		testTeiExport();
	}
	
	
public static void testTeiExport() {

		TrpFImagestore storeConfig = TrpFimgStoreConf.getFImagestore();
		DocExporter ex = new DocExporter(new FimgStoreGetClient(storeConfig));
		try {
			TrpDoc doc = LocalDocReader.load("C:/tmp/EMAN_GuizotProject_images/EMAN_GuizotProject_images");
			//TrpDoc doc = LocalDocReader.load("/mnt/dea_scratch/TRP/Bentham_box_002", true);
//			ex.writeFatDoc(doc, "/tmp/fatTest", true);
			CommonExportPars commonPars = new CommonExportPars();
			commonPars.setPages("1");
			String exportFilename = "C:/tmp/teiExporterTest.xml";
			ex.writeTEI(doc, exportFilename, commonPars, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
}
