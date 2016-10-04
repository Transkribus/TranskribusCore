package eu.transkribus.core.model.builder.alto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
	
	private static final String PAGE_TO_ALTO_XSLT = "xslt/PageToAltoWordLevel.xsl";
	
	public AltoExporter(){}
	
	public File createAltoOuputDir(TrpDoc doc, String path) {
		if(doc == null) {
			throw new IllegalArgumentException("TrpDoc is null!");
		}
		if(path == null){
			throw new IllegalArgumentException("path is null!");
		}		
		File outputDir = new File(path);
		
		outputDir.mkdir();
		
		File altoOutputDir = new File(outputDir.getAbsolutePath() + File.separatorChar
					+ LocalDocConst.ALTO_FILE_SUB_FOLDER);
		altoOutputDir.mkdir();
		
		return altoOutputDir;
		
	}
	
	public File exportAltoFile(TrpPage p, File altoOutputDir) throws JAXBException, FileNotFoundException, TransformerException {
		if(p == null){
			throw new IllegalArgumentException("TrpPage is null!");
		}
		String imgName = p.getImgFileName();
		int lastIndex = imgName.lastIndexOf(".");
		if (lastIndex == -1){
			lastIndex = imgName.length();
		}
		
		return exportAltoFile(p, imgName.substring(0,lastIndex)+".xml", altoOutputDir);
	}
	
	public File exportAltoFile(TrpPage p, final String fileName, File altoOutputDir) throws JAXBException, FileNotFoundException, TransformerException {
		if(p == null || fileName == null){
			throw new IllegalArgumentException("An argument is null!");
		}
		
		TrpTranscriptMetadata t = p.getCurrentTranscript();
		PcGtsType pc = PageXmlUtils.unmarshal(t.getUrl());
		
		StreamSource mySrc = new StreamSource();
		mySrc.setInputStream(new ByteArrayInputStream(PageXmlUtils.marshalToBytes(pc)));
		
		InputStream is = XslTransformer.class.getClassLoader().getResourceAsStream(PAGE_TO_ALTO_XSLT);
//		InputStream xslIS = new BufferedInputStream(new FileInputStream(xslID));
		InputStream xslIS = new BufferedInputStream(is);
		StreamSource xslSource = new StreamSource(xslIS);

        // das Factory-Pattern unterstützt verschiedene XSLT-Prozessoren
        TransformerFactory transFact =
                TransformerFactory.newInstance();
        Transformer trans;
//		try {
			trans = transFact.newTransformer(xslSource);
			
			File altoFile = new File(altoOutputDir.getAbsolutePath()+"/"+fileName);			
			trans.transform(mySrc, new StreamResult(new FileOutputStream(altoFile)));
			
			return altoFile;
//		} catch (TransformerConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (TransformerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
			
	public void export(final TrpDoc doc, final String path) throws DocumentException, MalformedURLException, IOException, JAXBException, TransformerException {		
		File altoOutputDir = createAltoOuputDir(doc, path);
		
		//TrpPdfDocument pdf = new TrpPdfDocument(pdfFile, useWordLevel);
		notifyObservers("Exporting Altos...");
		setChanged();
		
		for (int i = 0; i < doc.getPages().size(); i++) {

			logger.info("Processing page " + (i+1));
			notifyObservers(Integer.valueOf(i+1));
			setChanged();
			TrpPage p = doc.getPages().get(i);
			
			File altoFile = exportAltoFile(p, altoOutputDir);						
			//XslTransformer.transform(pc, PAGE_TO_ALTO_XSLT, pdfFile);
		}

		notifyObservers("Alto written at: " + path);
		setChanged();
		logger.info("ALTO files written at: " + path);
//		return outputDir;
	}
}
