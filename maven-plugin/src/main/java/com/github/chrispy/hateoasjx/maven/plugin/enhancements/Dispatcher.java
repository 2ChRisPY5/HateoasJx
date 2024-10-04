package com.github.chrispy.hateoasjx.maven.plugin.enhancements;

import java.lang.annotation.Annotation;
import java.util.Objects;

import com.github.chrispy.hateoasjx.api.Linkable;
import com.github.chrispy.hateoasjx.client.HateoasProxy;
import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.client.ClientChain;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.server.ServerChain;
import com.github.chrispy.hateoasjx.maven.plugin.util.JavassistUtil;

import javassist.CtClass;

/**
 * Dispatches between the server and client enhancements.
 */
public class Dispatcher extends Chain
{
	private final ServerChain server;
	private final ClientChain client;

	/**
	 * Constructor
	 */
	public Dispatcher()
	{
		super();
		this.server = new ServerChain();
		this.client = new ClientChain();
	}

	/**
	 * {@inheritedDoc}
	 */
	@Override
	protected void enhance(final ClassNode node)
	{
		final var ctClass = node.getCtClass();

		// dispatch execution
		if(hasAnnotation(ctClass, Linkable.class))
		{
			this.server.accept(node);
		}

		if(hasAnnotation(ctClass, HateoasProxy.class))
		{
			this.client.accept(node);
		}
	}

	/**
	 * Check if class hierarchie has the given annotation.
	 */
	private static boolean hasAnnotation(final CtClass clazz, final Class<? extends Annotation> anno)
	{
		if(Objects.isNull(clazz))
			return false;

		if(clazz.hasAnnotation(anno))
			return true;

		return hasAnnotation(JavassistUtil.getSuperclass(clazz), anno);
	}
}
