package eu.transkribus.core.model.beans;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "P2PALAMODEL")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpP2PaLAModel {
	@Id @Column private int id;
	@Column private String name;
	@Column private String description;
	@Column	private Timestamp created;
	@Column private String path;
	@Column private String custom;
	@Column private Integer train_set_size;
	@Column private Integer val_set_size;
	@Column private Integer test_set_size;
	@Column private String struct_types;
	@Column private String merged_struct_types;
	@Column private String out_mode;
	
	public TrpP2PaLAModel() {
	}
	
	public TrpP2PaLAModel(int id, String name, String description, Timestamp created, String path, String custom,
			Integer train_set_size, Integer val_set_size, Integer test_set_size, String struct_types,
			String merged_struct_types, String out_mode) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.created = created;
		this.path = path;
		this.custom = custom;
		this.train_set_size = train_set_size;
		this.val_set_size = val_set_size;
		this.test_set_size = test_set_size;
		this.struct_types = struct_types;
		this.merged_struct_types = merged_struct_types;
		this.out_mode = out_mode;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCustom() {
		return custom;
	}

	public void setCustom(String custom) {
		this.custom = custom;
	}

	public Integer getTrain_set_size() {
		return train_set_size;
	}

	public void setTrain_set_size(Integer train_set_size) {
		this.train_set_size = train_set_size;
	}

	public Integer getVal_set_size() {
		return val_set_size;
	}

	public void setVal_set_size(Integer val_set_size) {
		this.val_set_size = val_set_size;
	}

	public Integer getTest_set_size() {
		return test_set_size;
	}

	public void setTest_set_size(Integer test_set_size) {
		this.test_set_size = test_set_size;
	}

	public String getStruct_types() {
		return struct_types;
	}

	public void setStruct_types(String struct_types) {
		this.struct_types = struct_types;
	}

	public String getMerged_struct_types() {
		return merged_struct_types;
	}

	public void setMerged_struct_types(String merged_struct_types) {
		this.merged_struct_types = merged_struct_types;
	}
	
	public String getOut_mode() {
		return out_mode;
	}

	public void setOut_mode(String out_mode) {
		this.out_mode = out_mode;
	}

	@Override
	public String toString() {
		return "TrpP2PaLAModel [id=" + id + ", name=" + name + ", description=" + description + ", created=" + created
				+ ", path=" + path + ", custom=" + custom + ", train_set_size=" + train_set_size + ", val_set_size="
				+ val_set_size + ", test_set_size=" + test_set_size + ", struct_types=" + struct_types
				+ ", merged_struct_types=" + merged_struct_types + ", out_mode=" + out_mode + "]";
	}
	
}
