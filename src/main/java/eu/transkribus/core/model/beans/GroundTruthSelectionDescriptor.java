package eu.transkribus.core.model.beans;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GroundTruthSelectionDescriptor implements Serializable {
	private static final long serialVersionUID = -814556395701993224L;

	/**
	 * The ID of the entity the selected ground truth is linked to. The entity type is defined by the context, e.g. HTR for HTR training. 
	 */
	private int id;
	
	@XmlElementWrapper(name="gtPageList")
	@XmlElement
	private List<Integer> gtPages;
	
	public GroundTruthSelectionDescriptor() {}
	
	public GroundTruthSelectionDescriptor(int id) {
		this();
		this.id = id;
	}
	
	public GroundTruthSelectionDescriptor(int id, int gtPageId) {
		this(id);
		addGtPage(gtPageId);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Integer> getGtPages() {
		return gtPages;
	}

	public void setPages(List<Integer> gtPages) {
		this.gtPages = gtPages;
	}
	
	public void addGtPage(int gtPageId) {
		if(gtPages == null) {
			gtPages = new ArrayList<>();
		}
		gtPages.add(gtPageId);
	}

	@Override
	public String toString() {
		return "GroundTruthSelectionDescriptor [id=" + id + ", gtPages=" + gtPages + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gtPages == null) ? 0 : gtPages.hashCode());
		result = prime * result + id;
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
		GroundTruthSelectionDescriptor other = (GroundTruthSelectionDescriptor) obj;
		if (gtPages == null) {
			if (other.gtPages != null)
				return false;
		} else if (!gtPages.equals(other.gtPages))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
}

