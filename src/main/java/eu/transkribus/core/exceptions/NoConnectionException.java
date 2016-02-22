package eu.transkribus.core.exceptions;

public class NoConnectionException extends Exception {
	private static final long serialVersionUID = 674276374870376052L;

	public NoConnectionException() {
		super();
	}

	public NoConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoConnectionException(String message) {
		super(message);
	}

	public NoConnectionException(Throwable cause) {
		super(cause);
	}
}
