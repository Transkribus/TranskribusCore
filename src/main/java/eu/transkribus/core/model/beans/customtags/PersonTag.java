package eu.transkribus.core.model.beans.customtags;

import java.util.Set;

public class PersonTag extends CustomTag {
	public static final String TAG_NAME = "person";
	
	public final CustomTagAttribute[] ATTRIBUTES = { 
			new CustomTagAttribute("firstname", true, "Firstname", "The firstname of the person"),
			new CustomTagAttribute("lastname", true, "Firstname", "The lastname of the person"),
			new CustomTagAttribute("dateOfBirth", true, "Date of Birth", ""),
			new CustomTagAttribute("dateOfDeath", true, "Date of Death", ""),
			new CustomTagAttribute("occupation", true, "Occupation", ""),
			new CustomTagAttribute("notice", true, "Notice", ""),
	};
	
	public static String DEFAULT_COLOR = "#FFA500";
	
	String firstname, lastname;
	String dateOfBirth, dateOfDeath;
	String occupation;
	String notice;

	public PersonTag() {
		super(TAG_NAME);
	}
	
	public PersonTag(PersonTag other) {
		super(other);
		
		this.firstname = other.firstname;
		this.lastname = other.lastname;
		this.dateOfBirth = other.dateOfBirth;
		this.dateOfDeath = other.dateOfDeath;
		this.notice = other.notice;
	}
	
	@Override public String getDefaultColor() { return DEFAULT_COLOR; }
	
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

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getDateOfDeath() {
		return dateOfDeath;
	}

	public void setDateOfDeath(String dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}
	
	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	@Override protected void reset(boolean withIndices) {
		super.reset(withIndices);
		firstname = "";
		lastname = "";
		dateOfBirth = "";
		dateOfDeath = "";
		notice = "";
	}

	@Override public boolean isDeleteable() {
		return false;
	}

	@Override public boolean showInTagWidget() { return true; }
//	@Override public boolean canAddAttribute() { return false; }
	
	@Override
	public PersonTag copy() {
		return new PersonTag(this);
	}

	@Override protected Set<CustomTagAttribute> getPredefinedAttributes() {
		Set<CustomTagAttribute> atts = super.getPredefinedAttributes();
		for (CustomTagAttribute a : ATTRIBUTES) {
			atts.add(a);
		}
		
		return atts;
	}
	
}
