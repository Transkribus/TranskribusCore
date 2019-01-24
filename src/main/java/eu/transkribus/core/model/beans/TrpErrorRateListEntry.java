package eu.transkribus.core.model.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TrpErrorRateListEntry extends TrpErrorRateResult {
		
		// Page Number
		private Integer pageNumber;
		
		public TrpErrorRateListEntry() {
			super();
			pageNumber = null;
		}
		
		public Integer getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(Integer pageNumber) {
			this.pageNumber = pageNumber;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((pageNumber == null) ? 0 : pageNumber.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			TrpErrorRateListEntry other = (TrpErrorRateListEntry) obj;
			if (pageNumber == null) {
				if (other.pageNumber != null)
					return false;
			} else if (!pageNumber.equals(other.pageNumber))
				return false;
			return true;
		}
		
}
