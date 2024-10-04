package com.github.chrispy.hateoasjx.maven.plugin.util;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.github.chrispy.hateoasjx.maven.plugin.HateoasJxEnhanceException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 * Some utils around
 */
public final class JavassistUtil
{
	private static final ThreadLocal<ClassPool> CLASS_POOL = ThreadLocal.withInitial(ClassPool::getDefault);

	/**
	 * Not a constructor
	 */
	private JavassistUtil() throws IllegalAccessException
	{
		throw new IllegalAccessException();
	}

	/**
	 * @see ClassPool#get(String)
	 */
	public static CtClass load(final String fqdn)
	{
		return get(CLASS_POOL.get(), cp -> cp.get(fqdn), () -> fqdn);
	}

	/**
	 * @see ClassPool#get(String)
	 */
	public static CtClass load(final Class<?> clazz)
	{
		return load(clazz.getName());
	}

	/**
	 * @see CtClass#subtypeOf(CtClass)
	 */
	public static boolean subtypeOf(final CtClass ctClass, final Class<?> clazz)
	{
		try
		{
			return ctClass.subtypeOf(load(clazz));
		}
		catch(final NotFoundException ex)
		{
			throw new HateoasJxEnhanceException("The class hierarchy of %s is not complete on classpath"
				.formatted(clazz.getName()));
		}
	}

	/**
	 * @see CtClass#getSuperclass()
	 */
	public static CtClass getSuperclass(final CtClass clazz)
	{
		return get(clazz, CtClass::getSuperclass, () -> "Superclass of " + clazz.getName());
	}

	/**
	 * Find a method from the class
	 */
	public static Optional<CtMethod> findMethod(final CtClass clazz, final String name)
	{
		try
		{
			return Optional.of(clazz.getDeclaredMethod(name));
		}
		catch(final NotFoundException e)
		{
			return Optional.empty();
		}
	}

	/**
	 * Get the field from the class
	 */
	public static CtField getField(final CtClass clazz, final String name)
	{
		try
		{
			return clazz.getDeclaredField(name);
		}
		catch(final NotFoundException e)
		{
			throw new HateoasJxEnhanceException("Field %s is not delcared by type %s".formatted(name, clazz.getName()));
		}
	}

	/**
	 * Get the field type.
	 */
	public static CtClass getFieldType(final CtField field)
	{
		return JavassistUtil.get(field, CtField::getType, () -> "Type of field %s in class %s"
			.formatted(field.getName(), field.getDeclaringClass().getName()));
	}

	/**
	 * Get a value unsafe
	 */
	public static <I, R> R get(final I input, final UnsafeFunction<I, R> map, final Supplier<String> msg)
	{
		try
		{
			return map.apply(input);
		}
		catch(final NotFoundException | ClassNotFoundException ex)
		{
			throw HateoasJxEnhanceException.notFound(msg.get());
		}
	}

	/**
	 * Convenient wrapper for compiling stuff
	 */
	public static void compile(final UnsafeRunnable<CannotCompileException> action, final Supplier<String> msg)
	{
		try
		{
			action.run();
		}
		catch(final CannotCompileException ex)
		{
			throw new HateoasJxEnhanceException(msg.get()).initCause(ex);
		}
	}

	/**
	 * @see CtClass#getAnnotation(Class)
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> Optional<T> findAnnotation(final CtClass clazz, final Class<T> anno)
	{
		return Optional.ofNullable(get(clazz, c -> (T) c.getAnnotation(anno), anno::getName));
	}

	/**
	 * Get the annotation from the field.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Annotation> Optional<T> findAnnotation(final CtField field, final Class<T> anno)
	{
		return Optional.ofNullable(get(field, f -> (T) f.getAnnotation(anno), anno::getName));

	}

	/**
	 * @see ThreadLocal#remove()
	 */
	public static void close()
	{
		CLASS_POOL.remove();
	}

	/**
	 * Unsafe runnable
	 */
	@FunctionalInterface
	public static interface UnsafeRunnable<E extends Throwable>
	{
		/**
		 * @see #run()
		 */
		void run() throws E;
	}

	/**
	 * Unsafe function
	 */
	@FunctionalInterface
	public static interface UnsafeFunction<I, R>
	{
		/**
		 * @see Function#apply(Object)
		 */
		R apply(final I input) throws NotFoundException, ClassNotFoundException;
	}
}
