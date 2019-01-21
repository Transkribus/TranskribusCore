package eu.transkribus.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysUtils {	
	private final static Logger logger = LoggerFactory.getLogger(SysUtils.class);
	
	public static final boolean IS_WINDOWS = isWin();
	public static final boolean IS_LINUX = isLinux();
	public static final boolean IS_OSX = isOsx();
	
	public static final String OS_NAME = getOSName();
	
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
	
	public static int getJavaMajorVersion() {
		String v = getJavaVersion();
		System.out.println("v = "+v);
		try {
			if (v.startsWith("1.")) { // java version <= 8
				return Integer.parseInt(v.split("\\.")[1]);
			} else {
				return Integer.parseInt(v.split("\\.")[0]);
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException("Could not parse java major version: "+e.getMessage(), e);
		}
	}
	
	public static String getJavaVersion() {
		return System.getProperty("java.version");
	}
	
	public static boolean isJavaVersionGreater8() {
		return !SysUtils.getJavaVersion().startsWith("1.");
	}
	
	public static boolean is64Bit() {
		return getArch().contains("64");
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

	public static File findFileInPath(String file, String... additionalPaths) throws FileNotFoundException {
		// first add the paths that are given:
		List<String> allPaths = new ArrayList<String>();
		allPaths.addAll(Arrays.asList(additionalPaths));
		// then add all paths from the system PATH:
		String paths = System.getenv("PATH");
		String pathSep = isWin() ? ";" : ":";
		allPaths.addAll(Arrays.asList(paths.split(pathSep)));
		// then search the path:
		for (String path : allPaths) {
			path = path.trim();
			File f = new File(path+"/"+file);
			if (f.exists() && f.isFile()) {
				return f;
			} else {
				logger.info("Could not find file '" + file + "' in path: " + path);
			}
		}
		throw new FileNotFoundException("Cannot find file: "+ file);
	}
	
	public static JavaInfo getJavaInfo() {
		final String javaArch = System.getProperty("sun.arch.data.model");
		final String version = System.getProperty("java.version");
		final String fileEnc = System.getProperty("file.encoding");
		
		String realArch;
		realArch = getArchName();
		if (SysUtils.isWin()) {
			String arch = System.getenv("PROCESSOR_ARCHITECTURE");
			String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

			realArch = arch != null && arch.endsWith("64")
			                  || wow64Arch != null && wow64Arch.endsWith("64")
			                      ? "64" : "32";
		} else if(SysUtils.isLinux()) {
			Process p;
			try {
				p = Runtime.getRuntime().exec("lscpu");
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));	
				realArch = br.readLine().contains("64") ? "64" : "32" ;
				logger.debug("line : "+realArch);
			} catch (Exception e) {
				logger.warn("Could not determine platform architecture!", e);
				realArch = "unknown";
			}
		} else {
			//TODO implement a check for mac
			realArch = getArchName(); // FIXME: @philipp: can this go wrong? and: why not use this for other OS too?
		}
		return new JavaInfo(javaArch, version, fileEnc, realArch);
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
	
	
	public static class JavaInfo {
		private final String javaArch;
		private final String version;
		private final String fileEnc;
		private final String systemArch;
		
		JavaInfo(String javaArch, String version, String fileEnc, String systemArch) {
			this.javaArch = javaArch;
			this.version = version;
			this.fileEnc = fileEnc;
			this.systemArch = systemArch;
		}

		public String getJavaArch() {
			return javaArch;
		}

		public String getVersion() {
			return version;
		}

		public String getFileEnc() {
			return fileEnc;
		}

		public String getSystemArch() {
			return systemArch;
		}
		
		public String toPrettyString() {
			return "Java version: " + version + "\n"
				 + "Java platform architecture: " + javaArch + "\n"
				 + "System architecture: " + systemArch + "\n"
				 + "File encoding: " + fileEnc;
		}
	}
}
