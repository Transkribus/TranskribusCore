package eu.transkribus.core.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

import org.apache.commons.beanutils.PropertyUtils;

//import org.eclipse.swt.SWT;

/**
 * Implementation of {@link Comparator} to compare POJO.
 * 
 */
@SuppressWarnings("rawtypes")
public class BeanComparator implements Comparator {

	/** property name used to sort **/
	private final String sortPropertyName;
	/** the sort direction **/
	boolean up=false;
	
	public BeanComparator(String sortPropertyName) {
		this(sortPropertyName, false);
	}

	public BeanComparator(String sortPropertyName, boolean up) {
		this.sortPropertyName = sortPropertyName;
		this.up = up;
	}

	public int compare(Object o1, Object o2) {
//		if ((o1 instanceof Comparable) && (o2 instanceof Comparable)) {
//			// Compare simple type like String, Integer etc
//			Comparable c1 = ((Comparable) o1);
//			Comparable c2 = ((Comparable) o2);
//			return compare(c1, c2);
//		}

		try {
			o1 = PropertyUtils.getProperty(o1, sortPropertyName);
			o2 = PropertyUtils.getProperty(o2, sortPropertyName);
			
			if (o1 == null && o2 == null) {
				return 0;
			}
			else if (o1 == null && o2 != null) {
				return -1;
			} else if (o1 != null && o2 == null) {
				return 1;
			}
			
			if ((o1 instanceof Comparable) && (o2 instanceof Comparable)) {
				// Compare simple type like String, Integer etc
				Comparable c1 = ((Comparable) o1);
				Comparable c2 = ((Comparable) o2);
				return compare(c1, c2);
			}			
		} 
		catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return 0;
		}

		return 0;
	}

	private int compare(Comparable c1, Comparable c2) {
		if (up) {
			return c2.compareTo(c1);
		}
		return c1.compareTo(c2);
	}

}
