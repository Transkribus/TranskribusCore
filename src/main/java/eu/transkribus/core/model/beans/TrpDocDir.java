package eu.transkribus.core.model.beans;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpDocDir {
	private String name;
	private int nrOfImgs;
	@Deprecated
	private long size;
	private Date createDate;
	private TrpDocMetadata metadata;

	public TrpDocDir() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNrOfImgs() {
		return nrOfImgs;
	}

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

	public TrpDocMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(TrpDocMetadata metadata) {
		this.metadata = metadata;
	}
	
}
