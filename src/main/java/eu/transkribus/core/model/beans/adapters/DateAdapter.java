package eu.transkribus.core.model.beans.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 
 * Adapter for correct (un)marshalling of java.util.Date values with Jersey
 * 
 * @author philip
 *
 */
public class DateAdapter extends XmlAdapter<String, Date> {
	
    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public String marshal(Date v) throws Exception {
    	String result = null;
    	if(v==null) {
//    		System.out.println("DateAdapter marshall Date: date is null.");
    	} else {
    		result = dateFormat.format(v);
//    		System.out.println("DateAdapter marshall Date: " + v.toGMTString());
    	}
        return result;
    }

    @Override
    public Date unmarshal(String v) throws Exception {
    	Date result = null;
    	if(v != null) {
//        	System.out.println("DateAdapter.unmarshall String: " + v);
    		result = dateFormat.parse(v);
    	}
        return result;
    }

}