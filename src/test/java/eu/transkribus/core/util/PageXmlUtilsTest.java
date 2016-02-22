package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.bind.JAXBException;

import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.PageXmlUtils;

public class PageXmlUtilsTest {
	public static void main(String[] args){
		File[] files = {
				new File("/mnt/dea_scratch/TRP/test/page_xsl_test/ocr/Mittheilungen_Perthes_1855_0009.xml"),
				new File("/mnt/dea_scratch/TRP/test/ImagesOldPageXml/page/2010-03-19_backup/035_320_001.xml"),
				new File("/mnt/dea_scratch/TRP/test/page_xsl_test/Mittheilungen_Perthes_1855_0009.xml")
				};
		
		try {
			PcGtsType t = PageXmlUtils.createEmptyPcGtsType(new URL("https://dbis-thure.uibk.ac.at/fimagestore/Get?fileType=metadata&id=YSUGXUUGAHYCUQVMEUJAYQGO"));
			JaxbUtils.marshalToSysOut(t);
		} catch (IOException | JAXBException e) {
			
			e.printStackTrace();
		}
		
//		for(File f : files){
//			try {
//				System.out.println(XmlUtils.getXmlFormat(f).toString());
//			} catch (IOException e) {
//				
//				e.printStackTrace();
//			}
//		}
	}
}
