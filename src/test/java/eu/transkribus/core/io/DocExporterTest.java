package eu.transkribus.core.io;

import eu.transkribus.core.io.DocExporter;
import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.model.beans.TrpDoc;

public class DocExporterTest {
	public static void main(String[] args) throws Exception {
		testExportDocForText2Image();
	}
	
	public static void testSth() {
		DocExporter ex = new DocExporter();
		try {
			TrpDoc doc = LocalDocReader.load("/mnt/dea_scratch/TRP/Bentham_box_002", true);
//			ex.writeFatDoc(doc, "/tmp/fatTest", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void testExportDocForText2Image() throws Exception {
		DocExporter exporter = new DocExporter();
		
		TrpDoc doc = LocalDocReader.load("/mnt/dea_scratch/TRP/Bentham_box_002", true);

		exporter.exportDocForText2ImageTool(doc, "/home/sebastian/tmp/test_t2i_export", true, null, false, true);
		
	}
}
