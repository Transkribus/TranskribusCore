package eu.transkribus.core.model.beans.auth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.transkribus.core.model.beans.TrpCollection;
import eu.transkribus.core.model.beans.TrpUserCollection;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpUser implements Serializable {
	private static final long serialVersionUID = 2370532427197634733L;
	protected int userId;
	protected String userName;
	protected String email;
	protected String affiliation; //? TODO
	
	protected String firstname;
	protected String lastname;
	protected String gender;
	protected String orcid;
	protected String profilePicUrl;
	
	protected int isActive=1;
	protected boolean isAdmin = false;
	
	private java.sql.Timestamp created=null;
		
	@XmlTransient // shall not be transferred!
	protected String password;
	
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
	    this.profilePicUrl = trpUser.profilePicUrl;
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
	
	@Deprecated
	public boolean canDelete() {
		return affiliation.equals("DEA");
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
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
	
	public java.sql.Timestamp getCreated() {
		return created;
	}

	public void setCreated(java.sql.Timestamp created) {
		this.created = created;
	}

	public String getInfo(boolean withEmail) {
		String str = firstname+" "+lastname;
		if (withEmail)
			str += " "+email;
		
		return str;
	}
	
//	public String getRoleStr() {
//		TrpUserCollection uc = getUserCollection();
//		TrpRole r = uc == null ? TrpRole.None : (uc.getRole() == null ? TrpRole.None : uc.getRole());
//		return r.toString();
//	}
	
	public TrpRole getRoleInCollection() {
		TrpUserCollection uc = getUserCollection();
		return (uc == null ? TrpRole.None : (uc.getRole() == null ? TrpRole.None : uc.getRole()));
//		return r.toString();
	}	
	
	public String getFullname() {
		return firstname+" "+lastname;
	}
	
	public String toString() {
	    final String TAB = ", ";
	    String retValue = "TrpUser ( "+super.toString();
		retValue += TAB + "userId = " + this.userId;
		retValue += TAB + "userName = " + this.userName;
		retValue += TAB + "email = " + this.email;
		retValue += TAB + "affiliation = " + this.affiliation;
		retValue += TAB + "firstname = " + this.firstname;
		retValue += TAB + "lastname = " + this.lastname;
		retValue += TAB + "gender = " + this.gender;
		retValue += TAB + "orcid = " + this.orcid;
		retValue += TAB + "isAvtive = " + this.isActive;
		retValue += TAB + "created = " + ((created==null) ? "null" : created.toString());
		retValue += " )";
	    return retValue;
	}
}
