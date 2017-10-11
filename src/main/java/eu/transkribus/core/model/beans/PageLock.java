package eu.transkribus.core.model.beans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import eu.transkribus.core.model.beans.auth.TrpUserLogin;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PageLock implements Serializable {
	private static final long serialVersionUID = 2140280621229673319L;
//	private String userName;
	
	static DateFormat timeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	@XmlTransient
	private TrpUserLogin login;
	
	private Timestamp loginTime;
	private String userName;
	private int colId;
	private int docId;
	private int pageNr;
	private String lockType = "";
	
	public PageLock() {}
	
	public PageLock(TrpUserLogin login, int colId, int docId, int pageNr, String lockType){
		this.login = login;
		
		//this.loginTime = (Timestamp) login.getLoginTime();//getCreated();
		//System.out.println("login at: " + loginTime);
		
		this.loginTime = (Timestamp) login.getCreated();
		this.userName = login.getUserName();
		this.colId = colId;
		this.docId = docId;
		this.pageNr = pageNr;
		this.lockType = lockType;
	}

	public Timestamp getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Timestamp loginTime) {
		this.loginTime = loginTime;
	}
	
	public String getLoginTimeFormatted() {
		return timeFormatter.format(new Date(getLoginTime().getTime()));
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public int getPageNr() {
		return pageNr;
	}
	public void setPageNr(int pageNr) {
		this.pageNr = pageNr;
	}
	public String getLockType() {
		return lockType;
	}
	public void setLockType(String lockType) {
		this.lockType = lockType;
	}

	public int getColId() {
		return colId;
	}

	public void setColId(int colId) {
		this.colId = colId;
	}

	@Override public String toString() {
		return "PageLock [login=" + login + ", loginTime=" + loginTime + ", userName=" + userName + ", colId=" + colId + ", docId=" + docId + ", pageNr="
				+ pageNr + ", lockType=" + lockType + "]";
	}


	
}