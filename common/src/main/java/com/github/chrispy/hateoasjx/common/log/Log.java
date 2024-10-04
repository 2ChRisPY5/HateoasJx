package com.github.chrispy.hateoasjx.common.log;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Optional;;

/**
 * Internal HateoasJx logger utilizing {@link System.Logger}.
 */
public enum Log
{
	INSTANCE;

	private static final Logger LOGGER = System.getLogger("hateoasjx");

	private static final boolean DEBUG_ENABLED;
	static
	{
		final var severity = Optional.ofNullable(System.getProperty("hateoasJxLogLevel"))
			.or(() -> Optional.ofNullable(System.getenv("HATEOASJX_LOG_LEVEL")))
			.map(Level::valueOf)
			.orElse(Level.INFO)
			.getSeverity();

		DEBUG_ENABLED = severity < Level.INFO.getSeverity();
	}

	/**
	 * Log a message on {@code INFO} level.
	 * 
	 * @param pattern the message pattern
	 * @param args the pattern arguments
	 */
	public static void info(final String pattern, final Object... args)
	{
		LOGGER.log(Level.INFO, pattern, args);
	}

	/**
	 * Log a message on {@code WARNING} level.
	 * 
	 * @param pattern the message pattern
	 * @param args the pattern arguments
	 */
	public static void warn(final String pattern, final Object... args)
	{
		LOGGER.log(Level.WARNING, pattern, args);
	}

	/**
	 * Log a message on {@code ERROR} level.
	 * 
	 * @param pattern the message pattern
	 * @param args the pattern arguments
	 */
	public static void error(final String pattern, final Object... args)
	{
		LOGGER.log(Level.ERROR, pattern, args);
	}

	/**
	 * Log a message on {@code DEBUG} level.
	 * 
	 * @param pattern the message pattern
	 * @param args the pattern arguments
	 */
	public static void debug(final String pattern, final Object... args)
	{
		if(DEBUG_ENABLED)
		{
			LOGGER.log(Level.DEBUG, pattern, args);
		}
	}
}
