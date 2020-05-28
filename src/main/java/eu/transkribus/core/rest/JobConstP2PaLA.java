package eu.transkribus.core.rest;

public class JobConstP2PaLA {
	public static final String PROP_REGIONS = "regions";
	public static final String PROP_MERGED_REGIONS = "merge_regions";
	public static final String PROP_OUT_MODE = "out_mode";
	public static final String PROP_SPLIT_FRACTIONS = "splitFractions";
	
	// TODO: --min_area, --simplify_shapes, --nontext_regions, training: external baseline training algorithm
	public static final String MIN_AREA_PAR = "--min_area";
	public static final String RECTIFY_REGIONS_PAR = "--rectify_regions";
	public static final String ENRICH_EXISTING_TRANSCRIPTIONS_PAR = "enrichExistingTranscriptions";
	public static final String LABEL_REGIONS_PAR = "labelRegions";
	public static final String LABEL_LINES_PAR = "labelLines";
	public static final String LABEL_WORDS_PAR = "labelWords";
}
