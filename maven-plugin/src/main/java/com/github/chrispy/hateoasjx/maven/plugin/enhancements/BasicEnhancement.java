package com.github.chrispy.hateoasjx.maven.plugin.enhancements;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.ClassNode.Visitor;
import com.github.chrispy.hateoasjx.maven.plugin.util.JavassistUtil;

import javassist.CtClass;
import javassist.bytecode.ClassFile;

/**
 * Basic class for enhancement implementations
 */
public abstract class BasicEnhancement implements Visitor
{
	private final Predicate<ClassNode> toProcess;

	/**
	 * Constructor
	 */
	protected BasicEnhancement(final Predicate<ClassNode> toProcess)
	{
		this.toProcess = toProcess;
	}

	/**
	 * Constructor
	 */
	protected BasicEnhancement()
	{
		this(n -> true);
	}

	/**
	 * {@inherited}
	 */
	@Override
	public final void node(final ClassNode node)
	{
		if(!this.toProcess.test(node))
			return;

		enhance(node);
	}

	/**
	 * Implementation of the enhancement.
	 * 
	 * @param node the {@code ClassNode}
	 */
	protected abstract void enhance(final ClassNode node);

	/**
	 * Check class hierarchy for existing methods.
	 */
	protected static void markImplemented(final CtClass ctClass, final String method, final Consumer<Boolean> action)
	{
		if(Objects.isNull(ctClass))
			return;

		if(markImplemented(ctClass.getClassFile(), method, action))
			return;

		markImplemented(JavassistUtil.getSuperclass(ctClass), method, action);
	}

	/**
	 * Check if given method exists and execute action if true.
	 */
	protected static boolean markImplemented(final ClassFile classFile, final String method, final Consumer<Boolean> action)
	{
		if(Objects.nonNull(classFile.getMethod(method)))
		{
			action.accept(Boolean.TRUE);
			return true;
		}

		return false;
	}

	/**
	 * Check if interface was already added.
	 */
	protected static boolean alreadyImplemented(final ClassNode node, final Class<?> interfaze)
	{
		final var ctClass = node.getCtClass();

		return Stream.of(JavassistUtil.get(ctClass, CtClass::getInterfaces, () -> "An interface of " + ctClass.getName()))
			.map(CtClass::getName)
			.anyMatch(interfaze.getName()::equals);
	}
}
