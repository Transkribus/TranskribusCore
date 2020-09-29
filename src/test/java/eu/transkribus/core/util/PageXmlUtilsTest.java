package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;

public class PageXmlUtilsTest {
	private static final Logger logger = LoggerFactory.getLogger(PageXmlUtilsTest.class);
	
	/**
	 * A test outlining an issue that might be causing https://github.com/Transkribus/TranskribusServer/issues/61
	 * For large input texts I did not see this method return and that's why the test is set ignored.
	 * 
	 * @throws JAXBException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	@Ignore
	@Test
	public void testAssignPlainTextToPage() throws JAXBException, IOException, URISyntaxException {
		String basePath = "/mnt/dea_scratch/TRP/test/gui_issue_61/";
		Assume.assumeTrue(new File(basePath).isDirectory());
		
		File pageFile = new File(basePath + "/page", "p001.xml");
		PcGtsType pc = PageXmlUtils.unmarshal(pageFile);
		
		String lineTxt = FileUtils.readFileToString(new File(basePath, "plaintext_no_linebreaks.txt"));
		
		String txt = "";
		//The method runtime seems to depend on the nr of lines parameter here.
		//For small values it finishes quickly. The method blocked on the server for > 8 hours in some instances.
		int nrOfLines = 10;
		for(int i = 0; i < nrOfLines; i++) {
			txt += lineTxt + "\n";
		}
		
		logger.info("Test text length = {}", txt.length());
		PageXmlUtils.applyTextToLines((TrpPageType) pc.getPage(), txt);
	}
	
	public static void testGetTextRegions() throws Exception {
		String transcriptWithTables = "https://dbis-thure.uibk.ac.at/f/Get?id=VCLTRLDSWETCXIHQNHKOPRLS";
		
		PcGtsType t = PageXmlUtils.unmarshal(new URL(transcriptWithTables));
		
		List<TextRegionType> tr = PageXmlUtils.getTextRegions(t);
		
		for (TextRegionType r : tr) {
			System.out.println("tr: "+r.getClass().getSimpleName()+" id: "+r.getId()+" n-lines: "+r.getTextLine().size());
		}
	}
	
	public static void testValidation() {
		final String path = "/mnt/dea_scratch/TRP/Bentham_box_002/page/002_080_001.xml";
		try {
			logger.info(""+PageXmlUtils.isValid(new File(path)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
//		testGetTextRegions();
//		testSth();
		testValidation();
	}
}
