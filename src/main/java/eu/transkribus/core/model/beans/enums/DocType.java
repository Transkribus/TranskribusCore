package eu.transkribus.core.model.beans.enums;

public enum DocType {
	UNDEFINED(null),
	HANDWRITTEN(0),
	PRINT(1);
	
	private Integer value;
	
	private DocType(Integer value) {
		this.value = value;
	}
	
	public Integer getValue() {
		return value;
	}
	
	/**
	 * Resolve the enum type for an int value. If int value is not mapped, null is returned.
	 * 
	 * @param value
	 * @return
	 */
	public static DocType fromValue(final Integer value) {
		DocType result = UNDEFINED;
		if(value == null) {
			return result;
		}
		for(DocType t : DocType.values()) {
			if(value.equals(t.getValue())) {
				result = t;
				break;
			}
		}
		return result;
	}
}
