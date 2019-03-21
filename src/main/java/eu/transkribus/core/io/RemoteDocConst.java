package eu.transkribus.core.io;

public class RemoteDocConst {

	private RemoteDocConst() {}

	/**
	 * Status assigned to CITlab HTR sample docs on creation.
	 */
	public static final int STATUS_SAMPLE_DOC = 2;
	
	/**
	 * This status is set on document objects that contain the data of {@link TrpGroundTruthPage} 
	 * objects and are not usable on the collections API, as they do not have a valid document ID.
	 */
	public static final int STATUS_GROUND_TRUTH_DOC = 3;
}
