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
import eu.transkribus.core.util.JobDataUtils;
import eu.transkribus.core.util.KwsResultCache;

public class KwsTransformer {
	private static final Logger logger = LoggerFactory.getLogger(KwsTransformer.class);
	
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
	
	private KwsTransformer() {}
	
	public static TrpKwsQuery getQuery(TrpJobStatus job) {
		TrpKwsQuery q = new TrpKwsQuery();
//		logger.debug(job.toString());
		
		q.setJobId(job.getJobIdAsInt());
		q.setCreated(job.getCreated());
		q.setDuration(getKwsDuration(job));
		q.setScope(getKwsScope(job));
		q.setStatus(getKwsStatus(job));

		TrpKwsResult result = getKwsResultData(job);
		
		if(result == null) {
			TrpProperties props = job.getJobDataProps();
			List<String> queries = JobDataUtils.getStringList(props.getProperties(), JobConst.PROP_QUERY);
			queries.stream()
				.forEach(s -> q.getKeyWords()
								.add(new TrpKeyWord(s))
							);
		} else {
			result.getKeyWords()
				.forEach(k -> q.getKeyWords()
								.add(new TrpKeyWord(k.getKeyWord(), k.getHits().size()))
							);
		}
		return q;
	}
	
	public static TrpKwsResult getResult(TrpJobStatus job) {
		TrpKwsResult r = getKwsResultData(job);
		
		if(r == null) {
			return r;
		}

//		logger.debug(job.toString());
		r.setJobId(job.getJobIdAsInt());
		r.setCreated(job.getCreated());
		r.setDuration(getKwsDuration(job));
		r.setScope(getKwsScope(job));
		r.setStatus(getKwsStatus(job));

		return r;
	}
	
	public static List<TrpKwsHit> getHitList(TrpJobStatus job) {
		TrpKwsResult r = getKwsResultData(job);
		if(r == null) {
			return new ArrayList<>(0);
		}
		List<TrpKwsHit> hitList = new ArrayList<>(r.getTotalNrOfHits());
		for(TrpKeyWord k : r.getKeyWords()) {
			final String keyWord = k.getKeyWord();
			k.getHits().stream().forEach(h -> {
				h.setKeyword(keyWord);
				hitList.add(h);
			});
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
	
	private static String getKwsDuration(TrpJobStatus job) {
		final String durStr;
		if (job.getEndTime() < 1) {
			durStr = "N/A";
		} else {
			final long diff = job.getEndTime() - job.getCreateTime();
			durStr = DECIMAL_FORMAT.format(diff / 1000f) + " sec.";
		}
		return durStr;
	}

	private static String getKwsScope(TrpJobStatus job) {
		return job.getDocId() < 1 ? "Collection " + job.getColId() : "Document " + job.getDocId();
	}

	private static String getKwsStatus(TrpJobStatus job) {
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
