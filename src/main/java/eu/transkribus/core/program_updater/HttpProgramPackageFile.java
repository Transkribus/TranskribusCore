package eu.transkribus.core.program_updater;

import java.io.File;
import java.io.Serializable;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HttpProgramPackageFile extends ProgramPackageFile implements Serializable {
	private static final long serialVersionUID = 1421504966202208787L;
	String name, version, timestamp;
	
	/** HAS TO BE THERE FOR SERIALIZATION!! */
	public HttpProgramPackageFile() {	
	}
	
	public HttpProgramPackageFile(File f) {
		this.name = f.getName();
		this.version = stripVersion(name);
		this.timestamp = DATE_FORMAT.format(f.lastModified());
	}
	
	public HttpProgramPackageFile(File f, Properties buildInfo) {
		this(f);

		if (buildInfo.getProperty("Version") != null)
			this.version = buildInfo.getProperty("Version");
		
		if (buildInfo.getProperty("Date") != null)
			this.timestamp = buildInfo.getProperty("Date");
	}
	
	public HttpProgramPackageFile(String name, String version, String timestamp) {
		this.name = name;
		this.version = stripVersion(name);
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
