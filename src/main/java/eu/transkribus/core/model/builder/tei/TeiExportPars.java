package eu.transkribus.core.model.builder.tei;

import eu.transkribus.core.model.builder.ExportPars;



/**
 * TEI specific extension of ExportPars
 */
public class TeiExportPars extends ExportPars {
	public static final String LINE_BREAK_TYPE_LINE_TAG = "LINE_TAG";
	public static final String LINE_BREAK_TYPE_LINE_BREAKS = "LINE_BREAKS";
	
	public boolean regionZones=true;
	public boolean lineZones=true;
	public boolean wordZones=false;
	public boolean boundingBoxCoords=false;
	public String linebreakType = LINE_BREAK_TYPE_LINE_TAG;
	
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
	
	public boolean hasZones() {
		return regionZones == true || lineZones == true || wordZones == true;
	}
	
	public boolean isLineBreakType() {
		return linebreakType == TeiExportPars.LINE_BREAK_TYPE_LINE_BREAKS;
	}
	
	public boolean isLineTagType() {
		return linebreakType == TeiExportPars.LINE_BREAK_TYPE_LINE_TAG;
	}

	@Override
	public String toString() {
		return "TeiExportPars [regionZones=" + regionZones + ", lineZones=" + lineZones + ", wordZones=" + wordZones
				+ ", boundingBoxCoords=" + boundingBoxCoords + ", linebreakType=" + linebreakType
				+ ", writeTextOnWordLevel=" + writeTextOnWordLevel + ", doBlackening=" + doBlackening + ", pageIndices="
				+ pageIndices + ", selectedTags=" + selectedTags + "]";
	}

//	public static class TeiZoneExportMode {
//		public boolean regions=true;
//		public boolean lines=true;
//		public boolean words=false;
//		public boolean boundingBoxCoords=false;
//				
//		public TeiZoneExportMode(boolean regions, boolean lines, boolean words, boolean boundingBoxCoords) {
//			super();
//			this.regions = regions;
//			this.lines = lines;
//			this.words = words;
//			this.boundingBoxCoords = boundingBoxCoords;
//		}
//		
//		public boolean hasZones() {
//			return regions == true || lines == true || words == true;
//		}
//
//		@Override
//		public String toString() {
//			return "TeiZoneExportMode [regions=" + regions + ", lines=" + lines + ", words=" + words
//					+ ", boundingBoxCoords=" + boundingBoxCoords + "]";
//		}
//
//		
//
//	}
			
//	public enum TeiLinebreakMode {
//		LINE_TAG, // lines are covered with <l> ... </l>
//		LINE_BREAKS; // lines are ended by linebreak tag <lb>
//	}
	
	
//	public TeiZoneExportMode mode = new TeiZoneExportMode();
//	public TeiLinebreakMode linebreakMode = TeiLinebreakMode.LINE_TAG;
	
//	@Override public String toString() {
//		return "TeiExportPars [mode=" + mode + ", linebreakMode=" + linebreakMode + ", writeTextOnWordLevel=" + writeTextOnWordLevel + ", doBlackening="
//				+ doBlackening + ", pageIndices=" + pageIndices + ", selectedTags=" + selectedTags + "]";
//	}

}
