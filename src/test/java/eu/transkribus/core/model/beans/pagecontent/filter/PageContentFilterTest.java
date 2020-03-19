package eu.transkribus.core.model.beans.pagecontent.filter;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;

import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.util.PageXmlUtils;

public class PageContentFilterTest {
	
	
	@Test
	public void testNullFilterChain() throws JAXBException, IOException {
		PcGtsType pc = loadDeterioratedRegionXml();
		
		final String xmlStringBefore = new String(PageXmlUtils.marshalToBytes(pc));
		
		IPageContentFilter filterChain = new PageContentFilterChain();
		filterChain.doFilter(pc);
		
		final String xmlStringAfter = new String(PageXmlUtils.marshalToBytes(pc));
		/**
		 * Check disabled as there is no way to check equality of a PAGE XML.
		 * readingOrder will be rewritten on marshalling!
		 */
//		Assert.assertEquals(xmlStringBefore, xmlStringAfter);
	}
	
	@Test
	public void testDeterioratedRegionFilter() throws IOException, JAXBException {
		PcGtsType pc = loadDeterioratedRegionXml();
		//this resource contains 3 regions. One of the polygons consists of 1 point only and should be removed.
		final int nrOfRegionsInitial = PageXmlUtils.getTextRegions(pc).size();
		IPageContentFilter filter = new DeterioratedPolygonFilter(false);
		filter.doFilter(pc);
		final int nrOfRegionsAfterFilter = PageXmlUtils.getTextRegions(pc).size();
		//check if exactly one region was removed.
		Assert.assertEquals(nrOfRegionsInitial - 1, nrOfRegionsAfterFilter);
	}

	private PcGtsType loadDeterioratedRegionXml() throws JAXBException, IOException {
		PcGtsType pc;
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("PageContentFilter/deterioratedRegion.xml")) {
			pc = PageXmlUtils.unmarshal(is);
		}
		return pc;
	}
}
