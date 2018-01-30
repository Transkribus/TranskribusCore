package eu.transkribus.core.util;

import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.transkribus.core.util.LogUtil.Level;

public class SysResourcesUtil {
	
	public static final DecimalFormat DEC_FORMAT = new DecimalFormat("0.000");
	
	private static final double MIB_DIV = 1024 * 1024f;

	private SysResourcesUtil() {}

	public static void logMemUsage(final Logger logger, final Level lvl, final boolean inMiB) {
		LogUtil.log("Max mem" + (inMiB ? " (MiB): " : ": ") +  getMaxMemStr(inMiB), logger, lvl);
		if(inMiB) {
			LogUtil.log("Free mem (MiB): " + DEC_FORMAT.format(getFreeMem() / MIB_DIV), logger, lvl);
			LogUtil.log("Total mem (MiB): " +  DEC_FORMAT.format(getTotalMem() / MIB_DIV), logger, lvl);
			LogUtil.log("Used mem (MiB): " +  DEC_FORMAT.format(getUsedMem() / MIB_DIV), logger, lvl);
		} else {
			LogUtil.log("Free mem: " + getFreeMem(), logger, lvl);
			LogUtil.log("Total mem: " + getTotalMem(), logger, lvl);
			LogUtil.log("Used mem: " + getUsedMem(), logger, lvl);
		}
		
	}
	
	public static long getFreeMem() {
		return Runtime.getRuntime().freeMemory();
	}
	
	public static long getTotalMem() {
		return Runtime.getRuntime().totalMemory();
	}
	
	public static long getMaxMem() {
		return Runtime.getRuntime().maxMemory();
	}
	
	public static long getUsedMem() {
		return getTotalMem() - getFreeMem();
	}
	
	public static String getMaxMemStr(boolean inMiB) {
		final long maxMem = getMaxMem();
		if(maxMem == Long.MAX_VALUE) {
			return "No limit set"; 
		} else if(inMiB) {
			return DEC_FORMAT.format(getMaxMem() / MIB_DIV);
		} else {
			return ""+getMaxMem();
		}
	}

	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(SysResourcesUtil.class);
		SysResourcesUtil.logMemUsage(logger, Level.INFO, false);
		SysResourcesUtil.logMemUsage(logger, Level.INFO, true);
	}
}
