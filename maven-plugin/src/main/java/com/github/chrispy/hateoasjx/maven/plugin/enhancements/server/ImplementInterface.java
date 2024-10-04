package com.github.chrispy.hateoasjx.maven.plugin.enhancements.server;

import com.github.chrispy.hateoasjx.api.HateoasJxResolvable;
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
			|| alreadyImplemented(n, HateoasJxResolvable.class)));
	}

	/**
	 * {@inherited}
	 */
	@Override
	public void enhance(final ClassNode node)
	{
		node.getCtClass().addInterface(JavassistUtil.load(HateoasJxResolvable.class));
	}

	/**
	 * @return if class is partially enhanced
	 */
	private static boolean partialEnhanced(final ProcessInformation info)
	{
		return info.isSelfExists() || info.isRelationsExists() || info.isSubsitutionsExists();
	}
}
