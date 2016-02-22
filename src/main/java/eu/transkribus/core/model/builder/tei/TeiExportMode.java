package eu.transkribus.core.model.builder.tei;

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