package com.github.chrispy.hateoasjx.common;

/**
 * Basic exception type for HateoasJx.
 */
public class HateoasJxException extends RuntimeException
{
	/**
	 * Constructor
	 * 
	 * @param msg the exception message
	 */
	public HateoasJxException(final String msg)
	{
		super(msg);
	}

	/**
	 * Constructor
	 * 
	 * @param msg the exception message
	 * @param cause the root cause
	 */
	public HateoasJxException(final String msg, final Throwable cause)
	{
		super(msg, cause);
	}
}
