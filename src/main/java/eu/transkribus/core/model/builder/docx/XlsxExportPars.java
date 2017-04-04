package eu.transkribus.core.model.builder.docx;

public class XlsxExportPars {
	public static final String PARAMETER_KEY = "xlsxPars";
	
	boolean doWriteTagsXlsx = false;
	boolean doWriteTablesXlsx = false;
	
	public XlsxExportPars() {
		
	}

	public boolean isDoWriteTagsXlsx() {
		return doWriteTagsXlsx;
	}

	public void setDoWriteTagsXlsx(boolean doWriteTagsXlsx) {
		this.doWriteTagsXlsx = doWriteTagsXlsx;
	}

	public boolean isDoWriteTablesXlsx() {
		return doWriteTablesXlsx;
	}

	public void setDoWriteTablesXlsx(boolean doWriteTablesXlsx) {
		this.doWriteTablesXlsx = doWriteTablesXlsx;
	}

	@Override
	public String toString() {
		return "XlsxExportPars [doWriteTagsXlsx=" + doWriteTagsXlsx + ", doWriteTablesXlsx=" + doWriteTablesXlsx + "]";
	}
	
	

}
