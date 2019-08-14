package eu.transkribus.core.model.builder.ms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.record.cf.CellRangeUtil;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellRange;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twelvemonkeys.io.FileUtil;

import eu.transkribus.core.io.DocExporter;
import eu.transkribus.core.io.LocalDocReader;
import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTableCellType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTableRegionType;
import eu.transkribus.core.model.builder.ExportCache;
import eu.transkribus.core.model.builder.NoTablesException;



public class TrpXlsxTableBuilder {
	private final static Logger logger = LoggerFactory.getLogger(TrpXlsxTableBuilder.class);
	Workbook wb;
	
	public TrpXlsxTableBuilder() {
		
	}
	
	public void writeXlsxForTables(TrpDoc doc, File exportFile, Set<Integer> pageIndices, IProgressMonitor monitor, ExportCache cache) throws NoTablesException, IOException, InterruptedException {
		//TrpTableRegionType is contained in the regions too

		List<TrpPage> pages = doc.getPages();
		String exportPath = exportFile.getPath();
		
		int totalPages = pageIndices==null ? pages.size() : pageIndices.size();
		if (monitor!=null) {
			monitor.beginTask("Exporting tables to Excel", totalPages);
		}
				
		wb = new XSSFWorkbook();
		int c=0;
		int tableId = 0;
		for (int i=0; i<pages.size(); ++i) {
			if (pageIndices!=null && !pageIndices.contains(i))
				continue;
			
			if (monitor!=null) {
				if (monitor.isCanceled()) {
					throw new InterruptedException("Export was canceled by user");
//					logger.debug("Xlsx export cancelled!");
//					return;
				}
				monitor.subTask("Processing page "+(c+1));
			}
			
			TrpPage page = pages.get(i);
			//try to get previously loaded JAXB transcript
			JAXBPageTranscript tr = null;
			if(cache != null) {
				tr = cache.getPageTranscriptAtIndex(i);
			}
			if (tr == null){
				TrpTranscriptMetadata md = page.getCurrentTranscript();
				tr = new JAXBPageTranscript(md);
				tr.build();
			}			
			
			TrpPageType trpPage = tr.getPage();
			
			List<TrpRegionType> regions = trpPage.getRegions();
						
			for (int j=0; j<regions.size(); ++j) {
				TrpRegionType r = regions.get(j);
				
				if (r instanceof TrpTableRegionType){
					tableId++;
					logger.debug("is table");
					TrpTableRegionType table = (TrpTableRegionType) r;
					
					int cols = table.getNCols();
					int rows = table.getNRows();
															
					List<List<TrpTableCellType>> allRowCells = new ArrayList<List<TrpTableCellType>>();
					for (int k = 0; k<rows; k++){
						allRowCells.add(table.getRowCells(k));
					}
					
		            List<HashMap<Integer, TrpTableCellType>> allRows = new ArrayList<HashMap<Integer, TrpTableCellType>>();
		            		            
		            for (List<TrpTableCellType> rowCells : allRowCells){				          
		            	allRows.add(new HashMap<Integer, TrpTableCellType>());
		            }
		           
		            int rowIdx = 0;
		            for (List<TrpTableCellType> rowCells : allRowCells){
		            
		            	HashMap<Integer, TrpTableCellType> currRowMap = allRows.get(rowIdx);

		            	/*
		            	 * fill up all cells which are not set in TRP because they were merged there (needed for vertical and horizontal xslx cell merge)
		            	 */	            	
		            	for (TrpTableCellType cell : rowCells){
			            	//logger.debug("table cell text " + cell.getUnicodeTextFromLines());
		            		//logger.debug("curr row, add cell.getCol() " + cell.getCol() + " whats in the cell: " + cell.getRow());
			            	currRowMap.put(cell.getCol(), cell);

			            	if (cell.getRowSpan() > 1){
			            		for (int k = 1; k<cell.getRowSpan(); k++){
			            			HashMap<Integer, TrpTableCellType> tmpRowMap = allRows.get(rowIdx+k);
			            			tmpRowMap.put(cell.getCol(), null);
			            			allRows.remove(rowIdx+k);
			            			allRows.add(rowIdx+k, tmpRowMap);
			            		}
			            	}
			            	if (cell.getColSpan() > 1){
			            		for (int k = 1; k<cell.getColSpan(); k++){
			            			currRowMap.put(cell.getCol()+k, null);
			            		}
			            	}
		            	}
		            	allRows.remove(rowIdx);
		            	allRows.add(rowIdx, currRowMap);
		            	rowIdx++;
		            }

		            createTable(rows, cols, allRows, tableId);

				}
				
		
				logger.debug("writing xlsx for page "+(i+1)+"/"+doc.getNPages());
	
				
				if (monitor!=null) {
					monitor.worked(c);
				}
			}
			++c;
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
                	wb.getSheetAt(i).autoSizeColumn(j, true);
                }
                              
            }
                        
		}

		FileOutputStream fOut;
		try {
			//means no tables at all
			if (wb.getNumberOfSheets() == 0){
				//throw new NoTablesException("Sorry - No tables available for export");
				logger.info("Sorry - No tables available for export");
				Sheet noTables = wb.createSheet(WorkbookUtil.createSafeSheetName("No tables found"));
				CreationHelper crHelper = wb.getCreationHelper();
				Row firstRow = noTables.createRow(0);
				firstRow.createCell(0).setCellValue(crHelper.createRichTextString("Sorry - there were no tables available for your export. Please check the transcripts!"));
			}
			fOut = new FileOutputStream(exportPath);
			wb.write(fOut);
			fOut.close();
		} catch (IOException e) {
			if (!(e instanceof NoTablesException)) {
				logger.error(e.getMessage(), e);
			}
			throw e;
		}
		logger.info("wrote xlsx to: "+ exportPath);
	}
	
	private void createTable(int rows, int cols, List<HashMap<Integer, TrpTableCellType>> allRows, int tableID) {

		String tableName = "table_"+tableID;
		Sheet currSheet = wb.createSheet(WorkbookUtil.createSafeSheetName(tableName));
		
		CellStyle style=wb.createCellStyle();
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setWrapText(true);
		
		CellStyle rowStyle = (CellStyle) wb.createCellStyle();
		rowStyle.setWrapText(true);	
        		
	    int i = 0;
	    int colIdtmp = 0;

	    for (HashMap<Integer, TrpTableCellType> entry : allRows) {
	        	        
	        if (entry.keySet().size() != cols){
	        	logger.debug("size of entries does not match columns ");
	        }

	    	Row nextRow = currSheet.createRow(i);
	    	nextRow.setRowStyle(rowStyle);
	    	
	    	int maxLines = 0;
	    	
	    	i++;
	        
	        for (Integer key : entry.keySet()) {
	        	        	
	        	int colSpan = 0;
	        	int rowSpan = 0;
	        	boolean mergedVertical = false;
	        	boolean mergedHorizontal = false;
	        		        	
	        	if (entry.get(key) != null){
	        		
	        		colSpan = entry.get(key).getColSpan();
	        		rowSpan = entry.get(key).getRowSpan();
	        		
		        	if (rowSpan > 1){
		        		mergedVertical = true;
		        	}

		        	if (colSpan > 1){
		        		mergedHorizontal = true;
		        	}

		        			        	
		        	int colID = entry.get(key).getCol();
		        	int rowID = entry.get(key).getRow();
		        	
		        	String text = entry.get(key).getUnicodeTextFromLines();
		        	
		        	Cell currCell = nextRow.createCell(colID);
		        	currCell.setCellValue(text);   
		        	currCell.setCellStyle(style);
		        	
	        		if(mergedVertical || mergedHorizontal){
//	        			logger.debug("entry.get(key).getUnicodeTextFromLines() " + entry.get(key).getUnicodeTextFromLines());
//	        			logger.debug(" row ID " + rowID + " rowSpan " + rowSpan + " merged H "+ mergedHorizontal + " colID " + colID + " colSpan " + colSpan + " merged V " + mergedVertical);
	        			
	        			String [] textlines = text.split(System.lineSeparator());
	        			
	        			if (textlines.length > 0){
		        			int length = textlines.length;
		        			if (maxLines < length){
		        				maxLines = length;
		        			}
		        			
	        			}
	        			currSheet.addMergedRegion(new CellRangeAddress(rowID,rowID+rowSpan-1,colID,colID+colSpan-1));
	        		}

	        	}
	        	else{
		        	Cell currCell = nextRow.createCell(key);
		        	currCell.setCellStyle(style);
	        	}
	        }
	        
	        /*
	         * set the height of a row manually since it does not work for merged region cells!!!
	         * so if there are merged cells in a row we remember the maximum number of text lines in one of these cells to set the appropriate height
	         */
			logger.debug("row contains maximum nr.- of lines in cell of: "  + maxLines);
			if (maxLines > 0){
				nextRow.setHeight((short) (nextRow.getHeight()*maxLines));
			}
			
	    }
	}
	
	public static void main(String[] args) throws Exception {
		
		convertXslxIntoCsv("Y:/DIG_auftraege_archiv/tmp/StazH/match_xslx");
		
		
//		TrpDoc docWithoutTables = LocalDocReader.load("C:/01_Projekte/READ/Projekte/export_job_377290/41768/171/");
//		//TrpDoc docWithTables = LocalDocReader.load("C:/Neuer Ordner/TRAINING_TESTSET_NAF_Poll_Tax_M_5/TRAINING_TESTSET_NAF_Poll_Tax_M_5/");
//		
//		Set<Integer> pageIndices = null; // null means every page
//		
//		//pageIndices must be set here instead of being null because it gets used in ExportUtils
//		if (pageIndices == null){
//			pageIndices = new HashSet<Integer>();
//			for (int i = 0; i<docWithoutTables.getNPages(); i++){
//				pageIndices.add(i);
//			}
//		}
//		DocExporter docExporter =  new DocExporter();
//		docExporter.getCache().storePageTranscripts4Export(docWithoutTables, pageIndices, null, "Latest", -1, null);
//		
//		TrpXlsxTableBuilder txslx = new TrpXlsxTableBuilder();
//		txslx.writeXlsxForTables(docWithoutTables, new File("C:/01_Projekte/READ/Projekte/testWithoutTables.xlsx"), pageIndices, null, docExporter.getCache());
//		System.out.println("finished");
	}

	/*
	 * this was needed for converting xlsx files (page matching from Digitexx) into csv files for t2i matching of the StazH collection
	 */
	private static void convertXslxIntoCsv(String startDir) throws InvalidFormatException, IOException {
		
		
		File startDirectory = new File(startDir);
		String outputDir = "Y:/DIG_auftraege_archiv/tmp/StazH/match/";

		for (File file : startDirectory.listFiles()){
			if (file.isDirectory()){
				logger.debug("dir: " + file.getAbsolutePath());
				convertXslxIntoCsv(file.getAbsolutePath());
			}
			else{
				File csvFolder = new File(outputDir + file.getParentFile().getName());
				csvFolder.mkdir();
				
				String csvName = csvFolder.getAbsolutePath() + "/" + FileUtil.getBasename(file) + ".csv";
				
				if (new File(csvName).exists()){
					continue;
				}
				
				Workbook wb = new XSSFWorkbook(file);

				DataFormatter formatter = new DataFormatter();

				PrintStream out = new PrintStream(new FileOutputStream(csvName), true, "UTF-8");
				
				byte[] bom = {(byte)0xEF, (byte)0xBB, (byte)0xBF};

				out.write(bom);

				for (Sheet sheet : wb) {

				    for (Row row : sheet) {

				        boolean firstCell = true;

				        for (Cell cell : row) {

				            if ( ! firstCell ) out.print(',');

				            String text = formatter.formatCellValue(cell);

				            out.print(text);

				            firstCell = false;

				        }

				        out.println();

				    }

				}
			}
		}
			

		
	}
	



}
