package eu.transkribus.core.model.beans;

import java.io.File;

import javax.xml.bind.JAXBException;

import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.util.PageXmlUtils;

public class TrpElementComparatorTest {
	
	
	public static void main(String[] args) throws Exception {
		// TODO: create simple page-xml where this error occurs (region with no reading-order, same reading-order or whatever...)
		
		String file = "T:/leo4_data/Newseye/ONB_start/ONB_aze/aze19001104/page/aze19001104_00000017.xml";
		PcGtsType pc = PageXmlUtils.unmarshal(new File(file));
		TrpPageType page = (TrpPageType) pc.getPage();
		page.sortRegions();
	}

}
