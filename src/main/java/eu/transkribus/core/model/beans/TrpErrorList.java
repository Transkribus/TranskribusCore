package eu.transkribus.core.model.beans;

public class TrpErrorList extends ATrpErrorRate {
		
		// Page Number
		private Integer pageNumber;
		
		public TrpErrorList() {
			super();
			pageNumber = null;
		}
		
		public Integer getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(Integer pageNumber) {
			this.pageNumber = pageNumber;
		}
		
}
