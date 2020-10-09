package eu.transkribus.core.model.beans.enums;

public enum CreditSelectionStrategy {
	/**
	 * Consider packages owned by user only
	 */
	USER_ONLY,
	/**
	 * Consider packages linked to collection only
	 */
	COLLECTION_ONLY,
	/**
	 * Consider user-owned packages with higher priority than collection-linked packages
	 */
	USER_THEN_COLLECTION,
	/**
	 * Consider collection-linked packages with higher priority than user-owned packages
	 */
	COLLECTION_THEN_USER;
}
