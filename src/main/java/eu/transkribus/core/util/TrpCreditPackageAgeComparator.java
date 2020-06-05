package eu.transkribus.core.util;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.model.beans.TrpCreditPackage;

/**
 * Compares TrpTokenCredit objects by purchaseDate. Older Credits are ranked first. purchaseDate null values are considered oldest.
 */
public class TrpCreditPackageAgeComparator implements Comparator<TrpCreditPackage> {
	private static final Logger logger = LoggerFactory.getLogger(TrpCreditPackageAgeComparator.class);
	
	@Override
	public int compare(TrpCreditPackage o1, TrpCreditPackage o2) {
		//-1 => o1 is older
		//0 => age is equal
		//1 => o1 is younger
		final int result;
		if(o1.getPurchaseDate() == null && o2.getPurchaseDate() == null) {
			result = 0;
		} else if(o1.getPurchaseDate() == null && o2.getPurchaseDate() != null) {
			result = -1;
		} else if(o1.getPurchaseDate() != null && o2.getPurchaseDate() == null) {
			result = 1;
		} else if(o1.getPurchaseDate().equals(o2.getPurchaseDate())) {
			result = 0;
		} else if(o1.getPurchaseDate().before(o2.getPurchaseDate())) {
			result = -1;
		} else {
			result = 1;
		}
		logger.debug("Comparing credit purchase dates: 1 = {} <-> 2 = {} => result = {}", 
				o1.getPurchaseDate(), o2.getPurchaseDate(), result);
		return result;
	}
}