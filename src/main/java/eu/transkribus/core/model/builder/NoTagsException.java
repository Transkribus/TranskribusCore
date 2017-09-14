package eu.transkribus.core.model.builder;

import java.io.IOException;

public class NoTagsException extends IOException {
	private static final long serialVersionUID = 3218155391575105517L;

	public NoTagsException() {
	}

	public NoTagsException(String message) {
		super(message);
	}

	public NoTagsException(Throwable cause) {
		super(cause);
	}

	public NoTagsException(String message, Throwable cause) {
		super(message, cause);
	}

}
