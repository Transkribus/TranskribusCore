package eu.transkribus.core.model.beans.auth;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.TrpUserCollection;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpUserOverallInfo implements Serializable {
	
	private static final long serialVersionUID = 7847909738857522789L;
	
	protected String overallUserName;

	protected int overallUploads;
	protected int overallTraining;
	protected String overallTrainingTime;
	protected int overallHtr;
	protected String overallHtrTime;
	protected int overallOcr;
	protected String overallOcrTime;
	protected int overallLa;
	protected String overallLaTime;
	protected BigDecimal overallCreateDoc;
	protected BigDecimal overallDeleteDoc;
	protected BigDecimal overallHosting;
	protected TrpUserCollection userCollection = null; 
	
	public TrpUserOverallInfo() {}
	
	public TrpUserOverallInfo(TrpUserOverallInfo trpUserInfo) {
	    this.overallUserName = trpUserInfo.overallUserName;
	    this.overallUploads = trpUserInfo.overallUploads;
	    this.overallTraining = trpUserInfo.overallTraining;
	    this.overallHtr = trpUserInfo.overallHtr;
	    this.overallOcr = trpUserInfo.overallOcr;
	    this.overallLa = trpUserInfo.overallLa;
	   
	}

	

	public String getOverallUserName() {
		return overallUserName;
	}

	public void setOverallUserName(String overallUserName) {
		this.overallUserName = overallUserName;
	}

	public int getOverallUploads() {
		return overallUploads;
	}

	public void setOverallUploads(int overallUploads) {
		this.overallUploads = overallUploads;
	}

	public int getOverallTraining() {
		return overallTraining;
	}

	public void setOverallTraining(int overallTraining) {
		this.overallTraining = overallTraining;
	}

	public String getOverallTrainingTime() {
		return overallTrainingTime;
	}

	public void setOverallTrainingTime(String overallTrainingTime) {
		this.overallTrainingTime = overallTrainingTime;
	}

	public int getOverallHtr() {
		return overallHtr;
	}

	public void setOverallHtr(int overallHtr) {
		this.overallHtr = overallHtr;
	}

	public String getOverallHtrTime() {
		return overallHtrTime;
	}

	public void setOverallHtrTime(String overallHtrTime) {
		this.overallHtrTime = overallHtrTime;
	}

	public int getOverallOcr() {
		return overallOcr;
	}

	public void setOverallOcr(int overallOcr) {
		this.overallOcr = overallOcr;
	}

	public String getOverallOcrTime() {
		return overallOcrTime;
	}

	public void setOverallOcrTime(String overallOcrTime) {
		this.overallOcrTime = overallOcrTime;
	}

	public int getOverallLa() {
		return overallLa;
	}

	public void setOverallLa(int overallLa) {
		this.overallLa = overallLa;
	}

	public String getOverallLaTime() {
		return overallLaTime;
	}

	public void setOverallLaTime(String overallLaTime) {
		this.overallLaTime = overallLaTime;
	}

	public BigDecimal getOverallCreateDoc() {
		return overallCreateDoc;
	}

	public void setOverallCreateDoc(BigDecimal overallCreateDoc) {
		this.overallCreateDoc = overallCreateDoc;
	}

	public BigDecimal getOverallDeleteDoc() {
		return overallDeleteDoc;
	}

	public void setOverallDeleteDoc(BigDecimal overallDeleteDoc) {
		this.overallDeleteDoc = overallDeleteDoc;
	}

	public BigDecimal getOverallHosting() {
		return overallHosting;
	}

	public void setOverallHosting(BigDecimal overallHosting) {
		this.overallHosting = overallHosting;
	}

	public TrpUserCollection getUserCollection() {
		return userCollection;
	}

	public void setUserCollection(TrpUserCollection userCollection) {
		this.userCollection = userCollection;
	}
	

}
