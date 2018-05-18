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

}