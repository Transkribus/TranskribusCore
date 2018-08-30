package eu.transkribus.core.model.beans.searchresult;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

//@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KwPageWord{
	
	@XmlElement(name="txt")
	public String text;
	
	@XmlElement(name="rp")
	public Double conf;
	
//	@XmlElementWrapper(name="positions")
	@XmlElement(name="pos")
	public int[] pos = new int[2];
	
	@XmlElement(name="size")
	public int[] size = new int[2];
}
