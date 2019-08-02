package eu.transkribus.core.model.beans.customtags;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.PageType;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.TagType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.util.PageXmlUtils;

public class ExtractTagsFromDataset {
	
	private static final Logger logger = LoggerFactory.getLogger(ExtractTagsFromDataset.class);
	
	private static void extractTags() throws JAXBException {
		
		final File folder = new File("/home/lateknight/Documents/dbis_seminar/Datensatz/4379/HS_115/page");
		File textByLine = new File("/home/lateknight/Desktop/TagsText.txt");
		
		FileOutputStream textLineos = null;
		try {
			textLineos = new FileOutputStream(textByLine);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedWriter textLinebw = new BufferedWriter(new OutputStreamWriter(textLineos));
		
		for(final File fileEntry : folder.listFiles()) {
			
			File testXML = new File(fileEntry.getAbsolutePath());
			
			
			PcGtsType pc = PageXmlUtils.unmarshal(testXML);
			PageType page = pc.getPage();
			File textFile = new File("/home/lateknight/Desktop/NER_data/text_"+fileEntry.getName()+".txt");
			File tagFile = new File("/home/lateknight/Desktop/NER_data/tags_"+fileEntry.getName()+".txt");
			
			FileOutputStream textos = null;
			try {
				textos = new FileOutputStream(textFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileOutputStream tagos = null;
			try {
				tagos = new FileOutputStream(tagFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			BufferedWriter textbw = new BufferedWriter(new OutputStreamWriter(textos));
			BufferedWriter tagbw = new BufferedWriter(new OutputStreamWriter(tagos));
		
			List<TrpRegionType> regions = page.getTextRegionOrImageRegionOrLineDrawingRegion();
			
			for(TrpRegionType r : regions){
				TrpPageType trpPage = r.getPage();
				List<TrpTextLineType> lines = trpPage.getLines();
				for(TrpTextLineType line : lines) {
					List<WordType> words = line.getWord();
					CustomTagList tagLines = line.getCustomTagList();
					List<CustomTag> tagList = tagLines.getIndexedTags();
					for(CustomTag tag : tagList) {
						logger.info("Tag Text "+tag.getContainedText()+" Tagged with "+tag.tagName);
						try {
							textLinebw.write(tag.getContainedText() +" "+tag.tagName);
							textLinebw.newLine();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
//					try {
//						textbw.write(line.getUnicodeText());
//						textbw.newLine();
//						textLinebw.write(line.getUnicodeText());
////						tagbw.write(tagLines.getCustomTag());
////						tagbw.newLine();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
				}
			
			}
			try {
				textbw.close();
				tagbw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
	
	}
	
	public static void main(String[] args) {
		try {
			extractTags();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
