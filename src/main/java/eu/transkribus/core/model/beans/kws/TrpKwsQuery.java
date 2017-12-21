package eu.transkribus.core.model.beans.kws;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpKwsQuery {
	
	protected Integer jobId;
	protected Date created;
	protected String duration;
	protected String scope;
	protected String status;
	
	@XmlElementWrapper(name="collectionList")
	@XmlElement
	protected List<Integer> colIds = new ArrayList<>();
	@XmlElementWrapper(name="docList")
	@XmlElement
	protected List<Integer> docIds = new ArrayList<>();
	
	@XmlElementWrapper(name="keyWordList")
	@XmlElement
	protected List<TrpKeyWord> keyWords = new ArrayList<>();
	
	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public List<Integer> getColIds() {
		return colIds;
	}

	public void setColIds(List<Integer> colIds) {
		this.colIds = colIds;
	}

	public List<Integer> getDocIds() {
		return docIds;
	}

	public void setDocIds(List<Integer> docIds) {
		this.docIds = docIds;
	}
	
	public List<TrpKeyWord> getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(List<TrpKeyWord> keyWords) {
		this.keyWords = keyWords;
	}
}
