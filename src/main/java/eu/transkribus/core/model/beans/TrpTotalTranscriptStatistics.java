package eu.transkribus.core.model.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Statistics object that can be extracted from all classes extending ATotalTranscriptStatistics
 * 
 * @author gh
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpTotalTranscriptStatistics extends ATotalTranscriptStatistics {
	
	@Override
	public String toString() {
		return "TrpTranscriptStatistics [nrOfRegions=" + nrOfRegions + ", nrOfTranscribedRegions="
				+ nrOfTranscribedRegions + ", nrOfWordsInRegions=" + nrOfWordsInRegions + ", nrOfLines=" + nrOfLines
				+ ", nrOfTranscribedLines=" + nrOfTranscribedLines + ", nrOfWordsInLines=" + nrOfWordsInLines
				+ ", nrOfWords=" + nrOfWords + ", nrOfTranscribedWords=" + nrOfTranscribedWords + "]";
	}
	
}

