package com.github.chrispy.hateoasjx.client.internal;

import java.lang.reflect.Type;

import com.github.chrispy.hateoasjx.client.type.EntityType;

/**
 * Implementation for a simpe class.
 */
public record ClassEntityType<T>(Class<T> type) implements EntityType
{
	private static final Type[] EMPTY = {};

	/**
	 * {@inherited}
	 */
	@Override
	public Class<?> getRawClass()
	{
		return this.type;
	}

	/**
	 * {@inherited}
	 */
	@Override
	public Type getEntityType()
	{
		return isArray() ? this.type.componentType() : this.type;
	}

	/**
	 * {@inherited}
	 */
	@Override
	public Type[] getTypeArguments()
	{
		return EMPTY;
	}

	/**
	 * {@inherited}
	 */
	@Override
	public String getTypeName()
	{
		return this.type.getTypeName();
	}
}
