package eu.transkribus.core.model.builder;

import java.net.URL;
import java.util.List;
import java.util.Observable;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.TrpDoc;
import eu.transkribus.core.model.beans.TrpPage;
import eu.transkribus.core.model.beans.TrpTranscriptStatistics;
import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.util.PageXmlUtils;

public class DocStatisticsBuilder extends Observable {
	private static final Logger logger = LoggerFactory.getLogger(DocStatisticsBuilder.class);
	
	public TrpTranscriptStatistics compute(TrpDoc doc) throws JAXBException{
		if(doc == null){
			throw new IllegalArgumentException("TrpDoc is null!");
		}
		TrpTranscriptStatistics stats = new TrpTranscriptStatistics();
		List<TrpPage> pages = doc.getPages();
		for(TrpPage p : pages){
			final String msg = "Computing stats: page " + p.getPageNr() + "/" + pages.size();
			logger.debug(msg);
			notifyObservers(msg);
			setChanged();
			URL xmlUrl = p.getCurrentTranscript().getUrl();
			PcGtsType pc = PageXmlUtils.unmarshal(xmlUrl);
			TrpTranscriptStatistics pageStats = PageXmlUtils.extractStats(pc);
			stats.add(pageStats);
		}
		
		return stats;
	}
}
