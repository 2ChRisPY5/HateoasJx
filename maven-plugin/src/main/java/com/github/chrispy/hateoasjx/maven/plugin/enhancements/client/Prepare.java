package com.github.chrispy.hateoasjx.maven.plugin.enhancements.client;

import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.BasicEnhancement;
import com.github.chrispy.hateoasjx.maven.plugin.util.JavassistUtil;

/**
 * Gather basic information about class node.
 */
class Prepare extends BasicEnhancement
{
	/**
	 * {@inherited}
	 */
	@Override
	protected void enhance(final ClassNode node)
	{
		final var ctClass = node.getCtClass();
		final var classFile = ctClass.getClassFile();
		final var processInfo = node.getProcessInformation();

		// check class itself
		markImplemented(classFile, "$$_hjx_identifier", processInfo::identifierExists);
		markImplemented(classFile, "$$_hjx_initializer", processInfo::initializerExists);

		// special case if class extends from another external class
		// only do this for root nodes
		if(!node.hasParent())
		{
			final var superClass = JavassistUtil.getSuperclass(ctClass);
			markImplemented(superClass, "$$_hjx_identifier", processInfo::identifierExists);
			markImplemented(superClass, "$$_hjx_initializer", processInfo::initializerExists);
		}

		ctClass.defrost();
	}
}
