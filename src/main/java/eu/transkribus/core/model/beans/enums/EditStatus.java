package eu.transkribus.core.model.beans.enums;


public enum EditStatus {
	NEW(0, "New"),
	IN_PROGRESS(1, "In Progress"),
//	REOPENED,
//	RETAINED,
	DONE(2, "Done"),
//	APPROVED,
	FINAL(Integer.MAX_VALUE, "Final");
	
	int value=Integer.MIN_VALUE;
	String str=null;
	
	EditStatus(int value, String str) {
		this.value = value;
		this.str = str;
	}
	
	public int getValue() { return value; }
	public String getStr() { return str; }
	
    public static EditStatus fromString(String v) {
        for (EditStatus c: EditStatus.values()) {
            if (c.str.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }	
}
