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

import org.dea.fimagestore.core.FImagestoreConst;
import org.dea.fimagestore.core.client.IFImagestoreConfig;
import org.dea.fimgstoreclient.AbstractHttpClient.Scheme;
import org.dea.fimgstoreclient.utils.FimgStoreUriBuilder;

import io.swagger.v3.oas.annotations.Hidden;

@Entity
@Table(name="FIMGSTORE")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpFImagestore implements Serializable, IFImagestoreConfig {
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	@XmlTransient
	@Hidden
	private Integer storeId;
	@Column(name="host")
	private String hostName;
	@Column
	private String context = null;
	@Column
	private Integer port = null;
	@Column
	@XmlTransient
	@Hidden
	private String username = null;
	@Column
	@XmlTransient
	@Hidden
	private String password = null;
	@Column(name="STORE_LOCATION")
	@XmlTransient
	@Hidden
	private String storeLocation = null;
	
	@XmlTransient
	@Hidden
	private String fimagestoreGetUrlBase = null;
	
	public TrpFImagestore(){}
	
	/**
	 * Copy constructor
	 * 
	 * @param config the config to copy field values from
	 */
	public TrpFImagestore(TrpFImagestore config) {
		this();
		this.storeId = config.storeId;
		this.hostName = config.hostName;
		this.context = config.context;
		this.port = config.port;
		this.username = config.username;
		this.password = config.password;
		this.storeLocation = config.storeLocation;
		this.fimagestoreGetUrlBase = config.fimagestoreGetUrlBase;
	}


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
	
	/**
	 * Get FImagestore GET base URL including the "?id=" parameter.
	 */
	public String getFImagestoreGetUrlBase() {
		if(fimagestoreGetUrlBase == null) {
			FimgStoreUriBuilder builder = new FimgStoreUriBuilder(Scheme.https.toString(), getHostName(), null, getContext());
			fimagestoreGetUrlBase = builder.getBaseGetUri() + "?" + FImagestoreConst.ID_PARAM + "=";
		}
		return fimagestoreGetUrlBase;
	}
	
	@Override
	public String toString() {
		return "TrpFImagestore [storeId=" + storeId + ", hostName=" + hostName + ", context=" + context + ", port=" + port
				+ ", username=" + username + ", password=" + (password == null ? "null" : "[is set from DB]") 
				+ ", storeLocation=" + storeLocation + "]";
	}
	
}
