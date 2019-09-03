package eu.transkribus.core.model.beans.customtags;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.PageType;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.model.beans.pagecontent.TagType;
import eu.transkribus.core.model.beans.pagecontent.TextRegionType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpWordType;
import eu.transkribus.core.util.PageXmlUtils;


public class ExtractTagsFromDataset {
	
	private static final Logger logger = LoggerFactory.getLogger(ExtractTagsFromDataset.class);
	
	private final int docId = 5986;
	
	private static void extractTags() throws JAXBException {
		
		final File folder = new File("/home/lateknight/Documents/Master_Thesis/datensatz/151202/ONB_nfp_18950706_duplicated/page");	
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
			
			TrpPageType t = (TrpPageType)pc.getPage();
			
			List<TrpTextLineType> lines = t.getLines();
			
			
				for(TrpTextLineType line : lines) {
					
					CustomTagList tagLines = line.getCustomTagList();
					List<CustomTag> tagList = tagLines.getIndexedTags();

					String lineText = line.getUnicodeText();
					HashMap<String, CustomTag> tagMap = new HashMap<String, CustomTag>();
					
					for(CustomTag tag : tagList) {
						tagMap.put(tag.getContainedText(), tag);
					}
	
						try {
							StringTokenizer st = new StringTokenizer(lineText);
							while(st.hasMoreTokens()) {
								String token = st.nextToken();
								textLinebw.write(token);
								token = token.replace(",", "").replace(".", "").replace(";", "");
	
								boolean entityWritten= false;
								CustomTag tag = tagMap.get(token);
								// Handle continued tags
								if(tagMap.containsKey(token)) {
									if(tag.getTagName().equals("person")) {
										if(tag.isContinued() && tag.getOffset() == 0) {
											textLinebw.write("\t I-PER");
										}else {
											textLinebw.write("\t B-PER");
										}
										
										entityWritten = true;
									}else if(tag.getTagName().equals("place")) {
										if(tag.isContinued() && tag.getOffset() == 0) {
											textLinebw.write("\t I-LOC");
										}else {
											textLinebw.write("\t B-LOC");
										}
										entityWritten = true;
									}else if(tag.getTagName().equals("organization")) {
										if(tag.isContinued() && tag.getOffset() == 0) {
											textLinebw.write("\t I-ORG");
										}else {
											textLinebw.write("\t B-ORG");
										}
										entityWritten = true;
									}
								}else {
									for(Map.Entry<String, CustomTag> entry : tagMap.entrySet()) {
										CustomTag temp = entry.getValue();
										if(entry.getKey().startsWith(token)) {
											if(temp.getTagName().equals("person")) {
												textLinebw.write("\t B-PER");
												entityWritten = true;
											}else if(temp.getTagName().equals("place")) {
												textLinebw.write("\t B-LOC");
												entityWritten = true;
											}else if(temp.getTagName().equals("organization")) {
												textLinebw.write("\t B-ORG");
												entityWritten = true;
											}
										}
										Set<String> tokenSplit = new HashSet<String>(
												Arrays.asList(entry.getKey().split(" "))
												);
										if(!entityWritten && (tokenSplit.contains(token) || entry.getKey().endsWith(token))) {
											if(temp.getTagName().equals("person")) {
												textLinebw.write("\t I-PER");
												entityWritten = true;
											}else if(temp.getTagName().equals("place")) {
												textLinebw.write("\t I-LOC");
												entityWritten = true;
											}else if(temp.getTagName().equals("organization")) {
												textLinebw.write("\t I-ORG");
												entityWritten = true;
											}
										}
									}
								}
								if(!entityWritten) {
									textLinebw.write("\t O");
								}
							
								textLinebw.newLine();
							
							

							}							
						} catch (IOException e) {
							e.printStackTrace();
						}
					
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
