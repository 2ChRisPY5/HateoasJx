package com.github.chrispy.hateoasjx.maven.plugin.enhancements;

import java.util.function.Predicate;

import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;

/**
 * Basic chain implementation which only executes for root nodes.
 */
public abstract class Chain extends BasicEnhancement
{
	/**
	 * Constructor
	 */
	protected Chain()
	{
		super(Predicate.not(ClassNode::hasParent));
	}
}
