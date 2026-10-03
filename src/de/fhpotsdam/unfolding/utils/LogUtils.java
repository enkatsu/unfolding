package de.fhpotsdam.unfolding.utils;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * Configures the logging of Unfolding.
 */
public class LogUtils {

	/** The logger all Unfolding loggers inherit from. */
	public static final String UNFOLDING_LOGGER_NAME = "de.fhpotsdam";

	private LogUtils() {
	}

	/**
	 * Configures Unfolding's logging if log4j has not been configured, e.g. in Processing sketches. Otherwise, log4j
	 * prints warnings about missing appenders when Unfolding logs a message.
	 *
	 * Only configures Unfolding's loggers (to print warnings and errors to the console), and does nothing if the
	 * application configured log4j itself.
	 */
	public static synchronized void configureIfUnconfigured() {
		if (LogManager.getRootLogger().getAllAppenders().hasMoreElements()) {
			// Configured by the application, e.g. with a log4j.properties file
			return;
		}

		Logger unfoldingLogger = Logger.getLogger(UNFOLDING_LOGGER_NAME);
		if (unfoldingLogger.getAllAppenders().hasMoreElements()) {
			// Already configured
			return;
		}

		unfoldingLogger.addAppender(new ConsoleAppender(new PatternLayout("Unfolding %-5p %c{1} - %m%n")));
		unfoldingLogger.setLevel(Level.WARN);
		unfoldingLogger.setAdditivity(false);
	}

}
