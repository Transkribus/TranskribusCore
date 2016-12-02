package eu.transkribus.core.model.beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "ED_FEATURES")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EdFeature implements Serializable {
		private static final long serialVersionUID = 1L;
		
		@Id
		@Column(name = "FEATURE_ID")
		private int featureId = -1;
	
		@Column
		private String title;
		
		@Column
		private String description;
		
		@Column(name="COLLECTION_ID")
		private Integer colId;
		
		@XmlElementWrapper(name="optionList")
		@XmlElement
		private List<EdOption> options = new LinkedList<>();
		
		public EdFeature(EdFeature f) {
			featureId = f.getFeatureId();
			title = f.getTitle();
			description = f.getDescription();
			colId = f.getColId();
			for(EdOption o : f.getOptions()) {
				options.add(new EdOption(o));
			}
		}

		public int getFeatureId() {
			return featureId;
		}

		public void setFeatureId(int featureId) {
			this.featureId = featureId;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Integer getColId() {
			return colId;
		}

		public void setColId(Integer colId) {
			this.colId = colId;
		}

		public List<EdOption> getOptions() {
			return options;
		}

		public void setOptions(List<EdOption> options) {
			this.options = options;
		}
		
		public EdOption getSelectedOption() {
			if (options != null) {
				for (EdOption o : options) {
					if (o.isSelected())
						return o;
	
				}
			}
			return null;
		}

		@Override public String toString() {
			String str = "EdFeature [featureId=" + featureId + ", title=" + title + ", description=" + description + ", colId=" + colId + ", options=";
			for (EdOption o : options) {
				str += o+" - ";
			}
			str += "]";
						
			return str;
		}
		
		
}
