package eu.transkribus.core.model.beans.auth;

import java.util.List;

import eu.transkribus.core.util.AuthUtils;

public enum TrpRole {
	Admin(Integer.MAX_VALUE),
	Owner(10),
	Editor(9),
	Transcriber(2),
	CrowdTranscriber(1), // not used currently -> for users that can only transcribe but not change the segmentation
	Reader(0),
	None(-1);
	
	int value=Integer.MIN_VALUE;
	
	TrpRole(int value) {
		this.value = value;
	}
	
	public int getValue() { return value; }
	
	public boolean canManage() { 
		return AuthUtils.canManage(this); 
	}
	public boolean canTranscribe() { 
		return AuthUtils.canTranscribe(this); 
	}
	public boolean canRead() {
		return AuthUtils.canRead(this);
	}
	
	public boolean isVirtual() {
		return value > Owner.value || value == CrowdTranscriber.value || value <= None.value; // currently CrowdTranscriber is also virtual!!
	}
			
	public static TrpRole fromStringNonVirtual(final String roleStr) {
		TrpRole r = fromString(roleStr);
		
		if (r== null || r.isVirtual())
			return null;
		else
			return r;
	}
	
	public static TrpRole fromString(final String roleStr) {
		TrpRole trpRole = null;
		if (roleStr != null && !roleStr.isEmpty()) {
			try {
				trpRole = TrpRole.valueOf(roleStr);
			} catch (IllegalArgumentException e) {
				//silently ignore non-TRP roles
			}
		}
		return trpRole;
	}

	public static boolean hasRole(List<TrpRole> roles, TrpRole role) {
		return (roles != null && roles.contains(role));
	}
	
	public static boolean hasRole2(List<String> roles, TrpRole role) {
		return (roles != null && roles.contains(role.toString()));
	}
}