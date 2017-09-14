package eu.transkribus.core.exceptions;

public class TrpJobModuleDaoException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7065732947879429398L;

	public TrpJobModuleDaoException() {
	}

	public TrpJobModuleDaoException(String message) {
		super(message);
	}

	public TrpJobModuleDaoException(Throwable cause) {
		super(cause);
	}

	public TrpJobModuleDaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public TrpJobModuleDaoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
