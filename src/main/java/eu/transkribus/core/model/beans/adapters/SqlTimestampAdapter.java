package eu.transkribus.core.model.beans.adapters;

import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlTimestampAdapter extends XmlAdapter<java.util.Date, java.sql.Timestamp> {
	private static final Logger logger = LoggerFactory.getLogger(SqlTimestampAdapter.class);

	@Override
	public Timestamp unmarshal(Date v) throws Exception {
		if (v == null)
			return null;
		
		logger.debug("unmarshalling timestamp: "+v);
		
		return new Timestamp(v.getTime());
	}

	@Override
	public Date marshal(Timestamp v) throws Exception {
		if (v == null)
			return null;
		
		logger.debug("marshalling timestamp: "+v);
		
		return v;
	}

}
