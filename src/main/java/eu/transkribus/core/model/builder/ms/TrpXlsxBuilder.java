package eu.transkribus.core.model.builder.ms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.customtags.CustomTag;
import eu.transkribus.core.model.beans.customtags.CustomTagAttribute;
import eu.transkribus.core.model.beans.customtags.CustomTagList;
import eu.transkribus.core.model.beans.pagecontent.TextLineType;
import eu.transkribus.core.model.beans.pagecontent.WordType;
import eu.transkribus.core.model.beans.pagecontent_trp.ITrpShapeType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextLineType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTextRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpWordType;
import eu.transkribus.core.model.builder.ExportUtils;
import eu.transkribus.core.model.builder.TrpPageTranscriptBuilder;
import eu.transkribus.core.model.builder.rtf.TrpRtfBuilder;


public class TrpXlsxBuilder {
	private final static Logger logger = LoggerFactory.getLogger(TrpRtfBuilder.class);
	static Workbook wb;
	
	public TrpXlsxBuilder() {
		
	}
	
	private static void writeTagsForShapeElement(ITrpShapeType element, String context, String doc, String page, String regionID, String lineID, String wordId, Set<String> selectedTags) throws IOException{

		String textStr = element.getUnicodeText();
		CustomTagList cl = element.getCustomTagList();
		if (textStr == null || cl == null)
			throw new IOException("Element has no text or custom tag list: "+element+", class: "+element.getClass().getName());
			
		
		
		/*
		 * custom tags
		 * alle attribute auslesen und schreiben
		 * wenn 1. row: attribute keys schreiben und values schreiben
		 * wenn n. row: index von key in 1. row (=0) suchen und den value dort hineinschreiben
		 * 
		 */
		for (CustomTag nonIndexedTag : cl.getNonIndexedTags()) {
			
			if (!nonIndexedTag.getTagName().equals("textStyle") && !nonIndexedTag.getTagName().equals("readingOrder")){
				nonIndexedTag.getAttributesValuesMap();
				//logger.debug("nonindexed tag found " + nonIndexedTag.getTagName());
			}

		}
		
		
		for (CustomTag indexedTag : cl.getIndexedTags()) {

			if (!indexedTag.getTagName().equals("textStyle")){
				
				logger.debug("indexed tag found " + indexedTag.getTagName());
				
				Sheet firstSheet;
				Sheet currSheet;

				String tagname = indexedTag.getTagName();
				String overview = "Overview";
				
				if (!selectedTags.contains(tagname)){
					break;
				}
				
				/*
				 *first Excel page is the overview -> all tags without their special tag attributes
				 */
				if (wb.getSheet(overview) != null){
					firstSheet = wb.getSheet(overview);
				}
				else{
					firstSheet = wb.createSheet(WorkbookUtil.createSafeSheetName(overview));
				}
				
				//either find existent sheet or create new one
				if (wb.getSheet(tagname) != null){
					currSheet = wb.getSheet(tagname);
					logger.debug("existent sheet " + tagname);
				}
				else{
					currSheet = wb.createSheet(WorkbookUtil.createSafeSheetName(tagname));
					logger.debug("new sheet " + tagname);
				}
				
				CreationHelper crHelper = wb.getCreationHelper();
				Map<String, Object> attributes = indexedTag.getAttributeNamesValuesMap();
				Iterator<String> attributeIterator = attributes.keySet().iterator();
				
				int offset = (int) attributes.get("offset");
				int length = (int) attributes.get("length");
				
				logger.debug("text string " + textStr + " length " +textStr.length() + " offset " + offset + " length of substring " + length);
				String tmpTextStr = textStr.substring(offset, offset+length);
				
				int lastRowIdxOfFirstSheet = firstSheet.getLastRowNum();
				if (lastRowIdxOfFirstSheet == 0){
					fillFirstOverviewRow(firstSheet);
				}
				
				int lastRowIdx = currSheet.getLastRowNum();
				logger.debug("lastRowIdx " + lastRowIdx);
				if (lastRowIdx == 0){
					fillFirstRow(currSheet, attributes, crHelper);	
				}
				
				/*
				 * the first (overview) sheet shows all custom tags of the doc - tag attributes are stored as a list in one cell
				 */
				Row nextRowOfFirstSheet = firstSheet.createRow(++lastRowIdxOfFirstSheet);
				
				int idxHelper = 0;
				nextRowOfFirstSheet.createCell(idxHelper++).setCellValue(tmpTextStr);
				nextRowOfFirstSheet.createCell(idxHelper++).setCellValue(context);
				nextRowOfFirstSheet.createCell(idxHelper++).setCellValue(doc);
				nextRowOfFirstSheet.createCell(idxHelper++).setCellValue(page);
				nextRowOfFirstSheet.createCell(idxHelper++).setCellValue(regionID);
				nextRowOfFirstSheet.createCell(idxHelper++).setCellValue(lineID);
				nextRowOfFirstSheet.createCell(idxHelper++).setCellValue(wordId);
				
				//all attributes are s
				nextRowOfFirstSheet.createCell(idxHelper++).setCellValue(tagname + " " + attributes.toString());

				/*
				 * subsequent sheets shows all different tags on their own sheet
				 * 
				 */
				Row nextRow = currSheet.createRow(++lastRowIdx);
				
				int idx = 0;
				nextRow.createCell(idx++).setCellValue(tmpTextStr);
				nextRow.createCell(idx++).setCellValue(context);
				nextRow.createCell(idx++).setCellValue(doc);
				nextRow.createCell(idx++).setCellValue(page);
				nextRow.createCell(idx++).setCellValue(regionID);
				nextRow.createCell(idx++).setCellValue(lineID);
				nextRow.createCell(idx++).setCellValue(wordId);
				
//					for (int i = 0; i < attributes.size(); i++){
//						String attributeName = attributeIterator.next();
//						logger.debug("attributeName " + attributeName);
//						firstRow.createCell(i+idx).setCellValue(crHelper.createRichTextString(attributeName));
//						Object value = attributes.get(attributeName);		
//						logger.debug("attribute value " + value);
//						nextRow.createCell(i+idx).setCellValue(crHelper.createRichTextString(String.valueOf(value)));	
//					}
				
				/*
				 * each attribute of a custom tag is stored in a single cell
				 */
				Row row = currSheet.getRow(0);
				for (int i = 0; i < attributes.size(); i++){
					String attributeName = attributeIterator.next();
					Object value = attributes.get(attributeName);	
					for (int colIdx = 0; colIdx < row.getLastCellNum(); colIdx++){
						Cell cell = row.getCell(colIdx);

						if (cell.getRichStringCellValue().getString().equals(attributeName)){
							nextRow.createCell(colIdx).setCellValue(crHelper.createRichTextString(String.valueOf(value)));
							break;
						}
					}
				}

			}

		}

	}
	
