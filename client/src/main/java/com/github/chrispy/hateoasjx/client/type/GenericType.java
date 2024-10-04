package com.github.chrispy.hateoasjx.client.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.chrispy.hateoasjx.common.HateoasJxException;

/**
 * A convenient wrapper for parameterized types. This class implements {@link EntityType} and
 * {@link ParameterizedType} for compatibility.
 * 
 * @throws IllegalArgumentException if anything else than a parameterized type is declared.
 *             {@code new GenericType<String>()} for example will fail.
 */
public non-sealed abstract class GenericType<T> implements EntityType, ParameterizedType
{
	private final ParameterizedType actualType;

	/**
	 * Constructor
	 */
	protected GenericType()
	{
		final var declaredType = ((ParameterizedType) getClass().getGenericSuperclass())
			.getActualTypeArguments()[0];

		if(!(declaredType instanceof final ParameterizedType pt))
			throw new HateoasJxException(
				"Only ParameterizedType is allowed. Please use HateoasJxClient#get(Class, String) for non-generic types.");

		this.actualType = pt;
	}

	/**
	 * Constructor
	 */
	protected GenericType(final ParameterizedType pt)
	{
		this.actualType = pt;
	}

	/**
	 * Create a new generic type representing a {@code List} of type {@code T}.
	 * 
	 * @param <T> the list element type
	 * @return a new generic type
	 */
	public static <T> GenericType<List<T>> listOf(final Class<T> entityType)
	{
		return new KnownGenericType<>(List.class, entityType);
	}

	/**
	 * Create a new generic type representing a {@code Set} of type {@code T}.
	 * 
	 * @param <T> the set element type
	 * @return a new generic type
	 */
	public static <T> GenericType<Set<T>> setOf(final Class<T> entityType)
	{
		return new KnownGenericType<>(Set.class, entityType);
	}

	/**
	 * Create a new generic type representing a {@code Optional} of type {@code T}.
	 * 
	 * @param <T> the optional element type
	 * @return a new generic type
	 */
	public static <T> GenericType<Optional<T>> optionalOf(final Class<T> entityType)
	{
		return new KnownGenericType<>(Optional.class, entityType);
	}

	/**
	 * {@inherited}
	 */
	@Override
	public final Class<?> getRawClass()
	{
		return (Class<?>) this.actualType.getRawType();
	}

	/**
	 * {@inherited}
	 */
	@Override
	public final Type[] getTypeArguments()
	{
		return this.actualType.getActualTypeArguments();
	}

	/**
	 * {@inherited}
	 */
	@Override
	public Type getEntityType()
	{
		throw new HateoasJxException("Deriving the actual entity type from a custom GenericType is not supported.");
	}

	/**
	 * {@inherited}
	 */
	@Override
	public final String getTypeName()
	{
		return this.actualType.getTypeName();
	}

	/**
	 * {@inherited}
	 */
	@Override
	public final Type getRawType()
	{
		return this.actualType.getRawType();
	}

	/**
	 * {@inherited}
	 */
	@Override
	public final Type[] getActualTypeArguments()
	{
		return getTypeArguments();
	}

	/**
	 * {@inherited}
	 */
	@Override
	public final Type getOwnerType()
	{
		return null;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return this.actualType.hashCode();
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if(this == obj)
			return true;

		if(!(obj instanceof final GenericType other))
		{
			return false;
		}

		return this.actualType.equals(other.actualType);
	}

	/**
	 * Internal implementation for known Java Generic types
	 */
	private static class KnownGenericType<T> extends GenericType<T>
	{
		/**
		 * Constructor
		 */
		private KnownGenericType(final Type raw, final Type entityType)
		{
			super(new SingleParameterizedType(raw, entityType));
		}

		/**
		 * {@inherited}
		 */
		@Override
		public Type getEntityType()
		{
			return getActualTypeArguments()[0];
		}
	}

	/**
	 * Type representing a generic type with exactly one type argument.
	 */
	private static record SingleParameterizedType(Type raw, Type argument) implements ParameterizedType
	{
		/**
		 * {@inherited}
		 */
		@Override
		public Type getRawType()
		{
			return this.raw;
		}

		/**
		 * {@inherited}
		 */
		@Override
		public Type[] getActualTypeArguments()
		{
			return new Type[] {
				this.argument
			};
		}

		/**
		 * {@inherited}
		 */
		@Override
		public String getTypeName()
		{
			return this.raw.getTypeName() + '<' + this.argument.getTypeName() + '>';
		}

		/**
		 * {@inherited}
		 */
		@Override
		public Type getOwnerType()
		{
			return null;
		}
	}
}