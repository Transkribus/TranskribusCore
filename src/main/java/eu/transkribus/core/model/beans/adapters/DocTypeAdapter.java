package eu.transkribus.core.model.beans.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import eu.transkribus.core.model.beans.enums.DocType;

/**
 * Adapter for correct (un)marshalling of enum ScriptType
 * 
 * Needed for Jersey 2.x
 * 
 * @author philip
 * 
 */
public class DocTypeAdapter extends XmlAdapter<String, DocType> {
	@Override
	public String marshal(DocType v) throws Exception {
		String result = null;
		if (v != null) {
			result = v.toString();
		}
		return result;
	}

	@Override
	public DocType unmarshal(String v) throws Exception {
		DocType result = null;
		if (v != null) {
			result = DocType.valueOf(v);
		}
		return result;
	}
}
