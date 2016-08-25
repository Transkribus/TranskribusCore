package eu.transkribus.core.model.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="ACTION")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpAction {
	@Column(name="ACTIONID")
	private int actionId;
	@Column
	private int type;
	@Transient
	private String typeString;
	@Column
	private int userId;
	@Transient
	private String userName;
	@Column
	private long timestamp;
	@Column
	private int colId;
	@Transient
	private String colName;
	@Column
	private int docId;
	@Transient
	private String docName;
	@Column
	private int pageId;
	@Transient
	private int pageNr;
	@Column
	private int clientId;
	@Transient
	private String clientName;
	@Column
	private String clientVersion;
}
