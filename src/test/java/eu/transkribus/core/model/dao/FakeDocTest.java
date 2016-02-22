package eu.transkribus.core.model.dao;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.dao.FakeDocProvider;

public class FakeDocTest {
	public static void main(String[] args){
		TrpDoc doc = FakeDocProvider.create(false);
		System.out.println(doc.toString());
		
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(TrpDocMetadata.class);
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			FileOutputStream out = new FileOutputStream("/tmp/md.xml");
	
			m.marshal(doc.getMd(), out);
		
		} catch (JAXBException e) {
			
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		
//		FimgStoreUriBuilder builder = new FimgStoreUriBuilder();
//		System.out.println(builder.getImgUri("AAAAAAAAAAAAAAAAAAAAAAAA", ImgType.view).toString());
	}
}
