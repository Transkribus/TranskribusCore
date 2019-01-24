package eu.transkribus.core.model.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="DEALOG.DEA_DOCUMENT")
public class DeaDocument implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -227878210615000609L;
	@Id
	@Column
	private int dea_document_id;
	@Column
	private Integer dea_batch_id;
	@Column
	private String fat_xml_url;
	@Column
	private String folder;
	@Column
	private String services;
	@Column
	private String status;
	@Column
	private String message;
	@Column
	private int npages;
	@Column
	private String current_service;
	
	public DeaDocument() {
		super();
	}

	public DeaDocument(int dea_document_id, Integer dea_batch_id, String fat_xml_url, String folder, String services, String status, String message, int npages) {
		super();
		this.dea_document_id = dea_document_id;
		this.dea_batch_id = dea_batch_id;
		this.fat_xml_url = fat_xml_url;
		this.folder = folder;
		this.services = services;
		this.status = status;
		this.message = message;
		this.npages = npages;
	}
	
//	@Override
//	public String toString() {
//		return ReflectionToStringBuilder.toString(this);
//	}

	public int getDea_document_id() {
		return dea_document_id;
	}

	public void setDea_document_id(int dea_document_id) {
		this.dea_document_id = dea_document_id;
	}

	public Integer getDea_batch_id() {
		return dea_batch_id;
	}

	public void setDea_batch_id(Integer dea_batch_id) {
		this.dea_batch_id = dea_batch_id;
	}

	public String getFat_xml_url() {
		return fat_xml_url;
	}

	public void setFat_xml_url(String fat_xml_url) {
		this.fat_xml_url = fat_xml_url;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getServices() {
		return services;
	}

	public void setServices(String services) {
		this.services = services;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getNpages() {
		return npages;
	}

	public void setNpages(int npages) {
		this.npages = npages;
	}
	
	public String getCurrent_service() {
		return current_service;
	}

	public void setCurrent_service(String current_service) {
		this.current_service = current_service;
	}
	
	public String[] getServicesArray() {
		String tmp = services.replaceAll("(", "");
		tmp = services.replaceAll(")", "");
		tmp = services.replaceAll("[", "");
		tmp = services.replaceAll("]", "");
		String [] token = tmp.split(",");
		
		for (int i=0; i<token.length; ++i) {
			token[i] = token[i].trim();
		}
		
		return token;
	}
	
	public int getNServices() {
		return getServicesArray().length;
	}
}
