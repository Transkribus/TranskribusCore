package eu.transkribus.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(ProxyUtils.class);
	
	public static final ProxySettings PROXY_UIBK = new ProxySettings("http://proxy.uibk.ac.at", 3128, "", "");
	
	public static void setProxy(ProxySettings p) {
		if(p != null) {
			final String proxyHost = p.getHost();
			final int proxyPort = p.getPort();
			final String proxyUser = p.getUser();
			final String proxyPassword = p.getPassword();
			final String proxyPortStr = (proxyPort > 0 ? ""+proxyPort : "");
			System.setProperty("https.proxyHost", proxyHost);
			System.setProperty("https.proxyPort", proxyPortStr);
			System.setProperty("https.proxyUser", proxyUser);
			System.setProperty("https.proxyPassword", proxyPassword);
			System.setProperty("http.proxyHost", proxyHost);
			System.setProperty("http.proxyPort", proxyPortStr);
			System.setProperty("http.proxyUser", proxyUser);
			System.setProperty("http.proxyPassword", proxyPassword);
			System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");
		} else {
			System.setProperty("https.proxyHost", "");
			System.setProperty("https.proxyPort", "");
			System.setProperty("https.proxyUser", "");
			System.setProperty("https.proxyPassword", "");
			System.setProperty("http.proxyHost", "");
			System.setProperty("http.proxyPort", "");
			System.setProperty("http.proxyUser", "");
			System.setProperty("http.proxyPassword", "");
			System.setProperty("http.nonProxyHosts", "");
		}
	}
	
	public static void unsetProxy() {
		ProxyUtils.setProxy(null);
	}

	public static void logProxySettings() {
		logger.debug("HTTPS ProxyHost = " + System.getProperty("https.proxyHost"));
		logger.debug("HTTPS ProxyPort = " + System.getProperty("https.proxyPort"));
		logger.debug("HTTPS ProxyUser = " + System.getProperty("https.proxyUser"));
		logger.debug("HTTPS ProxyPassword = " + System.getProperty("https.proxyPassword"));
		logger.debug("ProxyHost = " + System.getProperty("http.proxyHost"));
		logger.debug("ProxyPort = " + System.getProperty("http.proxyPort"));
		logger.debug("ProxyUser = " + System.getProperty("http.proxyUser"));
		logger.debug("ProxyPassword = " + System.getProperty("http.proxyPassword"));
		logger.debug("NonProxyHosts = " + System.getProperty("http.nonProxyHosts"));
	}
	
	public static class ProxySettings {
		protected String host;
		protected int port;
		protected String user;
		protected String password;
		public ProxySettings() {
			host = "";
			port = -1;
			user = "";
			password = "";
		}
		public ProxySettings(String host, int port, String user, String password) {
			this.host = host;
			this.port = port;
			this.user = user;
			this.password = password;
		}
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
		public String getUser() {
			return user;
		}
		public void setUser(String user) {
			this.user = user;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		@Override
		public String toString() {
			return "ProxySettings [host=" + host + ", port=" + port + ", user=" + user
					+ ", password=" + password + "]";
		}
	}
}
