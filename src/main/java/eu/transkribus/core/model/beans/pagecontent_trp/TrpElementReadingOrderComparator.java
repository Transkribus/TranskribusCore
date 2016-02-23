package eu.transkribus.core.model.beans.pagecontent_trp;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrpElementReadingOrderComparator<T> implements Comparator<T> {
	final static Logger logger = LoggerFactory.getLogger(TrpElementReadingOrderComparator.class);
	boolean compareByCoordinatesIfEqualReadingOrder=true;
	
	public TrpElementReadingOrderComparator(boolean compareByCoordinatesIfEqualReadingOrder) {
		this.compareByCoordinatesIfEqualReadingOrder = compareByCoordinatesIfEqualReadingOrder;
	}
	
	@Override public int compare(T o1, T o2) {
//		if (!(o1 instanceof ITrpShapeType) || !(o2 instanceof ITrpShapeType)) {
//			return 0;
//		}
		
		Integer ro1 = o1 instanceof ITrpShapeType ? ((ITrpShapeType) o1).getReadingOrder() : null;
		Integer ro2 = o2 instanceof ITrpShapeType ? ((ITrpShapeType) o2).getReadingOrder() : null;
		
//		Integer id1 = o1 instanceof ITrpShapeType ? ((ITrpShapeType) o1).get : null;
//		Integer id2 = o2 instanceof ITrpShapeType ? ((ITrpShapeType) o2).getReadingOrder() : null;
		
		if (ro1 == null && ro2  != null) {
			return 1;
		}
		else if (ro1 != null && ro2 == null) {
			return -1;
		}
		else if (ro1 == null && ro2 == null) {
			if (compareByCoordinatesIfEqualReadingOrder){
				//logger.debug("ro1 == null && ro2 == null");
				return new TrpElementCoordinatesComparator().compare(o1, o2);
			}
			else
				return 0;
		}
		else {
//			logger.debug("ro1 && ro2 != null");
//			logger.debug("o1 id " + ((ITrpShapeType) o1).getId() + " reading order " + ro1);
//			logger.debug("o2 id " + ((ITrpShapeType) o2).getId() + " reading order " + ro2);
						
			int c = Integer.compare(ro1, ro2);
//			if (c==0 && compareByCoordinatesIfEqualReadingOrder) {
//				//if the reading order is equal we want to take the first value as smaller one because inserting  into the reading order
//				//means to shift all values one step to the right
//				logger.debug("equal reading order - take first (o1) as smaller one");
//				logger.debug("o1 id " + ((ITrpShapeType) o1).getId() + " reading order " + ro1);
//				logger.debug("o2 id " + ((ITrpShapeType) o2).getId() + " reading order " + ro2);
//				return -1;
//				//return new TrpElementCoordinatesComparator().compare(o1, o2);
//			} else
				return c;
		}
	}
}
