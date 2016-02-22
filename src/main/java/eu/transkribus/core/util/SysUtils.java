package eu.transkribus.core.util;

public class SysUtils {
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
}
