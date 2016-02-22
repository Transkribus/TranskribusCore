package eu.transkribus.core.model.beans.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import eu.transkribus.core.model.beans.TrpCollection;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpUserLogin extends TrpUser implements Serializable {
	private static final long serialVersionUID = 2370532427197634733L;
	private Date loginTime = new Date();
	private String sessionId;
	private String userAgent;
	private String ip;
	private String guiVersion;
	
//	@Deprecated
//	@XmlElementWrapper(name="roleList")
//	@XmlElement
//	private List<TrpRole> roles = new ArrayList<>();
//	
//	@Deprecated
//	@XmlElementWrapper(name="permissionMap")
//	@XmlElement
//	private Map<Integer, TrpRole> permissions = new HashMap<>();
	
//	@XmlElementWrapper(name="collectionList")
//	@XmlElement
//	protected List<TrpCollection> colList = new ArrayList<>();
	
	/**
	 * DO NOT USE! Just here because JaxB needs it :-/
	 */
	public TrpUserLogin(){
	}
	
	public TrpUserLogin(TrpUser user) {
		super(user);
	}
		
	public TrpUserLogin(final int userId, final String userName, final String sessionId){
		super(userId, userName);
		this.sessionId = sessionId;
		loginTime = new Date();
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

//	@Deprecated
//	public List<TrpRole> getRoles() {
//		return roles;
//	}
//	@Deprecated
//	public void setRoles(List<TrpRole> roles) {
//		this.roles = roles;
//	}
	
	
	
//	public boolean isAdmin() {
//		return TrpRole.hasRole(roles, TrpRole.Admin);		
//	}
//			
//	public boolean isCurator() {
//		return TrpRole.hasRole(roles, TrpRole.Curator);
//	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public void setGuiVersion(String guiVersion) {
		this.guiVersion = guiVersion;
	}
	
	public String getGuiVersion() {
		return guiVersion;
	}

//	public Map<Integer, TrpRole> getPermissions(){
//		return permissions;
//	}
//	public void setPermissions(Map<Integer, TrpRole> permissions) {
//		this.permissions = permissions;
//	}
	
//	public List<TrpCollection> getColList() {
//		return colList;
//	}
//
//	public void setColList(List<TrpCollection> colList) {
//		this.colList = colList;
//	}
//	
//	public boolean hasCollection(int colId) {		
//		boolean found = false;
//		if (colId != 0 && colList != null){
//			for(TrpCollection c : colList){
//				if(c.getColId() == colId){
//					found = true;
//					break;
//				}
//			}
//		}
//		return found;
//	}	
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName() + " {");
		sb.append(userId + " - ");
		sb.append(userName + " - ");
		sb.append(getFirstname() + " - ");
		sb.append(getLastname() + " - ");
		sb.append(getGender() + " - ");
		sb.append(affiliation + " - ");
		if (loginTime != null)
			sb.append(loginTime.toString() + " - ");
		sb.append(userAgent + " - ");
		sb.append("JSESSIONID=" + sessionId + " - ");
//		boolean isFirst = true;
//		for(TrpRole r : roles){
//			if(isFirst){
//				sb.append(r.toString());
//				isFirst = false;
//			} else {
//				sb.append(", " + r.toString());
//			}
//		}
		sb.append("isAdmin=" + isAdmin());// + " - ");
//		if (collections != null)
//			sb.append(" - Collections: "+Arrays.toString(collections.toArray()));
		sb.append("}");
		return sb.toString();
	}
}
