package eu.transkribus.core.io;

import java.io.IOException;

public class UnsupportedFormatException extends IOException {
	private static final long serialVersionUID = -3982171707467380945L;
	public UnsupportedFormatException(String msg) {
		super(msg);
	}
}
