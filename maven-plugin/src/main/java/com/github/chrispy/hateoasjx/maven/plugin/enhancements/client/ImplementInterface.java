package com.github.chrispy.hateoasjx.maven.plugin.enhancements.client;

import com.github.chrispy.hateoasjx.client.proxy.HateoasJxProxy;
import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.BasicEnhancement;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.ProcessInformation;
import com.github.chrispy.hateoasjx.maven.plugin.util.JavassistUtil;

/**
 * Responsible for adding the interface.
 */
class ImplementInterface extends BasicEnhancement
{
	/**
	 * Constructor
	 */
	ImplementInterface()
	{
		// if something is partial enhanced then interface is already in hierarchy
		super(n -> !(partialEnhanced(n.getProcessInformation())
			|| n.hasAncestorWith(ImplementInterface::partialEnhanced)
			|| alreadyImplemented(n, HateoasJxProxy.class)));
	}

	/**
	 * {@inherited}
	 */
	@Override
	protected void enhance(final ClassNode node)
	{
		node.getCtClass().addInterface(JavassistUtil.load(HateoasJxProxy.class));
	}

	/**
	 * Check if partial enhanced.
	 */
	private static boolean partialEnhanced(final ProcessInformation info)
	{
		return info.isIdentifierExists() || info.isInitializerExists();
	}
}
