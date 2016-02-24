package eu.transkribus.core.model.dao;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.activation.UnknownObjectException;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.pagecontent.PageType;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.RegionType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.builder.TrpPageTranscriptBuilder;
import eu.transkribus.core.model.dao.FakeDocProvider;

public class PageXmlDaoTest {
	public static void main(String[] args) {
		TrpDoc doc = FakeDocProvider.create(false);

		TrpTranscriptMetadata md = doc.getPages().get(0).getTranscripts().get(0);

		try {
			JAXBPageTranscript transcript = new JAXBPageTranscript(md);
			transcript.build();
//			JAXBPageTranscript transcript = TrpPageTranscriptBuilder.build(md);

			//get Source Document as String
//			DOMSource domSource = new DOMSource(transcript.getSourceDoc());
//			StringWriter writer = new StringWriter();
//			StreamResult result = new StreamResult(writer);
//			TransformerFactory tf = TransformerFactory.newInstance();
//			Transformer transformer = tf.newTransformer();
//			transformer.transform(domSource, result);
//			System.out.println("XML IN String format is: \n" + writer.toString());
			
			//check JaxB Element
			PcGtsType page = transcript.getPageData();
			
			if(page == null){
				System.out.println("page XML is null");
				System.exit(0);
			} 
			PageType pageType = page.getPage();
			System.out.println(page.getMetadata());
			if(pageType == null){
				System.out.println("pagetype element is null");
				System.exit(0);
			}
			
			if(pageType.getTextRegionOrImageRegionOrLineDrawingRegion() == null){
				System.out.println("Region list is null");
				System.exit(0);
			}
			
			int i= 0;
			for(TextRegionType tr : transcript.getPage().getTextRegions(true)) {
				tr.setId(""+i++);
			}
			
			List<TrpRegionType> regions = pageType.getTextRegionOrImageRegionOrLineDrawingRegion();
			
			for(RegionType r : regions){
				if(r instanceof TextRegionType){
					TextRegionType t = (TextRegionType)r;
					System.out.println(t.getId());
				}
			}
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}		
		
//		try {
//			PrimaPageTranscript ppt = PageXmlDao.getPrimaPageTranscript(md);
//			Page page = ppt.getPageData();
//			
//			System.out.println(page.getImageFilename());
//			Region r = page.getLayout().getRegion("tempReg357564684568544579089");
//			System.out.println(r.getType().getName());
////			System.out.println(page.getLayout().getParentChildRelation(r.getType(), r.getId().toString()).getRelationType());
//			
//			IdRegister idr = r.getIdRegister();
////			idr.
//			System.out.println(idr);
//		} catch (IllegalArgumentException e) {
//			
//			e.printStackTrace();
//		} catch (MalformedURLException e) {
//			
//			e.printStackTrace();
//		} catch (UnsupportedFormatVersionException e) {
//			
//			e.printStackTrace();
//		}

	}
}
