package com.github.chrispy.hateoasjx.maven.plugin.enhancements.client;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import com.github.chrispy.hateoasjx.client.HateoasProxy;
import com.github.chrispy.hateoasjx.client.type.EntityType;
import com.github.chrispy.hateoasjx.client.type.GenericType;
import com.github.chrispy.hateoasjx.maven.plugin.ClassNode;
import com.github.chrispy.hateoasjx.maven.plugin.enhancements.BasicEnhancement;
import com.github.chrispy.hateoasjx.maven.plugin.util.JavassistUtil;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.Descriptor;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

/**
 * Responsible for instrumenting the getters for the annotated fields.
 */
class Methods extends BasicEnhancement
{
	private static final Pattern GENERIC_FINDER = Pattern.compile("<\\[?L(.+);>");
	private static final String KNOWN_GENERIC_TYPE = GenericType.class.getName() + ".%s(%s.class)";

	/**
	 * {@inherited}
	 */
	@Override
	protected void enhance(final ClassNode node)
	{
		final var ctClass = node.getCtClass();

		// find annotated fields
		// then find their getter
		// enhance the body
		Stream.of(ctClass.getDeclaredFields())
			.filter(f -> f.hasAnnotation(HateoasProxy.class))
			.forEach(f -> {
				// anchor is either configured or the field name
				final var anchor = JavassistUtil.findAnnotation(f, HateoasProxy.class)
					.map(HateoasProxy::anchor)
					.filter(Predicate.not(String::isBlank))
					.orElseGet(f::getName);

				findForEnhancement(f).ifPresent(m -> enhance(m, anchor));
			});
	}

	/**
	 * Add the $$_hjx_init call at the beginning.
	 */
	private void enhance(final CtMethod method, final String anchor)
	{
		final var methodName = method.getName();
		final var fieldName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);

		// build first part until entity type
		final var stmt = new StringBuilder()
			.append("$0.").append(fieldName)
			.append(" = ($r)")
			.append("$$_hjx_init(")
			.append('"').append(anchor).append("\",")
			.append(buildEntityType(method)).append(',')
			.append("$0.").append(fieldName)
			.append(");");

		final Supplier<String> msg = () -> "Adding $$_hjx_init to %s#%s failed. Statement would be: %s"
			.formatted(method.getDeclaringClass().getName(), method.getName(), stmt.toString());

		JavassistUtil.compile(() -> method.insertBefore(stmt.toString()), msg);
	}

	/**
	 * Build the EntityType for the given method.
	 */
	private String buildEntityType(final CtMethod method)
	{
		final var returnType = JavassistUtil.get(method, CtMethod::getReturnType, () -> "Return type of %s#%s"
			.formatted(method.getDeclaringClass(), method.getName()));

		// check if return type is generic
		return Optional.ofNullable(method.getGenericSignature())
			.map(sig -> buildGenericEntityType(sig, returnType))
			// else its a simple class or array
			.orElseGet(() -> returnType.getName() + ".class");
	}

	/**
	 * Build the EntityType expression for given return type and signature.
	 */
	private String buildGenericEntityType(final String genericSig, final CtClass returnType)
	{
		final String factoryMethod;

		if(JavassistUtil.subtypeOf(returnType, List.class))
		{
			factoryMethod = "listOf";
		}
		else if(JavassistUtil.subtypeOf(returnType, Set.class))
		{
			factoryMethod = "setOf";
		}
		else if(Optional.class.getName().equals(returnType.getName()))
		{
			factoryMethod = "optionalOf";
		}
		else
		{
			return '(' + EntityType.class.getName() + ") null";
		}

		// build the factory expression
		final var matcher = GENERIC_FINDER.matcher(genericSig);
		matcher.find();
		final var actualType = Descriptor.toJavaName(matcher.group(1));

		return KNOWN_GENERIC_TYPE.formatted(factoryMethod, actualType);
	}

	/**
	 * Find the getter for given field.
	 */
	private static Optional<CtMethod> findForEnhancement(final CtField field)
	{
		final var fieldName = field.getName();
		final var getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

		// it has to be implemented; and not already enhanced
		return JavassistUtil.findMethod(field.getDeclaringClass(), getterName)
			.filter(get -> !(Modifier.isAbstract(get.getModifiers()) || alreadyEnhanced(get)));
	}

	/**
	 * Check if method already calls the internal initializer.
	 */
	private static boolean alreadyEnhanced(final CtMethod method)
	{
		final var finder = new FindInitCall();
		JavassistUtil.compile(() -> method.instrument(finder), () -> "");
		return finder.found;
	}

	/**
	 * Visitor for finding the call to $$_hjx_init.
	 */
	private static class FindInitCall extends ExprEditor
	{
		private boolean found = false;

		/**
		 * {@inherited}
		 */
		@Override
		public void edit(final MethodCall m) throws CannotCompileException
		{
			if(this.found)
				return;

			this.found = "$$_hjx_init".equals(m.getMethodName());
		}
	}
}
