package eu.transkribus.core.model.builder.tei;

/**
 * TEI specific extension of ExportPars
 */
public class TeiExportPars {
	public static final String LINE_BREAK_TYPE_LINE_TAG = "LINE_TAG";
	public static final String LINE_BREAK_TYPE_LINE_BREAKS = "LINE_BREAKS";
	
//	CommonExportPars commonPars = new CommonExportPars();
		
	boolean regionZones=true;
	boolean lineZones=true;
	boolean wordZones=false;
	boolean boundingBoxCoords=false;
	String linebreakType = LINE_BREAK_TYPE_LINE_TAG;
	
	boolean lineBreakAtBeginningOfLine=true;
	boolean pbImageNameAsXmlId=true;
	
	boolean lineXmlIds=false; // FIXME
		
	public TeiExportPars() {
	}

	public TeiExportPars(boolean regionZones, boolean lineZones, boolean wordZones, boolean boundingBoxCoords,
			String linebreakType) {
		super();
		this.regionZones = regionZones;
		this.lineZones = lineZones;
		this.wordZones = wordZones;
		this.boundingBoxCoords = boundingBoxCoords;
		this.linebreakType = linebreakType;
	}

	public boolean isRegionZones() {
		return regionZones;
	}

	public void setRegionZones(boolean regionZones) {
		this.regionZones = regionZones;
	}

	public boolean isLineZones() {
		return lineZones;
	}

	public void setLineZones(boolean lineZones) {
		this.lineZones = lineZones;
	}

	public boolean isWordZones() {
		return wordZones;
	}

	public void setWordZones(boolean wordZones) {
		this.wordZones = wordZones;
	}

	public boolean isBoundingBoxCoords() {
		return boundingBoxCoords;
	}

	public void setBoundingBoxCoords(boolean boundingBoxCoords) {
		this.boundingBoxCoords = boundingBoxCoords;
	}

	public String getLinebreakType() {
		return linebreakType;
	}

	public void setLinebreakType(String linebreakType) {
		this.linebreakType = linebreakType;
	}

	// utility methods:
	public boolean hasZones() {
		return regionZones == true || lineZones == true || wordZones == true;
	}
	
	public boolean isLineBreakType() {
		return linebreakType == TeiExportPars.LINE_BREAK_TYPE_LINE_BREAKS;
	}
	
	public boolean isLineTagType() {
		return linebreakType == TeiExportPars.LINE_BREAK_TYPE_LINE_TAG;
	}

	public boolean isLineBreakAtBeginningOfLine() {
		return lineBreakAtBeginningOfLine;
	}

	public void setLineBreakAtBeginningOfLine(boolean lineBreakAtBeginningOfLine) {
		this.lineBreakAtBeginningOfLine = lineBreakAtBeginningOfLine;
	}

	public boolean isPbImageNameAsXmlId() {
		return pbImageNameAsXmlId;
	}

	public void setPbImageNameAsXmlId(boolean pbImageNameAsXmlId) {
		this.pbImageNameAsXmlId = pbImageNameAsXmlId;
	}

	@Override
	public String toString() {
		return "TeiExportPars [regionZones=" + regionZones + ", lineZones=" + lineZones + ", wordZones=" + wordZones
				+ ", boundingBoxCoords=" + boundingBoxCoords + ", linebreakType=" + linebreakType
				+ ", lineBreakAtBeginningOfLine=" + lineBreakAtBeginningOfLine + ", pbImageNameAsXmlId="
				+ pbImageNameAsXmlId + "]";
	}


	

}
