package eu.transkribus.core.model.beans.pagecontent_extension.observable;

public class TextInsertData {
	public int start, end;
	public String replacement;
	public TextInsertData(int start, int end, String replacement) {
		super();
		this.start = start;
		this.end = end;
		this.replacement = replacement;
	}
	
	@Override public String toString() {
		return "TextInsertData [start=" + start + ", end=" + end
				+ ", replacement=" + replacement + "]";
	}
	
}