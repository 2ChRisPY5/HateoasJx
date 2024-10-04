package com.github.chrispy.hateoasjx.maven.plugin.enhancements.client;

import java.util.function.Consumer;

import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.Chain;

/**
 * Chain for client enhancement.
 */
public class ClientChain extends Chain
{
	private final Consumer<ClassNode> chain;

	/**
	 * Constructor
	 */
	public ClientChain()
	{
		super();
		this.chain = new Prepare()
			.andThen(new ImplementInterface())
			.andThen(new Identifier())
			.andThen(new Initializer())
			.andThen(new Methods());
	}

	/**
	 * {@inherited}
	 */
	@Override
	protected void enhance(final ClassNode node)
	{
		this.chain.accept(node);
	}
}