	private static void fillFirstOverviewRow(Sheet firstSheet) {
		Row firstRow = firstSheet.createRow(0);
		
		int idx = 0;
		firstRow.createCell(idx++).setCellValue("Value");
		firstRow.createCell(idx++).setCellValue("Context");
		firstRow.createCell(idx++).setCellValue("Doc");
		firstRow.createCell(idx++).setCellValue("Page");
		firstRow.createCell(idx++).setCellValue("Region");
		firstRow.createCell(idx++).setCellValue("Line");
		firstRow.createCell(idx++).setCellValue("Word");
		firstRow.createCell(idx++).setCellValue("Tag");
				
	}

	private static void fillFirstRow(Sheet currSheet, Map<String, Object> attributes, CreationHelper crHelper) {
		Row firstRow = currSheet.createRow(0);
		
		int idx = 0;
		firstRow.createCell(idx++).setCellValue("Value");
		firstRow.createCell(idx++).setCellValue("Context");
		firstRow.createCell(idx++).setCellValue("Doc");
		firstRow.createCell(idx++).setCellValue("Page");
		firstRow.createCell(idx++).setCellValue("Region");
		firstRow.createCell(idx++).setCellValue("Line");
		firstRow.createCell(idx++).setCellValue("Word");
		
		Iterator<String> attributeIterator = attributes.keySet().iterator();
		for (int i = 0; i < attributes.size(); i++){
			String attributeName = attributeIterator.next();
			logger.debug("attributeName " + attributeName);
			firstRow.createCell(i+idx).setCellValue(crHelper.createRichTextString(attributeName)); 	
		}
		
	}

