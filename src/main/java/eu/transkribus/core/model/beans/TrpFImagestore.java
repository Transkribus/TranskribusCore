package eu.transkribus.core.model.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="FIMGSTORE")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpStore implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column
	private int storeid;
	@Column(name="host")
	private String hostName;
	@Column
	private String context = null;
	@Column
	private Integer port = null;
	@Column
	private String userName = null;
	@Column
	private String passWord = null;
	
	public TrpStore(){}
	
	
	public int getStoreid() {
		return storeid;
	}


	public void setStoreid(int storeid) {
		this.storeid = storeid;
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

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer(this.getClass().getSimpleName() + " {");
		sb.append(this.getStoreid() + " - ");
		sb.append(this.getHostName());
		sb.append((this.getPort() != null) ? ":" + this.getPort() : "");
		sb.append((this.getContext() != null) ? "/" + this.getContext() + " }" : "/ }");
		return sb.toString();
	}
}
