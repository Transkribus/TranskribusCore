package eu.transkribus.core.exceptions;

public class InvalidUserInputException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4306544680705893723L;

	public InvalidUserInputException() {
	}

	public InvalidUserInputException(String message) {
		super(message);
	}

	public InvalidUserInputException(Throwable cause) {
		super(cause);
	}

	public InvalidUserInputException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidUserInputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
