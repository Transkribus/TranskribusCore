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
	
	public Double getWerDouble() {
		// FIXME Dummy method for fixing build of GUI. Add correct impl.
		return Double.valueOf(-1);
	}

	public Double getwAccDouble() {
		// FIXME Dummy method for fixing build of GUI. Add correct impl.
		return Double.valueOf(-1);
	}

	public Double getCerDouble() {
		// FIXME Dummy method for fixing build of GUI. Add correct impl.
		return Double.valueOf(-1);
	}

	public Double getcAccDouble() {
		// FIXME Dummy method for fixing build of GUI. Add correct impl.
		return Double.valueOf(-1);
	}

	public Double getBagTokensPrecDouble() {
		// FIXME Dummy method for fixing build of GUI. Add correct impl.
		return Double.valueOf(-1);
	}

	public Double getBagTokensRecDouble() {
		// FIXME Dummy method for fixing build of GUI. Add correct impl.
		return Double.valueOf(-1);
	}

	public Double getBagTokensFDouble() {
		// FIXME Dummy method for fixing build of GUI. Add correct impl.
		return Double.valueOf(-1);
	}

}