package eu.transkribus.core.util;

import org.slf4j.Logger;

/**
 * Some utility methods for logging
 * 
 * @author philip
 *
 */
public class LogUtil {

	public static final Level DEFAULT_LVL = Level.DEBUG;
	
	private LogUtil() {}
	
	/**
	 * Log the message with given logger and on level {@link #DEFAULT_LVL}. 
	 * If logger is null then System.out will be used for the 
	 * message.
	 * 
	 * @param msg
	 * @param logger
	 */
	public static void log(String msg, final Logger logger)	{
		logMsg(msg, logger, DEFAULT_LVL);
	}
	
	/**
	 * Log the message with given logger and Log level. 
	 * If logger is null then System.out will be used for the 
	 * message.
	 * 
	 * @param msg
	 * @param logger
	 * @param lvl
	 */
	public static void log(String msg, final Logger logger, Level lvl) {
		log(msg, null, logger, lvl);
	}
	
	/**
	 * Log the message with given logger and Log level. 
	 * If logger is null then System.out will be used for the 
	 * message and System.err for any throwable's stacktrace.
	 * 
	 * @param msg
	 * @param t
	 * @param logger
	 * @param lvl
	 */
	public static void log(String msg, final Throwable t, final Logger logger, Level lvl) {
		if(logger == null) {
			System.out.println(msg);
			if(t != null) t.printStackTrace();
			return;
		}
		if(lvl == null) {
			lvl = DEFAULT_LVL;
		}
		if(msg == null) {
			msg = "";
		}
		if(t == null) {
			logMsg(msg, logger, lvl);
		} else {
			logMsgWithStacktrace(msg, t, logger, lvl);
		}
	}
	
	/**
	 * log the message. No argument must be null!
	 * 
	 * @param msg
	 * @param logger
	 * @param lvl
	 */
	private static void logMsg(final String msg, final Logger logger, final Level lvl) {
		if(msg == null || logger == null || lvl == null) {
			throw new IllegalArgumentException("No argument must be null!");
		}
		switch(lvl) {
		case TRACE:
			logger.trace(msg);
			break;
		case INFO:
			logger.info(msg);
			break;
		case WARN:
			logger.warn(msg);
			break;
		case ERROR:
			logger.error(msg);
			break;
		default:
		case DEBUG:
			logger.debug(msg);
			break;
		}
	}

	/**
	 * log the message with stacktrace from throwable. No argument must be null!
	 * 
	 * @param msg
	 * @param logger
	 * @param lvl
	 */
	private static void logMsgWithStacktrace(final String msg, final Throwable t, final Logger logger, final Level lvl) {
		if(msg == null || t == null || logger == null || lvl == null) {
			throw new IllegalArgumentException("No argument must be null!");
		}
		switch(lvl) {
		case TRACE:
			logger.trace(msg, t);
			break;
		case INFO:
			logger.info(msg, t);
			break;
		case WARN:
			logger.warn(msg, t);
			break;
		case ERROR:
			logger.error(msg, t);
			break;
		default:
		case DEBUG:
			logger.debug(msg, t);
			break;
		}
	}
	
	/**
	 * Define log levels independently of underlying logging framework. SLF4j does not offer this yet...
	 * 
	 * @author philip
	 *
	 */
	public static enum Level {
		TRACE,
		DEBUG,
		INFO,
		WARN,
		ERROR;
	}
}

