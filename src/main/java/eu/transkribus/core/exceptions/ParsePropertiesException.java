package eu.transkribus.core.exceptions;

public class ParsePropertiesException extends RuntimeException {
	private static final long serialVersionUID = -5204800581619865166L;
	
	public ParsePropertiesException() {
	}

	public ParsePropertiesException(String message) {
		super(message);
	}

	public ParsePropertiesException(Throwable cause) {
		super(cause);
	}

	public ParsePropertiesException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParsePropertiesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	

}
