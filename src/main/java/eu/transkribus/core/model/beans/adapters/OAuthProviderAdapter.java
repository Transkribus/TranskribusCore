package eu.transkribus.core.model.beans.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.transkribus.core.model.beans.enums.OAuthProvider;

/**
 * 
 * Adapter for correct (un)marshalling of enum EditStatus
 * 
 * Needed for Jersey 2.x
 * 
 * @author philip
 * 
 */
public class OAuthProviderAdapter extends XmlAdapter<String, OAuthProvider> {

	@Override
	public String marshal(OAuthProvider v) throws Exception {
		String result = null;
		if (v != null) {
			result = v.toString();
		}
		return result;
	}

	@Override
	public OAuthProvider unmarshal(String v) throws Exception {
		OAuthProvider result = null;
		if(v != null){
			result = OAuthProvider.valueOf(v);
		}
		
		return result;
	}

}