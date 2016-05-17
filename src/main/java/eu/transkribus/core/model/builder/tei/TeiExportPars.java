package eu.transkribus.core.model.builder.tei;

import eu.transkribus.core.model.builder.ExportPars;



/**
 * TEI specific extension of ExportPars
 */
public class TeiExportPars extends ExportPars {
	public enum TeiExportMode {
		SIMPLE(1),
		ZONE_PER_PAR(2),
		ZONE_PER_LINE(3),
		ZONE_PER_WORD(4);
		
		int val;
		private TeiExportMode(int val) {
			this.val = val;
		}
	}
	
	public enum TeiLinebreakMode {
		LINE_TAG, // lines are covered with <l> ... </l>
		LINE_BREAKS; // lines are ended by linebreak tag <lb>
	}
	
	
	public TeiExportMode mode = TeiExportMode.ZONE_PER_LINE;
	public TeiLinebreakMode linebreakMode = TeiLinebreakMode.LINE_TAG;
	
	@Override public String toString() {
		return "TeiExportPars [mode=" + mode + ", linebreakMode=" + linebreakMode + ", writeTextOnWordLevel=" + writeTextOnWordLevel + ", doBlackening="
				+ doBlackening + ", pageIndices=" + pageIndices + ", selectedTags=" + selectedTags + "]";
	}

}
