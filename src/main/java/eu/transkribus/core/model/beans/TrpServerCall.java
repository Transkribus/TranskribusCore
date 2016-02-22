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
@Table(name="SERVER_CALLS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpServerCall {
	@Id
	@Column(name="ID")
	private int id;
	@Column
	private String ip;
	@Column
	private String useragent;
	
	@Column
	private int user_id;
	@Column
	private String call;
	@Column
	private String description;
	@Column
	private String session_id;
	@Column
	private Timestamp time;
	
	public TrpServerCall() {
	}
	
	public TrpServerCall(String ip, String useragent, int user_id, String call, String description, String session_id) {
		super();
		this.id = 0; // gets set by trigger on DB
		this.ip = ip;
		this.useragent = useragent;
		this.user_id = user_id;
		this.call = call;
		this.description = description;
		this.session_id = session_id;
		this.time = new Timestamp(System.currentTimeMillis()); // FIXME: currently gets set by database trigger; if set here, no time is stored (only date!!)
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getUseragent() {
		return useragent;
	}
	public void setUseragent(String useragent) {
		this.useragent = useragent;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getCall() {
		return call;
	}
	public void setCall(String call) {
		this.call = call;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}

}
