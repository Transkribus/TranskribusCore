package eu.transkribus.core.util;

public class LogbackUtil {

	public static boolean setLevel(org.slf4j.Logger logger, ch.qos.logback.classic.Level level) {
		if (logger instanceof ch.qos.logback.classic.Logger) {
			return setLevel((ch.qos.logback.classic.Logger) logger, level);
		}
		return false;
	}
	
	public static boolean setLevel(ch.qos.logback.classic.Logger logger, ch.qos.logback.classic.Level level) {
		logger.setLevel(level);
		return true;
	}
	
}
