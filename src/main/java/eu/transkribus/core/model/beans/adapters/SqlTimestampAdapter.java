package eu.transkribus.core.model.beans.adapters;

import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class SqlTimestampAdapter extends XmlAdapter<java.util.Date, java.sql.Timestamp> {
	@Override
	public Timestamp unmarshal(Date v) throws Exception {
		if (v == null)
			return null;

		return new Timestamp(v.getTime());
	}

	@Override
	public Date marshal(Timestamp v) throws Exception {
		if (v == null)
			return null;
		
		return v;
	}

}
