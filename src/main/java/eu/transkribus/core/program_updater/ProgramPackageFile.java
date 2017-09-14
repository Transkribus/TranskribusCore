package eu.transkribus.core.program_updater;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import eu.transkribus.core.program_updater.ProgramPackageFile.ProgramPackageProperties;

public abstract class ProgramPackageFile {
	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd_MM_yyyy_HH:mm");
	public final static String SNAPSHOT_SUFFIX = "-SNAPSHOT";
	public final static String SNAPSHOT_KEYWORD = "SNAPSHOT";
	public final static String BUILD_FN = "BUILD.txt";
	
	public static final String VERSION_PROP = "Version";
	public static final String DATE_PROP = "Date";
	
	public static class ProgramPackageProperties extends Properties {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3398461193268583770L;
		public String getVersion() { return getProperty(VERSION_PROP); }
		public String getDate() { return getProperty(DATE_PROP); }
	}
	
	public static ProgramPackageProperties parseBuildProperties(File f) throws IOException {
		try (ZipFile zf = new ZipFile(f)) {
			Enumeration<? extends ZipEntry> entries = zf.entries();
			while (entries.hasMoreElements()) {
				ZipEntry ze = (ZipEntry) entries.nextElement();
				if (ze.getName().endsWith(ProgramPackageFile.BUILD_FN)) {
					ProgramPackageProperties p = new ProgramPackageProperties();
					p.load(zf.getInputStream(ze));
					
					return p;
				}
			}
		}
		
		throw new IOException("File not found: "+f.getName());
	}
	
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
//		return !getVersion().endsWith(SNAPSHOT_SUFFIX);
		return !getVersion().contains(SNAPSHOT_KEYWORD);
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