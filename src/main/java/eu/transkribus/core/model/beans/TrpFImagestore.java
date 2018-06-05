package eu.transkribus.core.model.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name="FIMGSTORE")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpFImagestore implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	private Integer storeId;
	@Column(name="host")
	private String hostName;
	@Column
	private String context = null;
	@Column
	private Integer port = null;
	@Column
	@XmlTransient
	private String username = null;
	@Column
	@XmlTransient
	private String password = null;
	@XmlTransient
	@Column(name="STORE_LOCATION")
	private String storeLocation = null;
	
	public TrpFImagestore(){}
	
	
	public int getStoreId() {
		return storeId;
	}


	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}


	public String getHostName() {
		return hostName;
	}


	public void setHostName(String hostName) {
		this.hostName = hostName;
	}


	public String getContext() {
		return context;
	}


	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getPort() {
		return port;
	}


	public void setContext(String context) {
		this.context = context;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}

	public String getStoreLocation() {
		return storeLocation;
	}


	public void setStoreLocation(String storeLocation) {
		this.storeLocation = storeLocation;
	}
	
	@Override
	public String toString() {
		return "TrpFImagestore [storeId=" + storeId + ", hostName=" + hostName + ", context=" + context + ", port=" + port
				+ ", username=" + username + ", password=" + (password == null ? "null" : "[is set from DB]") + "]";
	}
	
}
