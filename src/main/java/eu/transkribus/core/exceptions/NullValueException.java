package eu.transkribus.core.exceptions;

public class NullValueException extends Exception {
	private static final long serialVersionUID = 624460373976491567L;

	public NullValueException() {
		super();
	}

	public NullValueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NullValueException(String message, Throwable cause) {
		super(message, cause);
	}

	public NullValueException(String message) {
		super(message);
	}

	public NullValueException(Throwable cause) {
		super(cause);
	}
	
}
