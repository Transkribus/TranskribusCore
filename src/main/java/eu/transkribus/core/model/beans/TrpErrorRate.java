package eu.transkribus.core.model.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.rest.ParameterMap;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpErrorRate extends TrpErrorRateResult {
	
	private ParameterMap params;
	
	List<TrpErrorRateListEntry> list;

	public TrpErrorRate() {
		list = new ArrayList<>();
	}

	public List<TrpErrorRateListEntry> getList() {
		return list;
	}

	public void setList(List<TrpErrorRateListEntry> list) {
		this.list = list;
	}
	
	public ParameterMap getParams() {
		return params;
	}

	public void setParams(ParameterMap params) {
		this.params = params;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrpErrorRate other = (TrpErrorRate) obj;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		if (params == null) {
			if (other.params != null)
				return false;
		} else if (!params.equals(other.params))
			return false;
		return true;
	}
}
