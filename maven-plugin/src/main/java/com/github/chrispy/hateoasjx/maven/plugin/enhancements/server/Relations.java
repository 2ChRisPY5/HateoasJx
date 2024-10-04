package com.github.chrispy.hateoasjx.maven.plugin.enhancements.server;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.BasicEnhancement;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.ProcessInformation;
import com.github.chrispy.hateoasjx.maven.plugin.util.JavassistUtil;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewMethod;
import javassist.Modifier;

/**
 * Enhancements for $$_hjx_relations.
 */
class Relations extends BasicEnhancement
{
	private static final String METHOD_NAME = "$$_hjx_relations";
	private static final String FIELD = "private static final java.util.Collection $$_hjx_RELATIONS = java.util.List.of(%s);";

	/**
	 * Constructor
	 */
	Relations()
	{
		super(n -> !n.getProcessInformation().isRelationsExists());
	}

	/**
	 * {@inherited}
	 */
	@Override
	public void enhance(final ClassNode node)
	{
		final var processInfo = node.getProcessInformation();
		final var ctClass = node.getCtClass();

		// add the field
		if(!addField(ctClass))
			return;

		// check if parent impl exists
		final var body = node.hasAncestorWith(ProcessInformation::isRelationsExists)
			? """
				{
					final java.util.List relations = new java.util.ArrayList(super.$$_hjx_relations());
					relations.addAll($$_hjx_RELATIONS);
					return relations;
				}"""
			: "return $$_hjx_RELATIONS;";

		// build add the method
		JavassistUtil.compile(() -> {
			final var method = CtNewMethod.make(Modifier.PUBLIC,
				JavassistUtil.load(Collection.class),
				METHOD_NAME,
				null,
				null,
				body,
				ctClass);
			method.setGenericSignature("()Ljava/util/Collection<Lcom/github/chrispy/hateoasjx/api/config/RelatedConfig;>;");
			ctClass.addMethod(method);
		}, () -> "Could not add $$_hjx_relations method");

		processInfo.relationsExists(true);
	}

	/**
	 * Add the static field for holding the relations
	 */
	private static boolean addField(final CtClass ctClass)
	{
		// build the related list
		final var init = Stream.of(ctClass.getDeclaredFields())
			.map(f -> {
				final var optRelated = ServerChain.getRelated(f);
				if(optRelated.isEmpty())
					return null;

				final var related = optRelated.get();

				// path is the configured path or the field name as fallback
				final var path = related.path().isBlank()
					? f.getName()
					: related.path();

				return "new com.github.chrispy.hateoasjx.api.config.RelatedConfig(\"%s\", \"%s\", %s)"
					.formatted(f.getName(), path, related.subordinate());
			})
			.filter(Objects::nonNull)
			.toList();

		// nothing to add
		if(init.isEmpty())
			return false;

		// add field
		final var body = FIELD.formatted(String.join(",", init));
		JavassistUtil.compile(() -> ctClass.addField(CtField.make(body, ctClass)),
			() -> "Could not add $$_hjx_RELATIONS field");

		return true;
	}
}
