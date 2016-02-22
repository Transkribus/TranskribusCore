package eu.transkribus.core.model.beans.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.transkribus.core.model.beans.enums.EditStatus;

/**
 * 
 * Adapter for correct (un)marshalling of enum EditStatus
 * 
 * Needed for Jersey 2.x
 * 
 * @author philip
 * 
 */
public class EditStatusAdapter extends XmlAdapter<String, EditStatus> {

	@Override
	public String marshal(EditStatus v) throws Exception {
		String result = null;
		
		if (v != null) {
//			System.out.println("EditStatusAdapter marshall: " + v.toString());
			result = v.toString();
		} else {
//			System.out.println("EditStatusAdapter marshall: type is null.");
		}
		
		return result;
	}

	@Override
	public EditStatus unmarshal(String v) throws Exception {
		EditStatus result = null;
		
//		System.out.println("EditStatusAdapter.unmarshall String: " + v);
		
		if(v != null){
			result = EditStatus.valueOf(v);
		}
		
		return result;
	}

}