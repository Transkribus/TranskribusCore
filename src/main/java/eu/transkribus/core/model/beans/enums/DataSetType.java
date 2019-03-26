package eu.transkribus.core.model.beans.enums;

public enum DataSetType {
	TRAIN("Train Set", 0),
	VALIDATION("Validation Set", 1);
	private final String label;
	private final int value;
	private DataSetType(final String label, final int value) {
		this.label = label;
		this.value = value;
	}
	public String getLabel() {
		return label;
	}
	public int getValue() {
		return value;
	}
}