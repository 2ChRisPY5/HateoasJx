package com.github.chrispy.hateoasjx.maven.plugin.enhancements.client;

import java.util.Optional;
import java.util.function.Predicate;

import com.github.chrispy.hateoasjx.client.HateoasProxy;
import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.BasicEnhancement;
import com.github.chrispy.hateoasjx.maven.plugin.util.ExpressionTranslator;
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
		final var getter = ExpressionTranslator.toGetterChain(ctClass, anchor);

		// just return the string
		final String statement;
		if(String.class.getName().equals(getter.resultType().getName()))
		{
			statement = getter.expression();
		}
		else
		{
			statement = "java.lang.String.valueOf(" + getter.expression() + ')';
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
