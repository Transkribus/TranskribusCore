package eu.transkribus.core.model.beans.auth;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;

import eu.transkribus.core.model.beans.TrpUserCollection;
import eu.transkribus.core.model.beans.adapters.SqlTimestampAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpUser implements Serializable {
	private static final long serialVersionUID = 2370532427197634733L;
	protected int userId;
	protected String userName;
	protected String email;
	protected Integer affiliationId;
	protected String affiliation;
	
	protected String firstname;
	protected String lastname;
	protected String gender;
	protected String orcid;
	protected String profilePicUrl;
	
	protected List<String> userRoleList;
	
	protected int isActive=1;
	
	/**
	 * userRoleList is now used instead of this flag. Still here for backwards compatibility during transition
	 */
	protected boolean isAdmin = false;
	
	@XmlJavaTypeAdapter(SqlTimestampAdapter.class)
	private Timestamp created = null;
	
	@XmlJavaTypeAdapter(SqlTimestampAdapter.class)
	private Timestamp delTime = null;
		
	@XmlTransient // shall not be transferred!
	protected String password;
	@XmlTransient
	protected String hash;
	
	// gets set when retrieving user for collection
	protected TrpUserCollection userCollection = null; 
	
	public TrpUser(){}
	
	/**
	 * Copy Constructor
	 * @param trpUser a <code>TrpUser</code> object
	 */
	public TrpUser(TrpUser trpUser) {
	    this.userId = trpUser.userId;
	    this.userName = trpUser.userName;
	    this.email = trpUser.email;
	    this.affiliation = trpUser.affiliation;
	    this.firstname = trpUser.firstname;
	    this.lastname = trpUser.lastname;
	    this.gender = trpUser.gender;
	    this.orcid = trpUser.orcid;
	    this.isAdmin = trpUser.isAdmin;
	    this.password = trpUser.password;
	    this.isActive = trpUser.isActive;
	    this.created = trpUser.created;
	    this.delTime = trpUser.delTime;
	    this.profilePicUrl = trpUser.profilePicUrl;
	    this.userRoleList = trpUser.userRoleList;
	}

	public TrpUser(final int userId, final String userName){
		this.userId = userId;
		this.userName = userName;
	}
	
	public TrpUserCollection getUserCollection() {
		return userCollection;
	}

	public void setUserCollection(TrpUserCollection userCollection) {
		this.userCollection = userCollection;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;			
	}	
	
	public List<String> getUserRoleList() {
		return userRoleList;
	}
	
	public void setUserRoleList(List<String> userRoleList) {
		this.userRoleList = userRoleList;
		
		if(userRoleList != null && userRoleList.contains("Admin")) {
			setAdmin(true);
		}
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	
	public Integer getAffiliationId() {
		return affiliationId;
	}

	public void setAffiliationId(Integer affiliationId) {
		this.affiliationId = affiliationId;
	}
	
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getOrcid() {
		return orcid;
	}
	
	public void setOrcid(String orcid){
		this.orcid = orcid;
	}

	public String getProfilePicUrl() {
		return profilePicUrl;
	}

	public void setProfilePicUrl(String profilePicUrl) {
		this.profilePicUrl = profilePicUrl;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}
	
	public Timestamp getDelTime()  {
		return delTime;
	}
	
	public void setDelTime(Timestamp delTime) {
		this.delTime = delTime;
	}

	public String getInfo(boolean withEmail) {
		String str = firstname+" "+lastname;
		if (withEmail)
			str += " "+email;
		
		return str;
	}
	
	public TrpRole getRoleInCollection() {
		TrpUserCollection uc = getUserCollection();
		return (uc == null ? TrpRole.None : (uc.getRole() == null ? TrpRole.None : uc.getRole()));
	}	
	
	public String getFullname() {
		return firstname+" "+lastname;
	}
	
	@Override
	public String toString() {
		return "TrpUser [userId=" + userId + ", userName=" + userName + ", email=" + email + ", affiliationId="
				+ affiliationId + ", affiliation=" + affiliation + ", firstname=" + firstname + ", lastname=" + lastname
				+ ", gender=" + gender + ", orcid=" + orcid + ", profilePicUrl=" + profilePicUrl + ", userRoleList="
				+ userRoleList + ", isActive=" + isActive + ", isAdmin=" + isAdmin + ", created=" + created
				+ ", delTime=" + delTime + ", password=" + (StringUtils.isEmpty(password) ? "null" : "[is set]") + ", hash=" + hash 
				+ ", userCollection=" + userCollection + "]";
	}
}
