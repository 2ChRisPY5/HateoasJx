package com.github.chrispy.hateoasjx.client.proxy;

import java.lang.StackWalker.Option;
import java.lang.reflect.ParameterizedType;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.github.chrispy.hateoasjx.client.HateoasJxClient;
import com.github.chrispy.hateoasjx.client.type.EntityType;
import com.github.chrispy.hateoasjx.client.type.GenericType;
import com.github.chrispy.hateoasjx.common.HateoasJxException;

/**
 * Will get injected by {@link HateoasJxClient} to deserialized entities.
 */
public final class HateoasJxInitializer
{
	private static final StackWalker WALKER = StackWalker.getInstance(EnumSet.of(Option.SHOW_REFLECT_FRAMES,
		Option.RETAIN_CLASS_REFERENCE), 5);

	private final HateoasJxClient client;
	private final Set<String> resolved = new HashSet<>();
	private final Map<String, String> links;

	/**
	 * Constructor
	 */
	public HateoasJxInitializer(final HateoasJxClient client, final Map<String, String> links)
	{
		this.client = client;
		this.links = links;
	}

	/**
	 * Mark anchor as resolved.
	 */
	boolean markResolved(final String anchor)
	{
		return this.resolved.add(anchor);
	}

	/**
	 * Resolve the anchor value and set it to calling instance.
	 * 
	 * @param anchor the related resource to load
	 */
	Object resolve(final String anchor, final EntityType type)
	{
		// no link exists for this anchor
		if(!this.links.containsKey(anchor))
		{
			markResolved(anchor);
			return null;
		}

		// we already resolved this one
		if(!markResolved(anchor))
			return null;

		final var resolvedType = Optional.ofNullable(type)
			.orElseGet(HateoasJxInitializer::traverseType);

		// execute call
		return this.client.get(resolvedType, this.links.get(anchor));
	}

	/**
	 * Traverses back the stack frames to reconstruct the original method return type. Only necessary from an
	 * enhanced proxy object if the getter's return type is not a known generic.
	 * 
	 * @return the found entity type
	 */
	private static EntityType traverseType()
	{
		final var frame = WALKER.walk(frames -> frames
			.skip(4)
			.findFirst()
			.orElseThrow(() -> new IllegalStateException(
				"Initializer was not called by a proxy getter. This is most likely a bytecode enhancement bug,")));

		final ParameterizedType returnType;
		try
		{
			returnType = (ParameterizedType) frame.getDeclaringClass()
				.getDeclaredMethod(frame.getMethodName())
				.getGenericReturnType();
		}
		catch(final NoSuchMethodException | SecurityException ex)
		{
			// cannot happen ever
			// method definetly exists because its' in the stackframes;
			// permission should be always granted because the call comes originally from the reflected class
			throw new HateoasJxException("An unexpected error occurred", ex);
		}

		return new GenericType<>(returnType) {
		};
	}
}
