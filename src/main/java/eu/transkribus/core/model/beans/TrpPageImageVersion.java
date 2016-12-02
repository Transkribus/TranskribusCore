package eu.transkribus.core.model.beans;

import java.io.Serializable;
import java.net.URL;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "PAGE_IMAGE_VERSIONS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpPageImageVersion implements Serializable, ITrpFile {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column
	private int page_image_versions_id = -1; // doesn't need to get set, gets set by trigger using sequence on insert; if set > 0, then it will try to use that in DB!
	@Column
	private int pageId;
	@Column
	private Integer tsid;
	@Column
	private String type;
	@Column
	private String description;
	@Column
	private Double translation_x;
	@Column
	private Double translation_y;
	@Column
	private Double scaling_x;
	@Column
	private Double scaling_y;
	@Column
	private Double rotation;
	
	@Column(name="imagekey")
	private String key;
	
	@Transient
	private URL url;
	
	private String md5Sum;
	
	@Column
	private Timestamp created = new Timestamp(System.currentTimeMillis());
	
	public TrpPageImageVersion() {
	}
	
	public TrpPageImageVersion(int pageid, Integer tsid, String type, String description, String imageKey) {
		super();
		this.pageId = pageid;
		this.tsid = tsid;
		this.type = type;
		this.description = description;
		this.key = imageKey;
	}

	public TrpPageImageVersion(int pageid, Integer tsid, String type, String description, Double translation_x, Double translation_y,
			Double scaling_x, Double scaling_y, Double rotation, String imageKey) {
		super();
		this.pageId = pageid;
		this.tsid = tsid;
		this.type = type;
		this.description = description;
		this.translation_x = translation_x;
		this.translation_y = translation_y;
		this.scaling_x = scaling_x;
		this.scaling_y = scaling_y;
		this.rotation = rotation;
		this.key = imageKey;
	}
	
	
	public TrpPageImageVersion(TrpPageImageVersion v) {
		this();
		page_image_versions_id = v.getPage_image_versions_id();
		pageId = v.getPageId();
		tsid = v.getTsid();
		type = v.getType();
		description = v.getDescription();
		translation_x = v.getTranslation_x();
		translation_y = v.getTranslation_y();
		scaling_x = v.getScaling_x();
		scaling_y = v.getScaling_y();
		rotation = v.getRotation();
		key = v.getKey();
		url = v.getUrl();
		md5Sum = v.getMd5Sum();
		created = v.getCreated();
	}

	public int getPage_image_versions_id() {
		return page_image_versions_id;
	}
	public void setPage_image_versions_id(int page_image_versions_id) {
		this.page_image_versions_id = page_image_versions_id;
	}

	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	
	public Integer getTsid() {
		return tsid;
	}

	public void setTsid(Integer tsid) {
		this.tsid = tsid;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getTranslation_x() {
		return translation_x;
	}
	public void setTranslation_x(Double translation_x) {
		this.translation_x = translation_x;
	}
	public Double getTranslation_y() {
		return translation_y;
	}
	public void setTranslation_y(Double translation_y) {
		this.translation_y = translation_y;
	}
	public Double getScaling_x() {
		return scaling_x;
	}
	public void setScaling_x(Double scaling_x) {
		this.scaling_x = scaling_x;
	}
	public Double getScaling_y() {
		return scaling_y;
	}
	public void setScaling_y(Double scaling_y) {
		this.scaling_y = scaling_y;
	}
	public Double getRotation() {
		return rotation;
	}
	public void setRotation(Double rotation) {
		this.rotation = rotation;
	}

	@Override public String getKey() {
		return key;
	}

	@Override public void setKey(String key) {
		this.key = key;
	}
	

	@Override public URL getUrl() {
		return url;
	}

	@Override public void setUrl(URL url) {
		this.url = url;
	}
	
	@Override public String getMd5Sum() {
		return md5Sum;
	}

	@Override public void setMd5Sum(String md5Sum) {
		this.md5Sum = md5Sum;
	}
	
	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	@Override public String toString() {
		return "TrpPageImageVersion [page_image_versions_id=" + page_image_versions_id + ", pageId=" + pageId + ", tsid=" + tsid + ", type=" + type
				+ ", description=" + description + ", translation_x=" + translation_x + ", translation_y=" + translation_y + ", scaling_x=" + scaling_x
				+ ", scaling_y=" + scaling_y + ", rotation=" + rotation + ", key=" + key + ", url=" + url + ", md5Sum=" + md5Sum + ", created=" + created + "]";
	}

	
}
