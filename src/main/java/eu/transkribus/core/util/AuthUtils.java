package eu.transkribus.core.util;

import eu.transkribus.core.model.beans.TrpDocMetadata;
import eu.transkribus.core.model.beans.auth.TrpRole;
import eu.transkribus.core.model.beans.auth.TrpUserLogin;

public class AuthUtils {
	
	public static boolean isOwner(TrpRole role) {
		return role!=null && role.getValue()>=TrpRole.Owner.getValue();
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
	
	public static boolean canDuplicate(TrpRole roleInCollection, TrpDocMetadata doc, TrpUserLogin user) {
		if (user.isAdmin() || doc.getUploaderId() == user.getUserId())
			return true;
		
		if (roleInCollection.getValue() >= TrpRole.Editor.getValue())
			return true;

		return false;
	}
		
}
