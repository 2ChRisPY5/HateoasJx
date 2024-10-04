package com.github.chrispy.hateoasjx.maven.plugin.enhancements.server;

import java.util.Optional;
import java.util.function.Consumer;

import com.github.chrispy.hateoasjx.api.Linkable;
import com.github.chrispy.hateoasjx.api.Related;
import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.Chain;
import com.github.chrispy.hateoasjx.maven.plugin.util.JavassistUtil;

import javassist.CtField;

/**
 * Chain for server enhancements.
 */
public class ServerChain extends Chain
{
	private final Consumer<ClassNode> chain;

	/**
	 * Constructor
	 */
	public ServerChain()
	{
		super();
		this.chain = new Prepare()
			.andThen(new ImplementInterface())
			.andThen(new Self())
			.andThen(new Relations())
			.andThen(new SubstitutionValues());
	}

	/**
	 * {@inherited}
	 */
	@Override
	protected void enhance(final ClassNode node)
	{
		this.chain.accept(node);
	}

	/**
	 * Get the {@code Linkable} annotation from the node.
	 * 
	 * @param node the {@code ClassNode}
	 */
	static Optional<Linkable> getLinkable(final ClassNode node)
	{
		return JavassistUtil.findAnnotation(node.getCtClass(), Linkable.class);
	}

	/**
	 * Get the {@code Related} annotation from the field.
	 * 
	 * @param node the {@code CtField}
	 */
	static Optional<Related> getRelated(final CtField field)
	{
		return JavassistUtil.findAnnotation(field, Related.class);
	}
}
