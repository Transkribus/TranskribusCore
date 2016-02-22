package eu.transkribus.core.util;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Static wrapper methods for IProgressMonitor methods which check for a null input monitor and do nothing in this case.
 */
public class ProgressUtils {
	/**
	 * Starts a task with the given name and a total progress. If the total progress is unknown, set totalWork to -1
	 */
	public static void beginTask(IProgressMonitor monitor, String name, int totalWork) {
		if (monitor != null)
			monitor.beginTask(name, totalWork);
	}
	
	public static void worked(IProgressMonitor monitor, int worked) {
		if (monitor != null)
			monitor.worked(worked);
	}
	
	public static void setCanceled(IProgressMonitor monitor, boolean value) {
		if (monitor != null)
			monitor.setCanceled(value);
	}
	
	public static boolean isCanceled(IProgressMonitor monitor) {
		return monitor == null ? false : monitor.isCanceled();
	}	
	
	public static void subTask(IProgressMonitor monitor, String name) {
		if (monitor != null)
			monitor.subTask(name);
	}
	
	public static void setTaskName(IProgressMonitor monitor, String name) {
		if (monitor != null)
			monitor.setTaskName(name);
	}
}
