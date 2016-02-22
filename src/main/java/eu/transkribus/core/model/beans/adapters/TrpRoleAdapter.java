package eu.transkribus.core.model.beans.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.transkribus.core.model.beans.auth.TrpRole;

/**
 * 
 * Adapter for correct (un)marshalling of enum TrpRoles
 * 
 * Needed for Jersey 2.x
 * 
 * @author philip
 * 
 */
public class TrpRoleAdapter extends XmlAdapter<String, TrpRole> {
	@Override
	public String marshal(TrpRole v) throws Exception {
		String result = null;
		if (v != null) {
			result = v.toString();
		}
		return result;
	}
	@Override
	public TrpRole unmarshal(String v) throws Exception {
		TrpRole result = null;
		if(v != null){
			result = TrpRole.valueOf(v);
		}
		return result;
	}
}