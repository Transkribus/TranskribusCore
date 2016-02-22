package eu.transkribus.core.program_updater;

import java.text.SimpleDateFormat;

public abstract class ProgramPackageFile {
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd_MM_yyyy_HH:mm");
	public final static String SNAPSHOT_SUFFIX = "-SNAPSHOT";
	public final static String BUILD_FN = "BUILD.txt";
	
	public static String stripVersion(String trpPackageFileName) {
		int i1 = trpPackageFileName.indexOf("-")+1;
		int i2 = trpPackageFileName.lastIndexOf("-");
		
		try {
			return trpPackageFileName.substring(i1, i2);
		} catch (IndexOutOfBoundsException ie) {
			return null;
		}
	}
	
	public boolean isReleaseVersion() { 
		return !getVersion().endsWith(SNAPSHOT_SUFFIX);
	};
	
	public abstract String getName();
	public abstract String getTimestamp();
	public abstract String getVersion();
	
	@Override
	public String toString() {
		String str = "Filename="+getName()+"\n";
		str += "Version="+getVersion()+"\n";
		str += "Date="+getTimestamp()+"\n";
		
		return str;
	}	
}