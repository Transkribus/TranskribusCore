package eu.transkribus.core.rmi.util;

public class NcsrToolException extends Exception {
	private static final long serialVersionUID = 4778016494715383031L;
	
	public NcsrToolException(String message, Throwable t){
		super(message, t);
	}

	public NcsrToolException(String message) {
		super(message);
	}
	
}
