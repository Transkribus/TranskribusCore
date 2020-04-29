package eu.transkribus.core.model.beans;

import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.util.CoreUtils;

@XmlRootElement
public class TrpBaselineErrorRateListEntry {
	
	private Integer pageNumber;
	private double precision=0.0d;
	private double recall=0.0d;
	
	public TrpBaselineErrorRateListEntry() {
		
	}
	
	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pageNumber == null) ? 0 : pageNumber.hashCode());
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
		TrpBaselineErrorRateListEntry other = (TrpBaselineErrorRateListEntry) obj;
		if (pageNumber == null) {
			if (other.pageNumber != null)
				return false;
		} else if (!pageNumber.equals(other.pageNumber))
			return false;
		if (Double.doubleToLongBits(precision) != Double.doubleToLongBits(other.precision))
			return false;
		if (Double.doubleToLongBits(recall) != Double.doubleToLongBits(other.recall))
			return false;
		return true;
	}

}
