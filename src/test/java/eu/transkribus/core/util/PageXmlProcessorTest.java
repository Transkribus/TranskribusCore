package eu.transkribus.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.dea.fimgstoreclient.FimgStoreGetClient;
import org.dea.fimgstoreclient.IFimgStoreGetClient;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.TranskribusMetadataType;

public class PageXmlProcessorTest {
	
	private static final Logger logger = LoggerFactory.getLogger(PageXmlProcessorTest.class);
	SebisStopWatch ssw = new SebisStopWatch();
	
//	@Test
//	public void testGetAllRegionsIds() throws XPathFactoryConfigurationException, ParserConfigurationException, MalformedURLException, IllegalArgumentException, XPathExpressionException, SAXException, IOException {
//		final String key = "CMTSKWAFFAQPTGSHECQTKDCM";
//		PageXmlProcessor proc = PageXmlProcessorFactory.newInstance();
//		List<String> ids = proc.getAllTextRegionIds(key);
//		ids.stream().forEach(id -> logger.debug(id));
//	}
	
	@Test
	public void testGetAllLines() {
		final String keyGT = "CMTSKWAFFAQPTGSHECQTKDCM";
		final String keyHyp = "MXDDRBWLXBQMEVAGMGDQMJJS";
		PageXmlProcessor proc = null;
		List<String> idsGT = null;
		List<String> idsHyp = null;
		ssw.start();
		IFimgStoreGetClient getter = new FimgStoreGetClient("files.transkribus.eu", "/");
		try {
			proc = new PageXmlRemoteFileProcessor(getter);
		} catch (XPathFactoryConfigurationException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		try {
			idsGT = proc.getAllLineIds(keyGT);
			idsHyp = proc.getAllLineIds(keyHyp);
		} catch (IllegalArgumentException | XPathExpressionException | SAXException | IOException e) {	
			e.printStackTrace();
		}
		ssw.stop(true);
		logger.debug("GT Id size : "+idsGT.size());
		logger.debug("Hyp Id size : "+idsHyp.size());
		
		ssw.start();
		if(idsGT.size() != idsHyp.size()) {
			logger.debug("Cannot compare document with diffrent Layout Analysis");
		}
		ssw.stop(true);
//		for(String s : idsGT) {
//			logger.debug("Line id : "+s);
//		}
		String a = idsGT.parallelStream().sorted().collect(Collectors.joining(""));
		String b = idsHyp.parallelStream().sorted().collect(Collectors.joining(""));
		a.equals(b);
		logger.debug(a);
		logger.debug(b);
		
	}
	
	@Test
	public void testDeleteNode() throws TransformerFactoryConfigurationError, TransformerException, JAXBException, IOException, 
			XPathExpressionException, XPathFactoryConfigurationException, ParserConfigurationException, SAXException {
		URL testResource = this.getClass().getClassLoader().getResource("PageContentFilter/deterioratedRegion.xml");
		File tmp = Files.createTempFile(this.getClass().getSimpleName(), ".xml").toFile();
		FileUtils.copyFile(FileUtils.toFile(testResource), tmp);
		
		final int docId = 1234;
		final String status = "kaputt";
		
		//add metadata node
		PcGtsType pc = PageXmlUtils.unmarshal(tmp);
		TranskribusMetadataType md = new TranskribusMetadataType();
		md.setDocId(docId);
		md.setStatus(status);
		pc.getMetadata().setTranskribusMetadata(md);
		PageXmlUtils.marshalToFile(pc, tmp);
		logger.info(new String(PageXmlUtils.marshalToBytes(pc)));
		
		//check node existence in file on disk
		pc = PageXmlUtils.unmarshal(tmp);
		TranskribusMetadataType md2 = pc.getMetadata().getTranskribusMetadata();
		Assert.assertEquals(docId, md2.getDocId());
		Assert.assertEquals(status, md2.getStatus());
		
		//remove node using XML Processor
		PageXmlFileProcessor xmlProc = new PageXmlFileProcessor();
		Document xmlDoc = xmlProc.parse(tmp);
		xmlProc.deleteNodeByXPath(xmlDoc, "//TranskribusMetadata");
		xmlProc.writeToFile(xmlDoc, tmp, true);
		
		//log war content in file
		String rawContent = FileUtils.readFileToString(tmp);
		logger.info("\n\nString=\n{}", rawContent);
		//check that file has at least one two line breaks
		Assert.assertTrue(StringUtils.countMatches(rawContent, "\n") > 2);
		//check that there are no empty lines
		Assert.assertTrue(StringUtils.countMatches(rawContent, "\n\n") == 0);
		
		//check that node is gone in file
		pc = PageXmlUtils.unmarshal(tmp);
		logger.info(new String(PageXmlUtils.marshalToBytes(pc)));
		TranskribusMetadataType md3 = pc.getMetadata().getTranskribusMetadata();
		Assert.assertNull(md3);
	}
	
	@Test
	public void testWriteToFile() throws IOException, TransformerFactoryConfigurationError, TransformerException, XPathFactoryConfigurationException, ParserConfigurationException, SAXException, XPathExpressionException {
		//any XML will work
		URL testResource = this.getClass().getClassLoader().getResource("PageContentFilter/deterioratedRegion.xml");
				
		PageXmlFileProcessor xmlProc = new PageXmlFileProcessor();
		Document xmlDoc = xmlProc.parse(testResource);
		xmlProc.deleteNodeByXPath(xmlDoc, "//TranskribusMetadata");
		//construct a filename with spaces, brackets and umlaut
		final File outFile = new File("/tmp/test_write_to_file with spaces in name (ü).xml");
		logger.debug("Out file path = {}", outFile.getAbsolutePath());
		
		//this is how LocalDocReader constructs the URL from it.
		final URL outFileUrl = outFile.toURI().toURL();
		logger.debug("File URL = {}", outFileUrl);
		
		//In ADocImportJob the URL is used for writing the output of the file operation
		final File pageFile = FileUtils.toFile(outFileUrl);
		logger.debug("File shall be written to: {}", pageFile.getAbsolutePath());
		
		//this must point to the same file as before
		Assert.assertTrue(pageFile.getAbsolutePath().equals(outFile.getAbsolutePath()));
		
		//PageXmlProcessor instantiated StreamResult from the file and not from a FileOutputStream and this is what was written to disk.
		final String badOutFilePath = "/tmp/test_write_to_file%20with%20spaces%20in%20name%20(%C3%BC).xml";
		
		try {
			xmlProc.writeToFile(xmlDoc, pageFile, true);
			logger.debug("Output written to: {}", pageFile.getAbsolutePath());
			
			Assert.assertFalse("TrpXPathProcessor wrote the file to the wrong location!", new File(badOutFilePath).isFile());
			
			//The must be at the expected location.
			Assert.assertTrue("TrpXPathProcessor did not write the expected output file!", pageFile.isFile());
		} finally {
			if(pageFile.isFile()) {
				FileUtils.deleteQuietly(pageFile);
			}
			File badOutFile = new File(badOutFilePath);
			if(badOutFile.isFile()) {
				FileUtils.deleteQuietly(badOutFile);
			}
		}
		
	}
}
