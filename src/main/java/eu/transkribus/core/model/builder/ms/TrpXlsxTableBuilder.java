package eu.transkribus.core.model.builder.ms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.JAXBPageTranscript;
import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpRegionType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTableCellType;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpTableRegionType;
import eu.transkribus.core.model.builder.ExportUtils;



public class TrpXlsxTableBuilder {
	private final static Logger logger = LoggerFactory.getLogger(TrpXlsxTableBuilder.class);
	static Workbook wb;
	
	public TrpXlsxTableBuilder() {
		
	}
	
	public static void writeXlsxForTables(TrpDoc doc, boolean wordBased, File exportFile, Set<Integer> pageIndices, IProgressMonitor monitor) throws Exception {
		
		//TrpTableRegionType is contained in the regions too

		List<TrpPage> pages = doc.getPages();
		String exportPath = exportFile.getPath();
		
		int totalPages = pageIndices==null ? pages.size() : pageIndices.size();
		if (monitor!=null) {
			monitor.beginTask("Exporting tables to Excel", totalPages);
		}
				
		wb = new XSSFWorkbook();
		int c=0;
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
			JAXBPageTranscript tr = ExportUtils.getPageTranscriptAtIndex(i);
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
					logger.debug("is table");
					TrpTableRegionType table = (TrpTableRegionType) r;
					
					int cols = table.getNCols();
					int rows = table.getNRows();
					
//		        	double maxX = PageXmlUtils.buildPolygon(table.getCoords().getPoints()).getBounds().getMaxX();
//		        	double minX = PageXmlUtils.buildPolygon(table.getCoords().getPoints()).getBounds().getMinX();
//		        	int tablesize = (int) (maxX - minX);
					
					List<List<TrpTableCellType>> allRowCells = new ArrayList<List<TrpTableCellType>>();
					for (int k = 0; k<rows; k++){
						allRowCells.add(table.getRowCells(k));
					}
					
		            List<HashMap<Integer, TrpTableCellType>> allRows = new ArrayList<HashMap<Integer, TrpTableCellType>>();
		            
		            HashMap<Integer, TrpTableCellType> nextRowMap = new HashMap<Integer, TrpTableCellType>();
		           
		            for (List<TrpTableCellType> rowCells : allRowCells){
		          
		            	HashMap<Integer, TrpTableCellType> currRowMap = new HashMap<Integer, TrpTableCellType>();
		            	
		            	/*
		            	 * fill up all cells which are not set in TRP (needed for vertical cell merge)
		            	 * the nextRowMap contains already all cells which span vertically with the cells above - means they got merged 
		            	 * in the table but have to be considered here 
		            	 */
	    				currRowMap.putAll(nextRowMap);
	    				nextRowMap.clear();
		            	
		            	for (TrpTableCellType cell : rowCells){
			            	//logger.debug("table cell text " + cell.getUnicodeTextFromLines());
			            	currRowMap.put(cell.getCol(), cell);
			            	//only one row or col span is considered -> FIXME: do it for all spans, but may happens never?
			            	if (cell.getRowSpan() > 1){
			            		nextRowMap.put(cell.getCol(), null);
			            	}
			            	if (cell.getColSpan() > 1){
			            		currRowMap.put(cell.getCol()+1, null);
			            	}
		            	}
		            	allRows.add(currRowMap);
		            }

		            createTable(rows, cols, allRows, j);

				}
				
			
			logger.debug("writing xlsx for page "+(i+1)+"/"+doc.getNPages());

			++c;
			if (monitor!=null) {
				monitor.worked(c);
			}
			}
		}
		
		CellStyle rowStyle = wb.createCellStyle();
		rowStyle.setWrapText(true);
		
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
                
                //headerRow.setRowStyle(rowStyle);
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
	
	private static void createTable(int rows, int cols, List<HashMap<Integer, TrpTableCellType>> allRows, int tableID) {

		String tableName = "table_"+tableID;
		Sheet currSheet = wb.createSheet(WorkbookUtil.createSafeSheetName(tableName));
		
		CellStyle style=wb.createCellStyle();
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		

        		
	    int i = 0;
	    int colIdtmp = 0;

	    for (HashMap<Integer, TrpTableCellType> entry : allRows) {
	        	        
	        if (entry.keySet().size() != cols){
	        	logger.debug("size of entries does not match columns ");
	        }

	    	Row nextRow = currSheet.createRow(i);
	    	
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
		        	
		        	Cell currCell = nextRow.createCell(colID);
		        	currCell.setCellStyle(style);
		        	currCell.setCellValue(entry.get(key).getUnicodeTextFromLines());
		        	
		        	
		        	
		        	// sheet.addMergedRegion(rowFrom,rowTo,colFrom,colTo);
		        	if(mergedVertical){
		        		currSheet.addMergedRegion(new CellRangeAddress(rowID,rowID+rowSpan-1,colID,colID));
		        	}
		        	
		        	if(mergedHorizontal){
		        		currSheet.addMergedRegion(new CellRangeAddress(rowID, rowID, colID, colID+colSpan-1));
		        	}
	        	}
	        	else{
		        	Cell currCell = nextRow.createCell(key);
		        	currCell.setCellStyle(style);
	        	}
	        }
	        
	        
	    }
	}
	



}
