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
	
	protected int htr;
	protected int ocr;
	protected int la;

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

	public int getHtr() {
		return htr;
	}

	public void setHtr(int htrRuns) {
		this.htr = htrRuns;
	}

	public int getOcr() {
		return ocr;
	}

	public void setOcr(int ocr) {
		this.ocr = ocr;
	}

	public BigDecimal getHosting() {
		return hosting;
	}

	public void setHosting(BigDecimal hosting) {
		this.hosting = hosting;
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
