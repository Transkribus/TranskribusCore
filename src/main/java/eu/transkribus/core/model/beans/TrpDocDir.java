package eu.transkribus.core.model.beans;

import java.io.File;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpDocDir {
	private String name;
	@Deprecated
	private int nrOfImgs;
	@Deprecated
	private long size;
	private int nrOfFiles;
	private Date createDate;
	@Deprecated
	private TrpDocMetadata metadata;
	
	@XmlTransient
	private File docDir;

	public TrpDocDir() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Deprecated
	public int getNrOfImgs() {
		return nrOfImgs;
	}
	@Deprecated
	public void setNrOfImgs(int nrOfImgs) {
		this.nrOfImgs = nrOfImgs;
	}
	@Deprecated
	public long getSize() {
		return size;
	}
	@Deprecated
	public void setSize(long size) {
		this.size = size;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	@Deprecated
	public TrpDocMetadata getMetadata() {
		return metadata;
	}
	@Deprecated
	public void setMetadata(TrpDocMetadata metadata) {
		this.metadata = metadata;
	}

	public int getNrOfFiles() {
		return nrOfFiles;
	}

	public void setNrOfFiles(int nrOfFiles) {
		this.nrOfFiles = nrOfFiles;
	}
	
	public File getDocDir() {
		return docDir;
	}
	
	public void setDocDir(File docDir) {
		this.docDir = docDir;
	}
}
