package eu.transkribus.core.model.beans;

public class TrpErrorList {
		
		// Page Number
		private int pageNumber;
		
		// Word Error Rate
		private String wer;
		
		// Word Accuracy
		private String wAcc;
		
		// Char Error Rate
		private String cer;
		
		// Char Accuracy
		private String cAcc;
		
		// Bag of Tokens Precison
		private String bagTokensPrec;
		
		// Bag of Tokens Recall
		private String bagTokensRec;
		 
		// Bag of Tokens F-measure
		private String bagTokensF;

		
		public String getWer() {
			return wer;
		}

		public void setWer(String wer) {
			this.wer = wer;
		}

		public String getCer() {
			return cer;
		}

		public void setCer(String cer) {
			this.cer = cer;
		}

		public String getwAcc() {
			return wAcc;
		}

		public void setwAcc(String wAcc) {
			this.wAcc = wAcc;
		}

		public String getcAcc() {
			return cAcc;
		}

		public void setcAcc(String cAcc) {
			this.cAcc = cAcc;
		}

		public String getBagTokensPrec() {
			return bagTokensPrec;
		}

		public void setBagTokensPrec(String bagTokensPrec) {
			this.bagTokensPrec = bagTokensPrec;
		}

		public String getBagTokensRec() {
			return bagTokensRec;
		}

		public void setBagTokensRec(String bagTokensRec) {
			this.bagTokensRec = bagTokensRec;
		}

		public String getBagTokensF() {
			return bagTokensF;
		}

		public void setBagTokensF(String bagTokensF) {
			this.bagTokensF = bagTokensF;
		}

		public int getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(int pageNumber) {
			this.pageNumber = pageNumber;
		}
		
}
