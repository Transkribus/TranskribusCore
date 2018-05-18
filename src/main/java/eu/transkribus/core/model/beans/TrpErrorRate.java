package eu.transkribus.core.model.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.rest.ParameterMap;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpErrorRate extends ATrpErrorRate {
	
	private ParameterMap params;
	
	List<TrpErrorList> list;

	public TrpErrorRate() {}

	
	public List<TrpErrorList> getList() {
		return list;
	}

	public void setList(List<TrpErrorList> list) {
		this.list = list;
	}
	
	public ParameterMap getParams() {
		return params;
	}

	public void setParams(ParameterMap params) {
		this.params = params;
	}
	
}
