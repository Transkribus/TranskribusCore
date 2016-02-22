package eu.transkribus.core.model.beans;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="EVENTS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpEvent implements Comparable<TrpEvent> {
	@Id
	@Column(name="EVENT_ID")
	private int id;
		
	@Column
	private Timestamp time;
	
	@Column
	private String title;
	
	@Column
	private String message;
	
	public TrpEvent(){}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override public String toString() {
		String str = "TrpEvent { eventId=" + id + ", time=" + time.toString() + ", title=" + title + ", message=" + message + "}";
		return str;
	}

	public Date getDate() {
		return new Date(getTime().getTime());
	}
	
	/**
	 * Uses the timestamp for comparison
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TrpEvent ev) {
		int result = 0;
		if (this.getTime().getTime() > ev.getTime().getTime()) {
			result = 1;
		} else if (this.getTime().getTime() < ev.getTime().getTime()) {
			result = -1;
		}
		return result;
	}
	
	
}
