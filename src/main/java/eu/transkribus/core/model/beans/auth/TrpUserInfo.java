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
	protected int uploads;
	protected int training;
	protected String trainingTime;
	
	protected int htr;
	protected String htrTime;
	protected int ocr;
	protected String ocrTime;
	protected int la;
	protected String laTime;

	protected BigDecimal createDoc;
	protected BigDecimal deleteDoc;
	protected BigDecimal hosting;
	
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

	public TrpUserCollection getUserCollection() {
		return userCollection;
	}

	public void setUserCollection(TrpUserCollection userCollection) {
		this.userCollection = userCollection;
	}
	

}
