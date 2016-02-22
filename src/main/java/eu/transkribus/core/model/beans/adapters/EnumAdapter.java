package eu.transkribus.core.model.beans.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.transkribus.core.model.beans.enums.EditStatus;
import eu.transkribus.core.model.beans.enums.ScriptType;

/**
 * 
 * Adapter for correct (un)marshalling of enum types
 * 
 * Needed for Jersey 2.x
 * 
 * @author philip
 * 
 */
public class EnumAdapter extends XmlAdapter<String, Enum<?>> {
	
	private Class<?>[] enumClasses = {ScriptType.class, EditStatus.class};

	@Override
	public String marshal(Enum<?> v) throws Exception {
		String result = null;
		
		if (v != null) {
			System.out.println("EnumAdapter marshall: " + v.toString());
			result = v.toString();
		} else {
			System.out.println("EnumAdapter marshall: null.");
		}
		
		return result;
	}

	@Override
	public Enum<?> unmarshal(String v) throws Exception {
		Enum<?> result = null;
		
		System.out.println("EnumAdapter unmarshall String: " + v);
		
		for(Class enumClass : enumClasses) {
            try {
                result = (Enum) Enum.valueOf(enumClass, v);
            } catch(IllegalArgumentException  e) {
            }
        }
        		
		return result;
	}

}