package eu.transkribus.core.exceptions;

import javax.security.auth.login.LoginException;

public class ClientVersionNotSupportedException extends LoginException {
	private static final long serialVersionUID = -4647556018283858334L;
	
	public static final int STATUS_CODE = 412;
	
	public ClientVersionNotSupportedException() {
		super();
	}
	
	public ClientVersionNotSupportedException(String message) {
		super(message);
	}

}
