package eu.transkribus.core.exceptions;

public class NoSuchJobException extends Exception {
	private static final long serialVersionUID = 4362236494665942646L;

	public NoSuchJobException() {
	}

	public NoSuchJobException(String message) {
		super(message);
	}

	public NoSuchJobException(Throwable cause) {
		super(cause);
	}

	public NoSuchJobException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchJobException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
