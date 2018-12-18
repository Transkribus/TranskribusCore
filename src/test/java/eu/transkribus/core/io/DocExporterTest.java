package eu.transkribus.core.io;

import org.dea.fimgstoreclient.FimgStoreGetClient;

import eu.transkribus.core.TrpFimgStoreConf;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpFImagestore;

public class DocExporterTest {
	public static void main(String[] args) throws Exception {
	}
	
	public static void testSth() {
		TrpFImagestore storeConfig = TrpFimgStoreConf.getFImagestore();
		DocExporter ex = new DocExporter(new FimgStoreGetClient(storeConfig));
		try {
			TrpDoc doc = LocalDocReader.load("/mnt/dea_scratch/TRP/Bentham_box_002", true);
//			ex.writeFatDoc(doc, "/tmp/fatTest", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
}
