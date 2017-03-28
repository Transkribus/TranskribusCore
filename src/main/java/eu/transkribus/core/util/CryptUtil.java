package eu.transkribus.core.util;

import org.apache.commons.codec.digest.Crypt;

public class CryptUtil {

	public static boolean isHashCorrect(final String plainToken, final String hash) {
		if(plainToken == null || hash == null){
			return false;
		}
		
		final String check = Crypt.crypt(plainToken, hash);
		return hash.equals(check);
	}
	
	public static String generateHash(final String token) {
		return Crypt.crypt(token);
	}

}