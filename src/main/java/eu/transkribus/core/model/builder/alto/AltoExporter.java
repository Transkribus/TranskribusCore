package eu.transkribus.core.model.builder.alto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Observable;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
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
	
	public File exportAltoFile(TrpPage p, File altoOutputDir, boolean splitIntoWords) throws JAXBException, IOException {
		if(p == null){
			throw new IllegalArgumentException("TrpPage is null!");
		}
		String imgName = p.getImgFileName();
		int lastIndex = imgName.lastIndexOf(".");
		if (lastIndex == -1){
			lastIndex = imgName.length();
		}
		
		return exportAltoFile(p, imgName.substring(0,lastIndex)+".xml", altoOutputDir, splitIntoWords);
	}
	
	public File exportAltoFile(TrpPage p, final String fileName, File altoOutputDir, boolean splitIntoWords) throws IOException {
		if(p == null || fileName == null){
			throw new IllegalArgumentException("An argument is null!");
		}
		
		TrpTranscriptMetadata t = p.getCurrentTranscript();
		
		InputStream pcIs = null;
		StreamSource mySrc = new StreamSource();
		try {
			PcGtsType pc = PageXmlUtils.unmarshal(t.getUrl());
			pcIs = new ByteArrayInputStream(PageXmlUtils.marshalToBytes(pc));
			mySrc.setInputStream(pcIs);
		} catch (JAXBException e) {
			throw new IOException("Could not read PAGE XML at: " + t.getUrl(), e);
		} finally {
			if(pcIs != null) {
				pcIs.close();
			}
		}
		
		InputStream is;
		if (splitIntoWords){
			is = XslTransformer.class.getClassLoader().getResourceAsStream(PAGE_TO_ALTO_WORD_LEVEL_XSLT);
		}
		else{
			is = XslTransformer.class.getClassLoader().getResourceAsStream(PAGE_TO_ALTO_XSLT);
		}
//		InputStream xslIS = new BufferedInputStream(new FileInputStream(xslID));
		InputStream xslIS = new BufferedInputStream(is);
		StreamSource xslSource = new StreamSource(xslIS);

        // das Factory-Pattern unterstützt verschiedene XSLT-Prozessoren
        TransformerFactory transFact =
                TransformerFactory.newInstance();
        Transformer trans;
		try {
			trans = transFact.newTransformer(xslSource);
			
			File altoFile = new File(altoOutputDir.getAbsolutePath()+"/"+fileName);			
			trans.transform(mySrc, new StreamResult(new FileOutputStream(altoFile)));
			
			return altoFile;
		} catch (TransformerException e) {
			throw new IOException("Could not create ALTO file.", e);
		} finally {
			if(xslIS != null) {
				xslIS.close();
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
			File altoFile = exportAltoFile(p, altoOutputDir, false);						
			//XslTransformer.transform(pc, PAGE_TO_ALTO_XSLT, pdfFile);
		}

		notifyObservers("Alto written at: " + path);
		setChanged();
		logger.info("ALTO files written at: " + path);
//		return outputDir;
	}
}
