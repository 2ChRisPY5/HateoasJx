package com.github.chrispy.hateoasjx.maven.plugin.enhancements.server;

import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.BasicEnhancement;
import com.github.chrispy.hateoasjx.maven.plugin.util.JavassistUtil;

import javassist.CtField;
import javassist.CtNewMethod;

/**
 * Enhancement for $$_hjx_self.
 */
class Self extends BasicEnhancement
{
	private static final String FIELD = """
		private static final com.github.chrispy.hateoasjx.api.config.SelfConfig $$_hjx_SELF =
			new com.github.chrispy.hateoasjx.api.config.SelfConfig("%s", "%s");""";

	private static final String METHOD = """
		public com.github.chrispy.hateoasjx.api.config.SelfConfig $$_hjx_self() {
			return $$_hjx_SELF;
		}""";

	/**
	 * Constructor
	 */
	Self()
	{
		super(n -> !n.getProcessInformation().isSelfExists() && ServerChain.getLinkable(n).isPresent());
	}

	/**
	 * {@inherited}
	 */
	@Override
	public void enhance(final ClassNode node)
	{
		final var ctClass = node.getCtClass();
		final var linkable = ServerChain.getLinkable(node).orElseThrow();

		// add the field
		JavassistUtil.compile(() -> ctClass.addField(CtField.make(FIELD.formatted(linkable.path(), linkable.identifiedBy()), ctClass)),
			() -> "Could not add $$_hjx_SELF field");

		// add the method
		JavassistUtil.compile(() -> ctClass.addMethod(CtNewMethod.make(METHOD, ctClass)),
			() -> "Could not add $$_hjx_self method");
	}
}
