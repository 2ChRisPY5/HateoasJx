package com.github.chrispy.hateoasjx.maven.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.github.chrispy.hateoasjx.maven.plugin.enhancements.ProcessInformation;

import javassist.CtClass;

/**
 * Represents a class node with parent and children.
 */
public class ClassNode implements Consumer<ClassNode.Visitor>
{
	private final CtClass clazz;
	private ClassNode parent;
	private final List<ClassNode> children = new ArrayList<>();

	private final ProcessInformation info = new ProcessInformation();

	/**
	 * Constructor
	 * 
	 * @param clazz the {@link CtClass}
	 * @param parent optional parent node
	 */
	public ClassNode(final CtClass clazz, final ClassNode parent)
	{
		this.clazz = clazz;
		this.parent = parent;
	}

	/**
	 * Insert given class into this tree.
	 * 
	 * @param child the child class
	 * @return true if class is part of this tree
	 */
	public boolean insert(final CtClass child)
	{
		// does not belong to this node
		if(!child.subclassOf(this.clazz))
			return false;

		// try finding a matching child first
		final var found = this.children.stream()
			.filter(c -> c.insert(child))
			.findFirst();

		// if no child could be found; it becomes a child of this
		if(found.isEmpty())
		{
			final var newChild = new ClassNode(child, this);

			// potentially re-arrange other childs
			final var rearrange = this.children.stream()
				.filter(c -> c.clazz.subclassOf(child))
				.toList();
			rearrange.forEach(newChild::addChild);
			this.children.removeAll(rearrange);

			// add child
			this.children.add(newChild);
		}

		return true;
	}

	/**
	 * Add as new child.
	 * 
	 * @param node the {@link ClassNode}
	 */
	public void addChild(final ClassNode node)
	{
		node.parent = this;
		this.children.add(node);
	}

	/**
	 * Check if this node has an ancestor passing the given test.
	 * 
	 * @param check the process information test
	 * @return only true if any ancestor passes the check
	 */
	public boolean hasAncestorWith(final Predicate<ProcessInformation> check)
	{
		if(!hasParent())
			return false;

		if(check.test(this.parent.info))
			return true;

		return this.parent.hasAncestorWith(check);
	}

	/**
	 * Check if this node has a parent.
	 * 
	 * @return true or false
	 */
	public boolean hasParent()
	{
		return Objects.nonNull(this.parent);
	}

	/**
	 * @return the process information
	 */
	public ProcessInformation getProcessInformation()
	{
		return this.info;
	}

	/**
	 * @return the {@link CtClass}
	 */
	public CtClass getCtClass()
	{
		return this.clazz;
	}

	/**
	 * {@inherited}
	 */
	@Override
	public void accept(final Visitor t)
	{
		t.node(this);
		this.children.forEach(t);
	}

	/**
	 * Visitor interface for {@link ClassNode}.
	 */
	@FunctionalInterface
	public static interface Visitor extends Consumer<ClassNode>
	{
		/**
		 * Visit a class node.
		 * 
		 * @param node the current node
		 */
		void node(final ClassNode node);

		/**
		 * @see Visitor#visit(ClassNode)
		 */
		@Override
		default void accept(final ClassNode t)
		{
			t.accept(this);
		}
	}
}
