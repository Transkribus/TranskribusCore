package eu.transkribus.core.model.beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import eu.transkribus.core.model.beans.customtags.CustomTag;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpDbTag implements Serializable, Comparable<TrpDbTag> {
	private static final long serialVersionUID = 8062423786357430801L;
	
	int id;
	int docid;
	int pageid;
	int tsid;
	String regionid;
	
	int offset;
	int length;
	String value;
	
	String customTagCss;
	
	public TrpDbTag() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDocid() {
		return docid;
	}

	public void setDocid(int docid) {
		this.docid = docid;
	}

	public int getPageid() {
		return pageid;
	}

	public void setPageid(int pageid) {
		this.pageid = pageid;
	}

	public int getTsid() {
		return tsid;
	}

	public void setTsid(int tsid) {
		this.tsid = tsid;
	}

	public String getRegionid() {
		return regionid;
	}

	public void setRegionid(String regionid) {
		this.regionid = regionid;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCustomTagCss() {
		return customTagCss;
	}

	public void setCustomTagCss(String customTagCss) {
		this.customTagCss = customTagCss;
	}

	@Override
	public String toString() {
		return "TrpDbTag [id=" + id + ", docid=" + docid + ", pageid=" + pageid + ", tsid=" + tsid + ", regionid="
				+ regionid + ", offset=" + offset + ", length=" + length + ", value=" + value + ", customTagCss="
				+ customTagCss + "]";
	}

	@Override
	public int compareTo(TrpDbTag arg0) {
		return Integer.compare(id, arg0.id);
	}

}
