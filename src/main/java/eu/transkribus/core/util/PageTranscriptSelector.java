package eu.transkribus.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptMetadata;
import eu.transkribus.core.model.beans.enums.EditStatus;

/**
 * PageTranscriptSelector encapsulates logic for determining which transcript to pick from a page for e.g. a training process in {@link #selectTranscript(TrpPage, EditStatus)}. A EditStatus may be given optionally to alter the selection.<br>
 * The method {@link #isQualifiedForTraining(TrpTranscriptMetadata)} will be used to decide if a given transcript may be used for training.<br>
 * {@link #getTrainDataSizeLabel(TrpPage, EditStatus)} can be used to produce a size description label for display in the frontend, e.g. "42 transcribed lined".<br><br>
 * <br><br>
 * This base implementation just does the selection of a transcript from the version history but will accept any transcript. This behavior should be overridden for specific trainings and their requirements on page content.
 * 
 */
public class PageTranscriptSelector {
	private static final Logger logger = LoggerFactory.getLogger(PageTranscriptSelector.class);

	/**
	 * Check if the page fulfills requirements for this training process.
	 * If status is null, the latest transript will be considered.
	 * If status is not null, the latest transcript with this status will be checked and the method returns false if the status does not exist.
	 * 
	 * @param page
	 * @param status
	 * @return
	 */
	public boolean isQualifiedForTraining(TrpPage page, EditStatus status) {
		TrpTranscriptMetadata tmd = selectTranscript(page, status);
		if(tmd == null) {
			//no transcript with this status containing text
			return false;
		}
		return isQualifiedForTraining(tmd);
	}
	
	/**
	 * The routine that selects a transcript from the version history of a page throughout the training dialog
	 * 
	 * @param status
	 * @return
	 */
	public TrpTranscriptMetadata selectTranscript(TrpPage page, EditStatus status) {
		//if no status filter is set use latest, otherwise check content of transcript with status
		if(status == null) {
			return page.getCurrentTranscript();
		} else {
			return page.getTranscriptWithStatusOrNull(status);
		}
	}
	
	/**
	 * Check if the transcript metadata qualifies for training. The base implementation just does a null check.<br>
	 * For indepth checks of the PageXML caching should be implemented as this check is called very often!
	 * 
	 * @param tmd
	 * @return
	 */
	public boolean isQualifiedForTraining(TrpTranscriptMetadata tmd) {
		if(tmd == null) {
			//null should not be passed
			logger.warn("Transcript object is null!");
			return false;
		}
		return true;
	}
	
	/**
	 * Return a size label to be shown in tables in treeviewers.
	 * 
	 * @param p
	 * @param status
	 * @return
	 */
	public String getTrainDataSizeLabel(TrpPage p, EditStatus status) {
		TrpTranscriptMetadata tmd = selectTranscript(p, status);
		if(tmd == null) {
			return "no transcript with status " + status.getStr();
		} else {
			return getTrainableItemCount(tmd);
		}
	}

	protected String getTrainableItemCount(TrpTranscriptMetadata tmd) {
		return "unknown number of items";
	}
}
