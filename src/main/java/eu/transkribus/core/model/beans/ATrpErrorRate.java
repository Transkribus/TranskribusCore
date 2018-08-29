package eu.transkribus.core.model.beans;

public class ATrpErrorRate {

	private String wer;
	private String wAcc;
	private String cer;
	private String cAcc;
	private String bagTokensPrec;
	private String bagTokensRec;
	private String bagTokensF;

	public ATrpErrorRate() {
		super();
	}

	public String getWer() {
		return wer;
	}
	
	public Double getWerDouble() {
		return Double.parseDouble(wer.replaceAll("%", "").replaceAll(",", "."));
	}

	public void setWer(String wer) {
		this.wer = wer;
	}

	public String getCer() {
		return cer;
	}
	
	public Double getCerDouble() {
		return Double.parseDouble(cer.replaceAll("%", "").replaceAll(",", "."));
	}
	

	public void setCer(String cer) {
		this.cer = cer;
	}

	public String getwAcc() {
		return wAcc;
	}
	
	public Double getwAccDouble() {
		return Double.parseDouble(wAcc.replaceAll("%", "").replaceAll(",", "."));
	}

	public void setwAcc(String wAcc) {
		this.wAcc = wAcc;
	}

	public String getcAcc() {
		return cAcc;
	}
	
	public Double getcAccDouble() {
		return Double.parseDouble(cAcc.replaceAll("%", "").replaceAll(",", "."));
	}


	public void setcAcc(String cAcc) {
		this.cAcc = cAcc;
	}

	public String getBagTokensPrec() {
		return bagTokensPrec;
	}
	
	public Double getBagTokensPrecDouble() {
		return Double.parseDouble(bagTokensPrec.replaceAll("%", "").replaceAll(",", "."));
	}

	public void setBagTokensPrec(String bagTokensPrec) {
		this.bagTokensPrec = bagTokensPrec;
	}

	public String getBagTokensRec() {
		return bagTokensRec;
	}
	
	public Double getBagTokensRecDouble() {
		return Double.parseDouble(bagTokensRec.replaceAll("%", "").replaceAll(",", "."));
	}

	public void setBagTokensRec(String bagTokensRec) {
		this.bagTokensRec = bagTokensRec;
	}

	public String getBagTokensF() {
		return bagTokensF;
	}
	
	public Double getBagTokensFDouble() {
		return Double.parseDouble(bagTokensF.replaceAll("%", "").replaceAll(",", "."));
	}

	public void setBagTokensF(String bagTokensF) {
		this.bagTokensF = bagTokensF;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bagTokensF == null) ? 0 : bagTokensF.hashCode());
		result = prime * result + ((bagTokensPrec == null) ? 0 : bagTokensPrec.hashCode());
		result = prime * result + ((bagTokensRec == null) ? 0 : bagTokensRec.hashCode());
		result = prime * result + ((cAcc == null) ? 0 : cAcc.hashCode());
		result = prime * result + ((cer == null) ? 0 : cer.hashCode());
		result = prime * result + ((wAcc == null) ? 0 : wAcc.hashCode());
		result = prime * result + ((wer == null) ? 0 : wer.hashCode());
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
		ATrpErrorRate other = (ATrpErrorRate) obj;
		if (bagTokensF == null) {
			if (other.bagTokensF != null)
				return false;
		} else if (!bagTokensF.equals(other.bagTokensF))
			return false;
		if (bagTokensPrec == null) {
			if (other.bagTokensPrec != null)
				return false;
		} else if (!bagTokensPrec.equals(other.bagTokensPrec))
			return false;
		if (bagTokensRec == null) {
			if (other.bagTokensRec != null)
				return false;
		} else if (!bagTokensRec.equals(other.bagTokensRec))
			return false;
		if (cAcc == null) {
			if (other.cAcc != null)
				return false;
		} else if (!cAcc.equals(other.cAcc))
			return false;
		if (cer == null) {
			if (other.cer != null)
				return false;
		} else if (!cer.equals(other.cer))
			return false;
		if (wAcc == null) {
			if (other.wAcc != null)
				return false;
		} else if (!wAcc.equals(other.wAcc))
			return false;
		if (wer == null) {
			if (other.wer != null)
				return false;
		} else if (!wer.equals(other.wer))
			return false;
		return true;
	}
}