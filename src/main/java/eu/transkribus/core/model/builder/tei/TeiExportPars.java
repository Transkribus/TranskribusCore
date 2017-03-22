package eu.transkribus.core.model.builder.tei;

import eu.transkribus.core.model.builder.ExportPars;



/**
 * TEI specific extension of ExportPars
 */
public class TeiExportPars extends ExportPars {
	
	public static class TeiZoneExportMode {
		public boolean regions=true;
		public boolean lines=true;
		public boolean words=false;
		public boolean boundingBoxCoords=false;
				
		public TeiZoneExportMode(boolean regions, boolean lines, boolean words, boolean boundingBoxCoords) {
			super();
			this.regions = regions;
			this.lines = lines;
			this.words = words;
			this.boundingBoxCoords = boundingBoxCoords;
		}
		
		public boolean hasZones() {
			return regions == true || lines == true || words == true;
		}

		@Override
		public String toString() {
			return "TeiZoneExportMode [regions=" + regions + ", lines=" + lines + ", words=" + words
					+ ", boundingBoxCoords=" + boundingBoxCoords + "]";
		}

		

	}
		
//	public enum TeiExportMode {
//		SIMPLE(1),
//		ZONE_PER_PAR(2),
//		ZONE_PER_LINE(3),
//		ZONE_PER_WORD(4);
//		
//		int val;
//		private TeiExportMode(int val) {
//			this.val = val;
//		}
//	}
	
	public enum TeiLinebreakMode {
		LINE_TAG, // lines are covered with <l> ... </l>
		LINE_BREAKS; // lines are ended by linebreak tag <lb>
	}
	
	
	public TeiZoneExportMode mode = new TeiZoneExportMode();
	public TeiLinebreakMode linebreakMode = TeiLinebreakMode.LINE_TAG;
	
	@Override public String toString() {
		return "TeiExportPars [mode=" + mode + ", linebreakMode=" + linebreakMode + ", writeTextOnWordLevel=" + writeTextOnWordLevel + ", doBlackening="
				+ doBlackening + ", pageIndices=" + pageIndices + ", selectedTags=" + selectedTags + "]";
	}

}
