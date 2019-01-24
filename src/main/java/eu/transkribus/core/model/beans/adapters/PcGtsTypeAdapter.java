package eu.transkribus.core.model.beans.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.pagecontent.PcGtsType;
import eu.transkribus.core.util.PageXmlUtils;

public class PcGtsTypeAdapter extends XmlAdapter<String, PcGtsType> {
	private final static Logger logger = LoggerFactory.getLogger(PcGtsTypeAdapter.class);
	
	@Override
	public PcGtsType unmarshal(String v) throws Exception {
		logger.debug("unmarshalling PcGtsType string!");
		
		return PageXmlUtils.unmarshal(v);
	}

	@Override
	public String marshal(PcGtsType v) throws Exception {
		logger.debug("marshalling PcGtsType!");
		
		byte[] b = PageXmlUtils.marshalToBytes(v);
		return new String(b, "UTF-8");
	}

}
