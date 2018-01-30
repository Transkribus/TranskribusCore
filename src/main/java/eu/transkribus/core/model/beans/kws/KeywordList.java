package eu.transkribus.core.model.beans.kws;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Jaxb sucks! This is just here as List&lt;String&gt; is not working
 * 
 * @author philip
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KeywordList {
	private List<String> keyword;

	public KeywordList() {}

	public KeywordList(List<String> keyword) {
		this.keyword = keyword;
	}

	public List<String> getData() {
		return keyword;
	}

	public void setData(List<String> data) {
		this.keyword = data;
	}
}