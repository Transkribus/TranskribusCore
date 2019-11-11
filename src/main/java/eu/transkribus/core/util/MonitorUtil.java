package eu.transkribus.core.util;

import org.eclipse.core.runtime.IProgressMonitor;

public class MonitorUtil {
	public static void beginTask(IProgressMonitor monitor, String name, int totalWork) {
		if (monitor != null) {
			monitor.beginTask(name, totalWork);
		}
	}
	
	public static void done(IProgressMonitor monitor) {
		if (monitor != null) {
			monitor.done();
		}
	}
	
	public static boolean isCanceled(IProgressMonitor monitor) {
		if (monitor != null) {
			return monitor.isCanceled();
		}
		return false;
	}
	
	public static void setTaskName(IProgressMonitor monitor, String name) {
		if (monitor != null) {
			monitor.setTaskName(name);
		}
	}
	
	public static void subTask(IProgressMonitor monitor, String name) {
		if (monitor != null) {
			monitor.subTask(name);
		}
	}
	
	public static void worked(IProgressMonitor monitor, int progress) {
		if (monitor != null) {
			monitor.worked(progress);
		}
	}	
}
