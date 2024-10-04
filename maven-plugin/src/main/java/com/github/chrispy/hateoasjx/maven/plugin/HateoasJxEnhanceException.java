package com.github.chrispy.hateoasjx.maven.plugin;

/**
 * Exception type for HateoasJx plugin mojos
 */
public class HateoasJxEnhanceException extends RuntimeException
{
	/**
	 * Constructor
	 * 
	 * @param msg the exception message
	 */
	public HateoasJxEnhanceException(final String msg)
	{
		super(msg);
	}

	/**
	 * Create a not found exception
	 */
	public static HateoasJxEnhanceException notFound(final String clazz)
	{
		return new HateoasJxEnhanceException(clazz + " is missing on classpath");
	}

	/**
	 * {@inherited}
	 */
	@Override
	public synchronized HateoasJxEnhanceException initCause(final Throwable cause)
	{
		super.initCause(cause);
		return this;
	}
}
