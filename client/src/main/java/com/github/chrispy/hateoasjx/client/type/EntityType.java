package com.github.chrispy.hateoasjx.client.type;

import java.lang.reflect.Type;
import java.util.Collection;

import com.github.chrispy.hateoasjx.client.RequestExecutor;
import com.github.chrispy.hateoasjx.client.internal.ClassEntityType;

/**
 * <p>
 * Holds the information about the requested result type used by {@link RequestExecutor}. It also implements
 * {@link Type} for compatibility.
 */
public sealed interface EntityType extends Type permits GenericType, ClassEntityType
{
	/**
	 * Returns the {@code Class} representing the class declared by this EntityType.
	 * 
	 * @return In case of a parameterized type like {@code Collection} the top-level {@code Class} is
	 *         returned. In any other case the declared type is considered a {@code Class} itself.
	 */
	Class<?> getRawClass();

	/**
	 * Returns an array of {@code Type}s representing the type parameters of the type declared by this
	 * EntityType.
	 * 
	 * @return contains values only if {@link #isParameterized()} is {@¢ode true}. Otherwise an empty array
	 *         will be returned.
	 */
	Type[] getTypeArguments();

	/**
	 * Returns the actual entity {@code Type} derived from the type declared by this EntityType.
	 * 
	 * @return In case of an {@code Array} its' componenet type is returned. If {@link #isParameterized()}
	 *         evaluates to {@code false} then this call is similar to {@link #getRawClass()}.
	 * @throws UnsupportedOperationException in case of using {@link GenericType} constructor
	 */
	Type getEntityType();

	/**
	 * @return {@code true} if the declared type by this EntityType represets a {@code ParameterizedType},
	 *         otherwise false.
	 */
	default boolean isParameterized()
	{
		return getTypeArguments().length > 0;
	}

	/**
	 * @return {@code true} if {@link #getActualClass()} is an array.
	 */
	default boolean isArray()
	{
		return getRawClass().isArray();
	}

	/**
	 * Indicates if this type represents a single entity or multiple.
	 * 
	 * @return {@code true} only if {@link #isArray()} is true or {@code Collection} is assignable to
	 *         {@link #getRawClass()}.
	 */
	default boolean hasMultiple()
	{
		return isArray() || Collection.class.isAssignableFrom(getRawClass());
	}
}
