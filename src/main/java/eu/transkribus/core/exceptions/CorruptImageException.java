package eu.transkribus.core.exceptions;

import java.io.IOException;

public class CorruptImageException extends IOException {
	private static final long serialVersionUID = 290778591221608181L;

	public CorruptImageException() {
		super();
	}
	
	public CorruptImageException(String message) {
		super(message);
	}
	
	public CorruptImageException(Throwable cause) {
		super(cause);
	}
	
	public CorruptImageException(String message, Throwable cause) {
		super(message, cause);
	}
}
