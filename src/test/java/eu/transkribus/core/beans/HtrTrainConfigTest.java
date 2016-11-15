package eu.transkribus.core.beans;

import javax.xml.bind.JAXBException;

import eu.transkribus.core.model.beans.DocumentSelectionDescriptor;
import eu.transkribus.core.model.beans.DocumentSelectionDescriptor.PageDescriptor;
import eu.transkribus.core.model.beans.HtrTrainConfig;
import eu.transkribus.core.model.beans.UroHtrTrainConfig;
import eu.transkribus.core.util.JaxbUtils;

public class HtrTrainConfigTest {
	public static void main(String[] args) throws JAXBException{
		HtrTrainConfig htc = new UroHtrTrainConfig();
		
		for(int i = 1; i < 3; i++) {
			DocumentSelectionDescriptor d = new DocumentSelectionDescriptor();
			d.setDocId(i);
			for(int j = 1; j < 3; j++) {
				PageDescriptor p = new PageDescriptor();
				p.setPageId(j);
				p.setTsId(j);
				d.getPages().add(p);
			}
			htc.getTrain().add(d);
		}
		
		System.out.println(JaxbUtils.marshalToString(htc, DocumentSelectionDescriptor.class, PageDescriptor.class));
	}
}
