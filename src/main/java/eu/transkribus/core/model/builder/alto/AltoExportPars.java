package eu.transkribus.core.model.builder.alto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AltoExportPars {
	
	public static final String PARAMETER_KEY = "altoPars";
	
	boolean splitIntoWordsInAltoXml = true;
	
	public AltoExportPars() {
		
	}
	
	

	public AltoExportPars(boolean splitIntoWordsInAltoXml) {
		super();
		this.splitIntoWordsInAltoXml = splitIntoWordsInAltoXml;
	}



	public boolean isSplitIntoWordsInAltoXml() {
		return splitIntoWordsInAltoXml;
	}

	public void setSplitIntoWordsInAltoXml(boolean splitIntoWordsInAltoXml) {
		this.splitIntoWordsInAltoXml = splitIntoWordsInAltoXml;
	}

	@Override
	public String toString() {
		return "AltoExportPars [splitIntoWordsInAltoXml=" + splitIntoWordsInAltoXml + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (splitIntoWordsInAltoXml ? 1231 : 1237);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AltoExportPars other = (AltoExportPars) obj;
		if (splitIntoWordsInAltoXml != other.splitIntoWordsInAltoXml)
			return false;
		return true;
	}
}
