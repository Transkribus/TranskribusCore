package eu.transkribus.core.model.builder;

import java.io.IOException;

public class NoTablesException extends IOException {
	private static final long serialVersionUID = 6264711575302274823L;

	public NoTablesException() {
	}

	public NoTablesException(String message) {
		super(message);
	}

	public NoTablesException(Throwable cause) {
		super(cause);
	}

	public NoTablesException(String message, Throwable cause) {
		super(message, cause);
	}

}
