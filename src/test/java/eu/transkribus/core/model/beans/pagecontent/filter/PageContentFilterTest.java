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
		DeterioratedPolygonFilter filter = new DeterioratedPolygonFilter(false);
		filter.doFilter(pc);
		final int nrOfRegionsAfterFilter = PageXmlUtils.getTextRegions(pc).size();
		//check if exactly one region was removed.
		final int expectRegionsRemoved = 1;
		Assert.assertEquals(expectRegionsRemoved, filter.getRemovedRegionCount());
		Assert.assertEquals(nrOfRegionsInitial - expectRegionsRemoved, nrOfRegionsAfterFilter);
	}
	
	@Test
	public void testTagPageContentFilterGap() throws JAXBException, IOException {
		//should remove 1 line
		testTagPageContentFilter(loadTaggedXml(), 1, "gap");
	}
	
	@Test
	public void testTagPageContentFilterUnclear() throws JAXBException, IOException {
		//should remove 1 line
		testTagPageContentFilter(loadTaggedXml(), 1, "unclear");
	}
	
	@Test
	public void testTagPageContentFilterGapAndUnclear() throws JAXBException, IOException {
		//should remove 2 lines
		testTagPageContentFilter(loadTaggedXml(), 2, "gap", "unclear");
	}
	
	@Test
	public void testTagPageContentFilterNonsenseTag() throws JAXBException, IOException {
		//should do nothing
		testTagPageContentFilter(loadTaggedXml(), 0, "asdfg");
	}
	
	@Test
	public void testTagPageContentFilterNoTagsSpecified() throws JAXBException, IOException {
		//should do nothing
		testTagPageContentFilter(loadTaggedXml(), 0);
	}
	
	public void testTagPageContentFilter(PcGtsType pc, int expectedLinesRemoved, String...tags) throws JAXBException, IOException {
		TagPageContentFilter tagFilter = new TagPageContentFilter(tags);
		final int nrOfLinesInitial = PageXmlUtils.getLines(pc).size();
		tagFilter.doFilter(pc);
		final int nrOfLinesAfterFilter = PageXmlUtils.getLines(pc).size();
		Assert.assertEquals(expectedLinesRemoved, tagFilter.getRemovedLinesCount());
		Assert.assertEquals(nrOfLinesInitial - expectedLinesRemoved, nrOfLinesAfterFilter);
		
	}

	/**
	 * @return a PcGtsType with a region defined by only 1 point.
	 * @throws JAXBException
	 * @throws IOException
	 */
	private PcGtsType loadDeterioratedRegionXml() throws JAXBException, IOException {
		return loadXmlResource("deterioratedRegion.xml");
	}
	
	/**
	 * @return a PcGtsType with one line containing a gap tag and one line containing an unclear tag
	 * @throws JAXBException
	 * @throws IOException
	 */
	private PcGtsType loadTaggedXml() throws JAXBException, IOException {
		return loadXmlResource("gapAndUnclearTaggedLines.xml");
	}
	
	private PcGtsType loadXmlResource(String name) throws JAXBException, IOException {
		PcGtsType pc;
		try (InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream("PageContentFilter/" + name)) {
			pc = PageXmlUtils.unmarshal(is);
		}
		return pc;
	}
}
