package eu.transkribus.core.model.beans.searchresult;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Facet {
	@XmlElement(name="facet_field")
	private String facetField;
	@XmlElement(name="facetMap")
	private Map<String,Long> facetMap = new HashMap<String,Long>();
	
	
	public Facet(){}


	public Map<String,Long> getFacetMap() {
		return facetMap;
	}

	public void setFacetMap(Map<String,Long> facets) {
		this.facetMap = facets;
	}


	public String getName() {
		return facetField;
	}


	public void setName(String name) {
		this.facetField = name;
	}
	
	

}
