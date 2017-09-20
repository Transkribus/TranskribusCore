package eu.transkribus.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;


/** A simple stopwatch */
public class SebisStopWatch {
	public static final SebisStopWatch SW = new SebisStopWatch(); 
	
	long start = 0, diff = 0;
	String name="";
	
	TimeUnit timeUnitGranularity = TimeUnit.SECONDS;
	
	long diffMs = 0;
	long diffSeconds = 0;
	long diffMinutes = 0;
	long diffHours = 0;
	long diffDays = 0;
	String timeStr="";
		
	public SebisStopWatch() {
		start = System.currentTimeMillis(); diff = 0;
	}
	
	public SebisStopWatch(String name) {
		this();
		this.name = name;
	}
	
	public SebisStopWatch(SebisStopWatch ssw) {
		this.start = ssw.start;
		this.diff = ssw.diff;
	}
		
	public long start() {
		start = System.currentTimeMillis();
		return start;
	}
	
	public long stop() {
		return stop(true);
	}
	
	public long stop(boolean verbose) {
		return stop(verbose, "Elapsed time: ", null);
	}
	
	public long stop(boolean verbose, String verbosePrefix) {
		return stop(verbose, verbosePrefix, null);
	}
		
	public long stop(boolean verbose, String verbosePrefix, Logger logger) {
		diff = System.currentTimeMillis() - start;
		calcDiffs();
		
		if (verbose) {
			String formatStr = (name==null || name.isEmpty()) ? verbosePrefix : name+" - "+verbosePrefix+": ";		
			
			String outStr = formatStr+timeStr+"\n";
			if (logger==null)
				System.out.printf(outStr);
			else
				logger.debug(outStr);
//				logger.log(logger.getLevel(), outStr);
		}
		
		return diff;
	}
	
	protected void calcDiffs() {
		if (timeUnitGranularity == TimeUnit.DAYS) {
			diffMs = diff % 1000;
			diffSeconds = diff/1000%60;
			diffMinutes = diff/(60*1000) % 60;
			diffHours = diff / (60 * 60 * 1000) % 24;
			diffDays = diff / (24 * 60 * 60 * 1000);
			
			timeStr = String.format("%d:%02d:%02d:%02d.%03d", diffDays, diffHours, diffMinutes, diffSeconds, diffMs);
		} else if (timeUnitGranularity == TimeUnit.HOURS) {
			diffMs = diff % 1000;
			diffSeconds = diff/1000%60;
			diffMinutes = diff/(60*1000) % 60;
			diffHours = diff / (60 * 60 * 1000);
			diffDays = 0;
			
			timeStr = String.format("%d:%02d:%02d.%03d", diffHours, diffMinutes, diffSeconds, diffMs);
		} else if (timeUnitGranularity == TimeUnit.MINUTES) {
			diffMs = diff % 1000;
			diffSeconds = diff/1000%60;
			diffMinutes = diff/(60*1000);
			diffHours = 0;
			diffDays = 0;
			
			timeStr = String.format("%d:%02d.%03d", diffMinutes, diffSeconds, diffMs);
		} else if (timeUnitGranularity == TimeUnit.SECONDS) {
			diffMs = diff % 1000;
			diffSeconds = diff/1000;
			diffMinutes = 0;
			diffHours = 0;
			diffDays = 0;
			
			timeStr = String.format("%d.%03d", diffSeconds, diffMs);
		} else {
			diffMs = diff;
			diffSeconds = 0;
			diffMinutes = 0;
			diffHours = 0;
			diffDays = 0;
			
			timeStr = String.format("%d", diffMs);
		}
	}
	
	public long restart(boolean verbose) {
		stop(verbose);
		return start();
	}
	
	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getDiff() {
		return diff;
	}

	public void setDiff(long diff) {
		this.diff = diff;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getDiffMs() {
		return diffMs;
	}

	public long getDiffSeconds() {
		return diffSeconds;
	}

	public long getDiffMinutes() {
		return diffMinutes;
	}

	public long getDiffHours() {
		return diffHours;
	}

	public long getDiffDays() {
		return diffDays;
	}
	
	public String getTimeStr() {
		return timeStr;
	}
	
	public TimeUnit getTimeUnitGranularity() {
		return timeUnitGranularity;
	}

	public void setTimeUnitGranularity(TimeUnit timeUnitGranularity) {
		this.timeUnitGranularity = timeUnitGranularity;
	}

	public static void main(String [] args) {
		SebisStopWatch ssw1 = new SebisStopWatch("1");
		ssw1.setTimeUnitGranularity(TimeUnit.MINUTES);
		
		SebisStopWatch ssw2 = new SebisStopWatch("2");
		SebisStopWatch ssw3 = new SebisStopWatch("3");
//		ssw3.setDateFormat("ss:SSS");
		
		ssw1.start();
		ssw2.start();
//		ssw3.start();
		
		List<Long> times = new ArrayList<Long>();
		for (int i=0; i<100; ++i) {
			try {
//				Thread.sleep(i*10);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long t = ssw1.stop(true);
//			times.add(t);
//			ssw2.restart(true);
//			ssw3.restart(true);
		}

		ssw1.stop(true);
		for (Long l : times) {
			System.out.println("d = "+l);
		}
		
		
		
	}
	
	/**
	 * SebisStopWatch for lazy people...
	 */
	public static class SSW extends SebisStopWatch {
		public static final SSW SW = new SSW();
		public long stop(String verbosePrefix, Logger logger) {
			return stop(true, verbosePrefix, logger);
		}
	}
}
