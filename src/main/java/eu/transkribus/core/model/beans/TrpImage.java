package eu.transkribus.core.model.beans;

import java.io.Serializable;
import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "Images")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpImage implements Serializable {
	private static final long serialVersionUID = -359788414403771313L;
	//objid for parentDoc is ID in DB
	@Id
	@Column
	private int imageId = -1;
	@Column(name = "IMAGEKEY")
	private String key = null; //The fimagestore key for getting the image file
	//imageUrl represents a link to the local filesystem or the link to the fimagestore
	@Column(name="IMGFILENAME")
	private String imgFileName;
	@Column
	private int width;
	@Column
	private int height;
	@Transient
	private URL url;
	@Column
	private java.sql.Timestamp created;
	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getImgFileName() {
		return imgFileName;
	}
	public void setImgFileName(String imgFileName) {
		this.imgFileName = imgFileName;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public URL getUrl() {
		return this.url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public java.sql.Timestamp getCreated() {
		return created;
	}
	public void setCreated(java.sql.Timestamp created) {
		this.created = created;
	}
	@Override
	public String toString() {
		return "TrpImage [imageId=" + imageId + ", key=" + key + ", imgFileName=" + imgFileName + ", width=" + width
				+ ", height=" + height + ", url=" + url + ", created=" + created + "]";
	}

}
