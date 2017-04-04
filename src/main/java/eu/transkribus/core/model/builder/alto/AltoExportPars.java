package eu.transkribus.core.model.builder.alto;

public class AltoExportPars {
	
	public static final String PARAMETER_KEY = "altoPars";
	
	boolean splitIntoWordsInAltoXml = true;
	
	public AltoExportPars() {
		
	}

	public boolean isSplitIntoWordsInAltoXml() {
		return splitIntoWordsInAltoXml;
	}

	public void setSplitIntoWordsInAltoXml(boolean splitIntoWordsInAltoXml) {
		this.splitIntoWordsInAltoXml = splitIntoWordsInAltoXml;
	}

	@Override
	public String toString() {
		return "AltoExportPars [splitIntoWordsInAltoXml=" + splitIntoWordsInAltoXml + "]";
	}

}
