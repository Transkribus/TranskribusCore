package eu.transkribus.core.model.beans;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TestBean implements Serializable, Comparable<TestBean> {
	private static final long serialVersionUID = 1L;
	
	private String string1 = "hallo";
	private String string2 = "welt";

	public String getString1() {
		return this.string1;
	}

	public void setString1(String string1) {
		this.string1 = string1;
	}

	public String getString2() {
		return this.string2;
	}

	public void setString2(String string2) {
		this.string2 = string2;
	}

	@Override
	public String toString() {
		return string1 + " " + string2;
	}

	@Override
	public int compareTo(TestBean o) {
		return 0;
	}
}
