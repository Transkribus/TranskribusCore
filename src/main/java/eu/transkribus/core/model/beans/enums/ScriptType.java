package eu.transkribus.core.model.beans.enums;

public enum ScriptType {
	HANDWRITTEN("Handwritten"),
	NORMAL("Normal"),
	NORMAL_LONG_S("Normal with long S"),
	GOTHIC("Gothic"),
	COMBINED("Combined");
	
	String str;
	
	ScriptType(String str) {
		this.str = str;
	}
	
	public String getStr() { return str; }
	
    public static ScriptType fromString(String v) {
        for (ScriptType c: ScriptType.values()) {
            if (c.str.equalsIgnoreCase(v) || c.toString().equalsIgnoreCase(v)) {
                return c;
            }
        }
        return null;
//        throw new IllegalArgumentException(v);
    }
	
}
