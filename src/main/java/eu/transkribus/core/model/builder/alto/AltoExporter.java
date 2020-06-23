package eu.transkribus.core.model.builder.alto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.DocumentException;

import eu.transkribus.core.io.LocalDocConst;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.util.PageXmlUtils;
import eu.transkribus.core.util.XslTransformer;

public class AltoExporter extends Observable {
	private static final Logger logger = LoggerFactory.getLogger(AltoExporter.class);
	
	private static final String PAGE_TO_ALTO_WORD_LEVEL_XSLT = "xslt/PageToAltoWordLevel.xsl";
	//not used anymore
	@Deprecated
	private static final String PAGE_TO_ALTO_XSLT = "xslt/PageToAlto.xsl";
	
	public AltoExporter(){}
	
	public File createAltoOuputDir(String path) {
		if(path == null){
			throw new IllegalArgumentException("path is null!");
		}		
		File outputDir = new File(path);
		
		outputDir.mkdir();
		
		File altoOutputDir = new File(outputDir.getAbsolutePath() + File.separatorChar
					+ LocalDocConst.ALTO_FILE_SUB_FOLDER);
		
		if (altoOutputDir.mkdir()){
			logger.debug("altoOutputDir created successfully ");
		}
		else{
			logger.debug("altoOutputDir could not be created!");
		}

		return altoOutputDir;
		
	}
	
	public File exportAltoFile(TrpPage p, File altoOutputDir, boolean splitIntoWords, boolean useWordLayer) throws JAXBException, IOException {
		if(p == null){
			throw new IllegalArgumentException("TrpPage is null!");
		}
		String imgName = p.getImgFileName();
		int lastIndex = imgName.lastIndexOf(".");
		if (lastIndex == -1){
			lastIndex = imgName.length();
		}
		
		return exportAltoFile(p, imgName.substring(0,lastIndex)+".xml", altoOutputDir, splitIntoWords, useWordLayer);
	}
	
	public File exportAltoFile(TrpPage p, final String fileName, File altoOutputDir, boolean splitIntoWords, boolean useWordLayer) throws IOException {
		if(p == null || fileName == null){
			throw new IllegalArgumentException("An argument is null!");
		}
		
		TrpTranscriptMetadata t = p.getCurrentTranscript();
		
		InputStream pcIs = null;
		InputStream xslIS = null;
		File altoFile = new File(altoOutputDir.getAbsolutePath()+"/"+fileName);

		try (FileOutputStream fos = new FileOutputStream(altoFile)){
			PcGtsType pc = PageXmlUtils.unmarshal(t.getUrl());
			pcIs = new ByteArrayInputStream(PageXmlUtils.marshalToBytes(pc));
			StreamSource mySrc = new StreamSource(pcIs);
			
			/*
			 * TODO: use these parameters for controlling the Alto output: should it contain tags, should word layer be considered, split 
			 * splitIntoWords: line text is splitted into words
			 * word layer: use the word layer (in PAGE XML) for the export
			 */
			Map<String, Object> params = null;
			params = new HashMap<>();
			
			/*
			 *  if splitIntoWords we also export 'place' and 'person' tags
			 *  ToDo: add all possible tag types?
			 *  add it for 'export lines' as well
			 *  add it for word layer as well - will be more important when we have words as result of ATR
			 */
			params.put("includeTags", new Boolean(splitIntoWords));
			params.put("splitIntoWords", new Boolean(splitIntoWords));
			params.put("useWordLayer", new Boolean(useWordLayer));
		
			//we can use params in xsl to controll the output and use only one xsl for both scenarios (line-based or word based)
			InputStream is = XslTransformer.class.getClassLoader().getResourceAsStream(PAGE_TO_ALTO_WORD_LEVEL_XSLT);
			
			
//			if (splitIntoWords){
//				is = XslTransformer.class.getClassLoader().getResourceAsStream(PAGE_TO_ALTO_WORD_LEVEL_XSLT);
//			}
//			else{
//				is = XslTransformer.class.getClassLoader().getResourceAsStream(PAGE_TO_ALTO_XSLT);
//			}
	//		InputStream xslIS = new BufferedInputStream(new FileInputStream(xslID));
			xslIS = new BufferedInputStream(is);
			StreamSource xslSource = new StreamSource(xslIS);
	
			// Create a transform factory instance. specify saxon for XSLT 2.0 support
			TransformerFactory transFact = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
			Transformer trans = transFact.newTransformer(xslSource);
	        
	        logger.debug("new transformer created!");
	        
			if(params != null && !params.entrySet().isEmpty()){
				for(Entry<String, Object> e : params.entrySet()){
					trans.setParameter(e.getKey(), e.getValue());
				}
			}

			trans.transform(mySrc, new StreamResult(fos));
			
			return altoFile;
		} catch (JAXBException e) {
			throw new IOException("Could not read PAGE XML at: " + t.getUrl(), e);
		} catch (TransformerConfigurationException e){
			logger.error("error message!" + e.getMessageAndLocation());
			throw new IOException("Could not create transformer.", e);
		} catch (TransformerException e) {
			throw new IOException("Could not create ALTO file.", e);
		} finally {
			if(xslIS != null) {
				xslIS.close();
			}
			if(pcIs != null) {
				pcIs.close();
			}
		}
	}
			
	public void export(final TrpDoc doc, final String path) throws DocumentException, MalformedURLException, IOException, JAXBException, TransformerException {		
		File altoOutputDir = createAltoOuputDir(path);
		
		//TrpPdfDocument pdf = new TrpPdfDocument(pdfFile, useWordLevel);
		notifyObservers("Exporting Altos...");
		setChanged();
		
		for (int i = 0; i < doc.getPages().size(); i++) {

			logger.info("Processing page " + (i+1));
			notifyObservers(Integer.valueOf(i+1));
			setChanged();
			TrpPage p = doc.getPages().get(i);
			
			//3rd parameter says 'splitLineIntoWords'
			File altoFile = exportAltoFile(p, altoOutputDir, false, false);						
			//XslTransformer.transform(pc, PAGE_TO_ALTO_XSLT, pdfFile);
		}

		notifyObservers("Alto written at: " + path);
		setChanged();
		logger.info("ALTO files written at: " + path);
//		return outputDir;
	}
}
