package eu.transkribus.core.beans;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import eu.transkribus.core.model.beans.DocumentDuplicationDescriptor;
import eu.transkribus.core.model.beans.DocumentDuplicationDescriptor.PageDescriptor;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.model.beans.HtrTrainConfig;

public class HtrTrainConfigTest {
	public static void main(String[] args) throws JAXBException{
		HtrTrainConfig htc = new HtrTrainConfig();
		
		for(int i = 1; i < 3; i++) {
			DocumentDuplicationDescriptor d = new DocumentDuplicationDescriptor();
			d.setDocId(i);
			for(int j = 1; j < 3; j++) {
				PageDescriptor p = new PageDescriptor();
				p.setPageId(j);
				p.setTsId(j);
				d.getPages().add(p);
			}
			htc.getTrainList().add(d);
		}
		
		System.out.println(JaxbUtils.marshalToString(htc, DocumentDuplicationDescriptor.class, PageDescriptor.class));
	}
}
