package eu.transkribus.core.util;

import java.lang.reflect.Field;

import org.apache.poi.hdgf.pointers.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysUtils {	
	private final static Logger logger = LoggerFactory.getLogger(SysUtils.class);
	
	public static String getOSName() {
		String osNameProperty = System.getProperty("os.name");

		if (osNameProperty == null) {
			throw new RuntimeException("os.name property is not set");
		} else {
			osNameProperty = osNameProperty.toLowerCase();
		}

		if (osNameProperty.contains("win")) {
			return "win";
		} else if (osNameProperty.contains("mac")) {
			return "osx";
		} else if (osNameProperty.contains("linux") || osNameProperty.contains("nix")) {
			return "linux";
		} else {
			throw new RuntimeException("Unknown OS name: " + osNameProperty);
		}
	}
	
	public static String getArch() {
		return System.getProperty("os.arch");
	}

	public static String getArchName() {
		String osArch = getArch();

		if (osArch != null && osArch.contains("64")) {
			return "64";
		} else {
			return "32";
		}
	}	

	public static boolean isWin() {
		return getOSName().equals("win");
	}
	
	public static boolean isOsx() {
		return getOSName().equals("osx");
	}
	
	public static boolean isLinux() {
		return getOSName().equals("linux");
	}
	
	
	public static Long processId(Process process) {
		if (isLinux() || isOsx()) {
			return unixLikeProcessId(process);
		} 
//		else if (isWin()) {
//			return windowsProcessId(process);
//		}
		else {
			throw new RuntimeException("Unsuppored operating system while retrieving process id: "+getOSName());
		}
	}

	private static Long unixLikeProcessId(Process process) {
		Class<?> clazz = process.getClass();
		try {
			if (clazz.getName().equals("java.lang.UNIXProcess")) {
				Field pidField = clazz.getDeclaredField("pid");
				pidField.setAccessible(true);
				Object value = pidField.get(process);
				if (value instanceof Integer) {
					logger.trace("Detected pid: {}", value);
					return ((Integer) value).longValue();
				}
			}
		} catch (SecurityException sx) {
			sx.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

//	/**
//	 * @see http://www.golesny.de/p/code/javagetpid
//	 *
//	 * @return
//	 */
//	private static Long windowsProcessId(Process process) {
//		if (process.getClass().getName().equals("java.lang.Win32Process")
//				|| process.getClass().getName().equals("java.lang.ProcessImpl")) {
//			/* determine the pid on windows plattforms */
//			try {
//				Field f = process.getClass().getDeclaredField("handle");
//				f.setAccessible(true);
//				long handl = f.getLong(process);
//
//				Kernel32 kernel = Kernel32.INSTANCE;
//				WinNT.HANDLE handle = new WinNT.HANDLE();
//				handle.setPointer(Pointer.createConstant(handl));
//				int ret = kernel.GetProcessId(handle);
//				logger.debug("Detected pid: {}", ret);
//				return Long.valueOf(ret);
//			} catch (Throwable e) {
//				e.printStackTrace();
//			}
//		}
//		return null;
//	}	
}
