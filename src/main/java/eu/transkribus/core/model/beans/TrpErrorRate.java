package eu.transkribus.core.model.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.rest.ParameterMap;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpErrorRate {
	
	private ParameterMap params;
	
	// Word Error Rate
	private String wer;
	
	// Word Accuracy
	private String wAcc;
	
	// Char Error Rate
	private String cer;
	
	// Char Accuracy
	private String cAcc;
	 

	public TrpErrorRate() {}
	
	public ParameterMap getParams() {
		return params;
	}

	public void setParams(ParameterMap params) {
		this.params = params;
	}

	public String getWer() {
		return wer;
	}

	public void setWer(String wer) {
		this.wer = wer;
	}

	public String getCer() {
		return cer;
	}

	public void setCer(String cer) {
		this.cer = cer;
	}

	public String getwAcc() {
		return wAcc;
	}

	public void setwAcc(String wAcc) {
		this.wAcc = wAcc;
	}

	public String getcAcc() {
		return cAcc;
	}

	public void setcAcc(String cAcc) {
		this.cAcc = cAcc;
	}
	
}
