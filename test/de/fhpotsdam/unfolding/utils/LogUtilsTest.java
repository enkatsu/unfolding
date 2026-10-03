package de.fhpotsdam.unfolding.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LogUtilsTest {

	private Logger unfoldingLogger;

	@Before
	public void before() {
		LogManager.resetConfiguration();
		unfoldingLogger = Logger.getLogger(LogUtils.UNFOLDING_LOGGER_NAME);
	}

	@After
	public void after() {
		LogManager.resetConfiguration();
	}

	@Test
	public void configuresUnfoldingLoggerIfLog4jIsUnconfigured() {
		LogUtils.configureIfUnconfigured();

		assertTrue(unfoldingLogger.getAllAppenders().hasMoreElements());
		assertEquals(Level.WARN, unfoldingLogger.getLevel());
		// Does not configure other loggers
		assertFalse(LogManager.getRootLogger().getAllAppenders().hasMoreElements());
	}

	@Test
	public void keepsConfigurationOfApplication() {
		BasicConfigurator.configure();

		LogUtils.configureIfUnconfigured();

		assertFalse(unfoldingLogger.getAllAppenders().hasMoreElements());
		assertNull(unfoldingLogger.getLevel());
	}

	@Test
	public void configuresOnlyOnce() {
		LogUtils.configureIfUnconfigured();
		LogUtils.configureIfUnconfigured();

		int appenders = 0;
		for (java.util.Enumeration<?> e = unfoldingLogger.getAllAppenders(); e.hasMoreElements(); e.nextElement()) {
			appenders++;
		}
		assertEquals(1, appenders);
	}

}
