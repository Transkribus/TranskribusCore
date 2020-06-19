package eu.transkribus.core.model.builder.txt;

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

public class TxtExporter extends Observable {
	private static final Logger logger = LoggerFactory.getLogger(TxtExporter.class);
	
	private static final String PAGE_TO_TXT_XSLT = "xslt/PageToTextfile.xsl";
	
	public TxtExporter(){}
	
	public File createTxtOuputDir(String path) {
		if(path == null){
			throw new IllegalArgumentException("path is null!");
		}		
		File outputDir = new File(path);
		
		outputDir.mkdir();
		
		File txtOutputDir = new File(outputDir.getAbsolutePath() + File.separatorChar
					+ LocalDocConst.TXT_FILE_SUB_FOLDER);
		
		if (txtOutputDir.mkdir()){
			logger.debug("txtOutputDir created successfully ");
		}
		else{
			logger.debug("txtOutputDir could not be created!");
		}

		return txtOutputDir;
		
	}
	
	public File exportTxtFile(TrpPage p, File txtOutputDir) throws JAXBException, IOException {
		if(p == null){
			throw new IllegalArgumentException("TrpPage is null!");
		}
		String imgName = p.getImgFileName();
		int lastIndex = imgName.lastIndexOf(".");
		if (lastIndex == -1){
			lastIndex = imgName.length();
		}
		
		return exportTxtFile(p, imgName.substring(0,lastIndex)+".txt", txtOutputDir);
	}
	
	public File exportTxtFile(TrpPage p, final String fileName, File txtOutputDir) throws IOException {
		if(p == null || fileName == null){
			throw new IllegalArgumentException("An argument is null!");
		}
		
		TrpTranscriptMetadata t = p.getCurrentTranscript();
		
		InputStream pcIs = null;
		InputStream xslIS = null;
		File txtFile = new File(txtOutputDir.getAbsolutePath()+"/"+fileName);

		try (FileOutputStream fos = new FileOutputStream(txtFile)){
			PcGtsType pc = PageXmlUtils.unmarshal(t.getUrl());
			pcIs = new ByteArrayInputStream(PageXmlUtils.marshalToBytes(pc));
			StreamSource mySrc = new StreamSource(pcIs);

			InputStream is = XslTransformer.class.getClassLoader().getResourceAsStream(PAGE_TO_TXT_XSLT);

			xslIS = new BufferedInputStream(is);
			StreamSource xslSource = new StreamSource(xslIS);
	
	        // das Factory-Pattern unterstützt verschiedene XSLT-Prozessoren
	        TransformerFactory transFact = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
	        Transformer trans = transFact.newTransformer(xslSource);
			trans.transform(mySrc, new StreamResult(fos));
			
			return txtFile;
		} catch (JAXBException e) {
			throw new IOException("Could not read PAGE XML at: " + t.getUrl(), e);
		} catch (TransformerException e) {
			throw new IOException("Could not create TXT file.", e);
		} finally {
			if(xslIS != null) {
				xslIS.close();
			}
			if(pcIs != null) {
				pcIs.close();
			}
		}
	}
			
}
