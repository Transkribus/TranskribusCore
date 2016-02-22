package eu.transkribus.core.exceptions;

public class InitializationFailedException extends RuntimeException {
	private static final long serialVersionUID = -8963797757897604237L;

	public InitializationFailedException() {
	}

	public InitializationFailedException(String message) {
		super(message);
	}

	public InitializationFailedException(Throwable cause) {
		super(cause);
	}

	public InitializationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public InitializationFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
