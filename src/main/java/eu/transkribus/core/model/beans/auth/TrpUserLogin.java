package eu.transkribus.core.model.beans.auth;

import java.io.Serializable;
import java.security.Principal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import eu.transkribus.core.model.beans.adapters.OAuthProviderAdapter;
import eu.transkribus.core.model.beans.enums.OAuthProvider;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpUserLogin extends TrpUser implements Principal, Serializable {
	private static final long serialVersionUID = 2370532427197634733L;
	private Date loginTime = new Date();
	private String sessionId;
	private String userAgent;
	private String ip;
	private String guiVersion;
	private String refreshToken;
	private String pic;
	@XmlJavaTypeAdapter(OAuthProviderAdapter.class)
	private OAuthProvider oAuthProvider;
	
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

	public void setRefreshToken(String refreshToken){
		this.refreshToken = refreshToken;
	}
	
	public String getRefreshToken(){
		return refreshToken;
	}
	
	public void setPic(String pic) {
		this.pic = pic;		
	}
	
	public String getPic() {
		return pic;		
	}
	
	@Override
	public String getName() {
		return this.getEmail();
	}
	
	public void setOAuthProvider(OAuthProvider prov) {
		this.oAuthProvider = prov;
	}
	
	public OAuthProvider getOAuthProvider(){
		return oAuthProvider;
	}
	
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
