package eu.transkribus.core.model.beans.rest;

import javax.ws.rs.core.GenericEntity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Doesn't work
 * 
 * @author philip
 *
 * @param <T>
 */
@XmlRootElement(name="ResultList")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbPaginatedGenericEntity<T> {
	
	@XmlAttribute
	protected int total;
	
	@XmlAttribute
	protected int index;
	
	@XmlAttribute
	protected int nValues;
	
	@XmlAttribute
	protected String sortColumnField;
	
	@XmlAttribute
	protected String sortDirection;
	
	@XmlAnyElement(lax=true)
    protected GenericEntity<T> entity;

    public JaxbPaginatedGenericEntity(){}

    public JaxbPaginatedGenericEntity(T list, int total, int index, int nValues, 
    		String sortColumnField, String sortDirection){
    	this();
    	this.setEntity(new GenericEntity<T>(list) {});
    	this.total = total;
    	this.index = index;
    	this.nValues = nValues;
    	this.sortColumnField = sortColumnField;
    	this.sortDirection = sortDirection;
    }

    public GenericEntity<T> getEntity() {
		return entity;
	}

	public void setEntity(GenericEntity<T> entity) {
		this.entity = entity;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getnValues() {
		return nValues;
	}

	public void setnValues(int nValues) {
		this.nValues = nValues;
	}

	public String getSortColumnField() {
		return sortColumnField;
	}

	public void setSortColumnField(String sortColumnField) {
		this.sortColumnField = sortColumnField;
	}

	public String getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}
}