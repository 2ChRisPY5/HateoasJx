package com.github.chrispy.hateoasjx.maven.plugin.enhancements.client;

import com.github.chrispy.hateoasjx.client.proxy.HateoasJxInitializer;
import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.BasicEnhancement;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.ProcessInformation;
import com.github.chrispy.hateoasjx.maven.plugin.util.JavassistUtil;

import javassist.CtField;
import javassist.CtNewMethod;

/**
 * Responsible for adding $$_hjx_initializer.
 */
class Initializer extends BasicEnhancement
{
	private static final String TYPE = HateoasJxInitializer.class.getName();
	private static final String FIELD = "private transient " + TYPE + " $$_hjx_initializer;";
	private static final String GETTER = "public " + TYPE + " $$_hjx_initializer() { return $0.$$_hjx_initializer; }";
	private static final String SETTER = "public void $$_hjx_initializer(" + TYPE + " t) { $0.$$_hjx_initializer = t; }";

	/**
	 * Constructor
	 */
	Initializer()
	{
		super(n -> !(n.getProcessInformation().isInitializerExists()
			|| n.hasAncestorWith(ProcessInformation::isInitializerExists)));
	}

	/**
	 * {@inherited}
	 */
	@Override
	protected void enhance(final ClassNode node)
	{
		final var ctClass = node.getCtClass();

		JavassistUtil.compile(() -> {
			ctClass.addField(CtField.make(FIELD, ctClass));
			ctClass.addMethod(CtNewMethod.make(GETTER, ctClass));
			ctClass.addMethod(CtNewMethod.make(SETTER, ctClass));
		}, () -> "Could not enhance the $$_hjx_initializer");
	}
}
