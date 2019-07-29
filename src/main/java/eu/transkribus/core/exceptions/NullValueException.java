package eu.transkribus.core.exceptions;

import java.io.IOException;

public class NullValueException extends IOException {
	private static final long serialVersionUID = 624460373976491567L;

	public NullValueException() {
		super();
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
