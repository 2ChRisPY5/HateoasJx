package com.github.chrispy.hateoasjx.maven.plugin.enhancements;

import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.Context;
import com.github.chrispy.hateoasjx.maven.plugin.HateoasJxEnhanceException;

/**
 * Writing the file
 */
public class Write extends BasicEnhancement
{
	private final Context ctx;

	/**
	 * Constructor
	 */
	public Write(final Context context)
	{
		super();
		this.ctx = context;
	}

	/**
	 * {@inherited}
	 */
	@Override
	protected void enhance(final ClassNode node)
	{
		final var dir = this.ctx.getDir();
		final var ctClass = node.getCtClass();
		try
		{
			ctClass.writeFile(dir.toString());
		}
		catch(final Exception ex)
		{
			throw new HateoasJxEnhanceException("Could not write %s to %s".formatted(ctClass, dir)).initCause(ex);
		}
		finally
		{
			ctClass.detach();
		}
	}
}