	public static void writeXlsxForDoc(TrpDoc doc, boolean wordBased, boolean writeTags, File exportFile, Set<Integer> pageIndices, IProgressMonitor monitor, Set<String> selectedTags) throws Exception {
			

		List<TrpPage> pages = doc.getPages();
		String exportPath = exportFile.getPath();
		
		int totalPages = pageIndices==null ? pages.size() : pageIndices.size();
		if (monitor!=null) {
			monitor.beginTask("Exporting to Excel", totalPages);
		}
				
		wb = new XSSFWorkbook();
		int c=0;
		for (int i=0; i<pages.size(); ++i) {
			if (pageIndices!=null && !pageIndices.contains(i))
				continue;
			
			if (monitor!=null) {
				if (monitor.isCanceled()) {
					logger.debug("Xlsx export cancelled!");
					return;
				}
				monitor.subTask("Processing page "+(c+1));
			}
			
			TrpPage page = pages.get(i);
			//try to get previously loaded JAXB transcript
			JAXBPageTranscript tr = ExportUtils.getPageTranscriptAtIndex(i);
			if (tr == null){
				TrpTranscriptMetadata md = page.getCurrentTranscript();
				tr = new JAXBPageTranscript(md);
				tr.build();
			}
			
			//old version
//			TrpPage page = pages.get(i);
//			TrpTranscriptMetadata md = page.getCurrentTranscript();
//			JAXBPageTranscript tr = new JAXBPageTranscript(md);
//			tr.build();
			
			
			TrpPageType trpPage = tr.getPage();
				
			
			logger.debug("writing xlsx for page "+(i+1)+"/"+doc.getNPages());
			
			List<TrpTextRegionType> textRegions = trpPage.getTextRegions(true);

			for (int j=0; j<textRegions.size(); ++j) {
				TrpTextRegionType r = textRegions.get(j);
								
				List<TextLineType> lines = r.getTextLine();
				
				for (int k=0; k<lines.size(); ++k) {
					TrpTextLineType trpL = (TrpTextLineType) lines.get(k);
					List<WordType> words = trpL.getWord();
					
					if (wordBased){
						for (int l=0; l<words.size(); ++l) {
							TrpWordType w = (TrpWordType) words.get(l);
							writeTagsForShapeElement(w, trpL.getUnicodeText(), String.valueOf(doc.getId()), String.valueOf(page.getPageNr()), r.getId(), trpL.getId(), w.getId(), selectedTags );
						}
					}
					else{
						writeTagsForShapeElement(trpL, trpL.getUnicodeText(), String.valueOf(doc.getId()), String.valueOf(page.getPageNr()), r.getId(), trpL.getId(), "", selectedTags );
					}

				}

			}
			


			++c;
			if (monitor!=null) {
				monitor.worked(c);
			}
		}
		
		/*
		 * auto size the columns
		 */
		for (int i = 0; i < wb.getNumberOfSheets(); i++){
            int numberOfCells = 0;
            Iterator rowIterator = wb.getSheetAt(i).rowIterator();
            /**
             * Escape the header row *
             */
            if (rowIterator.hasNext())
            {
                Row headerRow = (Row) rowIterator.next();
                //get the number of cells in the header row
                numberOfCells = headerRow.getPhysicalNumberOfCells();
                for (int j = 0; j<numberOfCells; j++){
                	wb.getSheetAt(i).autoSizeColumn(j);
                }
            }
		}

		FileOutputStream fOut;
		try {
			//means no tags at all
			if (wb.getNumberOfSheets() == 0){
				throw new IOException("Sorry - No tags available for export");
			}
			fOut = new FileOutputStream(exportPath);
			wb.write(fOut);
			fOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		logger.info("wrote xlsx to: "+ exportPath);
	}
	
	private static void ExcelTest(String Path) {
		Workbook wb = new XSSFWorkbook();
		Sheet employees = wb.createSheet(WorkbookUtil.createSafeSheetName("Mitarbeiter"));
		
		wb.getNumberOfSheets();
		wb.getSheet("test").getLastRowNum();
		
		Sheet s2 = wb.createSheet(WorkbookUtil.createSafeSheetName("Schorsch"));

		CreationHelper crHelper = wb.getCreationHelper();
		Row firstRow = employees.createRow(0);
		firstRow.createCell(0).setCellValue(crHelper.createRichTextString("Vorname"));
		firstRow.createCell(1).setCellValue(crHelper.createRichTextString("Nachname"));
		firstRow.createCell(2).setCellValue(crHelper.createRichTextString("Geburtsdatum"));

		Row secondRow = employees.createRow(1);
		secondRow.createCell(0).setCellValue(crHelper.createRichTextString("Santa"));
		secondRow.createCell(1).setCellValue(crHelper.createRichTextString("Claus"));
		secondRow.createCell(2).setCellValue(crHelper.createDataFormat().getFormat("1823-12-23"));

		Row thirdRow = employees.createRow(2);
		thirdRow.createCell(0).setCellValue(crHelper.createRichTextString("Oster"));
		thirdRow.createCell(1).setCellValue(crHelper.createRichTextString("Hase"));
		thirdRow.createCell(2).setCellValue(crHelper.createDataFormat().getFormat("1682-01-01"));

		CellStyle formatTableHead = wb.createCellStyle();
		formatTableHead.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
		formatTableHead.setFillPattern(CellStyle.SOLID_FOREGROUND);

		Font fontTableHead = wb.createFont();
		fontTableHead.setColor(IndexedColors.WHITE.getIndex());
		formatTableHead.setFont(fontTableHead);

		firstRow.getCell(0).setCellStyle(formatTableHead);
		firstRow.getCell(1).setCellStyle(formatTableHead);
		firstRow.getCell(2).setCellStyle(formatTableHead);

		CellStyle formatGebDate  = wb.createCellStyle();
		formatGebDate.setDataFormat(crHelper.createDataFormat().getFormat("dd.mm.yy"));

		secondRow.getCell(2).setCellStyle(formatGebDate);
		thirdRow.getCell(2).setCellStyle(formatGebDate);

		employees.autoSizeColumn(0);
		employees.autoSizeColumn(1);
		employees.autoSizeColumn(2);

		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(Path);
			wb.write(fOut);
			fOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) throws Exception {
		
		
		TrpDoc doc = LocalDocReader.load("C:/Users/Administrator/OCR_Sample_Document_-_Gothic_letter/");
		TrpXlsxBuilder txslx = new TrpXlsxBuilder();
		writeXlsxForDoc(doc, true, false, new File("C:/Users/Administrator/test.xlsx"), null, null, null);
		//ExcelTest("C:/Users/Administrator/test.xlsx");
		System.out.println("finished");
	}


}
