package com.github.chrispy.hateoasjx.client.proxy;

import java.util.Objects;

import com.github.chrispy.hateoasjx.client.HateoasJxClient;
import com.github.chrispy.hateoasjx.client.internal.ClassEntityType;
import com.github.chrispy.hateoasjx.client.type.EntityType;

/**
 * Bridge between {@link HateoasJxClient} and {@link HateoasJxInitializer}.
 * <br/>
 * <br/>
 * <em>Never implement this interface on your own!</em>
 */
public interface HateoasJxProxy
{
	/**
	 * @return the identifier of this entity
	 */
	default String $$_hjx_identifier()
	{
		return null;
	}

	/**
	 * Sets the lazy initializer
	 */
	void $$_hjx_initializer(final HateoasJxInitializer initializer);

	/**
	 * @return the lazy initializer
	 */
	HateoasJxInitializer $$_hjx_initializer();

	/**
	 * Convenient method for initializing a field.
	 */
	default <T> T $$_hjx_init(final String anchor, final Class<T> type, final T current)
	{
		return $$_hjx_init(anchor, new ClassEntityType<>(type), current);
	}

	/**
	 * Convenient method for initializing a field.
	 */
	@SuppressWarnings("unchecked")
	default <T> T $$_hjx_init(final String anchor, final EntityType type, final T current)
	{
		// abort if not injected
		final var li = $$_hjx_initializer();
		if(Objects.isNull(li))
			return current;

		// if current value exists; just update state
		if(Objects.nonNull(current))
		{
			li.markResolved(anchor);
			return current;
		}

		// resolve the value
		return (T) li.resolve(anchor, type);
	}
}
