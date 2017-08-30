package eu.transkribus.core.exceptions;

import java.io.IOException;

public class ChecksumException extends IOException {
	private static final long serialVersionUID = 3463778462770941345L;
	
	public ChecksumException() {
		super();
	}
	
	public ChecksumException(String message) {
		super(message);
	}
	
	public ChecksumException(Throwable cause) {
		super(cause);
	}
	
	public ChecksumException(String message, Throwable cause) {
		super(message, cause);
	}
}
