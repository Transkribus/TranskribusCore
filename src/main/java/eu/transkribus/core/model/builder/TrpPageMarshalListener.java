package eu.transkribus.core.model.builder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.customtags.CustomTagUtil;
import eu.transkribus.core.model.beans.pagecontent_trp.TrpPageType;


public class TrpPageMarshalListener extends javax.xml.bind.Marshaller.Listener {
	private final static Logger logger = LoggerFactory.getLogger(TrpPageMarshalListener.class);
	
    public void beforeMarshal(Object source) {
    	if (source instanceof TrpPageType) {
    		CustomTagUtil.writeCustomTagsToPage((TrpPageType) source);
    	}
    	
//    	if (source instanceof TrpPageType) {
//    		CustomTagUtil.writeReadingOrderCustomTagsToPageFormat((TrpPageType) source);
//    	}
//    	if (source instanceof ITrpShapeType) {
//    		CustomTagUtil.writeCustomTagListToCustomTag((ITrpShapeType) source);
//    	}
    }

    public void afterMarshal(Object source) {
    	
    }
}
