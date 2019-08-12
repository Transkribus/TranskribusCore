package eu.transkribus.core.util;

import java.util.Calendar;

public class DateUtils {
	/**
	 * Get the remaining seconds until the next instance of hour:minute:second
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static long getSecondsTill(final int hour, final int minute, final int second){ 
		long result = 0;
		Calendar now = Calendar.getInstance();
		final int currSecOfDay = (now.get(Calendar.HOUR_OF_DAY) * 3600) 
				+ (now.get(Calendar.MINUTE) * 60) + now.get(Calendar.SECOND);
		final int targetSecOfDay = (hour * 3600) + (minute * 60) + second;
		
		if(currSecOfDay == targetSecOfDay) {
			return result;
		} else if(currSecOfDay < targetSecOfDay) {
			result = targetSecOfDay - currSecOfDay;
		} else {
			//Fun fact: a day has 60*60*24 = 86400 seconds
			result = (86400 - currSecOfDay) + targetSecOfDay;
		}
		return result;
	}
}
