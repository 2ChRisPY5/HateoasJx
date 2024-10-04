package com.github.chrispy.hateoasjx.maven.plugin.enhancements.server;

import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.BasicEnhancement;
import com.github.chrispy.hateoasjx.maven.plugin.util.JavassistUtil;

/**
 * Gather information about the class nodes.
 */
class Prepare extends BasicEnhancement
{
	/**
	 * {@inherited}
	 */
	@Override
	public void enhance(final ClassNode node)
	{
		final var ctClass = node.getCtClass();
		final var classFile = ctClass.getClassFile();
		final var processInfo = node.getProcessInformation();

		// check class itself
		markImplemented(classFile, "$$_hjx_self", processInfo::selfExists);
		markImplemented(classFile, "$$_hjx_relations", processInfo::relationsExists);
		markImplemented(classFile, "$$_hjx_substitutions", processInfo::subsitutionsExists);

		// special case if class extends from another external class
		// only do this for root nodes
		if(!node.hasParent())
		{
			final var superClass = JavassistUtil.getSuperclass(ctClass);
			markImplemented(superClass, "$$_hjx_self", processInfo::selfExists);
			markImplemented(superClass, "$$_hjx_relations", processInfo::relationsExists);
			markImplemented(superClass, "$$_hjx_substitutions", processInfo::subsitutionsExists);
		}

		ctClass.defrost();
	}
}
