package eu.transkribus.core.model.beans;

/**
 * Enum with different publishing "level" for ATrpModels and respective GT data sets.
 * Maps release level int values as stored in the database from and to a level name.
 */
public enum ReleaseLevel {
	/**
	 * Model is private (default)
	 */
	None(0),
	/**
	 * Model is released to public. Datasets are private, i.e. can't be viewed or used by others.
	 */
	UndisclosedDataSet(1),
	/**
	 * Model is released to public. Datasets can be viewed and reused.
	 */
	DisclosedDataSet(2);
	private int value;
	private ReleaseLevel(int value) {
		this.value = value;
	}
	public int getValue() {
		return value;
	}
	/**
	 * Resolve the enum type for an int value. If int value is not mapped, null is returned.
	 * 
	 * @param value
	 * @return
	 */
	public static ReleaseLevel fromValue(final int value) {
		ReleaseLevel result = None;
		for(ReleaseLevel l : ReleaseLevel.values()) {
			if(value == l.getValue()) {
				result = l;
				break;
			}
		}
		return result;
	}
	
	/**
	 * Determine if this level allows to view the dataset.
	 * This method should be used instead of checking on specific levels, as future changes in ReleaseLevel might break code then.
	 * 
	 * @param level
	 * @return
	 */
	public static boolean isPrivateDataSet(ReleaseLevel level) {
		switch (level) {
		case None:
		case UndisclosedDataSet:
			return true;
		default:
			return false;
		}
	}
}