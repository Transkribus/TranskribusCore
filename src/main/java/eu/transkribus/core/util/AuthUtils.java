package eu.transkribus.core.util;

import eu.transkribus.core.model.beans.auth.TrpRole;

public class AuthUtils {
	
	public static boolean isOwner(TrpRole role) {
		return role!=null && role.getValue()==TrpRole.Owner.getValue();
	}
	
	/**
	 * Can this role e.g. upload a document
	 * @param role
	 * @return
	 */
	public static boolean canManage(TrpRole role) {
		return role != null && role.getValue() >= TrpRole.Editor.getValue();
	}

	/**
	 * Can this role do any editing
	 * @param role
	 * @return
	 */
	public static boolean canTranscribe(TrpRole role) {
		return role != null && role.getValue() >= TrpRole.CrowdTranscriber.getValue();
	}
	
	public static boolean canRead(TrpRole role){
		return role != null && role.getValue() >= TrpRole.Reader.getValue();
	}
	
}
