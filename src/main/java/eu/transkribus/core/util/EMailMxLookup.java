package eu.transkribus.core.util;

import java.util.Hashtable;
import javax.naming.*;
import javax.naming.directory.*;

import org.apache.commons.lang3.StringUtils;

public class EMailMxLookup {
	private final static String AT = "@";
	private final static String MX = "MX";
	
	public static int doLookupAddress(final String emailAddress) throws NamingException {
		if(StringUtils.isEmpty(emailAddress) || !emailAddress.contains(AT)) {
			throw new IllegalArgumentException("Not an email address: " + emailAddress);
		}
		return doLookupHost(emailAddress.split(AT)[1]);
	}
	
	/**
	 * Returns number of mail servers registered to the domain name
	 * 
	 * @param hostName
	 * @return
	 * @throws NamingException
	 */
	public static int doLookupHost(final String hostName) throws NamingException {
		Hashtable<String, String> env = new Hashtable<>();
		env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx = new InitialDirContext(env);
		Attributes attrs = ictx.getAttributes(hostName, new String[] { MX });
		Attribute attr = attrs.get(MX);
		if (attr == null) {
			return 0;
		}
		return attr.size();
	}
}