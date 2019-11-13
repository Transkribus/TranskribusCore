package eu.transkribus.core.model.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TrpErrorRateResult {

	private String wer;
	private String wAcc;
	private String cer;
	private String cAcc;
	private String bagTokensPrec;
	private String bagTokensRec;
	private String bagTokensF;

	public TrpErrorRateResult() {
		super();
	}

	public String getWer() {
		return wer;
	}
	
	public Double getWerDouble() {
		try {
			return Double.parseDouble(wer.replaceAll("%", "").replaceAll(",", "."));
		}
		catch(NumberFormatException e) {
			return null;
		}
	}

	public void setWer(String wer) {
		this.wer = wer;
	}

	public String getCer() {
		return cer;
	}
	
	public Double getCerDouble() {
		try {
			return Double.parseDouble(cer.replace("%", "").replaceAll(",", "."));
		}
		catch(NumberFormatException e) {
			return null;
		}
	}
	

	public void setCer(String cer) {
		this.cer = cer;
	}

	public String getwAcc() {
		return wAcc;
	}
	
	public Double getwAccDouble() {
		try {
			return Double.parseDouble(wAcc.replaceAll("%", "").replaceAll(",", "."));
		}catch(NumberFormatException e) {
			return null;
		}
		
		
	}

	public void setwAcc(String wAcc) {
		this.wAcc = wAcc;
	}

	public String getcAcc() {
		return cAcc;
	}
	
	public Double getcAccDouble() {
		try {
			return Double.parseDouble(cAcc.replaceAll("%", "").replaceAll(",", "."));
		}catch(NumberFormatException e) {
			return null;
		}
		
	}


	public void setcAcc(String cAcc) {
		this.cAcc = cAcc;
	}

	public String getBagTokensPrec() {
		return bagTokensPrec;
	}
	
	public Double getBagTokensPrecDouble() {
		try {
			return Double.parseDouble(bagTokensPrec.replaceAll("%", "").replaceAll(",", ".")) / 100;
		}catch(NumberFormatException e) {
			return null;
		}
		
	}

	public void setBagTokensPrec(String bagTokensPrec) {
		this.bagTokensPrec = bagTokensPrec;
	}

	public String getBagTokensRec() {
		return bagTokensRec;
	}
	
	public Double getBagTokensRecDouble() {
		try {
			return Double.parseDouble(bagTokensRec.replaceAll("%", "").replaceAll(",", ".")) / 100;
		}catch(NumberFormatException e) {
			return null;
		}
		
	}

	public void setBagTokensRec(String bagTokensRec) {
		this.bagTokensRec = bagTokensRec;
	}

	public String getBagTokensF() {
		return bagTokensF;
	}
	
	public Double getBagTokensFDouble() {
		try {
			return Double.parseDouble(bagTokensF.replaceAll("%", "").replaceAll(",", "."));
		}catch(NumberFormatException e) {
			return null;
		}
		
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
		TrpErrorRateResult other = (TrpErrorRateResult) obj;
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