package eu.transkribus.core.model.beans.auth;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.TrpUserCollection;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpUserInfo implements Serializable {
	
	private static final long serialVersionUID = 7847909738857522789L;
	protected String userName;
	protected String overallUserName;
	protected int uploads;
	protected int overallUploads;
	protected int training;
	protected int overallTraining;
	protected String trainingTime;
	protected String overallTrainingTime;
	
	protected int htr;
	protected int overallHtr;
	protected String htrTime;
	protected String overallHtrTime;
	protected int ocr;
	protected int overallOcr;
	protected String ocrTime;
	protected String overallOcrTime;
	protected int la;
	protected int overallLa;
	protected String laTime;
	protected String overallLaTime;

	protected BigDecimal createDoc;
	protected BigDecimal overallCreateDoc;
	protected BigDecimal deleteDoc;
	protected BigDecimal overallDeleteDoc;
	protected BigDecimal hosting;
	protected BigDecimal overallHosting;
	
	protected TrpUserCollection userCollection = null; 
	
	public TrpUserInfo() {}
	
	public TrpUserInfo(TrpUserInfo trpUserInfo) {
	    this.userName = trpUserInfo.userName;
	    this.uploads = trpUserInfo.uploads;
	    this.training = trpUserInfo.training;
	    this.htr = trpUserInfo.htr;
	    this.ocr = trpUserInfo.ocr;
	    this.la = trpUserInfo.la;
	   
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUploads() {
		return uploads;
	}

	public void setUploads(int uploads) {
		this.uploads = uploads;
	}

	public int getTraining() {
		return training;
	}

	public void setTraining(int training) {
		this.training = training;
	}

	public String getTrainingTime() {
		return trainingTime;
	}

	public void setTrainingTime(String trainingTime) {
		this.trainingTime = trainingTime;
	}

	public int getHtr() {
		return htr;
	}

	public void setHtr(int htrRuns) {
		this.htr = htrRuns;
	}

	public String getHtrTime() {
		return htrTime;
	}

	public void setHtrTime(String htrTime) {
		this.htrTime = htrTime;
	}

	public String getOcrTime() {
		return ocrTime;
	}

	public void setOcrTime(String ocrTime) {
		this.ocrTime = ocrTime;
	}

	public String getLaTime() {
		return laTime;
	}

	public void setLaTime(String laTime) {
		this.laTime = laTime;
	}

	public BigDecimal getCreateDoc() {
		return createDoc;
	}

	public void setCreateDoc(BigDecimal createDoc) {
		this.createDoc = createDoc;
	}

	public BigDecimal getDeleteDoc() {
		return deleteDoc;
	}

	public void setDeleteDoc(BigDecimal deleteDoc) {
		this.deleteDoc = deleteDoc;
	}

	public BigDecimal getHosting() {
		return hosting;
	}

	public void setHosting(BigDecimal hosting) {
		this.hosting = hosting;
	}

	public int getOcr() {
		return ocr;
	}

	public void setOcr(int ocr) {
		this.ocr = ocr;
	}
	
	public int getLa() {
		return la;
	}

	public void setLa(int la) {
		this.la = la;
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
