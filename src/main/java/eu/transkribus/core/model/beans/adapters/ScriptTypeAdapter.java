package eu.transkribus.core.model.beans.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.transkribus.core.model.beans.enums.ScriptType;

/**
 * 
 * Adapter for correct (un)marshalling of enum ScriptType
 * 
 * Needed for Jersey 2.x
 * 
 * @author philip
 * 
 */
public class ScriptTypeAdapter extends XmlAdapter<String, ScriptType> {

	@Override
	public String marshal(ScriptType v) throws Exception {
		String result = null;
		
		if (v != null) {
//			System.out.println("ScriptTypeAdapter marshall: " + v.toString());
			result = v.toString();
		} else {
//			System.out.println("ScriptTypeAdapter marshall: type is null.");
		}
		
		return result;
	}

	@Override
	public ScriptType unmarshal(String v) throws Exception {
		ScriptType result = null;
		
//		System.out.println("ScriptTypeAdapter.unmarshall String: " + v);
		
		if(v != null){
			result = ScriptType.valueOf(v);
		}
		
		return result;
	}

}