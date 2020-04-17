package eu.transkribus.core.model.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

import eu.transkribus.core.model.beans.rest.ParameterMap;
import eu.transkribus.core.util.CoreUtils;
import eu.transkribus.core.util.JaxbUtils;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpBaselineErrorRate {
	private ParameterMap params = new ParameterMap();
	private List<TrpBaselineErrorRateListEntry> list = new ArrayList<>();
	private double precision=0.0d;
	private double recall=0.0d;	
	
	public TrpBaselineErrorRate() {
	}
	
	public ParameterMap getParams() {
		return params;
	}

	public void setParams(ParameterMap params) {
		this.params = params;
	}
	
	public List<TrpBaselineErrorRateListEntry> getList() {
		return list;
	}

	public void setList(List<TrpBaselineErrorRateListEntry> list) {
		this.list = list;
	}	
	
	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public double getRecall() {
		return recall;
	}

	public void setRecall(double recall) {
		this.recall = recall;
	}	
	
	public double getF1() {
		return 2.0d*precision*recall/(precision+recall);
	}
	
	public String getPrRcF1Str() {
		return CoreUtils.roundTo2(precision)+"/"+CoreUtils.roundTo2(recall)+"/"+CoreUtils.roundTo2(getF1());
	}
	
	public String getSummary() {
		String str = "P/R/F1: "+getPrRcF1Str();
		str += " (";
		for (TrpBaselineErrorRateListEntry e : getList()) {
			str+="p"+e.getPageNumber()+": "+e.getPrRcF1Str()+", ";
		}
		str = StringUtils.removeEnd(str, ", ");
		str+=")";
		return str;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		long temp;
		temp = Double.doubleToLongBits(precision);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(recall);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrpBaselineErrorRate other = (TrpBaselineErrorRate) obj;
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
		if (Double.doubleToLongBits(precision) != Double.doubleToLongBits(other.precision))
			return false;
		if (Double.doubleToLongBits(recall) != Double.doubleToLongBits(other.recall))
			return false;
		return true;
	}

}
