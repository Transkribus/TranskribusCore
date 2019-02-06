package eu.transkribus.core.io.formats;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.tools.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.io.Files;
import com.itextpdf.text.Image;

import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.XmlUtils;
import eu.transkribus.interfaces.types.util.ImageUtils;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class IngestHTRIntoAbbyyXML {
	private static final Logger logger = LoggerFactory.getLogger(IngestHTRIntoAbbyyXML.class);
	
	private static boolean createFinalPageXml = false;

	public static void main(String[] args) throws IOException {	
		
		if (createFinalPageXml){
			
			String imgDir = args[2];
			
			File imgFileDir = new File(imgDir);
			File convertedFileDir = new File(imgDir +"/convertedAbbyy/" );
			
			if(!convertedFileDir.exists()){
				logger.error("'convertedAbbyy' directoriy does not exist: " + args[2]);
				return;
			}
			
			File[] ocrFiles = convertedFileDir.listFiles();
			File[] imgFiles = imgFileDir.listFiles(new FileFilter() {
			    @Override
			    public boolean accept(File pathname) {
			        return pathname.isFile();
			    }
			});
			
			String resultDir = imgFileDir.getAbsolutePath()+"/convertedPage/";
			new File(resultDir).mkdirs();
			
			for (File abbyyXml : ocrFiles){
			
				if  (abbyyXml != null){
				
					BufferedImage img = null;
					
					String imgFn = abbyyXml != null ? imgDir + File.separator +  StringUtils.substringBefore(abbyyXml.getName(),".xml") + ".jpg" : "";
					logger.debug("imgFn " + imgFn);
					
					File imgFile = new File(imgFn);
					if (imgFile.exists()){
		
						try 
						{
						    img = ImageIO.read(imgFile.getAbsoluteFile());
							File pageOutFile = new File(resultDir+abbyyXml.getName());
							
							if (pageOutFile.exists()){
								continue;
							}
			
						    createPageXml(pageOutFile, true, abbyyXml, true, true, false, imgFile.getName(), new Dimension(img.getWidth(), img.getHeight()));
						} 
						catch (IOException e) 
						{
						    e.printStackTrace();
						}
					}
				}
			}
			
		}
		else{
		
			if(args.length != 2){
				usage();
				return;
			}
			
			final String htrDir, ocrDir;
	
			htrDir = args[0];
			ocrDir = args[1];
					
			File htrMainDir = new File(htrDir);
			File ocrMainDir = new File(ocrDir);
			
			ingestHtrIntoAbbyyForAllFolders(ocrMainDir, htrMainDir);
		
		}


	}
	
	private static void ingestHtrIntoAbbyyForAllFolders(File ocrDir, File htrAndImgDir) throws IOException {
		if(!ocrDir.exists() || !htrAndImgDir.exists()){
			logger.error("One of the start directories does not exist: " + ocrDir + " or " + htrAndImgDir);
			usage();
			return;
		}
		
		if (htrAndImgDir.listFiles().length != ocrDir.listFiles().length){
			logger.error("Directories must contain the same number of folders!");
			usage();
			return;
		}
		
			
		int countAllConverted = 0;
		for (File imgFileDir : htrAndImgDir.listFiles()){
			File ocrFileDir = new File(ocrDir.getAbsolutePath() + File.separator + imgFileDir.getName() + "/ocr");
			logger.debug("ocr folder: " + ocrFileDir.getAbsolutePath());
			File[] ocrFiles = ocrFileDir.listFiles();
			
			File htrFileDir = new File(imgFileDir.getAbsolutePath() + "/page");
			logger.debug("htr folder: " + htrFileDir.getAbsolutePath());
			File[] htrFiles = htrFileDir.listFiles();

			logger.debug("img folder: " + imgFileDir.getAbsolutePath());
			File[] imgFiles = imgFileDir.listFiles(new FileFilter() {
			    @Override
			    public boolean accept(File pathname) {
			        return pathname.isFile();
			    }
			});
						
			File resultDir = new File(htrAndImgDir.getParentFile().getAbsolutePath() + "/converted" + File.separator + imgFileDir.getName() + File.separator + "ocr");
			if (resultDir.exists()){
				logger.debug(resultDir.getAbsolutePath() + " Result dir exists already - try next one");
				continue;
			}
			
			resultDir.mkdirs();
			
			logger.debug("resultDir folder: " + resultDir.getAbsolutePath());
			
			File sampleDir = new File(htrAndImgDir.getParentFile().getAbsolutePath() + "/samples" + File.separator);
			sampleDir.mkdirs();
			
			logger.debug("sampleDir folder: " + sampleDir.getAbsolutePath());
						
			if (htrFiles.length != ocrFiles.length){
				logger.error("Directories must contain the same number of files!");
				usage();
				return;
			}
	
			Arrays.sort(htrFiles, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
			Arrays.sort(ocrFiles, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
			Arrays.sort(imgFiles, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
//			
//			displayFiles(htrFiles);
//			displayFiles(ocrFiles);
//			displayFiles(imgFiles);
						
			int nr = 0;
			for (File htr : htrFiles){

				//String ocrName = findOcrFilename(htr);
				//String ocrFn = ocrName != null ? ocrFileDir + File.separator + ocrName + ".xml" : "";
				
				String ocrFn = ocrFileDir + File.separator + FilenameUtils.getBaseName(htr.getName())+ ".xml";
				logger.debug("ocrFn " + ocrFn);
				
				File ocrFile = new File(ocrFn);
				if (ocrFile.exists()){
//					logger.debug("htr file: " + htr.getName());
//					logger.debug("ocr file: " + ocrFile.getName());
					
					//System.in.read();
					
					File convertedFile = new File(resultDir.getAbsolutePath()+File.separator+htr.getName());
					
					if (convertedFile.exists()){
						logger.debug("Already converted (" + countAllConverted++ + ")");
						nr++;
						continue;
					}
					
					//ingestHTRIntoOCR(htr, ocrFiles[nr++]);
					File abbyyXml = combineHTRAndOCR(htr, ocrFile, convertedFile);
					
					//take test sample for every 500th page
					if  (abbyyXml != null && nr % 500 == 13){
						
						String resultPageDir = sampleDir.getAbsolutePath()+"/page/";
						new File(resultPageDir).mkdirs();
						
						File pageOutFile = new File(resultPageDir+htr.getName());
						
						BufferedImage img = null;

						try 
						{
						    img = ImageIO.read(imgFiles[nr].getAbsoluteFile());

						    FileUtils.copyFileToDirectory(imgFiles[nr], sampleDir);
						    createPageXml(pageOutFile, true, abbyyXml, true, true, false, imgFiles[nr].getName(), new Dimension(img.getWidth(), img.getHeight()));
						} 
						catch (IOException e) 
						{
						    e.printStackTrace();
						}
					}
					nr++;
				}
				else{
					logger.error("No ocr File found :(");
				}
			}
			
		}

		

		
	}

	private static void startSimpleTestWithFilenames(){
	    // read from files
	    String htrFilename= "C:/01_Projekte/READ/Projekte/Lehmann/Konvertierung/page/00000101.xml";
	    String ocrFilename = "C:/01_Projekte/READ/Projekte/Lehmann/Konvertierung/ocr/3697706.xml";
	    
	    ingestHTRIntoOCR(new File(htrFilename), new File(ocrFilename));
	}
	
	private static String findOcrFilename(File htrFile){
		try {
			FileInputStream fileIsHTR = new FileInputStream(htrFile);
				
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document htrDocument = builder.parse(fileIsHTR);			
			
			XPath xPath = XPathFactory.newInstance().newXPath();
			String expressionMd = "//TranskribusMetadata";
			
			NodeList nodeListHTR = (NodeList) xPath.compile(expressionMd).evaluate(htrDocument, XPathConstants.NODESET);
			
			System.out.println("nodeList length " + nodeListHTR.getLength());
			
			for (int i = 0; i < nodeListHTR.getLength(); i++){
				
				NamedNodeMap htrLineString = nodeListHTR.item(i).getAttributes();
    			Node pageId = nodeListHTR.item(i).getAttributes().getNamedItem("pageId");
    			if (pageId != null){
    				return pageId.getTextContent();
    			}
			}
		
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	private static void ingestHTRIntoOCR(File htrFile, File ocrFile) {
		try {
			
			FileInputStream fileIsHTR = new FileInputStream(htrFile);
			FileInputStream fileIsOCR = new FileInputStream(ocrFile);
				
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document htrDocument = builder.parse(fileIsHTR);
			Document ocrDocument = builder.parse(fileIsOCR);			
			
			XPath xPath = XPathFactory.newInstance().newXPath();
			String expressionHTR = "//TextLine/TextEquiv/Unicode";
			String expressionOCR = "//line";
			//xPath: get all charParams for current node
			String expAllCharParamsOfLine = ".//charParams";
			
			
			NodeList nodeListOCR = (NodeList) xPath.compile(expressionOCR).evaluate(ocrDocument, XPathConstants.NODESET);
			NodeList nodeListHTR = (NodeList) xPath.compile(expressionHTR).evaluate(htrDocument, XPathConstants.NODESET);
			
			int countEqualLines = 0;
			int countUnequalLines = 0;
			int countEqualLineLengths = 0;
			int countUnequalLinesLenths = 0;
			
//			String test = "test";
//			for (char c : test.toCharArray()){
//				System.out.println(c);
//				
//			}
			
			//System.out.println("result is " + result);
			//		// and iterate on links
			System.out.println("nodeList length " + nodeListHTR.getLength());
			for (int i = 0; i < nodeListHTR.getLength(); i++){
				
				String htrLineString = nodeListHTR.item(i).getTextContent();
				
//				NodeList formatting = nodeListOCR.item(i).getChildNodes();

//				for (int j = 0; j<formatting.getLength(); j++){
//					//NodeList charParams = formatting.item(j).getChildNodes();
//					
//					logger.debug("node " + formatting.item(j));
				
					Node ocrLine = (Node) nodeListOCR.item(i);
					Node formatting = ocrLine.getLastChild();
					
					//logger.debug(ocrLine.getLastChild().getNodeName());
					
					String left = "", right = "", top = "", bottom = "";
							
		        	NamedNodeMap ocrLineAttributes = ocrLine.getAttributes();
		        	for (int l = 0; l<ocrLineAttributes.getLength(); l++){
		        		Node currAttr = ocrLineAttributes.item(l);
		        		//logger.debug("node map name " + currAttr.getNodeName());
		        		switch (currAttr.getNodeName()){
			        		case "l": left=currAttr.getNodeValue();
			        		case "r": right=currAttr.getNodeValue();
			        		case "t": top=currAttr.getNodeValue();
			        		case "b": bottom=currAttr.getNodeValue();
			        		default: break;
		        		}
		        	}
		        	
		        	int lineLeft = Integer.valueOf(left);
		        	int lineRight = Integer.valueOf(right);
		        	int lineTop = Integer.valueOf(top);
		        	int lineBottom = Integer.valueOf(bottom);
				
					NodeList nlCharParams = (NodeList) xPath.compile(expAllCharParamsOfLine).evaluate((Node) nodeListOCR.item(i), XPathConstants.NODESET);
//					logger.debug("ocr length: " + nlCharParams.getLength());
//					logger.debug("htr length: " + htrLineString.length());
					
					boolean differentLength = nlCharParams.getLength() != htrLineString.length();					
					float newCharWidth = ( (Integer.valueOf(right)-Integer.valueOf(left))/htrLineString.length());
					
					String ocrLineString = "";
					for (int k = 0; k<nlCharParams.getLength(); k++){
						ocrLineString += nlCharParams.item(k).getTextContent();
					}
					
//					logger.debug("htr line text: " + htrLineString);
//					logger.debug("ocr line text: " + ocrLineString);
					
					if (htrLineString.length() == ocrLineString.length()){
						countEqualLineLengths++;
					}
					else{
						countUnequalLinesLenths++;
					}
					
					if (htrLineString.equals(ocrLineString)){
//						logger.debug("ocr and htr are the same - keep charparams as they are!!");
						countEqualLines++;	
						continue;
					}
					
					else{
						countUnequalLines++;
						//logger.debug("go on");
					
					}
					
//					if (ocrLineString.length() > htrLineString.length()){
//						System.in.read();
//					}
					
					for (int k = 0; k<nlCharParams.getLength(); k++){
						
						Node charParamInOCR = nlCharParams.item(k);
						
			        	Character ocrChar = nlCharParams.item(k).getTextContent().charAt(0);
			        	Character htrChar = k<htrLineString.length()? htrLineString.charAt(k) : null;

//			        	NamedNodeMap nodeMap = charParamInOCR.getAttributes();
//			        	for (int l = 0; l<nodeMap.getLength(); l++){
//			        		logger.debug("node map name " + nodeMap.item(l).getNodeName());
//			        		logger.debug("node map " + nodeMap.item(l).getNodeValue());
//			        	}
						
						/*
						 * cases to be considered
						 * (1) ocr line shorter than htr -> add charparams for all additional chars
						 * (2) htr line shorter than ocr -> delete needless chars
						 * (3) equal size -> only replace text content for unequal chars
						 * (4) empty char in htr text but not in ocr
						 */
			        	
			        	//case 2: pointless ocr charParams need to be deleted
			        	if(htrChar == null){
//			        		logger.debug("remove child: " + charParamInOCR.getTextContent());
			        		
			        		//text node of this element needs to be removed as well - otherwise you get empty lines in xml
			                Node prevElem = charParamInOCR.getPreviousSibling();
			                if (prevElem != null && 
			                    prevElem .getNodeType() == Node.TEXT_NODE &&
			                    prevElem .getNodeValue().trim().length() == 0) {
			                	charParamInOCR.getParentNode().removeChild(prevElem);
			                }
			        		//System.in.read();
			        		charParamInOCR.getParentNode().removeChild(charParamInOCR);
			        		
			        		continue;
			        	}
			        	
			        	//if chars differ: replace ocr with htr (includes case 3, valid for case 1 and 2 too)
			        	if (ocrChar != htrChar){
			        		
			        		//if we deal with different length of htr and ocr we newly calculate the coordinates of the bounding box of each character 
			        		if (differentLength){
			        			int l = (int) (lineLeft+newCharWidth*k);
			        			int r = (int) (lineLeft+newCharWidth*(k+1));
			        			
			        			Node leftCoord = charParamInOCR.getAttributes().getNamedItem("l");
			        			leftCoord.setNodeValue(Integer.toString(l));
			        			
			        			Node rightCoord = charParamInOCR.getAttributes().getNamedItem("r");
			        			rightCoord.setNodeValue(Integer.toString(r));
			        		}
			        		
			        		//empty space - insert and add wordStart = 1;
			        		if (Character.isSpaceChar(htrChar)){//.equals("\u0020")){
			        			if (charParamInOCR.getNextSibling() != null && charParamInOCR.getNextSibling().getNextSibling() != null){
				        			Node wordStart = charParamInOCR.getNextSibling().getNextSibling().getAttributes().getNamedItem("wordStart");
				        			if (wordStart != null){
				        				wordStart.setNodeValue("1");
				        			}
			        			}
			        		}
			        		//set wordStart=0
			        		else if (Character.isSpaceChar(ocrChar)){
			        			if (charParamInOCR.getNextSibling() != null && charParamInOCR.getNextSibling().getNextSibling() != null){
			        				logger.debug(charParamInOCR.getNextSibling().getNextSibling().getNodeName());
				        			Node wordStart = charParamInOCR.getNextSibling().getNextSibling().getAttributes().getNamedItem("wordStart");
				        			if (wordStart != null){
				        				wordStart.setNodeValue("0");
				        			}
			        			}
			        		}

			        		deleteAllObsoleteAtttributes(charParamInOCR);
			        		
			        		
			    		   //Element additionalCharParam = ocrDocument.createElement("charParams");
			        		
			        		//test if this is sufficient
			        		
			        		charParamInOCR.setTextContent(htrChar.toString());
 			        		
			    		   //charParamInOCR.getParentNode().replaceChild(charParamInOCR, charParamInOCR);
			        	}
			        	
						
						//nlCharParams.item(k).getParentNode().replaceChild(newChild, oldChild)
//						logger.debug("node name: " + nlCharParams.item(k).getNodeName());
//						if (nlCharParams.item(k).getNodeName().equals("charParams"))
//							logger.debug("charParams: " + nlCharParams.item(k).getTextContent());
//						if (k<htrLineString.length())
//							logger.debug("vs. HTR char " + htrLineString.charAt(k));
					}
					
					//case 1:
					int remaining = htrLineString.length()-ocrLineString.length();
					if (remaining > 0){
						//logger.debug("append node to line " + ocrLine.getTextContent());
						for (int j = ocrLineString.length(); j<htrLineString.length(); j++){
							
							/*
							 * calculate the left and right coordinates from lineWidth/numberOfHTRChars
							 * top and bottom are taken from line
							 */
							
		        			int l = (int) (lineLeft+newCharWidth*j);
		        			int r = (int) (lineLeft+newCharWidth*(j+1));
		        			
							Character htrChar = htrLineString.charAt(j);
							Element additionalCharParam = ocrDocument.createElement("charParams");
							additionalCharParam.setAttribute("l", Integer.toString(l));
							additionalCharParam.setAttribute("t", top);
							additionalCharParam.setAttribute("r", Integer.toString(r));
							additionalCharParam.setAttribute("b", bottom);
			    		   			    		   
							additionalCharParam.appendChild(ocrDocument.createTextNode(htrChar.toString()));
							
							Node node = formatting.appendChild(additionalCharParam);
//							logger.debug("node added " + node.getTextContent());
							//System.in.read();
						}
						
					}
				//}
				
//				for (char c : currLineString.toCharArray()){
//					
//				}
				//logger.debug("ocr line length: " + nodeListOCR.item(i).getTextContent());
				//logger.debug("child nodes of line: " + nodeListOCR.item(i).getFirstChild().getTextContent());
				//logger.debug("ocr line length: " + nodeListOCR.item(i).getTextContent().length());
			}
			
	       TransformerFactory tff  = TransformerFactory.newInstance();
	       Transformer transformer = tff.newTransformer();
	       	      
	       DOMSource xmlSource = new DOMSource(ocrDocument);
	       StreamResult outputTarget = new StreamResult(new File(ocrFile.getParentFile().getAbsolutePath() + File.separatorChar + htrFile.getName()));
	       
	       transformer.transform(xmlSource, outputTarget);
			
			logger.debug("Statistics of file " + htrFile.getAbsolutePath());
			logger.debug("countEqualLines: "+countEqualLines);
			logger.debug("countUnequalLines: " +countUnequalLines);
			logger.debug("countEqualLineLengths: " +countEqualLineLengths);
			logger.debug("countUnequalLinesLenths: " + countUnequalLinesLenths);
						
			
//			System.out.println("nodeListOCR length " + nodeListOCR.getLength());
//			for (int i = 0; i < nodeListOCR.getLength(); i++){
//				logger.debug("node value: " + nodeListOCR.item(i).getTextContent().length());
//			}
			

//		    Document doc1 = expandedData1.getOwnerDocument();
//		    // insert the nodes
//		    Node expandedData2 = (Node) xpath.evaluate("//expandedData", ocr, NODE);
//		    expandedData1.getParentNode()
//		        .replaceChild(doc1.adoptNode(expandedData2), expandedData1);
//		    // print results
//		    TransformerFactory.newInstance()
//		        .newTransformer()
//		        .transform(new DOMSource(doc1), new StreamResult(System.out));
	    
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 * experiment: combine OCR and HTR: the longest string wins
	 * seems to bring best results
	 * TODO: calculate error rates and compare!
	 */
	
	private static File combineHTRAndOCR(File htrFile, File ocrFile, File convertedFile) {
		try {
			
			FileInputStream fileIsHTR = new FileInputStream(htrFile);
			FileInputStream fileIsOCR = new FileInputStream(ocrFile);
				
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document htrDocument = builder.parse(fileIsHTR);
			Document ocrDocument = builder.parse(fileIsOCR);			
			
			XPath xPath = XPathFactory.newInstance().newXPath();
			String expressionHTR = "//TextLine/TextEquiv/Unicode";
			String expressionOCR = "//block[@blockType='Text']//line";
			String expressionOCRTables = "//block[@blockType='Table']//line";
			//xPath: get all charParams for current node
			String expAllCharParamsOfLine = ".//charParams";
			
			
			NodeList nodeListOCR = (NodeList) xPath.compile(expressionOCR).evaluate(ocrDocument, XPathConstants.NODESET);
			NodeList nodeListOCRTables = (NodeList) xPath.compile(expressionOCRTables).evaluate(ocrDocument, XPathConstants.NODESET);
			NodeList nodeListHTR = (NodeList) xPath.compile(expressionHTR).evaluate(htrDocument, XPathConstants.NODESET);
			

			
			int countEqualLines = 0;
			int countUnequalLines = 0;
			int countEqualLineLengths = 0;
			int countUnequalLinesLenths = 0;
			
//			String test = "test";
//			for (char c : test.toCharArray()){
//				System.out.println(c);
//				
//			}
			
			int tableNr = 0;
			//System.out.println("result is " + result);
			//		// and iterate on links
			//System.out.println("nodeList length " + nodeListHTR.getLength());
			for (int i = 0; i < nodeListHTR.getLength(); i++){
				
				String htrLineString = nodeListHTR.item(i).getTextContent();
				
//				NodeList formatting = nodeListOCR.item(i).getChildNodes();

//				for (int j = 0; j<formatting.getLength(); j++){
//					//NodeList charParams = formatting.item(j).getChildNodes();
//					
//					logger.debug("node " + formatting.item(j));
				
					Node ocrLine = (Node) nodeListOCR.item(i);
					Node formatting = null;
					if (ocrLine != null){
						//logger.debug("ocr line not null");
						formatting = ocrLine.getLastChild();
					}
					else{
						logger.debug("ocr line is null - try table lines");
						ocrLine = (Node) nodeListOCRTables.item(tableNr++);
					}
					
					if (ocrLine == null){
						logger.debug("ocr line is null");
						logger.debug("htr file name: " + htrFile.getName());
						logger.debug("ocr file name: " + ocrFile.getName());
					}
					//logger.debug(ocrLine.getLastChild().getNodeName());
					
					String left = "", right = "", top = "", bottom = "";
							
		        	NamedNodeMap ocrLineAttributes = ocrLine.getAttributes();
		        	for (int l = 0; l<ocrLineAttributes.getLength(); l++){
		        		Node currAttr = ocrLineAttributes.item(l);
		        		//logger.debug("node map name " + currAttr.getNodeName());
		        		switch (currAttr.getNodeName()){
			        		case "l": left=currAttr.getNodeValue();break;
			        		case "r": right=currAttr.getNodeValue();break;
			        		case "t": top=currAttr.getNodeValue();break;
			        		case "b": bottom=currAttr.getNodeValue();break;
			        		default: break;
		        		}
		        	}
		        	
		        	int lineLeft = Integer.valueOf(left);
		        	int lineRight = Integer.valueOf(right);
		        	int lineTop = Integer.valueOf(top);
		        	int lineBottom = Integer.valueOf(bottom);
				
					NodeList nlCharParams = (NodeList) xPath.compile(expAllCharParamsOfLine).evaluate(ocrLine, XPathConstants.NODESET);
//					logger.debug("ocr length: " + nlCharParams.getLength());
//					logger.debug("htr length: " + htrLineString.length());
									
					String ocrLineString = "";
					String penaltyString = "";
					boolean ocrIsWorse = false;
					for (int k = 0; k<nlCharParams.getLength(); k++){
						ocrLineString += nlCharParams.item(k).getTextContent();
//	        			Node penalty = nlCharParams.item(k).getAttributes().getNamedItem("wordPenalty");
//	        			Node charConf = nlCharParams.item(k).getAttributes().getNamedItem("charConfidence");
//	        			
//	        			if (penalty != null){
//	        				penaltyString = penalty.getTextContent();
//	        			}
//	        			
//	        			if (penalty != null && Integer.valueOf(penalty.getTextContent())>20){
//	        				logger.debug("word Penalty: " + penalty.getTextContent());
//	        				
//	        				ocrIsWorse = true;
//	        			}
//	        			if (charConf != null && charConf.getTextContent().equals("-1")){
//	        				logger.debug("char Confidence: " + charConf.getTextContent());
//	        				ocrIsWorse = true;
//	        			}
					}
					
//					logger.debug("htr line text: " + htrLineString);
//					logger.debug("ocr line text: " + ocrLineString);
					

					
					if (htrLineString.length() == ocrLineString.length()){
						countEqualLineLengths++;
					}
					else{
						countUnequalLinesLenths++;
					}
					
					if (htrLineString.equals(ocrLineString)){
//						logger.debug("ocr and htr are the same - keep charparams as they are!!");
						countEqualLines++;	
						continue;
					}
					
					countUnequalLines++;
					
					/*
					 * this HTR correction with better OCR does not bring better results for an already good HTR
					 */
					
					//if (ocrLineString.replaceAll("\\s","").length() > htrLineString.replaceAll("\\s","").length()){
//					if (ocrLineString.length() > htrLineString.length()
//							&& (ocrLineString.length() - htrLineString.length()>2)){
//						logger.debug("OCR String longer then HTR string - OCR could be better");
//						logger.debug("is ocr better? " + !ocrIsWorse);
//						//System.in.read();
//						//continue;
//					}
//					
//					if (ocrLineString.length() > htrLineString.length()
//							&& (ocrLineString.length() - htrLineString
//									.length()>2) && !ocrIsWorse){
//						logger.debug("OCR String longer then HTR string - OCR wins");
//						logger.debug("penaltyString " + penaltyString);
//						//System.in.read();
//						continue;
//					}
										
//					if(wordPenalty){
//						System.in.read();
//					}
					
					boolean differentLength = nlCharParams.getLength() != htrLineString.length();
					
					int nrOfChars = (htrLineString.length()>0 ? htrLineString.length() : ocrLineString.length());
					float newCharWidth = ( (Integer.valueOf(right)-Integer.valueOf(left))/nrOfChars);
					
//					if (ocrLineString.length() > htrLineString.length()){
//						System.in.read();
//					}
					
					for (int k = 0; k<nlCharParams.getLength(); k++){
						
						Node charParamInOCR = nlCharParams.item(k);
						
			        	Character ocrChar = nlCharParams.item(k).getTextContent().charAt(0);
			        	Character htrChar = k<htrLineString.length()? htrLineString.charAt(k) : null;

//			        	NamedNodeMap nodeMap = charParamInOCR.getAttributes();
//			        	for (int l = 0; l<nodeMap.getLength(); l++){
//			        		logger.debug("node map name " + nodeMap.item(l).getNodeName());
//			        		logger.debug("node map " + nodeMap.item(l).getNodeValue());
//			        	}
						
						/*
						 * cases to be considered
						 * (1) ocr line shorter than htr -> add charparams for all additional chars
						 * (2) htr line shorter than ocr -> delete needless chars
						 * (3) equal size -> only replace text content for unequal chars
						 * (4) empty char in htr text but not in ocr
						 */
			        	
			        	//case 2: pointless ocr charParams need to be deleted
			        	if(htrChar == null){
//			        		logger.debug("remove child: " + charParamInOCR.getTextContent());
			        		
			        		//text node of this element needs to be removed as well - otherwise you get empty lines in xml
			                Node prevElem = charParamInOCR.getPreviousSibling();
			                if (prevElem != null && 
			                    prevElem .getNodeType() == Node.TEXT_NODE &&
			                    prevElem .getNodeValue().trim().length() == 0) {
			                	charParamInOCR.getParentNode().removeChild(prevElem);
			                }
			        		//System.in.read();
			        		charParamInOCR.getParentNode().removeChild(charParamInOCR);
			        		
			        		continue;
			        	}
			        	
		        		//if we deal with different length of htr and ocr we newly calculate the coordinates of the bounding box of each character 
		        		if (differentLength){
		        			int l = (int) (lineLeft+newCharWidth*k);
		        			int r = (int) (lineLeft+newCharWidth*(k+1));
		        			
		        			Node leftCoord = charParamInOCR.getAttributes().getNamedItem("l");
		        			leftCoord.setNodeValue(Integer.toString(l));
		        			
		        			Node rightCoord = charParamInOCR.getAttributes().getNamedItem("r");
		        			rightCoord.setNodeValue(Integer.toString(r));
		        		}
			        	
			        	//if chars differ: replace ocr with htr (includes case 3, valid for case 1 and 2 too)
			        	if (ocrChar != htrChar){

			        		//empty space - insert and add wordStart = 1;
			        		if (Character.isSpaceChar(htrChar)){//.equals("\u0020")){
			        			if (charParamInOCR.getNextSibling() != null && charParamInOCR.getNextSibling().getNextSibling() != null){
				        			Node wordStart = charParamInOCR.getNextSibling().getNextSibling().getAttributes().getNamedItem("wordStart");
				        			if (wordStart != null){
				        				wordStart.setNodeValue("1");
				        			}
				        			else{
				        				Element currNode = (Element) charParamInOCR.getNextSibling().getNextSibling();
				        				currNode.setAttribute("wordStart", "1");
				        			}
				        			
			        			}
			        		}
			        		//set wordStart=0
			        		else if (Character.isSpaceChar(ocrChar)){
			        			if (charParamInOCR.getNextSibling() != null && charParamInOCR.getNextSibling().getNextSibling() != null){
			        				//logger.debug(charParamInOCR.getNextSibling().getNextSibling().getNodeName());
				        			Node wordStart = charParamInOCR.getNextSibling().getNextSibling().getAttributes().getNamedItem("wordStart");
				        			if (wordStart != null){
				        				wordStart.setNodeValue("0");
				        			}
				        			else{
				        				Element currNode = (Element) charParamInOCR.getNextSibling().getNextSibling();
				        				currNode.setAttribute("wordStart", "0");
				        			}
			        			}
			        		}

			        		deleteAllObsoleteAtttributes(charParamInOCR);
			        		
			        		
			    		   //Element additionalCharParam = ocrDocument.createElement("charParams");
			        		
			        		//test if this is sufficient
			        		
			        		charParamInOCR.setTextContent(htrChar.toString());
 			        		
			    		   //charParamInOCR.getParentNode().replaceChild(charParamInOCR, charParamInOCR);
			        	}
			        	
						
						//nlCharParams.item(k).getParentNode().replaceChild(newChild, oldChild)
//						logger.debug("node name: " + nlCharParams.item(k).getNodeName());
//						if (nlCharParams.item(k).getNodeName().equals("charParams"))
//							logger.debug("charParams: " + nlCharParams.item(k).getTextContent());
//						if (k<htrLineString.length())
//							logger.debug("vs. HTR char " + htrLineString.charAt(k));
					}
					
					//case 1:
					int remaining = htrLineString.length()-ocrLineString.length();
					if (remaining > 0){
						//logger.debug("append node to line " + ocrLine.getTextContent());
						for (int j = ocrLineString.length(); j<htrLineString.length(); j++){
							
							/*
							 * calculate the left and right coordinates from lineWidth/numberOfHTRChars
							 * top and bottom are taken from line
							 */
							
		        			int l = (int) (lineLeft+newCharWidth*j);
		        			int r = (int) (lineLeft+newCharWidth*(j+1));
		        			
							Character htrChar = htrLineString.charAt(j);
							Element additionalCharParam = ocrDocument.createElement("charParams");
							additionalCharParam.setAttribute("l", Integer.toString(l));
							additionalCharParam.setAttribute("t", top);
							additionalCharParam.setAttribute("r", Integer.toString(r));
							additionalCharParam.setAttribute("b", bottom);
			    		   			    		   
							additionalCharParam.appendChild(ocrDocument.createTextNode(htrChar.toString()));
							
							if (formatting != null){
								formatting.appendChild(additionalCharParam);
								//logger.debug("node added " + node.getTextContent());
								//System.in.read();
							}
							else{
								ocrLine.appendChild(additionalCharParam);
							}
						}
						
					}
				//}
				
//				for (char c : currLineString.toCharArray()){
//					
//				}
				//logger.debug("ocr line length: " + nodeListOCR.item(i).getTextContent());
				//logger.debug("child nodes of line: " + nodeListOCR.item(i).getFirstChild().getTextContent());
				//logger.debug("ocr line length: " + nodeListOCR.item(i).getTextContent().length());
			}
			
	       TransformerFactory tff  = TransformerFactory.newInstance();
	       Transformer transformer = tff.newTransformer();
	       	      
	       DOMSource xmlSource = new DOMSource(ocrDocument);
	       
	       //File convertedAbbyyXml = new File(htrFile.getParentFile().getParentFile().getAbsolutePath() + File.separatorChar +"ocr" + File.separatorChar + htrFile.getName());
	       StreamResult outputTarget = new StreamResult(convertedFile);
	       
	       transformer.transform(xmlSource, outputTarget);
			
			logger.debug("Statistics of file " + ocrFile.getAbsolutePath() );
			logger.debug("countEqualLines: "+countEqualLines);
			logger.debug("countUnequalLines: " +countUnequalLines);
			logger.debug("countEqualLineLengths: " +countEqualLineLengths);
			logger.debug("countUnequalLinesLenths: " + countUnequalLinesLenths);
			
			//System.in.read();
			
			return convertedFile;
						
			
//			System.out.println("nodeListOCR length " + nodeListOCR.getLength());
//			for (int i = 0; i < nodeListOCR.getLength(); i++){
//				logger.debug("node value: " + nodeListOCR.item(i).getTextContent().length());
//			}
			

//		    Document doc1 = expandedData1.getOwnerDocument();
//		    // insert the nodes
//		    Node expandedData2 = (Node) xpath.evaluate("//expandedData", ocr, NODE);
//		    expandedData1.getParentNode()
//		        .replaceChild(doc1.adoptNode(expandedData2), expandedData1);
//		    // print results
//		    TransformerFactory.newInstance()
//		        .newTransformer()
//		        .transform(new DOMSource(doc1), new StreamResult(System.out));
	    
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private static void deleteAllObsoleteAtttributes(Node charParamInOCR) throws IOException
 {
				
		//logger.debug("deleteAllObsoleteAtttributes " + charParamInOCR.getTextContent());
		
    	NamedNodeMap charParamAttributes = charParamInOCR.getAttributes();
    	
    	
    	ArrayList<String> attrName =new ArrayList<String>(); 
		/*
		 * copy the attribute names - charParamAttributes shrinks with removing the attributes
		 * and with the attribute names we can find and remove the attribute
		 */
    	for (int l = 0; l<charParamAttributes.getLength(); l++){
    		attrName.add(charParamAttributes.item(l).getNodeName());
    	}    	
    	
    	for (int l = 0; l<attrName.size(); l++){
    		
//    		logger.debug("l " + l);
//    		logger.debug("curr Attr: " + attrName.get(l));
    		
    		if (!attrName.get(l).matches("l|r|b|t|wordStart")){
    			//logger.debug("delete: " + charParamAttributes.getNamedItem(attrName.get(l)));
    			charParamAttributes.removeNamedItem(attrName.get(l));
    		}
    	}
    	//System.in.read();
	}
	
	private static void compareVersions() {
		 StringUtils.getLevenshteinDistance("fly", "ant");
		
	}


	/**
	 * Method will create a PAGE XML from the given source files at pageOutFile.
	 * if no supported source file exists (abbyy/alto/txt), then a skeleton will be created if possible.
	 * <br/><br/>
	 * This method must NEVER return null. Many mechanisms in Transkribus
	 * depend on this method reliably creating a file.
	 * 
	 * @param pageOutFile
	 * @param doOverwrite
	 * @param abbyyXml
	 * @param altoXml
	 * @param txtFile
	 * @param preserveOcrFontFamily
	 * @param preserveOcrTxtStyles
	 * @param replaceBadChars
	 * @param imgFile
	 * @param dim
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected static File createPageXml(File pageOutFile, boolean doOverwrite, File abbyyXml, boolean preserveOcrFontFamily, boolean preserveOcrTxtStyles, 
			boolean replaceBadChars, final String imgFileName, Dimension dim) 
					throws FileNotFoundException, IOException {
		if(pageOutFile == null) {
			throw new IllegalArgumentException("PAGE XML output File is null.");
		}
		if(pageOutFile.exists() && !doOverwrite) {
			throw new IOException("PAGE XML already exists at: " + pageOutFile.getAbsolutePath());
		}
		if(StringUtils.isEmpty(imgFileName)) {
			throw new IllegalArgumentException("Image filename must not be empty");
		}
		
		PcGtsType pc = null;
		
		if(abbyyXml != null){
			//try find Abbyy XML
			pc = createPageFromAbbyy(imgFileName, abbyyXml, preserveOcrTxtStyles, preserveOcrFontFamily, replaceBadChars);
		}
				
		//from here we need the dimension of the image
		if(dim == null) {
			//set (0,0) here in order to make the following work
			dim = new Dimension();
		}
				
		//if still null, there is no suitable file for this page yet => create one
		if (pc == null) {
			logger.warn("No Transcript XML found for img: " + FilenameUtils.getBaseName(imgFileName));
			logger.info("Creating empty PageXml.");
			pc = PageXmlUtils.createEmptyPcGtsType(imgFileName, dim);
		}
		
		//create the file
		try{
			JaxbUtils.marshalToFile(pc, pageOutFile);
		} catch (JAXBException je) {
			throw new IOException("Could not create PageXml on disk!", je);
		}
		
		return pageOutFile;
	}
	
	private static PcGtsType createPageFromAbbyy(final String imgFileName, File abbyyXml, boolean preserveOcrTxtStyles,
			boolean preserveOcrFontFamily, boolean replaceBadChars) throws IOException {
		try{
			XmlFormat xmlFormat = XmlUtils.getXmlFormat(abbyyXml);
			if(xmlFormat.equals(XmlFormat.ABBYY_10)){
				logger.info(abbyyXml.getAbsolutePath() + ": Transforming Finereader10/11 XML to PAGE XML.");
				PcGtsType pc = PageXmlUtils.createPcGtsTypeFromAbbyy(
						abbyyXml, imgFileName, 
						preserveOcrTxtStyles, preserveOcrFontFamily, 
						replaceBadChars
						);
				return pc;
			}
			throw new IOException("Not a valid Finereader10/11 XML file.");
		} catch(IOException | TransformerException ioe){
			logger.error(ioe.getMessage(), ioe);
			throw new IOException("Could not migrate file: " + abbyyXml.getAbsolutePath(), ioe);
		} catch (ParserConfigurationException | SAXException xmle) {
			logger.error(xmle.getMessage(), xmle);
			throw new IOException("Could not transform XML file!", xmle);
		} catch (JAXBException je) {
			/* TODO This exception is only thrown when the pageXML is unmarshalled 
			 * for inserting the image filename which is not included in the abbyy xml! */
			logger.error(je.getMessage(), je);
			throw new IOException("Transformation output is not a valid page XML!", je);
		}
	}

	private static void usage() {
		System.out.println("Use: java -jar jarFileName htrDirectoryName ocrDirectoryName\n"
				+ "folders contain htr and ocr results and must contain the same number of files!");
		return;		
	}
	
	public static void displayFiles(File[] files) {
		for (File file : files) {
			System.out.printf("File: %-20s Last Modified:" + "\n", file.getName());
		}
	}

}
