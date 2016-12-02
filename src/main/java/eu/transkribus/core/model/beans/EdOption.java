package eu.transkribus.core.model.beans;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "ED_OPTIONS")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EdOption implements Serializable {
		private static final long serialVersionUID = 1L;
		
		@Id
		@Column(name = "OPTION_ID")
		private Integer optionId = null;
		
		@Column(name= "FEATURE_ID")
		private int featureId = -1;
		
		@Column
		private String text;
		
		private boolean selected = false;

		public EdOption() {}
		
		public EdOption(EdOption o) {
			this();
			optionId = o.getOptionId();
			featureId = o.getFeatureId();
			text = o.getText();
			selected = o.isSelected();
		}

		public Integer getOptionId() {
			return optionId;
		}

		public void setOptionId(Integer optionId) {
			this.optionId = optionId;
		}

		public int getFeatureId() {
			return featureId;
		}

		public void setFeatureId(int featureId) {
			this.featureId = featureId;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		@Override public String toString() {
			return "EdOption [optionId=" + optionId + ", featureId=" + featureId + ", text=" + text + ", selected=" + selected + "]";
		}
		
		
}
