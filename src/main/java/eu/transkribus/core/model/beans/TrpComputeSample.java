package eu.transkribus.core.model.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpComputeSample {
	
	private boolean isValid;
    private double minProp;
    private double maxProp;
    private double mean;
    private String text;
    
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
	public double getMinProp() {
		return minProp;
	}
	public void setMinProp(double minProp) {
		this.minProp = minProp;
	}
	public double getMaxProp() {
		return maxProp;
	}
	public void setMaxProp(double maxProp) {
		this.maxProp = maxProp;
	}
	public double getMean() {
		return mean;
	}
	public void setMean(double mean) {
		this.mean = mean;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
}
