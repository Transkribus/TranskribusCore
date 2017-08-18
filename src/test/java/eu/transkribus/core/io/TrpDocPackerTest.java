package eu.transkribus.core.io;

import java.io.File;

import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.io.TrpDocPacker;
import eu.transkribus.core.io.util.Md5SumComputer;
import eu.transkribus.core.model.beans.TrpDoc;

public class TrpDocPackerTest {
	
	public static void main(String[] args) {
	
//	final String BASE = "/mnt/dea_scratch/TRP/";
////	String docPath = BASE + "Bentham_box_002/";
//	String shitDoc = BASE + "Schauplatz";	
//	String shitDoc = BASE + "TRPTestDoc_I_ZvS_1901_1Q"; 
	
		
	String shitDoc = "/mnt/iza_retro/P6080-029-019_transcriptorium/master_images/14_bozen_stadtarchiv/Ratsprotokolle Bozen 1470-1684 - Lieferung USB Platte 9-7-2013/HS 37/HS 37a";
	String zipPath = "/tmp/test.zip";
	
//	String unzipPath = "/home/philip/TRP_DOC_1397138823910.zip";
	
	try {
		TrpDocPacker packer = new TrpDocPacker();
		
		TrpDoc doc = LocalDocReader.load(shitDoc, true);
		
		Md5SumComputer md5Comp = new Md5SumComputer();
//		md5Comp.addObserver(passthroughObserver);
		doc = md5Comp.computeAndSetMd5Sums(doc);	
		
		File zipFile = packer.packDocFiles(doc, zipPath);
//		
//		TrpDoc doc2 = packer.unpackDoc(new File(unzipPath), null);
				
//		System.out.println(doc2);
//		TrpDoc doc = LocalDocReader.load(docPath, false);
	}catch (Exception e){
		e.printStackTrace();
	}
	
	}
}