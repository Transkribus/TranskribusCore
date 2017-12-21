package eu.transkribus.core.model.beans.transformer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.io.util.TrpProperties;
import eu.transkribus.core.model.beans.job.TrpJobStatus;
import eu.transkribus.core.model.beans.kws.TrpKeyWord;
import eu.transkribus.core.model.beans.kws.TrpKwsHit;
import eu.transkribus.core.model.beans.kws.TrpKwsQuery;
import eu.transkribus.core.model.beans.kws.TrpKwsResult;
import eu.transkribus.core.rest.JobConst;
import eu.transkribus.core.util.JaxbUtils;
import eu.transkribus.core.util.KwsResultCache;

public class KwsTransformer {
	private static final Logger logger = LoggerFactory.getLogger(KwsTransformer.class);
	
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
	
	private KwsTransformer() {}
	
	public static TrpKwsQuery getQuery(TrpJobStatus job) {
		TrpKwsResult result = getKwsResultData(job);
		TrpKwsQuery q = new TrpKwsQuery(job, result);
		return q;
	}
	
	public static List<TrpKwsHit> getHitList(TrpJobStatus job, String keyword, int index, int nValues) {
		TrpKwsResult r = getKwsResultData(job);
		if(r == null) {
			return new ArrayList<>(0);
		}
		final boolean doPaging;
		if(nValues > 0) {
			doPaging = true;
		} else {
			doPaging = false;
			nValues = Integer.MAX_VALUE;
		}
		List<TrpKwsHit> hitList = new ArrayList<>(r.getTotalNrOfHits());
		
		int count = 0;
		for(TrpKeyWord k : r.getKeyWords()) {
			final String currWord = k.getKeyWord();
			if(keyword != null && !currWord.equals(keyword)) {
				continue;
			}
			for(int i = 0; i < k.getHits().size(); i++) {
				if(!doPaging || (i >= index && count < nValues)) {
					TrpKwsHit h = k.getHits().get(i);
					h.setKeyword(currWord);
					hitList.add(h);
					count++;
				}
			}
		}
		return hitList;
	}
	
	public static TrpKwsResult getKwsResultData(TrpJobStatus job) {
		TrpProperties props = job.getJobDataProps();
		TrpKwsResult result;
		if((result = KwsResultCache.INSTANCE.get(job.getJobIdInt())) == null) {
			result = KwsTransformer.extractResultDataFromProps(props);
			if(result != null) {
				KwsResultCache.INSTANCE.put(result);
			}
		}
		return result;
	}
	
	public static TrpKwsResult extractResultDataFromProps(TrpProperties props) {
		final String xmlStr = props.getString(JobConst.PROP_RESULT);
//		logger.debug(xmlStr);
		TrpKwsResult res = null;
		if(xmlStr != null) {
			try {
				res = JaxbUtils.unmarshal(xmlStr, TrpKwsResult.class, TrpKeyWord.class, TrpKwsHit.class);
			} catch (JAXBException e) {
				logger.error("Could not unmarshal kws result from job!");
			}
		}
		return res;
	}
	
	public static String getKwsDuration(TrpJobStatus job) {
		final String durStr;
		if (job.getEndTime() < 1) {
			durStr = "N/A";
		} else {
			final long diff = job.getEndTime() - job.getCreateTime();
			durStr = DECIMAL_FORMAT.format(diff / 1000f) + " sec.";
		}
		return durStr;
	}

	public static String getKwsScope(TrpJobStatus job) {
		return job.getDocId() < 1 ? "Collection " + job.getColId() : "Document " + job.getDocId();
	}

	public static String getKwsStatus(TrpJobStatus job) {
		final String statusStr;
		switch(job.getState()) {
		case TrpJobStatus.RUNNING:
			statusStr = "Processing...";
			break;
		case TrpJobStatus.FAILED:
			statusStr = "Failed. See job description for more info.";
			break;
		default:
			statusStr = "Completed";
			break;
		}
		return statusStr;
	}
}
