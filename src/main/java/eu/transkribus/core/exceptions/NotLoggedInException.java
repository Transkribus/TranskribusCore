package eu.transkribus.core.exceptions;

public class NotLoggedInException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8662711930645619453L;

	public NotLoggedInException() {
	}

	public NotLoggedInException(String message) {
		super(message);
	}

	public NotLoggedInException(Throwable cause) {
		super(cause);
	}

	public NotLoggedInException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotLoggedInException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
