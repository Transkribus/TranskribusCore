package eu.transkribus.core.exceptions;

public class InsufficientCreditsException extends Exception {
	private static final long serialVersionUID = 5619532661297491956L;

	public InsufficientCreditsException(String message, Throwable cause) {
		super(message, cause);
	}

	public InsufficientCreditsException(String message) {
		super(message);
	}
}