package com.github.chrispy.hateoasjx.maven.plugin.enhancements.client;

import java.util.Optional;
import java.util.function.Predicate;

import com.github.chrispy.hateoasjx.client.HateoasProxy;
import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.BasicEnhancement;
import com.github.chrispy.hateoasjx.maven.plugin.util.JavassistUtil;

import javassist.CtNewMethod;

/**
 * Responsible for implementing $$_hjx_identifier.
 */
class Identifier extends BasicEnhancement
{
	private static final String BODY = """
		public java.lang.String $$_hjx_identifier() {
			return %s;
		}""";

	/**
	 * Constructor
	 */
	Identifier()
	{
		super(n -> !n.getProcessInformation().isIdentifierExists() && getProxyAnchor(n).isPresent());
	}

	/**
	 * {@inherited}
	 */
	@Override
	protected void enhance(final ClassNode node)
	{
		final var ctClass = node.getCtClass();
		final var anchor = getProxyAnchor(node).orElseThrow();

		// get field and type
		final var field = JavassistUtil.getField(ctClass, anchor);
		final var fieldType = JavassistUtil.getFieldType(field);

		// just return the string
		final String statement;
		if(String.class.getName().equals(fieldType.getName()))
		{
			statement = "$0." + anchor;
		}
		else
		{
			statement = "java.lang.String.valueOf($0." + anchor + ')';
		}

		JavassistUtil.compile(() -> ctClass.addMethod(CtNewMethod.make(BODY.formatted(statement), ctClass)),
			() -> "Could not enhance the $$_hjx_identifier");

		node.getProcessInformation().identifierExists(true);
	}

	/**
	 * Get the optional HateoasProxy anchor value.
	 */
	private static Optional<String> getProxyAnchor(final ClassNode node)
	{
		return JavassistUtil.findAnnotation(node.getCtClass(), HateoasProxy.class)
			.map(HateoasProxy::anchor)
			.filter(Predicate.not(String::isBlank));
	}
}
