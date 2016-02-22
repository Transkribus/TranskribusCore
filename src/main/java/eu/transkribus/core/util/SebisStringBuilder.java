package eu.transkribus.core.util;

public class SebisStringBuilder {
	public StringBuilder sb = new StringBuilder();
	
	int cInd;

	public SebisStringBuilder() {}
	
	public void incIndent() {
		++cInd;
	}
	
	public void decIndent() {
		--cInd;
		if (cInd < 0)
			cInd = 0;
	}
	
	public String getIndent() {
		String ind = "";
		for (int i=0; i<cInd; ++i) {
			ind+="\t";
		}
		return ind;
	}
	
//	public void append(String txt) {
//		sb.append(getIndent()+txt);
//	}
	
	public void addLine(String line) {
		sb.append(getIndent()+line+"\n");
	}
	
	public void addLineWIndent(String line) {
		incIndent();
		addLine(line);
		decIndent();
	}
	
	@Override public String toString() {
		return sb.toString(); 
	}
}
