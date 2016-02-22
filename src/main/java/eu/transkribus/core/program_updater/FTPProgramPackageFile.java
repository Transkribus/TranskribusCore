package eu.transkribus.core.program_updater;

import org.apache.commons.net.ftp.FTPFile;

public class FTPProgramPackageFile extends ProgramPackageFile {
	public final FTPFile f;
	public final String path;
	public final String version;
	
	public FTPProgramPackageFile(FTPFile f, String path) {
		this.f = f;
		this.path = path;
		this.version = stripVersion(f.getName());
	}

//	public String toString() {
//	    final String TAB = ", ";
//	    String retValue = "MyFTPFile (filename = " + this.f.getName();
//		retValue += TAB + "path = " + this.path;
//		retValue += TAB + "version = " + this.version;
//		retValue += ")";
//	    return retValue;
//	}
	
	public String getVersion() {
		return stripVersion(f.getName());
	}

	@Override public String getName() {
		return f.getName();
	}

	@Override public String getTimestamp() {
		return ProgramPackageFile.DATE_FORMAT.format(f.getTimestamp().getTime());
	}
}